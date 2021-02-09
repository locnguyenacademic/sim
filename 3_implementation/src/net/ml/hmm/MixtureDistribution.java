/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.ml.hmm;

import java.util.Arrays;
import java.util.List;

/**
 * This class represents mixture distribution which is exactly a set of probability density functions along with their weights.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public final class MixtureDistribution implements Distribution {


	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * List of distributions (components).
	 */
	protected List<Distribution> dists;
	
	
	/**
	 * List of weights.
	 */
	protected List<Double> weights;
	
	
	/**
	 * Default constructor.
	 */
	private MixtureDistribution() {
		super();
		dists = Util.newList(0);
		weights = Util.newList(0);
	}
	
	
	/**
	 * Constructor with list of distributions (components) and list of weights.
	 * @param dists list of distributions (components).
	 * @param weights list of weights.
	 */
	public MixtureDistribution(Distribution[] dists, double[] weights) {
		if (dists.length != weights.length)
			throw new RuntimeException("Invalid parameters");
		
		this.dists.addAll(Arrays.asList(dists));
		double sum = 0;
		for (double weight : weights) {
			sum += weight;
			this.weights.add(weight);
		}
		if (sum != 1)
			throw new RuntimeException("Invalid parameters");
	}
	
	
	@Override
	public double getProb(Obs x) {
		double mprob = 0;
		int K = dists.size();
		for (int k = 0; k < K; k++)
			mprob += weights.get(k) * dists.get(k).getProb(x);
		
		return mprob;
	}

	
	@Override
	public double getProb(Obs x, int kComp) {
		if (kComp < 0)
			return getProb(x);
		else
			return weights.get(kComp) * dists.get(kComp).getProb(x);
	}

	
	/**
	 * Replacing distribution (component).
	 * @param k index to replace distribution. 
	 * @param dist specified distribution (component).
	 */
	public void replaceDist(int k, Distribution dist) {
		dists.set(k, dist);
	}
	
	
	/**
	 * Getting component count.
	 * @return component count.
	 */
	public int getComponentCount() {
		return dists.size();
	}
	
	
	/**
	 * Getting component at specified index.
	 * @param kComp specified index.
	 * @return component at specified index.
	 */
	public Distribution getComponent(int kComp) {
		return dists.get(kComp);
	}
	
	
	/**
	 * Learning mixture distribution from observation sequence and gamma list (probability list).
	 * @param O observation sequence.
	 * @param glistByK gamma list.
	 */
	public void learn(List<Obs> O, List<List<Double>> glistByK) {
		int K = dists.size();
		List<Double> numerators = Util.newList(K);
		double denominator = 0;
		
		for (int k = 0; k < K; k++) {
			Distribution dist = dists.get(k);
			if (dist instanceof MixtureDistribution) {
				((MixtureDistribution)dist).learn(O, glistByK);
			}
			else if (dist instanceof AtomicDistribution){
				((AtomicDistribution)dist).learn(O, glistByK.get(k));
			}
			
			List<Double> glist = glistByK.get(k);
			double numerator = 0;
			for (double g : glist) {
				numerator += g;
				denominator += g;
			}
			numerators.add(numerator);
		}//End for k
		
		for (int k = 0; k < K; k++) {
			double weight = numerators.get(k)/denominator;
			weights.set(k, weight);
		}
	}
	
	
	@Override
	public String toString() {
		int K = dists.size();
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("Weights: ");
		for (int k = 0; k < K; k++) {
			if (k > 0)
				buffer.append(", ");
			buffer.append(String.format("w%d=" + Util.DECIMAL_FORMAT, k+1, weights.get(k)));
		}
		
		buffer.append("\nPartial components:\n");
		for (int k = 0; k < K; k++) {
			if (k > 0)
				buffer.append("\n");
			buffer.append("    " + dists.get(k));
		}
		return buffer.toString();
	}


	/**
	 * Creating normal mixture distribution.
	 * @param means means of components.
	 * @param variances variances of components.
	 * @param weights weights of components.
	 * @return normal mixture distribution.
	 */
	public static MixtureDistribution createNormalMixture(double[] means, double[] variances, double[] weights) {
		MixtureDistribution mdist = new MixtureDistribution();
		int n = weights.length;
		for (int i = 0; i < n; i++) {
			NormalDistribution normal = new NormalDistribution(means[i], variances[i]);
			mdist.dists.add(normal);
			mdist.weights.add(weights[i]);
		}
		
		return mdist;
	}
	
	
}
