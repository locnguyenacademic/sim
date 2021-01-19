/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.pso;

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
	 * Lower bound of position.
	 */
	public final static String POSITION_LOWER_BOUND_FIELD = "pso_position_lower_bound";
	
	
	/**
	 * Default value for lower bound of position.
	 */
	public final static String POSITION_LOWER_BOUND_DEFAULT = "-1, -1";


	/**
	 * Upper bound of position.
	 */
	public final static String POSITION_UPPER_BOUND_FIELD = "pso_position_upper_bound";
	
	
	/**
	 * Default value for upper bound of position.
	 */
	public final static String POSITION_UPPER_BOUND_DEFAULT = "1, 1";
	
	
	/**
	 * Phi 1 parameter.
	 */
	public final static String PHI1_FIELD = "pso_phi1";

	
	/**
	 * Default value for Phi 1 parameter.
	 */
	public final static double PHI1_DEFAULT = 0.5;

	
	/**
	 * Phi 2 parameter.
	 */
	public final static String PHI2_FIELD = "pso_phi2";

	
	/**
	 * Default value for Phi 2 parameter.
	 */
	public final static double PHI2_DEFAULT = 0.5;

	
	/**
	 * Omega parameter.
	 */
	public final static String OMEGA_FIELD = "pso_omega";

	
	/**
	 * Default value for Omega parameter.
	 */
	public final static double OMEGA_DEFAULT = 1;

	
	/**
	 * Chi parameter.
	 */
	public final static String CHI_FIELD = "pso_chi";

	
	/**
	 * Default value for Chi parameter.
	 */
	public final static double CHI_DEFAULT = 0.5;

	
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
	public Double getPhi1() {
		double phi1 = config.getAsReal(PHI1_FIELD);
		return Util.isUsed(phi1) && phi1 > 0 ? phi1 : PHI1_DEFAULT;
	}


	@Override
	public void setPhi1(Double phi1) {
		config.put(PHI1_FIELD, phi1);
	}


	@Override
	public Double getPhi2() {
		double phi2 = config.getAsReal(PHI2_FIELD);
		return Util.isUsed(phi2) && phi2 > 0 ? phi2 : PHI2_DEFAULT;
	}


	@Override
	public void setPhi2(Double phi2) {
		config.put(PHI2_FIELD, phi2);
	}

	
	@Override
	public Double getOmega() {
		double omega = config.getAsReal(OMEGA_FIELD);
		return Util.isUsed(omega) && omega > 0 ? omega : OMEGA_DEFAULT;
	}


	@Override
	public void setOmega(Double omega) {
		config.put(OMEGA_FIELD, omega);
	}

	
	@Override
	public Double getChi() {
		double chi = config.getAsReal(CHI_FIELD);
		return Util.isUsed(chi) && chi > 0 ? chi : CHI_DEFAULT;
	}


	@Override
	public void setChi(Double chi) {
		config.put(CHI_FIELD, chi);
	}

	
	@Override
	public Double[] getPositionLowerBound() {
		return extractBound(POSITION_LOWER_BOUND_FIELD);
	}
	

	@Override
	public void setPositionLowerBound(Double[] lower) {
		config.put(POSITION_LOWER_BOUND_FIELD, TextParserUtil.toText(lower, ","));
	}


	@Override
	public Double[] getPositionUpperBound() {
		return extractBound(POSITION_UPPER_BOUND_FIELD);
	}


	@Override
	public void setPositionUpperBound(Double[] upper) {
		config.put(POSITION_UPPER_BOUND_FIELD, TextParserUtil.toText(upper, ","));
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
		config.put(PHI1_FIELD, PHI1_DEFAULT);
		config.put(PHI2_FIELD, PHI2_DEFAULT);
		config.put(OMEGA_FIELD, OMEGA_DEFAULT);
		config.put(CHI_FIELD, CHI_DEFAULT);
		
		return config;
	}


}
