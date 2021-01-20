package me.armar.plugins.autorank.hooks.quests;

import java.util.UUID;

public class QuestsAlternative implements QuestsPlugin {

    private final me.staartvin.utils.pluginlibrary.autorank.hooks.QuestsAlternative questsHook;

    public QuestsAlternative(me.staartvin.utils.pluginlibrary.autorank.hooks.QuestsAlternative hook) {
        this.questsHook = hook;
    }

    @Override
    public int getNumberOfActiveQuests(UUID uuid) {
        return questsHook.getNumberOfActiveQuests(uuid);
    }

    @Override
    public int getNumberOfCompletedQuests(UUID uuid) {
        return questsHook.getNumberOfCompletedQuests(uuid);
    }

    @Override
    public boolean hasCompletedQuest(UUID uuid, String questName) {
        return questsHook.isQuestCompleted(uuid, questName);
    }
}
