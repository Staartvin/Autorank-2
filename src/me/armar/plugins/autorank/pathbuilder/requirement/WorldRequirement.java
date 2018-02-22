package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import org.bukkit.entity.Player;

public class WorldRequirement extends Requirement {

    String worldName = null;

    @Override
    public String getDescription() {
        return Lang.WORLD_REQUIREMENT.getConfigValue(worldName);
    }

    @Override
    public String getProgress(final Player player) {
        final String world = player.getWorld().getName();
        return world + "/" + worldName;
    }

    @Override
    public boolean meetsRequirement(final Player player) {

        final String world = player.getWorld().getName();

        return (worldName != null && world.equals(worldName));
    }

    @Override
    public boolean setOptions(final String[] options) {

        if (options.length > 0) {
            worldName = options[0];
        }

        if (worldName == null) {
            this.registerWarningMessage("No world is specified");
            return false;
        }

        return true;
    }
}
