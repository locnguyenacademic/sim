package net.hudup.temp.test.a;

import net.hudup.alg.cf.nb.NeighborCFExtItemBased;
import net.hudup.core.data.Profile;
import net.hudup.core.data.RatingVector;
import net.hudup.core.logistic.ForTest;

public class AI24SM extends NeighborCFExtItemBased implements ForTest {


	private static final long serialVersionUID = 1L;

	
	public AI24SM() {

	}

	
	@Override
	protected double sim0(String measure, RatingVector vRating1, RatingVector vRating2, Profile profile1,
			Profile profile2, Object... params) {
		return sm(vRating1, vRating2, profile1, profile2);
	}

	
	@Override
	public String getName() {
		return "AI24.SM";
	}

	
}

