package me.armar.plugins.autorank.permissions;

/**
 * This class represents a permission node that is used by Autorank to check whether a player is able to perform an action.
 *
 * @author Staartvin
 */
public final class AutorankPermission {

    public static final String ADD_LOCAL_TIME = "autorank.add";
    public static final String ARCHIVE_PLAYERS = "autorank.archive";
    public static final String CHECK_SELF = "autorank.check";
    public static final String CHECK_OTHERS = "autorank.checkothers";
    public static final String CHOOSE_PATH = "autorank.choose";
    public static final String COMPLETE_REQUIREMENT = "autorank.complete";
    public static final String CONVERT_TIME_DATA = "autorank.convert.data";
    public static final String CONVERT_PLAYER_DATA = "autorank.convert.playerdata";
    public static final String CONVERT_SIMPLE_CONFIG = "autorank.convert.simpleconfig";
    public static final String CONVERT_ADVANCED_CONFIG = "autorank.convert.advancedconfig";
    public static final String DEBUG_FILE = "autorank.debug";
    public static final String FORCE_CHECK = "autorank.forcecheck";
    public static final String ADD_GLOBAL_TIME = "autorank.gadd";
    public static final String CHECK_GLOBAL = "autorank.gcheck";
    public static final String EXCLUDE_FROM_PATHING = "autorank.exclude";
    public static final String SET_GLOBAL_TIME = "autorank.gset";
    public static final String HELP_PAGES = "autorank.help";
    public static final String SHOW_HOOKS = "autorank.hooks";
    public static final String IMPORT_DATA = "autorank.import";
    public static final String FORCE_UPDATE_LEADERBOARD = "autorank.leaderboard.force";
    public static final String BROADCAST_LEADERBOARD = "autorank.leaderboard.broadcast";
    public static final String VIEW_LEADERBOARD = "autorank.leaderboard";
    public static final String RELOAD_AUTORANK = "autorank.reload";
    public static final String REMOVE_LOCAL_TIME = "autorank.remove";
    public static final String RESET_DATA = "autorank.reset";
    public static final String SET_LOCAL_TIME = "autorank.set";
    public static final String SYNC_MYSQL_TABLE = "autorank.sync";
    public static final String SYNC_STATS_DATA = "autorank.syncstats";
    public static final String CHECK_TIME_PLAYED_SELF = "autorank.times.self";
    public static final String CHECK_TIME_PLAYED_OTHERS = "autorank.times.others";
    public static final String TRACK_REQUIREMENT = "autorank.track";
    public static final String VIEW_PATH = "autorank.view";
    public static final String EXCLUDE_FROM_TIME_UPDATES = "autorank.timeexclude";
    public static final String NOTICE_ON_UPDATE_AVAILABLE = "autorank.noticeonupdate";
    public static final String NOTICE_ON_WARNINGS = "autorank.noticeonwarning";
    public static final String EXCLUDE_FROM_LEADERBOARD = "autorank.leaderboard.exclude";
    public static final String BACKUP_DATA_FILES = "autorank.backup.data";

    private AutorankPermission() {
    }
}
