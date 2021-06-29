package net.temp.hudup.alg.cf.test.multcombined;

import java.rmi.RemoteException;
import java.util.Set;

import net.hudup.alg.cf.nb.NeighborCFExt;
import net.hudup.alg.cf.nb.NeighborCFExtItemBased;
import net.hudup.core.alg.DuplicatableAlg;
import net.hudup.core.alg.RecommendParam;
import net.hudup.core.alg.cf.nb.NeighborCFItemBased;
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

	}

	
	@Override
	public RatingVector estimate(RecommendParam param, Set<Integer> queryIds) throws RemoteException {
		return NeighborCFItemBased.estimate(this, param, queryIds);
	}

	
	@Override
	protected double cod(
			RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		return cod(vRating1, vRating2, getUserMeans());
	}

	
	@Override
	protected double pip(RatingVector vRating1, RatingVector vRating2, Profile profile1, Profile profile2) {
		return pip(vRating1, vRating2, getUserMeans());
	}


	@Override
	protected double pss(RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		return pss(vRating1, vRating2, getUserMeans());
	}

	
	@Override
	protected double pc(RatingVector vRating1, RatingVector vRating2, Profile profile1,
			Profile profile2, int fixedColumnId) {
		return pc(vRating1, vRating2, fixedColumnId, getUserMeans());
	}


	@Override
	protected Set<Integer> getRowIds() {
		return getItemIds();
	}

	
	@Override
	protected RatingVector getRowRating(int rowId) {
		return dataset.getItemRating(rowId);
	}

	
	@Override
	protected double calcRowMean(RatingVector vRating) {
		return calcItemMean(vRating);
	}


	@Override
	protected RatingVector getColumnRating(int columnId) {
		return this.dataset.getUserRating(columnId);
	}

	
	@Override
	protected Set<Integer> getColumnIds() {
		return getUserIds();
	}


	@Override
	protected double calcColumnMean(RatingVector vRating) {
		return calcUserMean(vRating);
	}

	
	@Override
	public String getName() {
		String name = getConfig().getAsString(DUPLICATED_ALG_NAME_FIELD);
		if (name != null && !name.isEmpty())
			return name;
		else
			return "multiplycombined2cf_itembased";
	}


	@Override
	public void setName(String name) {
		getConfig().put(DUPLICATED_ALG_NAME_FIELD, name);
	}

	
	@Override
	public String getDescription() throws RemoteException {
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
