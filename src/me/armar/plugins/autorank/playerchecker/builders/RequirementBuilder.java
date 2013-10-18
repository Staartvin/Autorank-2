package me.armar.plugins.autorank.playerchecker.builders;

import java.util.HashMap;
import java.util.Map;

import me.armar.plugins.autorank.AutorankTools;
import me.armar.plugins.autorank.playerchecker.requirement.Requirement;

public class RequirementBuilder {

	private Map<String, Class<? extends Requirement>> reqs = new HashMap<String, Class<? extends Requirement>>();

	public Requirement create(String type) {
		Requirement res = null;
		Class<? extends Requirement> c = reqs.get(type);
		if (c != null)
			try {
				res = c.newInstance();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return res;
	}

	public void registerRequirement(String type,
			Class<? extends Requirement> requirement) {
		// Add type to the list
		reqs.put(type, requirement);
		
		// Add type to the list of AutorankTools so it can use the correct name.
		AutorankTools.registerRequirement(type);
		
	}

}
