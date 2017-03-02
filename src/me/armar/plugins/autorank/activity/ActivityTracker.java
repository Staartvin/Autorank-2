package me.armar.plugins.autorank.activity;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.configuration.ConfigurationSection;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.config.SimpleYamlConfiguration;
import me.armar.plugins.autorank.util.AutorankTools;

public class ActivityTracker {

    private Autorank plugin;

    public ActivityTracker(Autorank instance) {
        this.plugin = instance;
    }

    private SimpleYamlConfiguration workingFile;
    private String lastUsedFile;

    /**
     * Actions that we track of a player.
     * 
     * @author Staartvin
     *
     */
    public static enum ActionType {
        LOGGED_IN, LOGGED_OUT, WENT_AFK, RETURNED_FROM_AFK
    };

    private final DateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

    /**
     * Load the working file
     */
    public void loadWorkingFile() {

        workingFile = getWorkingFile();

        this.getWorkingFile().saveFile();

        plugin.getServer().getScheduler().runTaskTimer(plugin, new SaveActivityFile(workingFile), 0,
                AutorankTools.TICKS_PER_MINUTE * 1);
    }

    /**
     * Get the file that is currently used for storing activities of players.
     * This file depends on the time of day, as it stores activities on a
     * per-day basis. It could mean that at one moment the file contains some
     * data, while at the next it doesn't (when a new day started).
     * 
     * @return the file used to store activities of players.
     */
    public SimpleYamlConfiguration getWorkingFile() {
        if (lastUsedFile == null || !lastUsedFile.equals(this.getCurrentFileString())) {
            lastUsedFile = this.getCurrentFileString();

            // Attempt to save the old working file before overwriting it.
            if (workingFile != null) {
                workingFile.saveFile();
            }

            // Overwrite it with a new file.
            workingFile = new SimpleYamlConfiguration(plugin, getCurrentFileString(), null);
        }

        return workingFile;
    }

    /**
     * Get the path to the current working file for this moment.
     * 
     * @return path to the working file.
     */
    private String getCurrentFileString() {
        return this.getFileString(Calendar.getInstance().getTime());
    }

    /**
     * Get the name of the file that is used to store the activity of a
     * particular day. The Date object corresponds to that given day.
     * 
     * @param date
     *            Date object representing a day.
     * @return path of the file.
     */
    private String getFileString(Date date) {

        String dateString = dayFormat.format(date);

        return "/activity/activity-on-" + dateString + ".yml";
    }

    /**
     * Get the current timestamp in HH:mm:ss.
     * 
     * @return current formatted timestamp
     */
    private String getCurrentTime() {
        return timeFormat.format(Calendar.getInstance().getTime());
    }

    /**
     * Record the action of a player.
     * 
     * @param uuid
     *            UUID of the player.
     * @param action
     *            Type of action.
     */
    public void addAction(UUID uuid, ActionType action) {
        String date = this.getCurrentTime();

        this.getWorkingFile().set(uuid.toString() + "." + date, action.toString());
    }

    /**
     * Get all actions that have happened for the given UUID within the given
     * history.
     * 
     * @param uuid
     *            UUID of the player
     * @param history
     *            History to find activities in.
     * @return a list of activities in a {@code ActivityList}.
     */
    private ActivityList getActivities(UUID uuid, History history) {

        List<String> fileNames = getRelevantFiles(history);

        ActivityList activities = new ActivityList();

        for (String fileName : fileNames) {
            SimpleYamlConfiguration file = new SimpleYamlConfiguration(plugin, "/activity/" + fileName, null);

            ConfigurationSection uuidSection = file.getConfigurationSection(uuid.toString());

            if (uuidSection == null) {
                continue;
            }

            Calendar oldDate = Calendar.getInstance();

            oldDate.setTime(AutorankTools.getDateFromString(fileName.replace("activity-on-", "").replace(".yml", ""),
                    dayFormat));

            for (String key : uuidSection.getKeys(false)) {

                Calendar cal = Calendar.getInstance();

                cal.setTime(AutorankTools.getDateFromString(key, timeFormat));

                cal.set(Calendar.YEAR, oldDate.get(Calendar.YEAR));
                cal.set(Calendar.MONTH, oldDate.get(Calendar.MONTH));
                cal.set(Calendar.DAY_OF_MONTH, oldDate.get(Calendar.DAY_OF_MONTH));

                // Don't count data points that are before the start of the
                // history search
                if (cal.getTime().before(history.getDate(true)) || cal.getTime().after(new Date())) {
                    continue;
                }

                activities.addActivity(cal.getTime(), ActionType.valueOf(uuidSection.getString(key)));
            }

        }

        return activities;
    }

    /**
     * Get all file names that should be used for getting data for the given
     * history. <br>
     * The names of the files are not paths, just the name of the file
     * (including the .yml extension).
     * 
     * @param history
     *            History the history object
     * @return a list of strings representing the names of the files.
     */
    private List<String> getRelevantFiles(History history) {

        List<String> files = new ArrayList<>();

        if (history == null) {
            return files;
        }

        Date historyDate = history.getDate(true);

        File activityFolder = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "/activity");

        File[] activityFiles = activityFolder.listFiles();

        for (File activityFile : activityFiles) {

            if (activityFile.isFile()) {

                String fileName = activityFile.getName();

                fileName = fileName.replace("activity-on-", "");

                Date date = AutorankTools.getDateFromString(fileName, dayFormat);

                if (date.after(historyDate) || AutorankTools.isSameDay(historyDate, date)) {
                    files.add(activityFile.getName());
                }
            }
        }

        return files;
    }

    /**
     * Get the total play time of a player (in seconds) over a specific history.
     * 
     * @param uuid
     *            UUID of the player
     * @param history
     *            {@link History} object to represent specific point in history
     * @return number of seconds played in the given history.
     */
    public long getActivityInHistory(UUID uuid, History history) {

        if (uuid == null || history == null) {
            return 0;
        }

        ActivityList activities = this.getActivities(uuid, history);

        long total = this.getTimePlayed(activities, history);

        return total;
    }

    /**
     * Get the time a player has played in the given data set. The history
     * object is used to calculate play time when the data set is not complete.
     * <br>
     * This asssumes that the data is all for one player.
     * 
     * @param data
     *            Data set to use.
     * @param history
     *            History to use when the data set is incomplete.
     * @return the number of seconds the player has played in this data set.
     */
    private long getTimePlayed(ActivityList data, History history) {
        long total = 0;

        int index = -1;

        ActionType previousType = null;
        Date previousDate = null;

        Date previousAFKDate = null;

        boolean isAFK = false;

        // Assume the data map is sorted.
        for (Entry<Date, ActionType> entry : data.getActivities().entrySet()) {

            index++;

            Date date = entry.getKey();
            ActionType type = entry.getValue();

            if (date == null)
                continue;

            if (index == 0 && index != data.getActivities().size() - 1) {
                // Only care about logged in

                if (type == ActionType.LOGGED_OUT) {
                    long timeDiff = AutorankTools.calculateDifference(history.getDate(true), date, TimeUnit.SECONDS);

                    total += timeDiff;

                    continue;
                } else if (type != ActionType.LOGGED_IN) {
                    continue;
                }

                previousDate = date;
                previousType = type;
                isAFK = false;

                continue;
            }

            if (type == ActionType.LOGGED_OUT) {

                if (previousType == ActionType.LOGGED_IN) {

                    long timeDiff = AutorankTools.calculateDifference(previousDate, date, TimeUnit.SECONDS);

                    total += timeDiff;

                    previousType = type;
                    previousDate = null;
                    isAFK = false;

                    continue;
                }

            } else if (type == ActionType.LOGGED_IN) {

                // If 'logged is' in the last index,
                // add time from then to now.
                if (index == data.getActivities().size() - 1) {
                    
                    long timeDiff = AutorankTools.calculateDifference(date, new Date(), TimeUnit.SECONDS);

                    total += timeDiff;

                    continue;
                }
                
                if (previousType == ActionType.LOGGED_OUT) {

                    previousType = type;
                    previousDate = date;
                    isAFK = false;

                    continue;
                } else if (previousType == null) {
                    // First logged in point that we encounter

                    previousDate = date;
                    previousType = type;
                    isAFK = false;

                    continue;
                }

            } else if (type == ActionType.WENT_AFK) {

                if (previousType == ActionType.LOGGED_IN) {

                    isAFK = true;
                    previousAFKDate = date;

                    continue;
                }

            } else if (type == ActionType.RETURNED_FROM_AFK) {

                if (isAFK) {
                    isAFK = false;

                    long timeDiff = AutorankTools.calculateDifference(previousAFKDate, date, TimeUnit.SECONDS);

                    total -= timeDiff;

                    continue;
                }

            }
        }

        return total;
    }
}
