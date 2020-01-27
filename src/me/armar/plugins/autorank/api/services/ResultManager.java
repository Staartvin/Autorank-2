package me.armar.plugins.autorank.api.services;

import me.armar.plugins.autorank.pathbuilder.result.AbstractResult;

import java.util.List;
import java.util.Optional;

/**
 * This service can be used to inject results for Autorank. A result is identified by an identifier (that
 * is used in the Paths.yml file). When a result is matched in the paths file, it is constructed using the
 * {@link me.armar.plugins.autorank.pathbuilder.builders.ResultBuilder}. For more information about a
 * result, see {@link me.armar.plugins.autorank.pathbuilder.result.AbstractResult}.
 */
public interface ResultManager {

    /**
     * Register a result to be used by Autorank.
     *
     * @param identifier  Identifier string that is used in the Paths.yml file.
     * @param resultClass Class that should be used to construct the result.
     * @return whether the result was successfully registered.
     */
    boolean registerResult(String identifier, Class<? extends AbstractResult> resultClass);

    /**
     * Unregister a result that was registered before.
     *
     * @param identifier Identifier used by result.
     * @return whether the result was successfully unregistered.
     */
    boolean unRegisterResult(String identifier);

    /**
     * Get all registered results.
     *
     * @return a list of classes used for results.
     */
    List<Class<? extends AbstractResult>> getRegisteredResults();

    /**
     * Get a result class by identifier.
     *
     * @param identifier Identifier used when registering the result.
     * @return result class matching the identifier, if found.
     */
    Optional<Class<? extends AbstractResult>> getResult(String identifier);


}
