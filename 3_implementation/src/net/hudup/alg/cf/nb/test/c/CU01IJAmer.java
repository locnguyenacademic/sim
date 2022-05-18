package net.hudup.alg.cf.nb.test.c;

import net.hudup.alg.cf.nb.Measure;
import net.hudup.alg.cf.nb.NeighborCFExtUserBased;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Profile;
import net.hudup.core.data.RatingVector;
import net.hudup.core.logistic.ForTest;

public class CU01IJAmer extends NeighborCFExtUserBased implements ForTest {

	
	private static final long serialVersionUID = 1L;

	
	public CU01IJAmer() {

	}

	
	@Override
	protected double sim0(String measure, RatingVector vRating1, RatingVector vRating2, Profile profile1,
			Profile profile2, Object... params) {
		return jaccardImproved(vRating1, vRating2, profile1, profile2) * amer(vRating1, vRating2, profile1, profile2);
	}

	
	@Override
	public String getName() {
		return "CU01.IJ.Amer";
	}

	
	@Override
	public DataConfig createDefaultConfig() {
		DataConfig config = super.createDefaultConfig();
		config.put(MEASURE, Measure.JACCARD);
		config.put(JACCARD_TYPE, JACCARD_TYPE_IJ);
		return config;
	}
	
	
}
