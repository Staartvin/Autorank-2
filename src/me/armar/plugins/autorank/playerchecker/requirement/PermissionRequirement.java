package me.armar.plugins.autorank.playerchecker.requirement;

import java.util.ArrayList;
import java.util.List;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;

import org.bukkit.entity.Player;

public class PermissionRequirement extends Requirement {

	List<String> permissions = new ArrayList<String>();

	@Override
	public String getDescription() {
		return Lang.PERMISSION_REQUIREMENT
				.getConfigValue(AutorankTools.seperateList(permissions, "or"));
	}

	@Override
	public String getProgress(final Player player) {
		final String progress = "unknown";
		return progress;
	}

	@Override
	public boolean meetsRequirement(final Player player) {

		for (String perm: permissions) {
			if (player.hasPermission(perm)) return true;
		}
		
		return false;
	}

	@Override
	public boolean setOptions(List<String[]> optionsList) {
		
		for (String[] options: optionsList) {
			try {
				permissions.add(options[0]);
			} catch (final Exception e) {
				return false;
			}	
		}
		
		return !permissions.isEmpty();
	}
}
