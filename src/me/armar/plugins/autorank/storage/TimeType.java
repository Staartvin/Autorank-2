package me.armar.plugins.autorank.storage;

/**
 * Represents a type of time that Autorank stores.
 * <br>
 * <br>
 * Autorank stores play time of players per day, week, month and total time.
 * Every day, week or month these files are reset. The total time data is, trivially, never reset.
 */
public enum TimeType {
    DAILY_TIME, WEEKLY_TIME, MONTHLY_TIME, TOTAL_TIME
}
