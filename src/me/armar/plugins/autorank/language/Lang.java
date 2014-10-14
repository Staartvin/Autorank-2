package me.armar.plugins.autorank.language;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Every enumeration value has its path and default value.
 * To get the path, do {@link #getPath()}.
 * To get the default value, do {@link #getDefault()}.
 * 
 * For the defined value in the lang.yml config, use
 * {@link #getConfigValue(String... args)}.
 * String objects are expected as input.
 * 
 * @author Staartvin and gomeow
 * 
 */
public enum Lang {
	/**
	 * You have already completed this requirement!
	 */
	ALREADY_COMPLETED_REQUIREMENT("already-completed-requirement",
			"You have already completed this requirement!"),
	/**
	 * Autorank has been reloaded.
	 */
	AUTORANK_RELOADED("autorank-reloaded", "&3Autorank has been reloaded."),
	/**
	 * Travel at least {0} {1}.
	 */
	BLOCKS_MOVED_REQUIREMENT("blocks-moved-requirement",
			"Travel at least {0} {1}."),
	/**
	 * Break at least {0}.
	 */
	BROKEN_BLOCKS_REQUIREMENT("broken-blocks-requirement",
			"Break at least {0}."),
	/**
	 * Cannot check for console!
	 */
	CANNOT_CHECK_CONSOLE("cannot-check-console", "&cCannot check for console!"),
	/**
	 * Take at least {0} damage
	 */
	DAMAGE_TAKEN_REQUIREMENT("damage-taken-requirement",
			"Take at least {0} damage."),
	/**
	 * New data has been imported!
	 */
	DATA_IMPORTED("data-imported", "New data has been imported!"),
	/**
	 * days
	 */
	DAY_PLURAL("day-plural", "days"),
	/**
	 * day
	 */
	DAY_SINGULAR("day-singular", "day"),
	/**
	 * You do not meet requirements for #{0}:
	 */
	DO_NOT_MEET_REQUIREMENTS_FOR("do-not-meet-requirements-for",
			"You do not meet requirements for #&6{0}&c:"),
	/**
	 * Done
	 */
	DONE_MARKER("done-marker", "Done"),
	/**
	 * Have at least level {0} in exp.
	 */
	EXP_REQUIREMENT("exp-requirement", "Have at least level {0} in exp."),
	/**
	 * Have at least {0} power in your faction.
	 */
	FACTIONS_POWER_REQUIREMENT("factions-power-requirement",
			"Have at least {0} power in your faction."),
	/**
	 * Catch at least {0} fish.
	 */
	FISH_CAUGHT_REQUIREMENT("fish-caught-requirement",
			"Catch at least {0} fish."),
	/**
	 * Be in gamemode {0}.
	 */
	GAMEMODE_REQUIREMENT("gamemode-requirement", "Be in gamemode {0}."),
	/**
	 * Play for at least {0} on any of the servers.
	 */
	GLOBAL_TIME_REQUIREMENT("global-time-requirement",
			"Play for at least {0} on any of the servers."),
	/**
	 * hours
	 */
	HOUR_PLURAL("hour-plural", "hours"),
	/**
	 * hour
	 */
	HOUR_SINGULAR("hour-singular", "hour"),
	/**
	 * Invalid format, use {0}.
	 */
	INVALID_FORMAT("invalid-format", "&cInvalid format, use {0}."),
	/**
	 * {0} is not a valid number!
	 */
	INVALID_NUMBER("invalid-number", "{0} is not a valid number!"),
	/**
	 * Obtain {0}.
	 */
	ITEM_REQUIREMENT("item-requirement", "Obtain {0}."),
	/**
	 * Craft at least {0} item(s).
	 */
	ITEMS_CRAFTED_REQUIREMENT("items-crafted-requirement",
			"Craft at least {0} item(s)."),
	/**
	 * Be at {0}.
	 */
	LOCATION_REQUIREMENT("location-requirement", "Be at {0}."),
	/**
	 * Have at least power level {0}.
	 */
	MCMMO_POWER_LEVEL_REQUIREMENT("mcmmo-power-level-requirement",
			"Have at least power level {0}."),
	/**
	 * Have at least level {0} in {1}.
	 */
	MCMMO_SKILL_LEVEL_REQUIREMENT("mcmmo-skill-level-requirement",
			"Have at least level {0} in {1}."),
	/**
	 * meets all the requirements for rank {0}
	 */
	MEETS_ALL_REQUIREMENTS("meets-all-requirements",
			"meets all the requirements for rank {0}"),
	/**
	 * meets all the requirements
	 */
	MEETS_ALL_REQUIREMENTS_WITHOUT_RANK_UP(
			"meets-all-requirements-without-rank-up",
			"meets all the requirements"),
	/**
	 * minutes
	 */
	MINUTE_PLURAL("minute-plural", "minutes"),
	/**
	 * minute
	 */
	MINUTE_SINGULAR("minute-singular", "minute"),
	/**
	 * Have at least {0}.
	 */
	MONEY_REQUIREMENT("money-requirement", "Have at least {0}."),
	/**
	 * MySQL is not enabled and therefore global time does not exist!
	 */
	MYSQL_IS_NOT_ENABLED("mysql-is-not-enabled",
			"MySQL is not enabled and therefore global time does not exist!"),
	/**
	 * no groups
	 */
	NO_GROUPS("no-groups", "no groups"),
	/**
	 * and does not have a next rankup
	 */
	NO_NEXT_RANK("no-next-rankup", "Does not have a next rankup."),
	/**
	 * You need ({0}) for that!
	 */
	NO_PERMISSION("no-permission", "&cYou need ({0}) for that!"),
	/**
	 * Optional
	 */
	OPTIONAL_MARKER("optional-marker", "Optional"),
	/**
	 * Have permission '{0}'.
	 */
	PERMISSION_REQUIREMENT("permission-requirement", "Have permission '{0}'."),
	/**
	 * Place at least {0}.
	 */
	PLACED_BLOCKS_REQUIREMENT("placed-blocks-requirement",
			"Place at least {0}."),
	/**
	 * {0} is excluded from ranking!
	 */
	PLAYER_IS_EXCLUDED("player-is-excluded", "{0} is excluded from ranking!"),
	/**
	 * {0} has never been logged before.
	 */
	PLAYER_IS_INVALID("player-is-invalid",
			"&6{0}&4 has never been logged before."),
	/**
	 * Kill at least {0} player(s).
	 */
	PLAYER_KILLS_REQUIREMENT("player-kills-requirement",
			"Kill at least {0} player(s)."),
	/**
	 * {0} is not online!
	 */
	PLAYER_NOT_ONLINE("player-not-online", "&6{0}&4 is not online!"),
	/**
	 * Changed playtime of {0} to {1}.
	 */
	PLAYTIME_CHANGED("playtime-changed", "Changed playtime of {0} to {1}."),
	/**
	 * and will now be ranked up.
	 */
	RANKED_UP_NOW("ranked-up-now", " and will now be ranked up."),
	/**
	 * minutes
	 */
	SECOND_PLURAL("second-plural", "seconds"),
	/**
	 * minute
	 */
	SECOND_SINGULAR("second-singular", "second"),
	/**
	 * You have successfully completed requirement #{0}:
	 */
	SUCCESSFULLY_COMPLETED_REQUIREMENT("successfully-completed-requirement",
			"You have successfully completed requirement &6{0}&a:"),
	/**
	 * Play for at least {0}.
	 */
	TIME_REQUIREMENT("time-requirement", "Play for at least {0}."),
	/**
	 * Shear at least {0} sheep.
	 */
	TIMES_SHEARED_REQUIREMENT("times-sheared-requirement",
			"Shear at least {0} sheep."),
	/**
	 * Kill at least {0}.
	 */
	TOTAL_MOBS_KILLED_REQUIREMENT("total-mobs-killed-requirement",
			"Kill at least {0}."),
	/**
	 * Be with this server for at least {0}.
	 */
	TOTAL_TIME_REQUIREMENT("total-time-requirement",
			"Be with this server for at least {0}."),
	/**
	 * Player {0} is unknown and couldn't be identified.
	 */
	UNKNOWN_PLAYER("unknown-player",
			"&cPlayer {0} is unknown and couldn't be identified."),
	/**
	 * Vote at least {0} times.
	 */
	VOTE_REQUIREMENT("vote-requirement", "Vote at least {0} times."),
	/**
	 * Be in region '{0}'.
	 */
	WORLD_GUARD_REGION_REQUIREMENT("world-guard-region-requirement",
			"Be in region '{0}'."),
	/**
	 * Be in {0}.
	 */
	WORLD_REQUIREMENT("world-requirement", "Be in {0}."),
	/**
	 * Progress of requirement {0}:
	 */
	REQUIREMENT_PROGRESS("requirement-progress", "Progress of requirement {0}:"), ;

	private static FileConfiguration LANG;

	/**
	 * Set the {@code FileConfiguration} to use.
	 * 
	 * @param config The config to set.
	 */
	public static void setFile(final FileConfiguration config) {
		LANG = config;
	}

	private String path, def;

	/**
	 * Lang enum constructor.
	 * 
	 * @param path The string path.
	 * @param start The default string.
	 */
	Lang(final String path, final String start) {
		this.path = path;
		this.def = start;
	}

	/**
	 * Get the value in the config with certain arguments
	 * 
	 * @param args arguments that need to be given. (Can be null)
	 * @return value in config or otherwise default value
	 */
	public String getConfigValue(final String... args) {
		String value = ChatColor.translateAlternateColorCodes('&',
				LANG.getString(this.path, this.def));

		if (args == null)
			return value;
		else {
			if (args.length == 0)
				return value;

			for (int i = 0; i < args.length; i++) {
				value = value.replace("{" + i + "}", args[i]);
			}
		}

		return value;
	}

	/**
	 * Get the default value of the path.
	 * 
	 * @return The default value of the path.
	 */
	public String getDefault() {
		return this.def;
	}

	/**
	 * Get the path to the string.
	 * 
	 * @return The path to the string.
	 */
	public String getPath() {
		return this.path;
	}
}
