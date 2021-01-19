/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.pso;

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
	 * Multiplying this vector by specified number.
	 * @param alpha specified number.
	 * @return resulted vector from multiplying this vector by specified number. 
	 */
	public abstract Vector<T> multiplyCoeff(double alpha);

	
	/**
	 * Wise-multiplying this vector and specified vector.
	 * @param that specified vector.
	 * @return resulted vector from wise-multiplying this vector and specified vector.
	 */
	public abstract Vector<T> multiplyWise(Vector<T> that);

	
}
