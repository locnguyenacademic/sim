package net.jsi.remote;

import java.rmi.RemoteException;
import java.util.List;

import net.jsi.Market;
import net.jsi.Universe;

public interface UniverseRemote extends MarketRemote {

	
	boolean sync(Universe otherUniverse) throws RemoteException;

	
	@Override
	boolean sync(Market otherMarket) throws RemoteException;

	
	List<String> getMarketNames() throws RemoteException;
	
	
	Market getMarket(String marketName) throws RemoteException;
	
	
	@Override
	MarketRemote export(int serverPort) throws RemoteException;


	@Override
	boolean unexport() throws RemoteException;


}
