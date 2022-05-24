/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.alg.cf.nb.beans;

import java.util.Arrays;
import java.util.List;

import net.hudup.alg.cf.nb.Measure;
import net.hudup.alg.cf.nb.NeighborCFExtUserBased;
import net.hudup.core.data.Profile;
import net.hudup.core.data.RatingVector;

/**
 * ESim measure.
 * 
 * @author Ali Amer
 * @version 1.0
 *
 */
public class ESim extends NeighborCFExtUserBased {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Default constructor.
	 */
	public ESim() {

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
		return Measure.ESIM;
	}


	@Override
	public String getMeasure() {
		return getDefaultMeasure();
	}


	@Override
	protected void updateConfig(String measure) {
		super.updateConfig(measure);
		
		config.remove(MEASURE);
		config.remove(VALUE_BINS_COUNT_FIELD);
		config.remove(COSINE_NORMALIZED_FIELD);
		config.remove(COSINE_WEIGHTED_FIELD);
		config.remove(COSINE_RA_FIELD);
		config.remove(PEARSON_RA_FIELD);
		config.remove(PEARSON_WEIGHTED_FIELD);
		config.remove(MSD_FRACTION_FIELD);
		config.remove(ENTROPY_SUPPORT_FIELD);
		config.remove(BCF_MEDIAN_MODE_FIELD);
		config.remove(MU_ALPHA_FIELD);
		config.remove(SMTP_LAMBDA_FIELD);
		config.remove(SMTP_GENERAL_VAR_FIELD);
		config.remove(TA_NORMALIZED_FIELD);
		config.remove(RATINGJ_THRESHOLD_FIELD);
		config.remove(INDEXEDJ_INTERVALS_FIELD);
		config.remove(JACCARD_TYPE);
		config.remove(COSINE_TYPE);
		config.remove(PEARSON_TYPE);
		config.remove(MSD_TYPE);
		config.remove(TRIANGLE_TYPE);
		config.remove(PSS_TYPE);
		config.remove(BCF_TYPE);
		config.remove(PIP_TYPE);
		config.remove(MMD_TYPE);
		config.remove(TA_TYPE);
		config.remove(HSMD_TYPE);
		config.remove(QUASI_TFIDF_TYPE);
		config.remove(IPWR_ALPHA_FIELD);
		config.remove(IPWR_BETA_FIELD);
		config.remove(KL_TYPE);
	}


	@Override
	protected double sim0(String measure, RatingVector vRating1, RatingVector vRating2, Profile profile1,
			Profile profile2, Object... params) {
		return esim(vRating1, vRating2, profile1, profile2);
	}

	
	@Override
	public String getName() {
		String name = getConfig().getAsString(DUPLICATED_ALG_NAME_FIELD);
		if (name != null && !name.isEmpty())
			return name;
		else
			return "neighborcf_esim";
	}


}
