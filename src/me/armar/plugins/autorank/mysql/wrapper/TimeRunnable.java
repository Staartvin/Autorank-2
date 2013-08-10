package me.armar.plugins.autorank.mysql.wrapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import me.armar.plugins.autorank.data.SQLDataStorage;

/**
 * This will get the database time.
 * 
 * We have to wait for the thread to finish before we can get the results
 * Every database lookup will have to have its own thread.
 * @author Staartvin
 *
 */
public class TimeRunnable implements Runnable{

	private MySQLWrapper wrapper;
	private SQLDataStorage mysql;
	private String name, table;
	private int time = 0;
	
	public TimeRunnable(MySQLWrapper wrapper, SQLDataStorage mysql, String name, String table) {
		this.wrapper = wrapper;
		this.mysql = mysql;
		this.name = name;
		this.table = table;
	}
	
	@Override
	public void run() {
		if (mysql != null) {

			String statement = "SELECT * FROM " + table + " WHERE name='"
					+ name + "'";
			ResultSet rs = mysql.executeQuery(statement);

			if (rs == null)
				time = -1;

			try {
				if (rs.next()) {
					time = rs.getInt(2);
				} else {
					time = -1;
				}

			} catch (SQLException e) {
				System.out.println("SQLException: " + e.getMessage());
				System.out.println("SQLState: " + e.getSQLState());
				System.out.println("VendorError: " + e.getErrorCode());
			}
		}
		
		wrapper.databaseTime = time; 
	}

}
