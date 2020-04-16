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
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public String getName() throws RemoteException {
		// TODO Auto-generated method stub
		return "Simecommend Evaluator";
	}


	@Override
	public boolean acceptAlg(Alg alg) throws RemoteException {
		// TODO Auto-generated method stub
		if (alg == null) return false;
//		AlgRemote remoteAlg = (alg instanceof AlgRemoteWrapper) ? ((AlgRemoteWrapper)alg).getRemoteAlg() : null;
//		if ((remoteAlg != null) && (remoteAlg instanceof Alg))
//			alg = (Alg)remoteAlg;

		return (alg instanceof Recommender);
	}


	/**
	 * The main method to start evaluator.
	 * @param args The argument parameter of main method. It contains command line arguments.
	 * @throws Exception if there is any error.
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String regressEvClassName = SimecommendEvaluator.class.getName();
		new Evaluator().run(new String[] {regressEvClassName});
	}


}
