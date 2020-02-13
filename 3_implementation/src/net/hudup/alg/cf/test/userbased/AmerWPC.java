package net.hudup.alg.cf.test.userbased;

import net.hudup.alg.cf.NeighborCFTwosCombinedUserBased;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.TestAlg;
import net.hudup.core.data.DataConfig;

public class AmerWPC extends NeighborCFTwosCombinedUserBased implements TestAlg {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Default constructor.
	 */
	public AmerWPC() {
		// TODO Auto-generated constructor stub
	}


	@Override
	public String getDefaultMeasure() {
		// TODO Auto-generated method stub
		return AMER;
	}

	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		String name = getConfig().getAsString(DUPLICATED_ALG_NAME_FIELD);
		if (name != null && !name.isEmpty())
			return name;
		else
			return "u08.07.amer_pearson_weighted";
	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		AmerWPC cf = new AmerWPC();
		cf.getConfig().putAll((DataConfig)this.getConfig().clone());
		
		return cf;
	}


	@Override
	public DataConfig createDefaultConfig() {
		// TODO Auto-generated method stub
		DataConfig config = super.createDefaultConfig();
		config.put(MEASURE, AMER);
		config.put(OTHER_MEASURE, WPC);
		
		return config;
	}


}
