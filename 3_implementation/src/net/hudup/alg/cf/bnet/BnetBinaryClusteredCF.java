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
import java.util.Set;

import elvira.Bnet;
import net.hudup.core.Util;
import net.hudup.core.alg.KBase;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.Pair;
import net.hudup.core.data.bit.BitData;
import net.hudup.core.data.bit.BitItem;
import net.hudup.core.logistic.NextUpdate;
import net.hudup.core.logistic.SystemUtil;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.logistic.inference.BnetBinaryLearner;

/**
 * This class implements collaborative filtering algorithm based on clustered binary Bayesian networks.
 * Note, Bayesian networks are clustered.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@NextUpdate
@Deprecated
public class BnetBinaryClusteredCF extends BnetBinaryCF {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public BnetBinaryClusteredCF() {
		
	}
	
	
	@Override
	public KBase newKB() throws RemoteException {
		// TODO Auto-generated method stub
		return BnetBinaryClusteredKB.create(this);
	}
	

	@Override
	protected Bnet chooseBnet(Collection<Integer> bitItemIds) {
		// TODO Auto-generated method stub
		
		BnetBinaryClusteredKB bbcKb = (BnetBinaryClusteredKB)kb; 
		List<Bnet> bnetList =  bbcKb.getBnetList();
		
		int maxBnetCount = 0;
		int maxBnetIdx = -1;
		for (int bnetIdx = 0; bnetIdx < bnetList.size(); bnetIdx++) {
			int count = BnetUtil.countForBnetIdx(bbcKb.MT, bnetIdx, bitItemIds);
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
	public String getName() {
		// TODO Auto-generated method stub
		return "bayesnet_binary_clustered";
	}


	@Override
	public String getDescription() throws RemoteException {
		// TODO Auto-generated method stub
		return "Binary clustered Bayesian network collaborative filtering algorithm";
	}


}



/**
 * This is knowledge base of collaborative filtering algorithm based on clustered binary Bayesian networks.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@NextUpdate
abstract class BnetBinaryClusteredKB extends BnetBinaryKB {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Mapping table of bit set created from Bayesian networks..
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
		
		BitData bitData = BitData.create(dataset);
		
		bnetList = BnetBinaryLearner.learning_clustered(
				bitData, 
				config.getAsInt(K2_MAX_PARENTS));
		
		bitItemMap.clear();
		itemIds.clear();
		Set<Integer> bitItemIds = bitData.bitItemIds();
		for (int bitItemId : bitItemIds) {
			BitItem item = bitData.get(bitItemId);
			Pair pair = item.pair();
			
			bitItemMap.put(bitItemId, pair);
			itemIds.add(pair.key());
		}
		
		MT = BnetUtil.createMT(bnetList);
		
		
		bitData.clear();
		bitData = null;
		SystemUtil.enhance();
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
	public static BnetBinaryClusteredKB create(final BnetBinaryClusteredCF cf) {
		return new BnetBinaryClusteredKB() {

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
