package me.armar.plugins.autorank.rankbuilder;

import java.util.List;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.playerchecker.requirement.Requirement;
import me.armar.plugins.autorank.playerchecker.result.Result;

/**
 * Represents a group of changes, including all requirements and results.
 * <p>
 * Date created:  14:23:30
 * 5 aug. 2015
 * @author Staartvin
 *
 */
public class ChangeGroup {

	private List<Requirement> requirements;
	private List<Result> results;
	private String parentGroup, internalGroup;
	
	private Autorank plugin;
	
	public ChangeGroup(Autorank plugin, List<Requirement> reqs, List<Result> results) {
		this.plugin = plugin;
		this.setRequirements(reqs);
		this.setResults(results);
	}
	
	public ChangeGroup(Autorank plugin) {
		this.plugin = plugin;
	}

	public List<Requirement> getRequirements() {
		return requirements;
	}

	public void setRequirements(List<Requirement> requirements) {
		this.requirements = requirements;
	}

	public List<Result> getResults() {
		return results;
	}

	public void setResults(List<Result> results) {
		this.results = results;
	}

	public String getParentGroup() {
		return parentGroup;
	}

	public void setParentGroup(String parentGroup) {
		this.parentGroup = parentGroup;
	}

	public String getInternalGroup() {
		return internalGroup;
	}

	public void setInternalGroup(String internalGroup) {
		this.internalGroup = internalGroup;
	}
	
	
}
