package net.hudup.alg.cf.test.userbased;

import net.hudup.alg.cf.WaspWaistCFUserBased;
import net.hudup.core.logistic.ForTest;

public class WW extends WaspWaistCFUserBased implements ForTest {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public WW() {

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
			return "u11.01.ww";
	}


}
