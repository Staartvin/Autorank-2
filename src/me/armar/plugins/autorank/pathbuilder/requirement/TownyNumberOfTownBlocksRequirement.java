package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.staartvin.utils.pluginlibrary.autorank.Library;
import me.staartvin.utils.pluginlibrary.autorank.hooks.TownyAdvancedHook;
import org.bukkit.entity.Player;

public class TownyNumberOfTownBlocksRequirement extends AbstractRequirement {

    int numberOfTownBlocks = 0;
    private TownyAdvancedHook hook;

    @Override
    public String getDescription() {

        String lang = Lang.TOWNY_NEED_NUMBER_OF_TOWN_BLOCKS.getConfigValue(numberOfTownBlocks);

        // Check if this requirement is world-specific
        if (this.isWorldSpecific()) {
            lang = lang.concat(" (in world '" + this.getWorld() + "')");
        }

        return lang;
    }

    @Override
    public String getProgressString(final Player player) {
        return hook.getNumberOfTownBlocks(player.getName()) + "/" + numberOfTownBlocks;
    }

    @Override
    public boolean meetsRequirement(final Player player) {

        if (!hook.isHooked())
            return false;

        return hook.getNumberOfTownBlocks(player.getName()) >= numberOfTownBlocks;
    }

    @Override
    public boolean initRequirement(final String[] options) {

        // Add dependency
        addDependency(Library.TOWNY_ADVANCED);

        this.hook =
                (TownyAdvancedHook) this.getAutorank().getDependencyManager().getLibraryHook(Library.TOWNY_ADVANCED).orElse(null);

        try {
            numberOfTownBlocks = Integer.parseInt(options[0]);
        } catch (NumberFormatException e) {
            this.registerWarningMessage("An invalid integer was provided.");
            return false;
        }

        if (numberOfTownBlocks <= 0) {
            this.registerWarningMessage("Number of town blocks should be bigger than zero!");
            return false;
        }

        if (hook == null || !hook.isHooked()) {
            this.registerWarningMessage("Towny is not available");
            return false;
        }

        return true;
    }

    @Override
    public boolean needsOnlinePlayer() {
        return true;
    }
}
