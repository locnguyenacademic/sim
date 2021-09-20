package net.jsi;

import java.io.File;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class UniverseRemoteImpl implements UniverseRemote {


	protected Universe universe = null;
	
	
	private UniverseRemote exportedStub = null;
	
	
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


	private Market newMarket(String name, double leverage, double unitBias) {
		MarketImpl market = new MarketImpl(name, leverage, unitBias) {

			private static final long serialVersionUID = 1L;

			@Override
			public Market getSuperMarket() {
				return universe;
			}
			
		};
		market.setTimeViewInterval(universe.getTimeViewInterval());
		market.setTimeValidInterval(universe.getTimeValidInterval());
		return market;
	}


	@Override
	public synchronized boolean sync(Universe otherUniverse, boolean removeRedundant) throws RemoteException {
		if (otherUniverse == null || !universe.getName().equals(otherUniverse.getName())) return false;
		universe.setBasicInfo(otherUniverse, removeRedundant);
		
		for (int i = 0; i < otherUniverse.size(); i++) {
			MarketImpl otherMarket = otherUniverse.c(otherUniverse.get(i));
			if (otherMarket == null) continue;
			MarketImpl market = universe.c(universe.get(otherMarket.getName()));
			if (market == null)
				market = universe.c(newMarket(otherMarket.getName(), otherMarket.getLeverage(), otherMarket.getUnitBias()));
			
			if (market != null) {
				market.sync(otherMarket, removeRedundant);
				if (universe.lookup(market.getName()) < 0) universe.add(market);
			}
		}
		
		return true;
	}


	public synchronized void open(File workingDir) {
		universe.open(workingDir);
	}
	
	
	public synchronized void save(File workingDir) {
		universe.save(workingDir);
	}

	
	@Override
	public synchronized List<String> getMarketNames() throws RemoteException {
		return universe.names();
	}


	@Override
	public synchronized Market getMarket(String marketName) throws RemoteException {
		return universe.get(marketName);
	}


	public Universe getUniverse() {
		return universe;
	}
	
	
	@Override
	public UniverseRemote export(int serverPort) throws RemoteException {
		if (exportedStub != null) return exportedStub;
		try {
			exportedStub = (UniverseRemote)UnicastRemoteObject.exportObject(this, serverPort);
		}
		catch (Exception e) {e.printStackTrace();}
		
		return exportedStub;
	}


	@Override
	public boolean unexport() throws RemoteException {
		if (exportedStub != null) {
			try {
	        	UnicastRemoteObject.unexportObject(this, true);
	        	exportedStub = null;
	        	return true;
			}
			catch (Throwable e) {e.printStackTrace();}
			return false;
		}
		else
			return false;
	}


}
