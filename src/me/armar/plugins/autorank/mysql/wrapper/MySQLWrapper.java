package me.armar.plugins.autorank.mysql.wrapper;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.data.SQLDataStorage;
import me.armar.plugins.autorank.data.SimpleYamlConfiguration;

import org.bukkit.configuration.ConfigurationSection;

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

	private final Autorank plugin;
	private SQLDataStorage mysql;
	String hostname, username, password, database, table;
	// Database time
	int databaseTime = 0;
	// This thread will be used to check if the database time has been retrieved.
	Thread timeThread;

	public MySQLWrapper(final Autorank instance) {
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

		final String statement = "CREATE TABLE  IF NOT EXISTS " + table + " "
				+ "(name VARCHAR(16) not NULL, " + " time INTEGER not NULL, "
				+ " modified TIMESTAMP not NULL, " + " PRIMARY KEY ( name ))";

		// Run async to prevent load issues.
		plugin.getServer().getScheduler()
				.runTaskAsynchronously(plugin, new Runnable() {

					@Override
					public void run() {
						mysql.execute(statement);
					}
				});

	}

	/**
	 * Because the MySQL queries are done async, we need to wait for the result.
	 * Otherwise it would be cached and out of date.
	 * This waits for the thread to die and then it will continue
	 * Use this whenever you do an async MySQL thread.
	 */
	private void waitForThread(final Thread thread) {
		if (thread.isAlive()) {
			try {
				thread.join();
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void sqlSetup(final SimpleYamlConfiguration config) {
		final ConfigurationSection s = config.getConfigurationSection("sql");

		final Boolean enabled = s.getBoolean("enabled");
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

	/**
	 * Gets the database time of player
	 * Run this ASYNC!
	 * 
	 * @param name Playername to get the time of
	 * @return time player has played across all servers
	 */
	public int getDatabaseTime(final String name) {
		// Check if connection is still alive
		// TODO: make this run async. (When I try to, it doesn't get the fresh result.)
		// I need to make it wait for the task.
		if (mysql.isClosed()) {
			mysql.connect();
		}
		// Retrieve database time
		timeThread = new Thread(new TimeRunnable(this, mysql, name, table));
		timeThread.start();

		// Wait for thread to finish
		waitForThread(timeThread);

		return databaseTime;
	}

	/**
	 * Sets the time of a player
	 * 
	 * @param playerName Player to set the time of
	 * @param time Time to change to
	 */
	public void setGlobalTime(final String playerName, final int time) {
		// Check if connection is still alive
		if (mysql.isClosed()) {
			mysql.connect();
		}

		final String statement = "INSERT INTO " + table + " VALUES ('"
				+ playerName + "', " + time + ", CURRENT_TIMESTAMP) "
				+ "ON DUPLICATE KEY UPDATE " + "time=" + time;

		// Run async to prevent load issues.
		plugin.getServer().getScheduler()
				.runTaskAsynchronously(plugin, new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						mysql.execute(statement);
					}
				});
	}

	public boolean isMySQLEnabled() {
		return mysql != null;
	}

	public String getDatabaseName() {
		return database;
	}
}
