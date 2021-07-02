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
 * <br>
 * Zhenhua Tan and Liangliang He contributed RES measure.<br>
 * <br>
 * Jesús Bobadilla, Fernando Ortega, and Antonio Hernando contributed singularity measure (SM).<br>
 * <br>
 * Manochandar and Punniyamoorthy contributed MPIP measure.<br>
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
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
		
		config.removeReadOnly(VALUE_BINS_FIELD);
		config.removeReadOnly(COSINE_NORMALIZED_FIELD);
		config.removeReadOnly(MSD_FRACTION_FIELD);
		config.removeReadOnly(ENTROPY_SUPPORT_FIELD);
		config.removeReadOnly(BCF_MEDIAN_MODE_FIELD);
		config.removeReadOnly(MU_ALPHA_FIELD);
		config.removeReadOnly(SMTP_LAMBDA_FIELD);
		config.removeReadOnly(SMTP_GENERAL_VAR_FIELD);
		config.removeReadOnly(TA_NORMALIZED_FIELD);
		config.removeReadOnly(RATINGJ_THRESHOLD_FIELD);
		config.removeReadOnly(INDEXEDJ_INTERVALS_FIELD);
		config.removeReadOnly(ESIM_TYPE);
		config.removeReadOnly(JACCARD_TYPE);
		config.removeReadOnly(COSINE_TYPE);
		config.removeReadOnly(PEARSON_TYPE);
		config.removeReadOnly(MSD_TYPE);
		config.removeReadOnly(TRIANGLE_TYPE);
		config.removeReadOnly(PSS_TYPE);
		config.removeReadOnly(BCF_TYPE);
		config.removeReadOnly(PIP_TYPE);
		config.removeReadOnly(MMD_TYPE);
		config.removeReadOnly(TA_TYPE);
		config.removeReadOnly(SMD2_TYPE);
		config.removeReadOnly(QUASI_TFIDF_TYPE);
		config.removeReadOnly(IPWR_ALPHA_FIELD);
		config.removeReadOnly(IPWR_BETA_FIELD);
		
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
