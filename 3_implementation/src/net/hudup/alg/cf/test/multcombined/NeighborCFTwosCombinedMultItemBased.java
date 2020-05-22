package net.hudup.alg.cf.test.multcombined;

import java.rmi.RemoteException;
import java.util.Set;

import net.hudup.alg.cf.NeighborCFExt;
import net.hudup.alg.cf.NeighborCFExtItemBased;
import net.hudup.core.alg.DuplicatableAlg;
import net.hudup.core.alg.RecommendParam;
import net.hudup.core.alg.cf.NeighborCFItemBased;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Profile;
import net.hudup.core.data.RatingVector;

public class NeighborCFTwosCombinedMultItemBased extends NeighborCFTwosCombinedMult implements DuplicatableAlg {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public NeighborCFTwosCombinedMultItemBased() {
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public RatingVector estimate(RecommendParam param, Set<Integer> queryIds) throws RemoteException {
		// TODO Auto-generated method stub
		return NeighborCFItemBased.estimate(this, param, queryIds);
	}

	
	@Override
	protected double cod(
			RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		return cod(vRating1, vRating2, this.userMeans);
	}

	
	@Override
	protected double pip(RatingVector vRating1, RatingVector vRating2, Profile profile1, Profile profile2) {
		// TODO Auto-generated method stub
		return pip(vRating1, vRating2, this.userMeans);
	}


	@Override
	protected double pss(RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		return pss(vRating1, vRating2, this.userMeans);
	}

	
	@Override
	protected double pc(RatingVector vRating1, RatingVector vRating2, Profile profile1,
			Profile profile2, int fixedColumnId) {
		// TODO Auto-generated method stub
		return pc(vRating1, vRating2, fixedColumnId, this.userMeans);
	}


	@Override
	protected RatingVector getColumnRating(int columnId) {
		// TODO Auto-generated method stub
		return this.dataset.getUserRating(columnId);
	}

	
	@Override
	protected Set<Integer> getColumnIds() {
		// TODO Auto-generated method stub
		return this.userIds;
	}

	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		String name = getConfig().getAsString(DUPLICATED_ALG_NAME_FIELD);
		if (name != null && !name.isEmpty())
			return name;
		else
			return "multiplycombined2cf_itembased";
	}


	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub
		getConfig().put(DUPLICATED_ALG_NAME_FIELD, name);
	}

	
	@Override
	public String getDescription() throws RemoteException {
		// TODO Auto-generated method stub
		return "Item-based nearest neighbors collaborative filtering algorithm by multiplicated combination of two other ones";
	}


	@Override
	public DataConfig createDefaultConfig() {
		DataConfig config = super.createDefaultConfig();
		config.addReadOnly(DUPLICATED_ALG_NAME_FIELD);
		return config;
	}


	@Override
	protected NeighborCFExt createDualCF() {
		return new NeighborCFExtItemBased();
	}


}
