/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.ea.pso;

import java.util.Collection;

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
	 * Getting value at specified index.
	 * @param index specified index.
	 * @return value at specified index.
	 */
	public abstract T get(int index);
	
	
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
	
	
	/**
	 * Calculating the module of this vector.
	 * @return the module of this vector.
	 */
	public abstract T module();

	
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
