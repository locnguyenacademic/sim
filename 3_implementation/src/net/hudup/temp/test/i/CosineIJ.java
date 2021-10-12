package net.hudup.temp.test.i;

import net.hudup.alg.cf.nb.NeighborCFExtItemBased;
import net.hudup.core.data.Profile;
import net.hudup.core.data.RatingVector;
import net.hudup.core.logistic.ForTest;

public class CosineIJ extends NeighborCFExtItemBased implements ForTest {

	
	private static final long serialVersionUID = 1L;

	
	public CosineIJ() {

	}

	
	@Override
	protected double sim0(String measure, RatingVector vRating1, RatingVector vRating2, Profile profile1,
			Profile profile2, Object... params) {
		return jaccardImproved(vRating1, vRating2, profile1, profile2) * cosineNormal(vRating1, vRating2, profile1, profile2);
	}

	
	@Override
	public String getName() {
		return "i07.01.CosineIJ";
	}

	
}
