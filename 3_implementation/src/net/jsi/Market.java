/**
 * JSI: JAGGED STRATEGY INVESTMENT 
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: jsi.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.jsi;

import java.io.Serializable;
import java.util.List;

public interface Market extends Serializable, Cloneable {

	
	double getBalance(long timeInterval);
	
	
	double getFreeMargin(long timeInterval);


	double getMargin(long timeInterval);
	
	
	double getProfit(long timeInterval);


	double getTakenValue(long timeInterval);

	
	double getROI(long timeInterval);


	double getROIByLeverage(long timeInterval);

	
	double calcTotalBias(long timeInterval);

	
	double calcInvestAmount(long timeInterval);

		
	String getName();
	
	
	long getTimeViewInterval();
	
	
	List<Stock> getStocks(long timeInterval);
	
	
	Market getSuperMarket();
	
	
	Universe getNearestUniverse();
	
	
	List<String> getSupportStockCodes();

	
	List<String> getDefaultStockCodes();

	
	StockImpl c(Stock stock);
	
	
}
