package net.hudup.alg.cf.test.userbased;

import net.hudup.alg.cf.Measure;
import net.hudup.alg.cf.NeighborCFExtUserBased;
import net.hudup.core.logistic.ForTest;

/**
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class SMD extends NeighborCFExtUserBased implements ForTest {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public SMD() {
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public String getDefaultMeasure() {
		// TODO Auto-generated method stub
		return Measure.SMD;
	}

	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		String name = getConfig().getAsString(DUPLICATED_ALG_NAME_FIELD);
		if (name != null && !name.isEmpty())
			return name;
		else
			return "u08.01.smd";
	}


}
