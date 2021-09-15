/**
 * JSI: JAGGED STRATEGY INVESTMENT 
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: jsi.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.jsi;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public interface Universe extends Market, Serializable, Cloneable {

	
	int size();
	

	List<String> names();
	
	
	Market get(int index);
	
	
	Market get(String name);

		
	double calcInvestAmount(long timeInterval);

		
	QueryEstimator query(String name, Market refMarket);

	
	int lookup(String name);

	
	boolean add(Market market);
	
	
	Market remove(int index);
	
	
	Market set(int index, Market market);
	
	
	void addDefaultStockCodes(Collection<String> defaultStockCodes);

	
	List<String> getDefaultCategories();

	
	void addDefaultCategories(Collection<String> defaultCategories);

	
	Market newMarket(String name, double leverage, double unitBias);


	MarketImpl c(Market market);
	
	
	Market getWatchMarket(String name);
	
	
	StockInfoStore getPlaceStore();
	
	
	Market getPlaceMarket(String name);
	
	
}
