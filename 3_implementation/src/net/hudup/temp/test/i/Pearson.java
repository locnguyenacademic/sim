package net.hudup.temp.test.i;

import net.hudup.alg.cf.nb.NeighborCFExtItemBased;
import net.hudup.core.data.Profile;
import net.hudup.core.data.RatingVector;

public class Pearson extends NeighborCFExtItemBased {


	private static final long serialVersionUID = 1L;

	
	public Pearson() {

	}

	
	@Override
	protected double sim0(String measure, RatingVector vRating1, RatingVector vRating2, Profile profile1,
			Profile profile2, Object... params) {
		return corr(vRating1, vRating2);
	}

	
	@Override
	public String getName() {
		return "i02.00.Pearson";
	}

	
}
