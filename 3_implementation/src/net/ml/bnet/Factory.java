/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.ml.bnet;

/**
 * This interface represents a factory to create Bayesian network and other relevant objects.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface Factory {

	
	/**
	 * Create Bayesian network.
	 * @return Bayesian network.
	 */
	Bnet createNetwork();
	
	
}
