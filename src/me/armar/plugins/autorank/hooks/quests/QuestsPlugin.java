package me.armar.plugins.autorank.hooks.quests;

import java.util.UUID;

public interface QuestsPlugin {

    int getNumberOfActiveQuests(UUID uuid);

    int getNumberOfCompletedQuests(UUID uuid);

    boolean hasCompletedQuest(UUID uuid, String questName);

}
