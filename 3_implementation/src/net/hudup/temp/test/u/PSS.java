package net.hudup.temp.test.u;

import net.hudup.alg.cf.nb.NeighborCFExtUserBased;
import net.hudup.core.data.Profile;
import net.hudup.core.data.RatingVector;
import net.hudup.core.logistic.ForTest;

public class PSS extends NeighborCFExtUserBased implements ForTest {


	private static final long serialVersionUID = 1L;

	
	public PSS() {

	}

	
	@Override
	protected double sim0(String measure, RatingVector vRating1, RatingVector vRating2, Profile profile1,
			Profile profile2, Object... params) {
		return pssNormal(vRating1, vRating2, profile1, profile2);
	}

	
	@Override
	public String getName() {
		return "u03.00.PSS";
	}

	
}

