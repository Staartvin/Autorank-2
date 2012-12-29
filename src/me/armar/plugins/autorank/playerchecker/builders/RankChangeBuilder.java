package me.armar.plugins.autorank.playerchecker.builders;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.data.SimpleYamlConfiguration;
import me.armar.plugins.autorank.playerchecker.RankChange;
import me.armar.plugins.autorank.playerchecker.additionalrequirement.*;
import me.armar.plugins.autorank.playerchecker.result.*;

public class RankChangeBuilder {

    private ResultBuilder resultBuilder;
    private AdditionalRequirementBuilder requirementBuilder;
    private Autorank autorank;

    public RankChangeBuilder(Autorank autorank) {
	this.autorank = autorank;
	setResultBuilder(new ResultBuilder());
	setRequirementBuilder(new AdditionalRequirementBuilder());
    }

    public List<RankChange> createFromSimpleConfig(SimpleYamlConfiguration config) {
	// TODO logging errors and not making faulty RankChanges
	List<RankChange> result = new ArrayList<RankChange>();

	Set<String> ranks = config.getKeys(false);
	Iterator<String> it = ranks.iterator();

	while (it.hasNext()) {
	    String rank = it.next();
	    String value = (String) config.get(rank);

	    String[] options = value.split(" after ");

	    List<AdditionalRequirement> req = new ArrayList<AdditionalRequirement>();
	    AdditionalRequirement timeReq = new TimeRequirement();
	    timeReq.setOptions(new String[] { options[1] });
	    timeReq.setAutorank(autorank);
	    req.add(timeReq);

	    List<Result> res = new ArrayList<Result>();
	    Result change = new RankChangeResult();
	    change.setOptions(new String[] { rank, options[0] });
	    change.setAutorank(autorank);
	    res.add(change);

	    Result message = new MessageResult();
	    message.setOptions(new String[] { "&2You got ranked to " + options[0] });
	    message.setAutorank(autorank);
	    res.add(message);

	    result.add(new RankChange(rank, req, res));
	}

	return result;
    }

    public List<RankChange> createFromAdvancedConfig(SimpleYamlConfiguration config) {
	List<RankChange> result = new ArrayList<RankChange>();

	ConfigurationSection section = config.getConfigurationSection("ranks");
	for (String group : section.getKeys(false)) {

	    List<AdditionalRequirement> req = new ArrayList<AdditionalRequirement>();
	    List<Result> res = new ArrayList<Result>();
	    ConfigurationSection groupSection = section.getConfigurationSection(group);

	    if (groupSection.get("requirements") != null) {
		ConfigurationSection reqList = groupSection.getConfigurationSection("requirements");
				
		for (String requirement : reqList.getKeys(false)) {
		    req.add(createRequirement(requirement, reqList.get(requirement).toString()));
		}
		
	    }
	   	    
	    if (groupSection.get("results") != null) {
		ConfigurationSection resList = groupSection.getConfigurationSection("results");
		
		for (String resu : resList.getKeys(false)) {
		    res.add(createResult(resu, (String) resList.get(resu)));
		}
	    }

	    result.add(new RankChange(group, req, res));
	}

	return result;
    }

    private Result createResult(String type, String arg) {
	Result res = resultBuilder.create(type);
	
	if(res != null){
	    res.setAutorank(autorank);
	    res.setOptions(arg.split(";"));
	}
	
	return res;
    }

    private AdditionalRequirement createRequirement(String type, String arg) {
	AdditionalRequirement res = requirementBuilder.create(type);
	
	if(res != null){
	    res.setAutorank(autorank);
	    res.setOptions(arg.split(";"));
	}
	
	return res;
    }

    public ResultBuilder getResultBuilder() {
	return resultBuilder;
    }

    private void setResultBuilder(ResultBuilder resultBuilder) {
	this.resultBuilder = resultBuilder;
    }

    public AdditionalRequirementBuilder getRequirementBuilder() {
	return requirementBuilder;
    }

    private void setRequirementBuilder(AdditionalRequirementBuilder requirementBuilder) {
	this.requirementBuilder = requirementBuilder;
    }

}
