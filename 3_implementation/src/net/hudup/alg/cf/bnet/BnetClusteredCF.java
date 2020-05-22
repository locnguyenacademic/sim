/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.alg.cf.bnet;

import java.rmi.RemoteException;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import elvira.Bnet;
import net.hudup.core.Util;
import net.hudup.core.alg.KBase;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.logistic.inference.BnetLearner;

/**
 * This class implements collaborative filtering algorithm based on clustered binary Bayesian networks.
 * Note, Bayesian networks are clustered.
 * 
 * @author Loc Nguyen
 * @version 10.0
 * 
 * 
 */
@Deprecated
public class BnetClusteredCF extends BnetCF2 {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default number of nodes in Bayesian network.
	 */
	public final static int DEFAULT_BNET_NODE_NUMBER = 0;


	/**
	 * Default constructor.
	 */
	public BnetClusteredCF() {
		super();
	}
	
	
	@Override
	public KBase newKB() throws RemoteException {
		// TODO Auto-generated method stub
		return BnetClusteredKB.create(this);
	}
	

	@Override
	protected Bnet chooseBnet(Collection<Integer> itemIds) {
		// TODO Auto-generated method stub
		
		BnetClusteredKB bcKb = (BnetClusteredKB)kb; 
		List<Bnet> bnetList =  bcKb.getBnetList();
		
		int maxBnetCount = 0;
		int maxBnetIdx = -1;
		for (int bnetIdx = 0; bnetIdx < bnetList.size(); bnetIdx++) {
			int count = BnetUtil.countForBnetIdx(bcKb.MT, bnetIdx, itemIds);
			if (maxBnetCount < count) {
				maxBnetCount = count;
				maxBnetIdx = bnetIdx;
			}
				
		}
		
		if (maxBnetCount == 0)
			return null;
		else
			return bnetList.get(maxBnetIdx);
	}


	@Override
	public DataConfig createDefaultConfig() {
		DataConfig config = super.createDefaultConfig();
		
		config.put(BnetClusteredKB.BNET_NODE_NUMBER, new Integer(DEFAULT_BNET_NODE_NUMBER));
		
		return config;
		
	}


	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "bayesnet_clustered";
	}

	
	@Override
	public String getDescription() throws RemoteException {
		// TODO Auto-generated method stub
		return "Clustered Bayesian network collaborative filtering algorithm";
	}


}



/**
 * This is knowledge base of collaborative filtering algorithm based on clustered Bayesian networks.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
abstract class BnetClusteredKB extends BnetKB2 {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Number of nodes in Bayesian network.
	 */
	public final static String BNET_NODE_NUMBER = "bnet_node_number";
	
	
	/**
	 * Map of bit set created from Bayesian network.
	 */
	protected Map<Integer, BitSet> MT = Util.newMap();

	
	@Override
	public void load() throws RemoteException {
		// TODO Auto-generated method stub
		super.load();
		
		UriAdapter adapter = new UriAdapter(config);
		MT = BnetUtil.loadMT(adapter, config.getStoreUri(), getName());
		adapter.close();
	}

	
	@Override
	protected void learnBnet(Dataset dataset) {
		bnetList = BnetLearner.learning_clustered(
				dataset, 
				config.getAsInt(K2_MAX_PARENTS),
				config.getAsInt(BNET_NODE_NUMBER),
				config.getAsReal(DIM_REDUCE_RATIO),
				getCompleteMethod());
		
		MT = BnetUtil.createMT(bnetList);
		
		itemIds.clear();
		for (Bnet bnet : bnetList) {
			List<Integer> ids = BnetUtil.itemIdListOf(bnet.getNodeList());
			itemIds.addAll(ids);
			
		}
		
	}
	
	
	@Override
	public void save(DataConfig storeConfig) throws RemoteException {
		// TODO Auto-generated method stub
		super.save(storeConfig);
		
		UriAdapter adapter = new UriAdapter(storeConfig);
		BnetUtil.saveMT(adapter, storeConfig.getStoreUri(), MT, getName());
		adapter.close();
	}

	
	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		super.close();
		
		MT.clear();
	}


	/**
	 * Create knowledge base of collaborative filtering algorithm based on clustered binary Bayesian network.
	 * @param cf collaborative filtering algorithm based on clustered binary Bayesian network.
	 * @return knowledge base of collaborative filtering algorithm based on clustered binary Bayesian network.
	 */
	public static BnetClusteredKB create(final BnetClusteredCF cf) {
		return new BnetClusteredKB() {

			
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



