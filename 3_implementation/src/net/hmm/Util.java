/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hmm;

import java.util.ArrayList;
import java.util.List;

import net.hudup.core.Constants;

/**
 * This is utility class to provide static utility methods.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
final public class Util {
	
	
	/**
	 * Probability epsilon.
	 */
	public final static double PROB_EPSILON = 0.01f;

	
	/**
	 * The maximum number digits in decimal precision.
	 */
	public final static int DECIMAL_PRECISION = 12;

	
	/**
	 * Creating a new list.
	 * @param <T> type of elements in list.
	 * @return new empty list.
	 */
	public static <T> List<T> newList() {
	    return new ArrayList<T>();
	}

	
	/**
	 * Creating a new list with initial capacity.
	 * @param <T> type of elements in list.
	 * @param initialCapacity initial capacity of this list.
	 * @return new list with initial capacity.
	 */
	public static <T> List<T> newList(int initialCapacity) {
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
	 * Converting the specified number into a string. The number of decimal digits is specified by the parameter {@code decimal}. 
	 * @param number specified number.
	 * @param decimal the number of decimal digits.
	 * @return text format of the specified number.
	 */
	public static String format(double number, int decimal) {
		return String.format("%." + decimal + "f", number);
	}


	/**
	 * Converting the specified number into a string. The number of decimal digits is specified by {@link Constants#DECIMAL_PRECISION}.
	 * @param number specified number.
	 * @return text format of number of the specified number.
	 */
	public static String format(double number) {
		return format(number, DECIMAL_PRECISION);
	}


}
