package me.armar.plugins.autorank.rankbuilder.builders;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;

import me.armar.plugins.autorank.playerchecker.result.Result;
import net.md_5.bungee.api.ChatColor;

public class ResultBuilder {

	private final Map<String, Class<? extends Result>> results = new HashMap<String, Class<? extends Result>>();

	public Result create(final String type) {
		Result res = null;
		final Class<? extends Result> c = results.get(type);
		if (c != null) {
			try {
				res = c.newInstance();
			} catch (final Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			Bukkit.getServer().getConsoleSender()
					.sendMessage("[Autorank] " + ChatColor.RED + "Result '" + type + "' is not a valid result type!");
			return null;
		}
		return res;
	}

	public void registerResult(final String type, final Class<? extends Result> result) {
		results.put(type, result);
	}

}
