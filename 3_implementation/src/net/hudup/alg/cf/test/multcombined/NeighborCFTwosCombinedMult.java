package net.hudup.alg.cf.test.multcombined;

import net.hudup.alg.cf.NeighborCFTwosCombined;
import net.hudup.core.data.DataConfig;

public abstract class NeighborCFTwosCombinedMult extends NeighborCFTwosCombined {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Default constructor.
	 */
	public NeighborCFTwosCombinedMult() {
		// TODO Auto-generated constructor stub
	}


	@Override
	public DataConfig createDefaultConfig() {
		// TODO Auto-generated method stub
		DataConfig config = super.createDefaultConfig();
		config.put(COMBINED_WEIGHT1_FIELD, 1);
		config.put(COMBINED_WEIGHT2_FIELD, 1);
		config.put(COMBINED_TYPE_FIELD, COMBINED_TYPE_MULTIPLY);
		return config;
	}

	
}
