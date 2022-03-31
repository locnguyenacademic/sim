package net.hudup.temp.test.b;

import net.hudup.alg.cf.nb.NeighborCFExtItemBased;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Profile;
import net.hudup.core.data.RatingVector;
import net.hudup.core.logistic.ForTest;

public class BI22ZSim extends NeighborCFExtItemBased implements ForTest {


	private static final long serialVersionUID = 1L;

	
	public BI22ZSim() {

	}

	
	@Override
	protected double sim0(String measure, RatingVector vRating1, RatingVector vRating2, Profile profile1,
			Profile profile2, Object... params) {
		return esim(vRating1, vRating2, profile1, profile2);
	}

	
	@Override
	public String getName() {
		return "BI22.ZSim";
	}

	
	@Override
	public DataConfig createDefaultConfig() {
		DataConfig config = super.createDefaultConfig();
		config.put(ESIM_TYPE, ESIM_TYPE_ZSIM);
		return config;
	}
	

}

