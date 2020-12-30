package net.hudup.alg.cf.test.itembased;

import net.hudup.alg.cf.Measure;
import net.hudup.alg.cf.NeighborCFTwosCombinedItemBased;
import net.hudup.core.data.DataConfig;
import net.hudup.core.logistic.ForTest;

public class SMDBCFJ extends NeighborCFTwosCombinedItemBased implements ForTest {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Default constructor.
	 */
	public SMDBCFJ() {
		// TODO Auto-generated constructor stub
	}


	@Override
	public String getDefaultMeasure() {
		// TODO Auto-generated method stub
		return Measure.SMD;
	}

	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		String name = getConfig().getAsString(DUPLICATED_ALG_NAME_FIELD);
		if (name != null && !name.isEmpty())
			return name;
		else
			return "i08.12.smd_bcfj";
	}


	@Override
	public DataConfig createDefaultConfig() {
		DataConfig config = super.createDefaultConfig();
		config.put(MEASURE, Measure.SMD);
		config.put(OTHER_MEASURE, Measure.BCFJ);
		
		return config;
	}


}
