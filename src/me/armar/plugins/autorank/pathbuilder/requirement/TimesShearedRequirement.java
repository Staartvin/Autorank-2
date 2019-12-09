package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.statsmanager.StatsPlugin;
import me.armar.plugins.autorank.util.AutorankTools;
import me.staartvin.plugins.pluginlibrary.Library;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TimesShearedRequirement extends AbstractRequirement {

    int timesShorn = -1;

    @Override
    public String getDescription() {
        String lang = Lang.TIMES_SHEARED_REQUIREMENT.getConfigValue(timesShorn + "");

        // Check if this requirement is world-specific
        if (this.isWorldSpecific()) {
            lang = lang.concat(" (in world '" + this.getWorld() + "')");
        }

        return lang;
    }

    @Override
    public String getProgress(final Player player) {
        final int progressBar = this.getStatsPlugin().getNormalStat(StatsPlugin.StatType.TIMES_SHEARED,
                player.getUniqueId(), AutorankTools.makeStatsInfo("world", this.getWorld()));

        return progressBar + "/" + timesShorn;
    }

    @Override
    protected boolean meetsRequirement(UUID uuid) {
        if (!getStatsPlugin().isEnabled())
            return false;

        return this.getStatsPlugin().getNormalStat(StatsPlugin.StatType.TIMES_SHEARED, uuid,
                AutorankTools.makeStatsInfo("world", this.getWorld())) >= timesShorn;
    }

    @Override
    public boolean initRequirement(final String[] options) {

        // Add dependency
        addDependency(Library.STATZ);

        try {
            timesShorn = Integer.parseInt(options[0]);
        } catch (final Exception e) {
            this.registerWarningMessage("An invalid number is provided");
            return false;
        }

        if (timesShorn < 0) {
            this.registerWarningMessage("No number is provided or smaller than 0.");
            return false;
        }


        return true;
    }
}
