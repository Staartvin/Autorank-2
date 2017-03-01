package me.armar.plugins.autorank.activity;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * A History object is a record used for representing the history.
 * Each history object is made up of a {@code value} variable and a {@code unit} variable.
 * The value variable is used to determine how far you want to go back (quantitative) and
 * the unit variable is used to determine what time unit you want to use (qualitative).
 * 
 * <br><br>
 * An example: if you want to go 20 hours back, you can that by creating a new History object like so:
 * 
 * {@code History h = new History(20, TimeUnit.HOURS);}
 * 
 * <br><br>
 * <b>Note that you can only use TimeUnit.MINUTES, HOURS and DAYS as per restriction of Autorank.</b>
 * <p>
 * You need to initialise a History object by providing both a value and time unit. Since
 * a History object is meant to represent a specific point in history, these variables are immutable. 
 * 
 * <p>
 * Finally, there is a {@link #getDate()} method that can be used to obtain a Date object that represents the specific point
 * in time of this History object. If it were to be <i>28 Feb 2017</i> (specific time is irrelevant for now) and you requested a History object
 * with {@code new History(10, TimeUnit.DAYS)}, {@link #getDate()} will return a Date object representing <i>18 Feb 2017</i>. 
 * 
 * @author Staartvin
 *
 */
public class History {

    private int value = 0;
    private TimeUnit timeUnit;
    
    private Date savedDate;
    
    public History(int value, TimeUnit unit) throws IllegalArgumentException {
        
        if (unit == TimeUnit.HOURS && unit == TimeUnit.MINUTES && unit == TimeUnit.DAYS) {
            throw new IllegalArgumentException("Can only use hours, minutes or days");
        }
        
        this.setValue(value);
        this.setTimeUnit(unit);
        
        this.getDate(true);
    }

    public int getValue() {
        return value;
    }

    private void setValue(int value) {
        this.value = value;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    private void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }
    
    /**
     * Get a Date object representing this point in History. 
     * You can choose to cache the Date object; a cached value will always return the same Date (namely the one that refers
     * to the specific point in history of when this History object was created). When you do not want a cached value, it will
     * return the Date object representing this History object but called as if it were created at the moment of calling this method.
     * Hence, calling it twice in a row, both times without cache, would result in two different Date objects being returned.
     *  
     * @param cached Whether you want a Date object to be cached.
     * @return the Date object representing a specific point in History.
     */
    public Date getDate(boolean cached) {
        
        if (!cached) {
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
          
            if (timeUnit == TimeUnit.HOURS) {
                c.add(Calendar.HOUR, -value);
            } else if (timeUnit == TimeUnit.MINUTES) {
                c.add(Calendar.MINUTE, -value);
            } else if (timeUnit == TimeUnit.DAYS) {
                c.add(Calendar.DAY_OF_MONTH, -value);
            }
            
            return c.getTime();
        }
        
        if (savedDate != null) {
            return savedDate;
        }
        
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
      
        if (timeUnit == TimeUnit.HOURS) {
            c.add(Calendar.HOUR, -value);
        } else if (timeUnit == TimeUnit.MINUTES) {
            c.add(Calendar.MINUTE, -value);
        } else if (timeUnit == TimeUnit.DAYS) {
            c.add(Calendar.DAY_OF_MONTH, -value);
        }
        
        savedDate = c.getTime();
        
        return savedDate;
    }
}
