package net.hudup.alg.cf.test.userbased;

import net.hudup.alg.cf.Measure;
import net.hudup.alg.cf.NeighborCFExtUserBased;
import net.hudup.core.data.DataConfig;
import net.hudup.core.logistic.ForTest;

public class TANJ extends NeighborCFExtUserBased implements ForTest {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public TANJ() {
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public String getDefaultMeasure() {
		// TODO Auto-generated method stub
		return Measure.TAJ;
	}

	
	@Override
	public String getName() {
		String name = getConfig().getAsString(DUPLICATED_ALG_NAME_FIELD);
		if (name != null && !name.isEmpty())
			return name;
		else
			return "u10.04.tanj";
	}


	@Override
	public DataConfig createDefaultConfig() {
		DataConfig config = super.createDefaultConfig();
		config.put(TA_NORMALIZED_FIELD, true);
		return config;
	}


}
