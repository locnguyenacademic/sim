/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.pso;

import net.hudup.core.logistic.NextUpdate;

/**
 * This class implements particle swarm optimization (PSO) algorithm with probabilistic constriction coefficient.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
@NextUpdate
public class PSOProbConstrict extends PSOImpl {


	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default construction.
	 */
	public PSOProbConstrict() {

	}

	
	@Override
	public String getName() {
		return "pso_prob_constrict";
	}

	
}
