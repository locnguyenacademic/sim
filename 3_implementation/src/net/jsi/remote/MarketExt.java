package net.jsi.remote;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import net.jsi.Market;
import net.jsi.MarketImpl;
import net.jsi.StockGroup;
import net.jsi.StockImpl;
import net.jsi.StockInfo;
import net.jsi.Universe;
import net.jsi.Util;

public class MarketExt extends MarketImpl implements MarketRemote {

	
	private static final long serialVersionUID = 1L;


	private MarketRemote exportedStub = null;
	
	
	public MarketExt(String name, double refLeverage, double unitBias) {
		this(name, refLeverage, unitBias, true);
	}

	
	public MarketExt(String name, double refLeverage, double unitBias, boolean createAssocMarkets) {
		super(name, refLeverage, unitBias, createAssocMarkets);
	}
	

	@Override
	public synchronized boolean sync(Market otherMarket) throws RemoteException {
		MarketImpl other = null;
		if (otherMarket instanceof MarketImpl)
			other = (MarketImpl)otherMarket;
		else {
			Universe u = this.getNearestUniverse();
			other = u != null ? (MarketImpl)u.c(otherMarket) : null;
		}
		if (other == null) return false;
		
		sync(this, other, true);
		
		if (this.getWatchMarket() != null && other.getWatchMarket() != null && this.getWatchMarket() != other.getWatchMarket())
			sync(this.getWatchMarket(), other.getWatchMarket(), false);
		
		if (this.getPlaceMarket() != null && other.getPlaceMarket() != null && this.getPlaceMarket() != other.getPlaceMarket())
			sync(this.getPlaceMarket(), other.getPlaceMarket(), true);
		
		if (this.getTrashMarket() != null && other.getTrashMarket() != null && this.getTrashMarket() != other.getTrashMarket())
			sync(this.getTrashMarket(), other.getTrashMarket(), false);
		
		
		long timeViewInterval = 0;
		if (this.getTimeViewInterval() > 0 && otherMarket.getTimeViewInterval() > 0)
			timeViewInterval = Math.max(this.getTimeViewInterval(), otherMarket.getTimeViewInterval());
		this.setTimeViewInterval(timeViewInterval);
		
		long timeValidInterval = 0;
		if (this.getTimeValidInterval() > 0 && otherMarket.getTimeValidInterval() > 0)
			timeValidInterval = Math.max(this.getTimeValidInterval(), otherMarket.getTimeValidInterval());
		this.setTimeValidInterval(timeValidInterval);

		return true;
	}
	
	
	private static boolean sync(MarketImpl thisMarket, MarketImpl otherMarket, boolean syncStore) {
		if (!thisMarket.getName().equals(otherMarket.getName())) return false;
		if (syncStore) thisMarket.getStore().sync(otherMarket.getStore());
		
		for (int i = 0; i < otherMarket.size(); i++) {
			StockGroup otherGroup = otherMarket.get(i);
			String code = otherGroup.code();
			boolean buy = otherGroup.isBuy();
			
			StockGroup thisGroup = thisMarket.get(code, buy);
			if (thisGroup == null) thisGroup = newGroup(thisMarket, code, buy);
			for (int j = 0; j < otherGroup.size(); j++) {
				StockImpl otherStock = otherMarket.c(otherGroup.get(j));
				if (otherStock == null) continue;
				
				long takenTimePoint = otherStock.getTakenTimePoint(0);
				StockImpl thisStock = thisMarket.c(thisGroup.get(takenTimePoint));
				if (thisStock == null)
					otherMarket.addStock(code, buy, otherStock.getLeverage(), otherStock.getVolume(0, true), takenTimePoint);
				else {
					thisStock.setVolume(otherStock.getVolume(0, true));
					thisStock.take(0, takenTimePoint);
				}
			}
		}
		
		
		List<StockGroup> removedGroups = Util.newList(0);
		for (int i = 0; i < thisMarket.size(); i++) {
			StockGroup thisGroup = thisMarket.get(i);
			String code = thisGroup.code();
			boolean buy = thisGroup.isBuy();

			StockGroup otherGroup = otherMarket.get(code, buy);
			if (otherGroup == null) {
				removedGroups.add(thisGroup);
				continue;
			}
			
			for (int j = 0; j < thisGroup.size(); j++) {
				StockImpl thisStock = thisMarket.c(thisGroup.get(j));
				if (thisStock == null) continue;
				
				long takenTimePoint = thisStock.getTakenTimePoint(0);
				StockImpl otherStock = otherMarket.c(otherGroup.get(takenTimePoint));
				if (otherStock == null) thisGroup.remove(thisStock);
			}
			
			if (thisGroup.size() == 0) removedGroups.add(thisGroup);
		}
		
		for (StockGroup removedGroup : removedGroups) thisMarket.remove(removedGroup);
		
		return true;
	}

	
	private static StockGroup newGroup(Market thisMarket, String code, boolean buy) {
		StockInfo info = thisMarket.getStore().getCreate(code);
		if (info == null) return null;
		
		StockGroup group = new StockGroup(code, buy) {
			private static final long serialVersionUID = 1L;
			
			@Override
			public Market getSuperMarket() {
				return thisMarket;
			}
		};
		
		return group;
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
