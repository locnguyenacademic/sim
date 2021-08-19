package net.jsi;

import java.io.Serializable;
import java.util.Date;

public class Price implements Serializable, Cloneable {

	
	private static final long serialVersionUID = 1L;


	protected double price = 0;
	
	
	protected double lowPrice = 0;
	
	
	protected double highPrice = 0;
	
	
	protected long time = System.currentTimeMillis();
	
	
	public Price() {
		
	}
	
	
	public Price(double price, double lowPrice, double highPrice, long time) {
		this.price = price;
		this.lowPrice = lowPrice;
		this.highPrice = highPrice;
		this.time = time;
	}
	
	
	public double get() {
		return price;
	}
	
	
	public double low() {
		return lowPrice;
	}
	
	
	public double high() {
		return highPrice;
	}
	
	
	public long time() {
		return time;
	}
	
	
	public Date date() {
		return new Date(time);
	}


	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
	
}
