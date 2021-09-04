/**
 * JSI: JAGGED STRATEGY INVESTMENT 
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: jsi.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.jsi;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

public class StockProperty implements Serializable, Cloneable {


	private static final long serialVersionUID = 1L;
	
	
	public static Universe g = null;
	
	
	public final static String NONAME = "noname";


	/**
	 * Working directory.
	 */
	public final static String WORKING_DIRECTORY = "working/jsi";

	
	public final static String MARKET_NAME_PREFIX = "Market ";

	
	public final static String NOTCODE1 = "1";

	
	public final static String NOTCODE2 = "-1";

	
	public final static String NOTCODE3 = "0";

	
	public final static String NOTCODE4 = "$";

	
	public final static String NOTCODE5 = "#";

	
	public final static String NOTCODE6 = "&";

	
	public final static String NOTCODE7 = "@";

	
	public final static String JSI_EXT = "jsi";

	
	public final static String JSI_DESC = "JSI files";

	
	public final static String MAX_PRICE_COUNT_FIELD = "mpc";
	
	
	public final static String SWAP_FIELD = "swp";
	
	
	public final static String SPREAD_FIELD = "spd";
	
	
	public final static String COMMISSION_FIELD = "com";
	
	
	public final static String TIME_UPDATE_PRICE_INTERVAL_FIELD = "tup";

	
	public final static String PRICE_RATIO_FIELD = "prr";

	
	public static int MAX_PRICE_COUNT = 1000;


	public static double LEVERAGE = 0.05;


	public static double UNIT_BIAS = LEVERAGE*100;


	public static long TIME_UPDATE_PRICE_INTERVAL = 1;


	public static long TIME_VIEW_INTERVAL = 1000*3600*24*10;


	public static long TIME_VALID_INTERVAL = TIME_VIEW_INTERVAL*10;


	public static double TIME_VIEW_PERIOD_RATIO = 4.0;
	
	
	public static double PRICE_RATIO = 1;
	
	
	public static boolean NULL_DIALOG = false;
	
	
	/**
	 * If this flag is set to be true, the main table will be updated when the other table is being processed.
	 * Therefore setting this flag to be true will consume more time.
	 */
	public static boolean RUNTIME_CASCADE = false;


	public static boolean LOOKUP_WHEN_READ_PRICES = true;
	
	
	public int maxPriceCount = MAX_PRICE_COUNT;
	
	
	public double swap = 0;
	
	
	public double spread = 0;
	
	
	public double commission = 0;
	
	
	public long timeUpdatePriceInterval = StockProperty.TIME_UPDATE_PRICE_INTERVAL;
	
	
	public double priceRatio = PRICE_RATIO;
	
	
	public Map<String, Object> moreProperties = Util.newMap(0);
	
	
	public void set(StockProperty property) {
		if (property == null) return;
		
		this.maxPriceCount = property.maxPriceCount;
		this.swap = property.swap;
		this.spread = property.spread;
		this.commission = property.commission;
		this.timeUpdatePriceInterval = property.timeUpdatePriceInterval;
		
		this.moreProperties.clear();
		if (property != null) this.moreProperties.putAll(property.moreProperties);
	}
	
	
	private Set<String> getDefaultPropertiesKeys() {
		Set<String> keys = Util.newSet(0);
		keys.add(MAX_PRICE_COUNT_FIELD);
		keys.add(SWAP_FIELD);
		keys.add(SPREAD_FIELD);
		keys.add(COMMISSION_FIELD);
		keys.add(TIME_UPDATE_PRICE_INTERVAL_FIELD);
		keys.add(PRICE_RATIO_FIELD);
		
		return keys;
	}
	
	
	public String toText() {
		Map<String, Object> properties = Util.newMap(0);
		properties.putAll(this.moreProperties);
		
		properties.put(MAX_PRICE_COUNT_FIELD, maxPriceCount);
		properties.put(SWAP_FIELD, swap);
		properties.put(SPREAD_FIELD, spread);
		properties.put(COMMISSION_FIELD, commission);
		properties.put(PRICE_RATIO_FIELD, priceRatio);
		properties.put(TIME_UPDATE_PRICE_INTERVAL_FIELD, timeUpdatePriceInterval);
		
		return toText(properties);
	}
	
	
	public void parseText(String text) {
		Map<String, Object> properties = fromText(text);
		parseText(properties);
	}

	
	public void parseText(Collection<String> texts) {
		Map<String, Object> properties = fromText(texts);
		parseText(properties);
	}

	
	private void parseText(Map<String, Object> properties) {
		Set<String> keys = properties.keySet();
		moreProperties.clear();
		for (String key : keys) {
			if (key == null || key.isEmpty()) continue;
			Object value = properties.get(key);
			if (value == null) continue;

			if (key.equals(MAX_PRICE_COUNT_FIELD) ||
				key.equals(SWAP_FIELD) ||
				key.equals(SPREAD_FIELD) ||
				key.equals(COMMISSION_FIELD) ||
				key.equals(PRICE_RATIO_FIELD) ||
				key.equals(TIME_UPDATE_PRICE_INTERVAL_FIELD)) {
				
				if (!(value instanceof Number))
					continue;
				else if (key.equals(MAX_PRICE_COUNT_FIELD))
					maxPriceCount = ((Number)value).intValue();
				else if (key.equals(SWAP_FIELD))
					swap = ((Number)value).doubleValue();
				else if (key.equals(SPREAD_FIELD))
					spread = ((Number)value).doubleValue();
				else if(key.equals(COMMISSION_FIELD))
					commission = ((Number)value).doubleValue();
				else if(key.equals(PRICE_RATIO_FIELD))
					priceRatio = ((Number)value).doubleValue();
				else if (key.equals(TIME_UPDATE_PRICE_INTERVAL_FIELD))
					timeUpdatePriceInterval = ((Number)value).longValue();
			}
			else {
				moreProperties.put(key, value);
			}
		} // End for

	}
	
	
	public String getMorePropertiesText() {
		Set<String> defaultKeys = getDefaultPropertiesKeys();
		Map<String, Object> properties = Util.newMap(0);
		Set<String> keys = moreProperties.keySet();
		for (String key : keys) {
			if (!defaultKeys.contains(key)) properties.put(key, moreProperties.get(key));
		}
		
		return toText(properties);
	}
	
	
	public void setMorePropertiesText(String propertiesText) {
		Set<String> defaultKeys = getDefaultPropertiesKeys();
		Map<String, Object> properties = fromText(propertiesText);
		this.moreProperties.clear();
		Set<String> keys = properties.keySet();
		for (String key : keys) {
			if (!defaultKeys.contains(key)) this.moreProperties.put(key, properties.get(key));
		}
	}
	
	
	protected static String toText(Map<String, Object> properties) {
		StringBuffer buffer = new StringBuffer();
		Set<String> keys = properties.keySet();
		boolean first = false;
		for (String key : keys) {
			if (key == null || key.isEmpty()) continue;
			Object value = properties.get(key);
			if (value == null) continue;
			
			if (first) buffer.append(", ");
			
			String vText = value.toString();
			try {
				if (value instanceof Double)
					vText = Util.format((Double)value);
				else if (value instanceof Long)
					vText = value.toString();
				else if (value instanceof Integer)
					vText = value.toString();
				else if (value instanceof Number)
					vText = Util.format(((Number)value).doubleValue());
				else if (value instanceof Date)
					vText = "" + ((Date)value).getTime();
			}
			catch (Exception e) {}
			
			if (vText != null && !vText.isEmpty()) buffer.append(key + "=" + vText);
			first = true;
		}
		
		return buffer.toString();
	}
	
	
	protected static Map<String, Object> fromText(String text) {
		Map<String, Object> properties = Util.newMap(0);
		if (text == null) return properties;

		String[] eqs = text.split("[[\n][,]]");
		return fromText(Arrays.asList(eqs));
	}
	
	
	protected static Map<String, Object> fromText(Collection<String> texts) {
		Map<String, Object> properties = Util.newMap(0);
		if (texts == null) return properties;
		
		for (String text : texts) {
			String[] pair = text.split("=");
			try {
				if (pair == null || pair.length < 2) continue;
				String key = pair[0].trim();
				String value = pair[1].trim();
				if (key.isEmpty() || value.isEmpty()) continue;
				
				Object v = null;
				if (key.equals(MAX_PRICE_COUNT_FIELD))
					v = parseInt(value);
				else if (key.equals(SWAP_FIELD) || key.equals(SPREAD_FIELD) || key.equals(COMMISSION_FIELD) || key.equals(PRICE_RATIO_FIELD))
					v = Double.parseDouble(value);
				else if (key.equals(TIME_UPDATE_PRICE_INTERVAL_FIELD))
					v = parseLong(value);
				else {
					try {
						long v1 = parseLong(value);
						int v2 = parseInt(value);
						v = v1 == v2 ? v2 : v1;
					}
					catch (Exception e) {
						try {
							v = parseLong(value);
						}
						catch (Exception e2) {v = null;}
					}
					
					if (v == null) {
						try {
							v = Double.parseDouble(value);
						}
						catch (Exception e) {v = null;}
					}
					
					if (v == null) v = value;
				}
					
				if (v != null) properties.put(key, v);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return properties;
	}

	
	private static int parseInt(String s) {
		try {
			return Integer.parseInt(s);
		}
		catch (Exception e) {
			return (int) Double.parseDouble(s);
		}
	}
	
	
	private static long parseLong(String s) {
		try {
			return Long.parseLong(s);
		}
		catch (Exception e) {
			return (long) Double.parseDouble(s);
		}
	}


}
