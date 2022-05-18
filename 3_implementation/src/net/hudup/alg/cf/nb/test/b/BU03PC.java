package net.hudup.alg.cf.nb.test.b;

import java.rmi.RemoteException;
import java.util.Set;

import net.hudup.alg.cf.nb.Measure;
import net.hudup.alg.cf.nb.NeighborCFExtUserBased;
import net.hudup.core.Constants;
import net.hudup.core.alg.RecommendParam;
import net.hudup.core.alg.cf.nb.NeighborCFUserBased;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Profile;
import net.hudup.core.data.RatingVector;
import net.hudup.core.logistic.ForTest;

public class BU03PC extends NeighborCFExtUserBased implements ForTest {

	
	private static final long serialVersionUID = 1L;

	
	public BU03PC() {

	}

	
	@Override
	public RatingVector estimate(RecommendParam param, Set<Integer> queryIds) throws RemoteException {
		return NeighborCFUserBased.estimateNormal(this, param, queryIds);
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
		return "BU03.PC";
	}

	
	@Override
	public DataConfig createDefaultConfig() {
		DataConfig config = super.createDefaultConfig();
		config.put(MEASURE, Measure.PC);
		return config;
	}
	
	
}

