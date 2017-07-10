package me.armar.plugins.autorank.config;

import java.util.ArrayList;
import java.util.List;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.data.flatfile.FlatFileManager.TimeType;

/**
 * This class manages the internalprops.yml file. It is used to store data that
 * is not specific to players or admins, <br>
 * but is used internally (hence the name) by Autorank. Think of the cached
 * leaderboard, the last time something was updated, etc. <br>
 * This file should not be altered by the server owner, since Autorank manages
 * it.
 * 
 * @author Staartvin
 *
 */
public class InternalPropertiesConfig {

    private final Autorank plugin;
    private SimpleYamlConfiguration config;

    public InternalPropertiesConfig(final Autorank instance) {
        this.plugin = instance;
    }

    /**
     * Get the cached leaderboard for a certain time {@linkplain TimeType}.
     * 
     * @param type
     *            Type of time
     * @return the cached leaderboard for the given time type.
     */
    public List<String> getCachedLeaderboard(final TimeType type) {
        // Type is the leaderboard type you want to get (all time, daily, weekly
        // or monthly)
        return config.getStringList("leaderboards." + type.toString().toLowerCase() + ".cached leaderboard");
    }

    /**
     * Get the last time any of the leaderboard was updated (UNIX timestamp).
     * 
     * @return a UNIX timestamp or 0 if never updated before.
     */
    public long getLeaderboardLastUpdateTime(final TimeType type) {
        return config.getLong("leaderboards." + type.toString().toLowerCase() + ".last updated", 0);
    }

    /**
     * 
     * Get the last stored value of a time type. Autorank stores the current
     * day, week and month. On a new calendar day, any of will be changed (as a
     * new day has arrived). Autorank then knows a new day/week/month has
     * arrived, so it can reset the data files.
     * 
     * @param type
     *            type of time
     * @return the previously stored value. Returns 1 if nothing was stored yet
     *         or 0 if you try to access the total time.
     */
    public int getTrackedTimeType(final TimeType type) {
        if (type == TimeType.DAILY_TIME) {
            return config.getInt("tracked day", 1);
        } else if (type == TimeType.WEEKLY_TIME) {
            return config.getInt("tracked week", 1);
        } else if (type == TimeType.MONTHLY_TIME) {
            return config.getInt("tracked month", 1);
        } else {
            return 0;
        }
    }

    /**
     * Get whether the UUIDS have been converted to a new format. Since Autorank
     * 3.7.1, a new format of storing player names was introduced. If all values
     * were properly converted, this method will return true. If it hasn't been
     * run before or did not successfully convert all names, it will return
     * false.
     * 
     * @return true if the uuids are stored in the new format, false otherwise.
     */
    public boolean hasTransferredUUIDs() {
        return config.getBoolean("has converted uuids", false);
    }

    /**
     * Set whether the UUIDS have been correctly converted to a new format.
     * 
     * @param value
     *            Value of the conversion
     */
    public void hasTransferredUUIDs(final boolean value) {
        config.set("has converted uuids", value);

        config.saveFile();
    }

    /**
     * Load the internalprops.yml file.
     */
    public void loadFile() {
        config = new SimpleYamlConfiguration(plugin, "internalprops.yml", "Internal properties");

        config.options()
                .header("This is the internal properties file of Autorank. \nYou should not touch any values here, unless instructed by a developer."
                        + "\nAutorank uses these to keep track of certain aspects of the plugin.");

        config.addDefault("leaderboard last updated", 0); // When was the
                                                          // leaderboard
                                                          // updated for last
                                                          // time? In UNIX
                                                          // time.
        config.addDefault("has converted uuids", false); // Did it already
                                                         // convert uuids?

        config.addDefault("tracked month", 1); // This is used to keep track of
                                               // what month we are checking the
                                               // data for. If this is changed,
                                               // the montly_data.yml gets
                                               // reset.
        config.addDefault("tracked week", 1); // This is used to keep track of
                                              // what week we are checking the
                                              // data for. If this is changed,
                                              // the weekly_data.yml gets reset.
        config.addDefault("tracked day", 1); // This is used to keep track of
                                             // what day we are checking the
                                             // data for. If this is changed,
                                             // the daily_data.yml gets reset.

        final List<String> newList = new ArrayList<String>();
        newList.add("&cThis leaderboard wasn't set up yet.");

        config.addDefault("leaderboards.total_time.cached leaderboard", newList);
        config.addDefault("leaderboards.daily_time.cached leaderboard", newList);
        config.addDefault("leaderboards.weekly_time.cached leaderboard", newList);
        config.addDefault("leaderboards.monthly_time.cached leaderboard", newList);

        config.options().copyDefaults(true);

        config.saveFile();
    }

    /**
     * Set the cached leaderboard for a certain time type.
     * 
     * @param type
     *            Type of time
     * @param cachedLeaderboard
     *            A list of strings
     */
    public void setCachedLeaderboard(final TimeType type, final List<String> cachedLeaderboard) {
        config.set("leaderboards." + type.toString().toLowerCase() + ".cached leaderboard", cachedLeaderboard);

        config.saveFile();
    }

    /**
     * Set the time any leaderboard was last updated.
     * 
     * @param time
     *            Last update time (UNIX timestamp)
     */
    public void setLeaderboardLastUpdateTime(final TimeType type, final long time) {
        config.set("leaderboards." + type.toString().toLowerCase() + ".last updated", time);

        config.saveFile();
    }

    /**
     * Set the value of a time type. See {@link #getTrackedTimeType(TimeType)}
     * for more info.
     * 
     * @param type
     *            Type of time
     * @param value
     *            Value to set the time to.
     */
    public void setTrackedTimeType(final TimeType type, final int value) {
        if (type == TimeType.DAILY_TIME) {
            config.set("tracked day", value);
        } else if (type == TimeType.WEEKLY_TIME) {
            config.set("tracked week", value);
        } else if (type == TimeType.MONTHLY_TIME) {
            config.set("tracked month", value);
        } else {
            return;
        }

        config.saveFile();
    }
    
    /**
     * Check whether Autorank is using a 4.0+ format.
     * @return true if it is, false otherwise.
     */
    public boolean isConvertedToNewFormat() {
        return config.getBoolean("is converted to new format", false);
    }
    
    /**
     * Set whether Autorank is using the 4.0+ format.
     * @param value either true or false
     */
    public void setConvertedToNewFormat(boolean value) {
        config.set("is converted to new format", value);
        
        config.saveFile();
    }
}
