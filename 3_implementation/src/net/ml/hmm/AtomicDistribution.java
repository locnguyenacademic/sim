/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.ml.hmm;

import java.util.List;

/**
 * This class represents atomic (non-mixture) distribution.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public abstract class AtomicDistribution implements Distribution {


	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public AtomicDistribution() {
		super();
	}
	
	
	/**
	 * Learning distribution from observation sequence and gramma list (probability list).
	 * @param O observation sequence.
	 * @param glist gramma list.
	 */
	public abstract void learn(List<Obs> O, List<Double> glist);
	
	
}
