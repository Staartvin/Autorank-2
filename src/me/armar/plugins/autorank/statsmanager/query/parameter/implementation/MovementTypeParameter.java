package me.armar.plugins.autorank.statsmanager.query.parameter.implementation;

import me.armar.plugins.autorank.statsmanager.query.parameter.StatisticParameter;

public class MovementTypeParameter extends StatisticParameter {

    public MovementTypeParameter(String value) {
        super(value);
    }

    @Override
    public String getKey() {
        return "moveType";
    }

    @Override
    public void setKey(String key) {
    }

    public enum MovementType {
        FOOT, BOAT, MINECART, PIG, PIG_IN_MINECART, HORSE, WALK
    }
}


