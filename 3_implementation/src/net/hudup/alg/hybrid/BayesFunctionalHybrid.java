/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.alg.hybrid;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.hudup.core.Util;
import net.hudup.core.alg.KBase;
import net.hudup.core.alg.KBaseAbstract;
import net.hudup.core.alg.cf.ModelBasedCFAbstract;
import net.hudup.core.logistic.NextUpdate;

/**
 * This class will implement a so-called hybrid Bayes functional CF algorithm.
 *  
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@NextUpdate
@Deprecated
public abstract class BayesFunctionalHybrid extends ModelBasedCFAbstract {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public BayesFunctionalHybrid() {
		super();
		// TODO Auto-generated constructor stub
	}


	@Override
	public KBase newKB() throws RemoteException {
		// TODO Auto-generated method stub
		return BayesFunctionalHybridKB.create(this);
	}

	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "bayes_functional";
	}

	
}


/**
 * Knowledge base of the hybrid Bayes functional CF algorithm.
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@NextUpdate
@Deprecated
abstract class BayesFunctionalHybridKB extends KBaseAbstract {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Maximum user features.
	 */
	public static final int MAX_USER_FEATURES = 10;
	
	
	/**
	 * Maximum item features.
	 */
	public static final int MAX_ITEM_FEATURES = 10;

	
	/**
	 * User features values.
	 */
	protected Map<Integer, List<Integer>> uFeatureValues = Util.newMap();

	
	/**
	 * Item features values.
	 */
	protected Map<Integer, List<Integer>> iFeatureValues = Util.newMap();

	
	/**
	 * Map of user feature parameters.
	 */
	protected Map<Integer, Map<Integer, Map<Integer, Double>>> uFeatureParams = Util.newMap();
	
	
	/**
	 * Map of item feature parameters.
	 */
	protected Map<Integer, Map<Integer, Map<Integer, Double>>> iFeatureParams = Util.newMap();


	/**
	 * Map of rating parameters.
	 */
	protected Map<Integer, Double> rParams = Util.newMap();
	
	
	/**
	 * Default constructors.
	 */
	public BayesFunctionalHybridKB() {
		
	}
	
	
	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		super.close();
		destroyDataStructure();
	}

	
	/**
	 * Getting list user feature id (s).
	 * @return list user feature id (s).
	 */
	protected Set<Integer> uFeatureIds() {
		return uFeatureValues.keySet();
	}
	
	
	/**
	 * Getting list item feature id (s).
	 * @return list item feature id (s).
	 */
	protected Set<Integer> iFeatureIds() {
		return iFeatureValues.keySet();
	}

	
	/**
	 * Getting possible rating (s).
	 * @return possible rating (s).
	 */
	protected Set<Integer> ratings() {
		return rParams.keySet();
	}
	
	
	/**
	 * Destroying data structure.
	 */
	private void destroyDataStructure() {
		uFeatureValues.clear();
		iFeatureValues.clear();
		uFeatureParams.clear();
		iFeatureParams.clear();
		rParams.clear();
	}


	@Override
	public boolean isEmpty() throws RemoteException {
		// TODO Auto-generated method stub
		return uFeatureParams.size() == 0 || iFeatureParams.size() == 0;
	}
	
	
	/**
	 * Create knowledge base of the hybrid Bayes functional CF algorithm.
	 * @param innerCf the hybrid Bayes functional CF algorithm.
	 * @return {@link BayesFunctionalHybrid}
	 */
	public static BayesFunctionalHybridKB create(final BayesFunctionalHybrid recommender) {
		
		return new BayesFunctionalHybridKB() {

			/**
			 * Serial version UID for serializable class. 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return recommender.getName();
			}
			
		};
	}


}
