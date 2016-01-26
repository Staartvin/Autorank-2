package me.armar.plugins.autorank.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLDataStorage {

	private Connection conn = null;
	private final String database;
	private final String hostname;
	private final String password;

	private final String username;

	/**
	 * Create a new MySQL Connection
	 * 
	 * @param hostname Hostname (Ex. 127.0.0.1:3306)
	 * @param username Username
	 * @param password Password
	 * @param database Database
	 */
	public SQLDataStorage(final String hostname, final String username,
			final String password, final String database) {
		this.hostname = hostname;
		this.username = username;
		this.password = password;
		this.database = database;
	}

	/**
	 * Open a new MySQL connection
	 * 
	 * @return true if connection was successfully set up.
	 */
	public boolean connect() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			
			final String url = "jdbc:mysql://" + hostname + "/" + database;

			conn = DriverManager.getConnection(url, username, password);

		} catch (final SQLException ex) {
			System.out.println("SQLDataStorage.connect");
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		} catch (final Exception e) {

			e.printStackTrace();
		}
		return conn != null;
	}

	/**
	 * Execute a query. Query cannot be null.
	 * This query doesn't return anything. (Good for updating tables)
	 * 
	 * @param sql Query to execute
	 */
	public void execute(final String sql) {
		Statement stmt = null;

		if (conn != null) {
			try {

				stmt = conn.createStatement();
				stmt.execute(sql);

			} catch (final SQLException ex) {
				System.out.println("SQLDataStorage.execute");
				System.out.println("SQLException: " + ex.getMessage());
				System.out.println("SQLState: " + ex.getSQLState());
				System.out.println("VendorError: " + ex.getErrorCode());
			} finally {

				if (stmt != null) {
					try {
						stmt.close();
					} catch (final SQLException sqlEx) {
					}

					stmt = null;
				}
			}
		}

	}

	/**
	 * Execute a query and returns a ResultSet. Query cannot be null.
	 * 
	 * @param sql Query to execute
	 * @return ResultSet if successfully performed, null if an error occured.
	 */
	public ResultSet executeQuery(final String sql) {
		Statement stmt = null;
		ResultSet rs = null;

		if (conn != null) {
			try {

				stmt = conn.createStatement();
				rs = stmt.executeQuery(sql);

			} catch (final SQLException ex) {
				System.out.println("SQLDataStorage.executeQuery");
				System.out.println("SQLException: " + ex.getMessage());
				System.out.println("SQLState: " + ex.getSQLState());
				System.out.println("VendorError: " + ex.getErrorCode());

			}
			// Removed this because the ResultSet would always be null on return.
			/*finally {

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
			} */
		}
		return rs;
	}

	/**
	 * Returns state of MySQL connection
	 * 
	 * @return true if closed, false if open
	 */
	public boolean isClosed() {
		try {
			return conn.isClosed();
		} catch (final SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return true;
		}
	}

	public void closeConnection() {
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}

}
