package parserDocs;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class work_db_fdb {
	
	public Map<String,String> data_dictionary = new HashMap<String,String>();
	private String data_id_object; 		//id_obj
	private String data_id_name_object;	//� ������� ����������� �������� �������
	private String data_id_cust; 		//id_cust
	private String index_phone;
	public boolean result; 
	
	public boolean SelectANOTHERCUST(SearchDB db, String card) {
			ResultSet data;
			String command_SQL = "select ANOTHERCUST.custname, ANOTHERCUST.id_cust, objects.label, "
	        		+ "(select  dogovora.dognum from  dogovora where dogovora.id_dog = ANOTHERCUST.id_dog ),"
	        		+ "  ANOTHERCUST.objid from ANOTHERCUST, objects where objects.id = ANOTHERCUST.custclassid "
	        		+ "and (ANOTHERCUST.custmode=1) and ANOTHERCUST.CUSTNUM="+ card;
			data= db.requesting_data_one(command_SQL);
			if (data == null) { 
				System.err.print("->�������� �� �������");
				return false;}
			data_id_name_object = db.redo_and_check(data,1); //� ������� ����������� �������� �������
			data_id_cust =        db.redo_and_check(data,2); //id_cust
			data_dictionary.put("card", card);   //� ������� ����������� �������� �������
			data_dictionary.put("type_object", db.redo_and_check(data,3).toUpperCase());  //� ������� ����������� ��� �������
	        	// �� ���� ������� ����������� ������������ ����
			if (((data_dictionary.get("type_object").equals("��������")) || (data_dictionary.get("type_object").equals("�����")))) {
				data_dictionary.put("type_object_physical", data_id_name_object);//� ������� ����������� ���������� ������� ��� ����������� ����
				}
			else {data_dictionary.put("type_object_legal", data_id_name_object);} //� ������� ����������� ���������� ������� ��� ������������ ����
			data_dictionary.put("cont", db.redo_and_check(data,4));  //� ������� ����������� ������� �������
			data_id_object = db.redo_and_check(data,5);  //id_obj
			return true;
	}
	
	public void SelectAdsress(SearchDB db) {
		ResultSet data;
		data= db.requesting_data_one("select podezd, floor, custdescription from anothercust_ext where id_cust=" + data_id_cust);
        data_dictionary.put("entrance", db.redo_and_check(data,1)); //� ������� ����������� �������
        data_dictionary.put("floor"   , db.redo_and_check(data,2)); //� ������� ����������� ����
        data_dictionary.put("memo1"   , db.redo_and_check(data,3)); //� ������� ����������� �������������� �������
	}
	
	public void SelectOps(SearchDB db) {
		ResultSet data;
		data= db.requesting_data_one("select ops_type.title from ops,  ops_type where ops.type_ = ops_type.subtype_ and ops.id = " + data_id_object);
        data_dictionary.put("type_ou" , db.redo_and_check(data,1)); //� ������� ����������� ��� ���������� ����������
	}
	
	public void SelectAddress2(SearchDB db) {	        
		ResultSet data;
		String command_SQL = "SELECT (select labels.label from  labels where addresses.id_street = labels.id  ), " +
            " addresses.house_num, addresses.building, addresses.korpus, addresses.flat_num, " +
            " (select labels.label from  labels where addresses.id_settl = labels.id  ) " +
            " FROM addresses where  addresses.id_owner=" + data_id_cust;
		data= db.requesting_data_one(command_SQL);//� ������� ����������� ����� �������
		data_dictionary.put("address_object", db.address_format(data, 1, 2 ,3 ,4 ,5 ,6 )); //
	}
	
	public void SelectPhones(SearchDB db) {
		ResultSet data;
		data= db.requesting_data_one("select phone from phones where phone_type=1 and id_owner=" + data_id_cust );
        data_dictionary.put("phone", db.redo_and_check(data,1)); //� ������� ����������� ������� �������
	}
	
	public void SelectXoPhones(SearchDB dbClone1, int line) throws SQLException {
			//������������ ������
		ResultSet data_phones_list = dbClone1.requesting_data_list("select phone_type, phone from phones where  id_owner=" + index_phone );
        String phone_all = "";
        //# � ����������� �� ���� data_phones[0] ������������� ��������� � ����� � ��������
        while (data_phones_list.next()) {	
        	int data_phones_index = data_phones_list.getInt(1);
            if (data_phones_index == 1) {phone_all += "  " + data_phones_list.getString(2) + "\n";}
            if (data_phones_index == 3) {phone_all += "�." + data_phones_list.getString(2) + "\n";}
            if (data_phones_index == 4) {phone_all += "c." + data_phones_list.getString(2) + "\n";}
            int lenght_string_allPhone = phone_all.length(); // ����������� ����� ������ �� ������� ��������� phone_all
            if (lenght_string_allPhone < 1) {lenght_string_allPhone = 1;} // substring(0, - 1) ������� ������ � �������� substring(0, 0) 
            data_dictionary.put("phone"+String.valueOf(line), phone_all.substring(0, lenght_string_allPhone - 1)); // ������� ��������� ������ \n
            }
	}
	
	public void SelectXoName(SearchDB db ) throws SQLException{
		int line =0;
		SearchDB dbClone1 = db.clone(); //������� ����� ������ � ���������� �� ���������� �������������
		dbClone1.SearchDBconnect();//����������� ����������� � ���� ������ � �����
		// ��������� ���������� select ������� ��� ���������� ���� � ���������� ������ ��� �������� ���������� ������ �������
        String command_SQL =    "select xo.surname, xo.name, xo.patronymic, " +
                         "(select labels.label FROM labels where addresses.id_street = labels.id)" +
                         ", addresses.house_num,  addresses.building, addresses.korpus, addresses.flat_num, " +
                         "xo.comment, (select labels.label from  labels where addresses.id_settl = labels.id  )," +
                         " xo.id_xo FROM xo, addresses, anothercust_xo where addresses.id_owner=xo.id_xo " +
                         " and anothercust_xo.id_xo =  xo.id_xo and anothercust_xo.id_cust ="  + data_id_cust;
        ResultSet data_human = db.requesting_data_list(command_SQL);
        while (data_human.next()) {
            ++line;
            // ������� ����������� ������� ��� ��������� ��������
        	data_dictionary.put("name"+     String.valueOf(line), (db.redo_and_check(data_human ,1) + " " + db.redo_and_check(data_human ,1) + " " + db.redo_and_check(data_human ,2)));
            data_dictionary.put("position"+ String.valueOf(line), db.redo_and_check(data_human ,9)); // ������� ����������� ����������� �� ��������
            data_dictionary.put("address"+  String.valueOf(line), db.address_format(data_human ,4 ,5 ,6 ,7 ,8 ,10)); // ������� ����������� ����� ��������
            index_phone = db.redo_and_check(data_human ,11);
            if (!index_phone.equals("0")) {	
             	SelectXoPhones(dbClone1,line);
             	}
            }
	}
		
	public void SelectObjects(SearchDB db) throws SQLException {
		int line =0;
		ResultSet data;
		String command_SQL = "select objects.ncn, objects.label from objects where objects.isactive = 1 and  objects.parid =" + data_id_object;
	    data = db.requesting_data_list(command_SQL);
	    while (data.next()) {
	    	++line;
	    	data_dictionary.put("Shn" + String.valueOf(line), data.getString(1)); // ����� ������
	        data_dictionary.put("Shm" + String.valueOf(line), data.getString(2)); // �������� ������
	        }
	    }
	
	public void read_data_fdb(String path_db, String  card) throws SQLException {
			SearchDB db  = new SearchDB(path_db);
			result = false;
			if (SelectANOTHERCUST(db, card)) { //���� � ������ ������� �� ������� ����� �� �������� � ���������� ��������� �� ����� ������  
				SelectAdsress(db);
				SelectOps(db);
				SelectAddress2(db);
				SelectPhones(db);
				SelectXoName(db);
				SelectObjects(db);
				System.out.print("->�������� ����������");
				result = true; //true - �������� ����������
				}
			}
}            


