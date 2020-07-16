package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.hooks.quests.QuestsPlugin;
import me.armar.plugins.autorank.language.Lang;
import me.staartvin.utils.pluginlibrary.Library;

import java.util.UUID;

public class QuestsActiveQuestsRequirement extends AbstractRequirement {

    private QuestsPlugin handler = null;
    private int activeQuests = -1;

    @Override
    public String getDescription() {
        return Lang.QUESTS_ACTIVE_QUESTS_REQUIREMENT.getConfigValue(activeQuests);
    }

    @Override
    public String getProgressString(UUID uuid) {
        return handler.getNumberOfActiveQuests(uuid) + "/" + activeQuests;
    }

    @Override
    protected boolean meetsRequirement(UUID uuid) {
        return handler.getNumberOfActiveQuests(uuid) >= activeQuests;
    }

    @Override
    public boolean initRequirement(final String[] options) {

        // Add dependency
        addDependency(Library.QUESTS);
        addDependency(Library.QUESTS_ALTERNATIVE);

        handler = this.getDependencyManager().getQuestsPlugin().orElse(null);

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

        return handler != null;
    }

    @Override
    public double getProgressPercentage(UUID uuid) {
        return handler.getNumberOfActiveQuests(uuid) * 1.0d / activeQuests;
    }
}
