/**
 * JSI: JAGGED STRATEGY INVESTMENT 
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: jsi.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.jsi;

import java.util.Date;

public class TakenPrice implements Price {

	
	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;


	private Price price = null;
	
	
	private double realPrice = Double.NaN;
	
	
	private double extraPrice = 0;
	
	
	public TakenPrice(Price price) {
		this(price, Double.NaN, 0);
	}
	
	
	public TakenPrice(Price price, double realPrice) {
		this(price, realPrice, 0);
	}
	
	
	public TakenPrice(Price price, double realPrice, double extra) {
		this.price = price;
		if (price == null)
			this.realPrice = realPrice;
		else if (price.get() != realPrice && !Double.isNaN(realPrice))
			this.realPrice = realPrice;
		this.extraPrice = extra;
	}

	
	@Override
	public double get() {
		return (Double.isNaN(realPrice) && price != null ? price.get() : realPrice) + extraPrice; 
	}

	
	@Override
	public void set(double price) {
		if (!Double.isNaN(realPrice)) realPrice = Double.NaN;
		this.price.set(price);
	}

	
	protected double queryReal() {
		return this.realPrice;
	}
	
	
	protected void setReal(double realPrice) {
		this.realPrice = realPrice;
	}
	
	
	protected void unsetReal() {
		this.realPrice = Double.NaN;
	}
	
	
	public double getExtra() {
		return extraPrice;
	}
	
	
	public void setExtra(double extra) {
		this.extraPrice = extra;
	}
	
	
	@Override
	public double getLow() {
		return price.getLow();
	}

	
	@Override
	public void setLow(double lowPrice) {
		price.setLow(lowPrice);
	}

	
	@Override
	public double getHigh() {
		return price.getHigh();
	}

	
	@Override
	public void setHigh(double highPrice) {
		price.setHigh(highPrice);
	}

	
	@Override
	public double getAlt() {
		return price.getAlt();
	}

	
	@Override
	public void setAlt(double altPrice) {
		price.setAlt(altPrice);
	}

	
	@Override
	public double getAverage() {
		return price.getAverage();
	}


	@Override
	public long getTime() {
		return price.getTime();
	}

	
	@Override
	public void setTime(long time) {
		price.setTime(time);
	}

	
	@Override
	public Date getDate() {
		return price.getDate();
	}

	
	@Override
	public boolean isValid() {
		return price.isValid() ? true : !Double.isNaN(realPrice);
	}

	
	public Price getPrice() {
		return price;
	}


	@Override
	public boolean copy(Price price) {
		if (price == null) return false;
		
		if (price instanceof TakenPrice) {
			this.price = ((TakenPrice)price).price;
			this.realPrice = ((TakenPrice)price).realPrice;
			this.extraPrice = ((TakenPrice)price).extraPrice;
		}
		else {
			this.set(price.get());
			this.setLow(price.getLow());
			this.setHigh(price.getHigh());
			this.setAlt(price.getAlt());
			this.realPrice = Double.NaN;
			this.setTime(price.getTime());
		}
			
		return true;
	}


	protected void copy(Price price, double realPrice) {
		copy(price);
		this.realPrice = realPrice;
	}
	
	
	@Override
	public boolean checkRefEquals(Price price) {
		if (price == null)
			return false;
		else if (price instanceof TakenPrice)
			return this == price;
		else if (price instanceof PriceImpl)
			return price.checkRefEquals(this);
		else
			return this == price;
	}
	
	
	@Override
	public void applyFactor(double factor) {
		price.applyFactor(factor);
		extraPrice = extraPrice*factor;
		if (!Double.isNaN(realPrice)) realPrice = realPrice*factor; 
	}


	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {Util.trace(e);}
		return null;
	}


}
