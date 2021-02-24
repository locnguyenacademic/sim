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
 * This interface represents doing event about neural network.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface NetworkDoEvent extends Cloneable, Serializable {

	
	/**
	 * Type of doing event.
	 * @author Loc Nguyen
	 * @version 10.0
	 */
	static enum Type {
		
		/**
		 * Doing task in progress.
		 */
		doing,
		
		/**
		 * All doing tasks are done, which means that doing process is finished.
		 */
		done
	}

	
	/**
	 * Getting event type.
	 * @return event type.
	 */
	Type getType();


	/**
	 * Getting name of algorithm that issues the doing result.
	 * @return name of algorithm that issues the doing result.
	 */
	String getAlgName();

	
	/**
	 * Getting the doing result issued by the algorithm.
	 * @return doing result issued by the algorithm.
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
