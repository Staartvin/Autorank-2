package me.armar.plugins.autorank.pathbuilder.requirement;

import org.bukkit.entity.Player;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.statsmanager.handlers.StatsHandler;
import me.armar.plugins.autorank.util.AutorankTools;

public class DamageTakenRequirement extends Requirement {

    int damageTaken = -1;

    @Override
    public String getDescription() {

        if (this.hasCustomDescription()) {
            return this.getCustomDescription();
        }
        
        String lang = Lang.DAMAGE_TAKEN_REQUIREMENT.getConfigValue(damageTaken + "");

        // Check if this requirement is world-specific
        if (this.isWorldSpecific()) {
            lang = lang.concat(" (in world '" + this.getWorld() + "')");
        }

        return lang;
    }

    @Override
    public String getProgress(final Player player) {
        final int damTaken = getStatsPlugin().getNormalStat(StatsHandler.statTypes.DAMAGE_TAKEN, player.getUniqueId(),
                AutorankTools.makeStatsInfo("world", this.getWorld()));

        return damTaken + "/" + damageTaken;
    }

    @Override
    public boolean meetsRequirement(final Player player) {

        if (!getStatsPlugin().isEnabled())
            return false;

        final int damTaken = getStatsPlugin().getNormalStat(StatsHandler.statTypes.DAMAGE_TAKEN, player.getUniqueId(),
                AutorankTools.makeStatsInfo("world", this.getWorld()));

        return damTaken >= damageTaken;
    }

    @Override
    public boolean setOptions(final String[] options) {

        damageTaken = Integer.parseInt(options[0]);

        return damageTaken != -1;
    }
}
