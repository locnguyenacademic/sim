package net.temp.hudup.alg.cf.test.multcombined;

import java.rmi.RemoteException;
import java.util.Set;

import net.hudup.alg.cf.nb.NeighborCFExt;
import net.hudup.alg.cf.nb.NeighborCFExtUserBased;
import net.hudup.core.alg.DuplicatableAlg;
import net.hudup.core.alg.RecommendParam;
import net.hudup.core.alg.cf.nb.NeighborCFUserBased;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Profile;
import net.hudup.core.data.RatingVector;

public class NeighborCFTwosCombinedMultUserBased extends NeighborCFTwosCombinedMult implements DuplicatableAlg {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public NeighborCFTwosCombinedMultUserBased() {
	}

	
	@Override
	public RatingVector estimate(RecommendParam param, Set<Integer> queryIds) throws RemoteException {
		return NeighborCFUserBased.estimate(this, param, queryIds);
	}

	
	@Override
	protected double cod(
			RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		return cod(vRating1, vRating2, getItemMeans());
	}

	
	@Override
	protected double pipNormal(RatingVector vRating1, RatingVector vRating2, Profile profile1, Profile profile2) {
		return pipNormal(vRating1, vRating2, getItemMeans());
	}


	@Override
	protected double mpip(RatingVector vRating1, RatingVector vRating2, Profile profile1, Profile profile2) {
		return mpip(vRating1, vRating2, getItemMeans());
	}


	@Override
	protected double pssNormal(RatingVector vRating1, RatingVector vRating2, Profile profile1, Profile profile2) {
		return pssNormal(vRating1, vRating2, getItemMeans());
	}

	
	@Override
	protected double pc(RatingVector vRating1, RatingVector vRating2, Profile profile1,
			Profile profile2, int fixedColumnId) {
		return pc(vRating1, vRating2, fixedColumnId, getItemMeans());
	}


	@Override
	protected Set<Integer> getRowIds() {
		return getUserIds();
	}


	@Override
	protected RatingVector getRowRating(int rowId) {
		return dataset.getUserRating(rowId);
	}


	@Override
	protected double calcRowMean(RatingVector vRating) {
		return calcUserMean(vRating);
	}


	@Override
	protected RatingVector getColumnRating(int columnId) {
		return this.dataset.getItemRating(columnId);
	}


	@Override
	protected Set<Integer> getColumnIds() {
		return getItemIds();
	}


	@Override
	protected double calcColumnMean(RatingVector vRating) {
		return calcItemMean(vRating);
	}

	
	@Override
	public String getName() {
		String name = getConfig().getAsString(DUPLICATED_ALG_NAME_FIELD);
		if (name != null && !name.isEmpty())
			return name;
		else
			return "multiplycombined2cf_userbased";
	}


	@Override
	public void setName(String name) {
		getConfig().put(DUPLICATED_ALG_NAME_FIELD, name);
	}

	
	@Override
	public String getDescription() throws RemoteException {
		return "User-based collaborative filtering algorithm by multiplicated combination of two other ones";
	}


	@Override
	public DataConfig createDefaultConfig() {
		DataConfig config = super.createDefaultConfig();
		config.addReadOnly(DUPLICATED_ALG_NAME_FIELD);
		return config;
	}

	
	@Override
	protected NeighborCFExt createDualCF() {
		return new NeighborCFExtUserBased();
	}


}
