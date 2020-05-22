package net.hudup.alg.cf.test.compound;

import net.hudup.alg.cf.NeighborCF3d;
import net.hudup.core.logistic.ForTest;

public class D3Cosine extends NeighborCF3d implements ForTest {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public D3Cosine() {

	}

	
	@Override
	public String getDefaultMeasure() {
		return COSINE;
	}

	
	@Override
	public String getName() {
		String name = getConfig().getAsString(DUPLICATED_ALG_NAME_FIELD);
		if (name != null && !name.isEmpty())
			return name;
		else
			return "c02.02.3d.cosine";
	}


}
