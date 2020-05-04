package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

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
        return Lang.TOTAL_TIME_REQUIREMENT.getConfigValue(AutorankTools.timeToString(totalTime, TimeUnit.MINUTES));
    }

    @Override
    public String getProgressString(UUID uuid) {

        // the time he first joined the server
        final long joinTime = Bukkit.getOfflinePlayer(uuid).getFirstPlayed();

        final long currentTime = System.currentTimeMillis();

        // Difference in minutes
        final long difference = (currentTime - joinTime) / 60000;

        return difference + " min/" + totalTime + " min";
    }

    @Override
    protected boolean meetsRequirement(UUID uuid) {

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);

        if (!offlinePlayer.hasPlayedBefore()) {
            getAutorank().debugMessage("User has not played before, so does not meet '" + this.getDescription() + "'.");
            return false;
        }

        // the time he first joined the server
        final long joinTime = offlinePlayer.getFirstPlayed();

        final long currentTime = System.currentTimeMillis();

        // Difference in minutes
        final long difference = (currentTime - joinTime) / 60000;

        System.out.println("Difference is: " + difference);
        System.out.println("Total time is: " + totalTime);

        return difference >= totalTime;
    }

    @Override
    public boolean initRequirement(final String[] options) {
        if (options.length > 0) {
            totalTime = AutorankTools.stringToTime(options[0], TimeUnit.MINUTES);
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

    @Override
    public double getProgressPercentage(UUID uuid) {
        final long joinTime = Bukkit.getOfflinePlayer(uuid).getFirstPlayed();

        final long currentTime = System.currentTimeMillis();

        // Difference in minutes
        final long difference = (currentTime - joinTime) / 60000;

        return difference * 1.0d / totalTime;
    }
}
