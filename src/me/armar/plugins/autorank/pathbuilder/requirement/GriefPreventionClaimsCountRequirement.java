package me.armar.plugins.autorank.pathbuilder.requirement;

import org.bukkit.entity.Player;

import me.armar.plugins.autorank.language.Lang;
import me.staartvin.statz.hooks.Dependency;
import me.staartvin.statz.hooks.handlers.GriefPreventionHandler;

public class GriefPreventionClaimsCountRequirement extends Requirement {

    int claimsCount = -1;
    private GriefPreventionHandler handler = null;

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

        handler = (GriefPreventionHandler) this.getDependencyManager()
                .getDependencyHandler(Dependency.GRIEF_PREVENTION);

        if (options.length > 0) {
            claimsCount = Integer.parseInt(options[0]);
        }

        return claimsCount != -1 && handler != null;
    }
}
