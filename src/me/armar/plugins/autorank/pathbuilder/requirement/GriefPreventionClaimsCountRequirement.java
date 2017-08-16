package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.staartvin.plugins.pluginlibrary.Library;
import me.staartvin.plugins.pluginlibrary.hooks.GriefPreventionHook;
import org.bukkit.entity.Player;

public class GriefPreventionClaimsCountRequirement extends Requirement {

    int claimsCount = -1;
    private GriefPreventionHook handler = null;

    @Override
    public String getDescription() {
        return Lang.GRIEF_PREVENTION_CLAIMS_COUNT_REQUIREMENT.getConfigValue(claimsCount);
    }

    @Override
    public String getProgress(final Player player) {
        final int level = handler.getNumberOfClaims(player.getUniqueId());

        return level + "/" + claimsCount;
    }

    @Override
    public boolean meetsRequirement(final Player player) {

        if (!handler.isAvailable())
            return false;

        final int level = handler.getNumberOfClaims(player.getUniqueId());

        return level >= claimsCount;
    }

    @Override
    public boolean setOptions(final String[] options) {

        handler = (GriefPreventionHook) this.getDependencyManager()
                .getLibraryHook(Library.GRIEFPREVENTION);

        if (options.length > 0) {
            claimsCount = Integer.parseInt(options[0]);
        }

        return claimsCount != -1 && handler != null;
    }
}
