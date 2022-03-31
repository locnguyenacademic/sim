package net.hudup.temp.test.a;

import net.hudup.alg.cf.nb.NeighborCFExtItemBased;
import net.hudup.core.data.Profile;
import net.hudup.core.data.RatingVector;
import net.hudup.core.logistic.ForTest;

public class AI03Pearson extends NeighborCFExtItemBased implements ForTest {


	private static final long serialVersionUID = 1L;

	
	public AI03Pearson() {

	}

	
	@Override
	protected double sim0(String measure, RatingVector vRating1, RatingVector vRating2, Profile profile1,
			Profile profile2, Object... params) {
		return corr(vRating1, vRating2);
	}

	
	@Override
	public String getName() {
		return "AI03.Pearson";
	}

	
}
