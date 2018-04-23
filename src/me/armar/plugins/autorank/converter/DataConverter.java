package me.armar.plugins.autorank.converter;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.config.SimpleYamlConfiguration;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class DataConverter {

    private Autorank plugin;

    public DataConverter(Autorank instance) {
        this.plugin = instance;
    }

    /**
     * Convert storage format from Autorank 3.8 (or lower) to Autorank 4.0
     *
     * @return true if it worked, false if it was already converted or did not
     * work.
     */
    public boolean convertData() {
        // Convert Autorank 3.8 or lower to Autorank 4.0

        if (plugin.getInternalPropertiesConfig().isConvertedToNewFormat()) {
            return false;
        }

        plugin.getLogger().info(
                "Autorank detected that you upgraded from an older version. It will need to convert your folders.");

        plugin.getLogger().info("Started converting folders of Autorank...");

        final String folderPath = plugin.getDataFolder().getAbsolutePath() + File.separator;

        // Rename storage files
        if (!new File(folderPath + "/storage/daily_time.yml").renameTo(new File(folderPath + "/storage/Daily_time" +
                ".yml"))) {
            plugin.getLogger().info("Could not rename daily_time.yml to Daily_time.yml!");
        } else {
            plugin.getLogger().info("Successfully converted Daily_time.yml!");
        }

        if (!new File(folderPath + "/storage/weekly_time.yml").renameTo(new File(folderPath + "/storage/Weekly_time.yml"))) {
            plugin.getLogger().info("Could not rename weekly_time.yml to Weekly_time.yml!");
        } else {
            plugin.getLogger().info("Successfully converted Weekly_time.yml!");
        }

        if (!new File(folderPath + "/storage/monthly_time.yml")
                .renameTo(new File(folderPath + "/storage/Monthly_time.yml"))) {
            plugin.getLogger().info("Could not rename monthly_time.yml to Monthly_time.yml!");
        } else {
            plugin.getLogger().info("Successfully converted Monthly_time.yml!");
        }

        // Now move Data.yml to /storage/Total_time.yml
        File totalTimeFile = new File(folderPath + "/storage/Total_time.yml");

        if (totalTimeFile.exists()) {
            plugin.getLogger().info("Deleting Total_time.yml");
            totalTimeFile.delete();
        }

        if (!new File(folderPath + "Data.yml").renameTo(totalTimeFile)) {
            plugin.getLogger().info("Could not rename Data.yml to Total_time.yml!");
        } else {
            plugin.getLogger().info("Successfully converted Data.yml!");
        }

        // Rename playerdata.yml to PlayerData.yml
        if (!new File(folderPath + "/playerdata/playerdata.yml")
                .renameTo(new File(folderPath + "/playerdata/PlayerData.yml"))) {
            plugin.getLogger().info("Could not rename playerdata.yml to PlayerData.yml!");
        } else {
            plugin.getLogger().info("Successfully converted playerdata.yml!");
        }

        plugin.getLogger().info("Conversion of Autorank is complete!");

        plugin.getInternalPropertiesConfig().setConvertedToNewFormat(true);

        // Reload file
        //plugin.getFlatFileManager().getDataFile(TimeType.TOTAL_TIME).reloadFile();
        //plugin.getFlatFileManager().getDataFile(TimeType.WEEKLY_TIME).reloadFile();
        //plugin.getFlatFileManager().getDataFile(TimeType.MONTHLY_TIME).reloadFile();
        //plugin.getFlatFileManager().getDataFile(TimeType.DAILY_TIME).reloadFile();

        return true;
    }

    /**
     * Convert the SimpleConfig file to the new format of the Paths.yml file.
     *
     * @return true if correctly converted, false otherwise.
     */
    public boolean convertSimpleConfigToPaths() {

        SimpleYamlConfiguration simpleConfig = new SimpleYamlConfiguration(plugin, "SimpleConfig.yml", "SimpleConfig");

        String newPathsFileName = "Paths_from_SimpleConfig.yml";

        SimpleYamlConfiguration newPathsFile = new SimpleYamlConfiguration(plugin, newPathsFileName,
                "Converted paths file");

        Map<String, String> paths = new HashMap<>();

        // Grab all the paths from the SimpleConfig.yml
        for (String fromGroup : simpleConfig.getKeys(false)) {
            String valueString = simpleConfig.getString(fromGroup);

            // We cannot do anything with this.
            if (valueString == null) {
                continue;
            }

            // Split value string
            if (!valueString.contains("after")) {
                continue;
            }

            String[] tempArray = valueString.split("after");
            String groupTo = tempArray[0].trim();
            String timePeriod = tempArray[1].trim();

            // Put into a new path
            paths.put(fromGroup, groupTo + ";" + timePeriod);
        }

        // Set all the paths according to Paths.yml format
        for (Entry<String, String> path : paths.entrySet()) {

            String groupFrom = path.getKey();

            String[] tempArray = path.getValue().split(";");

            String groupTo = tempArray[0];
            String timePeriod = tempArray[1];

            String pathName = groupFrom + " to " + groupTo;

            newPathsFile.set(pathName + ".prerequisites.in group.value", groupFrom);
            newPathsFile.set(pathName + ".requirements.time.value", timePeriod);
            newPathsFile.set(pathName + ".results.rank change", groupTo);
        }

        // Add a comment at the top of the file
        newPathsFile.options().indent(4);
        newPathsFile.options().header("This is a Paths.yml generated from your SimpleConfig.yml. "
                + "\nBeware that there can be errors made by the automatic transferring of formats."
                + "\n\nTo test this file with Autorank, perform the following steps:"
                + "\n1. Stop your server (very important);"
                + "\n2. Rename this file to 'Paths.yml' (without the quotation marks);"
                + "\n3. Restart your server and voilá!");

        // Save file at the end.
        newPathsFile.saveFile();

        return true;
    }

    /**
     * Convert the AdvancedConfig file to the new format of the Paths.yml file.
     *
     * @return true if correctly converted, false otherwise.
     */
    public boolean convertAdvancedConfigToPaths() {

        SimpleYamlConfiguration advancedConfig = new SimpleYamlConfiguration(plugin, "AdvancedConfig.yml",
                "AdvancedConfig");

        String newPathsFileName = "Paths_from_AdvancedConfig.yml";

        SimpleYamlConfiguration newPathsFile = new SimpleYamlConfiguration(plugin, newPathsFileName,
                "Converted paths file");

        List<ConvertiblePath> paths = new ArrayList<>();

        // Grab all the paths from the AdvancedConfig.yml
        for (String fromGroup : advancedConfig.getConfigurationSection("ranks").getKeys(false)) {

            String reqString = "ranks." + fromGroup + ".requirements";
            String resString = "ranks." + fromGroup + ".results";

            ConfigurationSection reqSection = advancedConfig.getConfigurationSection(reqString);
            ConfigurationSection resSection = advancedConfig.getConfigurationSection(resString);

            // There are no requirements, so skip this path
            if (reqSection == null) {
                continue;
            }

            // There are no results, this path is useless, so skip it.
            if (resSection == null) {
                continue;
            }

            // Store the new path in this record.
            ConvertiblePath newPath = new ConvertiblePath();

            // Set from group and path name
            newPath.setFromGroup(fromGroup);
            newPath.setPathName(fromGroup);

            // Let's grab all the requirements
            for (String requirement : reqSection.getKeys(false)) {
                String valueString = "";

                Object tempObject = advancedConfig.get(reqString + "." + requirement + ".value");

                if (tempObject != null) {
                    valueString = tempObject.toString();
                } else {
                    valueString = advancedConfig.get(reqString + "." + requirement).toString();
                }

                newPath.addRequirement(requirement, valueString);
            }

            // Now let's grab all the results
            for (String result : resSection.getKeys(false)) {
                String valueString = "";

                Object tempObject = advancedConfig.get(resString + "." + result + ".value");

                if (tempObject != null) {
                    valueString = tempObject.toString();
                } else {
                    valueString = advancedConfig.get(resString + "." + result).toString();
                }

                newPath.addResult(result, valueString);
            }

            // Now add this path to the list of convertible paths
            paths.add(newPath);
        }

        // Set all the paths according to Paths.yml format
        for (ConvertiblePath path : paths) {

            String groupFrom = path.getFromGroup();
            String pathName = path.getPathName();

            // Set only prerequisite
            newPathsFile.set(pathName + ".prerequisites.in group.value", groupFrom);

            // Now begin saving all requirements
            for (Entry<String, String> entry : path.getRequirements().entrySet()) {

                String reqName = entry.getKey();
                String reqValue = entry.getValue();

                // Value is an integer, so set it as an integer.
                if (reqValue.matches("^-?\\d+$")) {
                    newPathsFile.set(pathName + ".requirements." + reqName + ".value", Integer.parseInt(reqValue));
                } else {
                    newPathsFile.set(pathName + ".requirements." + reqName + ".value", reqValue);
                }
            }

            // Save all results
            for (Entry<String, String> entry : path.getResults().entrySet()) {

                String resName = entry.getKey();
                String resValue = entry.getValue();

                newPathsFile.set(pathName + ".results." + resName, resValue);
            }
        }

        // Add a comment at the top of the file
        newPathsFile.options().indent(4);
        newPathsFile.options().header("This is a Paths.yml generated from your AdvancedConfig.yml. "
                + "\nBeware that there can be errors made by the automatic transferring of formats."
                + "\n\nTo test this file with Autorank, perform the following steps:"
                + "\n1. Stop your server (very important);"
                + "\n2. Rename this file to 'Paths.yml' (without the quotation marks);"
                + "\n3. Restart your server and voilá!");

        // Save file at the end.
        newPathsFile.saveFile();

        return true;
    }
}

class ConvertiblePath {

    private String pathName;

    private String fromGroup;

    private Map<String, String> requirements = new HashMap<>();

    private Map<String, String> results = new HashMap<>();

    public String getPathName() {
        return pathName;
    }

    public void setPathName(String pathName) {
        this.pathName = pathName;
    }

    public void addRequirement(String key, String value) {
        requirements.put(key, value);
    }

    public void addResult(String key, String value) {
        results.put(key, value);
    }

    public Map<String, String> getRequirements() {
        return requirements;
    }

    public Map<String, String> getResults() {
        return results;
    }

    public String getFromGroup() {
        return fromGroup;
    }

    public void setFromGroup(String fromGroup) {
        this.fromGroup = fromGroup;
    }
}
