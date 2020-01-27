package me.armar.plugins.autorank.api.services;

import me.armar.plugins.autorank.pathbuilder.requirement.AbstractRequirement;

import java.util.List;
import java.util.Optional;

/**
 * This service can be used to inject requirements for Autorank. A requirement is identified by an identifier (that
 * is used in the Paths.yml file). When a requirement is matched in the paths file, it is constructed using the
 * {@link me.armar.plugins.autorank.pathbuilder.builders.RequirementBuilder}. For more information about a
 * requirement, see {@link AbstractRequirement}.
 */
public interface RequirementManager {

    /**
     * Register a requirement to be used by Autorank.
     *
     * @param identifier       Identifier string that is used in the Paths.yml file.
     * @param requirementClass Class that should be used to construct the requirement.
     * @return whether the requirement was successfully registered.
     */
    boolean registerRequirement(String identifier, Class<? extends AbstractRequirement> requirementClass);

    /**
     * Unregister a requirement that was registered before.
     *
     * @param identifier Identifier used by requirement.
     * @return whether the requirement was successfully unregistered.
     */
    boolean unRegisterRequirement(String identifier);

    /**
     * Get all registered requirements.
     *
     * @return a list of classes used for requirements.
     */
    List<Class<? extends AbstractRequirement>> getRegisteredRequirements();

    /**
     * Get a requirement class by identifier.
     *
     * @param identifier Identifier used when registering the requirement.
     * @return requirement class matching the identifier, if found.
     */
    Optional<Class<? extends AbstractRequirement>> getRequirement(String identifier);


}
