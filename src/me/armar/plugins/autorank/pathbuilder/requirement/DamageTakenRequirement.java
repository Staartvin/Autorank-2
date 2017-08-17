package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.statsmanager.StatsPlugin;
import org.bukkit.entity.Player;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;

public class DamageTakenRequirement extends Requirement {

    int damageTaken = -1;

    @Override
    public String getDescription() {

        String lang = Lang.DAMAGE_TAKEN_REQUIREMENT.getConfigValue(damageTaken + "");

        // Check if this requirement is world-specific
        if (this.isWorldSpecific()) {
            lang = lang.concat(" (in world '" + this.getWorld() + "')");
        }

        return lang;
    }

    @Override
    public String getProgress(final Player player) {
        final int damTaken = getStatsPlugin().getNormalStat(StatsPlugin.StatType.DAMAGE_TAKEN, player.getUniqueId(),
                AutorankTools.makeStatsInfo("world", this.getWorld()));

        return damTaken + "/" + damageTaken;
    }

    @Override
    public boolean meetsRequirement(final Player player) {

        if (!getStatsPlugin().isEnabled())
            return false;

        final int damTaken = getStatsPlugin().getNormalStat(StatsPlugin.StatType.DAMAGE_TAKEN, player.getUniqueId(),
                AutorankTools.makeStatsInfo("world", this.getWorld()));

        return damTaken >= damageTaken;
    }

    @Override
    public boolean setOptions(final String[] options) {

        try {
            damageTaken = Integer.parseInt(options[0]);
        } catch (NumberFormatException e) {
            this.registerWarningMessage("An invalid number is provided");
            return false;
        }

        if (damageTaken < 0) {
            this.registerWarningMessage("No number is provided or smaller than 0.");
            return false;
        }

        return true;
    }
}
