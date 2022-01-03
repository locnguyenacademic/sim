package net.jsi;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StockInfoStore implements Serializable, Cloneable {

	
	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;

	
	private static Map<String, PricePool> pricePools = Util.newMap(0);
	
	
	private static Map<String, PricePool> placePricePools = Util.newMap(0);
	
	
	private Map<String, StockInfo> infos = Util.newMap(0);
	
	
	public StockInfoStore() {

	}

	
	protected int size() {
		return infos.size();
	}
	
	
	public Set<String> codes() {
		return infos.keySet();
	}
	
	
	public StockInfo get(String code) {
		if (infos.containsKey(code))
			return infos.get(code);
		else
			return null;
	}
	
	
	public StockInfo getCreate(String code) {
		if (infos.containsKey(code))
			return infos.get(code);
		else
			return create(code);
	}
	
	
	protected StockInfo create(String code) {
		if (code == null || code.isEmpty()) return null;
		StockInfo si = new StockInfo(code);
		return set(code, si);
	}
	
	
	protected StockInfo set(String code, StockInfo si) {
		if (code == null || si == null)
			return null;
		else {
			infos.put(code, si);
			return si;
		}
	}
	
	
	public StockInfo remove(String code) {
		return infos.remove(code);
	}
	
	
	public Price getLastPrice(String code) {
		if (infos.containsKey(code))
			return infos.get(code).getLastPrice();
		else
			return null;
	}
	

	public Price getPriceByTimePoint(String code, long timePoint) {
		if (infos.containsKey(code))
			return infos.get(code).getPriceByTimePoint(timePoint);
		else
			return null;
	}
	
	
	protected List<Price> getPrices(String code, long timeInterval) {
		if (infos.containsKey(code))
			return infos.get(code).getPrices(timeInterval);
		else
			return Util.newList(0);
	}


	public boolean addPrice(String code, Price price) {
		StockInfo si = getCreate(code);
		if (si != null)
			return si.addPrice(price);
		else
			return false;
	}
	
	
	public boolean addPriceWithoutDuplicateTime(String code, Price price) {
		StockInfo si = getCreate(code);
		if (si != null)
			return si.addPriceWithoutDuplicateTime(price);
		else
			return false;
	}

	
	public void sync(StockInfoStore otherStore, long timeInterval, boolean removeRedundant) {
		Set<String> otherCodes = otherStore.codes();
		for (String otherCode : otherCodes) {
			StockInfo otherInfo = otherStore.get(otherCode);
			StockInfo thisInfo = this.getCreate(otherCode);
			thisInfo.sync(otherInfo, timeInterval, removeRedundant);
		}
		
		if (!removeRedundant) return;
		
		Set<String> thisCodes = Util.newSet(0);
		thisCodes.addAll(this.codes());
		for (String thisCode : thisCodes) {
			if (otherStore.get(thisCode) == null) this.remove(thisCode);
		}
	}
	
	
	public void retain(StockInfoStore referredStore, long timeInterval, boolean update) {
		Set<String> thisCodes = Util.newSet(0);
		thisCodes.addAll(this.codes());
		for (String thisCode : thisCodes) {
			StockInfo thisInfo = this.get(thisCode);
			StockInfo referredInfo = referredStore.get(thisCode);
			if (referredInfo == null)
				this.remove(thisCode);
			else
				thisInfo.retain(referredInfo, timeInterval, update);
		}
	}
	
	
	public void cutPrices(long timeInterval) {
		Set<String> codes = codes();
		for (String code : codes) {
			StockInfo si = get(code);
			si.cutPrices(timeInterval);
		}
	}

	
	public StockInfoStore pricePoolSync() {
		StockInfoStore clonedStore = new StockInfoStore();
		Set<String> codes = codes();
		for (String code : codes) {
			try {
				StockInfo clonedInfo = get(code).pricePoolSync();
				if (clonedInfo != null) clonedStore.set(code, clonedInfo);
			}
			catch (Throwable e) {
				Util.trace(e);
			}
		}
		
		return clonedStore;
	}
	
	
	protected static Set<String> getPricePoolCodes() {
		return pricePools.keySet();
	}
	
	
	public static PricePool getPricePool(String code) {
		if (pricePools.containsKey(code))
			return pricePools.get(code);
		else
			return null;
	}

	
	protected static PricePool getCreatePricePool(String code) {
		PricePool pricePool = getPricePool(code);
		if (pricePool != null) return pricePool;
		
		pricePool = new PricePool(code);
		pricePools.put(code, pricePool);
		return pricePool;
	}
	
	
	public static PricePool renamePricePool(String code, String newCode) {
		if (code == null || newCode == null) return null;
		
		if (pricePools.containsKey(newCode)) return null;
		PricePool pricePool = getPricePool(code);
		if (pricePool == null) return null;
		if (!pricePool.rename(newCode)) return null;
		pricePools.remove(code);
		pricePools.put(newCode, pricePool);
		
		if (placePricePools.containsKey(newCode)) return pricePool;
		PricePool placePricePool = getPlacePricePool(code);
		if (placePricePool == null) return pricePool;
		if (!placePricePool.rename(newCode)) return pricePool;
		placePricePools.remove(code);
		placePricePools.put(newCode, placePricePool);
		
		return pricePool;
	}
	

	protected static PricePool removePricePool(String code) {
		if (pricePools.containsKey(code))
			return pricePools.remove(code);
		else
			return null;
	}

	
	protected static PricePool getPlacePricePool(String code) {
		if (placePricePools.containsKey(code))
			return placePricePools.get(code);
		else
			return null;
	}

	
	protected static PricePool getCreatePlacePricePool(String code) {
		if (placePricePools.containsKey(code))
			return placePricePools.get(code);
		else {
			PricePool pricePool = new PricePool(code);
			placePricePools.put(code, pricePool);
			return pricePool;
		}
	}

	
}
