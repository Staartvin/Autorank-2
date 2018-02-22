package me.armar.plugins.autorank.util;

import me.armar.plugins.autorank.util.AutorankTools.Time;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Test for {@link AutorankTools}.
 */
public class AutorankToolsTest {

    private static int SECONDS_IN_MINUTE = 60;
    private static int MINUTES_IN_HOUR = 60;
    private static int HOURS_IN_DAY = 24;

    private static int SECONDS_IN_HOUR = SECONDS_IN_MINUTE * MINUTES_IN_HOUR;
    private static int SECONDS_IN_DAY = SECONDS_IN_HOUR * 24;

    private static int MINUTES_IN_DAY = MINUTES_IN_HOUR * 24;

    @Test
    public void shouldHandleInvalidTimeString() {
        // given / when / then
        assertThat(AutorankTools.stringToTime("1t7x", Time.MINUTES), equalTo(-1));
        assertThat(AutorankTools.stringToTime("", Time.SECONDS), equalTo(-1));
        assertThat(AutorankTools.stringToTime("hello world", Time.HOURS), equalTo(-1));
    }

    @Test
    public void shouldHandleNumberAsMinutes() {
        // given
        String time = "200";
        Time timeType = Time.HOURS;

        // when
        int result = AutorankTools.stringToTime(time, timeType);

        // then
        assertThat(result, equalTo(3)); // 180 minutes
    }

    @Test
    public void checkValidConversionMinutes() {
        checkConversion("2d", Time.MINUTES, 2 * MINUTES_IN_DAY);
        checkConversion("4d", Time.MINUTES, 4 * MINUTES_IN_DAY);
        checkConversion("100d", Time.MINUTES, 100 * MINUTES_IN_DAY);

        checkConversion("2d4h", Time.MINUTES, 2 * MINUTES_IN_DAY + 4 * MINUTES_IN_HOUR);
        checkConversion("2d25h", Time.MINUTES, 2 * MINUTES_IN_DAY + 25 * MINUTES_IN_HOUR);
        checkConversion("56d6h", Time.MINUTES, 56 * MINUTES_IN_DAY + 6 * MINUTES_IN_HOUR);

        checkConversion("152d2h10m", Time.MINUTES, 152 * MINUTES_IN_DAY + 2 * MINUTES_IN_HOUR + 10);
        checkConversion("0d2h10m", Time.MINUTES, 0 * MINUTES_IN_DAY + 2 * MINUTES_IN_HOUR + 10);
        checkConversion("2d26h80m", Time.MINUTES, 2 * MINUTES_IN_DAY + 26 * MINUTES_IN_HOUR + 80);
    }

    @Test
    public void checkValidConversionHours() {
        checkConversion("2d", Time.HOURS, 2 * HOURS_IN_DAY);
        checkConversion("4d", Time.HOURS, 4 * HOURS_IN_DAY);
        checkConversion("100d", Time.HOURS, 100 * HOURS_IN_DAY);

        checkConversion("2d4h", Time.HOURS, 2 * HOURS_IN_DAY + 4);
        checkConversion("2d25h", Time.HOURS, 2 * HOURS_IN_DAY + 25);
        checkConversion("56d6h", Time.HOURS, 56 * HOURS_IN_DAY + 6);

        checkConversion("152d2h10m", Time.HOURS, 152 * HOURS_IN_DAY + 2);
        checkConversion("0d2h10m", Time.HOURS, 0 * HOURS_IN_DAY + 2);
        checkConversion("2d26h80m", Time.HOURS, 2 * HOURS_IN_DAY + 26 + 1);
    }

    @Test
    public void checkValidConversionDays() {
        checkConversion("2d", Time.DAYS, 2);
        checkConversion("4d", Time.DAYS, 4);
        checkConversion("100d", Time.DAYS, 100);

        checkConversion("2d4h", Time.DAYS, 2);
        checkConversion("2d25h", Time.DAYS, 2 + 1);
        checkConversion("56d6h", Time.DAYS, 56);

        checkConversion("152d2h10m", Time.DAYS, 152);
        checkConversion("0d2h10m", Time.DAYS, 0);
        checkConversion("2d26h80m", Time.DAYS, 2 + 1);
    }

    private void checkConversion(String input, Time outputType, int expectedValue) {

        int result = AutorankTools.stringToTime(input, outputType);

        assertEquals("Incorrect conversion, should be " + expectedValue + " was " + result, expectedValue, result);
    }
}