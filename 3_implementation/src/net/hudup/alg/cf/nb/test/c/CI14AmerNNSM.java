package net.hudup.alg.cf.nb.test.c;

import net.hudup.alg.cf.nb.Measure;
import net.hudup.alg.cf.nb.NeighborCFExtItemBased;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Profile;
import net.hudup.core.data.RatingVector;
import net.hudup.core.logistic.ForTest;

public class CI14AmerNNSM extends NeighborCFExtItemBased implements ForTest {


	private static final long serialVersionUID = 1L;

	
	public CI14AmerNNSM() {

	}

	
	@Override
	protected double sim0(String measure, RatingVector vRating1, RatingVector vRating2, Profile profile1,
			Profile profile2, Object... params) {
		return amer(vRating1, vRating2, profile1, profile2) * nnsm(vRating1, vRating2, profile1, profile2);
	}

	
	@Override
	public String getName() {
		return "CI14.Amer.NNSM";
	}

	
	@Override
	public DataConfig createDefaultConfig() {
		DataConfig config = super.createDefaultConfig();
		config.put(MEASURE, Measure.AMER);
		return config;
	}
	
	
}
