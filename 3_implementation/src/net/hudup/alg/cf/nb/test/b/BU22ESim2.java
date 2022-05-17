package net.hudup.alg.cf.nb.test.b;

import net.hudup.alg.cf.nb.Measure;
import net.hudup.alg.cf.nb.NeighborCFExtUserBased;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Profile;
import net.hudup.core.data.RatingVector;
import net.hudup.core.logistic.ForTest;

public class BU22ESim2 extends NeighborCFExtUserBased implements ForTest {


	private static final long serialVersionUID = 1L;

	
	public BU22ESim2() {

	}

	
	@Override
	protected double sim0(String measure, RatingVector vRating1, RatingVector vRating2, Profile profile1,
			Profile profile2, Object... params) {
		return esim(vRating1, vRating2, profile1, profile2);
	}

	
	@Override
	public String getName() {
		return "BU22.ESim2";
	}

	
	@Override
	public DataConfig createDefaultConfig() {
		DataConfig config = super.createDefaultConfig();
		config.put(MEASURE, Measure.ESIM);
		config.put(ESIM_TYPE, ESIM_TYPE_ESIM2);
		return config;
	}
	

}
