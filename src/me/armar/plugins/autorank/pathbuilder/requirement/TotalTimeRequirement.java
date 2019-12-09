package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;
import me.armar.plugins.autorank.util.AutorankTools.Time;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * This requirement checks for the total time on the server
 * <p>
 * (i.e. the time now - the time he joined for the first time) Date created:
 * 13:49:33 15 jan. 2014
 *
 * @author Staartvin
 */
public class TotalTimeRequirement extends AbstractRequirement {

    int totalTime = -1;

    @Override
    public String getDescription() {
        return Lang.TOTAL_TIME_REQUIREMENT.getConfigValue(AutorankTools.timeToString(totalTime, Time.MINUTES));
    }

    @Override
    public String getProgress(final Player player) {

        // the time he first joined the server
        final long joinTime = player.getFirstPlayed();

        final long currentTime = System.currentTimeMillis();

        // Difference in minutes
        final long difference = (currentTime - joinTime) / 60000;

        return difference + " min/" + totalTime + " min";
    }

    @Override
    protected boolean meetsRequirement(UUID uuid) {

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);

        if (offlinePlayer.hasPlayedBefore()) return false;

        // the time he first joined the server
        final long joinTime = offlinePlayer.getFirstPlayed();

        final long currentTime = System.currentTimeMillis();

        // Difference in minutes
        final long difference = (currentTime - joinTime) / 60000;

        return difference >= totalTime;
    }

    @Override
    public boolean initRequirement(final String[] options) {
        if (options.length > 0) {
            totalTime = AutorankTools.stringToTime(options[0], Time.MINUTES);
        } else {
            this.registerWarningMessage("An invalid number is provided");
            return false;
        }

        if (totalTime < 0) {
            this.registerWarningMessage("No number is provided or smaller than 0.");
            return false;
        }

        return true;
    }
}
