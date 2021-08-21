package net.jsi;

import java.io.Serializable;
import java.util.Date;

public interface Price extends Serializable, Cloneable {


	double get();
	
	
	void set(double price);
	
	
	double getLow();
	
	
	void setLow(double lowPrice);
	
	
	double getHigh();
	
	
	void setHigh(double highPrice);
	
	
	long getTime();
	
	
	void setTime(long time);
	
	
	Date getDate();


	boolean isValid();
	
	
}
