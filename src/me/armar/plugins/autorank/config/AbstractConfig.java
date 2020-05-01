package me.armar.plugins.autorank.config;

import me.armar.plugins.autorank.Autorank;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Represents a config file of Autorank
 */
public abstract class AbstractConfig {

    private SimpleYamlConfiguration configFile;
    private Autorank plugin;
    private String fileName;

    private boolean isLoaded = false;

    /**
     * Create a new config file.
     */
    public void createNewFile() throws InvalidConfigurationException {
        configFile = new SimpleYamlConfiguration(plugin, fileName, fileName);

        plugin.debugMessage("File loaded (" + fileName + ")");
    }


    /**
     * Get the YML file.
     *
     * @return
     */
    public FileConfiguration getConfig() {
        if (configFile != null) {
            return configFile;
        }

        return null;
    }

    /**
     * Reload the YML file.
     */
    public void reloadConfig() {
        if (configFile != null) {
            configFile.reloadFile();
        }
    }

    /**
     * Save the YML file.
     */
    public void saveConfig() {
        if (configFile == null) {
            return;
        }

        configFile.saveFile();
    }

    /**
     * Load the YML file.
     *
     * @return true if the file is loaded correctly. False if an error occurred during loading.
     */
    public boolean loadConfig() {
        try {
            this.createNewFile();
            isLoaded = true;
        } catch (Exception e) {
            isLoaded = false;
            return false;
        }

        return true;
    }

    /**
     * Check whether this config file is loaded. Try using {@link #loadConfig()} to see if the file can be loaded
     * properly.
     *
     * @return true if it is loaded, false otherwise.
     */
    public boolean isLoaded() {
        return isLoaded;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Autorank getPlugin() {
        return plugin;
    }

    public void setPlugin(Autorank plugin) {
        this.plugin = plugin;
    }
}
