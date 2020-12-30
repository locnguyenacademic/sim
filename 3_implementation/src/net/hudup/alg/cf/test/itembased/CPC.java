package net.hudup.alg.cf.test.itembased;

import net.hudup.alg.cf.Measure;
import net.hudup.alg.cf.NeighborCFExtItemBased;
import net.hudup.core.logistic.ForTest;

public class CPC extends NeighborCFExtItemBased implements ForTest {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public CPC() {

	}

	
	@Override
	public String getDefaultMeasure() {
		return Measure.CPC;
	}

	
	@Override
	public String getName() {
		String name = getConfig().getAsString(DUPLICATED_ALG_NAME_FIELD);
		if (name != null && !name.isEmpty())
			return name;
		else
			return "i02.02.pearson_constrained";
	}


}
