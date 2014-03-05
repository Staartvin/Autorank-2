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

	
	public static Player getOnlinePlayer(String uuid) {
		for (Player p: Bukkit.getOnlinePlayers()) {
			if (p.getUniqueId().toString().equals(uuid)) {
				return p;
			}
		}
		
		return null;
	}
}
