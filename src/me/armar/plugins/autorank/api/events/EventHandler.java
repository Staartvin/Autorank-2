package me.armar.plugins.autorank.api.events;

import me.armar.plugins.autorank.Autorank;

public class EventHandler {

	@SuppressWarnings("unused")
	private Autorank plugin;
	
	// Check if a plugin cancelled the promote event;
	public static boolean doNotPromote = false;
	
	public EventHandler(Autorank instance) {
		plugin = instance;
	}
	
	public void doNotPromote(boolean status) {
		doNotPromote = status;
	}
	
}
