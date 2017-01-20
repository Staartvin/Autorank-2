package me.armar.plugins.autorank.pathbuilder.requirement;

import org.bukkit.entity.Player;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;
import me.armar.plugins.autorank.util.AutorankTools.Time;

/**
 * This requirement checks for local play time Date created: 13:49:33 15 jan.
 * 2014
 * 
 * @author Staartvin
 * 
 */
public class TimeRequirement extends Requirement {

    int timeNeeded = -1;

    @Override
    public String getDescription() {
        return Lang.TIME_REQUIREMENT.getConfigValue(AutorankTools.timeToString(timeNeeded, Time.MINUTES));
    }

    @Override
    public String getProgress(final Player player) {

        final int playtime = (getAutorank().getPlaytimes().getTimeOfPlayer(player.getName(), true) / 60);

        return playtime + " min/" + timeNeeded + " min";
    }

    @Override
    public boolean meetsRequirement(final Player player) {
        // Use getTimeOf so that when switched to another time, it'll still
        // work.
        // getTimeOfPlayer() is in seconds, so convert.
        final double playtime = this.getAutorank().getPlaytimes().getTimeOfPlayer(player.getName(), true) / 60;

        return timeNeeded != -1 && playtime >= timeNeeded;
    }

    @Override
    public boolean setOptions(final String[] options) {
        if (options.length > 0) {
            timeNeeded = AutorankTools.stringToTime(options[0], Time.MINUTES);
        }

        return timeNeeded != -1;
    }
}
