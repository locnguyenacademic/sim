package net.jsi;

import java.io.Serializable;

public class StockProperty implements Serializable, Cloneable {


	private static final long serialVersionUID = 1L;
	
	
	public final static String NONAME = "noname";


	public static int MAX_PRICE_COUNT = 1000;


	public static double LEVERAGE = 0.05;


	public static double UNIT_BIAS = LEVERAGE*100;


	public static long TIME_UPDATE_PRICE_INTERVAL = 0;


	public static long TIME_VIEW_INTERVAL = 1000*3600*24*10;


	public int maxPriceCount = StockProperty.MAX_PRICE_COUNT;
	
	
	public double swap = 0;
	
	
	public double spread = 0;
	
	
	public double commission = 0;
	
	
	public long timeUpdatePriceInterval = StockProperty.TIME_UPDATE_PRICE_INTERVAL;
	
	
	public Serializable moreProperty = null;
	
	
	public void set(StockProperty property) {
		if (property == null) return;
		
		this.maxPriceCount = property.maxPriceCount;
		this.swap = property.swap;
		this.spread = property.spread;
		this.commission = property.commission;
		this.timeUpdatePriceInterval = property.timeUpdatePriceInterval;
		this.moreProperty = property.moreProperty;
	}
	
	
}
