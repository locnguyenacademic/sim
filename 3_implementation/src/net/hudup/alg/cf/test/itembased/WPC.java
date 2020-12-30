package net.hudup.alg.cf.test.itembased;

import net.hudup.alg.cf.Measure;
import net.hudup.alg.cf.NeighborCFExtItemBased;
import net.hudup.core.logistic.ForTest;

public class WPC extends NeighborCFExtItemBased implements ForTest {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public WPC() {
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public String getDefaultMeasure() {
		// TODO Auto-generated method stub
		return Measure.WPC;
	}

	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		String name = getConfig().getAsString(DUPLICATED_ALG_NAME_FIELD);
		if (name != null && !name.isEmpty())
			return name;
		else
			return "i02.03.pearson_weighted";
	}


}
