package me.armar.plugins.autorank.pathbuilder.requirement;

import org.bukkit.entity.Player;

import me.armar.plugins.autorank.language.Lang;
import me.staartvin.statz.hooks.Dependency;
import me.staartvin.statz.hooks.handlers.EssentialsHandler;

public class EssentialsGeoIPRequirement extends Requirement {

    private EssentialsHandler essHandler = null;
    String location = null;

    @Override
    public String getDescription() {
        
        if (this.hasCustomDescription()) {
            return this.getCustomDescription();
        }
        
        return Lang.ESSENTIALS_GEOIP_LOCATION_REQUIREMENT.getConfigValue(location);
    }

    @Override
    public String getProgress(final Player player) {

        final String realLocation = essHandler.getGeoIPLocation(player);

        return realLocation + "/" + location;
    }

    @Override
    public boolean meetsRequirement(final Player player) {
        final String realLocation = essHandler.getGeoIPLocation(player);

        if (realLocation == null)
            return false;

        return location != null && location.equalsIgnoreCase(realLocation);
    }

    @Override
    public boolean setOptions(final String[] options) {

        essHandler = (EssentialsHandler) this.getDependencyManager().getDependencyHandler(Dependency.ESSENTIALS);

        if (options.length != 1) {
            return false;
        }

        location = options[0];

        return location != null;

    }
}
