package net.hudup.alg.cf.nb.test.c;

import net.hudup.alg.cf.nb.Measure;
import net.hudup.alg.cf.nb.NeighborCFExtUserBased;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Profile;
import net.hudup.core.data.RatingVector;
import net.hudup.core.logistic.ForTest;

public class CU14QuasiTFJ extends NeighborCFExtUserBased implements ForTest {


	private static final long serialVersionUID = 1L;

	
	public CU14QuasiTFJ() {

	}

	
	@Override
	protected double sim0(String measure, RatingVector vRating1, RatingVector vRating2, Profile profile1,
			Profile profile2, Object... params) {
		return quasiTfJaccard(vRating1, vRating2, profile1, profile2);
	}

	
	@Override
	public String getName() {
		return "CU14.QuasiTFJ";
	}

	
	@Override
	public DataConfig createDefaultConfig() {
		DataConfig config = super.createDefaultConfig();
		config.put(MEASURE, Measure.QUASI_TFIDF);
		return config;
	}
	
	
}
