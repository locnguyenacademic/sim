package net.ea.pso.adapter.beans;

import net.ea.pso.PSO;
import net.ea.pso.PSOAbstract;
import net.ea.pso.PSOConfig;
import net.ea.pso.PSOSetting;
import net.ea.pso.adapter.Util;

/**
 * General PSO with probabilistic constriction coefficient.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class PSOProb extends PSOGeneral {


	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public PSOProb() {

	}


	@Override
	protected PSOAbstract<Double> createPSO() {
		PSOAbstract<Double> pso = super.createPSO();
		try {
			PSOConfig config = pso.getConfig();
			config.put(PSO.FUNC_EXPR_FIELD, "-cos(x1)*cos(x2)*exp(-((x1-PI)^2)-((x2-PI)^2))");
			config.put(PSO.FUNC_VARNAMES_FIELD, "x1, x2");
			config.put(PSOSetting.POSITION_LOWER_BOUND_FIELD, "-10, -10");
			config.put(PSOSetting.POSITION_UPPER_BOUND_FIELD, "10, 10");
			config.put(PSOSetting.COGNITIVE_WEIGHT_FIELD, 2.05);
			config.put(PSOSetting.SOCIAL_WEIGHT_GLOBAL_FIELD, 2.05);
			config.put(PSOSetting.SOCIAL_WEIGHT_LOCAL_FIELD, 2.05);
			config.put(PSOSetting.INERTIAL_WEIGHT_FIELD, 1.0);
			config.put(PSOSetting.CONSTRICT_WEIGHT_FIELD, 0.7298);
			config.put(PSOSetting.CONSTRICT_WEIGHT_PROB_MODE_FIELD, true);
			config.put(PSOSetting.NEIGHBORS_FDR_MODE_FIELD, true);
			config.put(PSOSetting.NEIGHBORS_FDR_THRESHOLD_FIELD, 2.0);
		} catch (Throwable e) {Util.trace(e);}
		
		return pso;
	}

	
	@Override
	public String getName() {
		String name = getConfig().getAsString(DUPLICATED_ALG_NAME_FIELD);
		if (name != null && !name.isEmpty())
			return name;
		else
			return "pso_probability";
	}


}
