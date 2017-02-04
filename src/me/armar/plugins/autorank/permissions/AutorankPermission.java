package me.armar.plugins.autorank.permissions;

/**
 * This class represents a permission node that is used by Autorank to check whether a player is able to perform an action.
 * @author Staartvin
 */
public enum AutorankPermission {

    
    ADD_LOCAL_TIME("autorank.add"),
    ARCHIVE_PLAYERS("autorank.archive"),
    CHECK_SELF("autorank.check"),
    CHECK_OTHERS("autorank.checkothers"),
    CHOOSE_PATH("autorank.choose"),
    COMPLETE_REQUIREMENT("autorank.complete"),
    CONVERT_TIME_DATA("autorank.convert.data"),
    CONVERT_PLAYER_DATA("autorank.convert.playerdata"),
    DEBUG_FILE("autorank.debug"),
    FORCE_CHECK("autorank.forcecheck"),
    ADD_GLOBAL_TIME("autorank.gadd"),
    CHECK_GLOBAL("autorank.gcheck"),
    EXCLUDE_FROM_PATHING("autorank.exclude"),
    SET_GLOBAL_TIME("autorank.gset"),
    HELP_PAGES("autorank.help"),
    SHOW_HOOKS("autorank.hooks"),
    IMPORT_DATA("autorank.import"),
    FORCE_UPDATE_LEADERBOARD("autorank.leaderboard.force"),
    BROADCAST_LEADERBOARD("autorank.leaderboard.broadcast"),
    VIEW_LEADERBOARD("autorank.leaderboard"),
    RELOAD_AUTORANK("autorank.reload"),
    REMOVE_LOCAL_TIME("autorank.remove"),
    RESET_DATA("autorank.reset"),
    SET_LOCAL_TIME("autorank.set"),
    SYNC_MYSQL_TABLE("autorank.sync"),
    SYNC_STATS_DATA("autorank.syncstats"),
    CHECK_TIME_PLAYED_SELF("autorank.times.self"),
    CHECK_TIME_PLAYED_OTHERS("autorank.times.others"),
    TRACK_REQUIREMENT("autorank.track"),
    VIEW_PATH("autorank.view"),
    EXCLUDE_FROM_TIME_UPDATES("autorank.timeexclude"),
    NOTICE_ON_UPDATE_AVAILABLE("autorank.noticeonupdate"),
    NOTICE_ON_WARNINGS("autorank.noticeonwarning"),
    EXCLUDE_FROM_LEADERBOARD("autorank.leaderboard.exclude")
    
    ;
    
    private String permissionString;
    
    AutorankPermission (String permissionString) {
        this.permissionString = permissionString;
    }
    
    /**
     * Get the permission string for this permission.
     * @return
     */
    public String getPermissionString() {
        return this.permissionString;
    }
    
    @Override
    public String toString() {
        return this.permissionString;
    }
    
   
}
