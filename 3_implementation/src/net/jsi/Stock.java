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

public interface Stock extends Estimator, Serializable, Cloneable {
	
	
	boolean setPrice(Price price);
	
	
	boolean addPrice(Price price);

		
	Price getPriceByTimePoint(long timePoint);
	
	
	List<Price> getPrices(long timeInterval);
	
	
	List<Price> getInternalPrices();

	
	double getROIByLeverage(long timeInterval);

	
	boolean setUnitBias(double unitBias);
	
	
	double getTakenValue(long timeInterval);
	
	
	double getMargin(long timeInterval);

	
	double getValue(long timeInterval);

		
	double getProfit(long timeInterval);

		
	boolean isCommitted();
	
	
	void setCommitted(boolean committed);

		
	double getVolume(long timeInterval, boolean countCommitted);
	
	
	double getLeverage();

	
	boolean setLeverage(double leverage);

		
	String code();
	
	
	void setBasicInfo(Stock stock);
	
	
	StockProperty getProperty();
	
	
	double getStopLoss();
	
	
	double getTakeProfit();


	double getDividend(long timeInterval);

		
	long getDividendTimePoint(long timeInterval);

		
	StockInfoStore getStore();

	
}
