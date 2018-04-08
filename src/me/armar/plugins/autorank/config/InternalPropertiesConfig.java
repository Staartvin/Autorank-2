package me.armar.plugins.autorank.config;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.storage.TimeType;

import java.util.ArrayList;
import java.util.List;

/**
 * This class manages the internalprops.yml file. It is used to store storage that
 * is not specific to players or admins, <br>
 * but is used internally (hence the name) by Autorank. Think of the cached
 * leaderboard, the last time something was updated, etc. <br>
 * This file should not be altered by the server owner, since Autorank manages
 * it.
 *
 * @author Staartvin
 */
public class InternalPropertiesConfig extends AbstractConfig {

    private String fileName = "internalprops.yml";

    public InternalPropertiesConfig(final Autorank instance) {
        setPlugin(instance);
        setFileName(fileName);
    }

    /**
     * Get the cached leaderboard for a certain time {@linkplain TimeType}.
     *
     * @param type Type of time
     * @return the cached leaderboard for the given time type.
     */
    public List<String> getCachedLeaderboard(final TimeType type) {
        // Type is the leaderboard type you want to get (all time, daily, weekly
        // or monthly)
        return this.getConfig().getStringList("leaderboards." + type.toString().toLowerCase() + ".cached leaderboard");
    }

    /**
     * Get the last time any of the leaderboard was updated (UNIX timestamp).
     *
     * @return a UNIX timestamp or 0 if never updated before.
     */
    public long getLeaderboardLastUpdateTime(final TimeType type) {
        return this.getConfig().getLong("leaderboards." + type.toString().toLowerCase() + ".last updated", 0);
    }

    /**
     * Get the last stored value of a time type. Autorank stores the current
     * day, week and month. On a new calendar day, any of will be changed (as a
     * new day has arrived). Autorank then knows a new day/week/month has
     * arrived, so it can reset the storage files.
     *
     * @param type type of time
     * @return the previously stored value. Returns 1 if nothing was stored yet
     * or 0 if you try to access the total time.
     */
    public int getTrackedTimeType(final TimeType type) {
        if (type == TimeType.DAILY_TIME) {
            return this.getConfig().getInt("tracked day", 1);
        } else if (type == TimeType.WEEKLY_TIME) {
            return this.getConfig().getInt("tracked week", 1);
        } else if (type == TimeType.MONTHLY_TIME) {
            return this.getConfig().getInt("tracked month", 1);
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
        return this.getConfig().getBoolean("has converted uuids", false);
    }

    /**
     * Set whether the UUIDS have been correctly converted to a new format.
     *
     * @param value Value of the conversion
     */
    public void hasTransferredUUIDs(final boolean value) {
        this.getConfig().set("has converted uuids", value);

        this.saveConfig();
    }

    /**
     * Load the internalprops.yml file.
     */
    @Override
    public void loadConfig() {

        super.loadConfig();

        this.getConfig().options()
                .header("This is the internal properties file of Autorank. \nYou should not touch any values here, unless instructed by a developer."
                        + "\nAutorank uses these to keep track of certain aspects of the plugin.");

        this.getConfig().addDefault("leaderboard last updated", 0); // When was the
        // leaderboard
        // updated for last
        // time? In UNIX
        // time.
        this.getConfig().addDefault("has converted uuids", false); // Did it already
        // convert uuids?

        this.getConfig().addDefault("tracked month", 1); // This is used to keep track of
        // what month we are checking the
        // storage for. If this is changed,
        // the montly_data.yml gets
        // reset.
        this.getConfig().addDefault("tracked week", 1); // This is used to keep track of
        // what week we are checking the
        // storage for. If this is changed,
        // the weekly_data.yml gets reset.
        this.getConfig().addDefault("tracked day", 1); // This is used to keep track of
        // what day we are checking the
        // storage for. If this is changed,
        // the daily_data.yml gets reset.

        final List<String> newList = new ArrayList<String>();
        newList.add("&cThis leaderboard wasn't set up yet.");

        this.getConfig().addDefault("leaderboards.total_time.cached leaderboard", newList);
        this.getConfig().addDefault("leaderboards.daily_time.cached leaderboard", newList);
        this.getConfig().addDefault("leaderboards.weekly_time.cached leaderboard", newList);
        this.getConfig().addDefault("leaderboards.monthly_time.cached leaderboard", newList);

        this.getConfig().options().copyDefaults(true);

        this.saveConfig();
    }

    /**
     * Set the cached leaderboard for a certain time type.
     *
     * @param type              Type of time
     * @param cachedLeaderboard A list of strings
     */
    public void setCachedLeaderboard(final TimeType type, final List<String> cachedLeaderboard) {
        this.getConfig().set("leaderboards." + type.toString().toLowerCase() + ".cached leaderboard",
                cachedLeaderboard);

        this.saveConfig();
    }

    /**
     * Set the time any leaderboard was last updated.
     *
     * @param time Last update time (UNIX timestamp)
     */
    public void setLeaderboardLastUpdateTime(final TimeType type, final long time) {
        this.getConfig().set("leaderboards." + type.toString().toLowerCase() + ".last updated", time);

        this.saveConfig();
    }

    /**
     * Set the value of a time type. See {@link #getTrackedTimeType(TimeType)}
     * for more info.
     *
     * @param type  Type of time
     * @param value Value to set the time to.
     */
    public void setTrackedTimeType(final TimeType type, final int value) {
        if (type == TimeType.DAILY_TIME) {
            this.getConfig().set("tracked day", value);
        } else if (type == TimeType.WEEKLY_TIME) {
            this.getConfig().set("tracked week", value);
        } else if (type == TimeType.MONTHLY_TIME) {
            this.getConfig().set("tracked month", value);
        } else {
            return;
        }

        this.saveConfig();
    }

    /**
     * Check whether Autorank is using a 4.0+ format.
     *
     * @return true if it is, false otherwise.
     */
    public boolean isConvertedToNewFormat() {
        return this.getConfig().getBoolean("is converted to new format", false);
    }

    /**
     * Set whether Autorank is using the 4.0+ format.
     *
     * @param value either true or false
     */
    public void setConvertedToNewFormat(boolean value) {
        this.getConfig().set("is converted to new format", value);

        this.saveConfig();
    }
}
