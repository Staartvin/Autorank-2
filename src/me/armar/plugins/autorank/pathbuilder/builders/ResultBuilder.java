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
    private boolean validResult = false;

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
        resultType = AutorankTools.getCorrectResName(resultType);

        this.resultName = resultType;

        if (resultType == null) {
            Autorank.getInstance().getWarningManager().registerWarning(
                    String.format("You are using a '%s' result in path '%s', but that result doesn't exist!", resultType,
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
                    .sendMessage("[Autorank] " + ChatColor.RED + "Result '" + resultType + "' is not a valid result type!");
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

        // Set Autorank instance.
        result.setAutorank(Autorank.getInstance());

        if (stringValue == null) {
            return this;
        }

        // Initiliaze the result with options.
        result.setOptions(stringValue.split(";"));

        // Result is non-null and populated with data, so valid.
        validResult = true;

        return this;
    }

    /**
     * Finish the creation of the Result, will return the result object that was created.
     * @return created Result object.
     * @throws IllegalStateException if the result was not valid and could not be finished.
     */
    public Result finish() throws IllegalStateException {
        if (!validResult || result == null) {
            throw new IllegalStateException("Result '" + resultName + "' was not valid and could not be finished.");
        }

        return result;
    }

    /**
     * Check whether the associated result is valid.
     * @return true if it is, false otherwise.
     */
    public boolean isResultValid() {
        return validResult;
    }

    public static void registerResult(final String type, final Class<? extends Result> result) {
        results.put(type, result);

        // Add type to the list of AutorankTools so it can use the correct name.
        AutorankTools.registerResult(type);
    }

}
