package me.armar.plugins.autorank.playerchecker.requirement;

import java.util.List;

import me.armar.plugins.autorank.Autorank;
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

	/**
	 * Setup requirement specific objects
	 * @param options String[] containing values
	 * @param optional Is this an optional requirement?
	 * @param results List<Result> containing results
	 * @param autoComplete Will this auto complete?
	 * @return
	 */
	public abstract boolean setOptions(String[] options, boolean optional, List<Result> results, boolean autoComplete, int reqId);

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
	
	/**
	 * Use auto completion for this?
	 * @return true when auto complete; false otherwise
	 */
	public abstract boolean useAutoCompletion();

	public String toString() {
		return this.getClass().getSimpleName();
	}
	
	/*public final int getReqID(Class<? extends Requirement> req, Player player) {
		Map<RankChange, List<Requirement>> requirements = autorank.getPlayerChecker()
				.getAllRequirements(player);
		Set<RankChange> keySet = requirements.keySet();
		
		List<Requirement> realReq;
		int id = 0;
		
		for (Iterator<RankChange> it = keySet.iterator(); it.hasNext();) {
			RankChange rank = it.next();
			realReq = requirements.get(rank);
			
			System.out.print("Requirement size: " + realReq.size()); 
			for (int i=0;i<realReq.size();i++) {
				Requirement req2 = realReq.get(i);
				
				
				if (req2.getClass().equals(req)) {
					
					System.out.print("--------------------------------");
					System.out.print("REQ for " + req.getName() + ": " + req.hashCode());
					System.out.print("REQ2 for " + req2.getClass().getName() + ": " + req2.hashCode());
					id = i;
					break;
				}
			}
			
			System.out.print("--- [END] ---");
		}
		
		//System.out.print("REQ ID for " + req.toString() + ": " + id);
		return id;
	} */
	
	public final boolean isCompleted(int reqID, String playerName) {
		return autorank.getRequirementHandler().hasCompletedRequirement(reqID, playerName);
	}
	
	/**
	 * Gets the current progress of a player on a certain requirement.
	 * @param player Player to check for
	 * @return String containing the progress
	 */
	public abstract String getProgress(Player player);
	
	public abstract int getReqId();
}
