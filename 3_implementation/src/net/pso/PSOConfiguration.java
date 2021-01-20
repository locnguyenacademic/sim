/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.pso;

import java.io.Serializable;

/**
 * This class specifies configuration of particle swarm optimization (PSO) algorithm.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 * @param <T> parameter type.
 */
public class PSOConfiguration<T> implements Cloneable, Serializable {


	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Phi 1 parameter.
	 */
	public T phi1;

	
	/**
	 * Phi 2 parameter.
	 */
	public T phi2;

	
	/**
	 * Omega parameter.
	 */
	public T omega;

	
	/**
	 * Chi parameter.
	 */
	public T chi;

	
	/**
	 * Lower bound parameter.
	 */
	public T[] lower;

	
	/**
	 * Upper bound parameter.
	 */
	public T[] upper;
	
	
	/**
	 * Default constructor.
	 */
	public PSOConfiguration() {

	}

	
}
