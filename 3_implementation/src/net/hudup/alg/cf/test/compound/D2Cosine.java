package net.hudup.alg.cf.test.compound;

import net.hudup.alg.cf.nb.Measure;
import net.hudup.core.alg.cf.nb.NeighborCF2d;
import net.hudup.core.logistic.ForTest;

public class D2Cosine extends NeighborCF2d implements ForTest {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public D2Cosine() {

	}

	
	@Override
	public String getDefaultMeasure() {
		return Measure.COSINE;
	}

	
	@Override
	public String getName() {
		String name = getConfig().getAsString(DUPLICATED_ALG_NAME_FIELD);
		if (name != null && !name.isEmpty())
			return name;
		else
			return "c01.01.2d.cosine";
	}


}
