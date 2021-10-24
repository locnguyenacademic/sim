/**
 * JSI: JAGGED STRATEGY INVESTMENT 
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: jsi.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.jsi;

import java.io.File;
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
	
	
	boolean remove(Market market);
	
	
	boolean rename(String marketName, String newMarketName);

	
	Market set(int index, Market market);
	
	
	void addDefaultStockCodes(Collection<String> defaultStockCodes);

	
	List<String> getDefaultCategories();

	
	void addDefaultCategories(Collection<String> defaultCategories);

	
	Market newMarket(String name, double leverage, double unitBias);


	MarketImpl c(Market market);
	
	
	Market getWatchMarket(String name);
	
	
	Market getPlaceMarket(String name);
	

	StockInfoStore getCreateStore(String name);
	
	
	StockInfoStore getCreatePlaceStore(String name);
	
	
	PricePool getPricePool(String code);
	
	
	PricePool getCreatePricePool(String code);

	
	PricePool removePricePool(String code);

	
	void setTimeViewInterval(long timeViewInterval);

		
	void setTimeValidInterval(long timeValidInterval);
	
	
	void setBasicInfo(Universe other, boolean removeRedundant);
	
	
	void setBasicInfo(UniverseRemote other, boolean removeRedundant);

		
	boolean apply();

		
	void sortCodes();

		
	void sortCodes(String marketName);

	
	boolean open(File workingDir);

	
	boolean save(File workingDir);

	
	boolean saveBackup(File workingDir);

	
	boolean sync(UniverseRemote remoteUniverse, long timeInterval, boolean removeRedundant);

	
	PricePool renamePricePool(String code, String newCode);

	
}
