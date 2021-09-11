package parserDocsThisDB;

import static org.junit.Assert.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.junit.Test;
import parserDocs.SearchDB;
import parserDocs.replaсeTextDocs;

public class parserDocsThisDB_test {
	public static String name_in_file= "C:\\Users\\jax10\\eclipse-workspace\\helloapp\\word.docx";
	public static String name_out_file= "C:\\Users\\jax10\\eclipse-workspace\\helloapp\\out.docx";
	public static Map<String,String> data_dictionary1 ; //= new HashMap<String,String>()
	
	
	@Test
	public void test_strSummText_one() {
		//fail("Not yet implemented");
		final String expected ="<w:t>{{Key_name10}}</w:t>"; 
		replaсeTextDocs  replaсeText = new replaсeTextDocs(name_in_file,name_out_file);
		final String inString ="<w:t>{{Key_name10}}</w:t>";
		final String actual = replaсeText.strSummText(inString);
		System.out.println(actual);
		System.out.println(expected);
		assertEquals(expected, actual);
		//replaсeText.strFindCorrect(name_in_file);
		//System.out.println(actual);
	}
	
	@Test
	public void test_strSummText_several() {
		//fail("Not yet implemented");
		final String expected ="<w:t>{{Key_name11}}</w:t>"; 
		replaсeTextDocs  replaсeText = new replaсeTextDocs(name_in_file,name_out_file);
		final String inString ="<w:t>{{</w:t>123456<w:t>Key_name11</w:t>789<w:t>}}</w:t>";
		final String actual = replaсeText.strSummText(inString);
		assertEquals(expected, actual);
		//System.out.println(actual);
	}
	
	@Test
	public void test_strSummText_several2() {
		//fail("Not yet implemented");
		final String expected ="<w:t>{{</w:t>123456<w:t>Key_name11</w:t>789<w:t>}</w:t>222<w:p>"+
							   "<w:rPr><w:color w:val=\"FF0000\"/><w:highlight w:val=\"yellow\"/>"+
							   "333</w:rPr>444</w:p><w:t>}</w:t>44444<w:t>{{Key_name12}}</w:t>"; 
		replaсeTextDocs  replaсeText = new replaсeTextDocs(name_in_file,name_out_file);
		final String inString ="<w:t>{{</w:t>123456<w:t>Key_name11</w:t>789<w:t>}</w:t>222<w:p>"+
							   "<w:rPr>333</w:rPr>444</w:p><w:t>}</w:t>44444<w:t>{{Key_name12}}</w:t>";
		final String actual = replaсeText.strSummText(inString);
		System.out.println(actual);
		System.out.println(expected);
		assertEquals(expected, actual);
	}
	
	@Test
	public void test_strFindCorrect1() {
		//fail("Not yet implemented");
		final String expected ="a123456789a<w:t>{{Key_name10}}</w:t>d123456789d<w:t>{{Key_name11}}</w:t>c123456789c";
		replaсeTextDocs  replaсeText = new replaсeTextDocs(name_in_file,name_out_file);
		final String inString ="a123456789a<w:t>{{Key_name10}}</w:t>d123456789d<w:t>{{Key_name11}}</w:t>c123456789c";
		final String actual = replaсeText.strFindCorrect(inString);
		assertEquals(expected, actual);
	}
	
	@Test
	public void test_strFindCorrect2() {
		//fail("Not yet implemented");
		final String expected ="a123456789a<w:t>{{Key_name10}}</w:t>d123456789d<w:t>{{Key_name11}}</w:t>c123456789c";
		replaсeTextDocs  replaсeText = new replaсeTextDocs(name_in_file,name_out_file);
		final String inString ="a123456789a<w:t>{{</w:t>1111<w:t>Key_name10</w:t>2222<w:t>}}</w:t>d123456789d<w:t>{{Key_name11}}</w:t>c123456789c";
		final String actual = replaсeText.strFindCorrect(inString);
		assertEquals(expected, actual);
	}
	
	@Test
	public void test_strFindCorrect3() {
		//fail("Not yet implemented");
		final String expected ="a123456789a<w:t>{{Key_name10}}</w:t>d123456789d<w:t>{{Key_name11}}</w:t>c123456789c";
		replaсeTextDocs  replaсeText = new replaсeTextDocs(name_in_file,name_out_file);
		final String inString ="a123456789a<w:t>{{Key_name10}}</w:t>d123456789d<w:t>{{</w:t>123456<w:t>Key_name11</w:t>789<w:t>}}</w:t>c123456789c";
		final String actual = replaсeText.strFindCorrect(inString);
		System.out.println(expected);
		System.out.println(actual);
		assertEquals(expected, actual);
		}
	
	@Test
	public void test_strFindCorrect4() {
		//fail("Not yet implemented");
		Map<String, String> KeyMap = new HashMap<>();
		KeyMap.put("Key_name10", "TEST");
		final String expected ="a123456789a<w:t>TEST</w:t>d123456789d<w:t>{{Key_name11}}</w:t>c123456789c";
		replaсeTextDocs  replaсeText = new replaсeTextDocs(name_in_file,name_out_file);
		replaсeText.keyList = KeyMap;
		final String inString ="a123456789a<w:t>{{Key_name10}}</w:t>d123456789d<w:t>{{Key_name11}}</w:t>c123456789c";
		final String actual = replaсeText.strFindCorrect(inString);
		System.out.println(expected);
		System.out.println(actual);
		assertEquals(expected, actual);
		}
	
	@Test
	public void test_SearchDB1() {
		//fail("Not yet implemented");
		SearchDB test = new SearchDB();
		final boolean  actual = test.checkingPossibleConnection("localhost:C:/DB/don.gdb");
		assertTrue(actual);
		}
	
	@Test
	public void test_SearchDB2() {
		//fail("Not yet implemented");
		SearchDB test = new SearchDB();
		final boolean  actual = test.checkingPossibleConnection("localhost:Z:/DB/don.gdb");
		assertFalse(actual);
		}
	
	@Test
	public void test_SearchDB3() {
		//fail("Not yet implemented");
		SearchDB test = new SearchDB();
		final boolean  actual = test.checkingPossibleConnection("192.168.1.1:Z:/DB/don.gdb");;
		assertTrue(actual);
		}
	
	@Test
	public void test_SearchDB4() {
		//fail("Not yet implemented");
		SearchDB test = new SearchDB();
		final boolean  actual = test.checkingPossibleConnection("192.168.0.1:Z:/DB/don.gdb");
		assertFalse(actual);
		}
	
	@Test
	public void test_SearchDB5() {
		//fail("Not yet implemented");
		SearchDB test = new SearchDB();
		final boolean  actual = test.testConnectIp("192.168.1.1");
		assertTrue(actual);
		}
	
	@Test
	public void test_SearchDB6() {
		//fail("Not yet implemented");
		SearchDB test = new SearchDB();
		final boolean  actual = test.testConnectIp("192.168.0.1");
		assertFalse(actual);
		}

	
	@Test	
	public void test_SearchDB7() {
		//fail("Not yet implemented");
		SearchDB test = new SearchDB();
		final boolean  actual = test.testConnectLocalhostPath("C:/DB/don.gdb");
		assertTrue(actual);
		}
	
	@Test	
	public void test_SearchDB8() {
		//fail("Not yet implemented");
		SearchDB test = new SearchDB();
		final boolean  actual = test.testConnectLocalhostPath("Z:/DB/don.gdb");
		assertFalse(actual);
		}
	
	@Test	
	public void test_SearchDB9() {
		//fail("Not yet implemented");
		SearchDB test = new SearchDB();
		final boolean  actual = test.testConnectLocalhostPath("192.168.2.101:C:/DB/atlas.gdb");
		assertFalse(actual);
		}
	
	
	
	
	public void test_connectDB(){
		try {
        	Properties paramConnection = new Properties();
    	    paramConnection.setProperty("user", "sysdba");			// логин БД FIREBERD
    	    paramConnection.setProperty("password", "masterkey"); 	//пароль БД FIREBERD
    	    paramConnection.setProperty("encoding", "WIN1251");
    	    //paramConnection.setProperty("sql_dialect", "3");
			Class.forName("org.firebirdsql.jdbc.FBDriver");
			Connection conn=null;
			conn = DriverManager.getConnection("jdbc:firebirdsql:localhost:C:\\DB\\zaria.FDB", paramConnection);
			//conn = DriverManager.getConnection("jdbc:firebirdsql:localhost:C:\\DB\\don.gdb", paramConnection);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select CUSTNUM from anothercust"); 	//Выполняем SQL запрос.
			//ResultSet rs = stmt.executeQuery("select cr_key, cr_cardkey from cards where cr_cardkey < 10000 order by cr_cardkey"); 	//Выполняем SQL запрос.
			int Column =rs.getMetaData().getColumnCount()+1;
			while(rs.next()) {
				System.out.println();
				for (int n=1;n< Column; n++) {
					System.out.print(rs.getObject(n)+" | ");
					}
			}
			conn.close();
		} catch (ClassNotFoundException | SQLException e) {
			System.out.print(e);
			//e.printStackTrace();// TODO Автоматически созданный блок catch
			}
		}
	
	
	
	}


