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
	 * Default value for lower bound of position.
	 */
	public final static String POSITION_LOWER_BOUND_DEFAULT = "-100, -100"; //"-1, -1";


	/**
	 * Default value for upper bound of position.
	 */
	public final static String POSITION_UPPER_BOUND_DEFAULT = "100, 100"; //"1, 1";
	
	
	/**
	 * Default value for cognitive weight parameter.
	 */
	public final static double COGNITIVE_WEIGHT_DEFAULT = 1.4962;

	
	/**
	 * Default value for global social weight.
	 */
	public final static double SOCIAL_WEIGHT_GLOBAL_DEFAULT = 1.4962;

	
	/**
	 * Default value for local social weight.
	 */
	public final static double SOCIAL_WEIGHT_LOCAL_DEFAULT = 1.4962;

	
	/**
	 * Default value for inertial weight.
	 */
	public final static double INERTIAL_WEIGHT_DEFAULT = 0.7298;

	
	/**
	 * Default value for constriction weight.
	 */
	public final static double CONSTRICT_WEIGHT_DEFAULT = 1;

	
	/**
	 * Probabilistic constriction weight mode.
	 */
	public final static String CONSTRICT_WEIGHT_PROB_MODE_FIELD = "pso_constrict_weight_prob_mode";

	
	/**
	 * Default value for probabilistic constriction weight mode.
	 */
	public final static boolean CONSTRICT_WEIGHT_PROB_MODE_DEFAULT = false;

	
	/**
	 * Fitness distance ratio mode.
	 */
	public final static String NEIGHBORS_FDR_MODE_FIELD = "neighbors_fdr_mode";

	
	/**
	 * Fitness distance ratio mode.
	 */
	public final static boolean NEIGHBORS_FDR_MODE_DEFAULT = false;
	
	
	/**
	 * Fitness distance ratio threshold.
	 */
	public final static String NEIGHBORS_FDR_THRESHOLD_FIELD = "neighbors_fdr_threshold";

	
	/**
	 * Default value for fitness distance ratio threshold.
	 */
	public final static double NEIGHBORS_FDR_THRESHOLD_DEFAULT = 2;

	
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
		boolean fdrMode = config.getAsBoolean(NEIGHBORS_FDR_MODE_FIELD);
		double fdrThreshold = config.getAsReal(NEIGHBORS_FDR_THRESHOLD_FIELD);
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
	protected Vector<Double> defineConstrictWeightVector(Particle<Double> targetParticle, Optimizer<Double> optimizer) {
		boolean probMode = config.getAsBoolean(CONSTRICT_WEIGHT_PROB_MODE_FIELD);
		if (!probMode || func == null) return null;
		
		double weight = config.getAsReal(CONSTRICT_WEIGHT_FIELD);
		weight = Util.isUsed(weight) ? weight : CONSTRICT_WEIGHT_DEFAULT;
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
	public PSOConfiguration<?> getPSOConfiguration() throws RemoteException {
		PSOConfiguration<Double> psoConfig = new PSOConfiguration<Double>();
		
		double cognitiveWeight = config.getAsReal(COGNITIVE_WEIGHT_FIELD);
		psoConfig.cognitiveWeight = Util.isUsed(cognitiveWeight) && cognitiveWeight > 0 ? cognitiveWeight : COGNITIVE_WEIGHT_DEFAULT;
		
		double socialWeightGlobal = config.getAsReal(SOCIAL_WEIGHT_GLOBAL_FIELD);
		psoConfig.socialWeightGlobal = Util.isUsed(socialWeightGlobal) && socialWeightGlobal > 0 ? socialWeightGlobal : SOCIAL_WEIGHT_GLOBAL_DEFAULT;

		double socialWeightLocal = config.getAsReal(SOCIAL_WEIGHT_LOCAL_FIELD);
		psoConfig.socialWeightLocal = Util.isUsed(socialWeightLocal) && socialWeightLocal > 0 ? socialWeightLocal : SOCIAL_WEIGHT_LOCAL_DEFAULT;

		double inertialWeight = config.getAsReal(INERTIAL_WEIGHT_FIELD);
		psoConfig.inertialWeight = Util.isUsed(inertialWeight) && inertialWeight > 0 ? inertialWeight : INERTIAL_WEIGHT_DEFAULT;

		double constrictWeight = config.getAsReal(CONSTRICT_WEIGHT_FIELD);
		psoConfig.constrictWeight = Util.isUsed(constrictWeight) && constrictWeight > 0 ? constrictWeight : CONSTRICT_WEIGHT_DEFAULT;

		psoConfig.lower = extractBound(POSITION_LOWER_BOUND_FIELD);
		
		psoConfig.upper = extractBound(POSITION_UPPER_BOUND_FIELD);

		return psoConfig;
	}


	@Override
	public void setPSOConfiguration(PSOConfiguration<?> psoConfig) throws RemoteException {
		@SuppressWarnings("unchecked")
		PSOConfiguration<Double> psoc = (PSOConfiguration<Double>)psoConfig;

		config.put(COGNITIVE_WEIGHT_FIELD, psoc.cognitiveWeight);
		config.put(SOCIAL_WEIGHT_GLOBAL_FIELD, psoc.socialWeightGlobal);
		config.put(SOCIAL_WEIGHT_LOCAL_FIELD, psoc.socialWeightLocal);
		config.put(INERTIAL_WEIGHT_FIELD, psoc.inertialWeight);
		config.put(CONSTRICT_WEIGHT_FIELD, psoc.constrictWeight);
		config.put(POSITION_LOWER_BOUND_FIELD, TextParserUtil.toText(psoc.lower, ","));
		config.put(POSITION_UPPER_BOUND_FIELD, TextParserUtil.toText(psoc.upper, ","));
	}


	/**
	 * Extracting bound.
	 * @param key key of bound property.
	 * @return extracted bound.
	 */
	private Double[] extractBound(String key) {
		try {
			if (!config.containsKey(key))
				return new Double[0];
			else
				return TextParserUtil.parseListByClass(config.getAsString(key), Double.class, ",").toArray(new Double[] {});
		}
		catch (Throwable e) {}
		
		return new Double[0];
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
		config.put(POSITION_LOWER_BOUND_FIELD, POSITION_LOWER_BOUND_DEFAULT);
		config.put(POSITION_UPPER_BOUND_FIELD, POSITION_UPPER_BOUND_DEFAULT);
		config.put(COGNITIVE_WEIGHT_FIELD, COGNITIVE_WEIGHT_DEFAULT);
		config.put(SOCIAL_WEIGHT_GLOBAL_FIELD, SOCIAL_WEIGHT_GLOBAL_DEFAULT);
		config.put(SOCIAL_WEIGHT_LOCAL_FIELD, SOCIAL_WEIGHT_LOCAL_DEFAULT);
		config.put(INERTIAL_WEIGHT_FIELD, INERTIAL_WEIGHT_DEFAULT);
		config.put(CONSTRICT_WEIGHT_FIELD, CONSTRICT_WEIGHT_DEFAULT);
		config.put(CONSTRICT_WEIGHT_PROB_MODE_FIELD, CONSTRICT_WEIGHT_PROB_MODE_DEFAULT);
		config.put(NEIGHBORS_FDR_MODE_FIELD, NEIGHBORS_FDR_MODE_DEFAULT);
		config.put(NEIGHBORS_FDR_THRESHOLD_FIELD, NEIGHBORS_FDR_THRESHOLD_DEFAULT);
		
		return config;
	}


}
