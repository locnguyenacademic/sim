package net.hudup.temp.test.a;

import net.hudup.alg.cf.nb.NeighborCFExtItemBased;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Profile;
import net.hudup.core.data.RatingVector;
import net.hudup.core.logistic.ForTest;

public class AI17RA extends NeighborCFExtItemBased implements ForTest {


	private static final long serialVersionUID = 1L;

	
	public AI17RA() {

	}

	
	@Override
	protected double sim0(String measure, RatingVector vRating1, RatingVector vRating2, Profile profile1,
			Profile profile2, Object... params) {
		return cosineNormal(vRating1, vRating2, profile1, profile2);
	}

	
	@Override
	public String getName() {
		return "AI17.RA";
	}

	
	@Override
	public DataConfig createDefaultConfig() {
		DataConfig config = super.createDefaultConfig();
		config.put(COSINE_RA_FIELD, true);
		return config;
	}
	
	
}

