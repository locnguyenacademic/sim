/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.pso.evaluate;

import java.io.Serializable;
import java.rmi.RemoteException;

import net.hudup.Evaluator;
import net.hudup.core.alg.Alg;
import net.hudup.core.data.Profile;
import net.hudup.core.evaluate.HudupRecallMetric;
import net.hudup.core.evaluate.NoneWrapperMetricList;
import net.hudup.core.evaluate.SetupTimeMetric;
import net.hudup.core.evaluate.SpeedMetric;
import net.hudup.core.evaluate.execute.ExecuteAsLearnEvaluator;
import net.hudup.core.evaluate.execute.MAEVector;
import net.pso.Functor;
import net.pso.Optimizer;
import net.pso.PSO;
import net.pso.PSOAbstract;

/**
 * This class is the evaluator for particle swarm optimization (PSO) algorithm.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class PSOEvaluator extends ExecuteAsLearnEvaluator {
	

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

	
	@Override
	protected Serializable extractTestValue(Alg alg, Profile testingProfile) {
		if (testingProfile == null) return null;
		if (!(alg instanceof PSOAbstract<?>)) return null;
		
		PSOAbstract<?> pso = (PSOAbstract<?>)alg;
		Functor<?> functor = Functor.create(pso, testingProfile);
		if (functor == null || functor.func == null)
			return null;
		
		Optimizer<?> optimizer = functor.func.getOptimizer();
		return (Serializable) (optimizer != null ? optimizer.toArray() : null);
	}


	@Override
	public NoneWrapperMetricList defaultMetrics() throws RemoteException {
		NoneWrapperMetricList metricList = new NoneWrapperMetricList();
		
		SetupTimeMetric setupTime = new SetupTimeMetric();
		metricList.add(setupTime);
		
		SpeedMetric speed = new SpeedMetric();
		metricList.add(speed);
		
		HudupRecallMetric hudupRecall = new HudupRecallMetric();
		metricList.add(hudupRecall);
		
		MAEVector maeVector = new MAEVector();
		metricList.add(maeVector);

		return metricList;
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
