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

	
	private static final long serialVersionUID = 1L;


	private Price price = null;
	
	
	private double extraPrice = 0;
	
	
	public TakenPrice(Price price) {
		this.price = price;
	}
	
	
	public TakenPrice(Price price, double extra) {
		this.price = price;
		this.extraPrice = extra;
	}

	
	@Override
	public double get() {
		return price.get() + extraPrice;
	}

	
	@Override
	public void set(double price) {
		this.price.set(price);
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
		return price.isValid();
	}

	
	public Price getPrice() {
		return price;
	}


//	@Override
//	public Serializable getTag() {
//		return price.getTag();
//	}
//
//
//	@Override
//	public double getPriceRatio() {
//		return price.getPriceRatio();
//	}
//
//
//	@Override
//	public void setPriceRatio(double priceRatio) {
//		price.setPriceRatio(priceRatio);
//	}


	@Override
	public boolean copy(Price price) {
		if (price == null) return false;
		
		if (price instanceof TakenPrice) {
			this.price = ((TakenPrice)price).price;
			this.extraPrice = ((TakenPrice)price).extraPrice;
		}
		else {
			this.set(price.get());
			this.setLow(price.getLow());
			this.setHigh(price.getHigh());
			this.setAlt(price.getAlt());
			this.setTime(price.getTime());
		}
			
		return true;
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
	}


	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {e.printStackTrace();}
		return null;
	}


}
