package net.hudup.alg.cf.test.itembased;

import net.hudup.alg.cf.NeighborCFExtItemBased;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.TestAlg;
import net.hudup.core.data.DataConfig;

public class PearsonJ extends NeighborCFExtItemBased implements TestAlg {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public PearsonJ() {
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public String getDefaultMeasure() {
		// TODO Auto-generated method stub
		return PEARSONJ;
	}

	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		String name = getConfig().getAsString(DUPLICATED_ALG_NAME_FIELD);
		if (name != null && !name.isEmpty())
			return name;
		else
			return "i02.05.pearsonj";
	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		PearsonJ cf = new PearsonJ();
		cf.getConfig().putAll((DataConfig)this.getConfig().clone());
		
		return cf;
	}

	
}
