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

public class PriceImpl implements Price {

	
	private static final long serialVersionUID = 1L;


	private double price = 0;
	
	
	private double lowPrice = 0;
	
	
	private double highPrice = 0;
	
	
	private double priceRatio = StockProperty.PRICE_RATIO;
	
	
	protected long time = System.currentTimeMillis();
	
	
	protected Serializable tag = null;
	
	
	public PriceImpl() {
		
	}
	
	
	public PriceImpl(double price, double lowPrice, double highPrice, long time) {
		this.price = price;
		this.lowPrice = lowPrice;
		this.highPrice = highPrice;
		this.time = time;
	}
	
	
	@Override
	public double get() {
		return price*priceRatio;
	}
	
	
	@Override
	public void set(double price) {
		this.price = price;
	}
	
	
	@Override
	public double getLow() {
		return lowPrice*priceRatio;
	}
	
	
	@Override
	public void setLow(double lowPrice) {
		this.lowPrice = lowPrice;
	}
	
	
	@Override
	public double getHigh() {
		return highPrice*priceRatio;
	}
	
	
	@Override
	public void setHigh(double highPrice) {
		this.highPrice = highPrice;
	}
	
	
	@Override
	public long getTime() {
		return time;
	}
	
	
	@Override
	public void setTime(long time) {
		this.time = time;
	}
	
	
	@Override
	public Date getDate() {
		return new Date(time);
	}


	@Override
	public boolean copy(Price price) {
		if (price == null) return false;
		this.set(price.get());
		this.setLow(price.getLow());
		this.setHigh(price.getHigh());
		this.setTime(price.getTime());
		return true;
	}


	@Override
	public boolean isValid() {
		return price >= lowPrice && price <= highPrice && time >= 0;
	}


	@Override
	public Serializable getTag() {
		return tag;
	}


	@Override
	public void setTag(Serializable tag) {
		this.tag = tag;
	}


	@Override
	public double getPriceRatio() {
		return priceRatio;
	}
	
	
	@Override
	public void setPriceRatio(double priceRatio) {
		this.priceRatio = priceRatio;
	}
	
	
	@Override
	public Object clone() {
		try {
			return super.clone();
		}
		catch (CloneNotSupportedException e) {
			return null;
		}
	}


	public boolean checkRefEquals(Price price) {
		if (price == null)
			return false;
		else if (price instanceof TakenPrice)
			return this == ((TakenPrice)price).getPrice();
		else
			return this == price;
	}


	
	
	
}
