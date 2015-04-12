package me.armar.plugins.autorank.playerchecker.requirement;

import java.util.ArrayList;
import java.util.List;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;
import me.armar.plugins.autorank.util.AutorankTools.Time;
import org.bukkit.entity.Player;

/**
 * This requirement checks for the total time on the server
 *
 * (i.e. the time now - the time he joined for the first time) Date created: 13:49:33 15 jan. 2014
 *
 * @author Staartvin
 *
 */
public class TotalTimeRequirement extends Requirement {

    List<Integer> times = new ArrayList<Integer>();

    @Override
    public String getDescription() {

        final List<String> sTimes = new ArrayList<String>();

        for (final int time : times) {
            sTimes.add(AutorankTools.timeToString(time, Time.MINUTES));
        }

        return Lang.TOTAL_TIME_REQUIREMENT.getConfigValue(AutorankTools
                .seperateList(sTimes, "or"));
    }

    @Override
    public String getProgress(final Player player) {

        // the time he first joined the server
        final long joinTime = player.getFirstPlayed();

        final long currentTime = System.currentTimeMillis();

        // Difference in minutes
        final long difference = (currentTime - joinTime) / 60000;

        String progress = "";
        //progress = progress.concat(difference + " min" + "/" + time + " min");

        progress = AutorankTools.makeProgressString(times, "min", ""
                + difference);
        return progress;
    }

    @Override
    public boolean meetsRequirement(final Player player) {
        // the time he first joined the server
        final long joinTime = player.getFirstPlayed();

        final long currentTime = System.currentTimeMillis();

        // Difference in minutes
        final long difference = (currentTime - joinTime) / 60000;

        for (final int time : times) {
            if (time != -1 && difference >= time) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean setOptions(final List<String[]> optionsList) {
        for (final String[] options : optionsList) {
            if (options.length > 0) {
                times.add(AutorankTools.stringToTime(options[0], Time.MINUTES));
            } else {
                return false;
            }
        }

        return !times.isEmpty();
    }
}
