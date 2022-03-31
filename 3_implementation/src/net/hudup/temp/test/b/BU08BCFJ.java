package net.hudup.temp.test.b;

import net.hudup.alg.cf.nb.NeighborCFExtUserBased;
import net.hudup.core.data.Profile;
import net.hudup.core.data.RatingVector;
import net.hudup.core.logistic.ForTest;

public class BU08BCFJ extends NeighborCFExtUserBased implements ForTest {

	
	private static final long serialVersionUID = 1L;

	
	public BU08BCFJ() {

	}

	
	@Override
	protected double sim0(String measure, RatingVector vRating1, RatingVector vRating2, Profile profile1,
			Profile profile2, Object... params) {
		return bcfj(vRating1, vRating2, profile1, profile2);
	}
	
	
	@Override
	public String getName() {
		return "BU08.BCFJ";
	}

	
}
