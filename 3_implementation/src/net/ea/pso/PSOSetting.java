/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.ea.pso;

import java.io.Serializable;

/**
 * This class specifies configuration of particle swarm optimization (PSO) algorithm.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 * @param <T> data type.
 */
public class PSOSetting<T> implements Cloneable, Serializable {


	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Particle number.
	 */
	public final static String PARTICLE_NUMBER_FIELD = "pso_particle_number";
	
	
	/**
	 * Default value for particle number.
	 */
	public final static int PARTICLE_NUMBER_DEFAULT = 50;

	
	/**
	 * Lower bound of position.
	 */
	public final static String POSITION_LOWER_BOUND_FIELD = "pso_position_lower_bound";
	
	
	/**
	 * Default value for lower bound of position.
	 */
	public final static String POSITION_LOWER_BOUND_DEFAULT = "-1";


	/**
	 * Upper bound of position.
	 */
	public final static String POSITION_UPPER_BOUND_FIELD = "pso_position_upper_bound";
	
	
	/**
	 * Default value for upper bound of position.
	 */
	public final static String POSITION_UPPER_BOUND_DEFAULT = "1";
	
	
	/**
	 * Cognitive weight.
	 */
	public final static String COGNITIVE_WEIGHT_FIELD = "pso_weight_cognitive";

	
	/**
	 * Default value for cognitive weight parameter.
	 */
	public final static double COGNITIVE_WEIGHT_DEFAULT = 1.4962;

	
	/**
	 * Global social weight.
	 */
	public final static String SOCIAL_WEIGHT_GLOBAL_FIELD = "pso_weight_social_global";

	
	/**
	 * Default value for global social weight.
	 */
	public final static double SOCIAL_WEIGHT_GLOBAL_DEFAULT = 1.4962;

	
	/**
	 * Global social weight.
	 */
	public final static String SOCIAL_WEIGHT_LOCAL_FIELD = "pso_weight_social_local";

	
	/**
	 * Default value for local social weight.
	 */
	public final static double SOCIAL_WEIGHT_LOCAL_DEFAULT = 1.4962;

	
	/**
	 * Inertial weight.
	 */
	public final static String INERTIAL_WEIGHT_FIELD = "pso_weight_inertial";

	
	/**
	 * Default value for inertial weight.
	 */
	public final static double INERTIAL_WEIGHT_DEFAULT = 0.7298;

	
	/**
	 * Constriction weight.
	 */
	public final static String CONSTRICT_WEIGHT_FIELD = "pso_weight_constrict";

	
	/**
	 * Default value for constriction weight.
	 */
	public final static double CONSTRICT_WEIGHT_DEFAULT = 1;

	
	/**
	 * Probabilistic constriction weight mode.
	 */
	public final static String CONSTRICT_WEIGHT_PROB_MODE_FIELD = "pso_weight_constrict_prob_mode";

	
	/**
	 * Default value for probabilistic constriction weight mode.
	 */
	public final static boolean CONSTRICT_WEIGHT_PROB_MODE_DEFAULT = false;

	
	/**
	 * Probabilistic constriction weight accelerator.
	 */
	public final static String CONSTRICT_WEIGHT_PROB_ACC_FIELD = "pso_weight_constrict_prob_acc";

	
	/**
	 * Default value for probabilistic constriction weight accelerator.
	 */
	public final static double CONSTRICT_WEIGHT_PROB_ACC_DEFAULT = 1;

	
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
	 * Cognitive weight.
	 */
	public T cognitiveWeight;

	
	/**
	 * Global social weight.
	 */
	public T socialWeightGlobal;

	
	/**
	 * Local social weight.
	 */
	public T socialWeightLocal;

	
	/**
	 * Inertial weight.
	 */
	public Vector<T> inertialWeight;

	
	/**
	 * Restriction weight.
	 */
	public Vector<T> constrictWeight;

	
	/**
	 * Lower bound parameter.
	 */
	public T[] lower;

	
	/**
	 * Upper bound parameter.
	 */
	public T[] upper;
	
	
	/**
	 * Default constructor.
	 */
	public PSOSetting() {

	}

	
}
