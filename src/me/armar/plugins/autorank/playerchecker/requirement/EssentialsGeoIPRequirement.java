package me.armar.plugins.autorank.playerchecker.requirement;

import java.util.ArrayList;
import java.util.List;
import me.armar.plugins.autorank.hooks.DependencyManager.dependency;
import me.armar.plugins.autorank.hooks.essentialsapi.EssentialsHandler;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;
import org.bukkit.entity.Player;

public class EssentialsGeoIPRequirement extends Requirement {

    private final List<String> locations = new ArrayList<String>();
    private EssentialsHandler essHandler = null;

    @Override
    public String getDescription() {
        return Lang.ESSENTIALS_GEOIP_LOCATION_REQUIREMENT
                .getConfigValue(AutorankTools.seperateList(locations, "or"));
    }

    @Override
    public String getProgress(final Player player) {

        final String realLocation = essHandler.getGeoIPLocation(player);

        return AutorankTools.makeProgressString(locations, "", realLocation);
    }

    @Override
    public boolean meetsRequirement(final Player player) {
        final String realLocation = essHandler.getGeoIPLocation(player);

        if (realLocation == null) {
            return false;
        }

        for (final String loc : locations) {
            if (loc != null && loc.equalsIgnoreCase(realLocation)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean setOptions(final List<String[]> optionsList) {

        essHandler = (EssentialsHandler) this.getDependencyManager()
                .getDependency(dependency.ESSENTIALS);

        for (final String[] options : optionsList) {
            if (options.length != 1) {
                return false;
            }

            locations.add(options[0]);
        }

        return !locations.isEmpty();

    }
}
