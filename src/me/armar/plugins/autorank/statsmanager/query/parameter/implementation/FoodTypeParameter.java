package me.armar.plugins.autorank.statsmanager.query.parameter.implementation;

import me.armar.plugins.autorank.statsmanager.query.parameter.StatisticParameter;

public class FoodTypeParameter extends StatisticParameter {

    public FoodTypeParameter(String value) {
        super(value);
    }

    @Override
    public String getKey() {
        return "foodType";
    }

    @Override
    public void setKey(String key) {
    }
}
