package net.jsi;

import java.util.Date;

public class TakenPrice implements Price {

	
	
	private static final long serialVersionUID = 1L;


	protected Price price = null;
	
	
	protected double extra = 0;
	
	
	public TakenPrice(Price price) {
		this.price = price;
	}
	
	
	public TakenPrice(Price price, double extra) {
		this.price = price;
		this.extra = extra;
	}

	
	@Override
	public double get() {
		return price.get() + extra;
	}

	
	@Override
	public void set(double price) {
		this.price.set(price);
	}

	
	public double getExtra() {
		return extra;
	}
	
	
	public void setExtra(double extra) {
		this.extra = extra;
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

	
}
