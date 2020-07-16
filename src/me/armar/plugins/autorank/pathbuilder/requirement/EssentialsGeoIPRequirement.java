package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.staartvin.utils.pluginlibrary.Library;
import me.staartvin.utils.pluginlibrary.hooks.EssentialsXHook;

import java.util.UUID;

public class EssentialsGeoIPRequirement extends AbstractRequirement {

    private EssentialsXHook essHandler = null;
    String location = null;

    @Override
    public String getDescription() {
        return Lang.ESSENTIALS_GEOIP_LOCATION_REQUIREMENT.getConfigValue(location);
    }

    @Override
    public String getProgressString(UUID uuid) {
        return essHandler.getGeoIPLocation(uuid) + "/" + location;
    }

    @Override
    protected boolean meetsRequirement(UUID uuid) {
        final String realLocation = essHandler.getGeoIPLocation(uuid);

        if (realLocation == null)
            return false;

        return location != null && location.equalsIgnoreCase(realLocation);
    }

    @Override
    public boolean initRequirement(final String[] options) {

        // Add dependency
        addDependency(Library.ESSENTIALSX);

        essHandler = (EssentialsXHook) this.getDependencyManager().getLibraryHook(Library.ESSENTIALSX).orElse(null);

        if (options.length != 1) {
            return false;
        }

        location = options[0];

        if (location == null) {
            this.registerWarningMessage("No location is provided");
            return false;
        }

        if (essHandler == null || !essHandler.isHooked()) {
            this.registerWarningMessage("EssentialsX is not available");
            return false;
        }

        return true;
    }
}
