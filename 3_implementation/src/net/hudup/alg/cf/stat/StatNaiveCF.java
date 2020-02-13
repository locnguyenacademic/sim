package net.hudup.alg.cf.stat;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Set;

import net.hudup.core.alg.Alg;
import net.hudup.core.alg.RecommendParam;
import net.hudup.core.data.Rating;
import net.hudup.core.data.RatingVector;

/**
 * This class is the naive statistics CF algorithms.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@Deprecated
public class StatNaiveCF extends StatCF {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public StatNaiveCF() {
		super();
		// TODO Auto-generated constructor stub
	}


	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "stat_naive";
	}

	
	@Override
	public String getDescription() throws RemoteException {
		// TODO Auto-generated method stub
		return "Naive statistical collaborative filtering algorithm";
	}


	@Override
	public synchronized RatingVector estimate(RecommendParam param, Set<Integer> queryIds) throws RemoteException {
		// TODO Auto-generated method stub
		
		StatKB sKb = (StatKB)kb;
		double overMean = sKb.generalStat.mean;
		
		int count = 0;
		double overDevSum = 0;
		Collection<Rating> ratings = param.ratingVector.gets();
		for (Rating rating : ratings) {
			if (!rating.isRated())
				continue;
			
			overDevSum += rating.value - overMean;
			count ++;
		}
		
		double overDev = 0;
		if (count > 0)
			overDev = overDevSum / count;
		
		RatingVector result = param.ratingVector.newInstance(true);
		for (int queryId : queryIds) {
			double itemOverDev = 0;
			
			Stat itemStat = sKb.itemStats.get(queryId);
			if (itemStat != null)
				itemOverDev = itemStat.overDev;
			
			result.put(queryId, overMean + overDev + itemOverDev);
		}
		
		
		if (result.size() == 0)
			return null;
		else
			return result;
	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new StatNaiveCF();
	}

	
}
