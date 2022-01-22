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

/**
 * This class represents properties of stock.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class StockProperty implements Serializable, Cloneable {


	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Separating character for a pair of strings.
	 */
	private final static String PAIR_SEP = ":";
	

	/**
	 * JSI version string.
	 */
	public static final String VERSION = "2.0 build 2021.10.01";
			
	
	/**
	 * No-name text.
	 */
	public final static String NONAME = "noname";


	/**
	 * Working directory.
	 */
	public final static String WORKING_DIRECTORY = "working/jsi";

	
	/**
	 * Prefix of market name.
	 */
	public final static String MARKET_NAME_PREFIX = "Market ";

	
	/**
	 * The first special character.
	 */
	public final static String NOTCODE1 = "1";

	
	/**
	 * The second special character.
	 */
	public final static String NOTCODE2 = "-1";

	
	/**
	 * The third special character.
	 */
	public final static String NOTCODE3 = "0";

	
	/**
	 * The fourth special character.
	 */
	public final static String NOTCODE4 = "$";

	
	/**
	 * The fifth special character.
	 */
	public final static String NOTCODE5 = "#";

	
	/**
	 * The sixth special character.
	 */
	public final static String NOTCODE6 = "&";

	
	/**
	 * The seventh special character.
	 */
	public final static String NOTCODE7 = "@";

	
	/**
	 * The eighth special character.
	 */
	public final static String NOTCODE8 = "!";

	
	/**
	 * JSI file extension.
	 */
	public final static String JSI_EXT = "jsi";

	
	/**
	 * JSI file description.
	 */
	public final static String JSI_DESC = "JSI files";

	
	/**
	 * Field name for maximum number of prices for a stock.
	 */
	public final static String MAX_PRICE_COUNT_FIELD = "mpc";
	
	
	/**
	 * Swap field.
	 */
	public final static String SWAP_FIELD = "swp";
	
	
	/**
	 * Spread field.
	 */
	public final static String SPREAD_FIELD = "spd";
	
	
	/**
	 * Commission field.
	 */
	public final static String COMMISSION_FIELD = "com";
	
	
	/**
	 * Field name for time interval field for updating prices.
	 */
	public final static String TIME_UPDATE_PRICE_INTERVAL_FIELD = "tup";

	
	/**
	 * Price ratio field.
	 */
	public final static String PRICE_RATIO_FIELD = "prr";

	
	/**
	 * One-time fee field.
	 */
	public final static String ONETIME_FEE_FIELD = "otf";

	
	/**
	 * Everyday fee field.
	 */
	public final static String EVERYDAY_FEE_FIELD = "edf";

	
	/**
	 * Dividend field.
	 */
	public final static String DIVIDEND_FIELD = "dvd";

	
	/**
	 * Category field.
	 */
	public final static String CATEGORY_FIELD = "cat";

	
	/**
	 * Maximum number of prices for a stock.
	 */
	public static int MAX_PRICE_COUNT = 0;


	/**
	 * Default leverage.
	 */
	public static double LEVERAGE = 0.05;


	/**
	 * Default unit bias.
	 */
	public static double UNIT_BIAS = LEVERAGE*100;


	/**
	 * Time interval field for updating prices in milisecond.
	 */
	public static long TIME_UPDATE_PRICE_INTERVAL = 1;

	
	/**
	 * Time view interval in milisecond.
	 */
	public static long TIME_VIEW_INTERVAL = 1000*3600*24*30;


	/**
	 * Time valid interval in milisecond.
	 */
	public static long TIME_VALID_INTERVAL = 0;


	/**
	 * Time view period.
	 */
	public static double TIME_VIEW_PERIOD_RATIO = 4.0;
	
	
	/**
	 * Price ratio.
	 */
	public static double PRICE_RATIO = 1;
	
	
	/**
	 * Price factor.
	 */
	public static double PRICE_FACTOR = 1;

	
	/**
	 * Null dialog.
	 */
	public static boolean NULL_DIALOG = false;
	
	
	/**
	 * If this flag is set to be true, the main table will be updated when the other table is being processed.
	 * Therefore setting this flag to be true will consume more time.
	 */
	public static boolean RUNTIME_CASCADE = false;


	/**
	 * Undefined category.
	 */
	public static String CATEGORY_UNDEFINED = "undefined";
	
	
	/**
	 * Maximum number of prices for a stock.
	 */
	public int maxPriceCount = MAX_PRICE_COUNT;
	
	
	/**
	 * Swap value.
	 */
	public double swap = 0;
	
	
	/**
	 * Spread value.
	 */
	public double spread = 0;
	
	
	/**
	 * Commission value.
	 */
	public double commission = 0;
	
	
	/**
	 * Time interval field for updating prices in milisecond.
	 */
	public long timeUpdatePriceInterval = StockProperty.TIME_UPDATE_PRICE_INTERVAL;
	
	
	/**
	 * Price ratio.
	 */
	public double priceRatio = PRICE_RATIO;
	
	
	/**
	 * Additional properties.
	 */
	public Map<String, Object> moreProperties = Util.newMap(0);
	
	
	/**
	 * Setting with specified property.
	 * @param property specified property.
	 */
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
	
	
	/**
	 * Synchronizing with other property.
	 * @param otherProperty other property.
	 */
	protected void sync(StockProperty otherProperty) {
		set(otherProperty);
	}
	
	
	/**
	 * Getting default properties keys.
	 * @return default properties keys.
	 */
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
	
	
	/**
	 * Getting additional real value associated with specified field.
	 * @param field specified field.
	 * @return additional real value associated with specified field.
	 */
	private double getMoreRealValue(String field) {
		if (!moreProperties.containsKey(field)) return 0;
		double value = 0;
		try {
			Object v = moreProperties.get(field);
			if (v instanceof Number)
				value = ((Number)v).doubleValue();
			else
				value = Double.parseDouble(moreProperties.get(field).toString());
		} catch (Exception e) {}
		
		return value;
	}
	
	
	/**
	 * Getting additional long value associated with specified field.
	 * @param field specified field.
	 * @return additional long value associated with specified field.
	 */
	@SuppressWarnings("unused")
	private long getMoreLongValue(String field) {
		if (!moreProperties.containsKey(field)) return 0;
		long value = 0;
		try {
			Object v = moreProperties.get(field);
			if (v instanceof Number)
				value = ((Number)v).longValue();
			else
				value = Long.parseLong(moreProperties.get(field).toString());
		} catch (Exception e) {}
		
		return value;
	}

	
	/**
	 * Getting additional integer value associated with specified field.
	 * @param field specified field.
	 * @return additional integer value associated with specified field.
	 */
	@SuppressWarnings("unused")
	private long getMoreIntValue(String field) {
		if (!moreProperties.containsKey(field)) return 0;
		int value = 0;
		try {
			Object v = moreProperties.get(field);
			if (v instanceof Number)
				value = ((Number)v).intValue();
			else
				value = Integer.parseInt(moreProperties.get(field).toString());
		} catch (Exception e) {}
		
		return value;
	}

	
	/**
	 * Getting extra one-time fee.
	 * @return extra one-time fee.
	 */
	private double getExtraOneTimeFee() {
		return getMoreRealValue(ONETIME_FEE_FIELD);
	}
	
	
	/**
	 * Getting extra day fee in specified days.
	 * @param days specified days.
	 * @return extra day fee in specified days.
	 */
	private double getExtraDayFee(int days) {
		return getMoreRealValue(EVERYDAY_FEE_FIELD) * days;
	}
	
	
	/**
	 * Getting extra fee in specified days.
	 * @param days specified days.
	 * @return extra fee in specified days.
	 */
	public double getExtraFee(int days) {
		return getExtraOneTimeFee() + getExtraDayFee(days);
	}
	
	
	/**
	 * Setting extra one-time fee.
	 * @param otf extra one-time fee.
	 */
	public void setExtraOneTimeFee(double otf) {
		moreProperties.put(ONETIME_FEE_FIELD, otf);
	}
	
	
	/**
	 * Setting extra every day fee.
	 * @param edf extra every day fee.
	 */
	public void setExtraEveryDayFee(double edf) {
		moreProperties.put(EVERYDAY_FEE_FIELD, edf);
	}

	
	/**
	 * Getting dividend.
	 * @return dividend.
	 */
	public double getDividend() {
		if (!moreProperties.containsKey(DIVIDEND_FIELD)) return 0;
		String text = moreProperties.get(DIVIDEND_FIELD).toString();
		String[] pair = text.split(PAIR_SEP);
		if (pair == null || pair.length < 2) return 0;
		try {
			return Double.parseDouble(pair[0]);
		}
		catch (Exception e) {}
		return 0;
	}
	
	
	/**
	 * Getting dividend time.
	 * @return dividend time.
	 */
	public long getDividendTime() {
		if (!moreProperties.containsKey(DIVIDEND_FIELD)) return 0;
		String text = moreProperties.get(DIVIDEND_FIELD).toString();
		String[] pair = text.split(PAIR_SEP);
		if (pair == null || pair.length < 2) return 0;
		try {
			return Long.parseLong(pair[1]);
		}
		catch (Exception e) {}
		return 0;
	}
	
	
	/**
	 * Setting dividend.
	 * @param value dividend value.
	 * @param timePoint time point.
	 */
	public void setDividend(double value, long timePoint) {
		if (value > 0 && timePoint > 0)
			moreProperties.put(DIVIDEND_FIELD, Util.format(value) + ":" + timePoint);
		else
			moreProperties.remove(DIVIDEND_FIELD);
	}
	
	
	/**
	 * Getting category.
	 * @return category.
	 */
	public String getCategory() {
		if (moreProperties.containsKey(CATEGORY_FIELD))
			return (String)moreProperties.get(CATEGORY_FIELD);
		else
			return CATEGORY_UNDEFINED;
	}
	
	
	/**
	 * Setting category.
	 * @param category specified category.
	 */
	public void setCategory(String category) {
		if (category == null || category.isEmpty())
			moreProperties.remove(CATEGORY_FIELD);
		else
			moreProperties.put(CATEGORY_FIELD, category);
	}
	
	
	/**
	 * Converting this property into text.
	 * @return text converted from this property.
	 */
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
	
	
	/**
	 * Parsing text to this property.
	 * @param text specified text. The text includes lines.
	 */
	public void parseText(String text) {
		Map<String, Object> properties = fromText(text);
		parseText(properties);
	}

	
	/**
	 * Parsing the map of text to this property.
	 * @param texts specified map of texts.
	 */
	public void parseText(Collection<String> texts) {
		Map<String, Object> properties = fromText(texts);
		parseText(properties);
	}

	
	/**
	 * Parsing map of objects to this property.
	 * @param properties map of objects.
	 */
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
	
	
	/**
	 * Converting additional properties to text.
	 * @return text converted from addition properties.
	 */
	public String getMorePropertiesText() {
		Set<String> defaultKeys = getDefaultPropertiesKeys();
		Map<String, Object> properties = Util.newMap(0);
		Set<String> keys = moreProperties.keySet();
		for (String key : keys) {
			if (!defaultKeys.contains(key)) properties.put(key, moreProperties.get(key));
		}
		
		return toText(properties);
	}
	
	
	/**
	 * Setting additional properties from specified text.
	 * @param propertiesText specified text.
	 */
	public void setMorePropertiesText(String propertiesText) {
		Set<String> defaultKeys = getDefaultPropertiesKeys();
		Map<String, Object> properties = fromText(propertiesText);
		this.moreProperties.clear();
		Set<String> keys = properties.keySet();
		for (String key : keys) {
			if (!defaultKeys.contains(key)) this.moreProperties.put(key, properties.get(key));
		}
	}
	
	
	/**
	 * Converting map of object into text.
	 * @param properties map of objects as properties.
	 * @return text converted from map of objects.
	 */
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
	
	
	/**
	 * Parsing text to map of objects.
	 * @param text specified text. The text includes lines.
	 * @return map of parsed objects.
	 */
	protected static Map<String, Object> fromText(String text) {
		Map<String, Object> properties = Util.newMap(0);
		if (text == null) return properties;

		String[] eqs = text.split("[[\n][,]]");
		return fromText(Arrays.asList(eqs));
	}
	
	
	/**
	 * Parsing map of texts to map of objects.
	 * @param texts specified map of texts.
	 * @return map of parsed object.
	 */
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
			catch (Throwable e) {
				Util.trace(e);
			}
		}
		
		return properties;
	}

	
	/**
	 * Parsing text into integer.
	 * @param s specified text.
	 * @return integer parsed from text.
	 */
	private static int parseInt(String s) {
		try {
			return Integer.parseInt(s);
		}
		catch (Exception e) {
			return (int) Double.parseDouble(s);
		}
	}
	
	
	/**
	 * Parsing text into long number.
	 * @param s specified text.
	 * @return long number parsed from text.
	 */
	private static long parseLong(String s) {
		try {
			return Long.parseLong(s);
		}
		catch (Exception e) {
			return (long) Double.parseDouble(s);
		}
	}


	/**
	 * Create key from stock code and buy/sell indicator.
	 * @param code stock code.
	 * @param buy buy/sell indicator.
	 * @return key created from stock code and buy/sell indicator.
	 */
	public static String keyOf(String code, boolean buy) {
		return code != null ? code + PAIR_SEP + buy : null;
	}
	
	
	/**
	 * Getting stock code from key.
	 * @param key specified key.
	 * @return stock code from key.
	 */
	@SuppressWarnings("unused")
	private static String codeOf(String key) {
		if (key == null) return null;
		String[] sa = key.split(PAIR_SEP);
		if (sa == null || sa.length < 2 || sa[0] == null)
			return null;
		else
			return sa[0];
	}
	
	
	/**
	 * Getting buy/sell indicator from key.
	 * @param key specified key.
	 * @return buy/sell indicator from key.
	 */
	@SuppressWarnings("unused")
	private static boolean buyOf(String key) {
		if (key == null) return true;
		String[] sa = key.split(PAIR_SEP);
		if (sa == null || sa.length < 2 || sa[1] == null)
			return true;
		else {
			try {
				return Boolean.parseBoolean(sa[1]);
			} catch (Throwable e) {Util.trace(e);}
			return true;
		}
	}
	
	
}
