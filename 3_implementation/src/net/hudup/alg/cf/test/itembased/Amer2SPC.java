package net.hudup.alg.cf.test.itembased;

import net.hudup.alg.cf.NeighborCFTwosCombinedItemBased;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.TestAlg;
import net.hudup.core.data.DataConfig;

public class Amer2SPC extends NeighborCFTwosCombinedItemBased implements TestAlg {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Default constructor.
	 */
	public Amer2SPC() {
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
			return "i09.08.amer2_pearson_sigmoid";
	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		Amer2SPC cf = new Amer2SPC();
		cf.getConfig().putAll((DataConfig)this.getConfig().clone());
		
		return cf;
	}


	@Override
	public DataConfig createDefaultConfig() {
		// TODO Auto-generated method stub
		DataConfig config = super.createDefaultConfig();
		config.put(MEASURE, AMER2);
		config.put(OTHER_MEASURE, SPC);
		
		return config;
	}


}
