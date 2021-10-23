/**
 * JSI: JAGGED STRATEGY INVESTMENT 
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: jsi.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.jsi;

import java.awt.Component;
import java.awt.Dialog;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.text.DateFormatter;
import javax.swing.text.NumberFormatter;

/**
 * This is utility class to provide static utility methods.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public final class Util {

	
	public static String DATESIMPLE_FORMAT = "yyyy-MM-dd";

	
	public static String DATE_FORMAT = DATESIMPLE_FORMAT + " HH-mm-ss";
			
	
	public final static int DECIMAL_PRECISION_SHORT = 3;

	
	public final static int DECIMAL_PRECISION_MEDIUM = DECIMAL_PRECISION_SHORT + 3;

	
	public final static int DECIMAL_PRECISION_LONG = DECIMAL_PRECISION_MEDIUM + 3;

	
	public static int DECIMAL_PRECISION = DECIMAL_PRECISION_MEDIUM;

	
	/**
	 * Default Java regular expression for splitting a sentence into many words (tokens), including space.
	 */
	public final static String DEFAULT_SEP         = "[[\\s][,]]";

	
	/**
	 * Default Java regular expression for splitting a sentence into many words (tokens), not including white space.
	 */
	public final static String NOSPACE_DEFAULT_SEP = "[[,]]";

	
	/**
	 * Creating a new list with initial capacity.
	 * @param <T> type of elements in list.
	 * @param initialCapacity initial capacity of this list.
	 * @return new list with initial capacity.
	 */
	public static <T> List<T> newList(int initialCapacity) {
		try {
		    return net.jsi.adapter.Util.newList(initialCapacity);
		}
		catch (Throwable e) {}
		
	    return new ArrayList<T>(initialCapacity);
	}

	
	/**
	 * Creating a new set with initial capacity.
	 * @param <T> type of elements in set.
	 * @param initialCapacity initial capacity of this list.
	 * @return new set.
	 */
	public static <T> Set<T> newSet(int initialCapacity) {
		try {
		    return net.jsi.adapter.Util.newSet(initialCapacity);
		}
		catch (Throwable e) {}

		return new HashSet<T>(initialCapacity);
	}

	
	/**
	 * Creating a new vector with initial capacity.
	 * @param <T> type of elements in vector.
	 * @param initialCapacity initial capacity of this vector.
	 * @return new vector.
	 */
	public static <T> Vector<T> newVector(int initialCapacity) {
		try {
		    return net.jsi.adapter.Util.newVector(initialCapacity);
		}
		catch (Throwable e) {}

		return new Vector<T>(initialCapacity);
	}


	/**
	 * Creating a new map.
	 * @param <K> type of key.
	 * @param <V> type of value.
	 * @param initialCapacity initial capacity of this list.
	 * @return new map.
	 */
	public static <K, V> Map<K, V> newMap(int initialCapacity) {
		try {
		    return net.jsi.adapter.Util.newMap(initialCapacity);
		}
		catch (Throwable e) {}

	    return new HashMap<K, V>(initialCapacity);
	}

	
	/**
	 * Sorting the specified collection.
	 * @param <T> type of elements.
	 * @param data specified collection.
	 * @return sorted list.
	 */
	public static <T extends Comparable<T>> List<T> sort(Collection<T> data) {
		List<T> codeList = Util.newList(data.size());
		codeList.addAll(data);
		Collections.sort(codeList);
		
		return codeList;
	}
	
	
	/**
	 * Getting the parent frame of the specified component.
	 * @param comp specified component.
	 * @return Frame of the specified component. The method return {@code null} if the specified component has no parent frame.
	 */
	public static Dialog getDialogForComponent(Component comp) {
		if (StockProperty.NULL_DIALOG)
			return null;
		else if (comp == null)
	        return null;
	    if (comp instanceof Dialog)
	        return (Dialog)comp;
	    else
	    	return getDialogForComponent(comp.getParent());
	}


	/**
	 * Formatting specified date.
	 * @param date specified date.
	 * @return formatted text.
	 */
	public static String format(Date date) {
		SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT);
		return df.format(date);
	}


	/**
	 * Formatting specified date as simple text.
	 * @param date specified date.
	 * @return simple formatted text.
	 */
	public static String formatSimple(Date date) {
		SimpleDateFormat df = new SimpleDateFormat(DATESIMPLE_FORMAT);
		return df.format(date);
	}

	
	/**
	 * Converting the specified number into a string.
	 * @param number specified number.
	 * @return text format of number of the specified number.
	 */
	public static String format(double number) {
		return "" + round(number, DECIMAL_PRECISION);
	}


	public static String format(double number, int decimal) {
		return "" + round(number, decimal);
		//return String.format("%." + decimal + "f", number);
	}

	
	/**
	 * Rounding the specified number with decimal precision specified by the number of decimal digits.
	 * 
	 * @param number specified number.
	 * @param n decimal precision which is the number of decimal digits.
	 * @return number rounded from the specified number.
	 */
	public static double round(double number, int n) {
		if (Double.isNaN(number))
			return Double.NaN;
		
		long d = (long) Math.pow(10, n);
		return (double) Math.round(number * d) / d;
	}

	
	public static AbstractFormatter getNumberFormatter() {
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMinimumFractionDigits(DECIMAL_PRECISION_SHORT);
		nf.setMaximumFractionDigits(DECIMAL_PRECISION_LONG);
		
		NumberFormatter formatter = new NumberFormatter(nf);
		formatter.setAllowsInvalid(false);
        return formatter;
	}
	
	
	public static AbstractFormatter getDateFormatter() {
		DateFormat df = new SimpleDateFormat(DATE_FORMAT);
		
		DateFormatter formatter = new DateFormatter(df);
		formatter.setAllowsInvalid(false);
        return formatter;
	}

	
	public static AbstractFormatter getDateSimpleFormatter() {
		DateFormat df = new SimpleDateFormat(DATESIMPLE_FORMAT);
		
		DateFormatter formatter = new DateFormatter(df);
		formatter.setAllowsInvalid(false);
        return formatter;
	}

	
	public static Date parseDate(String dateTimeText) throws ParseException {
		if (dateTimeText == null || dateTimeText.isEmpty()) return null;
		DateFormat df = new SimpleDateFormat(DATE_FORMAT);
		return df.parse(dateTimeText);
	}
	
	
	public static void trace(Throwable e) {
		e.printStackTrace();
	}
	
	

}
