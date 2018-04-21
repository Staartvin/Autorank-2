package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class InBiomeRequirement extends AbstractRequirement {

    String biome = null;

    @Override
    public String getDescription() {
        String lang = Lang.IN_BIOME_REQUIREMENT.getConfigValue(biome);

        // Check if this requirement is world-specific
        if (this.isWorldSpecific()) {
            lang = lang.concat(" (in world '" + this.getWorld() + "')");
        }

        return lang;
    }

    @Override
    public String getProgress(final Player player) {
        final String currentBiome = player.getLocation().getBlock().getBiome().toString();

        return currentBiome + "/" + biome;
    }

    @Override
    public boolean meetsRequirement(final Player player) {

        // Check if this requirement is world-specific
        if (this.isWorldSpecific()) {
            // Is player in the same world as specified
            if (!this.getWorld().equals(player.getWorld().getName()))
                return false;
        }

        final Location pLocation = player.getLocation();

        return pLocation.getBlock().getBiome().toString().equals(biome);
    }

    @Override
    public boolean setOptions(final String[] options) {

        // biomes
        if (options.length != 1) {
            return false;
        }

        biome = options[0].toUpperCase().replace(" ", "_");

        if (biome == null) {
            this.registerWarningMessage("No biome is provided");
            return false;
        }

        return true;
    }
}
