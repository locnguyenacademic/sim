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
	 * Default value for lower bound of position.
	 */
	public final static String POSITION_LOWER_BOUND_DEFAULT = "-1, -1";


	/**
	 * Default value for upper bound of position.
	 */
	public final static String POSITION_UPPER_BOUND_DEFAULT = "1, 1";
	
	
	/**
	 * Default value for cognitive weight parameter.
	 */
	public final static double COGNITIVE_WEIGHT_DEFAULT = 0.5;

	
	/**
	 * Default value for global social weight.
	 */
	public final static double SOCIAL_WEIGHT_GLOBAL_DEFAULT = 0.5;

	
	/**
	 * Default value for local social weight.
	 */
	public final static double SOCIAL_WEIGHT_LOCAL_DEFAULT = 0.5;

	
	/**
	 * Default value for inertial weight.
	 */
	public final static double INERTIAL_WEIGHT_DEFAULT = 1;

	
	/**
	 * Default value for restriction weight.
	 */
	public final static double RESTRICTION_WEIGHT_DEFAULT = 0.5;

	
	/**
	 * Default constructor.
	 */
	public PSOImpl() {
		super();
	}

	
	@Override
	protected Function<Double> defineExprFunction(List<String> varNames, String expr) {
		return new ExprFunction(varNames, expr);
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

		double restrictionWeight = config.getAsReal(RESTRICTION_WEIGHT_FIELD);
		psoConfig.restrictionWeight = Util.isUsed(restrictionWeight) && restrictionWeight > 0 ? restrictionWeight : RESTRICTION_WEIGHT_DEFAULT;

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
		config.put(RESTRICTION_WEIGHT_FIELD, psoc.restrictionWeight);
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
		return "pso";
	}


	@Override
	public DataConfig createDefaultConfig() {
		DataConfig config = super.createDefaultConfig();
		config.put(POSITION_LOWER_BOUND_FIELD, POSITION_LOWER_BOUND_DEFAULT);
		config.put(POSITION_UPPER_BOUND_FIELD, POSITION_UPPER_BOUND_DEFAULT);
		config.put(COGNITIVE_WEIGHT_FIELD, COGNITIVE_WEIGHT_DEFAULT);
		config.put(SOCIAL_WEIGHT_GLOBAL_FIELD, SOCIAL_WEIGHT_GLOBAL_DEFAULT);
		config.put(SOCIAL_WEIGHT_LOCAL_FIELD, SOCIAL_WEIGHT_LOCAL_DEFAULT);
		config.put(INERTIAL_WEIGHT_FIELD, INERTIAL_WEIGHT_DEFAULT);
		config.put(RESTRICTION_WEIGHT_FIELD, RESTRICTION_WEIGHT_DEFAULT);
		
		return config;
	}


}
