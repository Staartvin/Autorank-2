package me.armar.plugins.autorank.statsmanager.query.parameter;

import io.reactivex.annotations.NonNull;
import me.armar.plugins.autorank.statsmanager.query.parameter.implementation.*;
import org.apache.commons.lang.Validate;

import java.lang.reflect.InvocationTargetException;

public enum ParameterType {

    WORLD(WorldTypeParameter.class),
    MOB_TYPE(MobTypeParameter.class),
    BLOCK_TYPE(BlockTypeParameter.class),
    MOVEMENT_TYPE(MovementTypeParameter.class),
    FOOD_TYPE(FoodTypeParameter.class);

    private final Class<? extends StatisticParameter> matchingParameter;

    ParameterType(Class<? extends StatisticParameter> parameter) {
        this.matchingParameter = parameter;
    }

    public static ParameterType getParameterType(@NonNull String key) {
        Validate.notNull(key);

        for (ParameterType type : ParameterType.values()) {
            try {
                if (type.getMatchingParameter().getDeclaredConstructor(String.class).newInstance("").getKey().equalsIgnoreCase(key)) {
                    return type;
                }
            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public @NonNull
    Class<? extends StatisticParameter> getMatchingParameter() {
        return matchingParameter;
    }

    public String getKey() {
        try {
            return this.getMatchingParameter().getDeclaredConstructor(String.class).newInstance("").getKey();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }

        return null;
    }


}
