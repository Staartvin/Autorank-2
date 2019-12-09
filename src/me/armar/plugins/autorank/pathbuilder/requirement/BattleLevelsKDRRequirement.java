package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.staartvin.plugins.pluginlibrary.Library;
import me.staartvin.plugins.pluginlibrary.hooks.BattleLevelsHook;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BattleLevelsKDRRequirement extends AbstractRequirement {

    private BattleLevelsHook handler = null;
    private double neededKillDeathRatio = -1.0;

    @Override
    public String getDescription() {
        return Lang.BATTLELEVELS_KILL_DEATH_RATIO_REQUIREMENT.getConfigValue(neededKillDeathRatio);
    }

    @Override
    public String getProgress(final Player player) {
        return handler.getKillDeathRatio(player.getUniqueId()) + "/" + neededKillDeathRatio;
    }

    @Override
    protected boolean meetsRequirement(UUID uuid) {

        if (!handler.isAvailable())
            return false;

        return handler.getKillDeathRatio(uuid) >= neededKillDeathRatio;
    }

    @Override
    public boolean initRequirement(final String[] options) {

        // Add dependency
        addDependency(Library.BATTLELEVELS);

        handler = (BattleLevelsHook) this.getDependencyManager().getLibraryHook(Library.BATTLELEVELS);

        if (options.length > 0) {
            try {
                neededKillDeathRatio = Double.parseDouble(options[0]);
            } catch (NumberFormatException e) {
                this.registerWarningMessage("An invalid number is provided");
                return false;
            }
        }

        if (neededKillDeathRatio < 0) {
            this.registerWarningMessage("No number is provided or smaller than 0.");
            return false;
        }

        return true;
    }

    @Override
    public boolean needsOnlinePlayer() {
        return false;
    }
}
