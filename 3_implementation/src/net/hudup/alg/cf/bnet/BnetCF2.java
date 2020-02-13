package net.hudup.alg.cf.bnet;

import java.rmi.RemoteException;
import java.util.List;

import elvira.Bnet;
import net.hudup.alg.cf.stat.MeanItemCF;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.KBase;
import net.hudup.core.alg.Recommender;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.logistic.NextUpdate;
import net.hudup.core.logistic.xURI;
import net.hudup.logistic.inference.BnetLearner;

/**
 * This class implements collaborative filtering based on Bayesian network.
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
@Deprecated
public class BnetCF2 extends BnetCF {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default dimension reducing ratio.
	 */
	public final static double DEFAULT_DIM_REDUCE_RATIO = 0.9;
	

	/**
	 * Default completing rating matrix method. 
	 */
	public final static Class<? extends Recommender> DEFAULT_COMPLETE_METHOD_CLASS = MeanItemCF.class;

	
	/**
	 * Default constructor.
	 */
	public BnetCF2() {
		super();
		// TODO Auto-generated constructor stub
	}


	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "bayesnet2";
	}

	
	@Override
	public String getDescription() throws RemoteException {
		// TODO Auto-generated method stub
		return "Bayesian network collaborative filtering algorithm (2)";
	}


	@Override
	public KBase newKB() throws RemoteException {
		// TODO Auto-generated method stub
		return BnetKB2.create(this);
	}


	@Override
	public DataConfig createDefaultConfig() {
		DataConfig config = super.createDefaultConfig();
		
		config.put(BnetKB2.DIM_REDUCE_RATIO, new Double(DEFAULT_DIM_REDUCE_RATIO));
		
		try {
			Alg completeMethod = DEFAULT_COMPLETE_METHOD_CLASS.newInstance();
			DataConfig completeMethodConfig = completeMethod.getConfig();
			xURI subStore = config.getStoreUri().concat(completeMethod.getName());
			completeMethodConfig.setStoreUri(subStore);
			
			config.put(BnetKB2.COMPLETE_METHOD, completeMethod);
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return config;
	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new BnetCF2();
	}

	
}


/**
 * This is knowledge base for collaborative filtering algorithm based on Bayesian network.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@NextUpdate
abstract class BnetKB2 extends BnetKB {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Dimension reducing ratio.
	 */
	public final static String DIM_REDUCE_RATIO = "dim_reduce_ratio";
	
	
	/**
	 * Completing rating matrix method.
	 */
	public final static String COMPLETE_METHOD = "complete_method";

	
	/**
	 * Default constructor.
	 */
	protected BnetKB2() {
		
	}
	
	
	@Override
	protected void learnBnet(Dataset dataset) {
		bnetList = BnetLearner.learning(
				dataset, 
				config.getAsInt(K2_MAX_PARENTS),
				config.getAsReal(DIM_REDUCE_RATIO),
				getCompleteMethod());
		
		itemIds.clear();
		for (Bnet bnet : bnetList) {
			List<Integer> ids = BnetUtil.itemIdListOf(bnet.getNodeList());
			itemIds.addAll(ids);
		}
		
	}
	
	
	/**
	 * Getting method of completing rating matrix.
	 * @return other collaborative filtering algorithm as completing method.
	 */
	protected Alg getCompleteMethod() {
		return (Alg) config.get(COMPLETE_METHOD);
	}
	
	
	/**
	 * Creating knowledge base from collaborative filtering algorithm based on Bayesian network.
	 * @param cf collaborative filtering algorithm based on Bayesian network.
	 * @return knowledge base from collaborative filtering algorithm based on Bayesian network.
	 */
	public static BnetKB2 create(final BnetCF2 cf) {
		return new BnetKB2() {

			/**
			 * Serial version UID for serializable class. 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return cf.getName();
			}
			
		};
		
	}
	

}


