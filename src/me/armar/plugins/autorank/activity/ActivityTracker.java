package me.armar.plugins.autorank.activity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.configuration.ConfigurationSection;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.config.SimpleYamlConfiguration;

public class ActivityTracker {

    private Autorank plugin;

    public ActivityTracker(Autorank instance) {
        this.plugin = instance;
    }

    private SimpleYamlConfiguration workingFile;

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
        workingFile = new SimpleYamlConfiguration(plugin, getCurrentFileString(), "Activity Tracker");

        workingFile.saveFile();

        this.getActivity(UUID.fromString("c5f39a1d-3786-46a7-8953-d4efabf8880d"));
    }

    /**
     * Get the path to the current working file of today.
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

        workingFile.set(uuid.toString() + "." + date, action.toString());
        workingFile.saveFile();
    }

    public Map<String, ActionType> getActivities(UUID uuid) {
        ConfigurationSection uuidSection = workingFile.getConfigurationSection(uuid.toString());

        Map<String, ActionType> actions = new HashMap<>();

        for (String key : uuidSection.getKeys(false)) {
            actions.put(key, ActionType.valueOf(uuidSection.getString(key)));
        }

        return actions;
    }

    public long getActivity(UUID uuid) {
        Map<String, ActionType> activityTemp = this.getActivities(uuid);
        
        Map<Date, ActionType> activity = new TreeMap<Date, ActionType>();
        
        for (String key : activityTemp.keySet()) {
            activity.put(this.getDateFromString(key), activityTemp.get(key));
        }

        long total = this.getTimePlayed(activity);
        
        System.out.println("TOTAL: " + total);

        return total;
    }

    private Date getDateFromString(String dateString) {
        try {
            return timeFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private long calculateDifference(Date d1, Date d2, TimeUnit unit) {
        if (d1 == null || d2 == null)
            return 0;
        long diffInMillies = d2.getTime() - d1.getTime();
        return unit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

    public long getTimePlayed(Map<Date, ActionType> data) {
        long total = 0;

        int index = -1;

        ActionType previousType = null;
        Date previousDate = null;
        
        Date previousAFKDate = null;
        
        boolean isAFK = false;

        // Assume the data map is sorted.
        for (Entry<Date, ActionType> entry : data.entrySet()) {

            System.out.println("------------------");
            
            index++;

            Date date = entry.getKey();
            ActionType type = entry.getValue();

            if (date == null)
                continue;
            
            System.out.println("INDEX: " + index);
            System.out.println("DATE: " + date);
            System.out.println("TYPE: " + type);

            if (index == 0) {
                // Only care about logged in
                if (type != ActionType.LOGGED_IN) {
                    continue;
                }

                previousDate = date;
                previousType = type;
                isAFK = false;

                continue;
            } /*else if (index == (data.size() - 1)) { // Last index
                
            } */else {
                if (type == ActionType.LOGGED_OUT) {

                    if (previousType == ActionType.LOGGED_IN) {

                        long timeDiff = this.calculateDifference(previousDate, date, TimeUnit.SECONDS);
                        System.out.println("I ADD HERE: " + timeDiff);

                        total += timeDiff;

                        previousType = type;
                        previousDate = null;
                        isAFK = false;

                        continue;
                    }

                } else if (type == ActionType.LOGGED_IN) {

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
                        
                        long timeDiff = this.calculateDifference(previousAFKDate, date, TimeUnit.SECONDS);
                        
                        System.out.println("I REMOVE (DUE TO AFK): " + timeDiff);
                        
                        total -= timeDiff;
                        
                        continue;
                    }
                    
                }

            }
            
            System.out.println("IGNORING IT");
        }

        return total;
    }
}
