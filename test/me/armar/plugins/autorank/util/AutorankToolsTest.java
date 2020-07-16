package me.armar.plugins.autorank.util;


import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Test for {@link AutorankTools}.
 */
public class AutorankToolsTest {

    private static final int SECONDS_IN_MINUTE = 60;
    private static final int MINUTES_IN_HOUR = 60;
    private static final int HOURS_IN_DAY = 24;

    private static final int SECONDS_IN_HOUR = SECONDS_IN_MINUTE * MINUTES_IN_HOUR;
    private static final int SECONDS_IN_DAY = SECONDS_IN_HOUR * 24;

    private static final int MINUTES_IN_DAY = MINUTES_IN_HOUR * 24;

    @Test
    public void shouldHandleInvalidTimeString() {
        // given / when / then
        assertThat(AutorankTools.stringToTime("1t7x", TimeUnit.MINUTES), equalTo(-1));
        assertThat(AutorankTools.stringToTime("", TimeUnit.SECONDS), equalTo(-1));
        assertThat(AutorankTools.stringToTime("hello world", TimeUnit.HOURS), equalTo(-1));
    }

    @Test
    public void shouldHandleNumberAsMinutes() {
        // given
        String time = "200";
        TimeUnit timeType = TimeUnit.HOURS;

        // when
        int result = AutorankTools.stringToTime(time, timeType);

        // then
        assertThat(result, equalTo(3)); // 180 minutes
    }

    @Test
    public void checkValidConversionMinutes() {
        checkConversion("2d", TimeUnit.MINUTES, 2 * MINUTES_IN_DAY);
        checkConversion("4d", TimeUnit.MINUTES, 4 * MINUTES_IN_DAY);
        checkConversion("100d", TimeUnit.MINUTES, 100 * MINUTES_IN_DAY);

        checkConversion("2d4h", TimeUnit.MINUTES, 2 * MINUTES_IN_DAY + 4 * MINUTES_IN_HOUR);
        checkConversion("2d25h", TimeUnit.MINUTES, 2 * MINUTES_IN_DAY + 25 * MINUTES_IN_HOUR);
        checkConversion("56d6h", TimeUnit.MINUTES, 56 * MINUTES_IN_DAY + 6 * MINUTES_IN_HOUR);

        checkConversion("152d2h10m", TimeUnit.MINUTES, 152 * MINUTES_IN_DAY + 2 * MINUTES_IN_HOUR + 10);
        checkConversion("0d2h10m", TimeUnit.MINUTES, 0 * MINUTES_IN_DAY + 2 * MINUTES_IN_HOUR + 10);
        checkConversion("2d26h80m", TimeUnit.MINUTES, 2 * MINUTES_IN_DAY + 26 * MINUTES_IN_HOUR + 80);
    }

    @Test
    public void checkValidConversionHours() {
        checkConversion("2d", TimeUnit.HOURS, 2 * HOURS_IN_DAY);
        checkConversion("4d", TimeUnit.HOURS, 4 * HOURS_IN_DAY);
        checkConversion("100d", TimeUnit.HOURS, 100 * HOURS_IN_DAY);

        checkConversion("2d4h", TimeUnit.HOURS, 2 * HOURS_IN_DAY + 4);
        checkConversion("2d25h", TimeUnit.HOURS, 2 * HOURS_IN_DAY + 25);
        checkConversion("56d6h", TimeUnit.HOURS, 56 * HOURS_IN_DAY + 6);

        checkConversion("152d2h10m", TimeUnit.HOURS, 152 * HOURS_IN_DAY + 2);
        checkConversion("0d2h10m", TimeUnit.HOURS, 0 * HOURS_IN_DAY + 2);
        checkConversion("2d26h80m", TimeUnit.HOURS, 2 * HOURS_IN_DAY + 26 + 1);
    }

    @Test
    public void checkValidConversionDays() {
        checkConversion("2d", TimeUnit.DAYS, 2);
        checkConversion("4d", TimeUnit.DAYS, 4);
        checkConversion("100d", TimeUnit.DAYS, 100);

        checkConversion("2d4h", TimeUnit.DAYS, 2);
        checkConversion("2d25h", TimeUnit.DAYS, 2 + 1);
        checkConversion("56d6h", TimeUnit.DAYS, 56);

        checkConversion("152d2h10m", TimeUnit.DAYS, 152);
        checkConversion("0d2h10m", TimeUnit.DAYS, 0);
        checkConversion("2d26h80m", TimeUnit.DAYS, 2 + 1);
    }

    private void checkConversion(String input, TimeUnit outputType, int expectedValue) {

        int result = AutorankTools.stringToTime(input, outputType);

        assertEquals("Incorrect conversion, should be " + expectedValue + " was " + result, expectedValue, result);
    }
}