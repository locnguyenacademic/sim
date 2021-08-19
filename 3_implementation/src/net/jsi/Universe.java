package net.jsi;

import java.io.Serializable;

public interface Universe extends Market, Serializable, Cloneable {

	
	int size();
	
	
	Market get(int index);

	
	boolean add(Market market);
	
	
	Market remove(int index);
	
	
	Market newMarket(String name, double leverage, double unitBias);


	MarketImpl convert(Market market);
	
	
}
