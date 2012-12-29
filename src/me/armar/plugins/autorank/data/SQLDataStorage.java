package me.armar.plugins.autorank.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLDataStorage {

    private String hostname;
    private String username;
    private String password;
    private String database;

    private Connection conn = null;

    public SQLDataStorage(String hostname, String username, String password, String database) {
	this.hostname = hostname;
	this.username = username;
	this.password = password;
	this.database = database;
    }
    
    public void execute(String sql) {
	Statement stmt = null;

	if (conn != null) {
	    try {

		stmt = conn.createStatement();
		stmt.execute(sql);

	    } catch (SQLException ex) {
		System.out.println("SQLDataStorage.execute");
		System.out.println("SQLException: " + ex.getMessage());
		System.out.println("SQLState: " + ex.getSQLState());
		System.out.println("VendorError: " + ex.getErrorCode());
	    } finally {


		if (stmt != null) {
		    try {
			stmt.close();
		    } catch (SQLException sqlEx) {
		    }

		    stmt = null;
		}
	    }
	}

    }

    public ResultSet executeQuery(String sql) {
	Statement stmt = null;
	ResultSet rs = null;

	if (conn != null) {
	    try {

		stmt = conn.createStatement();
		rs = stmt.executeQuery(sql);

	    } catch (SQLException ex) {
		System.out.println("SQLDataStorage.executeQuery");
		System.out.println("SQLException: " + ex.getMessage());
		System.out.println("SQLState: " + ex.getSQLState());
		System.out.println("VendorError: " + ex.getErrorCode());
	    } finally {

		if (rs != null) {
		    try {
			rs.close();
		    } catch (SQLException sqlEx) {
		    }

		    rs = null;
		}

		if (stmt != null) {
		    try {
			stmt.close();
		    } catch (SQLException sqlEx) {
		    }

		    stmt = null;
		}
	    }
	}

	return rs;
    }

    public boolean connect() {
	try {
	    Class.forName("com.mysql.jdbc.Driver").newInstance();

	    System.out.println("jdbc:mysql://" + hostname + "/" + database + "?" + "user=" + username + "&password="
		    + password);
	    
	    conn = DriverManager.getConnection("jdbc:mysql://" + hostname + "/" + database + "?" + "user=" + username + "&password="
		    + password);


	} catch (SQLException ex) {
	    System.out.println("SQLDataStorage.connect");
	    System.out.println("SQLException: " + ex.getMessage());
	    System.out.println("SQLState: " + ex.getSQLState());
	    System.out.println("VendorError: " + ex.getErrorCode());
	} catch (Exception e) {

	    e.printStackTrace();
	}
	return conn != null;
    }

}
