/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.ml.hmm;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * This class is a factory to create hidden Markov models, which is an implementation of the interface {@link HMM}.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class FactoryImpl implements Factory {
	
	
	@Override
	public HMM createDiscreteHMM(double[][] A, double[] PI, double[][] B) {
		return DefaultHMM.createDiscreteHMM(A, PI, B);
	}


	@Override
	public HMM createDiscreteHMM(int nState, int mObs) {
		return DefaultHMM.createDiscreteHMM(nState, mObs);
	}


	@Override
	public HMM createNormalHMM(double[][] A, double[] PI, double[] means, double[] variances) {
		return DefaultHMM.createNormalHMM(A, PI, means, variances);
	}


	@Override
	public HMM createExponentialHMM(double[][] A, double[] PI, double[] lambdas) {
		return DefaultHMM.createExponentialHMM(A, PI, lambdas);
	}


	@Override
	public HMM createNormalMixtureHMM(double[][] A, double[] PI, double[][] means, double[][] variances, double[][] weights) {
		return DefaultHMM.createNormalMixtureHMM(A, PI, means, variances, weights);
	}

	
	/**
	 * Main method.
	 * @param args argument parameter.
	 */
	public static void main(String[] args) {
		Factory factory = new FactoryImpl();
		
		DefaultHMM discreteHMM = ((HMMWrapperImpl)factory.createDiscreteHMM(
				new double[][] {
					{0.50f, 0.25f, 0.25f},
					{0.30f, 0.40f, 0.30f},
					{0.25f, 0.25f, 0.50f}}, 
				new double[] {0.33f, 0.33f, 0.33f}, 
				new double[][] {
					{0.60f, 0.20f, 0.15f, 0.05f},
					{0.25f, 0.25f, 0.25f, 0.25f},
					{0.05f, 0.10f, 0.35f, 0.50f}})).getHMMImpl();
		discreteHMM.setStateNames(Arrays.asList("sunny", "cloudy", "rainy"));
		discreteHMM.setObsNames(Arrays.asList("dry", "dryish", "damp", "soggy"));
		
		@SuppressWarnings("unused")
		DefaultHMM randomDiscreteHMM = ((HMMWrapperImpl)factory.createDiscreteHMM(50, 100)).
				getHMMImpl();
		
		DefaultHMM normalHMM = ((HMMWrapperImpl)factory.createNormalHMM(
				new double[][] {
					{0.50f, 0.25f, 0.25f},
					{0.30f, 0.40f, 0.30f},
					{0.25f, 0.25f, 0.50f}}, 
				new double[] {0.33f, 0.33f, 0.33f}, 
				new double[] {0.87f, 0.14f, 0.39f}, 
				new double[] {0.9f, 0.9f, 0.9f})).getHMMImpl(); 
		normalHMM.setStateNames(Arrays.asList("sunny", "cloudy", "rainy"));
		
		DefaultHMM exponentialHMM = ((HMMWrapperImpl)factory.createExponentialHMM(
				new double[][] {
					{0.50f, 0.25f, 0.25f},
					{0.30f, 0.40f, 0.30f},
					{0.25f, 0.25f, 0.50f}}, 
				new double[] {0.33f, 0.33f, 0.33f}, 
				new double[] {1.0f/0.87f, 1.0f/0.14f, 1.0f/0.39f})).getHMMImpl(); 
		exponentialHMM.setStateNames(Arrays.asList("sunny", "cloudy", "rainy"));
		
		DefaultHMM normalMixtureHMM = ((HMMWrapperImpl)factory.createNormalMixtureHMM(
				new double[][] {
					{0.50f, 0.25f, 0.25f},
					{0.30f, 0.40f, 0.30f},
					{0.25f, 0.25f, 0.50f}}, 
				new double[] {0.33f, 0.33f, 0.33f}, 
				new double[][] {
					{0.87f, 0.15f}, {0.39f, 0.89f}, {0.14f, 0.37f}},
				new double[][] {
					{1f, 1f}, {1f, 1f}, {1f, 1f}},
				new double[][] {
					{0.6f, 0.4f}, {0.5f, 0.5f}, {0.4f, 0.6f}})).getHMMImpl();
		normalMixtureHMM.setStateNames(Arrays.asList("sunny", "cloudy", "rainy"));

		DefaultHMM hmm = discreteHMM;
		
		List<Obs> O;
		O = MonoObs.createObsList(3f, 0f, 1f);
//		O = MonoObs.createObsListRandomInteger(100, hmm.getStateNumber());
//		O = MonoObs.createObsList(0.88f, 0.13f, 0.38f);
		
		Path workingDir = Paths.get("working");
		if (!Files.exists(workingDir)) {
			try {
				Files.createDirectory(workingDir);
			}
			catch (Exception e) {
				Util.trace(e);
			}
		}
		Printer printer = new Printer("working/hmm-testresult.txt");
		hmm.addListener(printer);
		hmm.em(O, HMM.LEARN_TERMINATED_THRESHOLD_DEFAULT, HMM.LEARN_TERMINATED_RATIO_MODE_DEFAULT, HMM.LEARN_MAX_ITERATION_DEFAULT);
//		hmm.viterbi(O);

		try {
			hmm.close();
		}
		catch (Exception e) {
			Util.trace(e);
		}
		printer.close();
	}


}
