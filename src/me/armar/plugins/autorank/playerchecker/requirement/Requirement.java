package me.armar.plugins.autorank.playerchecker.requirement;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.playerchecker.RankChange;
import me.armar.plugins.autorank.playerchecker.result.Result;

import org.bukkit.entity.Player;

public abstract class Requirement {

	private Autorank autorank;

	public final void setAutorank(Autorank autorank) {
		this.autorank = autorank;
	}

	public final Autorank getAutorank() {
		return autorank;
	}

	public abstract boolean setOptions(String[] options, boolean optional, List<Result> results);

	/**
	 * Does it meet the requirements?
	 * @param player Player to check for
	 * @return true if it meets the requirements; false otherwise
	 */
	public abstract boolean meetsRequirement(Player player);

	/**
	 * Gets the description of the requirement
	 * @return string containing description (in locale language)
	 */
	public abstract String getDescription();
	
	/**
	 * Is this an optional requirement?
	 * (Not a main requirement)
	 * @return true when optional; false otherwise.
	 */
	public abstract boolean isOptional();
	
	/**
	 * Gets the results when this requirement is finished
	 * @return A list of results that has to be done.
	 */
	public abstract List<Result> getResults();

	public String toString() {
		return this.getClass().getSimpleName();
	}
	
	public final int getReqID(Class<? extends Requirement> req, Player player) {
		Map<RankChange, List<Requirement>> requirements = autorank.getPlayerChecker()
				.getAllRequirements(player);
		Set<RankChange> keySet = requirements.keySet();
		
		List<Requirement> realReq;
		int id = 0;
		
		for (Iterator<RankChange> it = keySet.iterator(); it.hasNext();) {
			RankChange rank = it.next();
			realReq = requirements.get(rank);
			
			for (int i=0;i<realReq.size();i++) {
				Requirement req2 = realReq.get(i);
				if (req2.getClass().equals(req)) {
					id = i;
				}
			}
		}
		
		//System.out.print("REQ ID for " + req.toString() + ": " + id);
		return id;
	}
	
	public final boolean isCompleted(int reqID, String playerName) {
		return autorank.getRequirementHandler().hasCompletedRequirement(reqID, playerName);
	}
}
