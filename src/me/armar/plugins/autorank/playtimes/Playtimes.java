package me.armar.plugins.autorank.playtimes;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.data.SQLDataStorage;
import me.armar.plugins.autorank.data.SimpleYamlConfiguration;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitScheduler;

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
	private boolean saveToDatabase, readFromDatabase = false;

	public Playtimes(Autorank plugin) {
		this.plugin = plugin;
		this.scheduler = plugin.getServer().getScheduler();
		this.data = new SimpleYamlConfiguration(plugin, "Data.yml", null,
				"Data");
		this.save = new PlaytimesSave(this);
		this.update = new PlaytimesUpdate(this, plugin);

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
			
			// Get method
			String method = s.getString("method", "save");
			
			if (method.equalsIgnoreCase("save")) {
				saveToDatabase = true;
				plugin.getLogger().info("Only saving data to database!");
			} else if (method.equalsIgnoreCase("read")) {
				readFromDatabase = true;
				plugin.getLogger().info("Only reading data from database!");
			}

			this.sql = new SQLDataStorage(hostname, username, password,
					database);
			if (!sql.connect()) {
				this.sql = null;
				plugin.getLogger().severe("Could not connect to "  + hostname);
			} else {
				plugin.getLogger().info("Successfully established connection to " + hostname);
			}
				
		}

	}

	private void setupTable() {
		String statement = "CREATE TABLE  IF NOT EXISTS " + table + " "
				+ "(name VARCHAR(16) not NULL, " + " time INTEGER not NULL, "
				+ " modified TIMESTAMP not NULL, " + " PRIMARY KEY ( name ))";
		sql.execute(statement);
	}

	public int getTime(String name) {
		// This is done on purpose, for future work
		int time = data.getInt(name.toLowerCase());
		
		if (readFromDatabase) {
			this.syncingName = name;
			String statement = "SELECT * FROM " + table + " WHERE name='"
					+ syncingName + "'";
			time = getDBTime(sql.executeQuery(statement));
		}
		return time;
	}

	public void importData() {
		data.reload();
	}

	public void setTime(String name, int time) {
		if (saveToDatabase) {
			if (sql != null) {
				this.syncingName = name.toLowerCase();
				this.syncingTime = time;
				this.scheduler.runTaskAsynchronously(plugin, this);
			}
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
	
	
	/** 
	 * Archive old records. Records below the minimum will be removed because they are 'inactive'.
	 * @param minimum Lowest threshold to check for
	 * @return Amount of records removed
	 */
	public int archive(int minimum) {
		Object[] objectArray = getKeys().toArray();
		List<String> records = new ArrayList<String>();
		
		// Convert ObjectArray to List of Strings
		for (Object object:objectArray) {
			String record = (String) object;
			
			records.add(record);
		}
		// Keep a counter of archived items
		int counter = 0;
		
		for (String record: records) {
			int time = getTime(record);
			
			// Found a record to be archived
			if (time < minimum) {
				counter++;
				
				// Remove record
				data.set(record, null);
			}
		}
		
		save();
		return counter;
	}

	@Override
	public void run() {
		String statement = "SELECT * FROM " + table + " WHERE name='"
				+ syncingName + "'";
		ResultSet rs = sql.executeQuery(statement);
		if (rs != null) {
			try {
				if (rs.isClosed() || !rs.first())  {
					setDBTime();
					return;
				}
				
				if (rs.getDate(3) == null
						|| rs.getDate(3).before(
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

		if (rs == null) {
			return 0;
		}
		
		try {
			if (rs.next()) {
				result = rs.getInt(2);
			} else {
				result = null;
			}
			
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
