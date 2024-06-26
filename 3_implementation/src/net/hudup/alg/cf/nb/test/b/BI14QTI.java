package net.hudup.alg.cf.nb.test.b;

import net.hudup.alg.cf.nb.Measure;
import net.hudup.alg.cf.nb.NeighborCFExtItemBased;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Profile;
import net.hudup.core.data.RatingVector;
import net.hudup.core.logistic.ForTest;

public class BI14QTI extends NeighborCFExtItemBased implements ForTest {


	private static final long serialVersionUID = 1L;

	
	public BI14QTI() {

	}

	
	@Override
	protected double sim0(String measure, RatingVector vRating1, RatingVector vRating2, Profile profile1,
			Profile profile2, Object... params) {
		return quasiTfIdfNormal(vRating1, vRating2, profile1, profile2);
	}

	
	@Override
	public String getName() {
		return "BI14.QTI";
	}

	
	@Override
	public DataConfig createDefaultConfig() {
		DataConfig config = super.createDefaultConfig();
		config.put(MEASURE, Measure.QUASI_TFIDF);
		config.put(QUASI_TFIDF_TYPE, QUASI_TFIDF_TYPE_NORMAL);
		return config;
	}
	
	
}
