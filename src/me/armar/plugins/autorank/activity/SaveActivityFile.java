package me.armar.plugins.autorank.activity;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.config.SimpleYamlConfiguration;

public class SaveActivityFile implements Runnable{

    private SimpleYamlConfiguration file;
    
    public SaveActivityFile(SimpleYamlConfiguration file) {
        this.file = file;
    }
    
    @Override
    public void run() {
        
        Autorank.getAutorank().debugMessage("Saving " + file.getInternalFile().getName());;
        
        file.saveFile();
        
    }

 
}
