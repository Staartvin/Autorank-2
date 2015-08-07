package me.armar.plugins.autorank.hooks.mcmmoapi;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.hooks.DependencyHandler;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.api.ExperienceAPI;

/**
 * Handles all connections with McMMO.
 * <p>
 * Date created: 17:50:11 4 mrt. 2014
 * 
 * @author Staartvin
 * 
 */
public class McMMOHandler implements DependencyHandler {

	private mcMMO api;
	private final Autorank plugin;

	public McMMOHandler(final Autorank instance) {
		plugin = instance;
	}

	@Override
	public Plugin get() {
		final Plugin plugin = this.plugin.getServer().getPluginManager()
				.getPlugin("mcMMO");

		if (plugin == null || !(plugin instanceof mcMMO)) {
			return null; // Maybe you want throw an exception instead
		}

		return plugin;
	}

	public int getPowerLevel(final Player player) {
		int powerLevel = 0;

		if (!isAvailable())
			return powerLevel;

		try {
			powerLevel = ExperienceAPI.getPowerLevel(player);
		} catch (Exception e) {
			
			/*
			Class<? extends Exception> error = e.getClass();
			String errorMessage = error.toString().toLowerCase();

			if (errorMessage.contains("mcmmoplayernotfound")) {
				plugin.getLogger()
						.severe("Could not get user '"
								+ player.getName()
								+ "' of McMMO. Report McMMOPlayerNotFoundException to mcmmo devs.");
			}*/
			
			// Instead of erroring, use cache value
			powerLevel = ExperienceAPI.getPowerLevelOffline(player.getUniqueId());
			
		}

		return powerLevel;
	}

	/**
	 * Get the level of a certain skill of a player that is using mcMMO.
	 * 
	 * @param player Player to get the skill from
	 * @param skillName Name of the skill
	 * @return level of requested skill, or -1 if the skill is invalid.
	 */
	public int getSkillLevel(final Player player, final String skillName) {
		int skillLevel = 0;

		if (!isAvailable())
			return skillLevel;

		try {
			skillLevel = ExperienceAPI.getLevel(player, skillName);
		} catch (Exception e) {
			Class<? extends Exception> error = e.getClass();
			String errorMessage = error.toString().toLowerCase();

			// Error only when invalid skill
			if (errorMessage.contains("invalidskill")) {
				plugin.getLogger().warning(
						"Skill '" + skillName + "' is not a valid skill!");
				return -1;
			} /*else if (errorMessage.contains("mcmmoplayernotfound")) {
				plugin.getLogger()
						.severe("Could not get user '"
								+ player.getName()
								+ "' of McMMO. Report McMMOPlayerNotFoundException to mcmmo devs.");
			} else {
				e.fillInStackTrace();
			}*/
			
			// instead of erroring, get cache value.
			skillLevel = ExperienceAPI.getLevelOffline(player.getUniqueId(), skillName);
		}

		return skillLevel;
	}

	/* (non-Javadoc)
	 * @see me.armar.plugins.autorank.hooks.DependencyHandler#isAvailable()
	 */
	@Override
	public boolean isAvailable() {
		return api != null;
	}

	/* (non-Javadoc)
	 * @see me.armar.plugins.autorank.hooks.DependencyHandler#isInstalled()
	 */
	@Override
	public boolean isInstalled() {
		final mcMMO plugin = (mcMMO) get();

		return plugin != null && plugin.isEnabled();
	}

	/* (non-Javadoc)
	 * @see me.armar.plugins.autorank.hooks.DependencyHandler#setup()
	 */
	@Override
	public boolean setup(final boolean verbose) {
		if (!isInstalled()) {
			if (verbose) {
				plugin.getLogger().info("mcMMO has not been found!");
			}
			return false;
		} else {

			api = (mcMMO) get();

			if (api != null) {
				if (verbose) {
					plugin.getLogger().info(
							"mcMMO has been found and can be used!");
				}

				return true;
			} else {
				if (verbose) {
					plugin.getLogger().info(
							"mcMMO has been found but cannot be used!");
				}
				return false;
			}
		}
	}

}
