package me.armar.plugins.autorank.util;

import me.armar.plugins.autorank.util.AutorankTools.Time;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Test for {@link AutorankTools}.
 */
public class AutorankToolsTest {

    private Map<String, Integer> timeToSeconds = buildTimeToExpectedSecondsMap();

    @Test
    public void shouldConvertTimeToSeconds() {
        checkTimeEntries(Time.SECONDS, 1);
    }

    @Test
    public void shouldConvertTimeToMinutes() {
        checkTimeEntries(Time.MINUTES, 60);
    }

    @Test
    public void shouldConvertTimeToHours() {
        checkTimeEntries(Time.HOURS, 3600);
    }

    @Test
    public void shouldConvertTimeToDays() {
        checkTimeEntries(Time.DAYS, 24 * 3600);
    }

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

    private void checkTimeEntries(Time time, int secondsInTimeUnit) {
        for (Map.Entry<String, Integer> testCase : timeToSeconds.entrySet()) {
            // given / when
            int result = AutorankTools.stringToTime(testCase.getKey(), time);

            // then
            int expectedValue = testCase.getValue() / secondsInTimeUnit;
            assertThat("Text '" + testCase.getKey() + "' should evaluate to " + expectedValue + " " + time,
                result, equalTo(expectedValue));
        }
    }

    private static Map<String, Integer> buildTimeToExpectedSecondsMap() {
        Map<String, Integer> timeToSeconds = new LinkedHashMap<>();
        timeToSeconds.put("1d", 24 * 3600);
        timeToSeconds.put("4h", 4 * 3600);
        timeToSeconds.put("20m", 20 * 60);
        timeToSeconds.put("2d10h45m", 2 * 24 * 3600 + 10 * 3600 + 45 * 60);
        timeToSeconds.put("3d8m", 3 * 24 * 3600 + 8 * 60);
        timeToSeconds.put("11h0m", 11 * 3600);
        timeToSeconds.put("0d0h60m", 3600);
        return timeToSeconds;
    }

}