package me.armar.plugins.autorank.pathbuilder.builders;

import java.util.HashMap;
import java.util.Map;

import me.armar.plugins.autorank.Autorank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import me.armar.plugins.autorank.pathbuilder.result.Result;
import me.armar.plugins.autorank.util.AutorankTools;

/**
 * This class is used to create a new Result. It uses the Factory Method Design Pattern.
 */
public class ResultBuilder {

    private static final Map<String, Class<? extends Result>> results = new HashMap<String, Class<? extends Result>>();

    // Keep track of the associated result.
    private Result result = null;

    // Whether the associated result is valid.
    private boolean isValid = false;

    // Extra metadata for the associated result.
    private String pathName, resultName;

    /**
     * Create an empty Result.
     * @param pathName Name of the path that this result is in.
     * @param resultType Type of the result.
     * @return this builder.
     */
    public ResultBuilder createEmpty(String pathName, String resultType) {

        this.pathName = pathName;

        String originalResType = resultType;

        resultType = AutorankTools.getCorrectResName(resultType);

        this.resultName = resultType;

        if (resultType == null) {
            Autorank.getInstance().getWarningManager().registerWarning(
                    String.format("You are using a '%s' result in path '%s', but that result doesn't exist!", originalResType,
                            pathName),
                    10);
            return this;
        }

        final Class<? extends Result> c = results.get(resultType);
        if (c != null) {
            try {
                result = c.newInstance();
            } catch (final Exception e) {
                e.printStackTrace();
            }
        } else {
            Bukkit.getServer().getConsoleSender()
                    .sendMessage("[Autorank] " + ChatColor.RED + "Result '" + originalResType + "' is not a valid result type!");
        }
        return this;
    }

    /**
     * Populate the created Result with data.
     * @return this builder.
     */
    public ResultBuilder populateResult(String stringValue) {

        if (result == null) {
            return this;
        }

        if (stringValue == null) {
            return this;
        }

        // Initiliaze the result with options.
        result.setOptions(stringValue.split(";"));

        // Result is non-null and populated with data, so valid.
        isValid = true;

        return this;
    }

    /**
     * Finish the creation of the Result, will return the result object that was created.
     * @return created Result object.
     * @throws IllegalStateException if the result was not valid and could not be finished.
     */
    public Result finish() throws IllegalStateException {
        if (!isValid || result == null) {
            throw new IllegalStateException("Result '" + resultName + "' of '" + pathName + "' was not valid" +
                    " and could not be finished.");
        }

        return result;
    }

    /**
     * Check whether the associated result is valid.
     * @return true if it is, false otherwise.
     */
    public boolean isValid() {
        return isValid;
    }

    /**
     * Add a new type of Result that can be used in the Paths.yml file.
     * @param type String literal that must be used in the file to identify the result.
     * @param result Class of the Result that must be instantiated.
     */
    public static void registerResult(final String type, final Class<? extends Result> result) {
        results.put(type, result);

        // Add type to the list of AutorankTools so it can use the correct name.
        AutorankTools.registerResult(type);
    }

    /**
     * Create a Result using the ResultBuilder factory.
     * @param pathName Name of the path the result is in.
     * @param resultType Type of the result, which does not have to be the exact string value.
     * @param stringValue Value of the result string.
     * @return a newly created Result with the given data, or null if invalid data was given.
     */
    public static Result createResult(String pathName, String resultType, String stringValue) {
        ResultBuilder builder = new ResultBuilder().createEmpty(pathName, resultType).populateResult(stringValue);

        // Check if result is valid before building it.
        if (!builder.isValid()) {
            return null;
        }

        // Get result of ResultBuilder.
        final Result result = builder.finish();

        return result;
    }

}
