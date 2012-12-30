package me.armar.plugins.autorank.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ConcurrentModificationException;
import java.util.LinkedHashMap;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class SimpleYamlConfiguration extends YamlConfiguration {

    File file;

    public SimpleYamlConfiguration(JavaPlugin plugin, String fileName, LinkedHashMap<String, Object> configDefaults, String name) {
	/*
	 * accepts null as configDefaults -> check for resource and copies it if
	 * found, makes an empty config if nothing is found
	 */
	String folderPath = plugin.getDataFolder().getAbsolutePath() + File.separator;
	file = new File(folderPath + fileName);

	if (file.exists() == false) {
	    if (configDefaults == null) {
		if (plugin.getResource(fileName) != null) {
		    plugin.saveResource(fileName, false);
		    plugin.getLogger().info("New " + name + " file copied from jar");
		    try {
			this.load(file);
		    } catch (Exception e) {
			e.printStackTrace();
		    }
		}
	    } else {
		for (String key : configDefaults.keySet()) {
		    this.set(key, configDefaults.get(key));
		}

		try {
		    this.save(file);
		    plugin.getLogger().info("New " + name + " file created");
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    }
	} else {
	    try {
		this.load(file);
		plugin.getLogger().info(name + " file loaded");
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}

    }

    public void save() {
	try {
	    this.save(file);
	} catch (ConcurrentModificationException e) {
	    save();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }
    
	public void load() {
		try {
			this.load(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}
}
