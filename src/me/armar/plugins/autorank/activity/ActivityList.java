package me.armar.plugins.autorank.activity;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import me.armar.plugins.autorank.activity.ActivityTracker.ActionType;

/**
 * An ActivityList represents a list of actions that were performed by a player. This is simply a map where each key is a Date object
 * and the value a type of action ({@code ActionType}).
 * 
 * <p>
 * You initialise an ActivityList with an empty constructor and can use {@code #addActivity(Date, ActionType)} to add actions at a specific date.
 * @author Staartvin
 */
public class ActivityList {

    private Map<Date, ActionType> activities = new TreeMap<>();
   
    public void addActivity(Date date, ActionType actionType) {
        activities.put(date, actionType);
    }
    
    public Map<Date, ActionType> getActivities() {
        return this.activities;
    }
}
