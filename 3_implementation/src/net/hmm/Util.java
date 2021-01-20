/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hmm;

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
	public static String DECIMAL_FORMAT = "%." + net.hmm.adapter.Util.DECIMAL_PRECISION + "f";

	
	/**
	 * Creating a new list with initial capacity.
	 * @param <T> type of elements in list.
	 * @param initialCapacity initial capacity of this list.
	 * @return new list with initial capacity.
	 */
	public static <T> List<T> newList(int initialCapacity) {
	    return net.hmm.adapter.Util.newList(initialCapacity);
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
	    return net.hmm.adapter.Util.newMap(initialCapacity);
	}


	/**
	 * Tracing error.
	 * @param e throwable error.
	 */
	public static void trace(Throwable e) {
		net.hmm.adapter.Util.trace(e);
	}
	
	
}
