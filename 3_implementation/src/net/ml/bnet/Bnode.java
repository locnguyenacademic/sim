/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.ml.bnet;

import java.util.List;

/**
 * This interface represents a most abstract node Bayesian network.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface Bnode {

	
	/**
	 * Getting node name.
	 * @return node name.
	 */
	String getName();
	
	
	/**
	 * Setting parents nodes.
	 * @param parentNodes specified parent nodes.
	 */
	void setParents(Bnode...parentNodes);
	
	
	/**
	 * Getting parent nodes.
	 * @return list of parent nodes.
	 */
	List<Bnode> getParents();
	
	
	/**
	 * Getting child nodes.
	 * @return list of child nodes.
	 */
	List<Bnode> getChildren();
	
	
	/**
	 * Setting conditional probability table (CPT).
	 * @param probs conditional probability table.
	 */
	void setProbs(double...probs);
	
	
	/**
	 * Getting conditional probability table (CPT).
	 * @return conditional probability table (CPT).
	 */
	double[] getProbs();
	
	
}
