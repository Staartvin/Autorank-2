package me.armar.plugins.autorank.statsmanager.query.parameter.implementation;

import me.armar.plugins.autorank.statsmanager.query.parameter.StatisticParameter;

public class MobTypeParameter extends StatisticParameter {

    public MobTypeParameter(String value) {
        super(value);
    }

    @Override
    public String getKey() {
        return "mobType";
    }

    @Override
    public void setKey(String key) {
    }
}
