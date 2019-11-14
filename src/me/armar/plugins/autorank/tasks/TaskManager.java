package me.armar.plugins.autorank.tasks;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.playtimes.PlayTimeManager;
import me.armar.plugins.autorank.playtimes.UpdateTimePlayedTask;
import me.armar.plugins.autorank.util.AutorankTools;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * This class manages periodic tasks that need to be executed or killed.
 */
public class TaskManager {

    private Autorank plugin;

    private Map<UUID, Integer> updatePlayTimeTaskIds = new HashMap<>();

    public TaskManager(Autorank plugin) {
        this.plugin = plugin;
    }

    public void startUpdatePlayTimeTask(UUID uuid) {

        // Do not start a task if there is already one running
        if (updatePlayTimeTaskIds.containsKey(uuid)) {
            return;
        }

        BukkitTask task = plugin.getServer().getScheduler().runTaskTimer(plugin, new UpdateTimePlayedTask(plugin, uuid)
                , PlayTimeManager.INTERVAL_MINUTES * AutorankTools.TICKS_PER_MINUTE, PlayTimeManager
                .INTERVAL_MINUTES * AutorankTools.TICKS_PER_MINUTE);

        // Store taskID so we can refer to it later.
        updatePlayTimeTaskIds.put(uuid, task.getTaskId());

        plugin.debugMessage("Registered update play time task for player " + uuid + " (" + task.getTaskId() + ").");
    }

    public void stopUpdatePlayTimeTask(UUID uuid) {

        plugin.debugMessage("Stop update play time task for player " + uuid);

        // If no task is running, we can't kill it, so ignore the call.
        if (!updatePlayTimeTaskIds.containsKey(uuid)) {
            return;
        }

        // Cancel task
        plugin.getServer().getScheduler().cancelTask(updatePlayTimeTaskIds.get(uuid));

        // Remove it from registry so we can re-register if we ever want to.
        updatePlayTimeTaskIds.remove(uuid);
    }

}
