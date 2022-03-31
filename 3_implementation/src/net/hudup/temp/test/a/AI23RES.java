package net.hudup.temp.test.a;

import net.hudup.alg.cf.nb.NeighborCFExtItemBased;
import net.hudup.core.data.Profile;
import net.hudup.core.data.RatingVector;
import net.hudup.core.logistic.ForTest;

public class AI23RES extends NeighborCFExtItemBased implements ForTest {


	private static final long serialVersionUID = 1L;

	
	public AI23RES() {

	}

	
	@Override
	protected double sim0(String measure, RatingVector vRating1, RatingVector vRating2, Profile profile1,
			Profile profile2, Object... params) {
		return res(vRating1, vRating2, profile1, profile2);
	}

	
	@Override
	public String getName() {
		return "AI23.RES";
	}

	
}