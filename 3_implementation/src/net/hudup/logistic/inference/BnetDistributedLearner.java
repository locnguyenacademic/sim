package net.hudup.logistic.inference;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import elvira.Bnet;
import elvira.Link;
import elvira.LinkList;
import elvira.Node;
import elvira.NodeList;
import elvira.Relation;
import elvira.potential.PotentialTable;
import net.hudup.alg.cf.bnet.BnetUtil;
import net.hudup.core.Util;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.DatasetUtil2;
import net.hudup.core.data.FetcherUtil;
import net.hudup.core.data.Pair;
import net.hudup.logistic.math.DatasetStatsProcessor;

/**
 * This is utility class for learning distributed Bayesian network.
 * Each distributed Bayesian network has one root which is an item and many leaves which are attributes of such item or different items.
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
public class BnetDistributedLearner {

	
	/**
	 * Creating map of distributed Bayesian networks for specified dataset.
	 * @param dataset specified dataset.
	 * @return map of distributed Bayesian networks.
	 */
	public static Map<Integer, Bnet> createDistributedBnet(Dataset dataset) {
		Map<Integer, Bnet> bnetMap = Util.newMap();
		DatasetStatsProcessor util = new DatasetStatsProcessor(dataset);
		
		Set<Integer> itemIds = Util.newSet();
		FetcherUtil.fillCollection(itemIds, dataset.fetchItemIds(), true);
		for (int itemId : itemIds) {
			Bnet bnet = createDistributedBnet(util, itemIds, itemId, 
					dataset.getConfig().getNumberRatingsPerItem(), 
					(int)dataset.getConfig().getMinRating());
			
			if (bnet != null)
				bnetMap.put(itemId, bnet);
		}
		
		return bnetMap;
	}
	
	
	/**
	 * Creating map of distributed Bayesian networks from a root item id and many other item id (s).
	 * @param util utility class which calculates probabilities on dataset. 
	 * @param itemIds other item id (s).
	 * @param rootItemId root item id.
	 * @param numberRatingsPerItem number of ratings per item.
	 * @param minRating minimum rating value.
	 * @return map of distributed Bayesian networks created from a root item id and many other item id (s).
	 */
	private static Bnet createDistributedBnet(DatasetStatsProcessor util, Collection<Integer> itemIds, int rootItemId, 
			int numberRatingsPerItem, int minRating) {
		
		NodeList nodeList = new NodeList();
		
		Node root = BnetUtil.createItemNode(
				rootItemId, numberRatingsPerItem, minRating);
		
		for (int id : itemIds) {
			if (id == rootItemId)
				continue;
			
			Node node = BnetUtil.createItemNode(id, numberRatingsPerItem, minRating);
			LinkList linkList = new LinkList();
			Link link = new Link(root, node);
			linkList.insertLink(link);
			
			node.setParents(linkList);
			
			nodeList.insertNode(node);
		}
		
		Bnet bnet = new Bnet(nodeList);
		computeCPT(bnet, util, numberRatingsPerItem, minRating);
		
		
		return bnet;
	}
	
	
	/**
	 * Computing conditional probability table (CPT) of a distributed Bayesian network.
	 * @param bnet distributed Bayesian network.
	 * @param util utility class which calculates probabilities on dataset. 
	 * @param numberRatingsPerItem number of ratings per item.
	 * @param minRating minimum rating value.
	 */
	private static void computeCPT(Bnet bnet, DatasetStatsProcessor util, int numberRatingsPerItem, int minRating) {
		
		@SuppressWarnings("unchecked")
		Vector<Relation> relList = bnet.getRelationList();
		for (Relation rel : relList) {
			PotentialTable pot = (PotentialTable)rel.getValues();
			NodeList nlist = rel.getVariables();
			if (nlist.size() == 1) {
				int n = numberRatingsPerItem;
				double[] values = new double[n];
				
				Node node = nlist.elementAt(0);
				int nodeId = BnetUtil.extractItemId(node.getName());
				
				for (int i = 0; i < numberRatingsPerItem; i++) {
					double value = DatasetUtil2.realRatingValueOf(i, minRating);
					values[i] = util.probItem(nodeId, value);
				}
				
				pot.setValues(values);
			}
			else if (nlist.size() == 2) {
				int n = numberRatingsPerItem * numberRatingsPerItem;
				double[] values = new double[n];
				
				Node node = nlist.elementAt(0);
				int nodeId = BnetUtil.extractItemId(node.getName());

				Node parent = nlist.elementAt(1);
				int parentId = BnetUtil.extractItemId(parent.getName());
				
				for (int i = 0; i < numberRatingsPerItem; i++) {
					double nodeValue = DatasetUtil2.realRatingValueOf(i, minRating);
					Pair nodePair = new Pair(nodeId, nodeValue);
					
					for (int j = 0; j < numberRatingsPerItem; j++) {
						double parentValue = DatasetUtil2.realRatingValueOf(j, minRating);
						double parentProp = util.probItem(parentId, parentValue);
						
						Pair parentPair = new Pair(parentId, parentValue);
						List<Pair> pairs = Util.newList();
						pairs.add(nodePair);
						pairs.add(parentPair);
						double prob = util.probItem(pairs);
						
						values[i * numberRatingsPerItem + j] = prob / parentProp;
					}
				}
				
				
			}
			
		} // End Relation
		
	}
	
	
}
