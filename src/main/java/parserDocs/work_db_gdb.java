package parserDocs;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class work_db_gdb {
	
	public Map<String,String> data_dictionary = new HashMap<String,String>(); //словарь ключей для замены в тексте 
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
			System.err.print("->карточка не найдена");
			return false;}
		data_id_name_object =  db.redo_and_check(data ,14); // объект
		data_dictionary.put("card",     card);   //в словарь добавляется позывной объекта
		data_dictionary.put("entrance",db.redo_and_check(data ,7)); // подъезд 
		data_dictionary.put("floor",   db.redo_and_check(data ,6)); // этаж
		data_dictionary.put("cont",    db.redo_and_check(data ,2)); // номер договора
		data_dictionary.put("phone",   db.redo_and_check(data ,13));// телефон закроссированный
		id_key            = db.redo_and_check(data ,1);   // id - для определение типа прибора, список сотрудников, список шлейфов
		id_type_otp       = db.redo_and_check(data ,11);  // id - для определение типа объекта
		data_dictionary.put("memo1",db.redo_and_check(data ,9) + '\n' + db.redo_and_check(data ,10)); //характеристики объекта, уязвимости объекта
		data_dictionary.put("address_object", db.address_format(data, 15, 4, 5, 12, 8)); // адрес объекта
		return true;
	}
	
	public void SelectOtp_name(SearchDB db) throws SQLException {
		ResultSet data_type_object = db.requesting_data_one("select otp_name from  objtypes where otp_id=" + id_type_otp);
		String data_type_object_naim = db.redo_and_check(data_type_object ,1).toUpperCase(); 
		data_dictionary.put("type_object", data_type_object_naim);
		if (data_type_object_naim.equals("КВАРТИРА") || data_type_object_naim.equals("МХЛИГ")) {
		     //в словарь добавляется названание объекта для физического лица
		     data_dictionary.put("type_object_physical", data_id_name_object);
		     }
		else { data_dictionary.put("type_object_legal", data_id_name_object);} 				            //в словарь добавляется названание объекта для юридического лица
	}

	public void SelectXo(SearchDB db) throws SQLException {
		//создание списка с данными ФИО, статус, адрес, телефоны
		int line =0;
		ResultSet data = db.requesting_data_list("select * from  keymen where keymen.km_cr_key=" + id_key); //Запрос на список сотрудников
		while (data.next()) {
		    ++line;
		  	String sLine =String.valueOf(line); // в словарь добавляется ФИО сотрудника
		  	data_dictionary.put("name"+sLine     ,db.redo_and_check(data ,3) +" "+ db.redo_and_check(data ,4) + " " + db.redo_and_check(data ,5));
			data_dictionary.put("position"+sLine ,db.redo_and_check(data ,10)); // в словарь добавляется комментарий по человеку
			data_dictionary.put("address"+sLine  ,db.redo_and_check(data ,8));  // в словарь добавляется адрес человека
			data_dictionary.put("phone"+sLine    ,db.redo_and_check(data ,9));  // в словарь добавляется телефон человека
			}
		}
	
	public void SelectUo(SearchDB db) throws SQLException {
		//создание списка с типом оконечного устройства
		ResultSet data;
		data = db.requesting_data_one("select uo.uo_name from  line, uo where uo.uo_ln_id = line.ln_id and line.ln_cr_key="+id_key);
		data_dictionary.put("type_ou",db.redo_and_check(data ,1)); 
		}
	
	public void SelectObjects(SearchDB db) throws SQLException {
		//создать список шлейфов
		ResultSet data = db.requesting_data_list("select sh_number,sh_name from  sh where sh.sh_cr_key=" + id_key); //Запрос на список шлейфов
		int line = 0;
		while (data.next()) {
		    ++line;
		   	String sLine2 =String.valueOf(line);
		    try  {
		    	data_dictionary.put("Shn" + sLine2 ,String.valueOf(data.getInt(1)+1)); // номер шлейфа
		    } catch(NumberFormatException nfe) {}
		    data_dictionary.put("Shm" + sLine2 , db.redo_and_check(data ,2)); // описание шлейфа
		    }
		}
	
	public void read_data_gdb(String path_db, String  card)  throws SQLException {
		result = false;
		SearchDB db = new SearchDB(path_db);
		if (SelectCards(db, card)) { //если в первой таблице не найдены ключи то работать с остальными таблицами не имеет смысла
			SelectOtp_name(db);
			SelectXo(db);
			SelectObjects(db);
			System.out.print("->карточка обработана");
			result = true; //true - карточка обработана
			}
    	}
	}
