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
import net.hudup.core.logistic.BaseClass;
import net.hudup.core.logistic.NextUpdate;

/**
 * This algorithm simply recommend items to users based on mean item rating.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@NextUpdate
@BaseClass //It is not base class. This annotation is used for not storing this CF into plug-in.
public class MeanItemCF extends StatCF {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "mean_item";
	}

	
	@Override
	public String getDescription() throws RemoteException {
		// TODO Auto-generated method stub
		return "Mean item collaborative filtering algorithm";
	}


	@Override
	public synchronized RatingVector estimate(RecommendParam param, Set<Integer> queryIds) throws RemoteException {
		// TODO Auto-generated method stub
		StatKB sKb = (StatKB)kb;
		
		RatingVector result = param.ratingVector.newInstance(true);
		for (int queryId : queryIds) {
			Stat stat = null;
			
			stat = sKb.itemStats.get(queryId);
			
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
		return new MeanItemCF();
	}

	
}
