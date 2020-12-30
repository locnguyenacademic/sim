/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.evaluate;

import java.rmi.RemoteException;

import net.hudup.Evaluator;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.Recommender;

/**
 * This class represents the estimation evaluator for similarity measures.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class SimestimateEvaluator extends EstimateEvaluator {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public SimestimateEvaluator() {

	}

	
	@Override
	public String getName() throws RemoteException {
		return "simestimate";
	}


	@Override
	public boolean acceptAlg(Alg alg) throws RemoteException {
		return (alg != null) && (alg instanceof Recommender);
	}


	/**
	 * The main method to start evaluator.
	 * @param args The argument parameter of main method. It contains command line arguments.
	 * @throws Exception if there is any error.
	 */
	public static void main(String[] args) throws Exception {
		String regressEvClassName = SimestimateEvaluator.class.getName();
		new Evaluator().run(new String[] {regressEvClassName});
	}


}
