/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.pso;

import java.util.Collection;

import net.hudup.core.data.AttributeList;
import net.hudup.core.data.Profile;

/**
 * This class models a profile as vector.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public abstract class Vector<T> extends Profile {
	

	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor
	 */
	public Vector() {

	}


	/**
	 * Constructor with specified attribute list.
	 * @param attRef specified attribute list.
	 */
	public Vector(AttributeList attRef) {
		super(attRef);
	}

	
	/**
	 * Checking whether the specified value is valid.
	 * @param value specified value.
	 * @return whether the specified value is valid.
	 */
	public boolean isValid(T value) {
		return value != null;
	}
	
	
	/**
	 * Getting element zero.
	 * @return defined element zero.
	 */
	public abstract T elementZero();
	
	
//	/**
//	 * Getting zero vector.
//	 * @return zero vector.
//	 */
//	public abstract Vector<T> zero();
	
	
//	/**
//	 * Comparing value a with value b.
//	 * @param a value a.
//	 * @param b value b.
//	 * @return -1 if value a is less than value b, 0 if value a is equal to value b, and 1 if value a is larger than value b.
//	 */
//	@SuppressWarnings("unchecked")
//	public int compareTo(T a, T b) {
//		if (a == null && b == null) return 0;
//		if (a == null) return -1;
//		if (b == null) return 1;
//		
//		if ((a instanceof Number) && (b instanceof Number)) {
//			double va = ((Number)a).doubleValue();
//			double vb = ((Number)b).doubleValue();
//			if (va < vb)
//				return -1;
//			else if (va == vb)
//				return 0;
//			else
//				return 1;
//		}
//		else if (a instanceof Comparable<?>)
//			return ((Comparable<T>)a).compareTo(b);
//		else
//			return -1;
//	}
	
	
//	/**
//	 * Calculating the module of value a.
//	 * @param a value a.
//	 * @return the module of value a.
//	 */
//	public abstract double module(T a);

	
	/**
	 * Calculating the module of this vector.
	 * @return the module of this vector.
	 */
	public abstract T module();

	
//	/**
//	 * Calculating distance between value a with value b.
//	 * @param a value a.
//	 * @param b value b.
//	 * @return distance between value a with value b.
//	 */
//	public abstract double distance(T a, T b);
	
	
	/**
	 * Calculating distance between this vector and the other vector.
	 * @param that other vector.
	 * @return distance between this vector and the other vector.
	 */
	public abstract T distance(Vector<T> that);

		
	/**
	 * Duplicate this vector.
	 * @return duplicated vector.
	 */
	public abstract Vector<T> duplicate();
	
	
	/**
	 * Adding this vector and specified vector.
	 * @param that specified vector.
	 * @return resulted vector from adding this vector and specified vector.
	 */
	public abstract Vector<T> add(Vector<T> that);


	/**
	 * Subtracting this vector and specified vector.
	 * @param that specified vector.
	 * @return resulted vector from subtracting this vector and specified vector.
	 */
	public abstract Vector<T> subtract(Vector<T> that);


	/**
	 * Multiplying this vector by specified object.
	 * @param alpha specified object.
	 * @return resulted vector from multiplying this vector by specified number. 
	 */
	public abstract Vector<T> multiply(T alpha);


	/**
	 * Wise-multiplying this vector and specified vector.
	 * @param that specified vector.
	 * @return resulted vector from wise-multiplying this vector and specified vector.
	 */
	public abstract Vector<T> multiplyWise(Vector<T> that);

	
	/**
	 * Calculating mean of collection of vectors.
	 * @param vectors collection of vectors.
	 * @return mean of collection of vectors.
	 */
	public abstract Vector<T> mean(Collection<Vector<T>> vectors);
	
	
}
