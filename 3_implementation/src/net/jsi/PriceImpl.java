/**
 * JSI: JAGGED STRATEGY INVESTMENT 
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: jsi.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.jsi;

import java.util.Date;

public class PriceImpl implements Price {

	
	private static final long serialVersionUID = 1L;


	private double price = 0;
	
	
	private double lowPrice = 0;
	
	
	private double highPrice = 0;
	
	
	private double altPrice = 0;

	
	private long timePoint = System.currentTimeMillis();
	
	
	public PriceImpl() {
		
	}
	
	
	public PriceImpl(double price, double lowPrice, double highPrice, long timePoint) {
		this.price = price;
		this.lowPrice = lowPrice;
		this.highPrice = highPrice;
		this.timePoint = timePoint;
	}
	
	
	@Override
	public double get() {
		return price;
	}
	
	
	@Override
	public void set(double price) {
		this.price = price;
	}
	
	
	@Override
	public double getLow() {
		return lowPrice;
	}
	
	
	@Override
	public void setLow(double lowPrice) {
		this.lowPrice = lowPrice;
	}
	
	
	@Override
	public double getHigh() {
		return highPrice;
	}
	
	
	@Override
	public void setHigh(double highPrice) {
		this.highPrice = highPrice;
	}
	
	
	@Override
	public double getAlt() {
		return altPrice;
	}
	
	
	@Override
	public void setAlt(double altPrice) {
		this.altPrice = altPrice;
	}
	
	
	@Override
	public double getAverage() {
		double price = get();
		double alt = getAlt();
		return alt > 0 ? (price + alt) / 2.0 : price;
	}


	@Override
	public long getTime() {
		return timePoint;
	}
	
	
	@Override
	public void setTime(long timePoint) {
		this.timePoint = timePoint;
	}
	
	
	@Override
	public Date getDate() {
		return new Date(timePoint);
	}


	@Override
	public boolean copy(Price price) {
		if (price == null) return false;
		this.set(price.get());
		this.setLow(price.getLow());
		this.setHigh(price.getHigh());
		this.setAlt(price.getAlt());
		this.setTime(price.getTime());
		return true;
	}


	@Override
	public boolean isValid() {
		return price >= lowPrice && price <= highPrice && timePoint >= 0;
	}


	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {Util.trace(e);}
		return null;
	}


	public boolean checkRefEquals(Price price) {
		if (price == null)
			return false;
		else if (price instanceof TakenPrice)
			return this == ((TakenPrice)price).getPrice();
		else
			return this == price;
	}


	@Override
	public void applyFactor(double factor) {
		price = price*factor;
		lowPrice = lowPrice*factor;
		highPrice = highPrice*factor;
		altPrice = altPrice*factor;
	}


	
	
	
}
