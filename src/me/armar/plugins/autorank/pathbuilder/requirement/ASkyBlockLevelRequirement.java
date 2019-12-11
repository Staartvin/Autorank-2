package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.staartvin.plugins.pluginlibrary.Library;
import me.staartvin.plugins.pluginlibrary.hooks.ASkyBlockHook;

import java.util.UUID;

public class ASkyBlockLevelRequirement extends AbstractRequirement {

    private ASkyBlockHook handler;
    private int islandLevel = -1;

    @Override
    public String getDescription() {

        String lang = Lang.ASKYBLOCK_LEVEL_REQUIREMENT.getConfigValue(islandLevel + "");

        // Check if this requirement is world-specific
        if (this.isWorldSpecific()) {
            lang = lang.concat(" (in world '" + this.getWorld() + "')");
        }

        return lang;
    }

    @Override
    public String getProgressString(UUID uuid) {
        return handler.getIslandLevel(uuid) + "/" + this.islandLevel;
    }

    @Override
    public boolean meetsRequirement(UUID uuid) {
        return handler.getIslandLevel(uuid) >= this.islandLevel;
    }

    @Override
    public boolean initRequirement(final String[] options) {

        // Add dependency
        addDependency(Library.ASKYBLOCK);

        handler = (ASkyBlockHook) this.getAutorank().getDependencyManager()
                .getLibraryHook(Library.ASKYBLOCK);

        try {
            islandLevel = Integer.parseInt(options[0]);
        } catch (NumberFormatException e) {
            this.registerWarningMessage("An invalid number is provided");
            return false;
        }

        if (islandLevel < 0) {
            this.registerWarningMessage("No number is provided or smaller than 0.");
            return false;
        }

        if (handler == null || !handler.isAvailable()) {
            this.registerWarningMessage("ASkyBlock is not available");
            return false;
        }

        return true;
    }

    @Override
    public boolean needsOnlinePlayer() {
        return false;
    }
}
