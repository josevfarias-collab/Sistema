package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {

	private static final String URL = "jdbc:mysql://localhost:3306/sistema";
	private static final String USER="root";
	private static final String PASS="";
	
	public static Connection getConnection() {
	    try {
	        return DriverManager.getConnection(URL, USER, PASS);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }
	}
	
}
