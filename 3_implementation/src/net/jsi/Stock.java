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

	
//	double getMarginPrev(long timeInterval);

		
	double getValue(long timeInterval);

		
	double getProfit(long timeInterval);

		
	double getPriceOscill(long timeInterval);
	
	
	double calcOscillRatio(long timeInterval);

	
	double calcOscillRatioAbs(long timeInterval);

	
	double getPriceMinMaxDev(long timeInterval);

		
	boolean isCommitted();
	
	
	long getCommittedTimePoint();

		
	void setCommitted(boolean committed);

		
	void setCommitted(boolean committed, long timePoint);

	
	double getVolume(long timeInterval, boolean countCommitted);
	
	
	double getLeverage();

	
	boolean setLeverage(double leverage);

		
	String code();
	
	
	void setBasicInfo(Stock stock);
	
	
	void setExtraInfo(Stock stock);

	
	StockProperty getProperty();
	
	
	double getStopLoss();
	
	
	double getTakeProfit();


	double getDividend(long timeInterval);

		
	long getDividendTimePoint(long timeInterval);
	
	
	String getCategory();

		
	StockInfoStore getStore();

	
}
