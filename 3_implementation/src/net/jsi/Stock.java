package net.jsi;

import java.io.Serializable;
import java.util.List;

public interface Stock extends Estimator, Serializable, Cloneable {
	
	
	boolean setPrice(Price price);
	
	
	Price getPrice(long timePoint);
	
	
	List<Price> getPrices(long timeInterval);
	
	
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

	
	void setLeverage(double leverage);

		
	String code();
	
	
	void setBasicInfo(Stock stock);
	
	
	StockProperty getProperty();
	
	
	void setProperty(StockProperty property);
	
	
}
