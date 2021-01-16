/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hmm;

/**
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface Factory {
	
	
	/**
	 * Creating discrete hidden Markov model from transition probability matrix, initial state distribution, and observation probability matrix.
	 * @param A transition probability matrix.
	 * @param PI initial state distribution.
	 * @param B observation probability matrix
	 * @return Discrete hidden Markov model created from transition probability matrix, initial state distribution, and observation probability matrix.
	 */
	HMM createDiscreteHMM(double[][] A, double[] PI, double[][] B);


	/**
	 * Creating discrete hidden Markov model from the number of states and the number of observations with uniform probabilities.
	 * @param nState the number of states.
	 * @param mObs the number of observations.
	 * @return discrete hidden Markov model created from the number of states and the number of observations with uniform probabilities.
	 */
	HMM createDiscreteHMM(int nState, int mObs);

	
	/**
	 * 
	 * @param A
	 * @param PI
	 * @param means
	 * @param variances
	 * @return
	 */
	HMM createNormalHMM(double[][] A, double[] PI, double[] means, double[] variances, double epsilon);

	
	/**
	 * 
	 * @param A
	 * @param PI
	 * @param means
	 * @return
	 */
	HMM createExponentialHMM(double[][] A, double[] PI, double[] means, double epsilon);

	
	/**
	 * 
	 * @param A
	 * @param PI
	 * @param means
	 * @param variances
	 * @return
	 */
	HMM createNormalMixtureHMM(double[][] A, double[] PI, double[][] means, double[][] variances, double[][] weights, double epsilon);
	
	
}
