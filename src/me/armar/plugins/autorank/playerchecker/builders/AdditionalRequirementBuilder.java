package me.armar.plugins.autorank.playerchecker.builders;

import java.util.HashMap;
import java.util.Map;

import me.armar.plugins.autorank.playerchecker.additionalrequirement.AdditionalRequirement;

public class AdditionalRequirementBuilder {

	private Map<String, Class<? extends AdditionalRequirement>> reqs = new HashMap<String, Class<? extends AdditionalRequirement>>();

	public AdditionalRequirement create(String type) {
		AdditionalRequirement res = null;
		Class<? extends AdditionalRequirement> c = reqs.get(type);
		if (c != null)
			try {
				res = c.newInstance();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return res;
	}

	public void registerAdditionalRequirement(String type,
			Class<? extends AdditionalRequirement> requirement) {
		reqs.put(type, requirement);
	}

}
