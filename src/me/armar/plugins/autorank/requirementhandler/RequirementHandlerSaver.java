package me.armar.plugins.autorank.requirementhandler;

/**
 * Simple runnable task that saves the playerdata.yml every minute.
 * <p>
 * Date created: 17:59:49 22 mei 2014
 * 
 * @author Staartvin
 * 
 */
public class RequirementHandlerSaver implements Runnable {

	private RequirementHandler requirementHandler;

	public RequirementHandlerSaver(RequirementHandler reqHandler) {
		requirementHandler = reqHandler;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		requirementHandler.saveConfig();

	}

}
