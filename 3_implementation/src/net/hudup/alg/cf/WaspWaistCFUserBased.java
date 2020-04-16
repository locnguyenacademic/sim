/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.alg.cf;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.DuplicatableAlg;
import net.hudup.core.alg.RecommendParam;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.Profile;
import net.hudup.core.data.RatingVector;
import net.hudup.core.logistic.BaseClass;
import net.hudup.core.logistic.LogUtil;

/**
 * This abstract class implements the user-based Wasp Waist (WW) collaborative filtering algorithm.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
@BaseClass //It is not base class. The notation is used for later update.
public class WaspWaistCFUserBased extends WaspWaistCF implements DuplicatableAlg {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public WaspWaistCFUserBased() {
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public RatingVector estimate(RecommendParam param, Set<Integer> queryIds) throws RemoteException {
		/*
		 * There are three cases of param.ratingVector:
		 * 1. Its id is < 0, which indicates it is not stored in training dataset then, caching does not work even though this is cached algorithm.
		 * 2. Its id is >= 0 and, it must be empty or the same to the existing one in training dataset. If it is empty, it will be fulfilled as the same to the existing one in training dataset.
		 * 3. Its id is >= 0 but, it is not stored in training dataset then, it must be a full rating vector of a user.
		 */
		if (param.ratingVector == null) return null;
		
		RatingVector thisUser = param.ratingVector;
		RatingVector innerUser = dataset.getUserRating(thisUser.id());
		if (innerUser != null) {
			Set<Integer> itemIds = innerUser.fieldIds(true);
			itemIds.removeAll(thisUser.fieldIds(true));
			if (itemIds.size() > 0) thisUser = (RatingVector)thisUser.clone();
			for (int itemId : itemIds) {
				if (!thisUser.isRated(itemId))
					thisUser.put(itemId, innerUser.get(itemId));
			}
		}
		if (thisUser.size() == 0) return null;
		
		RatingVector result = thisUser.newInstance(true);
		boolean hybrid = config.getAsBoolean(HYBRID);
		Profile thisUserProfile = hybrid ? param.profile : null;
		double minValue = config.getMinRating();
		double maxValue = config.getMaxRating();
		double thisMean = thisUser.mean();
		Map<Integer, Double> localUserSimCache = Util.newMap();
		Fetcher<RatingVector> userRatings = dataset.fetchUserRatings();
		for (int itemId : queryIds) {
			if (thisUser.isRated(itemId)) {
				result.put(itemId, thisUser.get(itemId));
				continue;
			}
			
			double accum = 0;
			double simTotal = 0;
			RatingVector crushedItem = crush(itemId, thisUser);
			boolean calculated = false;
			try {
				while (userRatings.next()) {
					RatingVector thatUser = userRatings.pick();
					//That user can be this user because the crushed item can have estimated value for this user.
					//In the case that this user is that user, their similarity of course is highest.
					if (thatUser == null) continue;
					
					double thatValue = Constants.UNUSED;
					if (thatUser.isRated(itemId))
						thatValue = thatUser.get(itemId).value;
					else if (crushedItem != null && crushedItem.isRated(thatUser.id()))
						thatValue = crushedItem.get(thatUser.id()).value;
					else
						continue;

					Profile thatUserProfile = hybrid ? dataset.getUserProfile(thatUser.id()) : null;
					
					// computing similarity
					double sim = Constants.UNUSED;
					if (isCached() && thisUser.id() < 0) { //Local caching
						if (localUserSimCache.containsKey(thatUser.id()))
							sim = localUserSimCache.get(thatUser.id());
						else {
							sim = sim(thisUser, thatUser, thisUserProfile, thatUserProfile, itemId);
							localUserSimCache.put(thatUser.id(), sim);
						}
					}
					else
						sim = sim(thisUser, thatUser, thisUserProfile, thatUserProfile, itemId);
					if (!Util.isUsed(sim)) continue;
					
					double thatMean = thatUser.mean();
					double deviate = thatValue - thatMean;
					accum += sim * deviate;
					simTotal += Math.abs(sim);
					
					calculated = true;
				}
				userRatings.reset();
			}
			catch (Throwable e) {
				LogUtil.trace(e);
			}
			if (!calculated) continue;
			
			double value = simTotal == 0 ? thisMean : thisMean + accum / simTotal;
			value = (Util.isUsed(maxValue)) && (!Double.isNaN(maxValue)) ? Math.min(value, maxValue) : value;
			value = (Util.isUsed(minValue)) && (!Double.isNaN(minValue)) ? Math.max(value, minValue) : value;
			result.put(itemId, value);
		}
		
		try {
			userRatings.close();
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			LogUtil.trace(e);
		}
		localUserSimCache.clear();
		
		return result.size() == 0 ? null : result;
	}

	
	@Override
	protected RatingVector crushAsUsual(int columnId, RatingVector userRating) {
		RatingVector thisItem = this.dataset.getItemRating(columnId);
		if (thisItem == null) return null;
		RatingVector result = thisItem.compactClone();
		if (result.size() == 0) return null;
		
		Fetcher<RatingVector> vRatings = this.dataset.fetchItemRatings();
		List<Object[]> simList = Util.newList(0);
		boolean fulfill = (!isCached()) && (userRating != null);
		try {
			while (vRatings.next()) {
				//That rating vector is often considered as to be not empty and not to have unrated value.
				RatingVector thatItem = vRatings.pick();
				if (thatItem == null || thatItem.id() == columnId)
					continue;
				
				if (fulfill &&
						userRating.isRated(thatItem.id()) && !thatItem.isRated(userRating.id())) {
					thatItem = (RatingVector)thatItem.clone();
					thatItem.put(userRating.id(), userRating.get(thatItem.id()));
				}

				double sim = dualCF.sim(thisItem, thatItem, null, null, userRating.id());
				if (Util.isUsed(sim))
					simList.add(new Object[] {thatItem, sim});
			}
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		try {
			vRatings.close();
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			LogUtil.trace(e);
		}
		if (simList.size() == 0) return result;
		
		double minValue = config.getMinRating();
		double maxValue = config.getMaxRating();
		double thisMean = thisItem.mean();
		Set<Integer> userIds = Util.newSet();
		userIds.addAll(this.userIds);
		userIds.removeAll(thisItem.fieldIds(true));
		for (int userId : userIds) {
			double accum = 0;
			double simTotal = 0;
			boolean calculated = false;
			for (Object[] aSim : simList) {
				RatingVector thatItem = (RatingVector)aSim[0];
				if (!thatItem.isRated(userId)) continue;
				
				double thatValue = thatItem.get(userId).value;
				double thatMean = thatItem.mean();
				double deviate = thatValue - thatMean;
				accum += (double)(aSim[1]) * deviate;
				simTotal += Math.abs((double)(aSim[1]));
				
				calculated = true;
			}
			if (!calculated) continue;
			
			double value = simTotal == 0 ? thisMean : thisMean + accum / simTotal;
			value = (Util.isUsed(maxValue)) && (!Double.isNaN(maxValue)) ? Math.min(value, maxValue) : value;
			value = (Util.isUsed(minValue)) && (!Double.isNaN(minValue)) ? Math.max(value, minValue) : value;
			
			result.put(userId, value);
		}
		
		return result;
	}

	
	@Override
	protected NeighborCFExt createDualCF() {
		// TODO Auto-generated method stub
		return new NeighborCFExtItemBased();
	}


	@Override
	protected double cod(
			RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		return cod(vRating1, vRating2, this.itemMeans);
	}

	
	@Override
	protected double pip(RatingVector vRating1, RatingVector vRating2, Profile profile1, Profile profile2) {
		// TODO Auto-generated method stub
		return pip(vRating1, vRating2, this.itemMeans);
	}


	@Override
	protected double pss(RatingVector vRating1, RatingVector vRating2, Profile profile1, Profile profile2) {
		// TODO Auto-generated method stub
		return pss(vRating1, vRating2, this.itemMeans);
	}


	@Override
	protected double pc(RatingVector vRating1, RatingVector vRating2, Profile profile1,
			Profile profile2, int fixedColumnId) {
		// TODO Auto-generated method stub
		return pc(vRating1, vRating2, fixedColumnId, this.itemMeans);
	}


	@Override
	protected RatingVector getColumnRating(int fieldId) {
		// TODO Auto-generated method stub
		return this.dataset.getItemRating(fieldId);
	}

	
	@Override
	protected Set<Integer> getColumnIds() {
		// TODO Auto-generated method stub
		return this.itemIds;
	}

	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		String name = getConfig().getAsString(DUPLICATED_ALG_NAME_FIELD);
		if (name != null && !name.isEmpty())
			return name;
		else
			return "wwcf_userbased";
	}


	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub
		getConfig().put(DUPLICATED_ALG_NAME_FIELD, name);
	}

	
	@Override
	public String getDescription() throws RemoteException {
		// TODO Auto-generated method stub
		return "User-based Wasp Waist algorithm";
	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		WaspWaistCFUserBased cf = new WaspWaistCFUserBased();
		cf.getConfig().putAll((DataConfig)this.getConfig().clone());
		
		return cf;
	}


	@Override
	public DataConfig createDefaultConfig() {
		// TODO Auto-generated method stub
		DataConfig config = super.createDefaultConfig();
		config.addReadOnly(DUPLICATED_ALG_NAME_FIELD);
		return config;
	}

	
}
