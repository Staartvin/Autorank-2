package me.armar.plugins.autorank.pathbuilder.requirement;

import java.util.UUID;

import org.bukkit.entity.Player;

import me.armar.plugins.autorank.language.Lang;
import me.staartvin.statz.hooks.Dependency;
import me.staartvin.statz.hooks.handlers.AcidIslandHandler;

public class AcidIslandLevelRequirement extends Requirement {

    private AcidIslandHandler handler;
    private int islandLevel = -1;

    @Override
    public String getDescription() {

        if (this.hasCustomDescription()) {
            return this.getCustomDescription();
        }
        
        String lang = Lang.ACID_ISLAND_LEVEL_REQUIREMENT.getConfigValue(islandLevel + "");

        // Check if this requirement is world-specific
        if (this.isWorldSpecific()) {
            lang = lang.concat(" (in world '" + this.getWorld() + "')");
        }

        return lang;
    }

    @Override
    public String getProgress(final Player player) {

        final UUID uuid = this.getAutorank().getUUIDStorage().getStoredUUID(player.getName());

        final int islandLevel = handler.getIslandLevel(uuid);

        return islandLevel + "/" + this.islandLevel;
    }

    @Override
    public boolean meetsRequirement(final Player player) {

        final UUID uuid = this.getAutorank().getUUIDStorage().getStoredUUID(player.getName());

        final int islandLevel = handler.getIslandLevel(uuid);

        return islandLevel >= this.islandLevel;
    }

    @Override
    public boolean setOptions(final String[] options) {
        handler = (AcidIslandHandler) this.getAutorank().getDependencyManager()
                .getDependencyHandler(Dependency.ACIDISLAND);

        islandLevel = Integer.parseInt(options[0]);

        return islandLevel != -1 && handler != null;
    }
}
