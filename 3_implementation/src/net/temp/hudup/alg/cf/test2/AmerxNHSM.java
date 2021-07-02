/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.temp.hudup.alg.cf.test2;

import net.hudup.alg.cf.nb.Measure;
import net.hudup.core.data.DataConfig;
import net.hudup.core.logistic.ForTest;

/**
 * Amer + NHSM measure.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class AmerxNHSM extends net.hudup.alg.cf.nb.beans.AmerCombined implements ForTest {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Default constructor.
	 */
	public AmerxNHSM() {

	}


	@Override
	public String getName() {
		String name = getConfig().getAsString(DUPLICATED_ALG_NAME_FIELD);
		if (name != null && !name.isEmpty())
			return name;
		else
			return "a22.01.05.02.amerxnhsm";
	}


	@Override
	public DataConfig createDefaultConfig() {
		DataConfig config = super.createDefaultConfig();
		config.put(KNN, 100);
		config.put(FAST_RECOMMEND, true);
		config.put(OTHER_MEASURE, Measure.PSS);
		config.put(PSS_TYPE, PSS_TYPE_NHSM);
		
		return config;
	}


}
