package net.hudup.server.ext;

import java.rmi.RemoteException;

import net.hudup.core.client.PowerServer;
import net.jsi.remote.UniverseRemote;

public interface MultitaskServer extends PowerServer {

	
	UniverseRemote getInvestor() throws RemoteException;
	
	
}
