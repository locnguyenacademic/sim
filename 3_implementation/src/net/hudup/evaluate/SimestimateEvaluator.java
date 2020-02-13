package net.hudup.evaluate;

import java.rmi.RemoteException;

import net.hudup.Evaluator;
import net.hudup.alg.cf.NeighborCFTwosCombined;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.Recommender;
import net.hudup.core.logistic.LogUtil;

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
		// TODO Auto-generated constructor stub
	}

	
	@Override
	protected void initializeBeforeRun() {
		// TODO Auto-generated method stub
		super.initializeBeforeRun();
		
		//Setting min-max mode for combined similarities. Improving date: 2019.08.04 by Loc Nguyen
		try { //Use try-catch block because this code block is not important.
			if (this.config.containsKey(NeighborCFTwosCombined.COMBINED_MINMAX_MODE_FIELD)) {
				boolean minmax = this.config.getAsBoolean(NeighborCFTwosCombined.COMBINED_MINMAX_MODE_FIELD);
				for (Alg alg : algList) {
					if (alg instanceof NeighborCFTwosCombined)
						((NeighborCFTwosCombined)alg).setMinmax(minmax);
				}
			}
		}
		catch (Throwable e) {
			LogUtil.error("Error in setting support cache mode");
		}
	}

	
	@Override
	public String getName() throws RemoteException {
		// TODO Auto-generated method stub
		return "Simestimate Evaluator";
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
		String regressEvClassName = SimestimateEvaluator.class.getName();
		new Evaluator().run(new String[] {regressEvClassName});
	}


}
