package net.hudup.temp.test.b;

import net.hudup.alg.cf.nb.NeighborCFExtItemBased;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Profile;
import net.hudup.core.data.RatingVector;
import net.hudup.core.logistic.ForTest;

public class BI22ESim3 extends NeighborCFExtItemBased implements ForTest {


	private static final long serialVersionUID = 1L;

	
	public BI22ESim3() {

	}

	
	@Override
	protected double sim0(String measure, RatingVector vRating1, RatingVector vRating2, Profile profile1,
			Profile profile2, Object... params) {
		return esim(vRating1, vRating2, profile1, profile2);
	}

	
	@Override
	public String getName() {
		return "BI22.ESim3";
	}

	
	@Override
	public DataConfig createDefaultConfig() {
		DataConfig config = super.createDefaultConfig();
		config.put(ESIM_TYPE, ESIM_TYPE_ESIM3);
		return config;
	}
	

}
