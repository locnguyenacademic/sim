package net.hudup.alg.cf.test.itembased.temp;

import net.hudup.alg.cf.NeighborCFTwosCombinedItemBased;
import net.hudup.core.data.DataConfig;
import net.hudup.core.logistic.ForTest;

@Deprecated
public class AmerSmtp extends NeighborCFTwosCombinedItemBased implements ForTest {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Default constructor.
	 */
	public AmerSmtp() {

	}


	@Override
	public String getDefaultMeasure() {
		return AMER;
	}

	
	@Override
	public String getName() {
		String name = getConfig().getAsString(DUPLICATED_ALG_NAME_FIELD);
		if (name != null && !name.isEmpty())
			return name;
		else
			return "i08.04.amer_smtp";
	}


	@Override
	public DataConfig createDefaultConfig() {
		DataConfig config = super.createDefaultConfig();
		config.put(MEASURE, AMER);
		config.put(OTHER_MEASURE, SMTP);
		
		return config;
	}


}
