package net.hudup;

import net.hudup.core.client.PowerServer;
import net.hudup.server.ext.MultitaskServerImpl;

public class MultitaskServer extends Server {

	
	public MultitaskServer() {

	}

	
	@Override
	protected PowerServer create(boolean external) {
		return MultitaskServerImpl.create();
	}


	@Override
	public String getName() {
		return "Server Multitask";
	}


}
