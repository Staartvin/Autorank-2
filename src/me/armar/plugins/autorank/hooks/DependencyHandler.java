package me.armar.plugins.autorank.hooks;

import org.bukkit.plugin.Plugin;

/**
 * An interface class to use for a single AutorankDependency <br>
 * This class is used for single plugins only.
 * <p>
 * Date created: 17:52:02 4 mrt. 2014
 * 
 * @author Staartvin
 * 
 */
public interface DependencyHandler {

	/**
	 * Get the main class of the AutorankDependency. <br>
	 * <b>Note that you still have to cast it to the correct plugin.</b>
	 * 
	 * @return main class of plugin, or null if not found.
	 */
	public Plugin get();

	/**
	 * Check whether Autorank has hooked this AutorankDependency and thus can use it.
	 * 
	 * @return true if Autorank hooked into it, false otherwise.
	 */
	public boolean isAvailable();

	/**
	 * Check to see if this AutorankDependency is running on this server
	 * 
	 * @return true if it is, false otherwise.
	 */
	public boolean isInstalled();

	/**
	 * Setup the hook between this AutorankDependency and Autorank
	 * 
	 * @param verbose Whether to show output or not
	 * @return true if correctly setup, false otherwise.
	 */
	public boolean setup(boolean verbose) throws Exception;
}
