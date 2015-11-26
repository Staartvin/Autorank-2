package me.armar.plugins.autorank.playerchecker.result;

import java.util.ArrayList;
import java.util.UUID;

import me.armar.plugins.autorank.api.events.PlayerPromoteEvent;
import me.armar.plugins.autorank.util.uuid.UUIDManager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class RankChangeResult extends Result {

	String from = null;
	String to = null;
	String world = null;

	@Override
	public boolean applyResult(final Player player) {
		String oldrank = null;
		if (from == null) {
			return false;
		} else {
			oldrank = from;
		}
		if (world != null) {
			this.getAutorank()
					.getLogger()
					.info("Promote " + player.getName() + " on world " + world
							+ " from " + oldrank + " to " + to);
		} else {
			this.getAutorank()
					.getLogger()
					.info("Promote " + player.getName() + " globally from "
							+ oldrank + " to " + to);
		}

		// Call PlayerPromoteEvent

		// Create the event here
		final PlayerPromoteEvent event = new PlayerPromoteEvent(player, world,
				oldrank, to);
		// Call the event
		Bukkit.getServer().getPluginManager().callEvent(event);

		// Check if event is cancelled.
		if (event.isCancelled())
			return false;

		final UUID uuid = UUIDManager.getUUIDFromPlayer(player.getName());

		// When rank is changed: reset progress and update last known group
		getAutorank().getPlayerDataHandler().setPlayerProgress(uuid,
				new ArrayList<Integer>());

		getAutorank().getPlayerDataHandler().setLastKnownGroup(uuid, to);

		// Reset chosen path as the player is moved to another group
		getAutorank().getPlayerDataHandler().setChosenPath(uuid, null);

		return this.getAutorank().getPermPlugHandler().getPermissionPlugin()
				.replaceGroup(player, world, oldrank, to);
	}

	@Override
	public boolean setOptions(final String[] options) {
		//1 arg -> from group that the rank is being applied for to arg 0
		if (options.length == 1) {
			to = options[0].trim();
		}
		//2 args -> from arg 0 to arg 1
		if (options.length == 2) {
			from = options[0].trim();
			to = options[1].trim();
		}
		//3 args -> from arg 0 to arg 1 in world arg 2
		if (options.length == 3) {
			from = options[0].trim();
			to = options[1].trim();
			world = options[2].trim();
		}

		return to != null;
	}

}
