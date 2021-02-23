/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.temp.hudup.alg.cf.test3;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import net.hudup.alg.cf.nb.NeighborCFExtUserBased;
import net.hudup.core.Util;
import net.hudup.core.data.Profile;
import net.hudup.core.data.RatingVector;
import net.hudup.core.logistic.ForTest;

public class Amerb extends NeighborCFExtUserBased implements ForTest {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Default constructor.
	 */
	public Amerb() {

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
		return "amerb";
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
		Set<Integer> fieldIds1 = vRating1.fieldIds(true);
		Set<Integer> fieldIds2 = vRating2.fieldIds(true);
		Set<Integer> union = Util.newSet(fieldIds1.size());
		union.addAll(fieldIds1);
		union.addAll(fieldIds2);
		
		int Nab = 0, N = 0;
		for (int fieldId : union) {
			boolean rated1 = vRating1.isRated(fieldId);
			boolean rated2 = vRating2.isRated(fieldId);
			
			if (rated1 == rated2) {
				if (vRating1.get(fieldId).value == vRating2.get(fieldId).value)
					Nab++;
				else
					N += 2;
			}
			else
				N++;
		}
		
		return 2.0*Nab / (double)N;
	}

	
	@Override
	public String getName() {
		String name = getConfig().getAsString(DUPLICATED_ALG_NAME_FIELD);
		if (name != null && !name.isEmpty())
			return name;
		else
			return "amerb";
	}


}
