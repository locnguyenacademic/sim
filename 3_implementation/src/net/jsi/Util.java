/**
 * JSI: JAGGED STRATEGY INVESTMENT 
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: jsi.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.jsi;

import java.awt.Component;
import java.awt.Frame;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

/**
 * This is utility class to provide static utility methods.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public final class Util {

	
	public static String DATE_FORMAT = "yyyy-MM-dd HH-mm-ss";
			
	
	public static int DECIMAL_PRECISION = 2;

	
	/**
	 * Default Java regular expression for splitting a sentence into many words (tokens), including space.
	 */
	public final static String DEFAULT_SEP         = "[[\\s][::][\\|][,][;]]";

	
	/**
	 * Default Java regular expression for splitting a sentence into many words (tokens), not including white space.
	 */
	public final static String NOSPACE_DEFAULT_SEP = "[[::][\\|][,][;]]";

	
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
	public static Frame getFrameForComponent(Component comp) {
	    if (comp == null)
	        return null;
	    if (comp instanceof Frame)
	        return (Frame)comp;
	    else
	    	return getFrameForComponent(comp.getParent());
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
	 * Converting the specified number into a string.
	 * @param number specified number.
	 * @return text format of number of the specified number.
	 */
	public static String format(double number) {
		return String.format("%." + DECIMAL_PRECISION + "f", number);
	}


}
