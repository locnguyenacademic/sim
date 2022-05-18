package net.hudup.alg.cf.nb.test.b;

import net.hudup.alg.cf.nb.NeighborCFExtItemBased;
import net.hudup.alg.cf.nb.Measure;
import net.hudup.core.Constants;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Profile;
import net.hudup.core.data.RatingVector;
import net.hudup.core.logistic.ForTest;

public class BI03PC extends NeighborCFExtItemBased implements ForTest {

	
	private static final long serialVersionUID = 1L;

	
	public BI03PC() {

	}

	
	@Override
	protected double sim0(String measure, RatingVector vRating1, RatingVector vRating2, Profile profile1,
			Profile profile2, Object... params) {
		if ((params == null) || (params.length < 1) || !(params[0] instanceof Number))
			return Constants.UNUSED;
		else {
			int fixedColumnId = ((Number)(params[0])).intValue();
			return pc(vRating1, vRating2, profile1, profile2, fixedColumnId);
		}
	}
	
	
	@Override
	public String getName() {
		return "BI03.PC";
	}

	
	@Override
	public DataConfig createDefaultConfig() {
		DataConfig config = super.createDefaultConfig();
		config.put(MEASURE, Measure.PC);
		return config;
	}
	
	
}

