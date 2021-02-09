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
 * This class represents exponential distribution.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class ExponentialDistribution extends ContinuousDistribution {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Parameter lambda of exponential distribution.
	 */
	protected double lambda = 0;
	
	
	/**
	 * Constructor with lambda parameter.
	 * @param lambda specified lambda.
	 */
	public ExponentialDistribution(double lambda) {
		super();
		setParameters(lambda);
	}


	@Override
	public double getProb(Obs x) {
		double value = ((MonoObs)x).value;
		return lambda * Math.exp(-lambda*value);
	}

	
	@Override
	public double getProb(Obs x, int kComp) {
		return getProb(x);
	}

	
	@Override
	public void learn(List<Obs> O, List<Double> glist) {
		int T = O.size() - 1;
		if (T < 0) return;

		double numerator = 0;
		double denominator = 0;
		for (int t = 0; t <= T; t++) {
			double g = glist.get(t);
			numerator += g;
			denominator += g * ((MonoObs)(O.get(t))).value;
		}
		
		if (numerator != 0)
			setParameters(denominator / numerator);
	}


	/**
	 * Setting parameters: lambda.
	 * @param lambda lambda parameter of exponential distribution.
	 */
	public void setParameters(double lambda) {
		this.lambda = lambda;
	}

	
	@Override
	public String toString() {
		return String.format("Exponential distribution (lambda=" + Util.DECIMAL_FORMAT + ")", lambda);
	}
	

}
