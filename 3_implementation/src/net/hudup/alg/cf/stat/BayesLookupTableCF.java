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
import java.util.Set;

import net.hudup.core.Util;
import net.hudup.core.alg.KBase;
import net.hudup.core.alg.RecommendParam;
import net.hudup.core.alg.cf.ModelBasedCFAbstract;
import net.hudup.core.alg.cf.mf.SvdGradientKB;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.RatingVector;
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
