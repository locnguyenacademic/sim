/**
 * JSI: JAGGED STRATEGY INVESTMENT 
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: jsi.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.jsi.adapter;

import java.util.List;
import java.util.Set;
import java.util.Vector;

/**
 * This is utility class to provide static utility methods. It is also adapter to other libraries.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public final class Util {

	
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
	 * Creating a new set with initial capacity.
	 * @param <T> type of elements in set.
	 * @param initialCapacity initial capacity of this list.
	 * @return new set.
	 */
	public static <T> Set<T> newSet(int initialCapacity) {
	    return net.hudup.core.Util.newSet(initialCapacity);
	}

	
	/**
	 * Creating a new vector with initial capacity.
	 * @param <T> type of elements in vector.
	 * @param initialCapacity initial capacity of this vector.
	 * @return new vector.
	 */
	public static <T> Vector<T> newVector(int initialCapacity) {
	    return net.hudup.core.Util.newVector(initialCapacity);
	}


}
