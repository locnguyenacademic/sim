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
 * This class represents the recommendation evaluator for similarity measures.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class SimecommendEvaluator extends RecommendEvaluator {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public SimecommendEvaluator() {

	}

	
	@Override
	public String getName() throws RemoteException {
		return "Simecommend Evaluator";
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
		String regressEvClassName = SimecommendEvaluator.class.getName();
		new Evaluator().run(new String[] {regressEvClassName});
	}


}
