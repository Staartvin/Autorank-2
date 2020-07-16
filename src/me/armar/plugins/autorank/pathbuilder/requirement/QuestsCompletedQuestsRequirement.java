package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.hooks.quests.QuestsPlugin;
import me.armar.plugins.autorank.language.Lang;
import me.staartvin.utils.pluginlibrary.Library;

import java.util.UUID;

public class QuestsCompletedQuestsRequirement extends AbstractRequirement {

    private QuestsPlugin handler = null;
    private int completedQuests = -1;

    @Override
    public String getDescription() {
        return Lang.QUESTS_COMPLETED_QUESTS_REQUIREMENT.getConfigValue(completedQuests);
    }

    @Override
    public String getProgressString(UUID uuid) {
        return handler.getNumberOfCompletedQuests(uuid) + "/" + completedQuests;
    }

    @Override
    protected boolean meetsRequirement(UUID uuid) {
        return handler.getNumberOfCompletedQuests(uuid) >= completedQuests;
    }

    @Override
    public boolean initRequirement(final String[] options) {

        // Add dependency
        addDependency(Library.QUESTS);
        addDependency(Library.QUESTS_ALTERNATIVE);

        handler = getDependencyManager().getQuestsPlugin().orElse(null);

        if (options.length > 0) {
            try {
                completedQuests = Integer.parseInt(options[0]);
            } catch (NumberFormatException e) {
                this.registerWarningMessage("An invalid number is provided");
                return false;
            }
        }

        if (completedQuests < 0) {
            this.registerWarningMessage("No number is provided or smaller than 0.");
            return false;
        }

        return handler != null;
    }

    @Override
    public double getProgressPercentage(UUID uuid) {
        return handler.getNumberOfCompletedQuests(uuid) * 1.0d / completedQuests;
    }
}
