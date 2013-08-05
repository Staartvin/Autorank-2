package me.armar.plugins.autorank.mysql.wrapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.configuration.ConfigurationSection;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.data.SQLDataStorage;
import me.armar.plugins.autorank.data.SimpleYamlConfiguration;

/**
 * This class keeps all incoming and outgoing under control.
 * It sends MySQL queries and can locate the database.
 * Previously, {@link me.armar.plugins.autorank.playtimes.Playtimes} kept all
 * MySQL, but it wasn't neatly organised.
 * MySQLWrapper class is (hopefully) fail-prove and organised.
 * 
 * This also has a fail-safe when two queries are altering at the same time.
 * 
 * @author Staartvin
 * 
 */
public class MySQLWrapper {

	private Autorank plugin;
	private SQLDataStorage mysql;
	String hostname;
	String username;
	String password;
	String database;
	String table;

	public MySQLWrapper(Autorank instance) {
		plugin = instance;

		sqlSetup(plugin.getAdvancedConfig());
		if (mysql != null) {
			setupTable();
		}
	}

	public void setupTable() {
		// Check if connection is still alive
		if (mysql.isClosed()) {
			mysql.connect();
		}

		String statement = "CREATE TABLE  IF NOT EXISTS " + table + " "
				+ "(name VARCHAR(16) not NULL, " + " time INTEGER not NULL, "
				+ " modified TIMESTAMP not NULL, " + " PRIMARY KEY ( name ))";

		// TODO Auto-generated method stub
		mysql.execute(statement);

	}

	public void sqlSetup(SimpleYamlConfiguration config) {
		ConfigurationSection s = config.getConfigurationSection("sql");

		Boolean enabled = s.getBoolean("enabled");
		if (enabled != null && enabled) {

			hostname = s.getString("hostname");
			username = s.getString("username");
			password = s.getString("password");
			database = s.getString("database");
			table = s.getString("table");

			mysql = new SQLDataStorage(hostname, username, password, database);
			if (!mysql.connect()) {
				mysql = null;
				plugin.getLogger().severe("Could not connect to " + hostname);
			} else {
				plugin.getLogger().info(
						"Successfully established connection to " + hostname);
			}
		}
	}

	public int getDatabaseTime(final String name) {
		// Check if connection is still alive
		if (mysql.isClosed()) {
			mysql.connect();
		}

		int time = -1;

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
				System.out.println("Playtimes.getDBTime");
				System.out.println("SQLException: " + e.getMessage());
				System.out.println("SQLState: " + e.getSQLState());
				System.out.println("VendorError: " + e.getErrorCode());
			}
		}
		return time;
	}

	/**
	 * Sets the time of a player
	 * 
	 * @param playerName Player to set the time of
	 * @param time Time to change to
	 */
	public void setGlobalTime(String playerName, int time) {
		// Check if connection is still alive
		if (mysql.isClosed()) {
			mysql.connect();
		}

		String statement = "INSERT INTO " + table + " VALUES ('" + playerName
				+ "', " + time + ", CURRENT_TIMESTAMP) "
				+ "ON DUPLICATE KEY UPDATE " + "time=" + time;

		// TODO Auto-generated method stub
		mysql.execute(statement);

	}

	public boolean isMySQLEnabled() {
		return mysql != null;
	}
	
	public String getDatabaseName() {
		return database;
	}
}
