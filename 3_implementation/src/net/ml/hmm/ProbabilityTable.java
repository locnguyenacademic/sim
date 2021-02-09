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
 * This class represents ptobability table.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class ProbabilityTable extends DiscreteDistribution {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * List of defined probabilities
	 */
	protected List<Double> probs;
	
	
	/**
	 * Constructor of discrete distribution.
	 * @param n number of probabilities.
	 */
	public ProbabilityTable(int n) {
		super();
		
		probs = Util.newList(n);
		if (n > 0)
			probs.add(1.0);
		for (int i = 1; i < n; i++)
			probs.add(0.0);
	}
	
	
	/**
	 * 
	 * @return The number of probabilities
	 */
	public int size() {
		return probs.size();
	}
	
	
	@Override
	public double getProb(Obs x) {
		return probs.get((int)((MonoObs)x).value);
	}


	@Override
	public double getProb(Obs x, int kComp) {
		return getProb(x);
	}


	/**
	 * Setting probability.
	 * @param x index of probability.
	 * @param prob specified probability.
	 */
	public void setProb(double x, double prob) {
		probs.set((int)x, prob);
	}

	
	@Override
	public void learn(List<Obs> O, List<Double> glist) {
		int T = O.size() - 1;
		if (T < 0) return;
				
		double denominator = 0;
		for (int t = 0; t <= T; t++) {
			double g = glist.get(t);
			denominator += g; 
		}//End for t
		if (denominator == 0)
			return;

		int m = size();
		for (int k = 0; k < m; k++) {
			double numerator = 0;
			for (int t = 0; t <= T; t++) {
				numerator += ( (int)((MonoObs)(O.get(t))).value == k ) ? glist.get(t) : 0;
			}//End for t
			
			setProb(k, numerator/denominator);
		}//End for k
	}


	@Override
	public String toString() {
		int n = probs.size();
		StringBuffer buffer = new StringBuffer();
		
		for (int i = 0; i < n; i++) {
			if (i > 0) buffer.append(" ");
			
			buffer.append(String.format(Util.DECIMAL_FORMAT, probs.get(i)));
		}
		
		return buffer.toString();
	}

	
}
