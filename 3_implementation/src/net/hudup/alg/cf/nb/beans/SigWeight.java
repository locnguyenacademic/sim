/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.alg.cf.nb.beans;

import java.util.Set;

import net.hudup.alg.cf.nb.NeighborCFExtUserBased;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Profile;
import net.hudup.core.data.RatingVector;

/**
 * Significant weight measure.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class SigWeight extends NeighborCFExtUserBased {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Name of significant count.
	 */
	protected static final String SIGCOUNT_FIELD = "sigweight_count";

	
	/**
	 * Name of significant count.
	 */
	protected static final int SIGCOUNT_DEFAULT = 50;

	
	/**
	 * Default constructor.
	 */
	public SigWeight() {

	}

	
	@Override
	protected double sim0(String measure, RatingVector vRating1, RatingVector vRating2, Profile profile1, Profile profile2, Object... params) {
		double sim = super.sim0(measure, vRating1, vRating2, profile1, profile2, params);
		int sigCount = config.getAsInt(SIGCOUNT_FIELD);
		Set<Integer> common = commonFieldIds(vRating1, vRating2);
		return sim * Math.min(common.size(), sigCount)/sigCount;
	}

	
	@Override
	public String getName() {
		String name = getConfig().getAsString(DUPLICATED_ALG_NAME_FIELD);
		if (name != null && !name.isEmpty())
			return name;
		else
			return "neighborcf_sigweight";
	}


	@Override
	public DataConfig createDefaultConfig() {
		DataConfig config = super.createDefaultConfig();
		config.put(SIGCOUNT_FIELD, SIGCOUNT_DEFAULT);
		return config;
	}


}
