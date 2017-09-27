package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.statsmanager.StatsPlugin;
import me.staartvin.plugins.pluginlibrary.Library;
import org.bukkit.entity.Player;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;

public class ItemsCraftedRequirement extends Requirement {

    int itemsCrafted = -1;

    @Override
    public String getDescription() {

        String lang = Lang.ITEMS_CRAFTED_REQUIREMENT.getConfigValue(itemsCrafted + "");

        // Check if this requirement is world-specific
        if (this.isWorldSpecific()) {
            lang = lang.concat(" (in world '" + this.getWorld() + "')");
        }

        return lang;
    }

    @Override
    public String getProgress(final Player player) {
        final int progressBar = this.getStatsPlugin().getNormalStat(StatsPlugin.StatType.ITEMS_CRAFTED,
                player.getUniqueId(), AutorankTools.makeStatsInfo("world", this.getWorld()));

        return progressBar + "/" + itemsCrafted;
    }

    @Override
    public boolean meetsRequirement(final Player player) {

        if (!getStatsPlugin().isEnabled())
            return false;

        final int realItemsCrafted = this.getStatsPlugin().getNormalStat(StatsPlugin.StatType.ITEMS_CRAFTED,
                player.getUniqueId(), AutorankTools.makeStatsInfo("world", this.getWorld()));

        return realItemsCrafted >= itemsCrafted;
    }

    @Override
    public boolean setOptions(final String[] options) {

        // Add dependency
        addDependency(Library.STATZ);

        try {
            itemsCrafted = Integer.parseInt(options[0]);
        } catch (NumberFormatException e) {
            this.registerWarningMessage("An invalid number is provided");
            return false;
        }

        if (itemsCrafted < 0) {
            this.registerWarningMessage("No number is provided or smaller than 0.");
            return false;
        }

        return true;
    }
}
