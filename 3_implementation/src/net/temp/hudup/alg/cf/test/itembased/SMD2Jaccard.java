package net.temp.hudup.alg.cf.test.itembased;

import net.hudup.alg.cf.nb.Measure;
import net.hudup.alg.cf.nb.NeighborCFTwosCombinedItemBased;
import net.hudup.core.data.DataConfig;
import net.hudup.core.logistic.ForTest;

public class SMD2Jaccard extends NeighborCFTwosCombinedItemBased implements ForTest {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Default constructor.
	 */
	public SMD2Jaccard() {
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
			return "i09.09.smd2_jaccard";
	}


	@Override
	public DataConfig createDefaultConfig() {
		// TODO Auto-generated method stub
		DataConfig config = super.createDefaultConfig();
		config.put(MEASURE, Measure.SMD2);
		config.put(OTHER_MEASURE, Measure.JACCARD);
		
		return config;
	}


}
