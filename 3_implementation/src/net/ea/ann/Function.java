/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.ea.ann;

import java.io.Serializable;

/**
 * This interface represents activation function for each neuron.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface Function extends Serializable, Cloneable {

	
	/**
	 * Evaluating specified variable.
	 * @param x specified variable.
	 * @return evaluated value.
	 */
	double eval(double x);
	
	
}
