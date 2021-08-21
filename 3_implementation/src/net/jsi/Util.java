package net.jsi;

import java.awt.Component;
import java.awt.Frame;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
