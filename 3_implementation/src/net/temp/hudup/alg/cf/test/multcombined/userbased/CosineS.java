package net.temp.hudup.alg.cf.test.multcombined.userbased;

import net.hudup.alg.cf.nb.Measure;
import net.hudup.core.data.DataConfig;
import net.hudup.core.logistic.ForTest;
import net.temp.hudup.alg.cf.test.multcombined.NeighborCFTwosCombinedMultUserBased;

public class CosineS extends NeighborCFTwosCombinedMultUserBased implements ForTest {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Default constructor.
	 */
	public CosineS() {
		// TODO Auto-generated constructor stub
	}


	@Override
	public String getDefaultMeasure() {
		// TODO Auto-generated method stub
		return Measure.COSINE;
	}

	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		String name = getConfig().getAsString(DUPLICATED_ALG_NAME_FIELD);
		if (name != null && !name.isEmpty())
			return name;
		else
			return "mcu01.02.cosine_smd";
	}


	@Override
	public DataConfig createDefaultConfig() {
		DataConfig config = super.createDefaultConfig();
		config.put(MEASURE, Measure.COSINE);
		config.put(OTHER_MEASURE, Measure.SMD);
		
		return config;
	}


}
