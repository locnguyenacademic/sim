package net.hudup.alg.cf.nb.test.c;

import java.util.Set;

import net.hudup.alg.cf.nb.Measure;
import net.hudup.alg.cf.nb.NeighborCFExtItemBased;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Profile;
import net.hudup.core.data.RatingVector;
import net.hudup.core.logistic.ForTest;

public class CI14SMDEMX6 extends NeighborCFExtItemBased implements ForTest {


	private static final long serialVersionUID = 1L;

	
	public CI14SMDEMX6() {

	}

	
	@Override
	protected double sim0(String measure, RatingVector vRating1, RatingVector vRating2, Profile profile1,
			Profile profile2, Object... params) {
		Set<Integer> common = commonFieldIds(vRating1, vRating2);
		if (common.size() == 0) return 0;
		
		double mean1 = vRating1.mean();
		double var1 = vRating1.mleVar();
		double mean2 = vRating2.mean();
		double var2 = vRating2.mleVar();
		double urp = 1.0 - 1.0 / (1.0 + Math.exp(-Math.abs(mean1-mean2)*Math.abs(var1-var2)));
		
		return urp * smd(vRating1, vRating2, profile1, profile2) * stb(vRating1, vRating2, profile1, profile2);
	}

	
	@Override
	public String getName() {
		return "CI14.SMD.EMX6";
	}

	
	@Override
	public DataConfig createDefaultConfig() {
		DataConfig config = super.createDefaultConfig();
		config.put(MEASURE, Measure.SMD);
		return config;
	}
	
	
}
