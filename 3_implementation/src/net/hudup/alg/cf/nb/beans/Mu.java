/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.alg.cf.nb.beans;

import net.hudup.alg.cf.nb.NeighborCFExtUserBased;
import net.hudup.core.data.Profile;
import net.hudup.core.data.RatingVector;

/**
 * Mu measure.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class Mu extends NeighborCFExtUserBased {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Default constructor.
	 */
	public Mu() {

	}


	@Override
	protected void updateConfig(String measure) {
		super.updateConfig(measure);
		
		config.remove(MEASURE);
		config.remove(CALC_STATISTICS);
		config.remove(VALUE_BINS_FIELD);
		config.remove(COSINE_NORMALIZED_FIELD);
		config.remove(MSD_FRACTION_FIELD);
		config.remove(BCF_MEDIAN_MODE_FIELD);
		config.remove(SMTP_LAMBDA_FIELD);
		config.remove(SMTP_GENERAL_VAR_FIELD);
		config.remove(TA_NORMALIZED_FIELD);
	}


	@Override
	protected double sim0(String measure, RatingVector vRating1, RatingVector vRating2, Profile profile1,
			Profile profile2, Object... params) {
		return mu(vRating1, vRating2, profile1, profile2);
	}

	
	@Override
	public String getName() {
		String name = getConfig().getAsString(DUPLICATED_ALG_NAME_FIELD);
		if (name != null && !name.isEmpty())
			return name;
		else
			return "neighborcf_mu";
	}


}
