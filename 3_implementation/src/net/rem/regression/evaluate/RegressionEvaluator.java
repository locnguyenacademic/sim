package net.rem.regression.evaluate;

import java.io.Serializable;

import net.hudup.Evaluator;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.TestAlg;
import net.hudup.core.data.Profile;
import net.hudup.core.evaluate.execute.ExecuteEvaluator;
import net.hudup.core.logistic.LogUtil;
import net.rem.regression.RM;

/**
 * Evaluator for evaluating regression algorithms.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class RegressionEvaluator extends ExecuteEvaluator {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public RegressionEvaluator() {
		// TODO Auto-generated constructor stub
		super();
	}

	
	@Override
	protected Serializable extractTestValue(Alg alg, Profile testingProfile) {
		// TODO Auto-generated method stub
		try {
			return (Serializable) ((RM)alg).extractResponseValue(testingProfile);
		} catch (Exception e) {LogUtil.trace(e);}
		
		return null;
	}

	
	@Override
	public boolean acceptAlg(Alg alg) {
		// TODO Auto-generated method stub
		if (alg == null) return false;
//		AlgRemote remoteAlg = (alg instanceof AlgRemoteWrapper) ? ((AlgRemoteWrapper)alg).getRemoteAlg() : null;
//		if ((remoteAlg != null) && (remoteAlg instanceof Alg))
//			alg = (Alg)remoteAlg;

		return (alg instanceof RM) && (!(alg instanceof TestAlg));
	}

	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Regression Evaluator";
	}


	/**
	 * The main method to start evaluator.
	 * @param args The argument parameter of main method. It contains command line arguments.
	 * @throws Exception if there is any error.
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String regressEvClassName = RegressionEvaluator.class.getName();
		new Evaluator().run(new String[] {regressEvClassName});
	}

	
}
