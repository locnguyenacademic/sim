/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.alg.cf;

import java.rmi.RemoteException;
import java.util.Set;

import net.hudup.core.alg.DuplicatableAlg;
import net.hudup.core.alg.RecommendParam;
import net.hudup.core.alg.cf.NeighborCFUserBased;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Profile;
import net.hudup.core.data.RatingVector;

/**
 * This class implements the combined nearest neighbors collaborative filtering algorithm that combines two measures for users.<br>
 * Author Ali Amer proposed such combination.
 * 
 * @author Ali Amer
 * @version 1.0
 *
 */
public class NeighborCFTwosCombinedUserBased extends NeighborCFTwosCombined implements DuplicatableAlg {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public NeighborCFTwosCombinedUserBased() {

	}

	
	@Override
	public RatingVector estimate(RecommendParam param, Set<Integer> queryIds) throws RemoteException {
		return NeighborCFUserBased.estimate(this, param, queryIds);
	}

	
	@Override
	protected double cod(
			RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		return cod(vRating1, vRating2, this.itemMeans);
	}

	
	@Override
	protected double pip(RatingVector vRating1, RatingVector vRating2, Profile profile1, Profile profile2) {
		return pip(vRating1, vRating2, this.itemMeans);
	}


	@Override
	protected double pss(RatingVector vRating1, RatingVector vRating2, Profile profile1, Profile profile2) {
		return pss(vRating1, vRating2, this.itemMeans);
	}

	
	@Override
	protected double pc(RatingVector vRating1, RatingVector vRating2, Profile profile1,
			Profile profile2, int fixedColumnId) {
		return pc(vRating1, vRating2, fixedColumnId, this.itemMeans);
	}


	@Override
	protected RatingVector getColumnRating(int columnId) {
		return this.dataset.getItemRating(columnId);
	}


	@Override
	protected Set<Integer> getColumnIds() {
		return this.itemIds;
	}

	
	@Override
	public String getName() {
		String name = getConfig().getAsString(DUPLICATED_ALG_NAME_FIELD);
		if (name != null && !name.isEmpty())
			return name;
		else
			return "combined2cf_userbased";
	}


	@Override
	public void setName(String name) {
		getConfig().put(DUPLICATED_ALG_NAME_FIELD, name);
	}

	
	@Override
	public String getDescription() throws RemoteException {
		return "User-based collaborative filtering algorithm by combination of two other ones";
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
