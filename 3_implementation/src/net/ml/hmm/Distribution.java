/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.ml.hmm;

import java.io.Serializable;

/**
 * This interface represents a probability distribution.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface Distribution extends Serializable, Cloneable {

	
	/**
	 * Getting the defined probability at point x according to application.
	 * @param x specified point.
	 * @return defined probability at point x according to application.
	 */
	double getProb(Obs x);
	
	
	/**
	 * Getting the defined probability at point x at given the kth component in mixture model if existent.
	 * @param x specified point.
	 * @param kComp given the kth component in mixture model if existent.
	 * @return the defined probability at point x at given the kth component in mixture model if existent.
	 */
	double getProb(Obs x, int kComp);
	
	
}
