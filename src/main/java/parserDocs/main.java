package parserDocs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import org.ini4j.*;//
import java.sql.SQLException;

public class main {
	public static String name_in_file= "C:\\Users\\jax10\\eclipse-workspace\\helloapp\\original.docx";
	public static String name_out_file= "C:\\Users\\jax10\\eclipse-workspace\\helloapp\\out.docx";
	
	@SuppressWarnings("resource")
	public static void working_files(String file_config,String file_executive,String original_docx) {
		int separator = 0;
		//String pult ="";
		//String card ="";
		String filepath = Paths.get("").toAbsolutePath().toString() + "\\src\\main\\resources\\"; //получение пути к рабочему каталогу
		file_config    = filepath + file_config;   // добавляем путь к репозитарию + файл
		file_executive = filepath + file_executive;// добавляем путь к репозитарию + файл
		original_docx  = filepath + original_docx; // добавляем путь к репозитарию + файл
		try {
			FileReader file = new FileReader(file_executive);
			BufferedReader reader = new BufferedReader(file);
			String line = reader.readLine();
			Wini ini =new Wini();
			ini.getConfig().setLowerCaseSection(true);//определяем перевод в прописные парсинга 1 аргумента
			ini.getConfig().setLowerCaseOption(true); //определяем перевод в прописные парсинга 2 аргумента
			ini.load(new File(file_config)); 		  //загружаем фаил 
			data_viev sort_db = new data_viev(original_docx);
			while(line!=null){
				line = line.replace(" ", "").toLowerCase(); // убираем лишнии пробелы и переводит в прописные
				separator = line.indexOf(','); //поиск запятой в строке
				if (separator != -1) {		   //проверка на наличие зяпятой
					String pult =line.substring(0,separator);                //получаем позывной пульта
					String card = line.substring(separator+1,line.length()); //получекм номер карточки
					try {
						int i = Integer.parseInt(card); //проверка для защиты от доступа к базе данных посторонними командами
						String path_db = ini.get(pult , "ip_path_db");
						String path_directories = ini.get(pult , "path_pult");
						System.out.print("\n"+pult+"-"+card);
						//System.out.println(" [path_db]="+ path_db +" [card]=" + card +" [pult]="+ pult+ " [path_dir]=" + path_directories);
						try {
							if ((path_db!=null)||(path_directories!=null))  { 
								//path_directories = path_directories.replace('/', '\\'); //
								sort_db.viev_selection_(path_db, card, pult, filepath+path_directories);}
							else {System.err.print("->некоректные данные в файле конфигурации "+ file_config +" ["+pult.toUpperCase() + "] ip_path_db или path_pult несодержат значений");}
						} catch (SQLException e) {e.printStackTrace();}
						} catch(NumberFormatException e) {				
							System.err.print( "\n" +pult+"-"+card+"-> номер пульта содержит посторонние знаки и символы");		
						}
					}
				line = reader.readLine();//читаем фаил file_executive
				}
		} catch (IOException e) {System.out.println(e.toString());}
	}

	public static void main(String[] args){ 
		working_files("config.ini","command.txt","original.docx" );
		}
}



















