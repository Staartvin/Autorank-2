package me.armar.plugins.autorank.playtimes;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitScheduler;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.data.SQLDataStorage;
import me.armar.plugins.autorank.data.SimpleYamlConfiguration;

public class Playtimes implements Runnable {

	public static int INTERVAL_MINUTES = 5;

	private SimpleYamlConfiguration data;
	private SQLDataStorage sql;
	private PlaytimesSave save;
	private PlaytimesUpdate update;
	private String table;
	private Autorank plugin;
	private BukkitScheduler scheduler;
	private String syncingName;
	private int syncingTime;

	public Playtimes(Autorank plugin) {
		this.plugin = plugin;
		this.scheduler = plugin.getServer().getScheduler();
		this.data = new SimpleYamlConfiguration(plugin, "Data.yml", null,
				"Data");
		this.save = new PlaytimesSave(this);
		this.update = new PlaytimesUpdate(this);

		sqlSetup(plugin.getAdvancedConfig());
		if (sql != null) {
			setupTable();
		}

		plugin.getServer().getScheduler()
				.runTaskTimer(plugin, save, 12000, 12000);
		plugin.getServer().getScheduler().runTaskTimer(plugin, save, 600, 600);
		plugin.getServer()
				.getScheduler()
				.runTaskTimer(plugin, update, INTERVAL_MINUTES * 20 * 60,
						INTERVAL_MINUTES * 20 * 60);
		//plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, update, INTERVAL_MINUTES * 20 * 4, INTERVAL_MINUTES * 20 * 4);

	}

	private void sqlSetup(SimpleYamlConfiguration config) {
		ConfigurationSection s = config.getConfigurationSection("sql");

		Boolean enabled = s.getBoolean("enabled");
		if (enabled != null && enabled) {

			String hostname = s.getString("hostname");
			String username = s.getString("username");
			String password = s.getString("password");
			String database = s.getString("database");
			this.table = s.getString("table");

			this.sql = new SQLDataStorage(hostname, username, password,
					database);
			if (!sql.connect())
				this.sql = null;
			System.out.println(sql);

		}

	}

	private void setupTable() {
		String statement = "CREATE TABLE  IF NOT EXISTS " + table + " "
				+ "(name VARCHAR(16) not NULL, " + " time INTEGER not NULL, "
				+ " modified TIMESTAMP not NULL, " + " PRIMARY KEY ( name ))";
		sql.execute(statement);
	}

	public int getTime(String name) {
		return data.getInt(name.toLowerCase());
	}

	public void setTime(String name, int time) {
		if (sql != null) {
			this.syncingName = name;
			this.syncingTime = time;
			this.scheduler.runTaskAsynchronously(plugin, this);
		}
		data.set(name.toLowerCase(), time);
	}

	public void modifyTime(String name, int timeDifference)
			throws IllegalArgumentException {
		Object time = data.get(name);
		if (time != null) {
			setTime(name, (Integer) time + timeDifference);
		} else {
			throw new IllegalArgumentException("No data stored for player");
		}
	}

	public Set<String> getKeys() {
		return data.getKeys(false);
	}

	public void save() {
		data.save();
	}

	@Override
	public void run() {
		String statement = "SELECT * FROM " + table + " WHERE name='"
				+ syncingName + "'";
		ResultSet rs = sql.executeQuery(statement);
		if (rs != null) {

			try {
				if (rs.getDate("modified") == null
						|| rs.getDate("modified").before(
								Calendar.getInstance().getTime())) {
					//database is out of date
					setDBTime();
				} else {
					//database is more recent
					data.set(syncingName, getDBTime(rs));
				}
			} catch (SQLException e) {
				System.out.println("Playtimes.run");
				System.out.println("SQLException: " + e.getMessage());
				System.out.println("SQLState: " + e.getSQLState());
				System.out.println("VendorError: " + e.getErrorCode());
			}
		} else {
			//non existing entry
			setDBTime();
		}

	}

	private int getDBTime(ResultSet rs) {
		Integer result = null;

		try {
			result = rs.getInt(2);
		} catch (SQLException e) {
			System.out.println("Playtimes.getDBTime");
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
		}

		if (result == null)
			result = 0;
		return result;
	}

	private void setDBTime() {
		String statement = "INSERT INTO " + table + " VALUES ('" + syncingName
				+ "', " + syncingTime + ", CURRENT_TIMESTAMP) "
				+ "ON DUPLICATE KEY UPDATE " + "time=" + syncingTime;
		sql.execute(statement);
	}

}
