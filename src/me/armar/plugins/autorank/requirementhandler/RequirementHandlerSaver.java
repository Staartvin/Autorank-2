package me.armar.plugins.autorank.requirementhandler;

import java.util.ConcurrentModificationException;

/**
 * Simple runnable task that saves the playerdata.yml every minute.
 * <p>
 * Date created: 17:59:49 22 mei 2014
 * 
 * @author Staartvin
 * 
 */
public class RequirementHandlerSaver implements Runnable {

	private final RequirementHandler requirementHandler;

	public RequirementHandlerSaver(final RequirementHandler reqHandler) {
		requirementHandler = reqHandler;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			requirementHandler.saveConfig();
		} catch (final ConcurrentModificationException e) {
			requirementHandler.saveConfig();
		}

	}

}
