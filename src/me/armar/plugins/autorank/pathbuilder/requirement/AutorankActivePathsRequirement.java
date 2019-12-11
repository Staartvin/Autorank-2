package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.pathbuilder.Path;

import java.util.UUID;

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
    public String getProgressString(UUID uuid) {

        if (requiredPaths > 0) {
            return getAutorank().getPathManager().getActivePaths(uuid).size() + "/" + requiredPaths;
        }

        if (requiredPath == null) {
            findMatchingPath();
        }

        return "has " + requiredPath.getDisplayName() + " as active: " + requiredPath.isActive(uuid);
    }

    @Override
    protected boolean meetsRequirement(UUID uuid) {

        if (requiredPaths > 0) {
            return getAutorank().getPathManager().getActivePaths(uuid).size() >= requiredPaths;
        }

        if (requiredPath == null) {
            findMatchingPath();
        }

        return requiredPath.isActive(uuid);
    }

    @Override
    public boolean initRequirement(final String[] options) {

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

    @Override
    public boolean needsOnlinePlayer() {
        return false;
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
