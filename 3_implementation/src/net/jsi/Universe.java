package net.jsi;

import java.io.Serializable;
import java.util.List;

public interface Universe extends Market, Serializable, Cloneable {

	
	int size();
	

	List<String> names();
	
	
	Market get(int index);
	
	
	QueryEstimator query(int index);

	
	int lookup(String name);

	
	boolean add(Market market);
	
	
	Market remove(int index);
	
	
	Market set(int index, Market market);
	
	
	Market newMarket(String name, double leverage, double unitBias);


	List<String> getSupportStockCodes();

		
	MarketImpl c(Market market);
	
	
}
