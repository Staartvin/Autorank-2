package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * This requirement checks whether a player is in a group
 *
 * @author Staartvin
 */
public class InGroupRequirement extends AbstractRequirement {

    String group = null;

    @Override
    public String getDescription() {
        return Lang.GROUP_REQUIREMENT.getConfigValue(group);
    }

    @Override
    public String getProgress(final Player player) {

        Collection<String> groups = this.getAutorank().getPermPlugHandler().getPermissionPlugin().getPlayerGroups(player);

        for (String groupString : groups) {
            if (groupString.equalsIgnoreCase(group)) {
                return "you're in the group!";
            }
        }
        return "you're not in the group";
    }

    @Override
    public boolean meetsRequirement(final Player player) {
        for (String groupString : this.getAutorank().getPermPlugHandler().getPermissionPlugin()
                .getPlayerGroups(player)) {
            if (groupString.equalsIgnoreCase(group)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean setOptions(final String[] options) {
        if (options.length > 0) {
            group = options[0].trim();
        }

        if (group == null) {
            this.registerWarningMessage("No group is provided");
            return false;
        }

        return true;
    }
}
