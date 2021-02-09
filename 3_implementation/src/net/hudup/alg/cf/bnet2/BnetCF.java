/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.alg.cf.bnet2;

import java.rmi.RemoteException;
import java.util.Set;

import net.hudup.core.alg.KBase;
import net.hudup.core.alg.RecommendParam;
import net.hudup.core.alg.cf.ModelBasedCFAbstract;
import net.hudup.core.data.RatingVector;
import net.hudup.core.logistic.Inspector;
import net.hudup.core.logistic.NextUpdate;
import net.hudup.evaluate.ui.EvaluateGUI;

/**
 * This class represents a collaborative filtering algorithm based on Bayesian network.
 * 
 * @author ShahidNaseem, Anum Shafiq, Loc Nguyen
 * @version 1.0
 *
 */
@NextUpdate
@Deprecated //This class is not deprecated. The deprecated annotation is used for updating because this algorithm is will not registered in plugin.
public class BnetCF extends ModelBasedCFAbstract {

	
	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;

	
	@Override
	public RatingVector estimate(RecommendParam param, Set<Integer> queryIds) throws RemoteException {
		//Coding here, estimating missing values.
		return null;
	}

	
	@Override
	public RatingVector recommend(RecommendParam param, int maxRecommend) throws RemoteException {
		//Coding here, recommending items.
		return null;
	}

	
	@Override
	public KBase newKB() throws RemoteException {
		return new BnetKB();
	}

	
	@Override
	public String getName() {
		return "IETI.bayesnet";
	}

	
	@Override
	public String getDescription() throws RemoteException {
		return "Bayesian network collaborative filtering algorithm";
	}


	@Override
	public Inspector getInspector() {
		return EvaluateGUI.createInspector(this);
	}

	
}
