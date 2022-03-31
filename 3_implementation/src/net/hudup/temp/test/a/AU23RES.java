package net.hudup.temp.test.a;

import net.hudup.alg.cf.nb.NeighborCFExtUserBased;
import net.hudup.core.data.Profile;
import net.hudup.core.data.RatingVector;
import net.hudup.core.logistic.ForTest;

public class AU23RES extends NeighborCFExtUserBased implements ForTest {


	private static final long serialVersionUID = 1L;

	
	public AU23RES() {

	}

	
	@Override
	protected double sim0(String measure, RatingVector vRating1, RatingVector vRating2, Profile profile1,
			Profile profile2, Object... params) {
		return res(vRating1, vRating2, profile1, profile2);
	}

	
	@Override
	public String getName() {
		return "AU23.RES";
	}

	
}
