/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.pso;

import java.io.Serializable;

import net.hudup.core.data.Attribute;
import net.hudup.core.data.ProfileVector;

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
	 * @param arg argument.
	 * @return evaluated value.
	 */
	double eval(ProfileVector arg);
	
	
	/**
	 * Getting number of variables.
	 * @return number of variables.
	 */
	int getVarNum();
	
	
	/**
	 * Getting variable at specified index.
	 * @param index specified index.
	 * @return variable at specified index.
	 */
	Attribute getVar(int index);
	
	
	/**
	 * Getting optimizer.
	 * @return optimizer.
	 */
	Optimizer getOptimizer();
	
	
	/**
	 * Setting optimizer.
	 * @param optimizer specified optimizer.
	 */
	void setOptimizer(Optimizer optimizer);
	
	
}
