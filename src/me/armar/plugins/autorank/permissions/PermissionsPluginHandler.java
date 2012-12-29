package me.armar.plugins.autorank.permissions;

import org.bukkit.entity.Player;

public interface PermissionsPluginHandler {

    public String[] getPlayerGroups(Player player);
    public boolean replaceGroup(Player player, String world, String oldGroup, String newGroup);
    public boolean removeGroup(Player player, String world, String group);
    public boolean addGroup(Player player, String world, String group);
    
}
