package me.armar.plugins.autorank.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * This class is used to work with the UUID's used by Minecraft.
 * <p>
 * Date created:  17:16:17
 * 5 mrt. 2014
 * @author Staartvin
 *
 */
public class UUIDUtil {

	
	/**
	 * Get a player by its UUID.
	 * <br>
	 * The player has to be online.
	 * @param uuid uuid of the player (including hyphens)
	 * @return player object corresponding to this UUID, or null if no player with that UUID was found.
	 */
	public static Player getOnlinePlayer(String uuid) {
		for (Player p: Bukkit.getOnlinePlayers()) {
			if (p.getUniqueId().toString().equals(uuid)) {
				return p;
			}
		}
		
		return null;
	}
}
