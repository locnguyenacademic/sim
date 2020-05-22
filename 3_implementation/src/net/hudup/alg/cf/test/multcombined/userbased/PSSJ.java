package net.hudup.alg.cf.test.multcombined.userbased;

import net.hudup.alg.cf.test.multcombined.NeighborCFTwosCombinedMultUserBased;
import net.hudup.core.data.DataConfig;
import net.hudup.core.logistic.ForTest;

public class PSSJ extends NeighborCFTwosCombinedMultUserBased implements ForTest {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Default constructor.
	 */
	public PSSJ() {
		// TODO Auto-generated constructor stub
	}


	@Override
	public String getDefaultMeasure() {
		// TODO Auto-generated method stub
		return PSS;
	}

	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		String name = getConfig().getAsString(DUPLICATED_ALG_NAME_FIELD);
		if (name != null && !name.isEmpty())
			return name;
		else
			return "mcu04.01.pss_jaccard";
	}


	@Override
	public DataConfig createDefaultConfig() {
		DataConfig config = super.createDefaultConfig();
		config.put(MEASURE, PSS);
		config.put(OTHER_MEASURE, JACCARD);
		
		return config;
	}


}
