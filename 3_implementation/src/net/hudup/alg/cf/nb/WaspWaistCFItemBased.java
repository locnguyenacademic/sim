/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.alg.cf.nb;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Set;

import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.alg.DuplicatableAlg;
import net.hudup.core.alg.RecommendParam;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.Profile;
import net.hudup.core.data.RatingVector;
import net.hudup.core.logistic.BaseClass;
import net.hudup.core.logistic.LogUtil;

/**
 * This abstract class implements the item-based Wasp Waist (WW) collaborative filtering algorithm.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
@BaseClass //It is not base class. The notation is used for later update.
public class WaspWaistCFItemBased extends WaspWaistCF implements DuplicatableAlg {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public WaspWaistCFItemBased() {

	}

	
	@Override
	public RatingVector estimate(RecommendParam param, Set<Integer> queryIds) throws RemoteException {
		if (param.ratingVector == null) return null;
		
		RatingVector result = param.ratingVector.newInstance(true);
		boolean hybrid = config.getAsBoolean(HYBRID);
		RatingVector thisUser = param.ratingVector;
		double minValue = getMinRating();
		double maxValue = getMaxRating();
		boolean isBoundedMinMax = isBoundedMinMaxRating();; 
		double simThreshold = getSimThreshold(config);
		Fetcher<RatingVector> itemRatings = dataset.fetchItemRatings();
		RatingVector crushedUser = crush(thisUser.id(), thisUser);
		for (int itemId : queryIds) {
			RatingVector thisItem = dataset.getItemRating(itemId);
			if (thisItem == null) continue;
			if (thisUser.isRated(itemId) && !thisItem.isRated(thisUser.id())) {
				thisItem = (RatingVector)thisItem.clone();
				thisItem.put(thisUser.id(), thisUser.get(itemId));
			}

			if (thisItem.isRated(thisUser.id())) {
				result.put(itemId, thisItem.get(thisUser.id()));
				continue;
			}
			
			Profile thisItemProfile = hybrid ? dataset.getItemProfile(itemId) : null;
			double thisMean = calcRowMean(thisItem);
			double accum = 0;
			double simTotal = 0;
			boolean calculated = false;
			try {
				while (itemRatings.next()) {
					RatingVector thatItem = itemRatings.pick();
					//That item can be this item because the crushed user can have estimated value for this item.
					//In the case that this item is that item, their similarity of course is highest.
					if (thatItem == null) continue;
					if (thisUser.isRated(thatItem.id()) && !thatItem.isRated(thisUser.id())) {
						thatItem = (RatingVector)thatItem.clone();
						thatItem.put(thisUser.id(), thisUser.get(thatItem.id()));
					}
					
					double thatValue = Constants.UNUSED;
					if (thatItem.isRated(thisUser.id()))
						thatValue = thatItem.get(thisUser.id()).value;
					else if (crushedUser != null && crushedUser.isRated(thatItem.id()))
						thatValue = crushedUser.get(thatItem.id()).value;
					else
						continue;

					Profile thatItemProfile = hybrid ? dataset.getItemProfile(thatItem.id()) : null;
					
					//Computing similarity
					double sim = sim(thisItem, thatItem, thisItemProfile, thatItemProfile, thisUser.id());
					if (!Util.isUsed(sim) || (Util.isUsed(simThreshold) && sim < simThreshold))
						continue;
					
					double deviate = thatValue - calcRowMean(thatItem);
					accum += sim * deviate;
					simTotal += Math.abs(sim);
					
					calculated = true;
				}
				itemRatings.reset();
			}
			catch (Throwable e) {
				LogUtil.trace(e);
			}
			if (!calculated) continue;
			
			double value = simTotal == 0 ? thisMean : thisMean + accum / simTotal;
			value = isBoundedMinMax ? Math.min(value, maxValue) : value;
			value = isBoundedMinMax ? Math.max(value, minValue) : value;
			result.put(itemId, value);
		}
		
		try {
			itemRatings.close();
		} 
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		
		return result.size() == 0 ? null : result;
	}


	@Override
	protected RatingVector crushAsUsual(int columnId, RatingVector userRating) {
		RatingVector thisUser = this.dataset.getUserRating(columnId);
		if (thisUser == null) return null;
		if ((!isCached()) && (userRating != null)) {
			Set<Integer> rowIds = userRating.fieldIds(true);
			rowIds.removeAll(thisUser.fieldIds(true));
			if (rowIds.size() > 0) thisUser = (RatingVector)thisUser.clone();
			for (int rowId : rowIds) {
				if (!thisUser.isRated(rowId))
					thisUser.put(rowId, userRating.get(rowId));
			}
		}
		RatingVector result = thisUser.compactClone();
		if (result.size() == 0) return null;
		
		Fetcher<RatingVector> vRatings = this.dataset.fetchUserRatings();
		List<Object[]> simList = Util.newList(0);
		try {
			while (vRatings.next()) {
				//That rating vector is often considered as to be not empty and not to have unrated value.
				RatingVector thatUser = vRatings.pick();
				if (thatUser == null || thatUser.id() == columnId)
					continue;
				
				double sim = dualCF.sim(thisUser, thatUser, (Profile)null, (Profile)null);
				if (Util.isUsed(sim))
					simList.add(new Object[] {thatUser, sim});
			}
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		try {
			vRatings.close();
		} 
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		
		if (simList.size() == 0) return result;
		
		double minValue = getMinRating();
		double maxValue = getMaxRating();
		boolean isBoundedMinMax = isBoundedMinMaxRating(); 
		double thisMean = calcColumnMean(thisUser);
		Set<Integer> itemIds = Util.newSet();
		itemIds.addAll(this.itemIds);
		itemIds.removeAll(thisUser.fieldIds(true));
		for (int itemId : itemIds) {
			double accum = 0;
			double simTotal = 0;
			boolean calculated = false;
			for (Object[] aSim : simList) {
				RatingVector thatUser = (RatingVector)aSim[0];
				if (!thatUser.isRated(itemId)) continue;
				
				double thatValue = thatUser.get(itemId).value;
				double thatMean = calcColumnMean(thatUser);
				double deviate = thatValue - thatMean;
				accum += (double)(aSim[1]) * deviate;
				simTotal += Math.abs((double)(aSim[1]));
				
				calculated = true;
			}
			if (!calculated) continue;
			
			double value = simTotal == 0 ? thisMean : thisMean + accum / simTotal;
			value = isBoundedMinMax ? Math.min(value, maxValue) : value;
			value = isBoundedMinMax ? Math.max(value, minValue) : value;
			result.put(itemId, value);
		}
		
		return result;
	}

	
	@Override
	protected NeighborCFExt createDualCF() {
		return new NeighborCFExtUserBased();
	}


	@Override
	protected double cod(
			RatingVector vRating1, RatingVector vRating2,
			Profile profile1, Profile profile2) {
		return cod(vRating1, vRating2, this.userMeans);
	}

	
	@Override
	protected double pip(RatingVector vRating1, RatingVector vRating2, Profile profile1, Profile profile2) {
		return pip(vRating1, vRating2, this.userMeans);
	}


	@Override
	protected double pss(RatingVector vRating1, RatingVector vRating2, Profile profile1, Profile profile2) {
		return pss(vRating1, vRating2, this.userMeans);
	}


	@Override
	protected double pc(RatingVector vRating1, RatingVector vRating2, Profile profile1,
			Profile profile2, int fixedColumnId) {
		return pc(vRating1, vRating2, fixedColumnId, this.userMeans);
	}

	
	@Override
	protected Set<Integer> getRowIds() {
		return itemIds;
	}

	
	@Override
	protected RatingVector getRowRating(int rowId) {
		return dataset.getItemRating(rowId);
	}

	
	@Override
	protected double calcRowMean(RatingVector vRating) {
		return calcMean(this, itemMeans, vRating);
	}


	@Override
	protected Set<Integer> getColumnIds() {
		return userIds;
	}

	
	@Override
	protected RatingVector getColumnRating(int fieldId) {
		return dataset.getUserRating(fieldId);
	}


	@Override
	protected double calcColumnMean(RatingVector vRating) {
		return calcMean(this, userMeans, vRating);
	}

	
	@Override
	public String getName() {
		String name = getConfig().getAsString(DUPLICATED_ALG_NAME_FIELD);
		if (name != null && !name.isEmpty())
			return name;
		else
			return "wwcf_itembased";
	}


	@Override
	public void setName(String name) {
		getConfig().put(DUPLICATED_ALG_NAME_FIELD, name);
	}

	
	@Override
	public String getDescription() throws RemoteException {
		return "Item-based Wasp Waist algorithm";
	}


	@Override
	public DataConfig createDefaultConfig() {
		DataConfig config = super.createDefaultConfig();
		config.addReadOnly(DUPLICATED_ALG_NAME_FIELD);
		return config;
	}

	
}
