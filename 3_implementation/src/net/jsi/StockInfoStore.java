package net.jsi;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public class StockInfoStore implements Serializable, Cloneable {

	
	private static final long serialVersionUID = 1L;

	
	private static Map<String, PricePool> pricePools = Util.newMap(0);
	
	
	protected Map<String, StockInfo> stores = Util.newMap(0);
	
	
	public StockInfoStore() {

	}

	
	protected int size() {
		return stores.size();
	}
	
	
	protected Set<String> codes() {
		return stores.keySet();
	}
	
	
	public StockInfo get(String code) {
		if (stores.containsKey(code))
			return stores.get(code);
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
			stores.put(code, si);
			return si;
		}
	}
	
	
	protected StockInfo remove(String code) {
		return stores.remove(code);
	}
	
	
//	private Price getLastPrice(String code) {
//		if (stores.containsKey(code))
//			return stores.get(code).getLastPrice();
//		else
//			return null;
//	}
//	
//
//	private Price getPrice(String code, long timePoint) {
//		if (stores.containsKey(code))
//			return stores.get(code).getPrice(timePoint);
//		else
//			return null;
//	}
//	
//	
//	private List<Price> getPrices(String code, long timeInterval) {
//		if (stores.containsKey(code))
//			return stores.get(code).getPrices(timeInterval);
//		else
//			return Util.newList(0);
//	}


	protected static PricePool getPricePool(String code) {
		if (pricePools.containsKey(code))
			return pricePools.get(code);
		else {
			PricePool pricePool = new PricePool(code);
			pricePools.put(code, pricePool);
			return pricePool;
		}
	}
	
	
}
