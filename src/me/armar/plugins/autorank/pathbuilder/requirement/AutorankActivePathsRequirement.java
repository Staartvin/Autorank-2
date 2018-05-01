package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.pathbuilder.Path;
import org.bukkit.entity.Player;

public class AutorankActivePathsRequirement extends AbstractRequirement {

    private int requiredPaths = -1;
    private Path requiredPath;
    private String requiredPathName;

    @Override
    public String getDescription() {

        if (requiredPaths > 0) {
            return Lang.AUTORANK_NUMBER_OF_ACTIVE_PATHS_REQUIREMENT.getConfigValue(requiredPaths);
        }

        if (requiredPath == null) {
            findMatchingPath();
        }

        return Lang.AUTORANK_SPECIFIC_ACTIVE_PATH_REQUIREMENT.getConfigValue(requiredPath.getDisplayName());
    }

    @Override
    public String getProgress(final Player player) {

        if (requiredPaths > 0) {
            return getAutorank().getPathManager().getActivePaths(player.getUniqueId()).size() + "/" + requiredPaths;
        }

        if (requiredPath == null) {
            findMatchingPath();
        }

        return "has " + requiredPath.getDisplayName() + " as active: " + getAutorank().getPathManager().hasActivePath
                (player.getUniqueId(), requiredPath);
    }

    @Override
    public boolean meetsRequirement(final Player player) {

        if (requiredPaths > 0) {
            return getAutorank().getPathManager().getActivePaths(player.getUniqueId()).size() >= requiredPaths;
        }

        if (requiredPath == null) {
            findMatchingPath();
        }

        return getAutorank().getPathManager().hasActivePath(player.getUniqueId(), requiredPath);
    }

    @Override
    public boolean setOptions(final String[] options) {

        if (options.length > 0) {
            try {
                requiredPaths = Integer.parseInt(options[0]);
            } catch (NumberFormatException e) {

                // Probably not a number, so use it as a path name

                // We cannot retrieve the paths yet, as they are not loaded.
                requiredPathName = options[0];

                return true;
            }
        }

        if (requiredPaths < 0) {
            this.registerWarningMessage("No number is provided or smaller than 0.");
            return false;
        }

        return true;
    }

    private void findMatchingPath() {
        // Try internal name instead
        requiredPath = getAutorank().getPathManager().findPathByDisplayName(requiredPathName, false);

        if (requiredPath == null) {
            requiredPath = getAutorank().getPathManager().findPathByInternalName(requiredPathName, false);
        }

        if (requiredPath == null) {
            this.registerWarningMessage("There is no path called " + requiredPathName);
        }
    }
}
