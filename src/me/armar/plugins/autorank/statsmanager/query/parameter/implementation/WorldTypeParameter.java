package me.armar.plugins.autorank.statsmanager.query.parameter.implementation;

import me.armar.plugins.autorank.statsmanager.query.parameter.StatisticParameter;

public class WorldTypeParameter extends StatisticParameter {

    public WorldTypeParameter(String value) {
        super(value);
    }

    @Override
    public String getKey() {
        return "world";
    }

    @Override
    public void setKey(String key) {
    }
}
