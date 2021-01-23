/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.pso.evaluate;

import java.rmi.RemoteException;

import net.hudup.Evaluator;
import net.hudup.core.alg.Alg;
import net.hudup.core.evaluate.execute.NonexecuteEvaluator;
import net.pso.PSO;

/**
 * This class is the evaluator for particle swarm optimization (PSO) algorithm.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class PSOEvaluator extends NonexecuteEvaluator {
	

	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public PSOEvaluator() {

	}

	
	@Override
	public String getName() throws RemoteException {
		return "pso";
	}

	
	@Override
	public boolean acceptAlg(Alg alg) throws RemoteException {
		return (alg != null) && (alg instanceof PSO);
	}

	
	/**
	 * The main method to start evaluator.
	 * @param args The argument parameter of main method. It contains command line arguments.
	 * @throws Exception if there is any error.
	 */
	public static void main(String[] args) throws Exception {
		String regressEvClassName = PSOEvaluator.class.getName();
		new Evaluator().run(new String[] {regressEvClassName});
	}


}
