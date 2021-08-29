/**
 * JSI: JAGGED STRATEGY INVESTMENT 
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: jsi.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.jsi;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public class StockProperty implements Serializable, Cloneable {


	private static final long serialVersionUID = 1L;
	
	
	public final static String NONAME = "noname";


	/**
	 * Working directory.
	 */
	public final static String WORKING_DIRECTORY = "working/jsi";

	
	public final static String MARKET_NAME_PREFIX = "Market ";

	
	public final static String NOTCODE1 = "1";

	
	public final static String NOTCODE2 = "-1";

	
	public final static String NOTCODE3 = "0";

	
	public final static String JSI_EXT = "jsi";

	
	public final static String JSI_DESC = "JSI files";

	
	public static int MAX_PRICE_COUNT = 1000;


	public static double LEVERAGE = 0.05;


	public static double UNIT_BIAS = LEVERAGE*100;


	public static long TIME_UPDATE_PRICE_INTERVAL = 1;


	public static long TIME_VIEW_INTERVAL = 1000*3600*24*10;


	public static long TIME_VALID_INTERVAL = TIME_VIEW_INTERVAL*10;
	
	
	public static double PRICE_RATIO = 1;


	public int maxPriceCount = MAX_PRICE_COUNT;
	
	
	public double swap = 0;
	
	
	public double spread = 0;
	
	
	public double commission = 0;
	
	
	public long timeUpdatePriceInterval = StockProperty.TIME_UPDATE_PRICE_INTERVAL;
	
	
	public double priceRatio = PRICE_RATIO;
	
	
	public Map<String, String> moreProperty = Util.newMap(0);
	
	
	public void set(StockProperty property) {
		if (property == null) return;
		
		this.maxPriceCount = property.maxPriceCount;
		this.swap = property.swap;
		this.spread = property.spread;
		this.commission = property.commission;
		this.timeUpdatePriceInterval = property.timeUpdatePriceInterval;
		
		this.moreProperty.clear();
		if (property != null) this.moreProperty.putAll(property.moreProperty);
	}
	
	
	public String getMorePropertyText() {
		StringBuffer buffer = new StringBuffer();
		Set<String> keys = moreProperty.keySet();
		boolean first = false;
		for (String key : keys) {
			if (first) buffer.append(", ");
			buffer.append(key + "=" + moreProperty.get(key));
			first = true;
		}
		
		return buffer.toString();
	}
	
	
	public void setMorePropertyText(String propertyText) {
		this.moreProperty.clear();
		if (propertyText == null) return;
		
		String[] eqs = propertyText.split("[[\n][,]]");
		if (eqs == null) return;
		for (String eq : eqs) {
			String[] pair = eq.split("=");
			try {
				if (pair == null || pair.length < 2) continue;
				String key = pair[0].trim();
				String value = pair[1].trim();
				if (!key.isEmpty() && !value.isEmpty()) this.moreProperty.put(key, value);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		
	}
	
	
}
