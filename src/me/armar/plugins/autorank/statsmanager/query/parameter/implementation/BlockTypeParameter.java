package me.armar.plugins.autorank.statsmanager.query.parameter.implementation;

import me.armar.plugins.autorank.statsmanager.query.parameter.StatisticParameter;

public class BlockTypeParameter extends StatisticParameter {

    public BlockTypeParameter(String value) {
        super(value);
    }

    @Override
    public String getKey() {
        return "block";
    }

    @Override
    public void setKey(String key) {
    }
}
