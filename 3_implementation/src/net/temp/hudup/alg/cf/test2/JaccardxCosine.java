/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.temp.hudup.alg.cf.test2;

import java.util.Arrays;
import java.util.List;

import net.hudup.alg.cf.nb.NeighborCFExtUserBased;
import net.hudup.core.data.Profile;
import net.hudup.core.data.RatingVector;
import net.hudup.core.logistic.ForTest;

/**
 * Jaccard + Cosine measure.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class JaccardxCosine extends NeighborCFExtUserBased implements ForTest {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Default constructor.
	 */
	public JaccardxCosine() {

	}


	@Override
	public List<String> getAllMeasures() {
		return getMainMeasures();
	}


	@Override
	public List<String> getMainMeasures() {
		return Arrays.asList(getDefaultMeasure());
	}


	@Override
	protected String getDefaultMeasure() {
		return "a03.01.01.01.jaccardxcosine";
	}


	@Override
	public String getMeasure() {
		return getDefaultMeasure();
	}


	@Override
	protected void updateConfig(String measure) {
		super.updateConfig(measure);
		
		config.remove(MEASURE);
		config.remove(VALUE_BINS_FIELD);
		config.remove(MSD_FRACTION_FIELD);
		config.remove(BCF_MEDIAN_MODE_FIELD);
		config.remove(MU_ALPHA_FIELD);
		config.remove(SMTP_LAMBDA_FIELD);
		config.remove(SMTP_GENERAL_VAR_FIELD);
		config.remove(TA_NORMALIZED_FIELD);
		config.remove(RATINGJ_THRESHOLD_FIELD);
		config.remove(INDEXEDJ_INTERVALS_FIELD);
	}


	@Override
	protected double sim0(String measure, RatingVector vRating1, RatingVector vRating2, Profile profile1,
			Profile profile2, Object... params) {
		return jaccardNormal(vRating1, vRating2, profile1, profile2) * cosineNormal(vRating1, vRating2, profile1, profile2);
	}

	
	@Override
	public String getName() {
		String name = getConfig().getAsString(DUPLICATED_ALG_NAME_FIELD);
		if (name != null && !name.isEmpty())
			return name;
		else
			return "a03.01.01.01.jaccardxcosine";
	}


}
