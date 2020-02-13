package net.hudup.alg.cf.test.userbased;

import net.hudup.alg.cf.NeighborCFTwosCombinedUserBased;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.TestAlg;
import net.hudup.core.data.DataConfig;

public class Amer2Jaccard extends NeighborCFTwosCombinedUserBased implements TestAlg {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Default constructor.
	 */
	public Amer2Jaccard() {
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
			return "u09.09.amer2_jaccard";
	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		Amer2Jaccard cf = new Amer2Jaccard();
		cf.getConfig().putAll((DataConfig)this.getConfig().clone());
		
		return cf;
	}


	@Override
	public DataConfig createDefaultConfig() {
		// TODO Auto-generated method stub
		DataConfig config = super.createDefaultConfig();
		config.put(MEASURE, AMER2);
		config.put(OTHER_MEASURE, JACCARD);
		
		return config;
	}


}
