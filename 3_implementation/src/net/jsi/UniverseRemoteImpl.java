package net.jsi;

import java.io.File;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class UniverseRemoteImpl implements UniverseRemote, Serializable, Cloneable {


	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;


	protected Universe universe = null;
	
	
	protected boolean exported = false;
	
	
	public UniverseRemoteImpl(Universe universe) {
		super();
		this.universe = universe;
	}

	
	@Override
	public String getName() throws RemoteException {
		return universe.getName();
	}


	@Override
	public synchronized long getTimeViewInterval() throws RemoteException {
		return universe.getTimeViewInterval();
	}


	@Override
	public synchronized long getTimeValidInterval() throws RemoteException {
		return universe.getTimeValidInterval();
	}


	@Override
	public synchronized List<String> getDefaultStockCodes() throws RemoteException {
		return universe.getDefaultStockCodes();
	}


	@Override
	public synchronized List<String> getDefaultCategories() throws RemoteException {
		return universe.getDefaultCategories();
	}


	@Override
	public synchronized boolean sync(Universe otherUniverse, long timeInterval) throws RemoteException {
		return sync(otherUniverse, timeInterval, isAdminAccount());
	}


	private synchronized boolean sync(Universe otherUniverse, long timeInterval, boolean removeRedundant) {
		if (otherUniverse == null || !universe.getName().equals(otherUniverse.getName())) return false;
		universe.setBasicInfo(otherUniverse, removeRedundant);
		
		for (int i = 0; i < otherUniverse.size(); i++) {
			MarketImpl otherMarket = otherUniverse.c(otherUniverse.get(i));
			if (otherMarket == null) continue;
			MarketImpl market = universe.c(universe.get(otherMarket.getName()));
			if (market == null)
				market = universe.c(universe.newMarket(otherMarket.getName(), otherMarket.getLeverage(), otherMarket.getUnitBias()));
			
			if (market != null) {
				market.sync(otherMarket, timeInterval, removeRedundant);
				if (universe.lookup(market.getName()) < 0) universe.add(market);
			}
		}
		
		return true;
	}

	
	public synchronized boolean apply() {
		return universe.apply();
	}
	
	
	@Override
	public synchronized void sortCodes() throws RemoteException {
		universe.sortCodes();
	}


	@Override
	public void sortCodes(String marketName) throws RemoteException {
		universe.sortCodes(marketName);
	}


	public synchronized boolean open(File workingDir) {
		return universe.open(workingDir);
	}
	
	
	public synchronized void save(File workingDir) {
		universe.save(workingDir);
	}

	
	public synchronized void saveBackup(File workingDir) {
		universe.saveBackup(workingDir);
	}

	
	@Override
	public synchronized List<String> getMarketNames() throws RemoteException {
		return universe.names();
	}


	@Override
	public synchronized Market getMarket(String marketName) throws RemoteException {
		return universe.get(marketName);
	}


	public synchronized Universe getUniverse() {
		return universe;
	}
	
	
	protected boolean isAdminAccount() {
		return false;
	}
	
	
	@Override
	public synchronized boolean rename(String marketName, String newMarketName) throws RemoteException {
		return universe.rename(marketName, newMarketName);
	}


	@Override
	public synchronized boolean renamePricePool(String code, String newCode) throws RemoteException {
		return universe.renamePricePool(code, newCode) != null;
	}


	@Override
	public synchronized boolean export(int serverPort) throws RemoteException {
		if (exported) return false;
		try {
			return (exported = (UnicastRemoteObject.exportObject(this, serverPort) != null));
		}
		catch (Throwable e) {Util.trace(e);}
		
		return false;
	}


	@Override
	public synchronized boolean unexport() throws RemoteException {
		if (exported) {
			try {
	        	return !(exported = !UnicastRemoteObject.unexportObject(this, true));
			}
			catch (Throwable e) {Util.trace(e);}
			return false;
		}
		else
			return false;
	}


}
