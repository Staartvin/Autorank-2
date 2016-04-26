package me.armar.plugins.autorank.rankbuilder.builders;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;

import me.armar.plugins.autorank.playerchecker.requirement.Requirement;
import me.armar.plugins.autorank.util.AutorankTools;
import net.md_5.bungee.api.ChatColor;

public class RequirementBuilder {

	private final Map<String, Class<? extends Requirement>> reqs = new HashMap<String, Class<? extends Requirement>>();

	public Requirement create(final String type) {
		Requirement res = null;
		final Class<? extends Requirement> c = reqs.get(type);
		if (c != null)
			try {
				res = c.newInstance();
			} catch (final Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		else {
			Bukkit.getServer().getConsoleSender().sendMessage(
					"[Autorank] " + ChatColor.RED + "Requirement '" + type + "' is not a valid requirement type!");
			return null;
		}
		return res;
	}

	public void registerRequirement(final String type, final Class<? extends Requirement> requirement) {
		// Add type to the list
		reqs.put(type, requirement);

		// Add type to the list of AutorankTools so it can use the correct name.
		AutorankTools.registerRequirement(type);

	}

}
