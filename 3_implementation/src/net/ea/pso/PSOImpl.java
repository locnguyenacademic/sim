/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.ea.pso;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.commons.math3.random.RandomDataGenerator;

/**
 * This class is the default implementation of particle swarm optimization (PSO) algorithm.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class PSOImpl extends PSOAbstract<Double> {


	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public PSOImpl() {
		super();
		
		config.put(TERMINATED_THRESHOLD_FIELD, TERMINATED_THRESHOLD_DEFAULT);
		config.put(TERMINATED_RATIO_MODE_FIELD, TERMINATED_RATIO_MODE_DEFAULT);
		config.put(PSOSetting.POSITION_LOWER_BOUND_FIELD, PSOSetting.POSITION_LOWER_BOUND_DEFAULT);
		config.put(PSOSetting.POSITION_UPPER_BOUND_FIELD, PSOSetting.POSITION_UPPER_BOUND_DEFAULT);
		config.put(PSOSetting.COGNITIVE_WEIGHT_FIELD, PSOSetting.COGNITIVE_WEIGHT_DEFAULT);
		config.put(PSOSetting.SOCIAL_WEIGHT_GLOBAL_FIELD, PSOSetting.SOCIAL_WEIGHT_GLOBAL_DEFAULT);
		config.put(PSOSetting.SOCIAL_WEIGHT_LOCAL_FIELD, PSOSetting.SOCIAL_WEIGHT_LOCAL_DEFAULT);
		config.put(PSOSetting.INERTIAL_WEIGHT_FIELD, PSOSetting.INERTIAL_WEIGHT_DEFAULT);
		config.put(PSOSetting.CONSTRICT_WEIGHT_FIELD, PSOSetting.CONSTRICT_WEIGHT_DEFAULT);
		config.put(PSOSetting.CONSTRICT_WEIGHT_PROB_MODE_FIELD, PSOSetting.CONSTRICT_WEIGHT_PROB_MODE_DEFAULT);
		config.put(PSOSetting.CONSTRICT_WEIGHT_PROB_ACC_FIELD, PSOSetting.CONSTRICT_WEIGHT_PROB_ACC_DEFAULT);
		config.put(PSOSetting.NEIGHBORS_FDR_MODE_FIELD, PSOSetting.NEIGHBORS_FDR_MODE_DEFAULT);
		config.put(PSOSetting.NEIGHBORS_FDR_THRESHOLD_FIELD, PSOSetting.NEIGHBORS_FDR_THRESHOLD_DEFAULT);
	}

	
	@Override
	protected boolean terminatedCondition(Optimizer<Double> curOptimizer, Optimizer<Double> preOptimizer) {
		if (curOptimizer == null || preOptimizer == null) return false;
		
		double terminatedThreshold = config.getAsReal(TERMINATED_THRESHOLD_FIELD);
		terminatedThreshold = !Double.isNaN(terminatedThreshold) && terminatedThreshold >= 0 ? terminatedThreshold : TERMINATED_THRESHOLD_DEFAULT;
		boolean terminatedRatio = config.getAsBoolean(TERMINATED_RATIO_MODE_FIELD);
		if (terminatedRatio)
			return Math.abs(curOptimizer.bestValue - preOptimizer.bestValue) <= terminatedThreshold * Math.abs(preOptimizer.bestValue);
		else
			return Math.abs(curOptimizer.bestValue - preOptimizer.bestValue) <= terminatedThreshold;
	}


	@Override
	protected boolean checkABetterThanB(Double a, Double b) {
		if (func == null) return false;
		
		boolean minimize = config.getAsBoolean(MINIMIZE_MODE_FIELD);
		if (minimize)
			return a < b;
		else
			return a > b;
	}


	@Override
	protected List<Particle<Double>> defineNeighbors(Particle<Double> targetParticle) {
		if (func == null || targetParticle == null || targetParticle.position == null)
			return Util.newList(0);
		boolean fdrMode = config.getAsBoolean(PSOSetting.NEIGHBORS_FDR_MODE_FIELD);
		double fdrThreshold = config.getAsReal(PSOSetting.NEIGHBORS_FDR_THRESHOLD_FIELD);
		if (!fdrMode || Double.isNaN(fdrThreshold)) return Util.newList(0);
		
		if (!targetParticle.position.isValid(targetParticle.value))
			targetParticle.value = func.eval(targetParticle.position);
		if (!targetParticle.position.isValid(targetParticle.value))
			return Util.newList(0);

		List<Particle<Double>> neighbors = Util.newList(0);
		for (Particle<Double> particle : swarm) {
			if (particle.position == null || particle == targetParticle) continue;
			
			if (!particle.position.isValid(particle.value))
				particle.value = func.eval(particle.position);
			if (!particle.position.isValid(particle.value))
				continue;
			
			double fdis = Math.abs(targetParticle.value - particle.value);
			double xdis = targetParticle.position.distance(particle.position);
			if (!Double.isNaN(fdis) && !Double.isNaN(xdis) && fdis >= fdrThreshold*xdis) {
				neighbors.add(particle);
			}
		}
		
		return neighbors;
	}


	@Override
	protected Function<Double> defineExprFunction(List<String> varNames, String expr) {
		if (varNames.size() == 0 || expr == null)
			return null;
		else
			return new ExprFunction(varNames, expr);
	}


	@Override
	protected Vector<Double> customizeConstrictWeight(Particle<Double> targetParticle, Optimizer<Double> optimizer) {
		boolean probMode = config.getAsBoolean(PSOSetting.CONSTRICT_WEIGHT_PROB_MODE_FIELD);
		if (!probMode || func == null) return null;
		
		double weight = config.getAsReal(PSOSetting.CONSTRICT_WEIGHT_FIELD);
		weight = !Double.isNaN(weight) ? weight : PSOSetting.CONSTRICT_WEIGHT_DEFAULT;
		int n = func.getVarNum();
		Vector<Double> constrictWeight = func.createVector(0.0);
		for (int i = 0; i < n; i++) constrictWeight.setValue(i, weight);
		if (targetParticle == null || targetParticle.bestPosition == null)
			return constrictWeight;
		
		if (optimizer == null || optimizer.bestPosition == null) return constrictWeight;
		
		RandomDataGenerator rnd = new RandomDataGenerator();
		double acc = config.getAsReal(PSOSetting.CONSTRICT_WEIGHT_PROB_ACC_FIELD);
		acc = Double.isNaN(acc) ? 1 : acc;
		acc = acc < 1 ? 1 : acc;
		for (int i = 0; i < n; i++) {
			double mean = (targetParticle.bestPosition.getValueAsReal(i) + optimizer.bestPosition.getValueAsReal(i)) / 2.0;
			double deviate = Math.abs(targetParticle.bestPosition.getValueAsReal(i) - optimizer.bestPosition.getValueAsReal(i)) / acc;
			double variance = deviate * deviate;
			
			double w = Double.NaN;
			if (variance == 0) {
				w = weight;
			}
			else {
				double z = rnd.nextGaussian(mean, deviate);
				double d = mean - z;
				w = Math.exp(-0.5*d*d/variance);
			}
			
			if (!Double.isNaN(w)) constrictWeight.setValue(i, w);
		}
		
		return constrictWeight;
	}
	
	
	@Override
	public PSOSetting<Double> getPSOSetting() throws RemoteException {
		if (func == null)
			return new PSOSetting<Double>();
		else
			return func.extractPSOSetting(config);
	}


	@Override
	public void setPSOSetting(PSOSetting<Double> setting) throws RemoteException {
		config.put(PSOSetting.COGNITIVE_WEIGHT_FIELD, setting.cognitiveWeight);
		config.put(PSOSetting.SOCIAL_WEIGHT_GLOBAL_FIELD, setting.socialWeightGlobal);
		config.put(PSOSetting.SOCIAL_WEIGHT_LOCAL_FIELD, setting.socialWeightLocal);
		config.put(PSOSetting.INERTIAL_WEIGHT_FIELD, setting.inertialWeight);
		config.put(PSOSetting.CONSTRICT_WEIGHT_FIELD, setting.constrictWeight);
		config.put(PSOSetting.POSITION_LOWER_BOUND_FIELD, Util.toText(setting.lower, ","));
		config.put(PSOSetting.POSITION_UPPER_BOUND_FIELD, Util.toText(setting.upper, ","));
	}


	/**
	 * Extracting bound.
	 * @param key key of bound property.
	 * @return extracted bound.
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private Double[] extractBound(String key) {
		try {
			if (!config.containsKey(key))
				return func != null ? RealVector.toArray(func.zero()) : new Double[0];

			List<Double> boundList = Util.parseListByClass(config.getAsString(key), Double.class, ",");
			if (boundList == null || boundList.size() == 0)
				return func != null ? RealVector.toArray(func.zero()) : new Double[0];
			if (func == null) return boundList.toArray(new Double[] {});
			
			int n = func.getVarNum();
			if (n < boundList.size()) {
				boundList = boundList.subList(0, n);
				return boundList.toArray(new Double[] {});
			}
			
			double lastValue = boundList.get(boundList.size() - 1);
			n = n - boundList.size();
			for (int i = 0; i < n; i++) boundList.add(lastValue);
			return boundList.toArray(new Double[] {});
		}
		catch (Throwable e) {}
		
		return func != null ? RealVector.toArray(func.zero()) : new Double[0];
	}


	@Override
	public Functor<Double> createFunctor(Profile profile) {
		if (profile == null || profile.getAttCount() < 6) return null;
		
		Functor<Double> functor = new Functor<Double>();

		String expr = profile.getValueAsString(0);
		expr = expr != null ? expr.trim() : null;
		if (expr == null) return null;
		List<String> varNames = Util.parseListByClass(profile.getValueAsString(1), String.class, ",");
		if (varNames.size() == 0) return null;
		
		functor.func = defineExprFunction(varNames, expr);
		if (functor.func == null) return null;
		
		functor.setting = functor.func.extractPSOSetting(config);
		functor.setting.lower = functor.func.extractBound(profile.getValueAsString(2));
		functor.setting.upper = functor.func.extractBound(profile.getValueAsString(3));
		
		Vector<Double> bestPosition = functor.func.createVector(0.0);
		List<Double> position = Util.parseListByClass(profile.getValueAsString(4), Double.class, ",");
		int n = Math.min(bestPosition.getAttCount(), position.size());
		for (int i = 0; i < n; i++) {
			bestPosition.setValue(i, position.get(i));
		}
		
		Double bestValue = null;
		try {
			bestValue = Double.parseDouble(profile.getValueAsString(5));
		} catch (Exception e) {Util.trace(e);}
		
		functor.func.setOptimizer(new Optimizer<Double>(bestPosition, bestValue));
		
		return functor;
	}


}
