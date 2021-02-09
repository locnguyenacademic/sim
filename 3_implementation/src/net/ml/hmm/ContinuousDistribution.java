/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.ml.hmm;

/**
 * This class represents continuous distribution which is exactly probability density function.
 * 
 * @author Loc Nguyen
 * @version 1.0
 */
public abstract class ContinuousDistribution extends AtomicDistribution {


	/**
	 * Probability epsilon.
	 */
	public final static double PROB_EPSILON = 0.01;

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Very small number to form vicinity.
	 */
	protected double epsilon = PROB_EPSILON;
	
	
	/**
	 * Default constructor.
	 */
	public ContinuousDistribution() {
		super();
	}
	

	/**
	 * Getting epsilon.
	 * @return epsilon.
	 */
	public double getEpsilon() {
		return epsilon;
	}

	
	/**
	 * Setting epsilon.
	 * @param epsilon specified epsilon.
	 */
	public void setEpsilon(double epsilon) {
		this.epsilon = epsilon;
	}

	
}
