/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.alg.cf.stat;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Set;

import net.hudup.core.Util;
import net.hudup.core.alg.KBase;
import net.hudup.core.alg.RecommendFilterParam;
import net.hudup.core.alg.RecommendParam;
import net.hudup.core.alg.cf.ModelBasedCFAbstract;
import net.hudup.core.alg.cf.mf.SvdGradientKB;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Pair;
import net.hudup.core.data.RatingVector;
import net.hudup.core.evaluate.recommend.Accuracy;
import net.hudup.core.logistic.BaseClass;
import net.hudup.core.logistic.Inspector;
import net.hudup.core.logistic.NextUpdate;
import net.hudup.evaluate.ui.EvaluateGUI;

/**
 * This class implements a collaborative filtering algorithm based on Bayesian lookup table.
 * It is called Bayesian Lookup Table CF algorithm.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@NextUpdate
@BaseClass //It is not base class. This annotation is used for not storing this CF into plug-in.
public class BayesLookupTableCF extends ModelBasedCFAbstract {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default precision.
	 */
	public final static double DEFAULT_PRECISION = 0.80; // 20% deviation ratio
	
    
	/**
	 * Default maximum iteration.
	 */
	public final static int DEFAULT_MAX_ITERATION = 100;

	
	/**
	 * Default constructor.
	 */
	public BayesLookupTableCF() {
		super();
	}


	@Override
	public KBase newKB() throws RemoteException {
		return BayesLookupTableKB.create(this);
	}

	
	@Override
	public synchronized RatingVector estimate(RecommendParam param, Set<Integer> queryIds) throws RemoteException {
		BayesLookupTableKB kb = (BayesLookupTableKB) getKBase();
		if (kb.isEmpty())
			return null;
		
		RatingVector result = param.ratingVector.newInstance(true);
		
		int userId = result.id();
		for (int queryId : queryIds) {
			double ratingValue = kb.estimate(userId, queryId);
			if (Util.isUsed(ratingValue))
				result.put(queryId, ratingValue);
		}
		
		if (result.size() == 0)
			return null;
		else
			return result;
	}


	@Override
	public synchronized RatingVector recommend(RecommendParam param, int maxRecommend) throws RemoteException {
		BayesLookupTableKB kb = (BayesLookupTableKB) getKBase();
		if (kb.isEmpty())
			return null;

		param = recommendPreprocess(param);
		if (param == null)
			return null;
		
		filterList.prepare(param);
		List<Integer> itemIds = kb.getItemIds();
		Set<Integer> queryIds = Util.newSet();
		for (int itemId : itemIds) {
			
			if ( !param.ratingVector.isRated(itemId) && filterList.filter(getDataset(), RecommendFilterParam.create(itemId)) )
				queryIds.add(itemId);
		}
		
		double maxRating = getMaxRating();
		int userId = param.ratingVector.id();
		
		List<Pair> pairs = Util.newList();
		for (int itemId : queryIds) {
			
			double value = kb.estimate(userId, itemId);
			if (!Util.isUsed(value) || !Accuracy.isRelevant(value, this))
				continue;
			
			// Finding maximum rating
			int found = Pair.findIndexOfLessThan(value, pairs);
			Pair pair = new Pair(itemId, value);
			if (found == -1)
				pairs.add(pair);
			else 
				pairs.add(found, pair);
			
			int n = pairs.size();
			// Having maxRecommend + 1 if all are maximum rating.
			if (maxRecommend > 0 && n >= maxRecommend) {
				int lastIndex = n - 1;
				Pair last = pairs.get(lastIndex);
				if (last.value() == maxRating)
					break;
				else if (n > maxRecommend)
					pairs.remove(lastIndex);
			}
			
		}
		
		int n = pairs.size();
		if (maxRecommend > 0 && n > maxRecommend) {
			if (pairs.size() == maxRecommend + 1)
				pairs.remove(n - 1); //Remove the redundant recommended item because the pair list has almost maxRecommend + 1 pairs.
			else
				pairs = pairs.subList(0, maxRecommend); //The pair list has at most maxRecommend + 1 pairs and so this code line is for safe.
		}
		if (pairs.size() == 0)
			return null;
		
		RatingVector rec = param.ratingVector.newInstance(true);
		Pair.fillRatingVector(rec, pairs);
		return rec;
	}


//	/**
//	 * This is backup recommendation method. It is not used in current implementation.
//	 * @param param recommendation parameter. Please see {@link RecommendParam} for more details of this parameter.
//	 * @param maxRecommend the maximum recommended items (users) in the returned rating vector.
//	 * @return list of recommended items (users) which is provided to the user (item), represented by {@link RatingVector} class. The number of items (users) of such list is specified by the the maximum number. Return null if cannot estimate.
//	 * @throws RemoteException if any error raises.
//	 */
//	@SuppressWarnings("unused")
//	private synchronized RatingVector recommend0(RecommendParam param, int maxRecommend) throws RemoteException {
//		BayesLookupTableKB kb = (BayesLookupTableKB) getKBase();
//		if (kb.isEmpty())
//			return null;
//
//		param = recommendPreprocess(param);
//		if (param == null)
//			return null;
//		
//		filterList.prepare(param);
//		List<Integer> itemIds = kb.getItemIds();
//		Set<Integer> queryIds = Util.newSet();
//		for (int itemId : itemIds) {
//			
//			if ( !param.ratingVector.isRated(itemId) && filterList.filter(getDataset(), RecommendFilterParam.create(itemId)) )
//				queryIds.add(itemId);
//		}
//		
//		double maxRating = getMaxRating();
//		int userId = param.ratingVector.id();
//		
//		List<Pair> pairs = Util.newList();
//		//int size = queryIds.size();
//		//int i = 0;
//		for (int itemId : queryIds) {
//			//i++;
//			
//			double value = kb.estimate(userId, itemId);
//			if (!Util.isUsed(value))
//				continue;
//			
//			// Finding maximum rating
//			int found = Pair.findIndexOfLessThan(value, pairs);
//			Pair pair = new Pair(itemId, value);
//			if (found == -1)
//				pairs.add(pair);
//			else 
//				pairs.add(found, pair);
//			
//			int n = pairs.size();
//			// Having maxRecommend + 1 if all are maximum rating.
//			if (maxRecommend > 0 && n >= maxRecommend) {
//				int lastIndex = pairs.size() - 1;
//				Pair last = pairs.get(lastIndex);
//				if (last.value() == maxRating /*|| i >= size*/)
//					break;
//				else if (n > maxRecommend)
//					pairs.remove(lastIndex);
//			}
//			
//		}
//		
//		if (maxRecommend > 0 && pairs.size() > maxRecommend) {
//			if (pairs.size() == maxRecommend + 1)
//				pairs.remove(pairs.size() - 1); //Remove the redundant recommended item because the pair list has almost maxRecommend + 1 pairs.
//			else
//				pairs = pairs.subList(0, maxRecommend); //The pair list has at most maxRecommend + 1 pairs and so this code line is for safe.
//		}
//		if (pairs.size() == 0)
//			return null;
//		
//		RatingVector rec = param.ratingVector.newInstance(true);
//		Pair.fillRatingVector(rec, pairs);
//		return rec;
//	}

	
	@Override
	public String getName() {
		return "bayes_lookup_table";
	}

	
	@Override
	public String getDescription() throws RemoteException {
		return "Bayesian lookup table collaborative filtering algorithm";
	}


	@Override
	public Inspector getInspector() {
		return EvaluateGUI.createInspector(this);
	}

	
	@Override
	public DataConfig createDefaultConfig() {
		DataConfig tempConfig = super.createDefaultConfig();

		DataConfig config = new DataConfig() {

			/**
			 * Serial version UID for serializable class. 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean validate(String key, Serializable value) {
				
				if (value instanceof Number) {
					double valueNumber = ((Number)value).doubleValue();
					
					if (valueNumber < 0)
						return false;
				}
				
				return true;
			}
			
		};
		
//		xURI store = xURI.create(Constants.KNOWLEDGE_BASE_DIRECTORY).concat(getName());
//		config.setStoreUri(store);
		config.putAll(tempConfig);
		
		config.put(SvdGradientKB.PRECISION, DEFAULT_PRECISION);
		config.put(SvdGradientKB.MAX_ITERATION, DEFAULT_MAX_ITERATION);
		
		return config;
	}

	
}
