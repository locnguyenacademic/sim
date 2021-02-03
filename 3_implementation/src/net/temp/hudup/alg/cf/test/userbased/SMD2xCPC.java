package net.temp.hudup.alg.cf.test.userbased;

import net.hudup.alg.cf.nb.Measure;
import net.hudup.alg.cf.nb.NeighborCFTwosCombinedUserBased;
import net.hudup.core.data.DataConfig;
import net.hudup.core.logistic.ForTest;

public class SMD2xCPC extends NeighborCFTwosCombinedUserBased implements ForTest {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Default constructor.
	 */
	public SMD2xCPC() {

	}

	
	@Override
	public String getDefaultMeasure() {
		return Measure.SMD2;
	}

	
	@Override
	public String getName() {
		String name = getConfig().getAsString(DUPLICATED_ALG_NAME_FIELD);
		if (name != null && !name.isEmpty())
			return name;
		else
			return "u09.14.02.smd2xcpc";
	}


	@Override
	public DataConfig createDefaultConfig() {
		DataConfig config = super.createDefaultConfig();
		config.put(MEASURE, Measure.SMD2);
		config.put(OTHER_MEASURE, Measure.CPC);
		config.put(COMBINED_TYPE_FIELD, COMBINED_TYPE_MULTIPLY);
		config.put(COMBINED_MINMAX_MODE_FIELD, false);
		config.put(COMBINED_WEIGHT1_FIELD, 1);
		config.put(COMBINED_WEIGHT2_FIELD, 1);
		
		return config;
	}


}
