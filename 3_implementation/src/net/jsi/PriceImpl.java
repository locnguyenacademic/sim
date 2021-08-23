package net.jsi;

import java.util.Date;

public class PriceImpl implements Price {

	
	private static final long serialVersionUID = 1L;


	protected double price = 0;
	
	
	protected double lowPrice = 0;
	
	
	protected double highPrice = 0;
	
	
	protected long time = System.currentTimeMillis();
	
	
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
	public boolean isValid() {
		return price >= lowPrice && price <= highPrice && time >= 0;
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


	
	
	
}
