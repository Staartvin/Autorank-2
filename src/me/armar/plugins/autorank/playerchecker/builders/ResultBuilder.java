package me.armar.plugins.autorank.playerchecker.builders;

import java.util.HashMap;
import java.util.Map;

import me.armar.plugins.autorank.playerchecker.result.Result;

public class ResultBuilder {
    
    private Map<String, Class<? extends Result>> results = new HashMap<String, Class<? extends Result>>();
    
    public Result create(String type){
	Result res = null;
	Class<? extends Result> c = results.get(type);
	if(c != null)
	    try {
		res = c.newInstance();
	    } catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	return res;
    }

    public void registerResult(String type, Class<? extends Result> result) {
	results.put(type, result);
    }

}
