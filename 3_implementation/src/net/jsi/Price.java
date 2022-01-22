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

/**
 * This interface represents price of stock.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface Price extends Serializable, Cloneable {


	/**
	 * Getting price.
	 * @return default price.
	 */
	double get();
	
	
	/**
	 * Setting price.
	 * @param price default price.
	 */
	void set(double price);
	
	
	/**
	 * Getting bottom price.
	 * @return bottom price.
	 */
	double getLow();
	
	
	/**
	 * Setting bottom price.
	 * @param lowPrice bottom price.
	 */
	void setLow(double lowPrice);
	
	
	/**
	 * Getting top price.
	 * @return top price.
	 */
	double getHigh();
	
	
	/**
	 * Setting top price.
	 * @param highPrice top price.
	 */
	void setHigh(double highPrice);
	

	/**
	 * Getting alternative price.
	 * @return alternative price (open price)
	 */
	double getAlt();
	
	
	/**
	 * Setting alternative price.
	 * @param altPrice alternative price. It is often open price.
	 */
	void setAlt(double altPrice);

	
	/**
	 * Getting the average of default price and alternative price.
	 * @return the average of default price and alternative price.
	 */
	double getAverage();
	
	
	/**
	 * Getting price time.
	 * @return price time.
	 */
	long getTime();
	
	
	/**
	 * Setting price time.
	 * @param time price time.
	 */
	void setTime(long time);
	
	
	/**
	 * Getting price date.
	 * @return price date.
	 */
	Date getDate();


	/**
	 * Checking whether price is valid.
	 * @return whether price is valid.
	 */
	boolean isValid();
	
	
	/**
	 * Coping from the other price.
	 * @param price other price.
	 * @return true if coping from other price is successful.
	 */
	boolean copy(Price price);
	
	
	/**
	 * Checking if two prices are equal.
	 * @param price the other price.
	 * @return true if two prices are equal.
	 */
	boolean checkRefEquals(Price price);
	
	
	/**
	 * Applying (increasing) this price with specified factor.
	 * @param factor specified factor.
	 */
	void applyFactor(double factor);
	
	
	/**
	 * Cloning this price.
	 * @return cloned price.
	 */
	Object clone();
	
	
}
