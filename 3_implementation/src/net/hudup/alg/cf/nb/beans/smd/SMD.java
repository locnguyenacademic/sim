/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.alg.cf.nb.beans.smd;

import java.util.Arrays;
import java.util.List;

import net.hudup.alg.cf.nb.Measure;
import net.hudup.alg.cf.nb.NeighborCFExtUserBased;
import net.hudup.core.data.Profile;
import net.hudup.core.data.RatingVector;

/**
 * SMD measure.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class SMD extends NeighborCFExtUserBased {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Default constructor.
	 */
	public SMD() {

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
		return Measure.SMD;
	}


	@Override
	public String getMeasure() {
		return getDefaultMeasure();
	}


	@Override
	protected void updateConfig(String measure) {
		super.updateConfig(measure);
		
		config.remove(MEASURE);
		config.remove(CALC_STATISTICS_FIELD);
		config.remove(VALUE_BINS_FIELD);
		config.remove(COSINE_NORMALIZED_FIELD);
		config.remove(MSD_FRACTION_FIELD);
		config.remove(BCF_MEDIAN_MODE_FIELD);
		config.remove(MU_ALPHA_FIELD);
		config.remove(SMTP_LAMBDA_FIELD);
		config.remove(SMTP_GENERAL_VAR_FIELD);
		config.remove(TA_NORMALIZED_FIELD);
	}


	@Override
	protected double sim0(String measure, RatingVector vRating1, RatingVector vRating2, Profile profile1,
			Profile profile2, Object... params) {
		return smd(vRating1, vRating2, profile1, profile2);
	}

	
	@Override
	public String getName() {
		String name = getConfig().getAsString(DUPLICATED_ALG_NAME_FIELD);
		if (name != null && !name.isEmpty())
			return name;
		else
			return "neighborcf_smd";
	}


}