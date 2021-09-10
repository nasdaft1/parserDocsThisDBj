package parserDocs;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


public class replaсeTextDocs {
	// класс для обработки WORD DOCS файла, поиск ключей и замена их данных из словаря текст
	private String fileInDocx; //имя файла который будет читаться
	private String fileOutDocx;//имя файла в который будет сформированный 
	public Map<String, String> keyList = new HashMap<String,String>();; //словарь ключей для замены в тексте
	
	public replaсeTextDocs(String fileInDocx, String fileOutDocx) {
		this.fileInDocx = fileInDocx;  
		this.fileOutDocx= fileOutDocx;
		}
	
	public String strSummText(String strKitPart){ 
		//функция сумирует части текста {{XX.YY..ZZ}} в целый текст {{XXYYZZ}} 
		int startPosition = strKitPart.indexOf("<w:t>")+5; 	 			//поиск начала текста
		int endPosition = strKitPart.indexOf("</w:t>",startPosition); 	//поиск конца текста
		String PathText ="";
		//проверка на наличие ошибки форматирования в ключевом слове
		if (strKitPart.indexOf("</w:p>") == -1) { // поиск 
			while (startPosition != 4)  {
				PathText =  PathText+ strKitPart.substring(startPosition,endPosition); //вырезание текста и суммироваие с найденым
				startPosition = strKitPart.indexOf("<w:t>",startPosition)+5; //поиск начала текста
				endPosition = strKitPart.indexOf("</w:t>",startPosition);	 //поиск конца текста
				}
			}
		else {//замена XML кода для подсветки ошибки - цветом красным на желтом фоне 
			PathText = strKitPart.replaceAll("<w:rPr>", "<w:rPr><w:color w:val=\"FF0000\"/><w:highlight w:val=\"yellow\"/>");
			return PathText;
			}
		String key = keyList.get(PathText.substring(2, PathText.length()-2)); // поиск даных в коллекции HashMap 
		if (key != null) { PathText = key;}  // замена из колеции данными по ключам
		else { PathText = "";}
		return "<w:t>"+PathText+"</w:t>";
		}

	public String strFindCorrect(String str){
		String strNoSummText ="";
		int old_startPosition =0;
		int startPosition = str.indexOf("<w:t>{{");					  //поиск начала ключа {{
		String str1 = str.substring(0,startPosition);				  //формирования начала строки до первого ключа
		int endPosition = str.indexOf("}}</w:t>", startPosition)+8;	  //поиск конца ключа  }}
		while (startPosition != -1)  {								  //поиск всех ключей перебором строки по участкам {{ }}
			strNoSummText = str.substring(startPosition,endPosition); //формирование из частей ключацелый ключ 
			startPosition = str.indexOf("<w:t>{{", endPosition);      //поиск начальной позиции
			if (startPosition != -1) {								  //
				str1 = str1+ strSummText(strNoSummText) + str.substring(endPosition, startPosition);  //формирование ключа+межстрочного диапазона данных
				endPosition = str.indexOf("}}</w:t>",startPosition)+8;// поиск послежней позиции
				old_startPosition = startPosition;                    //переменная для правельного формирования последнего ключа
				}
			else {	
				strNoSummText = str.substring(old_startPosition,endPosition);						//формирование ключа
				str1 = str1+ strSummText(strNoSummText)+  str.substring(endPosition,str.length());} //формирование ключа+последнего диапазона данных
			}
		return str1;
		}
	
	private static byte[] getBytesFromInputStream(InputStream is, long size) throws IOException  {
		BufferedInputStream bufIn = new BufferedInputStream(is);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		BufferedOutputStream bos = new BufferedOutputStream(baos);
		int c = bufIn.read(); //чтение из файла
		while (c != -1) {
			bos.write(c);	  //запись в массив
			c = bufIn.read(); //чтение из файла
			}
		bos.flush();
		baos.flush();
		bos.close();
		return baos.toByteArray();
		}
	
	public void  replaсeText() { //процедура чтения, раз архивирования, обработки и за архивировать и сохранить фаил в DOCS 
		try {	
			FileInputStream inputStream = new FileInputStream(fileInDocx);
			FileOutputStream outputStream = new FileOutputStream(fileOutDocx);
			ZipInputStream zipInputS = new ZipInputStream(inputStream );
			ZipOutputStream zipOutputS = new ZipOutputStream(outputStream);
		    ZipEntry entry = null;
		    while ((entry = zipInputS.getNextEntry()) != null) {					  //перебор файлов в архиве zip файла
		    	byte[] bytes =  getBytesFromInputStream( zipInputS, entry.getSize() );//записываем вмассив bytes из файла в архиве
				//log.debug("Extracting " + entry.getName());
				if (entry.getName().equals("word/document.xml")) { // выполняем часть кода с файлом который соодержит ключи в XML фале 
				    String str = new String(bytes, "UTF-8");       // переводим буфер в строку str 
				    bytes = strFindCorrect(str).getBytes("UTF-8"); // обрабатываем строку для поиска ключей и замены на данные и обновляем массив 
				}
				zipOutputS.putNextEntry(entry); //запись названия файла в архиве 
				zipOutputS.write(bytes);		//запись массива в фаил в архиве
		    }
		    zipInputS.close();
		    zipOutputS.close();
		} catch (IOException e) {}
		}
}


	

	
	

