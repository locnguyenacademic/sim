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
 * This interface represents function.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface Function extends Serializable, Cloneable {

	
	/**
	 * Evaluating this function given arguments.
	 * @param args array of arguments.
	 * @return evaluated value.
	 */
	double evaluate(Object...args);
	
	
	/**
	 * Getting number of variables.
	 * @return number of variables.
	 */
	int getVarNum();
	
	
}
