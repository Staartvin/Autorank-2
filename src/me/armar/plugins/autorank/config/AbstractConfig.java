package me.armar.plugins.autorank.config;

import me.armar.plugins.autorank.Autorank;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Represents a config file of Autorank
 */
public abstract class AbstractConfig {

    private SimpleYamlConfiguration configFile;
    private Autorank plugin;
    private String fileName;

    /**
     * Create a new config file.
     */
    public void createNewFile() {
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
     */
    public void loadConfig() {
        this.createNewFile();
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public Autorank getPlugin() {
        return plugin;
    }

    public void setPlugin(Autorank plugin) {
        this.plugin = plugin;
    }
}
