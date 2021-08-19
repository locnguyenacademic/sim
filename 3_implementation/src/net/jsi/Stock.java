package net.jsi;

import java.io.Serializable;
import java.util.List;

public interface Stock extends Estimator, Serializable, Cloneable {
	
	
	boolean setPrice(Price price);
	
	
	Price getPrice(long timePoint);
	
	
	List<Price> getPrices(long timeInterval);
	
	
	boolean setUnitBias(double unitBias);
	
	
	double getTakenValue(long timeInterval);
	
	
	double getMargin(long timeInterval);

	
	double getValue(long timeInterval);

		
	double getProfit(long timeInterval);

		
	boolean isCommitted();
	
	
	void setCommitted(boolean committed);

		
	String code();
	
	
	void copyProperties(Stock stock);
	
	
}
