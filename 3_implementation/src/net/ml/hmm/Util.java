/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.ml.hmm;

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
	 * Decimal format.
	 */
	public static String DECIMAL_FORMAT = "%.12f";;

	
	/**
	 * Static code.
	 */
	static {
		try {
			DECIMAL_FORMAT = "%." + net.ml.hmm.adapter.Util.DECIMAL_PRECISION + "f";
		} catch (Throwable e) {}
	}
	
	
	/**
	 * Creating a new array.
	 * @param <T> element type.
	 * @param tClass element type.
	 * @param length array length.
	 * @return new array
	 */
	public static <T> T[] newArray(Class<T> tClass, int length) {
		try {
		    return net.ml.hmm.adapter.Util.newArray(tClass, length);
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
		    return net.ml.hmm.adapter.Util.newList(initialCapacity);
		}
		catch (Throwable e) {}
		
	    return new ArrayList<T>(initialCapacity);
	}
	
	
	/**
	 * Creating new list with specified size and initial value.
	 * @param <T> type of elements in list.
	 * @param size specified size.
	 * @param initialValue initial value.
	 * @return new list with specified size and initial value.
	 */
	public static <T> List<T> newList(int size, T initialValue) {
		List<T> array = newList(size);
		for (int i = 0; i < size; i++) {
			array.add(initialValue);
		}
		return array;
	}
	
	
	/**
	 * Creating matrix with specified rows, columns, and initial value for each element.
	 * @param <T> element type.
	 * @param rows specified rows.
	 * @param columns specified columns.
	 * @param initialValue initial value for each element.
	 * @return matrix with specified rows, columns, and initial value for each element.
	 */
	public static <T> List<List<T>> newList(int rows, int columns, T initialValue) {
		List<List<T>> matrix = newList(rows);
		for (int i = 0; i < rows; i++) {
			List<T> array = newList(columns, initialValue);
			matrix.add(array);
		}
		
		return matrix;
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
		    return net.ml.hmm.adapter.Util.newMap(initialCapacity);
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
			net.ml.hmm.adapter.Util.trace(e);
		}
		catch (Throwable ex) {
			e.printStackTrace();
		}
	}
	
	
}
