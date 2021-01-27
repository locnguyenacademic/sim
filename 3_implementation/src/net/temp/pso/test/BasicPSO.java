/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.temp.pso.test;

import net.hudup.core.data.DataConfig;
import net.pso.PSOImpl;

/**
 * Testing basic PSO.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class BasicPSO extends PSOImpl {


	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public BasicPSO() {

	}

	
	@Override
	public DataConfig createDefaultConfig() {
		DataConfig config = super.createDefaultConfig();
		config.put(FUNC_EXPR_FIELD, "-cos(x1)*cos(x2)*exp(-((x1-PI)^2)-((x2-PI)^2))");
		config.put(FUNC_VARNAMES_FIELD, "x1, x2");
		config.put(POSITION_LOWER_BOUND_FIELD, "-10, -10");
		config.put(POSITION_UPPER_BOUND_FIELD, "10, 10");
		config.put(COGNITIVE_WEIGHT_FIELD, 2.05);
		config.put(SOCIAL_WEIGHT_GLOBAL_FIELD, 2.05);
		config.remove(SOCIAL_WEIGHT_LOCAL_FIELD);
		config.put(INERTIAL_WEIGHT_FIELD, 1.0);
		config.put(CONSTRICT_WEIGHT_FIELD, 0.7298);
		config.remove(CONSTRICT_WEIGHT_PROB_MODE_FIELD);
		config.remove(NEIGHBORS_FDR_MODE_FIELD);
		config.remove(NEIGHBORS_FDR_THRESHOLD_FIELD);
		
		return config;
	}


	@Override
	public String getName() {
		return "01.pso_basic";
	}


}
