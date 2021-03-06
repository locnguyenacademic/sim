/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.alg.cf.nb;

import java.rmi.RemoteException;
import java.util.Set;

import net.hudup.core.alg.DuplicatableAlg;
import net.hudup.core.alg.RecommendParam;
import net.hudup.core.alg.cf.nb.NeighborCFUserBased;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Profile;
import net.hudup.core.data.RatingVector;

/**
 * This class sets up an advanced version of user-based nearest neighbors collaborative filtering (Neighbor CF) algorithm with type support.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class NeighborCFTypeUserBased extends NeighborCFType implements DuplicatableAlg {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public NeighborCFTypeUserBased() {

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
	protected Set<Integer> getColumnIds() {
		return getItemIds();
	}


	@Override
	protected RatingVector getColumnRating(int columnId) {
		return dataset.getItemRating(columnId);
	}


	@Override
	protected double calcColumnMean(RatingVector vRating) {
		return calcItemMean(vRating);
	}


	@Override
	protected RatingVector convertToTypeVector(RatingVector vRating) {
		if (vRating == null)
			return null;
		else
			return userTypeMap.get(vRating.id());
	}


	@Override
	protected double prob(int columnId) {
		return prob(columnId, false);
	}


	@Override
	public String getName() {
		String name = getConfig().getAsString(DUPLICATED_ALG_NAME_FIELD);
		if (name != null && !name.isEmpty())
			return name;
		else
			return "neighborcf_userbased_type";
	}


	@Override
	public void setName(String name) {
		getConfig().put(DUPLICATED_ALG_NAME_FIELD, name);
	}

	
	@Override
	public String getDescription() throws RemoteException {
		return "Type-supported user-based nearest neighbors collaborative filtering algorithm";
	}


	@Override
	public DataConfig createDefaultConfig() {
		DataConfig config = super.createDefaultConfig();
		config.addReadOnly(DUPLICATED_ALG_NAME_FIELD);
		return config;
	}

	
}
