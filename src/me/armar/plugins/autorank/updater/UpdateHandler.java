package me.armar.plugins.autorank.updater;

import me.armar.plugins.autorank.Autorank;

public class UpdateHandler {

	private Autorank plugin;
	
	public UpdateHandler(Autorank instance) {
		plugin = instance;
	}
	
	private Updater updater;
	
	public void setUpdater(Updater updater) {
		this.updater = updater;
	}
	
	public Updater getUpdater() {
		return updater;
	}
	
	public boolean doCheckForNewVersion() {
		return plugin.getAdvancedConfig().getBoolean("auto-updater.check-for-new-versions");
	}
	
	public boolean isUpdateAvailable() {
		// Check for new version
		return plugin.checkForUpdate();
	}
}
