package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.staartvin.plugins.pluginlibrary.Library;
import me.staartvin.plugins.pluginlibrary.hooks.EssentialsXHook;
import org.bukkit.entity.Player;

import java.util.UUID;

public class EssentialsGeoIPRequirement extends AbstractRequirement {

    private EssentialsXHook essHandler = null;
    String location = null;

    @Override
    public String getDescription() {
        return Lang.ESSENTIALS_GEOIP_LOCATION_REQUIREMENT.getConfigValue(location);
    }

    @Override
    public String getProgress(final Player player) {

        final String realLocation = essHandler.getGeoIPLocation(player.getUniqueId());

        return realLocation + "/" + location;
    }

    @Override
    protected boolean meetsRequirement(UUID uuid) {
        final String realLocation = essHandler.getGeoIPLocation(uuid);

        if (realLocation == null)
            return false;

        return location != null && location.equalsIgnoreCase(realLocation);
    }

    @Override
    public boolean setOptions(final String[] options) {

        // Add dependency
        addDependency(Library.ESSENTIALSX);

        essHandler = (EssentialsXHook) this.getDependencyManager().getLibraryHook(Library.ESSENTIALSX);

        if (options.length != 1) {
            return false;
        }

        location = options[0];

        if (location == null) {
            this.registerWarningMessage("No location is provided");
            return false;
        }

        if (essHandler == null || !essHandler.isAvailable()) {
            this.registerWarningMessage("EssentialsX is not available");
            return false;
        }

        return true;
    }
}
