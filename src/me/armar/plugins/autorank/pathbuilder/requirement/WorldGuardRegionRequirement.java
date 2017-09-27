package me.armar.plugins.autorank.pathbuilder.requirement;

import me.staartvin.plugins.pluginlibrary.Library;
import me.staartvin.plugins.pluginlibrary.hooks.WorldGuardHook;
import org.bukkit.entity.Player;

import me.armar.plugins.autorank.language.Lang;

/**
 * This requirement checks for WorldGuard region Date created: 13:49:33 15 jan.
 * 2014
 *
 * @author Staartvin
 */
public class WorldGuardRegionRequirement extends Requirement {

    private WorldGuardHook handler;
    String regionName = null;

    @Override
    public String getDescription() {

        String lang = Lang.WORLD_GUARD_REGION_REQUIREMENT.getConfigValue(regionName);

        // Check if this requirement is world-specific
        if (this.isWorldSpecific()) {
            lang = lang.concat(" (in world '" + this.getWorld() + "')");
        }

        return lang;
    }

    @Override
    public String getProgress(final Player player) {
        final String progress = "Cannot show progress";
        return progress;
    }

    @Override
    public boolean meetsRequirement(final Player player) {

        // Check if this requirement is world-specific
        if (this.isWorldSpecific()) {
            // Is player in the same world as specified
            if (!this.getWorld().equals(player.getWorld().getName()))
                return false;
        }

        return handler.isInRegion(player, regionName);
    }

    @Override
    public boolean setOptions(final String[] options) {

        // Add dependency
        addDependency(Library.WORLDGUARD);

        handler = (WorldGuardHook) this.getAutorank().getDependencyManager()
                .getLibraryHook(Library.WORLDGUARD);

        if (options.length > 0) {
            regionName = options[0].trim();
        }

        if (regionName == null) {
            this.registerWarningMessage("Region is not specified");
            return false;
        }

        if (handler == null || !handler.isAvailable()) {
            this.registerWarningMessage("WorldGuard is not available");
            return false;
        }

        return true;
    }
}
