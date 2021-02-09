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
 * This interface represents learning event about hidden Markov model (HMM).
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface HMMDoEvent extends Cloneable, Serializable {

	
	/**
	 * Type of learning event.
	 * @author Loc Nguyen
	 * @version 10.0
	 */
	static enum Type {
		
		/**
		 * Learning task in progress.
		 */
		doing,
		
		/**
		 * All learning tasks are done, which means that learning process is finished.
		 */
		done
	}

	
	/**
	 * Getting event type.
	 * @return event type.
	 */
	Type getType();


	/**
	 * Getting name of algorithm that issues the learning result.
	 * @return name of algorithm that issues the learning result.
	 */
	String getAlgName();

	
	/**
	 * Getting the learning result issued by the algorithm.
	 * @return learning result issued by the algorithm.
	 */
	Serializable getLearnResult();
	
	
	/**
	 * Getting progress step.
	 * @return progress step.
	 */
	int getProgressStep();
	
	
	/**
	 * Getting progress total in estimation.
	 * @return progress total in estimation.
	 */
	int getProgressTotalEstimated();


}
