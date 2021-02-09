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
 * This class represents normal distribution.
 * 
 * @author Loc Nguyen
 * @version 1.0
 */
public class NormalDistribution extends ContinuousDistribution {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Mean of normal distribution.
	 */
	protected double mean = 0;
	
	
	/**
	 * Variance of normal distribution.
	 */
	protected double variance = 1;
	
	
	/**
	 * Constructor with mean and variance.
	 * @param mean specified mean.
	 * @param variance specified variance.
	 */
	public NormalDistribution(double mean, double variance) {
		super();
		setParameters(mean, variance);
	}
	
	
	/**
	 * Default constructor.
	 */
	public NormalDistribution() {
		this(0, 1);
	}
	
	
	@Override
	public double getProb(Obs x) {
		double value = ((MonoObs)x).value;

		if (variance == 0 && mean != value) return 0;
		if (variance == 0 && mean == value) return 1;
		
		double d = value - mean;
		return (1.0 / (Math.sqrt(2*Math.PI*variance))) * Math.exp(-(d*d) / (2*variance));
	}


	@Override
	public double getProb(Obs x, int kComp) {
		return getProb(x);
	}


	@Override
	public void learn(List<Obs> O, List<Double> glist) {
		int T = O.size() - 1;
		if (T < 0) return;

		double numerator1 = 0;
		double denominator = 0;
		List<Double> G = Util.newList(T+1);
		for (int t = 0; t <= T; t++) {
			double g = glist.get(t);
			numerator1 += g * ((MonoObs)(O.get(t))).value;
			denominator += g;
			G.add(g);
		}
		if (denominator == 0)
			return;
		double mean = numerator1/denominator;
		
		double numerator2 = 0;
		for (int t = 0; t <= T; t++) {
			double d = ((MonoObs)(O.get(t))).value - mean;
			numerator2 += G.get(t)*d*d;
		}
		double variance = numerator2/denominator;
		
		if (variance != 0)
			setParameters(mean, variance);
	}


	/**
	 * Setting parameters: mean and variance.
	 * @param mean specified mean.
	 * @param variance specified variance.
	 */
	public void setParameters(double mean, double variance) {
		this.mean = mean;
		this.variance = variance;
	}
	
	
	@Override
	public String toString() {
		return String.format("Normal distribution (mean=" + Util.DECIMAL_FORMAT + ", variance=" + Util.DECIMAL_FORMAT + ")", mean, variance);
	}
	
	
}
