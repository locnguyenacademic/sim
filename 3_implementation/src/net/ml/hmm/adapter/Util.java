/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.ml.hmm.adapter;

import java.util.List;
import java.util.Map;

import net.hudup.core.Constants;

/**
 * This is utility class to provide static utility methods. It is also adapter to other libraries.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class Util {
	
	
	/**
	 * The maximum number digits in decimal precision.
	 */
	public static int DECIMAL_PRECISION = 12;

	
	/**
	 * Static code.
	 */
	static {
		try {
			DECIMAL_PRECISION = Constants.DECIMAL_PRECISION;
		}
		catch (Throwable e) {}
	}
	
	
	/**
	 * Creating a new array.
	 * @param <T> element type.
	 * @param tClass element type.
	 * @param length array length.
	 * @return new array
	 */
	public static <T> T[] newArray(Class<T> tClass, int length) {
		return net.hudup.core.Util.newArray(tClass, length);
	}

	
	/**
	 * Creating a new list with initial capacity.
	 * @param <T> type of elements in list.
	 * @param initialCapacity initial capacity of this list.
	 * @return new list with initial capacity.
	 */
	public static <T> List<T> newList(int initialCapacity) {
	    return net.hudup.core.Util.newList(initialCapacity);
	}
	
	
	/**
	 * Creating a new map.
	 * @param <K> type of key.
	 * @param <V> type of value.
	 * @param initialCapacity initial capacity of this list.
	 * @return new map.
	 */
	public static <K, V> Map<K, V> newMap(int initialCapacity) {
	    return net.hudup.core.Util.newMap(initialCapacity);
	}


	/**
	 * Tracing error.
	 * @param e throwable error.
	 */
	public static void trace(Throwable e) {
		net.hudup.core.logistic.LogUtil.trace(e);
	}


}
