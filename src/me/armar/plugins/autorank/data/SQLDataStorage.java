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

	/**
	 * Create a new MySQL Connection
	 * @param hostname Hostname (Ex. 127.0.0.1:3306)
	 * @param username Username
	 * @param password Password
	 * @param database Database
	 */
	public SQLDataStorage(String hostname, String username, String password,
			String database) {
		this.hostname = hostname;
		this.username = username;
		this.password = password;
		this.database = database;
	}
	
	/**
	 * Execute a query. Query cannot be null.
	 * This query doesn't return anything. (Good for updating tables)
	 * @param query Query to execute
	 */
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

	/**
	 * Execute a query and returns a ResultSet. Query cannot be null.
	 * @param query Query to execute
	 * @return ResultSet if successfully performed, null if an error occured.
	 */
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

	/**
	 * Open a new MySQL connection
	 * @return true if connection was successfully set up. 
	 */
	public boolean connect() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();

			System.out.println("jdbc:mysql://" + hostname + "/" + database
					+ "?" + "user=" + username + "&password=" + password);

			conn = DriverManager.getConnection("jdbc:mysql://" + hostname + "/"
					+ database + "?" + "user=" + username + "&password="
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
