package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;
import me.armar.plugins.autorank.util.AutorankTools.Time;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * This requirement checks for global playtime Date created: 13:49:53 15 jan.
 * 2014
 *
 * @author Staartvin
 */
public class GlobalTimeRequirement extends AbstractRequirement {

    int globalTime = -1;

    @Override
    public String getDescription() {
        return Lang.GLOBAL_TIME_REQUIREMENT.getConfigValue(AutorankTools.timeToString(globalTime, Time.MINUTES));
    }

    @Override
    public String getProgress(final Player player) {

        final int playtime = getAutorank().getMySQLManager().getGlobalTime(player.getUniqueId());

        return playtime + "/" + globalTime;
    }

    @Override
    public boolean meetsRequirement(final Player player) {
        final UUID uuid = player.getUniqueId();

        final double playtime = this.getAutorank().getMySQLManager().getGlobalTime(uuid);

        return globalTime != -1 && playtime >= globalTime;
    }

    @Override
    public boolean setOptions(final String[] options) {

        globalTime = AutorankTools.stringToTime(options[0], Time.MINUTES);

        if (globalTime < 0) {
            this.registerWarningMessage("No number is provided or smaller than 0.");
            return false;
        }

        return true;
    }
}
