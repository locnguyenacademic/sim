package net.hudup.alg.cf.test.userbased;

import net.hudup.alg.cf.Measure;
import net.hudup.alg.cf.NeighborCFTwosCombinedUserBased;
import net.hudup.core.data.DataConfig;
import net.hudup.core.logistic.ForTest;

public class SMD2COD extends NeighborCFTwosCombinedUserBased implements ForTest {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Default constructor.
	 */
	public SMD2COD() {
		// TODO Auto-generated constructor stub
	}


	@Override
	public String getDefaultMeasure() {
		// TODO Auto-generated method stub
		return Measure.SMD2;
	}

	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		String name = getConfig().getAsString(DUPLICATED_ALG_NAME_FIELD);
		if (name != null && !name.isEmpty())
			return name;
		else
			return "u09.03.smd2_cosine_adjusted";
	}


	@Override
	public DataConfig createDefaultConfig() {
		DataConfig config = super.createDefaultConfig();
		config.put(MEASURE, Measure.SMD2);
		config.put(OTHER_MEASURE, Measure.COD);
		
		return config;
	}


}
