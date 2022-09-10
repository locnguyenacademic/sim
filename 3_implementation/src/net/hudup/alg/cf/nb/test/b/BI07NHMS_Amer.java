package net.hudup.alg.cf.nb.test.b;

import net.hudup.alg.cf.nb.Measure;
import net.hudup.alg.cf.nb.NeighborCFExtItemBased;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Profile;
import net.hudup.core.data.RatingVector;
import net.hudup.core.logistic.ForTest;

public class BI07NHMS_Amer extends NeighborCFExtItemBased implements ForTest {

	
	private static final long serialVersionUID = 1L;

	
	public BI07NHMS_Amer() {

	}

	
	@Override
	protected double sim0(String measure, RatingVector vRating1, RatingVector vRating2, Profile profile1,
			Profile profile2, Object... params) {
		return nhsmAmer(vRating1, vRating2, profile1, profile2);
	}
	
	
	@Override
	public String getName() {
		return "BI07.NHMS.Amer";
	}

	
	@Override
	public DataConfig createDefaultConfig() {
		DataConfig config = super.createDefaultConfig();
		config.put(MEASURE, Measure.PSS);
		config.put(PSS_TYPE, PSS_TYPE_NHSM_Amer);
		return config;
	}
	
	
}