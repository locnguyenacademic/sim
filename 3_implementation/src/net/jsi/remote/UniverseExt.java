package net.jsi.remote;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import net.jsi.Market;
import net.jsi.MarketImpl;
import net.jsi.Universe;
import net.jsi.UniverseImpl;

public class UniverseExt extends UniverseImpl implements UniverseRemote {

	private static final long serialVersionUID = 1L;

	
	private MarketRemote exportedStub = null;
	
	
	public UniverseExt() {
		super();
	}

	
	@Override
	public Market newMarket(String name, double leverage, double unitBias) {
		Market superMarket = this;
		MarketExt market = new MarketExt(name, leverage, unitBias) {

			private static final long serialVersionUID = 1L;

			@Override
			public Market getSuperMarket() {
				return superMarket;
			}
			
		};
		market.setTimeViewInterval(this.getTimeViewInterval());
		market.setTimeValidInterval(this.getTimeValidInterval());
		return market;
	}


	@Override
	public boolean sync(Market otherMarket) throws RemoteException {
		if (!(otherMarket instanceof Universe)) return false;
		Universe otherUniverse = (Universe)otherMarket;
		if (!this.getName().equals(otherUniverse.getName())) return false;
		
		long timeViewInterval = 0;
		if (this.getTimeViewInterval() > 0 && otherUniverse.getTimeViewInterval() > 0)
			timeViewInterval = Math.max(this.getTimeViewInterval(), otherUniverse.getTimeViewInterval());
		this.setTimeViewInterval(timeViewInterval);
		
		long timeValidInterval = 0;
		if (this.getTimeValidInterval() > 0 && otherUniverse.getTimeValidInterval() > 0)
			timeValidInterval = Math.max(this.getTimeValidInterval(), otherUniverse.getTimeValidInterval());
		this.setTimeValidInterval(timeValidInterval);

		this.defaultStockCodes.addAll(otherUniverse.getDefaultStockCodes());
		this.defaultCategories.addAll(otherUniverse.getDefaultCategories());
		
		for (int i = 0; i < otherUniverse.size(); i++) {
			MarketImpl other = otherUniverse.c(otherUniverse.get(i));
			if (other == null) continue;
			MarketImpl m = this.c(this.get(other.getName()));
			if (m == null) m = (MarketImpl)newMarket(other.getName(), other.getLeverage(), other.getUnitBias());
			
			if (m instanceof MarketExt) {
				((MarketExt)m).sync(other);
				if (this.lookup(m.getName()) < 0) this.add(m);
			}
		}
		
		
		return true;
	}


	@Override
	public boolean sync(Universe otherUniverse) throws RemoteException {
		return sync((Market)otherUniverse);
	}


	public boolean sync(UniverseRemote remoteUniverse) {
		try {
			List<String> marketNames = remoteUniverse.getMarketNames();
			for (String marketName : marketNames) {
				Market remoteMarket = remoteUniverse.getMarket(marketName);
				if (remoteMarket == null || !(remoteMarket instanceof MarketImpl)) continue;
				if (lookup(remoteMarket.getName()) >= 0) continue;
				
				MarketExt market = (MarketExt)newMarket(remoteMarket.getName(), remoteMarket.getLeverage(), remoteMarket.getUnitBias());;
				market.sync(remoteMarket);
				add(market);
			}
			
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	

	@Override
	public List<String> getMarketNames() throws RemoteException {
		return getMarketNames();
	}


	@Override
	public Market getMarket(String marketName) throws RemoteException {
		return get(marketName);
	}


	@Override
	public MarketRemote export(int serverPort) throws RemoteException {
		if (exportedStub != null) return exportedStub;
		try {
			exportedStub = (MarketRemote)UnicastRemoteObject.exportObject(this, serverPort);
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
