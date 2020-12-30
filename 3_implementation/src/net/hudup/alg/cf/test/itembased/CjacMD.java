package net.hudup.alg.cf.test.itembased;

import net.hudup.alg.cf.Measure;
import net.hudup.alg.cf.NeighborCFExtItemBased;
import net.hudup.core.logistic.ForTest;

public class CjacMD extends NeighborCFExtItemBased implements ForTest {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public CjacMD() {

	}

	
	@Override
	public String getDefaultMeasure() {
		return Measure.CJACMD;
	}

	
	@Override
	public String getName() {
		String name = getConfig().getAsString(DUPLICATED_ALG_NAME_FIELD);
		if (name != null && !name.isEmpty())
			return name;
		else
			return "i16.02.cjacmd";
	}


}
