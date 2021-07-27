package net.temp.hudup.alg.cf.test.userbased.test;

import net.hudup.alg.cf.nb.Measure;
import net.hudup.alg.cf.nb.NeighborCFTwosCombinedUserBased;
import net.hudup.core.data.DataConfig;
import net.hudup.core.logistic.ForTest;

@Deprecated
public class SMD2Smtp extends NeighborCFTwosCombinedUserBased implements ForTest {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public SMD2Smtp() {
		// TODO Auto-generated constructor stub
	}


	@Override
	public String getDefaultMeasure() {
		// TODO Auto-generated method stub
		return Measure.HSMD;
	}

	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		String name = getConfig().getAsString(DUPLICATED_ALG_NAME_FIELD);
		if (name != null && !name.isEmpty())
			return name;
		else
			return "u09.04.smd2_smtp";
	}


	@Override
	public DataConfig createDefaultConfig() {
		DataConfig config = super.createDefaultConfig();
		config.put(MEASURE, Measure.HSMD);
		config.put(OTHER_MEASURE, Measure.SMTP);
		
		return config;
	}


}
