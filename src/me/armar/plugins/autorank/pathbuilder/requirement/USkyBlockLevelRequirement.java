package me.armar.plugins.autorank.pathbuilder.requirement;

import org.bukkit.entity.Player;

import me.armar.plugins.autorank.language.Lang;
import me.staartvin.statz.hooks.Dependency;
import me.staartvin.statz.hooks.handlers.uSkyBlockHandler;

public class USkyBlockLevelRequirement extends Requirement {

    private uSkyBlockHandler handler;
    private int islandLevel = -1;

    @Override
    public String getDescription() {

        if (this.hasCustomDescription()) {
            return this.getCustomDescription();
        }
        
        String lang = Lang.USKYBLOCK_LEVEL_REQUIREMENT.getConfigValue(islandLevel + "");

        // Check if this requirement is world-specific
        if (this.isWorldSpecific()) {
            lang = lang.concat(" (in world '" + this.getWorld() + "')");
        }

        return lang;
    }

    @Override
    public String getProgress(final Player player) {
        
        final double islandLevel = handler.getIslandLevel(player);

        return islandLevel + "/" + this.islandLevel;
    }

    @Override
    public boolean meetsRequirement(final Player player) {

        final double islandLevel = handler.getIslandLevel(player);

        return islandLevel >= this.islandLevel;
    }

    @Override
    public boolean setOptions(final String[] options) {

        handler = (uSkyBlockHandler) this.getAutorank().getDependencyManager()
                .getDependencyHandler(Dependency.USKYBLOCK);

        islandLevel = Integer.parseInt(options[0]);

        return islandLevel != -1 && handler != null;
    }
}
