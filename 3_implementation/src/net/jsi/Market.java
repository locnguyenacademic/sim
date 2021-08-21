package net.jsi;

import java.io.Serializable;
import java.util.List;

public interface Market extends Serializable, Cloneable {

	
	double getBalance();
	
	
	double getFreeMargin(long timeInterval);


	double getMargin(long timeInterval);
	
	
	double getProfit(long timeInterval);


	double getTakenValue(long timeInterval);

	
	double getROI(long timeInterval);


	double getROIByLeverage(long timeInterval);

	
	double estimateInvestAmount(long timeInterval);

		
	String name();
	
	
	long getTimeViewInterval();
	
	
	List<Stock> getStocks(long timeInterval);
	
	
	Market getSuperMarket();
	
	
	Universe getNearestUniverse();
	
	
	StockImpl c(Stock stock);
	
	
}
