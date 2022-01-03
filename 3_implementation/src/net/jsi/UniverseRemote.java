package net.jsi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface UniverseRemote extends Remote {

	
	String getName() throws RemoteException;
	
	
	long getTimeViewInterval() throws RemoteException;

		
	long getTimeValidInterval() throws RemoteException;

	
	public List<String> getDefaultStockCodes() throws RemoteException;
	
	
	public List<String> getDefaultCategories() throws RemoteException;

		
	boolean sync(Universe otherUniverse, long timeInterval) throws RemoteException;

	
	void sortCodes() throws RemoteException;
	
	
	void sortCodes(String marketName) throws RemoteException;

	
	List<String> getMarketNames() throws RemoteException;
	
	
	Market getMarket(String marketName) throws RemoteException;
	
	
	boolean rename(String marketName, String newMarketName) throws RemoteException;

	
	boolean renamePricePool(String code, String newCode) throws RemoteException;

	
	boolean export(int serverPort) throws RemoteException;


	boolean unexport() throws RemoteException;


}
