/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.alg.cf;

import java.rmi.RemoteException;
import java.util.Map;
import java.util.Set;

import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.alg.RecommendParam;
import net.hudup.core.alg.cf.NeighborCF2d;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.Profile;
import net.hudup.core.data.RatingVector;
import net.hudup.core.logistic.BaseClass;
import net.hudup.core.logistic.Inspector;
import net.hudup.core.logistic.LogUtil;
import net.hudup.evaluate.ui.EvaluateGUI;

/**
 * This class sets up an advanced version of 3-dimensional nearest neighbors collaborative filtering algorithm.
 * Note that similarity for this algorithm must be normalized in interval [0, 1].
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
@BaseClass //It is not base class. The notation is used for later update.
public class NeighborCF3d extends NeighborCF2d {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public NeighborCF3d() {

	}

	
	@Override
	public synchronized RatingVector estimate(RecommendParam param, Set<Integer> queryIds) throws RemoteException {
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
		double minValue = getMinRating();
		double maxValue = getMaxRating();
		boolean isUsedMinMax = isUsedMinMaxRating(); 
		Map<Integer, Double> localUserSimCache = Util.newMap();
		Fetcher<RatingVector> userRatings = dataset.fetchUserRatings();
		Fetcher<RatingVector> itemRatings = dataset.fetchItemRatings();
		for (int itemId : queryIds) {
			double accum = 0;
			double simTotal = 0;
			
			//Estimation according to user-based matrix.
			if (thisUser.isRated(itemId)) {
				result.put(itemId, thisUser.get(itemId));
				continue;
			}
			
			RatingVector thisItem = dataset.getItemRating(itemId);
			try {
				while (userRatings.next()) {
					RatingVector thatUser = userRatings.pick();
					double userSim = Constants.UNUSED;
					if (thatUser != null && thatUser.id() != thisUser.id() && thatUser.isRated(itemId)) {
						Profile thatUserProfile = hybrid ? dataset.getUserProfile(thatUser.id()) : null;
						
						//Computing similarity
						if (isCached() && thisUser.id() < 0) { //Local caching
							if (localUserSimCache.containsKey(thatUser.id()))
								userSim = localUserSimCache.get(thatUser.id());
							else {
								userSim = sim(thisUser, thatUser, thisUserProfile, thatUserProfile, itemId);
								localUserSimCache.put(thatUser.id(), userSim);
							}
						}
						else
							userSim = sim(thisUser, thatUser, thisUserProfile, thatUserProfile, itemId);
						
						if (Util.isUsed(userSim)) {
							accum += userSim * thatUser.get(itemId).value;
							simTotal += Math.abs(userSim);
						}
					}

					if (thisItem == null) continue;
					
					Profile thisItemProfile = hybrid ? dataset.getItemProfile(itemId) : null;
					while (itemRatings.next()) {
						RatingVector thatItem = itemRatings.pick();
						if (thatItem == null || thatItem.id() == itemId)
							continue;
						if (thisUser.isRated(thatItem.id()) && !thatItem.isRated(thisUser.id())) {
							thatItem = (RatingVector)thatItem.clone();
							thatItem.put(thisUser.id(), thisUser.get(thatItem.id()));
						}
						
						if (!thatItem.isRated(thatUser.id())) continue;

						Profile thatItemProfile = hybrid ? dataset.getItemProfile(thatItem.id()) : null;

						//Computing similarity
						double itemSim = itemBasedCF.sim(thisItem, thatItem, thisItemProfile, thatItemProfile, thisUser.id());
						if (!Util.isUsed(itemSim)) continue;

						double thatItemValue = thatItem.get(thatUser.id()).value;
						if (thatUser.id() == thisUser.id()) {
							accum += itemSim * thatItemValue;
							simTotal += Math.abs(itemSim);
						}
						else if (Util.isUsed(userSim)) {
							double d1 = 1 - userSim;
							double d2 = 1 - itemSim;
							double userItemSim = 1 - Math.sqrt(d1*d1 + d2*d2);
							
							accum += userItemSim * thatItemValue;
							simTotal += Math.abs(userItemSim);
						}
					}
					itemRatings.reset();

				}
				userRatings.reset();
			}
			catch (Throwable e) {
				LogUtil.trace(e);
			}
			
			if (simTotal == 0) continue;
			
			double value = accum / simTotal;
			value = isUsedMinMax ? Math.min(value, maxValue) : value;
			value = isUsedMinMax ? Math.max(value, minValue) : value;
			result.put(itemId, value);
		}
		
		try {
			userRatings.close();
			itemRatings.close();
		} 
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		localUserSimCache.clear();
		
		return result.size() == 0 ? null : result;
	}

	
	@Override
	public String getName() {
		String name = getConfig().getAsString(DUPLICATED_ALG_NAME_FIELD);
		if (name != null && !name.isEmpty())
			return name;
		else
			return "neighborcf_3d";
	}


	@Override
	public String getDescription() throws RemoteException {
		return "Three-dimension nearest neighbors collaborative filtering algorithm";
	}


	@Override
	public Inspector getInspector() {
		return EvaluateGUI.createInspector(this);
	}

	
}
