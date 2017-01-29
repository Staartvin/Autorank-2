package me.armar.plugins.autorank.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ConcurrentModificationException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * This represents any YAML file that Autorank uses. <br>
 * It is used for the data.yml, playerdata.yml and daily/monthly/weekly data
 * files.
 * 
 * @author Staartvin
 *
 */
public class SimpleYamlConfiguration extends YamlConfiguration {

    File file;

    /**
     * Create a new YAML file.
     * 
     * @param plugin
     *            Plugin to create it for.
     * @param fileName
     *            Path of the file.
     * @param name
     *            Name of the file that is used to show in the console.
     */
    public SimpleYamlConfiguration(final JavaPlugin plugin, final String fileName, final String name) {
        /*
         * accepts null as configDefaults -> check for resource and copies it if
         * found, makes an empty config if nothing is found
         */
        final String folderPath = plugin.getDataFolder().getAbsolutePath() + File.separator;
        file = new File(folderPath + fileName);

        if (!file.exists()) {
            if (plugin.getResource(fileName) != null) {
                plugin.saveResource(fileName, false);
                plugin.getLogger().info("New " + name + " file copied from jar");
                try {
                    this.load(file);
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            try {
                this.load(file);
                plugin.getLogger().info(name + " file loaded");
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Get the internal YAML file.
     */
    public File getInternalFile() {
        return file;
    }

    /**
     * Load the YAML file.
     */
    public void loadFile() throws FileNotFoundException, IOException, InvalidConfigurationException {
        try {
            this.load(file);
        } catch (final FileNotFoundException e) {
            throw e;
        } catch (final IOException e) {
            throw e;
        } catch (final InvalidConfigurationException e) {
            throw e;
        }
    }

    /**
     * Reload the YAML file.
     */
    public void reloadFile() {
        try {
            loadFile();
        } catch (FileNotFoundException e) {
           // Catch exception, do nothing. 
        } catch (IOException e) {
            // Catch exception, do nothing. 
        } catch (InvalidConfigurationException e) {
            // Catch exception, do nothing. 
        }
        
        saveFile();
    }

    /**
     * Save the YAML file.
     */
    public void saveFile() {
        try {
            this.save(file);
        } catch (final ConcurrentModificationException e) {
            saveFile();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
}
