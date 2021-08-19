package net.jsi;

import java.io.Serializable;

public interface Market extends Serializable, Cloneable {

	
	double getBalance();
	
	
	double getMargin();
	
	
	double getFreeMargin();


	double getProfit();


	double getROI();


	String name();
	
	
}
