/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.pso;

import java.io.Serializable;

import net.hudup.core.alg.SetupAlgEvent;

/**
 * This class represents learning event of particle swarm optimization (PSO) algorithm.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class PSOLearnEvent extends SetupAlgEvent {

	
	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Constructor with a source of event, algorithm name, learning result, progress step, and progress total.
	 * @param source source of event. It is usually an evaluator but it can be the algorithm itself. This source is invalid in remote call because the source is transient variable.
	 * @param type type of event.
	 * @param algName name of the algorithm issuing the setup result.
	 * @param learnResult specified result.
	 * @param progressStep progress step.
	 * @param progressTotalEstimated progress total estimated.
	 */
	public PSOLearnEvent(Object source, Type type, String algName, Serializable learnResult, int progressStep, int progressTotalEstimated) {
		super(source, type, algName, null, learnResult, progressStep, progressTotalEstimated);
	}

	
}
