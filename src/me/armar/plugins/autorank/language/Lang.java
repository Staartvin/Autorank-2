package me.armar.plugins.autorank.language;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Every enumeration value has its path and default value. To get the path, do
 * {@link #getPath()}. To get the default value, do {@link #getDefault()}.
 * 
 * For the defined value in the lang.yml config, use
 * {@link #getConfigValue(String... args)}. String objects are expected as
 * input.
 * 
 * @author Staartvin and gomeow
 * 
 */

public enum Lang {
    /**
     * Obtain at least {0} achievements
     */
    ACHIEVEMENT_MULTIPLE_REQUIREMENT("achievement-multiple-requirement", "Obtain at least {0} achievements"),
    /**
     * Obtain Achievement '{0}'
     */
    ACHIEVEMENT_SINGLE_REQUIREMENT("achievement-single-requirement", "Obtain Achievement '{0}'"),
    /**
     * Have at least an (acid) island level of {0}.
     */
    ACID_ISLAND_LEVEL_REQUIREMENT("acid-island-level-requirement", "Have at least an (acid) island level of {0}."),
    /**
     * but has already completed this path before.
     */
    ALREADY_COMPLETED_PATH("already-completed-path", "but has already completed this path before."),
    /**
     * &cYou have already completed this requirement!
     */
    ALREADY_COMPLETED_REQUIREMENT("already-completed-requirement", "&cYou have already completed this requirement!"),
    /**
     * &4You're already on this path!
     */
    ALREADY_ON_THIS_PATH("already-on-this-path", "&4You're already on this path!"),
    /**
     * and
     */
    AND("and", "and"),
    /**
     * &a----------- [Times of &6{0}&a] -----------
     */
    AR_TIMES_HEADER("ar-times-header", "&a----------- [Times of &6{0}&a] -----------"),
    /**
     * &6{0}&9 played:
     */
    AR_TIMES_PLAYER_PLAYED("ar-times-player-played", "&6{0}&9 played:"),
    /**
     * &eThis month: &d
     */
    AR_TIMES_THIS_MONTH("ar-times-this-month", "&eThis month: &d{0}"),
    /**
     * &cThis week: &d
     */
    AR_TIMES_THIS_WEEK("ar-times-this-week", "&cThis week: &d{0}"),
    /**
     * &3Today: &d
     */
    AR_TIMES_TODAY("ar-times-today", "&3Today: &d{0}"),
    /**
     * &aTotal: &d
     */
    AR_TIMES_TOTAL("ar-times-total", "&aTotal: &d{0}"),
    /**
     * Have at least an (skyblock) island level of {0}.
     */
    ASKYBLOCK_LEVEL_REQUIREMENT("askyblock-level-requirement", "Have at least an (skyblock) island level of {0}."),
    /**
     * &3Autorank's files have been reloaded.
     */
    AUTORANK_RELOADED("autorank-reloaded", "&3Autorank's files have been reloaded."),
    /**
     * Travel at least {0} {1}.
     */
    BLOCKS_MOVED_REQUIREMENT("blocks-moved-requirement", "Travel at least {0} {1}"),
    /**
     * Break at least {0}.
     */
    BROKEN_BLOCKS_REQUIREMENT("broken-blocks-requirement", "Break at least {0}"),
    /**
     * &cCannot check for console!
     */
    CANNOT_CHECK_CONSOLE("cannot-check-console", "&cCannot check for console!"),
    /**
     * &2You have chosen &7'{0}'&2.
     */
    CHOSEN_PATH("chosen-path", "&2You have chosen &7'{0}'&2."),
    /**
     * Perform the following command: {0}
     */
    COMMAND_RESULT("command-result", "Perform the following command(s): {0}"),
    /**
     * Take at least {0} damage
     */
    DAMAGE_TAKEN_REQUIREMENT("damage-taken-requirement", "Take at least {0} damage"),
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
     * You do not meet requirements for #&6{0}&c:
     */
    DO_NOT_MEET_REQUIREMENTS_FOR("do-not-meet-requirements-for", "You do not meet requirements for #&6{0}&c:"),
    /**
     * Done
     */
    DONE_MARKER("done-marker", "Done"),
    /**
     * Play {0} effect on you.
     */
    EFFECT_RESULT("effect-result", "Play '{0}' effect on you."),
    /**
     * Be from area '{0}'.
     */
    ESSENTIALS_GEOIP_LOCATION_REQUIREMENT("essentials-geoip-location-requirement", "Be from area {0}"),
    /**
     * Have at least level {0} in exp.
     */
    EXP_REQUIREMENT("exp-requirement", "Have at least level {0} in exp"),
    /**
     * Have at least {0} power in your faction.
     */
    FACTIONS_POWER_REQUIREMENT("factions-power-requirement", "Have at least {0} power in your faction"),
    /**
     * Catch at least {0} fish.
     */
    FISH_CAUGHT_REQUIREMENT("fish-caught-requirement", "Catch at least {0} fish"),
    /**
     * Eat at least '{0}'.
     */
    FOOD_EATEN_REQUIREMENT("food-eaten-requirement", "Eat at least {0}"),
    /**
     * Be in gamemode {0}.
     */
    GAMEMODE_REQUIREMENT("gamemode-requirement", "Be in gamemode {0}"),
    /**
     * Play for at least {0} on any of the servers.
     */
    GLOBAL_TIME_REQUIREMENT("global-time-requirement", "Play for at least {0} on any of the servers"),
    /**
     * Have at least {0} bonus blocks in GriefPrevention
     */
    GRIEF_PREVENTION_BONUS_BLOCKS_REQUIREMENT("grief-prevention-bonus-blocks-requirement", "Have at least {0} bonus blocks in GriefPrevention"),
    /**
     * Have at least {0} claimed blocks in GriefPrevention
     */
    GRIEF_PREVENTION_CLAIMED_BLOCKS_REQUIREMENT("grief-prevention-claimed-blocks-requirement", "Have at least {0} claimed blocks in GriefPrevention"),
    /**
     * Have at least {0} claims in GriefPrevention
     */
    GRIEF_PREVENTION_CLAIMS_COUNT_REQUIREMENT("grief-prevention-claims-count-requirement", "Have at least {0} claims in GriefPrevention"),
    /**
     * Have at least {0} remaining blocks to use in claims in GriefPrevention
     */
    GRIEF_PREVENTION_REMAINING_BLOCKS_REQUIREMENT("grief-prevention-remaining-blocks-requirement", "Have at least {0} remaining blocks to use in claims in GriefPrevention"),
    /**
     * &6{0}&7 has played for {1}.
     */
    HAS_PLAYED_FOR("has-played-for", "&6{0}&7 has played for {1}."),
    /**
     * hours
     */
    HOUR_PLURAL("hour-plural", "hours"),
    /**
     * hour
     */
    HOUR_SINGULAR("hour-singular", "hour"),
    /**
     * Be in biome '{0}'.
     */
    IN_BIOME_REQUIREMENT("in-biome-requirement", "Be in biome {0}"),
    /**
     * &cInvalid format, use {0}.
     */
    INVALID_FORMAT("invalid-format", "&cInvalid format, use {0}."),
    /**
     * &4You have not specified a valid leaderboard type! &eOnly 'total',
     * 'daily', 'weekly' and 'monthly' are allowed.
     */
    INVALID_LEADERBOARD_TYPE("invalid-leaderboard-type", "&4You have not specified a valid leaderboard type! &eOnly 'total', 'daily', 'weekly' and 'monthly' are allowed."),
    /**
     * &c{0} is not a valid number!
     */
    INVALID_NUMBER("invalid-number", "&c{0} is not a valid number!"),
    /**
     * Obtain {0}.
     */
    ITEM_REQUIREMENT("item-requirement", "Obtain {0}"),
    /**
     * Craft at least {0} item(s).
     */
    ITEMS_CRAFTED_REQUIREMENT("items-crafted-requirement", "Craft at least {0} item(s)"),
    /**
     * Have at least {0} points in Jobs
     */
    JOBS_CURRENT_POINTS_REQUIREMENT("jobs-current-points-requirement", "Have at least {0} points in Jobs"),
    /**
     * Have at least {0} experience in the job '{1}'
     */
    JOBS_EXPERIENCE_REQUIREMENT("jobs-experience-requirement", "Have at least {0} experience in the job '{1}'"),
    /**
     * Have at least level {0} in the job '{1}'
     */
    JOBS_LEVEL_REQUIREMENT("jobs-level-requirement", "Have at least level {0} in the job '{1}'"),
    /**
     * Have at least {0} points in total in Jobs
     */
    JOBS_TOTAL_POINTS_REQUIREMENT("jobs-total-points-requirement", "Have at least {0} points in total in Jobs"),
    /**
     * &a------------------------------------
     */
    LEADERBOARD_FOOTER("leaderboard-footer", "&a------------------------------------"),
    /**
     * &a-------- Leaderboard (All time) --------
     */
    LEADERBOARD_HEADER_ALL_TIME("leaderboard-header-all-time", "&a-------- Leaderboard (All time) --------"),
    /**
     * &a-------- Leaderboard (Daily time) --------
     */
    LEADERBOARD_HEADER_DAILY("leaderboard-header-daily", "&a-------- Leaderboard (Daily time) --------"),
    /**
     * &a-------- Leaderboard (Monthly time) --------
     */
    LEADERBOARD_HEADER_MONTHLY("leaderboard-header-monthly", "&a-------- Leaderboard (Monthly time) --------"),
    /**
     * &a-------- Leaderboard (Weekly time) --------
     */
    LEADERBOARD_HEADER_WEEKLY("leaderboard-header-weekly", "&a-------- Leaderboard (Weekly time) --------"),
    /**
     * Be at {0}.
     */
    LOCATION_REQUIREMENT("location-requirement", "Be at {0}"),
    /**
     * Have at least power level {0}.
     */
    MCMMO_POWER_LEVEL_REQUIREMENT("mcmmo-power-level-requirement", "Have at least power level {0}"),
    /**
     * Have at least level {0} in {1}.
     */
    MCMMO_SKILL_LEVEL_REQUIREMENT("mcmmo-skill-level-requirement", "Have at least level {0} in {1}"),
    /**
     * meets all the requirements for path {0}
     */
    MEETS_ALL_REQUIREMENTS("meets-all-requirements", "meets all the requirements for path {0}"),
    /**
     * meets all the requirements
     */
    MEETS_ALL_REQUIREMENTS_WITHOUT_RANK_UP("meets-all-requirements-without-rank-up", "meets all the requirements"),
    /**
     * Send you the following message: {0}
     */
    MESSAGE_RESULT("message-result", "Send you the following message: {0}"),
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
    MONEY_REQUIREMENT("money-requirement", "Have at least {0}"),
    /**
     * &cMySQL is not enabled!
     */
    MYSQL_IS_NOT_ENABLED("mysql-is-not-enabled", "&cMySQL is not enabled!"),
    /**
     * none (no path found)
     */
    NO_FURTHER_PATH_FOUND("no-further-path-found", "none (no path found)"),
    /**
     * &4There was no ranking path found with that name.
     */
    NO_PATH_FOUND_WITH_THAT_NAME("no-path-found-with-that-name", "&4There was no ranking path found with that name."),
    /**
     * &cYou need ({0}) for that!
     */
    NO_PERMISSION("no-permission", "&cYou need ({0}) for that!"),
    /**
     * Optional
     */
    OPTIONAL_MARKER("optional-marker", "Optional"),
    /**
     * &4You cannot use this command as this server has not enabled partial
     * completion!
     */
    PARTIAL_COMPLETION_NOT_ENABLED("partial-completion-not-enabled", "&4You cannot use this command as this server has not enabled partial completion!"),
    /**
     * Have permission '{0}'.
     */
    PERMISSION_REQUIREMENT("permission-requirement", "Have permission {0}"),
    /**
     * Place at least {0}.
     */
    PLACED_BLOCKS_REQUIREMENT("placed-blocks-requirement", "Place at least {0}"),
    /**
     * &c{0} is excluded from ranking!
     */
    PLAYER_IS_EXCLUDED("player-is-excluded", "&c{0} is excluded from ranking!"),
    /**
     * &6{0}&c has never been logged before.
     */
    PLAYER_IS_INVALID("player-is-invalid", "&6{0}&4 has never been logged before."),
    /**
     * Kill at least {0} player(s).
     */
    PLAYER_KILLS_REQUIREMENT("player-kills-requirement", "Kill at least {0} player(s)"),
    /**
     * &6{0}&c is not online!
     */
    PLAYER_NOT_ONLINE("player-not-online", "&6{0}&4 is not online!"),
    /**
     * Changed playtime of {0} to {1}.
     */
    PLAYTIME_CHANGED("playtime-changed", "Changed playtime of {0} to {1}."),
    /**
     * &eYour progress for the rank is reset.
     */
    PROGRESS_RESET("progress-reset", "&eYour progress for the path is reset."),
    /**
     * Change rank to {0}.
     */
    RANK_CHANGE_RESULT("rank-change-result", "Change rank to {0}."),
    /**
     * and now completed his path.
     */
    COMPLETED_PATH_NOW("completed-path-now", " and now completed his path."),
    /**
     * Progress of requirement {0}:
     */
    REQUIREMENT_PROGRESS("requirement-progress", "Progress of requirement {0}:"),
    /**
     * &6[Autorank] &5A new day has arrived! &eAll daily times have been reset.
     */
    RESET_DAILY_TIME("reset-daily-time", "&6[Autorank] &5A new day has arrived! &eAll daily times have been reset."),
    /**
     * &6[Autorank] &5A new day has arrived! &eAll daily times have been reset.
     */
    RESET_MONTHLY_TIME("reset-monthly-time", "&6[Autorank] &5A new month has arrived! &eAll monthly times have been reset."),
    /**
     * &6[Autorank] &5A new day has arrived! &eAll daily times have been reset.
     */
    RESET_WEEKLY_TIME("reset-weekly-time", "&6[Autorank] &5A new week has arrived! &eAll weekly times have been reset."),
    /**
     * Have at least a combat level of {0} in RPGme.
     */
    RPGME_COMBAT_LEVEL_REQUIREMENT("rpgme-combat-level-requirement", "Have at least a combat level of {0} in RPGme."),
    /**
     * Have at least a combined level {0} of RPGMe.
     */
    RPGME_POWER_LEVEL_REQUIREMENT("rpgme-power-level-requirement", "Have at least a combined level {0} of RPGMe."),
    /**
     * Have at least level {0} in {1} in RPGme.
     */
    RPGME_SKILL_LEVEL_REQUIREMENT("rpgme-skill-level-requirement", "Have at least level {0} in {1} in RPGme."),
    /**
     * minutes
     */
    SECOND_PLURAL("second-plural", "seconds"),
    /**
     * minute
     */
    SECOND_SINGULAR("second-singular", "second"),
    /**
     * Spawn firework at {0}.
     */
    SPAWN_FIREWORK_RESULT("spawn-firework-result", "Spawn firework at {0}."),
    /**
     * You have successfully completed requirement #{0}:
     */
    SUCCESSFULLY_COMPLETED_REQUIREMENT("successfully-completed-requirement", "You have successfully completed requirement &6{0}&a:"),
    /**
     * Get teleported to {0}.
     */
    TELEPORT_RESULT("teleport-result", "Get teleported to {0}."),
    /**
     * Play for at least {0}.
     */
    TIME_REQUIREMENT("time-requirement", "Play for at least {0}"),
    /**
     * Shear at least {0} sheep.
     */
    TIMES_SHEARED_REQUIREMENT("times-sheared-requirement", "Shear at least {0} sheep"),
    /**
     * Kill at least {0}.
     */
    TOTAL_MOBS_KILLED_REQUIREMENT("total-mobs-killed-requirement", "Kill at least {0}"),
    /**
     * Be with this server for at least {0}.
     */
    TOTAL_TIME_REQUIREMENT("total-time-requirement", "Be with this server for at least {0}"),
    /**
     * &cPlayer {0} is unknown and couldn't be identified.
     */
    UNKNOWN_PLAYER("unknown-player", "&cPlayer {0} is unknown and couldn't be identified."),
    /**
     * Vote at least {0} times.
     */
    VOTE_REQUIREMENT("vote-requirement", "Vote at least {0} times"),
    /**
     * Be in region '{0}'.
     */
    WORLD_GUARD_REGION_REQUIREMENT("world-guard-region-requirement", "Be in region {0}"),
    /**
     * Be in {0}.
     */
    WORLD_REQUIREMENT("world-requirement", "Be in {0}"),
    /**
     * &cYou are a robot, '{0}'
     */
    YOU_ARE_A_ROBOT("you-are-a-robot", "&cYou are a robot, {0}"),
    /**
     * Be in group {0}.
     */
    GROUP_REQUIREMENT("group-requirement", "Be in group {0}."),
    /**
     * Reward you with {0}.
     */
    MONEY_RESULT("money-result", "Reward you with {0}."),
    /**
     * &2You have automatically been assigned the path '&6{0}&2'.
     */
    AUTOMATICALLY_ASSIGNED_PATH("automatically-assigned-path", "&2You have automatically been assigned the path '&6{0}&2'."),
    /**
     * &2You completed requirement &6{0}&2: &3{1}
     */
    COMPLETED_REQUIREMENT("completed-requirement", "&2You completed requirement &6{0}&2: &3{1}"),
    /**
     * &2{0} has played for {1} and currently has no path. There are no paths
     * left to choose.
     */
    NO_PATH_LEFT_TO_CHOOSE("no-path-left-to-choose", "&2{0} has played for {1} and currently has no path. There are no paths left to choose."),
    /**
     * &4You already completed this path before. You are not allowed to retake
     * it!
     */
    PATH_NOT_ALLOWED_TO_RETAKE("path-not-allowed-to-retake", "&4You already completed this path before. You are not allowed to retake it!"),
    /**
     * &4There are no paths that you can choose.
     */
    NO_PATHS_TO_CHOOSE("no-paths-to-choose", "&4There are no paths that you can choose."),;

    private static FileConfiguration LANG;

    /**
     * Set the {@code FileConfiguration} to use.
     * 
     * @param config
     *            The config to set.
     */
    public static void setFile(final FileConfiguration config) {
        LANG = config;
    }

    private String path, def;

    /**
     * Lang enum constructor.
     * 
     * @param path
     *            The string path.
     * @param start
     *            The default string.
     */
    Lang(final String path, final String start) {
        this.path = path;
        this.def = start;
    }

    /**
     * Get the value in the config with certain arguments.
     * 
     * @param args
     *            arguments that need to be given. (Can be null)
     * @return value in config or otherwise default value
     */
    public String getConfigValue(final Object... args) {
        String value = ChatColor.translateAlternateColorCodes('&', LANG.getString(this.path, this.def));

        if (args == null)
            return value;
        else {
            if (args.length == 0)
                return value;

            for (int i = 0; i < args.length; i++) {
                value = value.replace("{" + i + "}", args[i].toString());
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
