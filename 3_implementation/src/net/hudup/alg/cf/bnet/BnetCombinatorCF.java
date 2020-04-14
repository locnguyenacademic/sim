package net.hudup.alg.cf.bnet;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Set;

import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.AlgList;
import net.hudup.core.alg.CompositeRecommenderAbstract;
import net.hudup.core.alg.RecommendFilterParam;
import net.hudup.core.alg.RecommendParam;
import net.hudup.core.alg.Recommender;
import net.hudup.core.alg.cf.mf.SvdGradientCF;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.RatingVector;
import net.hudup.core.logistic.Inspector;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.RatingFilter;
import net.hudup.core.logistic.ValueTriple;
import net.hudup.evaluate.ui.EvaluateGUI;

/**
 * This class implements composite collaborative filtering algorithm based on Bayesian network.
 * It uses another collaborative filtering algorithm based on Bayesian network.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@Deprecated
public class BnetCombinatorCF extends CompositeRecommenderAbstract {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Class of collaborative filtering algorithm based on Bayesian network.
	 */
	public final static Class<? extends BnetCFAbstract> BNET_CF_CLASS = BnetClusteredCF.class;

	
	/**
	 * Class of other recommendation algorithm.
	 */
	public final static Class<? extends Recommender> OTHER_RECOMMENDER_CLASS = SvdGradientCF.class;

	
	/**
	 * Default constructor.
	 */
	public BnetCombinatorCF() {
		super();
		// TODO Auto-generated constructor stub
	}


	/**
	 * Estimating rating values.
	 * @param param specified recommendation parameter.
	 * @param queryIds specified query identifiers (IDs). Such IDs can be item IDs or user IDs.
	 * @param referredRatingValue referred rating value.
	 * @param ratingFilter specified rating filter.
	 * @return list of {@link ValueTriple} (s).
	 * @throws RemoteException if any error raises.
	 */
	protected List<ValueTriple> estimate(RecommendParam param, Set<Integer> queryIds, double referredRatingValue, RatingFilter ratingFilter) throws RemoteException {
		// TODO Auto-generated method stub
		
		List<ValueTriple> triples = getBnetCF().bnetEstimate(param, queryIds, referredRatingValue, ratingFilter);
		if (triples.size() == 0)
			return Util.newList();
		
		Recommender recommender = getOtherRecommender();
		RatingVector vRating = recommender.estimate(param, queryIds);
		for (ValueTriple triple : triples) {
			double estimatedValue = Constants.UNUSED;
			if (vRating != null) {
				int itemId = triple.key();
				estimatedValue = vRating.contains(itemId) ? vRating.get(itemId).value : Constants.UNUSED;
			}
			
			double value = Util.isUsed(estimatedValue) ? (triple.getValue1() + estimatedValue) / 2 : triple.getValue1();
			triple.setValue1(value);
		}
		
		return triples;
	}

	
	@Override
	public synchronized RatingVector estimate(RecommendParam param, Set<Integer> queryIds) throws RemoteException {
		
		List<ValueTriple> triples = estimate(param, queryIds, Constants.UNUSED, null);
		if (triples == null || triples.size() == 0)
			return null;
		
		RatingVector result = param.ratingVector.newInstance(true);
		ValueTriple.fillByValue1(result, triples);
		
		return result;
	}
	
	
	@Override
	public synchronized RatingVector recommend(RecommendParam param, int maxRecommend) throws RemoteException {
		// TODO Auto-generated method stub
		param = recommendPreprocess(param);
		if (param == null)
			return null;
		
		filterList.prepare(param);
		Set<Integer> totalItemIds = getBnetCF().getItemIds();
		Set<Integer> queryIds = Util.newSet();
		for (int itemId : totalItemIds) {
			
			if ( !param.ratingVector.isRated(itemId) && filterList.filter(getDataset(), RecommendFilterParam.create(itemId)) )
				queryIds.add(itemId);
		}
		
		double avgRating = (config.getMaxRating() + config.getMinRating()) / 2.0; 
		List<ValueTriple> triples = estimate(param, queryIds, avgRating, new RatingFilter() {

			@Override
			public boolean accept(double ratingValue, double referredRatingValue) {
				// TODO Auto-generated method stub
				return ratingValue >= referredRatingValue;
			}
			
		});
		if (triples == null || triples.size() == 0)
			return null;
		
		ValueTriple.sortByValue1(triples, true, maxRecommend);
		
		RatingVector result = param.ratingVector.newInstance(true);
		ValueTriple.fillByValue1(result, triples);
		
		return result;
	}

	
	@Override
	public DataConfig createDefaultConfig() {
		// TODO Auto-generated method stub
		DataConfig config = super.createDefaultConfig();
		
		try {
			AlgList algList = new AlgList();

			BnetCFAbstract bnetCF = BNET_CF_CLASS.newInstance();
			bnetCF.getConfig().setStoreUri(config.getStoreUri().concat(bnetCF.getName()));
			algList.add(bnetCF);
			
			Recommender otherRecommender = OTHER_RECOMMENDER_CLASS.newInstance();
			otherRecommender.getConfig().setStoreUri(config.getStoreUri().concat(otherRecommender.getName()));
			algList.add(otherRecommender);

			config.put(INNER_RECOMMENDER, algList);
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}

		
		return config;
	}


	/**
	 * Getting collaborative filtering algorithm based on Bayesian network.
	 * @return collaborative filtering algorithm based on Bayesian network.
	 */
	private BnetCFAbstract getBnetCF() {
		AlgList innerRecommenders = getInnerRecommenders();
		for (int i = 0; i < innerRecommenders.size(); i++) {
			Recommender recommender = (Recommender)innerRecommenders.get(i);
			if (recommender instanceof BnetCFAbstract)
				return (BnetCFAbstract) recommender;
		}
		
		return null;
	}
	
	
	/**
	 * Getting other recommender algorithm.
	 * @return other recommender algorithm.
	 */
	private Recommender getOtherRecommender() {
		AlgList innerRecommenders = getInnerRecommenders();
		for (int i = 0; i < innerRecommenders.size(); i++) {
			Recommender recommender = (Recommender)innerRecommenders.get(i);
			if (!(recommender instanceof BnetCFAbstract))
				return recommender;
		}
		
		return null;
	}
	
	
	@Override
	public Dataset getDataset() throws RemoteException {
		// TODO Auto-generated method stub
		BnetCFAbstract bnetCF = getBnetCF();
		if (bnetCF != null)
			return bnetCF.getDataset();
		else
			return null;
	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new BnetCombinatorCF();
	}


	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "bayesnet_combinator";
	}


	@Override
	public String getDescription() throws RemoteException {
		// TODO Auto-generated method stub
		return "Collaborative filtering algorithm by combining other Bayesian network collaborative filtering algorithms";
	}


	@Override
	public Inspector getInspector() {
		// TODO Auto-generated method stub
		return EvaluateGUI.createInspector(this);
	}

	
}
