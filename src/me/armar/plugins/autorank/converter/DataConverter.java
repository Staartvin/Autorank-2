package me.armar.plugins.autorank.converter;

import java.io.File;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.data.flatfile.FlatFileManager.TimeType;

public class DataConverter {

    private Autorank plugin;

    public DataConverter(Autorank instance) {
        this.plugin = instance;
    }

    /**
     * Convert data format from Autorank 3.8 (or lower) to Autorank 4.0
     * @return true if it worked, false if it was already converted or did not work.
     */
    public boolean convertData() {
        // Convert Autorank 3.8 or lower to Autorank 4.0
        
        if (plugin.getInternalPropertiesConfig().isConvertedToNewFormat()) {
            return false;
        }
        
        plugin.getLogger().info("Autorank detected that you upgraded from an older version. It will need to convert your folders.");
        
        plugin.getLogger().info("Started converting folders of Autorank...");
        
        final String folderPath = plugin.getDataFolder().getAbsolutePath() + File.separator;
        
        // Rename data files
        if (!new File(folderPath + "/data/daily_time.yml").renameTo(new File(folderPath + "/data/Daily_time.yml"))) {
            plugin.getLogger().info("Could not rename daily_time.yml to Daily_time.yml!");
        } else {
            plugin.getLogger().info("Successfully converted Daily_time.yml!");
        }
        
        if (!new File(folderPath + "/data/weekly_time.yml").renameTo(new File(folderPath + "/data/Weekly_time.yml"))) {
            plugin.getLogger().info("Could not rename weekly_time.yml to Weekly_time.yml!");
        } else {
            plugin.getLogger().info("Successfully converted Weekly_time.yml!");
        }
        
        if (!new File(folderPath + "/data/monthly_time.yml").renameTo(new File(folderPath + "/data/Monthly_time.yml"))) {
            plugin.getLogger().info("Could not rename monthly_time.yml to Monthly_time.yml!");
        } else {
            plugin.getLogger().info("Successfully converted Monthly_time.yml!");
        }
        
        // Now move Data.yml to /data/Total_time.yml
        File totalTimeFile = new File(folderPath + "/data/Total_time.yml");
        
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
        if (!new File(folderPath + "/playerdata/playerdata.yml").renameTo(new File(folderPath + "/playerdata/PlayerData.yml"))) {
            plugin.getLogger().info("Could not rename playerdata.yml to PlayerData.yml!");
        } else {
            plugin.getLogger().info("Successfully converted playerdata.yml!");
        }
        
        plugin.getLogger().info("Conversion of Autorank is complete!");
        
        plugin.getInternalPropertiesConfig().setConvertedToNewFormat(true);
        
        // Reload file
        plugin.getFlatFileManager().getDataFile(TimeType.TOTAL_TIME).reloadFile();
        plugin.getFlatFileManager().getDataFile(TimeType.WEEKLY_TIME).reloadFile();
        plugin.getFlatFileManager().getDataFile(TimeType.MONTHLY_TIME).reloadFile();
        plugin.getFlatFileManager().getDataFile(TimeType.DAILY_TIME).reloadFile();
        
        return true;
    }
}
