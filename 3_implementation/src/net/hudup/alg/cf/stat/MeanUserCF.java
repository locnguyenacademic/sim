/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.alg.cf.stat;

import java.rmi.RemoteException;
import java.util.Set;

import net.hudup.core.alg.Alg;
import net.hudup.core.alg.RecommendParam;
import net.hudup.core.data.RatingVector;

/**
 * This algorithm simply recommend items to users based on mean user rating.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@Deprecated
public class MeanUserCF extends StatCF {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "mean_user";
	}

	
	@Override
	public String getDescription() throws RemoteException {
		// TODO Auto-generated method stub
		return "Mean user collaborative filtering algorithm";
	}


	@Override
	public synchronized RatingVector estimate(RecommendParam param, Set<Integer> queryIds) throws RemoteException {
		// TODO Auto-generated method stub
		StatKB sKb = (StatKB)kb;
		
		RatingVector result = param.ratingVector.newInstance(true);
		for (int queryId : queryIds) {
			Stat stat = null;
			
			stat = sKb.userStats.get(queryId);
			
			if (stat != null)
				result.put(queryId, stat.mean);
		}
		
		
		if (result.size() == 0)
			return null;
		else
			return result;
	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new MeanUserCF();
	}

	
	
}
