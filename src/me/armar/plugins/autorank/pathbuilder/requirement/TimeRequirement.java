package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;
import me.armar.plugins.autorank.util.AutorankTools.Time;
import org.bukkit.entity.Player;

/**
 * This requirement checks for local play time Date created: 13:49:33 15 jan.
 * 2014
 *
 * @author Staartvin
 */
public class TimeRequirement extends AbstractRequirement {

    int timeNeeded = -1;

    @Override
    public String getDescription() {
        return Lang.TIME_REQUIREMENT.getConfigValue(AutorankTools.timeToString(timeNeeded, Time.MINUTES));
    }

    @Override
    public String getProgress(final Player player) {

        final int playtime = (getAutorank().getPlayTimeManager().getTimeOfPlayer(player.getName(), true) / 60);

        return AutorankTools.timeToString(playtime, Time.MINUTES) + "/" + AutorankTools.timeToString(timeNeeded,
                Time.MINUTES);
    }

    @Override
    public boolean meetsRequirement(final Player player) {
        // Use getTimeOf so that when switched to another time, it'll still
        // work.
        // getTimeOfPlayer() is in seconds, so convert.
        final int playTime = this.getAutorank().getPlayTimeManager().getTimeOfPlayer(player.getName(), true) / 60;

        return timeNeeded != -1 && playTime >= timeNeeded;
    }

    @Override
    public boolean setOptions(final String[] options) {

        if (options.length > 0) {
            timeNeeded = AutorankTools.stringToTime(options[0], Time.MINUTES);
        }

        if (timeNeeded < 0) {
            this.registerWarningMessage("No number is provided or smaller than 0.");
            return false;
        }

        return true;
    }

    @Override
    public boolean needsOnlinePlayer() {
        return true;
    }
}
