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

import net.hudup.core.Constants;
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
		mSet.add(Measure.RES);

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
		config.removeReadOnly(BCF_MEDIAN_MODE_FIELD);
		config.removeReadOnly(MU_ALPHA_FIELD);
		config.removeReadOnly(SMTP_LAMBDA_FIELD);
		config.removeReadOnly(SMTP_GENERAL_VAR_FIELD);
		config.removeReadOnly(TA_NORMALIZED_FIELD);
		config.removeReadOnly(RATINGJ_THRESHOLD_FIELD);
		config.removeReadOnly(INDEXEDJ_INTERVALS_FIELD);
		config.removeReadOnly(ESIM_TYPE_FIELD);
		if (measure.equals(Measure.RES)) {
			config.addReadOnly(VALUE_BINS_FIELD);
			config.addReadOnly(COSINE_NORMALIZED_FIELD);
			config.addReadOnly(MSD_FRACTION_FIELD);
			config.addReadOnly(BCF_MEDIAN_MODE_FIELD);
			config.addReadOnly(MU_ALPHA_FIELD);
			config.addReadOnly(SMTP_LAMBDA_FIELD);
			config.addReadOnly(SMTP_GENERAL_VAR_FIELD);
			config.addReadOnly(TA_NORMALIZED_FIELD);
			config.addReadOnly(RATINGJ_THRESHOLD_FIELD);
			config.addReadOnly(INDEXEDJ_INTERVALS_FIELD);
			config.addReadOnly(ESIM_TYPE_FIELD);
		}
		else
			super.updateConfig(measure);
	}


	@Override
	protected double sim0(String measure, RatingVector vRating1, RatingVector vRating2, Profile profile1, Profile profile2, Object... params) {
		if (measure.equals(Measure.RES))
			return res(vRating1, vRating2, profile1, profile2);
		else
			return super.sim0(measure, vRating1, vRating2, profile1, profile2, params);
	}


	/**
	 * Calculating the RES measure between two pairs.
	 * Zhenhua Tan and Liangliang He developed the ESim measure. Loc Nguyen implements it.
	 * @param vRating1 first rating vector.
	 * @param vRating2 second rating vector.
	 * @param profile1 first profile.
	 * @param profile2 second profile.
	 * @return RES measure between both two rating vectors and profiles.
	 * @author Zhenhua Tan, Liangliang He
	 */
	protected double res(RatingVector vRating1, RatingVector vRating2, Profile profile1, Profile profile2) {
		Set<Integer> set1 = vRating1.fieldIds(true);
		Set<Integer> set2 = vRating2.fieldIds(true);
		Set<Integer> common = Util.newSet();
		common.addAll(set1);
		common.retainAll(set2);
		double n = common.size();
		double N = set1.size() + set2.size() - n;
		double jacc = n/N;
		
		double range = Math.PI / (getMaxRating()-getMinRating());
		double mean = getRatingMean();
		double mean1 = vRating1.mean() - mean;
		double mean2 = vRating2.mean() - mean;
		double res = 0;
		for (int id : common) {
			double v1 = vRating1.get(id).value;
			double v2 = vRating2.get(id).value;
			double phibase1 = v1 - ratingMedian;
			double phibase2 = v2 - ratingMedian;
			
			double phi1 = Constants.UNUSED;
			if (phibase1 * mean1 >= 0)
				phi1 = range * (1/(1+Math.abs(mean1))) * phibase1;
			else
				phi1 = range * (1+Math.abs(mean1)/ratingMedian) * phibase1;
			
			double phi2 = Constants.UNUSED;
			if (phibase2 * mean2 >= 0)
				phi2 = range * (1/(1+Math.abs(mean2))) * phibase2;
			else
				phi2 = range * (1+Math.abs(mean2)/ratingMedian) * phibase2;
			
			double C = Math.sqrt(0.5 + 0.5*Math.cos(phi1-phi2));
			
			double d1 = Math.exp(-Math.abs(v1-v2));
			double ri = getColumnRating(id).mean();
			double d2 = Math.exp(0.5 * (Math.abs(v1-ri)+Math.abs(v2-ri)));
			double D = d1*d2;
			
			res += C*D*jacc;
		}
		
		return Math.atan(res) / (0.5*Math.PI);
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
