package me.armar.plugins.autorank.statsmanager.query;

import me.armar.plugins.autorank.statsmanager.query.parameter.ParameterType;
import me.armar.plugins.autorank.statsmanager.query.parameter.StatisticParameter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StatisticQuery {

    private final List<StatisticParameter> parameters = new ArrayList<>();

    public static StatisticQuery makeStatisticQuery(Object... strings) {
        StatisticQuery query = new StatisticQuery();

        for (int i = 0; i < strings.length; i += 2) {
            Object string = strings[i];

            // Either key or value is null
            if (string == null || strings[i + 1] == null) {
                continue;
            }

            String key = (String) string;
            String value = strings[i + 1].toString();

            query.addParameter(ParameterType.getParameterType(key), value);

        }

        return query;
    }

    public void addParameter(StatisticParameter parameter) {
        this.parameters.add(parameter);
    }

    public void addParameter(ParameterType parameterType, String value) {
        this.parameters.add(StatisticParameter.createInstance(parameterType, value));
    }

    public Optional<StatisticParameter> getParameter(ParameterType parameterType) {
        return parameters.stream().filter(p -> p.getParameterType().equals(parameterType)).findFirst();
    }

    public void removeParameter(ParameterType parameterType) {
        parameters.removeIf(parameter -> parameter.getParameterType() == parameterType);
    }

    public boolean hasParameter(ParameterType type) {
        return getParameter(type).isPresent();
    }

    public Optional<String> getParameterValue(ParameterType type) {
        return getParameter(type).map(StatisticParameter::getValue);
    }
}
