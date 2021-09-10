package parserDocs;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class data_viev {
	private String original_docx_name ="";
	private boolean result;
	public Map<String,String> data_dictionary1 ; //= new HashMap<String,String>();

	public data_viev(String original_docx) {
		original_docx_name = original_docx;
		}

	public void viev_selection_(String path_db, String card, String pult, String  directories) throws SQLException{
		String path_db_type = "";
		int path_db_length = path_db.length(); // ����������� ����� ���� � ������
		if (path_db_length > 4) {
			path_db_type = path_db.substring(path_db_length - 4); // ����������� ���� �����
			}
		
		if (path_db_type.equals(".fdb")) { // ��� ������ � ����� ���� ������ .fdb
			work_db_fdb work_db = new work_db_fdb();
			work_db.read_data_fdb(path_db, card);
			data_dictionary1 = work_db.data_dictionary;
			result = work_db.result;
			}
		
		if (path_db_type.equals(".gdb")) { // ��� ������ � ����� ���� ������ .gdb
			work_db_gdb work_db = new work_db_gdb();
			work_db.read_data_gdb(path_db, card);
			data_dictionary1 = work_db.data_dictionary;
			result = work_db.result;
			}
			
		if (((path_db_type.equals(".fdb")) || (path_db_type.equals(".gdb"))) && (result == true)) {
			String path_and_name_file = directories + card + ".docx";
			repla�eTextDocs a = new repla�eTextDocs(original_docx_name, path_and_name_file);
			DateFormat dateFormat = new SimpleDateFormat("dd.MM.yy�."); //����������� ������� ������
			a.keyList = data_dictionary1; 						// ��������� � ����� ������� �
			a.keyList.put("ed",pult.toUpperCase());				// � ������� ��������� �������� ������ 
			a.keyList.put("data",dateFormat.format(new Date()));// ������� ���� �� ��������
			a.repla�eText();
			}
		}
	}
