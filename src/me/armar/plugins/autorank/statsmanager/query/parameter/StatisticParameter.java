package me.armar.plugins.autorank.statsmanager.query.parameter;

import java.lang.reflect.InvocationTargetException;

public abstract class StatisticParameter {

    String key = null, value = null;

    public StatisticParameter(String key, String value) {
        this.setKey(key);
        this.setValue(value);
    }

    public StatisticParameter(String value) {
        this.setValue(value);
    }

    public static StatisticParameter createInstance(ParameterType parameterType, String value) {
        try {
            return parameterType.getMatchingParameter().getDeclaredConstructor(String.class).newInstance(value);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ParameterType getParameterType() {
        for (ParameterType type : ParameterType.values()) {
            if (type.getMatchingParameter().isAssignableFrom(this.getClass())) {
                return type;
            }
        }

        return null;
    }

}
