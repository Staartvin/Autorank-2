package me.armar.plugins.autorank.hooks.quests;

import me.staartvin.utils.pluginlibrary.autorank.hooks.QuestsHook;

import java.util.UUID;

public class Quests implements QuestsPlugin {

    private final QuestsHook questsHook;

    public Quests(QuestsHook hook) {
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
