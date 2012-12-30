package me.armar.plugins.autorank.permissions;

import org.bukkit.entity.Player;

//import ru.tehkode.permissions.PermissionManager;
//import ru.tehkode.permissions.bukkit.PermissionsEx;

public class PermissionsEXHandler implements PermissionsPluginHandler {

	@Override
	public String[] getPlayerGroups(Player player) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean replaceGroup(Player player, String world, String oldGroup,
			String newGroup) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeGroup(Player player, String world, String group) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addGroup(Player player, String world, String group) {
		// TODO Auto-generated method stub
		return false;
	}

/*    private PermissionManager pex;

    public PermissionsEXHandler() {
	pex = PermissionsEx.getPermissionManager();
    }

    @Override
    public String[] getPlayerGroups(Player player) {
	return pex.getUser(player).getGroupsNames();
    }

    @Override
    public boolean replaceGroup(Player player, String world, String oldGroup, String newGroup) {
	removeGroup(player, world, oldGroup);
	addGroup(player, world, newGroup);
	return true;
    }

    @Override
    public boolean removeGroup(Player player, String world, String group) {
	if (world == null) {
	    pex.getUser(player).removeGroup(group);
	} else {
	    pex.getUser(player).removeGroup(group, world);
	}
	return true;
    }

    @Override
    public boolean addGroup(Player player, String world, String group) {
	if (world == null) {
	    pex.getUser(player).addGroup(group);
	} else {
	    pex.getUser(player).addGroup(group, world);
	}
	return true;
    }
*/
}
