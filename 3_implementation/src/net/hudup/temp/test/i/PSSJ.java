package net.hudup.temp.test.i;

import net.hudup.alg.cf.nb.NeighborCFExtItemBased;
import net.hudup.core.data.Profile;
import net.hudup.core.data.RatingVector;
import net.hudup.core.logistic.ForTest;

public class PSSJ extends NeighborCFExtItemBased implements ForTest {

	
	private static final long serialVersionUID = 1L;

	
	public PSSJ() {

	}

	
	@Override
	protected double sim0(String measure, RatingVector vRating1, RatingVector vRating2, Profile profile1,
			Profile profile2, Object... params) {
		return jaccardNormal(vRating1, vRating2, profile1, profile2) * pssNormal(vRating1, vRating2, profile1, profile2);
	}

	
	@Override
	public String getName() {
		return "i06.03.PSSJ";
	}

	
}
