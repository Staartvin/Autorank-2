package me.armar.plugins.autorank.playerchecker.builders;

import java.util.HashMap;
import java.util.Map;
import me.armar.plugins.autorank.playerchecker.result.Result;

public class ResultBuilder {

    private final Map<String, Class<? extends Result>> results = new HashMap<String, Class<? extends Result>>();

    public Result create(final String type) {
        Result res = null;
        final Class<? extends Result> c = results.get(type);
        if (c != null) {
            try {
                res = c.newInstance();
            } catch (final Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return res;
    }

    public void registerResult(final String type,
            final Class<? extends Result> result) {
        results.put(type, result);
    }

}
