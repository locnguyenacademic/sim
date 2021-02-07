/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.pso;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.commons.math3.random.RandomDataGenerator;

import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Profile;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.parser.TextParserUtil;

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
	 * Terminated threshold.
	 */
	public final static String TERMINATED_THRESHOLD_FIELD = "terminated_threshold";

	
	/**
	 * Default value for terminated threshold .
	 */
	public final static double TERMINATED_THRESHOLD_DEFAULT = 0.001;
	
	
	/**
	 * Terminated ratio mode.
	 */
	public final static String TERMINATED_RATIO_MODE_FIELD = "terminated_ratio_mode";

	
	/**
	 * Default value for terminated ratio mode.
	 */
	public final static boolean TERMINATED_RATIO_MODE_DEFAULT = false;

	
	/**
	 * Default constructor.
	 */
	public PSOImpl() {
		super();
	}

	
	@Override
	protected boolean terminatedCondition(Optimizer<Double> curOptimizer, Optimizer<Double> preOptimizer) {
		if (curOptimizer == null || preOptimizer == null) return false;
		
		double terminatedThreshold = config.getAsReal(TERMINATED_THRESHOLD_FIELD);
		terminatedThreshold = Util.isUsed(terminatedThreshold) && terminatedThreshold >= 0 ? terminatedThreshold : TERMINATED_THRESHOLD_DEFAULT;
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
			return Util.newList();
		boolean fdrMode = config.getAsBoolean(PSOConfig.NEIGHBORS_FDR_MODE_FIELD);
		double fdrThreshold = config.getAsReal(PSOConfig.NEIGHBORS_FDR_THRESHOLD_FIELD);
		if (!fdrMode || !Util.isUsed(fdrThreshold)) return Util.newList();
		
		if (!targetParticle.position.isValid(targetParticle.value))
			targetParticle.value = func.eval(targetParticle.position);
		if (!targetParticle.position.isValid(targetParticle.value))
			return Util.newList();

		List<Particle<Double>> neighbors = Util.newList();
		for (Particle<Double> particle : swarm) {
			if (particle.position == null || particle == targetParticle) continue;
			
			if (!particle.position.isValid(particle.value))
				particle.value = func.eval(particle.position);
			if (!particle.position.isValid(particle.value))
				continue;
			
			double fdis = Math.abs(targetParticle.value - particle.value);
			double xdis = targetParticle.position.distance(particle.position);
			if (Util.isUsed(fdis) && Util.isUsed(xdis) && fdis >= fdrThreshold*xdis) {
				neighbors.add(particle);
			}
		}
		
		return neighbors;
	}


	@Override
	protected Function<Double> defineExprFunction(List<String> varNames, String expr) {
		return new ExprFunction(varNames, expr);
	}


	@Override
	protected Vector<Double> customizeConstrictWeight(Particle<Double> targetParticle, Optimizer<Double> optimizer) {
		boolean probMode = config.getAsBoolean(PSOConfig.CONSTRICT_WEIGHT_PROB_MODE_FIELD);
		if (!probMode || func == null) return null;
		
		double weight = config.getAsReal(PSOConfig.CONSTRICT_WEIGHT_FIELD);
		weight = Util.isUsed(weight) ? weight : PSOConfig.CONSTRICT_WEIGHT_DEFAULT;
		int n = func.getVarNum();
		Vector<Double> constrictWeight = func.createVector(0.0);
		for (int i = 0; i < n; i++) constrictWeight.setValue(i, weight);
		if (targetParticle == null || targetParticle.bestPosition == null)
			return constrictWeight;
		
		if (optimizer == null || optimizer.bestPosition == null) return constrictWeight;
		
		RandomDataGenerator rnd = new RandomDataGenerator();
		for (int i = 0; i < n; i++) {
			double mean = (targetParticle.bestPosition.getValueAsReal(i) + optimizer.bestPosition.getValueAsReal(i)) / 2.0;
			double deviate = Math.abs(targetParticle.bestPosition.getValueAsReal(i) - optimizer.bestPosition.getValueAsReal(i));
			double variance = deviate * deviate;
			
			double w = Constants.UNUSED;
			if (variance == 0) {
				w = weight;
			}
			else {
				double z = rnd.nextGaussian(mean, deviate);
				double d = mean - z;
				w = Math.exp(-0.5*d*d/variance);
			}
			
			if (Util.isUsed(w)) constrictWeight.setValue(i, w);
		}
		
		return constrictWeight;
	}
	
	
	@Override
	public PSOConfig<?> getPSOConfig() throws RemoteException {
		if (func == null)
			return new PSOConfig<Double>();
		else
			return func.extractPSOConfig(config);
	}


	@Override
	public void setPSOConfig(PSOConfig<?> psoConfig) throws RemoteException {
		@SuppressWarnings("unchecked")
		PSOConfig<Double> psoc = (PSOConfig<Double>)psoConfig;

		config.put(PSOConfig.COGNITIVE_WEIGHT_FIELD, psoc.cognitiveWeight);
		config.put(PSOConfig.SOCIAL_WEIGHT_GLOBAL_FIELD, psoc.socialWeightGlobal);
		config.put(PSOConfig.SOCIAL_WEIGHT_LOCAL_FIELD, psoc.socialWeightLocal);
		config.put(PSOConfig.INERTIAL_WEIGHT_FIELD, psoc.inertialWeight);
		config.put(PSOConfig.CONSTRICT_WEIGHT_FIELD, psoc.constrictWeight);
		config.put(PSOConfig.POSITION_LOWER_BOUND_FIELD, TextParserUtil.toText(psoc.lower, ","));
		config.put(PSOConfig.POSITION_UPPER_BOUND_FIELD, TextParserUtil.toText(psoc.upper, ","));
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

			List<Double> boundList = TextParserUtil.parseListByClass(config.getAsString(key), Double.class, ",");
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
	public String getName() {
		return "pso_general";
	}


	@Override
	public DataConfig createDefaultConfig() {
		DataConfig config = super.createDefaultConfig();
		config.put(TERMINATED_THRESHOLD_FIELD, TERMINATED_THRESHOLD_DEFAULT);
		config.put(TERMINATED_RATIO_MODE_FIELD, TERMINATED_RATIO_MODE_DEFAULT);
		config.put(PSOConfig.POSITION_LOWER_BOUND_FIELD, PSOConfig.POSITION_LOWER_BOUND_DEFAULT);
		config.put(PSOConfig.POSITION_UPPER_BOUND_FIELD, PSOConfig.POSITION_UPPER_BOUND_DEFAULT);
		config.put(PSOConfig.COGNITIVE_WEIGHT_FIELD, PSOConfig.COGNITIVE_WEIGHT_DEFAULT);
		config.put(PSOConfig.SOCIAL_WEIGHT_GLOBAL_FIELD, PSOConfig.SOCIAL_WEIGHT_GLOBAL_DEFAULT);
		config.put(PSOConfig.SOCIAL_WEIGHT_LOCAL_FIELD, PSOConfig.SOCIAL_WEIGHT_LOCAL_DEFAULT);
		config.put(PSOConfig.INERTIAL_WEIGHT_FIELD, PSOConfig.INERTIAL_WEIGHT_DEFAULT);
		config.put(PSOConfig.CONSTRICT_WEIGHT_FIELD, PSOConfig.CONSTRICT_WEIGHT_DEFAULT);
		config.put(PSOConfig.CONSTRICT_WEIGHT_PROB_MODE_FIELD, PSOConfig.CONSTRICT_WEIGHT_PROB_MODE_DEFAULT);
		config.put(PSOConfig.NEIGHBORS_FDR_MODE_FIELD, PSOConfig.NEIGHBORS_FDR_MODE_DEFAULT);
		config.put(PSOConfig.NEIGHBORS_FDR_THRESHOLD_FIELD, PSOConfig.NEIGHBORS_FDR_THRESHOLD_DEFAULT);
		
		return config;
	}


	@Override
	public Functor<Double> createFunctor(Profile profile) {
		if (profile == null || profile.getAttCount() < 6) return null;
		
		Functor<Double> functor = new Functor<Double>();

		String expr = profile.getValueAsString(0);
		expr = expr != null ? expr.trim() : null;
		if (expr == null) return null;
		List<String> varNames = TextParserUtil.parseListByClass(profile.getValueAsString(1), String.class, ",");
		if (varNames.size() == 0) return null;
		
		functor.func = defineExprFunction(varNames, expr);
		if (functor.func == null) return null;
		
		try {
			functor.psoConfig = functor.func.extractPSOConfig(getConfig());
			functor.psoConfig.lower = functor.func.extractBound(profile.getValueAsString(2));
			functor.psoConfig.upper = functor.func.extractBound(profile.getValueAsString(3));
		} catch (Exception e) {LogUtil.trace(e);}
		
		Vector<Double> bestPosition = functor.func.createVector(0.0);
		List<Double> position = TextParserUtil.parseListByClass(profile.getValueAsString(4), Double.class, ",");
		int n = Math.min(bestPosition.getAttCount(), position.size());
		for (int i = 0; i < n; i++) {
			bestPosition.setValue(i, position.get(i));
		}
		
		Double bestValue = null;
		try {
			bestValue = Double.parseDouble(profile.getValueAsString(5));
		} catch (Exception e) {LogUtil.trace(e);}
		
		functor.func.setOptimizer(new Optimizer<Double>(bestPosition, bestValue));
		
		return functor;
	}


}
