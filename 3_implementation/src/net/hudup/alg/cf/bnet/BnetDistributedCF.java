/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.alg.cf.bnet;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import elvira.Bnet;
import elvira.Evidence;
import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.KBase;
import net.hudup.core.alg.KBaseAbstract;
import net.hudup.core.alg.RecommendParam;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.logistic.NextUpdate;
import net.hudup.core.logistic.RatingFilter;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.ValueTriple;
import net.hudup.core.logistic.xURI;
import net.hudup.core.parser.TextParserUtil;
import net.hudup.logistic.inference.BnetDistributedLearner;

/**
 * This class implements collaborative filtering algorithm based on distributed Bayesian networks.
 * 
 * @author Loc Nguyen
 * @version 10.0
 * 
 */
@NextUpdate
@Deprecated
public class BnetDistributedCF extends BnetCFAbstract {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public BnetDistributedCF() {
		super();
	}
	
	
	@Override
	protected List<ValueTriple> bnetEstimate(RecommendParam param, Set<Integer> queryIds, double referredRatingValue, RatingFilter ratingFilter) {
		// TODO Auto-generated method stub
		
		List<ValueTriple> result = Util.newList();
		
		BnetDistributedKB bdKb = (BnetDistributedKB)kb;
		double minRating = getMinRating();
		
		for (int queryId : queryIds) {
			Bnet bnet = bdKb.getBnet(queryId);
			Set<Integer> newQueryId = Util.newSet();
			newQueryId.add(queryId);
			
			Evidence ev = BnetUtil.createItemEvidence(
					bnet.getNodeList(), 
					param.ratingVector, 
					minRating);
			
			List<ValueTriple> r = BnetUtil.inference(
	        		bnet, 
	        		ev, 
	        		newQueryId, 
	        		minRating,
	        		referredRatingValue,
	        		ratingFilter);
	        
	        if (r.size() == 0)
	            r = BnetUtil.inference(
	            	bnet, 
	            	new Evidence(), 
	            	newQueryId,
	        		minRating,
	        		referredRatingValue,
	        		ratingFilter);
	        
	        ValueTriple triple = ValueTriple.getByKey(r, queryId);
	        if (triple != null)
	        	result.add(triple);
	        
		}
		
		if (result.size() == 0)
			return null;
		else
			return result;
	}


	@Override
	protected Set<Integer> getItemIds() {
		// TODO Auto-generated method stub
		
		BnetDistributedKB bdKb = (BnetDistributedKB)kb;
		
		return bdKb.itemIds;
	}


	@Override
	public String getName() {
		return "bayesnet_distributed";
	}

	
	@Override
	public String getDescription() throws RemoteException {
		return "Distributed Bayesian network collaborative filtering algorithm";
	}


	@Override
	public KBase newKB() throws RemoteException {
		// TODO Auto-generated method stub
		return BnetDistributedKB.create(this);
	}


}


/**
 * This is knowledge base of collaborative filtering algorithm based on distributed Bayesian networks.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@NextUpdate
abstract class BnetDistributedKB extends KBaseAbstract {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * File extension of Bayesian network.
	 */
	public final static String BNET_FILEEXT = "elv";

	
	/**
	 * Map of distributed Bayesian networks.
	 */
	protected Map<Integer, Bnet> bnetMap = Util.newMap();

	
	/**
	 * Set of item identifiers.
	 */
	protected Set<Integer> itemIds = Util.newSet();

	
	/**
	 * Default constructor.
	 */
	private BnetDistributedKB() {
		
	}
	
	
	@Override
	public void load() throws RemoteException {
		// TODO Auto-generated method stub
		super.load();
		
		UriAdapter adapter = new UriAdapter(config);
		bnetMap = loadBnetMap(adapter, config.getStoreUri(), getName());
		adapter.close();
		
		itemIds = bnetMap.keySet();
	}

	
	@Override
	public void learn(Dataset dataset, Alg alg) throws RemoteException {
		// TODO Auto-generated method stub
		super.learn(dataset, alg);
		
		bnetMap = BnetDistributedLearner.createDistributedBnet(dataset);
		itemIds = bnetMap.keySet();
		
	}

	
	@Override
	public void save(DataConfig storeConfig) throws RemoteException {
		// TODO Auto-generated method stub
		
		super.save(storeConfig);
		
		UriAdapter adapter = new UriAdapter(storeConfig);
		saveBnetMap(adapter, bnetMap, storeConfig.getStoreUri(), getName());
		adapter.close();
	}

	
	@Override
	public boolean isEmpty() throws RemoteException {
		// TODO Auto-generated method stub
		return bnetMap.size() == 0;
	}

	
	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		super.close();
		
		bnetMap.clear();
		itemIds.clear();
	}


	/**
	 * Getting Bayesian network of given item identifier.
	 * @param id given item identifier.
	 * @return Bayesian network of given item identifier.
	 */
	protected Bnet getBnet(int id) {
		return bnetMap.get(id);
	}
	
	
	/**
	 * Create knowledge base of collaborative filtering algorithm based on distributed binary Bayesian network.
	 * @param cf collaborative filtering algorithm based on distributed binary Bayesian network.
	 * @return knowledge base of collaborative filtering algorithm based on distributed binary Bayesian network.
	 */
	public static BnetDistributedKB create(final BnetDistributedCF cf) {
		return new BnetDistributedKB() {

			
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
	

	/**
	 * Loading Bayesian networks from store.
	 * @param adapter URI adapter.
	 * @param store store URI.
	 * @param prefixName prefix of names of files.
	 * @return map of Bayesian networks from store.
	 */
	protected static Map<Integer, Bnet> loadBnetMap(UriAdapter adapter, xURI store, String prefixName) {
		
		List<xURI> uriList = BnetCFAbstract.getUriList(store, BNET_FILEEXT, prefixName, false);
		
		Map<Integer, Bnet> bnetMap = Util.newMap();
		for(xURI uri : uriList) {
			String name = uri.getLastName();
			String snum = name.substring(name.lastIndexOf(TextParserUtil.CONNECT_SEP) + 1, name.lastIndexOf("."));
			int id = Integer.parseInt(snum);
			
			Bnet bnet = BnetUtil.loadBnet(adapter, uri);
			if (bnet != null)
				bnetMap.put(id, bnet);
		}

		return bnetMap;
	}


	/**
	 * Saving a Bayesian network to store.
	 * @param adapter URI adapter.
	 * @param bnetMap map of Bayesian networks.
	 * @param store store URI.
	 * @param prefixName prefix of names of files.
	 */
	protected static void saveBnetMap(UriAdapter adapter, Map<Integer, Bnet> bnetMap, xURI store, String prefixName) {
		adapter.create(store, true);

		Set<Integer> ids = bnetMap.keySet();
		for (int id : ids) {
			Bnet bnet = bnetMap.get(id);
			String filename = prefixName + TextParserUtil.CONNECT_SEP + id + "." + BNET_FILEEXT;
			xURI uri = store.concat(filename);
			
			BnetUtil.saveBnet(adapter, bnet, uri);
					
		}
		
	} // saveBnet
	
	
}