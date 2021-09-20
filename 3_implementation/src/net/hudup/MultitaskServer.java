package net.hudup;

import net.hudup.core.client.PowerServer;

/**
 * This class starts the multi-task server.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class MultitaskServer extends Server {

	
	/**
	 * The main method to start server.
	 * @param args The argument parameter of main method. It contains command line arguments.
	 */
	public static void main(String[] args) {
		new MultitaskServer().run(args);
	}

	
	/**
	 * Default constructor.
	 */
	public MultitaskServer() {

	}

	
	@Override
	protected PowerServer create(boolean external) {
		return net.hudup.server.ext.MultitaskServer.create();
	}


	@Override
	public String getName() {
		return "Server multitask";
	}


}
