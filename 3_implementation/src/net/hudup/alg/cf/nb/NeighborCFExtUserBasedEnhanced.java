/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.alg.cf.nb;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.alg.RecommendParam;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.DatasetMetadata2;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.ObjectPair;
import net.hudup.core.data.Profile;
import net.hudup.core.data.RatingVector;
import net.hudup.core.logistic.LogUtil;

/**
 * This class implements an advancement of user-based collaborative filtering in which neighbors of active user are reduced by most popular items.
 * The idea of neighbor reduction was proposed by the covering reduction collaborative filtering (CRFCF) algorithm.
 * CRFCF is developed by Zhipeng Zhang, Yao Zhang, Yonggong Ren.
 * 
 * @author Zhipeng Zhang, Yao Zhang, Yonggong Ren, Loc Nguyen.
 * @version 1.0
 *
 */
public class NeighborCFExtUserBasedEnhanced extends NeighborCFExtUserBased {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Rare threshold.
	 */
	public static final String POPULAR_THRESHOLD_FIELD = "popular_threshold";

	
	/**
	 * Default value for rare threshold.
	 */
	public static final double POPULAR_THRESHOLD_DEFAULT = 0.5;
	
	
	/**
	 * Rare item set.
	 */
	protected Set<Integer> rareSet = Util.newSet();
	

	/**
	 * Constructor.
	 */
	public NeighborCFExtUserBasedEnhanced() {

	}

	
	@Override
	public synchronized void setup(Dataset dataset, Object... params) throws RemoteException {
		super.setup(dataset, params);
		calcRareSet(dataset);
	}


	@Override
	public synchronized void unsetup() throws RemoteException {
		super.unsetup();
		rareSet.clear();
	}


	@Override
	public RatingVector estimate(RecommendParam param, Set<Integer> queryIds) throws RemoteException {
		if (param.ratingVector == null) return null;
		
		int knn = getConfig().getAsInt(KNN);
		knn = knn < 0 ? 0 : knn;
		boolean fastRecommend = getConfig().getAsBoolean(FAST_RECOMMEND) && knn > 0;

		RatingVector thisUser = param.ratingVector;
		RatingVector innerUser = getDataset().getUserRating(thisUser.id());
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
		
		boolean hybrid = getConfig().getAsBoolean(HYBRID);
		Profile thisProfile = hybrid ? param.profile : null;
		List<ObjectPair<RatingVector>> pairs = null;
		if (fastRecommend) {
			pairs = getKnnList(thisUser, thisProfile, queryIds, knn);
			if (pairs.size() == 0) return null;
		}
		
		RatingVector result = thisUser.newInstance(true);
		double minValue = getMinRating();
		double maxValue = getMaxRating();
		boolean isBoundedMinMax = isBoundedMinMaxRating();; 
		double thisMean = calcRowMean(thisUser);
		for (int itemId : queryIds) {
			if (thisUser.isRated(itemId)) {
				result.put(itemId, thisUser.get(itemId));
				continue;
			}
			
			if (!fastRecommend) {
				pairs = getKnnList(thisUser, thisProfile, Arrays.asList(itemId), knn);
				if (pairs.size() == 0) continue;
			}

			double accum = 0;
			double simTotal = 0;
			boolean calculated = false;
			for (ObjectPair<RatingVector> pair : pairs) {
				RatingVector thatUser = pair.key();
				if (!thatUser.isRated(itemId)) continue;
				
				double sim = pair.value();
				double deviate = thatUser.get(itemId).value - calcRowMean(thatUser);
				accum +=  sim * deviate;
				simTotal += Math.abs(sim);
				
				calculated = true;
			}
			if (!calculated) continue;
			
			double value = simTotal == 0 ? thisMean : thisMean + accum / simTotal;
			value = isBoundedMinMax ? Math.min(value, maxValue) : value;
			value = isBoundedMinMax ? Math.max(value, minValue) : value;
			result.put(itemId, value);
		}
		
		return result.size() == 0 ? null : result;
	}


	/**
	 * Getting list of k nearest neighbors.
	 * @param thisUser this user rating vector.
	 * @param thisProfile this user profile.
	 * @param itemIds collection of item identifiers.
	 * @param knn the number of nearest neighbors.
	 * @return list of k nearest neighbors.
	 */
	protected List<ObjectPair<RatingVector>> getKnnList(RatingVector thisUser, Profile thisProfile, Collection<Integer> itemIds, int knn) {
		if (dataset == null || thisUser == null) return Util.newList();
		
		List<RatingVector> cn = Util.newList();
		Fetcher<RatingVector> userRatings = null;
		try {
			userRatings = dataset.fetchUserRatings();
			while (userRatings.next()) {
				RatingVector thatUser = userRatings.pick();
				if (thatUser == null || thatUser.id() == thisUser.id() || !thatUser.isRated())
					continue;
				
				if (itemIds == null || itemIds.size() == 0)
					cn.add(thatUser);
				else {
					for (int itemId : itemIds) {
						if (thatUser.isRated(itemId)) {
							cn.add(thatUser);
							break;
						}
					}
				}
			}
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		finally {
			try {
				if (userRatings != null) userRatings.close();
			} catch (Throwable e) {LogUtil.trace(e);}
		}
		
		List<RatingVector> nb = Util.newList();
		if (thisUser.id() >= 0 && !rareSet.isEmpty()) {
			for (RatingVector user : cn) {
				Set<Integer> cu = user.fieldIds(true);
				cu.retainAll(rareSet);
				if (!cu.isEmpty()) {
					nb.add(user);
				}
			}
		}

		knn = knn < 0 ? 0 : knn;
		List<ObjectPair<RatingVector>> pairs = Util.newList(knn);
		boolean hybrid = getConfig().getAsBoolean(HYBRID);
		thisProfile = hybrid ? thisProfile : null;
		Map<Integer, Double> localUserSimCache = Util.newMap(knn);
		try {
			for (RatingVector thatUser : nb) {
				Profile thatProfile = hybrid ? dataset.getUserProfile(thatUser.id()) : null;
				
				// computing similarity
				double sim = Constants.UNUSED;
				if (isCached() && isCachedSim() && thisUser.id() < 0) { //Local caching
					if (localUserSimCache.containsKey(thatUser.id()))
						sim = localUserSimCache.get(thatUser.id());
					else {
						sim = sim(thisUser, thatUser, thisProfile, thatProfile);
						localUserSimCache.put(thatUser.id(), sim);
					}
				}
				else
					sim = sim(thisUser, thatUser, thisProfile, thatProfile);
				if (!Util.isUsed(sim)) continue;
				
				int found = ObjectPair.findIndexOfLessThan(sim, pairs);
				ObjectPair<RatingVector> pair = new ObjectPair<RatingVector>(thatUser, sim);
				if (found == -1) {
					if (knn == 0 || pairs.size() < knn) pairs.add(pair);
				}
				else {
					pairs.add(found, pair);
					if (knn > 0 && pairs.size() > knn) pairs.remove(pairs.size() - 1);
				}
			}
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		localUserSimCache.clear();
		
		return pairs;
	}
	
	
	/**
	 * Calculating rare item set from dataset.
	 * @param dataset specified dataset.
	 */
	protected void calcRareSet(Dataset dataset) {
		if (dataset == null) return;
		
		rareSet.clear();
		DatasetMetadata2 dm = DatasetMetadata2.create(dataset);
		double popular = config.getAsReal(POPULAR_THRESHOLD_FIELD);
		popular = Util.isUsed(popular) ? popular : POPULAR_THRESHOLD_DEFAULT; 
		double threshold = popular * dm.itemAverageRatingCount;
		
		Fetcher<RatingVector> vRatings = null;
		try {
			vRatings = dataset.fetchItemRatings();
			while (vRatings.next()) {
				RatingVector item = vRatings.pick();
				if (item == null) continue;
				
				if ((double)item.count(true) < threshold) rareSet.add(item.id());
			}
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		finally {
			try {
				if (vRatings != null) vRatings.reset();
			} catch (Throwable e) {LogUtil.trace(e);}
		}
		
	}
	
	
	@Override
	public String getName() {
		return "neighborcf_userbased_enhanced";
	}


	@Override
	public String getDescription() throws RemoteException {
		return "Enhanced user-based nearest neighbors collaborative filtering algorithm";
	}
	
	
	@Override
	public DataConfig createDefaultConfig() {
		DataConfig config = super.createDefaultConfig();
		config.put(POPULAR_THRESHOLD_FIELD, POPULAR_THRESHOLD_DEFAULT);
		return config;
	}


	/**
	 * Getting list of k nearest neighbors.
	 * @param thisUser this user rating vector.
	 * @param thisProfile this user profile.
	 * @param itemId item identifier.
	 * @param knn the number of nearest neighbors.
	 * @return list of k nearest neighbors.
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private List<ObjectPair<RatingVector>> getKnnList0(RatingVector thisUser, Profile thisProfile, Collection<Integer> itemIds, int knn) {
		if (dataset == null || thisUser == null) return Util.newList();
		
		List<RatingVector> cn = Util.newList();
		Fetcher<RatingVector> userRatings = null;
		try {
			userRatings = dataset.fetchUserRatings();
			while (userRatings.next()) {
				RatingVector thatUser = userRatings.pick();
				if (thatUser == null || thatUser.id() == thisUser.id() || !thatUser.isRated())
					continue;
				
				if (itemIds == null || itemIds.size() == 0)
					cn.add(thatUser);
				else {
					for (int itemId : itemIds) {
						if (thatUser.isRated(itemId)) {
							cn.add(thatUser);
							break;
						}
					}
				}
			}
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		finally {
			try {
				if (userRatings != null) userRatings.close();
			} catch (Throwable e) {LogUtil.trace(e);}
		}
		
		List<RatingVector> nb = Util.newList();
		if (thisUser.id() >= 0) {
			List<Set<Integer>> C = Util.newList();
			Set<Integer> unionC = Util.newSet();
			for (RatingVector user : cn) {
				Set<Integer> cu = user.fieldIds(true);
				cu.retainAll(rareSet);
				if (!cu.isEmpty()) {
					C.add(cu);
					unionC.addAll(cu);
				}
			}
			
			Set<Integer> ns = Util.newSet();
			ns.addAll(rareSet);
			ns.removeAll(unionC);
			if (!ns.isEmpty()) C.add(ns);
			C = cra(C);
			
			for (RatingVector user : cn) {
				Set<Integer> ids0 = user.fieldIds(true);
				for (Set<Integer> cu : C) {
					Set<Integer> ids = Util.newSet(ids0.size());
					ids.addAll(ids0);
					ids.retainAll(cu);
					if (!ids.isEmpty()) {
						nb.add(user);
						break;
					}
				}
			}
		}

		if (nb.size() == 0)
			nb = cn;
		
		knn = knn < 0 ? 0 : knn;
		List<ObjectPair<RatingVector>> pairs = Util.newList(knn);
		boolean hybrid = getConfig().getAsBoolean(HYBRID);
		thisProfile = hybrid ? thisProfile : null;
		Map<Integer, Double> localUserSimCache = Util.newMap(knn);
		try {
			for (RatingVector thatUser : nb) {
				Profile thatProfile = hybrid ? dataset.getUserProfile(thatUser.id()) : null;
				
				// computing similarity
				double sim = Constants.UNUSED;
				if (isCached() && isCachedSim() && thisUser.id() < 0) { //Local caching
					if (localUserSimCache.containsKey(thatUser.id()))
						sim = localUserSimCache.get(thatUser.id());
					else {
						sim = sim(thisUser, thatUser, thisProfile, thatProfile);
						localUserSimCache.put(thatUser.id(), sim);
					}
				}
				else
					sim = sim(thisUser, thatUser, thisProfile, thatProfile);
				if (!Util.isUsed(sim)) continue;
				
				int found = ObjectPair.findIndexOfLessThan(sim, pairs);
				ObjectPair<RatingVector> pair = new ObjectPair<RatingVector>(thatUser, sim);
				if (found == -1) {
					if (knn == 0 || pairs.size() < knn) pairs.add(pair);
				}
				else {
					pairs.add(found, pair);
					if (knn > 0 && pairs.size() > knn) pairs.remove(pairs.size() - 1);
				}
			}
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		localUserSimCache.clear();
		
		return pairs;
	}

	
	/**
	 * Implementing the covering reduction algorithm (CRA) to extract an irreducible covering of the specified covering.
	 * @param C specified covering.
	 * @return irreducible covering.
	 */
	@Deprecated
	private List<Set<Integer>> cra(List<Set<Integer>> C) {
		if (C == null) return Util.newList();
		
		List<Set<Integer>> RC = Util.newList();
		while (C.size() > 0) {
			Set<Integer> c = C.remove(0);
			if (c.isEmpty()) continue;
			
			boolean contained = false;
			for (Set<Integer> rc : RC) {
				if (rc.containsAll(c)) {
					contained = true;
					break;
				}
			}
			
			if (!contained) RC.add(c);
		}
		
		return RC;
	}


}
