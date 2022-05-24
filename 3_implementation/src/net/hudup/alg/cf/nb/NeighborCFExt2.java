/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.alg.cf.nb;

import java.awt.Component;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import net.hudup.core.Util;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Profile;
import net.hudup.core.data.RatingVector;

/**
 * This class sets up another advanced version of nearest neighbors collaborative filtering algorithm with more similarity measures.
 * <br>
 * There are many authors who contributed measure to this class.<br>
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
@Deprecated
public abstract class NeighborCFExt2 extends NeighborCFExt {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Default constructor.
	 */
	public NeighborCFExt2() {

	}
	
	
	@Override
	public List<String> getAllMeasures() {
		Set<String> mSet = Util.newSet();
		mSet.addAll(getMainMeasures());
		
		List<String> measures = Util.newList();
		measures.addAll(mSet);
		Collections.sort(measures);
		return measures;
	}
	
	
	@Override
	public List<String> getMainMeasures() {
		List<String> measures = super.getMainMeasures();
		Set<String> mSet = Util.newSet();
		mSet.addAll(measures);

		measures.clear();
		measures.addAll(mSet);
		Collections.sort(measures);
		return measures;
	}


	@Override
	protected void updateConfig(String measure) {
		if (measure == null) return;
		
		config.addReadOnly(VALUE_BINS_COUNT_FIELD);
		config.addReadOnly(COSINE_NORMALIZED_FIELD);
		config.addReadOnly(COSINE_WEIGHTED_FIELD);
		config.addReadOnly(PEARSON_WEIGHTED_FIELD);
		config.addReadOnly(MSD_FRACTION_FIELD);
		config.addReadOnly(ENTROPY_SUPPORT_FIELD);
		config.addReadOnly(BCF_MEDIAN_MODE_FIELD);
		config.addReadOnly(MU_ALPHA_FIELD);
		config.addReadOnly(SMTP_LAMBDA_FIELD);
		config.addReadOnly(SMTP_GENERAL_VAR_FIELD);
		config.addReadOnly(TA_NORMALIZED_FIELD);
		config.addReadOnly(RATINGJ_THRESHOLD_FIELD);
		config.addReadOnly(INDEXEDJ_INTERVALS_FIELD);
		config.addReadOnly(ESIM_TYPE);
		config.addReadOnly(JACCARD_TYPE);
		config.addReadOnly(COSINE_TYPE);
		config.addReadOnly(PEARSON_TYPE);
		config.addReadOnly(MSD_TYPE);
		config.addReadOnly(TRIANGLE_TYPE);
		config.addReadOnly(PSS_TYPE);
		config.addReadOnly(BCF_TYPE);
		config.addReadOnly(PIP_TYPE);
		config.addReadOnly(MMD_TYPE);
		config.addReadOnly(TA_TYPE);
		config.addReadOnly(HSMD_TYPE);
		config.addReadOnly(QUASI_TFIDF_TYPE);
		config.addReadOnly(IPWR_ALPHA_FIELD);
		config.addReadOnly(IPWR_BETA_FIELD);
		config.addReadOnly(KL_TYPE);
		
		super.updateConfig(measure);
	}


	@Override
	protected double sim0(String measure, RatingVector vRating1, RatingVector vRating2, Profile profile1, Profile profile2, Object... params) {
		return super.sim0(measure, vRating1, vRating2, profile1, profile2, params);
	}


	@Override
	public DataConfig createDefaultConfig() {
		DataConfig tempConfig = super.createDefaultConfig();

		DataConfig config = new DataConfig() {

			/**
			 * Serial version UID for serializable class.
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Serializable userEdit(Component comp, String key, Serializable defaultValue) {
				return tempConfig.userEdit(comp, key, defaultValue);
			}
			
		};

		config.putAll(tempConfig);
		
		return config;
	}


}
