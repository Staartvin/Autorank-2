package me.armar.plugins.autorank.testcases;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import me.armar.plugins.autorank.activity.History;

public class HistoryTest {

    public Date getTimeBack(int value, TimeUnit unit) {
        
        int type = Calendar.HOUR;
        
        if (unit == TimeUnit.HOURS) {
            type = Calendar.HOUR;
        } else if (unit == TimeUnit.DAYS) {
            type = Calendar.DAY_OF_MONTH;
        } else if (unit == TimeUnit.MINUTES) {
            type = Calendar.MINUTE;
        }
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(type, -value);
        
        return cal.getTime();
    }
    
    @Test
    public void test() {
        System.out.println("1 hours back");
        History h = new History(1, TimeUnit.HOURS);
        
        assertEquals("1 hour back", this.getTimeBack(1, TimeUnit.HOURS), h.getDate(true));
    }
    
    @Test
    public void test2() {
        System.out.println("6 hours back");
        History h = new History(6, TimeUnit.HOURS);
        
        assertEquals("6 hours back", this.getTimeBack(6, TimeUnit.HOURS), h.getDate(true));
    }
    
    @Test
    public void test3() {
        System.out.println("25 hours back");
        History h = new History(25, TimeUnit.HOURS);
        
        assertEquals("25 hours back", this.getTimeBack(25, TimeUnit.HOURS), h.getDate(true));
    }
    
    @Test
    public void test4() {
        System.out.println("1 day back");
        History h = new History(1, TimeUnit.DAYS);
        
        assertEquals("1 day back", this.getTimeBack(1, TimeUnit.DAYS), h.getDate(true));
    }
    
    @Test
    public void test5() {
        System.out.println("1 year back");
        History h = new History(365, TimeUnit.DAYS);
        
        assertEquals("1 year back", this.getTimeBack(365, TimeUnit.DAYS), h.getDate(true));
    }

}
