package net.hudup.alg.cf;

import java.rmi.RemoteException;
import java.util.Map;
import java.util.Set;

import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.RecommendParam;
import net.hudup.core.alg.cf.NeighborCF2d;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.Profile;
import net.hudup.core.data.RatingVector;
import net.hudup.core.logistic.BaseClass;
import net.hudup.core.logistic.Inspector;
import net.hudup.evaluate.ui.EvaluateGUI;

/**
 * This class sets up an advanced version of 3-dimensional neighbor collaborative filtering (Neighbor CF) algorithm.
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
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public synchronized RatingVector estimate(RecommendParam param, Set<Integer> queryIds) throws RemoteException {
		// TODO Auto-generated method stub
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
							double thatUserValue = thatUser.get(itemId).value;
							accum += userSim * thatUserValue;
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
				e.printStackTrace();
			}
			
			if (simTotal == 0) continue;
			
			double value = accum / simTotal;
			value = (Util.isUsed(maxValue)) && (!Double.isNaN(maxValue)) ? Math.min(value, maxValue) : value;
			value = (Util.isUsed(minValue)) && (!Double.isNaN(minValue)) ? Math.max(value, minValue) : value;
			result.put(itemId, value);
		}
		
		try {
			userRatings.close();
			itemRatings.close();
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		localUserSimCache.clear();
		
		return result.size() == 0 ? null : result;
	}

	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		String name = getConfig().getAsString(DUPLICATED_ALG_NAME_FIELD);
		if (name != null && !name.isEmpty())
			return name;
		else
			return "neighborcf_3d";
	}


	@Override
	public String getDescription() throws RemoteException {
		// TODO Auto-generated method stub
		return "Three-dimension nearest neighbors collaborative filtering algorithm";
	}


	@Override
	public Inspector getInspector() {
		// TODO Auto-generated method stub
		return EvaluateGUI.createInspector(this);
	}

	
	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		NeighborCF3d cf = new NeighborCF3d();
		cf.getConfig().putAll((DataConfig)this.getConfig().clone());
		
		return cf;
	}


}
