package net.hudup.temp.test.b;

import net.hudup.alg.cf.nb.Measure;
import net.hudup.alg.cf.nb.NeighborCFExtItemBased;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Profile;
import net.hudup.core.data.RatingVector;
import net.hudup.core.logistic.ForTest;

public class BI10TMJ extends NeighborCFExtItemBased implements ForTest {

	
	private static final long serialVersionUID = 1L;

	
	public BI10TMJ() {

	}

	
	@Override
	protected double sim0(String measure, RatingVector vRating1, RatingVector vRating2, Profile profile1,
			Profile profile2, Object... params) {
		return triangleNormal(vRating1, vRating2, profile1, profile2) * jaccardNormal(vRating1, vRating2, profile1, profile2);
	}
	
	
	@Override
	public String getName() {
		return "BI10.TMJ";
	}

	
	@Override
	public DataConfig createDefaultConfig() {
		DataConfig config = super.createDefaultConfig();
		config.put(MEASURE, Measure.TRIANGLE);
		config.put(TRIANGLE_TYPE, TRIANGLE_TYPE_TMJ);
		return config;
	}
	
	
}
