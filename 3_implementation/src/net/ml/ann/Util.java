/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.ml.ann;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is utility class to provide static utility methods.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class Util {
	
	
	/**
	 * Creating a new array.
	 * @param <T> element type.
	 * @param tClass element type.
	 * @param length array length.
	 * @return new array
	 */
	public static <T> T[] newArray(Class<T> tClass, int length) {
		try {
		    return net.ml.ann.adapter.Util.newArray(tClass, length);
		}
		catch (Throwable e) {}
		
		@SuppressWarnings("unchecked")
		T[] array = (T[]) Array.newInstance(tClass, length);
		return array;
	}

	
	/**
	 * Creating a new list with initial capacity.
	 * @param <T> type of elements in list.
	 * @param initialCapacity initial capacity of this list.
	 * @return new list with initial capacity.
	 */
	public static <T> List<T> newList(int initialCapacity) {
		try {
		    return net.ml.ann.adapter.Util.newList(initialCapacity);
		}
		catch (Throwable e) {}
		
	    return new ArrayList<T>(initialCapacity);
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
		    return net.ml.ann.adapter.Util.newMap(initialCapacity);
		}
		catch (Throwable e) {}
		
	    return new HashMap<K, V>(initialCapacity);
	}

	
	/**
	 * Tracing error.
	 * @param e throwable error.
	 */
	public static void trace(Throwable e) {
		try {
			net.ml.ann.adapter.Util.trace(e);
		}
		catch (Throwable ex) {
			e.printStackTrace();
		}
	}
	
	
}
