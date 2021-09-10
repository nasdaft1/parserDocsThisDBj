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


public class repla�eTextDocs {
	// ����� ��� ��������� WORD DOCS �����, ����� ������ � ������ �� ������ �� ������� �����
	private String fileInDocx; //��� ����� ������� ����� ��������
	private String fileOutDocx;//��� ����� � ������� ����� �������������� 
	public Map<String, String> keyList = new HashMap<String,String>();; //������� ������ ��� ������ � ������
	
	public repla�eTextDocs(String fileInDocx, String fileOutDocx) {
		this.fileInDocx = fileInDocx;  
		this.fileOutDocx= fileOutDocx;
		}
	
	public String strSummText(String strKitPart){ 
		//������� �������� ����� ������ {{XX.YY..ZZ}} � ����� ����� {{XXYYZZ}} 
		int startPosition = strKitPart.indexOf("<w:t>")+5; 	 			//����� ������ ������
		int endPosition = strKitPart.indexOf("</w:t>",startPosition); 	//����� ����� ������
		String PathText ="";
		//�������� �� ������� ������ �������������� � �������� �����
		if (strKitPart.indexOf("</w:p>") == -1) { // ����� 
			while (startPosition != 4)  {
				PathText =  PathText+ strKitPart.substring(startPosition,endPosition); //��������� ������ � ����������� � ��������
				startPosition = strKitPart.indexOf("<w:t>",startPosition)+5; //����� ������ ������
				endPosition = strKitPart.indexOf("</w:t>",startPosition);	 //����� ����� ������
				}
			}
		else {//������ XML ���� ��� ��������� ������ - ������ ������� �� ������ ���� 
			PathText = strKitPart.replaceAll("<w:rPr>", "<w:rPr><w:color w:val=\"FF0000\"/><w:highlight w:val=\"yellow\"/>");
			return PathText;
			}
		String key = keyList.get(PathText.substring(2, PathText.length()-2)); // ����� ����� � ��������� HashMap 
		if (key != null) { PathText = key;}  // ������ �� ������� ������� �� ������
		else { PathText = "";}
		return "<w:t>"+PathText+"</w:t>";
		}

	public String strFindCorrect(String str){
		String strNoSummText ="";
		int old_startPosition =0;
		int startPosition = str.indexOf("<w:t>{{");					  //����� ������ ����� {{
		String str1 = str.substring(0,startPosition);				  //������������ ������ ������ �� ������� �����
		int endPosition = str.indexOf("}}</w:t>", startPosition)+8;	  //����� ����� �����  }}
		while (startPosition != -1)  {								  //����� ���� ������ ��������� ������ �� �������� {{ }}
			strNoSummText = str.substring(startPosition,endPosition); //������������ �� ������ ���������� ���� 
			startPosition = str.indexOf("<w:t>{{", endPosition);      //����� ��������� �������
			if (startPosition != -1) {								  //
				str1 = str1+ strSummText(strNoSummText) + str.substring(endPosition, startPosition);  //������������ �����+������������ ��������� ������
				endPosition = str.indexOf("}}</w:t>",startPosition)+8;// ����� ��������� �������
				old_startPosition = startPosition;                    //���������� ��� ����������� ������������ ���������� �����
				}
			else {	
				strNoSummText = str.substring(old_startPosition,endPosition);						//������������ �����
				str1 = str1+ strSummText(strNoSummText)+  str.substring(endPosition,str.length());} //������������ �����+���������� ��������� ������
			}
		return str1;
		}
	
	private static byte[] getBytesFromInputStream(InputStream is, long size) throws IOException  {
		BufferedInputStream bufIn = new BufferedInputStream(is);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		BufferedOutputStream bos = new BufferedOutputStream(baos);
		int c = bufIn.read(); //������ �� �����
		while (c != -1) {
			bos.write(c);	  //������ � ������
			c = bufIn.read(); //������ �� �����
			}
		bos.flush();
		baos.flush();
		bos.close();
		return baos.toByteArray();
		}
	
	public void  repla�eText() { //��������� ������, ��� �������������, ��������� � �� ������������ � ��������� ���� � DOCS 
		try {	
			FileInputStream inputStream = new FileInputStream(fileInDocx);
			FileOutputStream outputStream = new FileOutputStream(fileOutDocx);
			ZipInputStream zipInputS = new ZipInputStream(inputStream );
			ZipOutputStream zipOutputS = new ZipOutputStream(outputStream);
		    ZipEntry entry = null;
		    while ((entry = zipInputS.getNextEntry()) != null) {					  //������� ������ � ������ zip �����
		    	byte[] bytes =  getBytesFromInputStream( zipInputS, entry.getSize() );//���������� ������� bytes �� ����� � ������
				//log.debug("Extracting " + entry.getName());
				if (entry.getName().equals("word/document.xml")) { // ��������� ����� ���� � ������ ������� ��������� ����� � XML ���� 
				    String str = new String(bytes, "UTF-8");       // ��������� ����� � ������ str 
				    bytes = strFindCorrect(str).getBytes("UTF-8"); // ������������ ������ ��� ������ ������ � ������ �� ������ � ��������� ������ 
				}
				zipOutputS.putNextEntry(entry); //������ �������� ����� � ������ 
				zipOutputS.write(bytes);		//������ ������� � ���� � ������
		    }
		    zipInputS.close();
		    zipOutputS.close();
		} catch (IOException e) {}
		}
}


	

	
	

