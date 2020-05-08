package me.armar.plugins.autorank.pathbuilder.requirement;

import me.armar.plugins.autorank.language.Lang;
import me.staartvin.utils.pluginlibrary.Library;
import me.staartvin.utils.pluginlibrary.hooks.QuestsHook;

import java.util.UUID;

public class QuestsCompleteSpecificQuestRequirement extends AbstractRequirement {

    private QuestsHook handler = null;
    private String questName = null;

    @Override
    public String getDescription() {
        return Lang.QUESTS_COMPLETE_SPECIFIC_QUEST_REQUIREMENT.getConfigValue(questName);
    }

    @Override
    public String getProgressString(UUID uuid) {
        return handler.isQuestCompleted(uuid, questName) + "";
    }

    @Override
    protected boolean meetsRequirement(UUID uuid) {

        if (!handler.isHooked())
            return false;

        return handler.isQuestCompleted(uuid, questName);
    }

    @Override
    public boolean initRequirement(final String[] options) {

        // Add dependency
        addDependency(Library.QUESTS);

        handler = (QuestsHook) this.getDependencyManager().getLibraryHook(Library.QUESTS);

        if (options.length > 0) {
            questName = options[0];
        } else {
            this.registerWarningMessage("No quest name was provided.");
            return false;
        }

        if (handler == null || !handler.isHooked()) {
            this.registerWarningMessage("Quests is not available!");
            return false;
        }

        return true;
    }
}
