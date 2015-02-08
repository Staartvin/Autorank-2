package me.armar.plugins.autorank.playerchecker.requirement;

import java.util.ArrayList;
import java.util.List;

import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.statsmanager.handlers.StatsHandler;
import me.armar.plugins.autorank.util.AutorankTools;

import org.bukkit.entity.Player;

public class PlayerKillsRequirement extends Requirement {

	private final List<Integer> totalPlayersKilled = new ArrayList<Integer>();

	@Override
	public String getDescription() {
		return Lang.PLAYER_KILLS_REQUIREMENT.getConfigValue(AutorankTools
				.seperateList(totalPlayersKilled, "or"));
	}

	@Override
	public String getProgress(final Player player) {
		String progress = "";
		final int killed = getStatsPlugin().getNormalStat(
				StatsHandler.statTypes.PLAYERS_KILLED.toString(),
				player.getUniqueId());

		progress = AutorankTools.makeProgressString(totalPlayersKilled,
				"player(s)", killed + "");
		return progress;
	}

	@Override
	public boolean meetsRequirement(final Player player) {
		final int killed = getStatsPlugin().getNormalStat(
				StatsHandler.statTypes.PLAYERS_KILLED.toString(),
				player.getUniqueId());

		for (final int killedPlayers : totalPlayersKilled) {
			if (killed >= killedPlayers)
				return true;
		}

		return false;
	}

	@Override
	public boolean setOptions(final List<String[]> optionsList) {

		for (final String[] options : optionsList) {
			try {
				totalPlayersKilled.add(Integer.parseInt(options[0]));
			} catch (final Exception e) {
				return false;
			}
		}

		return !totalPlayersKilled.isEmpty();
	}
}
