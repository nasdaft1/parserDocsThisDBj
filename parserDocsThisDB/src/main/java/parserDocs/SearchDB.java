package parserDocs;


import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class SearchDB implements Cloneable{
	
	public Statement stmt;
	public String path_db;
	private String url;  
	private Properties paramConnection;
	// ������� �������� ���������� � ����������� ��� ���������� ������� ���������
	// ������� ����� ��� ����������� � ���������� � ��

	public SearchDB(String path_db) {
		this.path_db = path_db;
		this.paramConnection = new Properties();
    	this.paramConnection.setProperty("user", "sysdba");			// ����� �� FIREBERD
    	this.paramConnection.setProperty("password", "masterkey"); 	//������ �� FIREBERD
    	this.paramConnection.setProperty("encoding", "WIN1251");		//��������� �� FIREBERD
    	this.url = "jdbc:firebirdsql:"+ path_db;
	    if (checkingPossibleConnection(path_db)) {//�������� ���� �� ��������� ��� ������� �� ip � ���������� 
	    	SearchDBconnect();
	    	}
	    }
	
	public SearchDB() {} // ��� ������� � ������ � ������� �� JUnit
	
	public void SearchDBconnect(){
		try {
    		Class.forName("org.firebirdsql.jdbc.FBDriver");
    		Connection conn=null;
    		conn = DriverManager.getConnection(url,  paramConnection);
    		stmt = conn.createStatement();
    		if (conn == null) {System.err.print("->�� ��������� ���� ������ "+ path_db);}
			} catch (ClassNotFoundException | SQLException e) {	}
		}
	
	public boolean testConnectIp(String ipAdress) {  
		try {
			@SuppressWarnings("resource")
			Socket socket = new Socket();
			socket.connect(new InetSocketAddress(ipAdress, 23), 10);
			if (socket.isConnected()) { return true; } // ���� ���������� �� 
			} catch (IOException e) { }
		System.err.print("->��� ������� � ���������� ���� ������ "+ ipAdress);
		return false;  // ��� ���������� ��� ����� ������
		}
	
	//�������� ����� �� ���������� LOCALHOST
	public boolean testConnectLocalhostPath(String pathFile) {
		final File file = new File(pathFile);
	    if (file.exists()) { 
	    	return true;}
	    System.err.print("->����������� ����� �� ���������� "+ pathFile);
	    return false;  // �� ������ ���� �� ��������� �� ������� ����
	    }
	
	//������� �������� ������� � ����� �� ���������� � ���������� � ����
	public boolean checkingPossibleConnection(String path_dbConnect) {
		//path_dbConnect
		String ip = path_dbConnect.substring(0,path_dbConnect.indexOf(":")).toUpperCase();
		String diskPath = path_dbConnect.substring(path_dbConnect.indexOf(":")+1,path_dbConnect.length());

		if (ip.equals("LOCALHOST")) {
			if (!testConnectLocalhostPath(diskPath)) {return false;}
			} else {
				if (!testConnectIp(ip)) {return false;}
				}
		return true;
		}
   
	//�������� ResultSet � �������������� �������		
	public ResultSet requesting_data_list (String command_SQL) {
		ResultSet rs = null;
		try {			
			rs = stmt.executeQuery(command_SQL);
			} catch (SQLException e) {
			e.printStackTrace();
			}
		return rs;
		}
	
	//�������� ResultSet � ����� ������� ������
	public ResultSet requesting_data_one (String command_SQL) {
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(command_SQL);
			if (rs.next()) { } else {System.err.println(command_SQL+"->��� ������");}
			} catch (Exception e) {}
		return rs;
		}
	
	// ��������� ������� ������ �������
	public String address_format(ResultSet data, int street , int house_num, int bilding, int korpus, int flat_num) {
		String street_str   = redo_and_check(data, street);  // �����
		String bilding_str  = redo_and_check(data, bilding); // ��������
		String korpus_str   = redo_and_check(data, korpus);  // ������ ������
		String flat_num_str = redo_and_check(data, flat_num);// ����� ������
	    if (!flat_num_str.equals("")) {flat_num_str = "-"     + flat_num_str;}
        if (!bilding_str.equals(""))  {bilding_str  = " ���." + bilding_str;}
        if (!korpus_str.equals(""))   {korpus_str   = "\\"    + korpus_str;}
	    String s = street_str + " "  + redo_and_check(data, house_num) + bilding_str + korpus_str + flat_num_str;
	    return s;
	    }
	
	// ��������� ������� ������ ������� � ������������� ����� (�������, ����, �����)
	public String address_format(ResultSet data, int street , int house_num, int bilding, int korpus, int flat_num, int district) {
		String district_str = redo_and_check(data, district);
		return district_str +" "+ address_format(data, street , house_num, bilding, korpus, flat_num);
	    }
		
	// ����� ��� ��������� ������ ������������� (index) ���� ������� ���� ������
	public String redo_and_check(ResultSet data,int index) {
		String result ="";
		try {
			if (data.getObject(index) !=  null) {result = (data.getString(index)).trim(); }// ���� ���� �� Null ��������� � str � ������� �������
			} catch (SQLException e) {}
		return result;
		}
	
	// ��������� ������������ �������
    public SearchDB clone() {
    	try {
			return (SearchDB)super.clone();
		} catch (CloneNotSupportedException e) {e.printStackTrace(); return null;}
    	}
}

