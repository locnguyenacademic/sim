/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.ml.ann;

import java.io.Serializable;

/**
 * This class represents connection weight between two neurons.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class Weight implements Serializable, Cloneable {


	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Weight.
	 */
	protected double weight = 0;
	
	
	/**
	 * Constructor with real weight.
	 * @param weight real weight.
	 */
	public Weight(double weight) {
		this.weight = weight;
	}

	
	/**
	 * Getting weight.
	 * @return real weight.
	 */
	public double get() {
		return weight;
	}
	
	
	/**
	 * Setting weight.
	 * @param weight real weight.
	 */
	public void set(double weight) {
		this.weight = weight;
	}
	
	
}
