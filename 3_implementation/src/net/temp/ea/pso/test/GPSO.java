/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.temp.ea.pso.test;

import net.ea.pso.PSOConfig;
import net.ea.pso.PSOImpl;
import net.hudup.core.data.DataConfig;
import net.hudup.core.logistic.ForTest;

/**
 * Testing general PSO.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class GPSO extends PSOImpl implements ForTest {


	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public GPSO() {

	}

	
	@Override
	public DataConfig createDefaultConfig() {
		DataConfig config = super.createDefaultConfig();
		config.put(FUNC_EXPR_FIELD, "-cos(x1)*cos(x2)*exp(-((x1-PI)^2)-((x2-PI)^2))");
		config.put(FUNC_VARNAMES_FIELD, "x1, x2");
		config.put(PSOConfig.POSITION_LOWER_BOUND_FIELD, "-10, -10");
		config.put(PSOConfig.POSITION_UPPER_BOUND_FIELD, "10, 10");
		config.put(PSOConfig.COGNITIVE_WEIGHT_FIELD, 2.05);
		config.put(PSOConfig.SOCIAL_WEIGHT_GLOBAL_FIELD, 2.05);
		config.put(PSOConfig.SOCIAL_WEIGHT_LOCAL_FIELD, 2.05);
		config.put(PSOConfig.INERTIAL_WEIGHT_FIELD, 1.0);
		config.put(PSOConfig.CONSTRICT_WEIGHT_FIELD, 0.7298);
		config.remove(PSOConfig.CONSTRICT_WEIGHT_PROB_MODE_FIELD);
		config.put(PSOConfig.NEIGHBORS_FDR_MODE_FIELD, true);
		config.put(PSOConfig.NEIGHBORS_FDR_THRESHOLD_FIELD, 2.0);
		
		return config;
	}


	@Override
	public String getName() {
		return "02.gpso";
	}


}
