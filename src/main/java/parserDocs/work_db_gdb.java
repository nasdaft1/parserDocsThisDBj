package parserDocs;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class work_db_gdb {
	
	public Map<String,String> data_dictionary = new HashMap<String,String>(); //������� ������ ��� ������ � ������ 
	private String data_id_name_object;
	private String id_key;
	private String id_type_otp;
	public boolean result; 
	
	public boolean SelectCards(SearchDB db,String card) throws SQLException {
		String command_SQL = "select cards.cr_key, cards.cr_oc_number, cards.cr_st_id, cards.cr_buildnum,cards.cr_buildcorps, " +
	              "cards.cr_floor,cards.cr_entrance, cards.cr_placenum,cards.cr_placedesc,cards.cr_safetyproblems, " +
	              "cards.cr_otp_id,cards.cr_buildfract,cards.cr_crossphone, cards.cr_name, " +
	              "(select str_name from  streets where streets.str_id = cards.cr_st_id )" +
	              " from cards where cr_cardkey=" + card;
		ResultSet data= db.requesting_data_one(command_SQL);
		if (data == null) {
			System.err.print("->�������� �� �������");
			return false;}
		data_id_name_object =  data.getString(14).trim(); // ������
		data_dictionary.put("card",     card);   //� ������� ����������� �������� �������
		data_dictionary.put("entrance",data.getString(7)); // �������
		data_dictionary.put("floor",   data.getString(6)); // ����
		data_dictionary.put("cont",    data.getString(2).trim()); // ����� ��������
		data_dictionary.put("phone",   data.getString(13).trim());// ������� ����������������
		id_key            = data.getString(1);   // id - ��� ����������� ���� �������, ������ �����������, ������ �������
		id_type_otp       = data.getString(11);  // id - ��� ����������� ���� �������
		data_dictionary.put("memo1",(data.getString(9)).trim() + '\n' + (data.getString(10)).trim()); //�������������� �������, ���������� �������
		data_dictionary.put("address_object", db.address_format(data, 15, 4, 5, 12, 8)); // ����� �������
		return true;
	}
	
	public void SelectOtp_name(SearchDB db) throws SQLException {
		ResultSet data_type_object = db.requesting_data_one("select otp_name from  objtypes where otp_id=" + id_type_otp);
		String data_type_object_naim = data_type_object.getString(1).trim().toUpperCase();
		data_dictionary.put("type_object", data_type_object_naim);
		if (data_type_object_naim.equals("��������") || data_type_object_naim.equals("�����")) {
		     //� ������� ����������� ���������� ������� ��� ����������� ����
		     data_dictionary.put("type_object_physical", data_id_name_object);
		     }
		else { data_dictionary.put("type_object_legal", data_id_name_object);} 				            //� ������� ����������� ���������� ������� ��� ������������ ����
	}

	public void SelectXo(SearchDB db) throws SQLException {
		//�������� ������ � ������� ���, ������, �����, ��������
		int line =0;
		ResultSet data = db.requesting_data_list("select * from  keymen where keymen.km_cr_key=" + id_key); //������ �� ������ �����������
		while (data.next()) {
		    ++line;
		  	String sLine =String.valueOf(line); // � ������� ����������� ��� ����������
		  	data_dictionary.put("name"+sLine     ,data.getString(3).trim() +" "+ data.getString(4).trim() + " " + data.getString(5).trim());
			data_dictionary.put("position"+sLine ,db.redo_and_check(data ,10)); // � ������� ����������� ����������� �� ��������
			data_dictionary.put("address"+sLine  ,data.getString(8).trim());  // � ������� ����������� ����� ��������
			data_dictionary.put("phone"+sLine    ,data.getString(9).trim());  // � ������� ����������� ������� ��������
			}
		}
	
	public void SelectUo(SearchDB db) throws SQLException {
		//�������� ������ � ����� ���������� ����������
		ResultSet data;
		data = db.requesting_data_one("select uo.uo_name from  line, uo where uo.uo_ln_id = line.ln_id and line.ln_cr_key="+id_key);
		data_dictionary.put("type_ou",data.getString(1).trim()); 
		}
	
	public void SelectObjects(SearchDB db) throws SQLException {
		//������� ������ �������
		ResultSet data = db.requesting_data_list("select sh_number,sh_name from  sh where sh.sh_cr_key=" + id_key); //������ �� ������ �������
		int line = 0;
		while (data.next()) {
		    ++line;
		   	String sLine2 =String.valueOf(line);
		    data_dictionary.put("Shn" + sLine2 ,String.valueOf(data.getInt(1)+1)); // ����� ������
		    data_dictionary.put("Shm" + sLine2 , data.getString(2)); // �������� ������
		    }
		}
	
	public void read_data_gdb(String path_db, String  card)  throws SQLException {
		result = false;
		SearchDB db = new SearchDB(path_db);
		if (SelectCards(db, card)) { //���� � ������ ������� �� ������� ����� �� �������� � ���������� ��������� �� ����� ������
			SelectOtp_name(db);
			SelectXo(db);
			SelectObjects(db);
			System.out.print("->�������� ����������");
			result = true; //true - �������� ����������
			}
    	}
	}