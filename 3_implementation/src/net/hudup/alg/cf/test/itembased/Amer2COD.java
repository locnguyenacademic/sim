package net.hudup.alg.cf.test.itembased;

import net.hudup.alg.cf.NeighborCFTwosCombinedItemBased;
import net.hudup.core.data.DataConfig;
import net.hudup.core.logistic.ForTest;

public class Amer2COD extends NeighborCFTwosCombinedItemBased implements ForTest {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Default constructor.
	 */
	public Amer2COD() {
		// TODO Auto-generated constructor stub
	}


	@Override
	public String getDefaultMeasure() {
		// TODO Auto-generated method stub
		return AMER2;
	}

	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		String name = getConfig().getAsString(DUPLICATED_ALG_NAME_FIELD);
		if (name != null && !name.isEmpty())
			return name;
		else
			return "i09.03.amer2_cosine_adjusted";
	}


	@Override
	public DataConfig createDefaultConfig() {
		DataConfig config = super.createDefaultConfig();
		config.put(MEASURE, AMER2);
		config.put(OTHER_MEASURE, COD);
		
		return config;
	}


}
