package me.armar.plugins.autorank.playerchecker.result;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TeleportResult extends Result{

	private Location location;
	
	@Override
	public boolean setOptions(String[] options) {
		// x;y;z;world;yaw;pitch
		
		if (options.length < 4) return false;
		
		if (options.length == 6) {
			location = new Location(Bukkit.getServer().getWorld(options[3]), Integer.parseInt(options[0]), Integer.parseInt(options[1]), Integer.parseInt(options[2]), Float.parseFloat(options[4]), Float.parseFloat(options[5]));
		} else {
			location = new Location(Bukkit.getServer().getWorld(options[3]), Integer.parseInt(options[0]), Integer.parseInt(options[1]), Integer.parseInt(options[2]));
		}
		return location != null;
	}

	@Override
	public boolean applyResult(Player player) {
		if (player == null) return false;
		
		player.teleport(location);
		return location != null;
	}

}
