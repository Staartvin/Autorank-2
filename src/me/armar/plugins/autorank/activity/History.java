package me.armar.plugins.autorank.activity;

import java.util.concurrent.TimeUnit;

public class History {

    private int value = 0;
    private TimeUnit timeUnit;
    
    public History(int value, TimeUnit unit) {
        this.setValue(value);
        this.setTimeUnit(unit);
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }
}
