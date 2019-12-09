package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import org.bukkit.entity.Player;

public class PermissionRequirement extends AbstractRequirement {

    String permission = null;

    @Override
    public String getDescription() {

        String lang = Lang.PERMISSION_REQUIREMENT.getConfigValue(permission);

        // Check if this requirement is world-specific
        if (this.isWorldSpecific()) {
            lang = lang.concat(" (in world '" + this.getWorld() + "')");
        }

        return lang;
    }

    @Override
    public String getProgress(final Player player) {
        final String progress = "unknown";
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

        return player.hasPermission(permission);
    }

    @Override
    public boolean initRequirement(final String[] options) {

        permission = options[0];

        if (permission == null) {
            this.registerWarningMessage("No permission is provided");
            return false;
        }

        return true;
    }

    @Override
    public boolean needsOnlinePlayer() {
        return true;
    }
}
