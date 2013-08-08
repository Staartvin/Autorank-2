package me.armar.plugins.autorank.playerchecker;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.entity.Player;

import me.armar.plugins.autorank.playerchecker.requirement.Requirement;
import me.armar.plugins.autorank.playerchecker.result.Result;

public class RankChange {

	private String rank;
	private List<Requirement> req;
	private List<Result> res;
	private String rankTo;

	public RankChange(String rank, String rankTo, List<Requirement> req,
			List<Result> res) {
		this.rank = rank;
		this.req = req;
		this.res = res;
		this.rankTo = rankTo;
	}

	public String getRank() {
		return rank;
	}

	public List<Requirement> getReq() {
		return req;
	}

	public List<Result> getRes() {
		return res;
	}

	public String getRankTo() {
		return rankTo;
	}

	public boolean checkRequirements(Player player) {
		boolean result = true;

		for (Requirement r : req) {
			if (r != null)
				// When optional, always true
				if (r.isOptional())
					continue;

			if (!r.meetsRequirement(player)) {
				result = false;
				break;
			} else {
				// Player meets requirement, thus perform results of requirement
				// Perform results of a requirement as well
				List<Result> results = r.getResults();
				
				boolean noErrors = true;
				for (Result realResult: results) {
					if (!realResult.applyResult(player)) {
						noErrors = false;
					}
				}
				result = noErrors;
			}
		}

		return result;
	}

	public List<Requirement> getFailedRequirements(Player player) {
		List<Requirement> failed = new CopyOnWriteArrayList<Requirement>();
		failed.addAll(req);

		for (Requirement r : failed) {
			if (r != null)
				if (r.meetsRequirement(player)) {
					failed.remove(r);
				}
		}

		return failed;
	}

	public boolean applyChange(Player player, String group) {
		boolean result = true;

		if (checkRequirements(player)) {
			for (Result r : res) {
				if (r != null)
					if (!r.applyResult(player, group))
						result = false;
			}
		} else {
			result = false;
		}

		return result;
	}

	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(rank);
		b.append(": ");

		boolean first = true;
		for (Requirement r : req) {
			if (!first)
				b.append(", ");
			first = false;
			b.append(r.toString());
		}

		b.append(" -> ");

		first = true;
		for (Result r : res) {
			if (!first)
				b.append(", ");
			first = false;
			if (r != null) {
				b.append(r.toString());
			} else {
				b.append("NULL");
			}

		}
		return b.toString();
	}

}
