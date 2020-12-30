package net.hudup.alg.cf.test.userbased;

import net.hudup.alg.cf.Measure;
import net.hudup.alg.cf.NeighborCFExtUserBased;
import net.hudup.core.logistic.ForTest;

public class Jaccard extends NeighborCFExtUserBased implements ForTest {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public Jaccard() {
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public String getDefaultMeasure() {
		return Measure.JACCARD;
	}

	
	@Override
	public String getName() {
		String name = getConfig().getAsString(DUPLICATED_ALG_NAME_FIELD);
		if (name != null && !name.isEmpty())
			return name;
		else
			return "u03.01.jaccard";
	}


}
