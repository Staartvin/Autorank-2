package me.armar.plugins.autorank.playerchecker.requirement;

import java.util.ArrayList;
import java.util.List;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.playerchecker.result.Result;
import org.bukkit.entity.Player;

public class PermissionRequirement extends Requirement {

	private String permission = null;
	private boolean optional = false;
	private boolean autoComplete = false;
	private int reqId;
	List<Result> results = new ArrayList<Result>();

	public PermissionRequirement() {
		super();
	}

	@Override
	public boolean setOptions(final String[] options, final boolean optional,
			final List<Result> results, final boolean autoComplete,
			final int reqId) {
		this.optional = optional;
		this.results = results;
		this.autoComplete = autoComplete;
		this.reqId = reqId;

		try {
			permission = options[0];
			return true;
		} catch (final Exception e) {
			permission = null;
			return false;
		}
	}

	@Override
	public boolean meetsRequirement(final Player player) {
		// TODO Auto-generated method stub
		if (isCompleted(getReqId(), player.getName())) {
			return true;
		}

		return permission != null && player.hasPermission(permission);
	}

	@Override
	public String getDescription() {
		return Lang.PERMISSION_REQUIREMENT
				.getConfigValue(new String[] { permission });
	}

	@Override
	public boolean isOptional() {
		return optional;
	}

	@Override
	public List<Result> getResults() {
		return results;
	}

	@Override
	public String getProgress(final Player player) {
		String progress = "unknown";
		return progress;
	}

	@Override
	public boolean useAutoCompletion() {
		return autoComplete;
	}

	@Override
	public int getReqId() {
		return reqId;
	}
}
