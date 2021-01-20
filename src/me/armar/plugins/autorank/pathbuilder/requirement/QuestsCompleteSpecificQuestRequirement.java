package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.hooks.quests.QuestsPlugin;
import me.armar.plugins.autorank.language.Lang;
import me.staartvin.utils.pluginlibrary.autorank.Library;

import java.util.UUID;

public class QuestsCompleteSpecificQuestRequirement extends AbstractRequirement {

    private QuestsPlugin handler = null;
    private String questName = null;

    @Override
    public String getDescription() {
        return Lang.QUESTS_COMPLETE_SPECIFIC_QUEST_REQUIREMENT.getConfigValue(questName);
    }

    @Override
    public String getProgressString(UUID uuid) {
        return handler.hasCompletedQuest(uuid, questName) + "";
    }

    @Override
    protected boolean meetsRequirement(UUID uuid) {
        return handler.hasCompletedQuest(uuid, questName);
    }

    @Override
    public boolean initRequirement(final String[] options) {

        // Add dependency
        addDependency(Library.QUESTS);
        addDependency(Library.QUESTS_ALTERNATIVE);

        handler = getDependencyManager().getQuestsPlugin().orElse(null);

        if (options.length > 0) {
            questName = options[0];
        } else {
            this.registerWarningMessage("No quest name was provided.");
            return false;
        }

        if (handler == null) {
            this.registerWarningMessage("There is no Quests plugin available!");
            return false;
        }

        return true;
    }
}
