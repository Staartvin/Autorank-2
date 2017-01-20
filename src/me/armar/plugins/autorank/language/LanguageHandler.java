package me.armar.plugins.autorank.language;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.armar.plugins.autorank.Autorank;

/**
 * This class
 * 
 * @author Staartvin Return the language being used.
 */
public class LanguageHandler {

    private FileConfiguration languageConfig;
    private File languageConfigFile;
    private final Autorank plugin;

    public LanguageHandler(final Autorank autorank) {
        plugin = autorank;
    }

    /**
     * Create a new language file.
     */
    public void createNewFile() {
        reloadConfig();
        saveConfig();

        Lang.setFile(languageConfig);

        loadConfig();

        plugin.getLogger().info("Language file loaded (lang.yml)");
    }

    public FileConfiguration getConfig() {
        if (languageConfig == null) {
            this.reloadConfig();
        }
        return languageConfig;
    }

    public void loadConfig() {

        languageConfig.options().header("Language file");

        for (final Lang value : Lang.values()) {
            languageConfig.addDefault(value.getPath(), value.getDefault());
        }

        languageConfig.options().copyDefaults(true);
        saveConfig();
    }

    @SuppressWarnings("deprecation")
    public void reloadConfig() {
        if (languageConfigFile == null) {
            languageConfigFile = new File(plugin.getDataFolder() + "/lang", "lang.yml");
        }
        languageConfig = YamlConfiguration.loadConfiguration(languageConfigFile);

        // Look for defaults in the jar
        final InputStream defConfigStream = plugin.getResource("lang.yml");
        if (defConfigStream != null) {
            final YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            languageConfig.setDefaults(defConfig);
        }
    }

    public void saveConfig() {
        if (languageConfig == null || languageConfigFile == null) {
            return;
        }
        try {
            getConfig().save(languageConfigFile);
        } catch (final IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Could not save config to " + languageConfigFile, ex);
        }
    }
}
