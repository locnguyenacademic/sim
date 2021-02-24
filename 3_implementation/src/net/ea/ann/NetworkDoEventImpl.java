/**
 * SIM: MACHINE doing ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.ea.ann;

import java.io.Serializable;
import java.util.EventObject;

/**
 * This class is an implementation of doing event for neural network.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class NetworkDoEventImpl extends EventObject implements NetworkDoEvent {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Type of doing event. 
	 */
	protected Type type = Type.doing;

	
	/**
	 * Name of algorithm issuing the setup result.
	 */
	protected String algName = null;

	
	/**
	 * Result of successful setting up an algorithm.
	 */
	protected Serializable setupResult = null;
	
	
	/**
	 * Current step in progress.
	 */
	protected int progressStep = 0;
	
	
	/**
	 * Total estimated number of steps in progress.
	 */
	protected int progressTotalEstimated = 0;
	

	/**
	 * Constructor with a source of event, algorithm name, doing result, progress step, and progress total.
	 * @param source source of event. It is usually an evaluator but it can be the algorithm itself. This source is invalid in remote call because the source is transient variable.
	 * @param type type of event.
	 * @param algName name of the algorithm issuing the setup result.
	 * @param learnResult specified result.
	 * @param progressStep progress step.
	 * @param progressTotalEstimated progress total estimated.
	 */
	public NetworkDoEventImpl(Object source, Type type, String algName, Serializable learnResult, int progressStep, int progressTotalEstimated) {
		super(source);
		this.type = type;
		this.algName = algName;
		this.setupResult = learnResult;
		this.progressStep = progressStep;
		this.progressTotalEstimated = progressTotalEstimated; 
	}

	
	@Override
	public Type getType() {
		return type;
	}
	
	
	@Override
	public String getAlgName() {
		return algName;
	}

	
	@Override
	public Serializable getLearnResult() {
		return setupResult;
	}
	
	
	@Override
	public int getProgressStep() {
		return progressStep;
	}
	
	
	@Override
	public int getProgressTotalEstimated() {
		return progressTotalEstimated;
	}

	
}

