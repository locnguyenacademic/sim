package net.jsi.remote;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

import net.jsi.Market;

public interface MarketRemote extends Remote, Serializable, Cloneable {

	
	boolean sync(Market otherMarket) throws RemoteException;

	
	MarketRemote export(int serverPort) throws RemoteException;


	boolean unexport() throws RemoteException;


}
