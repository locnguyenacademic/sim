/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.temp.hudup.alg.cf.test2;

import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Profile;
import net.hudup.core.data.RatingVector;
import net.hudup.core.logistic.ForTest;

/**
 * NHSM measure for test.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class NHSM extends net.hudup.alg.cf.nb.beans.NHSM implements ForTest {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor
	 */
	public NHSM() {

	}

	
	@Override
	public String getName() {
		String name = getConfig().getAsString(DUPLICATED_ALG_NAME_FIELD);
		if (name != null && !name.isEmpty())
			return name;
		else
			return "a05.02.nhsm";
	}


	@Override
	protected double nhsm(RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		double urp = urp(vRating1, vRating2, profile1, profile2);
		return pss(vRating1, vRating2, profile1, profile2) * urp;
	}

	
	@Override
	public DataConfig createDefaultConfig() {
		DataConfig config = super.createDefaultConfig();
		config.put(KNN, 100);
		config.put(FAST_RECOMMEND, true);
		
		return config;
	}


}
