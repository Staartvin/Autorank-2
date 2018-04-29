package me.armar.plugins.autorank.config;

import me.armar.plugins.autorank.Autorank;

enum DefaultBehaviorOption {
    /**
     * Whether we should automatically assign a path to a player. (boolean)
     */
    AUTO_CHOOSE_PATH(Boolean.class, true),
    /**
     * The default priority of a path. (integer)
     */
    PRIORITY_PATH(Integer.class, 1),
    /**
     * Whether Autorank should show the path (e.g. in /ar view list) based on whether they meet the
     * prerequisites of that path or not. (boolean)
     */
    SHOW_PATH_BASED_ON_PREREQUISITES(Boolean.class, false),
    /**
     * Whether we should auto complete a requirement when a player meets a requirement of their current path.
     * (boolean)
     */
    AUTO_COMPLETE_REQUIREMENT(Boolean.class, true),
    /**
     * Whether a requirement is optional to complete or not. (boolean)
     */
    IS_OPTIONAL_REQUIREMENT(Boolean.class, false),
    /**
     * Whether we allow a player to complete a path over and over again. (boolean)
     */
    ALLOW_INFINITE_PATHING(Boolean.class, false),
    /**
     * Whether a player can complete requirements one by one or should meet all requirements at the same time.
     */
    ALLOW_PARTIAL_COMPLETION(Boolean.class, true);

    private Class classType;
    private Object defaultValue;

    /**
     * DefaultBehavior option for a path.
     *
     * @param classType    Type of option (string, boolean, integer, etc.)
     * @param defaultValue Default value (if it was not specified in the file) determined by Autorank. Matches the
     *                     classType value.
     */
    DefaultBehaviorOption(Class classType, Object defaultValue) {
        this.classType = classType;
        this.defaultValue = defaultValue;
    }

    public Class getClassType() {
        return classType;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }
}

/**
 * This class is used to access the properties of the DefaultBehaviorConfig.yml file. This configuration file determines
 * what the default values are for the options of a path. So, instead of setting an option for each path, you just
 * change
 * the default behavior of a path.
 *
 * @author Staartvin
 */
public class DefaultBehaviorConfig extends AbstractConfig {

    private String fileName = "DefaultBehavior.yml";

    public DefaultBehaviorConfig(final Autorank instance) {
        setPlugin(instance);
        setFileName(fileName);
    }

    @Override
    public void loadConfig() {

        super.loadConfig();

        this.getConfig().options().header("This file allows you to change the default behavior of a path. " +
                "\nFor example, if you change 'ALLOW_INFINITE_PATHING' to true, " +
                "all paths will by default allow infinite pathing, unless specified otherwise by setting it to false" +
                " for a path." +
                "\nIf you're unsure about the option, please consult the wiki about what they mean. " +
                "\nMoreover, be careful with changing defaults: it can ruin your Autorank setup!");


        // Load all default options in if they are not present.
        for (DefaultBehaviorOption option : DefaultBehaviorOption.values()) {
            this.getConfig().addDefault(option.toString(), option.getDefaultValue());
        }

        this.getConfig().options().copyDefaults(true);

        saveConfig();
    }

    // ------------------------ Specific methods for DefaultBehavior.yml from here. ------------------------------

    /**
     * Get the default value for a boolean-type option.
     *
     * @param option Option to get default type of
     * @return the default value (true or false) of the given option
     * @throws IllegalArgumentException if the option is not of a boolean type.
     */
    public boolean getDefaultBooleanBehaviorOfOption(DefaultBehaviorOption option) throws IllegalArgumentException {
        if (!option.getClassType().equals(Boolean.class)) {
            throw new IllegalArgumentException("Option " + option + " is not of type boolean!");
        }

        return this.getConfig().getBoolean(option.toString());
    }

    /**
     * Get the default value for an integer-type option.
     *
     * @param option Option to get default type of
     * @return the default value (integer) of the given option
     * @throws IllegalArgumentException if the option is not of an integer type.
     */
    public int getDefaultIntegerBehaviorOfOption(DefaultBehaviorOption option) throws IllegalArgumentException {
        if (!option.getClassType().equals(Integer.class)) {
            throw new IllegalArgumentException("Option " + option + " is not of type integer!");
        }

        return this.getConfig().getInt(option.toString());
    }

    /**
     * Get the default value for a string-type option.
     *
     * @param option Option to get default type of
     * @return the default value (String) of the given option
     * @throws IllegalArgumentException if the option is not of a string type.
     */
    public String getDefaultStringBehaviorOfOption(DefaultBehaviorOption option) throws IllegalArgumentException {
        if (!option.getClassType().equals(String.class)) {
            throw new IllegalArgumentException("Option " + option + " is not of type String!");
        }

        return this.getConfig().getString(option.toString());
    }


}
