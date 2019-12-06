package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.staartvin.plugins.pluginlibrary.Library;
import me.staartvin.plugins.pluginlibrary.hooks.QuestsHook;
import org.bukkit.entity.Player;

import java.util.UUID;

public class QuestsActiveQuestsRequirement extends AbstractRequirement {

    private QuestsHook handler = null;
    private int activeQuests = -1;

    @Override
    public String getDescription() {
        return Lang.QUESTS_ACTIVE_QUESTS_REQUIREMENT.getConfigValue(activeQuests);
    }

    @Override
    public String getProgress(final Player player) {
        return handler.getNumberOfActiveQuests(player.getUniqueId()) + "/" + activeQuests;
    }

    @Override
    protected boolean meetsRequirement(UUID uuid) {

        if (!handler.isAvailable())
            return false;

        return handler.getNumberOfActiveQuests(uuid) >= activeQuests;
    }

    @Override
    public boolean setOptions(final String[] options) {

        // Add dependency
        addDependency(Library.QUESTS);

        handler = (QuestsHook) this.getDependencyManager().getLibraryHook(Library.QUESTS);

        if (options.length > 0) {
            try {
                activeQuests = Integer.parseInt(options[0]);
            } catch (NumberFormatException e) {
                this.registerWarningMessage("An invalid number is provided");
                return false;
            }
        }

        if (activeQuests < 0) {
            this.registerWarningMessage("No number is provided or smaller than 0.");
            return false;
        }

        return true;
    }
}
