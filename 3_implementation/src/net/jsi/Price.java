/**
 * JSI: JAGGED STRATEGY INVESTMENT 
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: jsi.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
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
	
	
	double getAlt();
	
	
	void setAlt(double altPrice);

	
	double getAverage();
	
	
	long getTime();
	
	
	void setTime(long time);
	
	
	Date getDate();


	boolean isValid();
	
	
//	double getPriceRatio();
//	
//	
//	void setPriceRatio(double priceRatio);

	
//	Serializable getTag();
	
	
	boolean copy(Price price);
	
	
	boolean checkRefEquals(Price price);
	
	
	void applyFactor(double factor);
	
	
	Object clone();
	
	
}
