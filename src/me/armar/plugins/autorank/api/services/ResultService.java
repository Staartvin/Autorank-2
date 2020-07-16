package me.armar.plugins.autorank.api.services;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.pathbuilder.builders.ResultBuilder;
import me.armar.plugins.autorank.pathbuilder.result.AbstractResult;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ResultService implements ResultManager {

    private final Autorank plugin;

    public ResultService(Autorank instance) {
        this.plugin = instance;
    }

    @Override
    public boolean registerResult(String identifier, Class<? extends AbstractResult> resultClass) {
        Objects.requireNonNull(identifier);
        Objects.requireNonNull(resultClass);

        ResultBuilder.registerResult(identifier, resultClass);

        return true;
    }

    @Override
    public boolean unRegisterResult(String identifier) {
        Objects.requireNonNull(identifier);
        return ResultBuilder.unRegisterResult(identifier);
    }

    @Override
    public List<Class<? extends AbstractResult>> getRegisteredResults() {
        return ResultBuilder.getRegisteredResults();
    }

    @Override
    public Optional<Class<? extends AbstractResult>> getResult(String identifier) {
        return ResultBuilder.getRegisteredResult(identifier);
    }
}
