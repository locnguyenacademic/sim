/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.ml.hmm;

/**
 * This interface represents factory to create hidden Markov model (HMM).
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface Factory {
	
	
	/**
	 * Creating discrete hidden Markov model (HMM) from transition probability matrix, initial state distribution, and observation probability matrix.
	 * @param A transition probability matrix.
	 * @param PI initial state distribution.
	 * @param B observation probability matrix
	 * @return Discrete hidden Markov model created from transition probability matrix, initial state distribution, and observation probability matrix.
	 */
	HMM createDiscreteHMM(double[][] A, double[] PI, double[][] B);


	/**
	 * Creating discrete hidden Markov model (HMM) from the number of states and the number of observations with uniform probabilities.
	 * @param nState the number of states.
	 * @param mObs the number of observations.
	 * @return discrete hidden Markov model created from the number of states and the number of observations with uniform probabilities.
	 */
	HMM createDiscreteHMM(int nState, int mObs);

	
	/**
	 * Create continuous hidden Markov model (HMM) with continuous normal distributions of observations.
	 * @param A transition probability matrix.
	 * @param PI initial state distribution.
	 * @param means means of continuous normal distributions of observations.
	 * @param variances variances of continuous normal distributions of observations.
	 * @return continuous hidden Markov model (HMM) with continuous normal distribution of observations.
	 */
	HMM createNormalHMM(double[][] A, double[] PI, double[] means, double[] variances);

	
	/**
	 * Create continuous hidden Markov model (HMM) with continuous exponential distributions of observations.
	 * @param A transition probability matrix.
	 * @param PI initial state distribution.
	 * @param lambdas lambda parameters of continuous exponential distributions of observations.
	 * @return continuous hidden Markov model (HMM) with continuous exponential distributions of observations.
	 */
	HMM createExponentialHMM(double[][] A, double[] PI, double[] lambdas);

	
	/**
	 * Create continuous hidden Markov model (HMM) with continuous normal mixture distributions of observations.
	 * @param A transition probability matrix.
	 * @param PI initial state distribution.
	 * @param means means of continuous normal mixture distributions of observations.
	 * @param variances variances of continuous normal mixture distributions of observations.
	 * @param weights weights of components.
	 * @return continuous hidden Markov model (HMM) with continuous normal mixture distributions of observations.
	 */
	HMM createNormalMixtureHMM(double[][] A, double[] PI, double[][] means, double[][] variances, double[][] weights);
	
	
}
