package me.armar.plugins.autorank.language;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Every enumeration value has its path and default value.
 * To get the path, do {@link #getPath()}.
 * To get the default value, do {@link #getDefault()}.
 * 
 * For the defined value in the lang.yml config, use {@link #getConfigValue(String[])}.
 * A string array is expected for the input. This can be null
 * @author Staartvin and gomeow
 *
 */
public enum Lang {
    PLAYER_NOT_ONLINE("player-not-online", "{0} is not online!"),
    AUTORANK_RELOADED("autorank-reloaded", "&3Autorank has been reloaded."),
    INVALID_FORMAT("invalid-format", "&cInvalid format, use {0}."),
    PLAYTIME_CHANGED("playtime_changed", "Changed playtime of {0} to {1}."),
    CANNOT_CHECK_CONSOLE("cannot-check-console", "&cCannot check for console!"),
    NO_PERMISSION("no-permission", "&cYou need ({0}) for that!"),
    HAS_PLAYED_FOR("has-played-for", " has played for "),
    IS_IN("is-in", "is in "),
    NO_GROUPS("no-groups", "no groups"),
    ONE_GROUP("one-group", "group "),
    MULTIPLE_GROUPS("multiple-groups", "groups "),
    NO_NEXT_RANK("no-next-rankup", "and does not have a next rankup"),
    MEETS_ALL_REQUIREMENTS("meets-all-requirements", "meets all the requirements for rank {0}"),
    RANKED_UP_NOW("ranked-up-now", " and will now be ranked up."),
    REQUIREMENTS_TO_RANK("requirements-to-rank", "Requirements to be ranked up: "),
    DATA_IMPORTED("data-imported", "New data has been imported!"),
    TIME_REQUIREMENT("time-requirement", "Play for at least {0} or higher."),
    WORLD_REQUIREMENT("world-requirement", "Be in {0}."),
    VOTE_REQUIREMENT("vote-requirement", "Vote at least {0} times."),
    MONEY_REQUIREMENT("money-requirement", "Have at least {0}."),
    BROKEN_BLOCKS_REQUIREMENT("broken-blocks-requirement", "Break at least {0}."),
    PLACED_BLOCKS_REQUIREMENT("placed-blocks-requirement", "Place at least {0}."),
    EXP_REQUIREMENT("exp-requirement", "Have at least level {0} in exp."),
    GAMEMODE_REQUIREMENT("gamemode-requirement", "Be in gamemode {0}."),
    ITEM_REQUIREMENT("Obtain {0}.", "item-requirement");

 
    private String path, def;
    private static FileConfiguration LANG;
    
    /**
    * Lang enum constructor.
    * @param path The string path.
    * @param start The default string.
    */
    Lang(String path, String start) {
        this.path = path;
        this.def = start;
    }
 
    /**
    * Set the {@code FileConfiguration} to use.
    * @param config The config to set.
    */
    public static void setFile(FileConfiguration config) {
        LANG = config;
    }
    
    /**
     * Get the value in the config with certain arguments
     * @param args arguments that need to be given. (Can be null)
     * @return value in config or otherwise default value
     */
    public String getConfigValue(String[] args) {
    	String value = ChatColor.translateAlternateColorCodes('&', LANG.getString(this.path, this.def));
    	
    	if (args == null) return value;
    	else {
    		if (args.length == 0) return value;
    		
    		for (int i=0;i<args.length;i++) {
    			value = value.replace("{" + i + "}" , args[i]);
    		}
    	}
    	
    	return value;
    }
 
    /**
    * Get the default value of the path.
    * @return The default value of the path.
    */
    public String getDefault() {
        return this.def;
    }
 
    /**
    * Get the path to the string.
    * @return The path to the string.
    */
    public String getPath() {
        return this.path;
    }
}
