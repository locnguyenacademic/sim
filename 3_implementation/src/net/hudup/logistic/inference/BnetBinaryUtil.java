package net.hudup.logistic.inference;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import elvira.Bnet;
import elvira.Configuration;
import elvira.Evidence;
import elvira.FiniteStates;
import elvira.Node;
import elvira.NodeList;
import elvira.inference.elimination.VariableElimination;
import elvira.potential.PotentialTable;
import net.hudup.alg.cf.bnet.BnetUtil;
import net.hudup.core.Util;
import net.hudup.core.data.Pair;
import net.hudup.core.data.RatingVector;
import net.hudup.core.logistic.DSUtil;
import net.hudup.core.logistic.RatingFilter;
import net.hudup.core.logistic.ValueTriple;
import net.hudup.data.bit.BitDataUtil;

/**
 * This is utility class for processing binary Bayesian network.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class BnetBinaryUtil {

	
	/**
	 * Create bit (binary) item node.
	 * @param bitItemId bit (binary) item id.
	 * @return {@link FiniteStates} as bit (binary) item node.
	 */
	public static FiniteStates createBitItemNode(int bitItemId) {
		return new FiniteStates(
				BnetUtil.createItemNodeName(bitItemId), new String[] {"0", "1"}); // Pay attention
		
	}
	
	
	/**
	 * Creating list of bit (binary) item nodes.
	 * @param bitItemIdList list of bit (binary) item id (s).
	 * @return list of bit (binary) item nodes.
	 */
	protected static NodeList createBitItemNodeList(List<Integer> bitItemIdList) {
		
		NodeList nodeList = new NodeList();
		
		for (int bitItemId : bitItemIdList) {
			Node node = createBitItemNode(bitItemId);
			nodeList.insertNode(node);
		}
		
		return nodeList;
		
	}
	
	
	/**
	 * Creating bit (binary) item configuration.
	 * @param bitItemMap map of pairs. Each pair has a key (real item id) and value (real rating value).
	 * @param nodeList list of nodes.
	 * @param userRating user rating vector.
	 * @return bit (binary) item configuration.
	 */
	public static Configuration createBitItemConfiguration(
			Map<Integer, Pair> bitItemMap,
			NodeList nodeList, 
			RatingVector userRating) {
		
		Vector<FiniteStates> vars = new Vector<FiniteStates>();
		Vector<Integer> vals = new Vector<Integer>();
		
		Set<Integer> itemIds = userRating.fieldIds(true);
		for (int itemId : itemIds) {
			double rating = userRating.get(itemId).value;
			
			int bitItemId = BitDataUtil.findBitItemIdOf(
					bitItemMap, itemId, rating);
			if (bitItemId < 0)
				continue;
			
			String nodeName = BnetUtil.createItemNodeName(bitItemId);
			if (nodeList.getId(nodeName) == -1)
				continue;
			
			FiniteStates node = (FiniteStates)nodeList.getNode(nodeName);
			
			vars.addElement(node);
			vals.add(1);
		}
		
		return new Configuration(vars, vals);
		
	}
	
	
	/**
	 * Create item evidence.
	 * @param bitItemMap map of pairs. Each pair has a key (real item id) and value (real rating value).
	 * @param nodeList list of nodes.
	 * @param userRating user rating vector.
	 * @return item evidence.
	 */
	public static Evidence createBitItemEvidence(
			Map<Integer, Pair> bitItemMap,
			NodeList nodeList, 
			RatingVector userRating) {
		
		Configuration conf = createBitItemConfiguration(
				bitItemMap, nodeList, userRating);
		Evidence evidence = new Evidence();
		
		Vector<?> vars = conf.getVariables();
		Vector<?> vals = conf.getValues();
		
		for (int i = 0; i < vars.size(); i++) {
			FiniteStates node = (FiniteStates)vars.get(i);
			int value = (Integer)vals.get(i);
			evidence.insert(node, value);
		}
		
		return evidence;
	}
	
	
	/**
	 * Making inference on given Bayesian network and evidence.
	 * @param bnet given Bayesian network.
	 * @param bitItemMap map of pairs. Each pair has a key (real item id) and value (real rating value).
	 * @param ev given evidence.
	 * @param queryItemIds list of queried item identifiers.
	 * @param referredRatingValue referred rating value, often relevant value = (minimum value + maximum value)/2.
	 * @param ratingFilter rating filter.
	 * @return list of {@link ValueTriple} as reference result.
	 */
	public static List<ValueTriple> inference(
			Bnet bnet,
			Map<Integer, Pair> bitItemMap,
			Evidence ev,
			Set<Integer> queryItemIds,
			double referredRatingValue,
			RatingFilter ratingFilter) {
		
		VariableElimination ve = new VariableElimination(bnet, ev);
        ve.obtainInterest();
        
        ve.propagate();
        bnet.setCompiledPotentialList(ve.getResults());
        //bnet.showResults(ve);
        
        return getInferenceResult(bnet, bitItemMap, queryItemIds, referredRatingValue, ratingFilter);
	}

	
	/**
	 * Getting reference result.
	 * @param bnet given Bayesian network.
	 * @param bitItemMap map of pairs. Each pair has a key (real item id) and value (real rating value).
	 * @param queryItemIds list of queried item identifiers.
	 * @param referredRatingValue referred rating value, often relevant value = (minimum value + maximum value)/2.
	 * @param ratingFilter rating filter.
	 * @return list of {@link ValueTriple} as reference result.
	 */
	private static List<ValueTriple> getInferenceResult(
			Bnet bnet, 
			Map<Integer, Pair> bitItemMap,
			Set<Integer> queryItemIds,
			double referredRatingValue,
			RatingFilter ratingFilter) {
		
		List<ValueTriple> triples = Util.newList();

        Vector<?> results = bnet.getCompiledPotentialList();
        
        for (int i = 0 ; i < results.size() ; i++) {
        	PotentialTable pot = (PotentialTable)results.elementAt(i);
        	Vector<?> vars = pot.getVariables();
        	FiniteStates node = (FiniteStates)vars.elementAt(0);
            int bitItemId = BnetUtil.extractItemId(node.getName());
            
            Pair pair = bitItemMap.get(bitItemId);
            int itemId = pair.key();
            if(!queryItemIds.contains(itemId))
            	continue;
            
    		double[] values = pot.getValues();
    		if (!DSUtil.assertNotNaN(values))
    			continue;
    		
    		if (values[1] <= values[0]) // Pay attention here: the recommended item must have high probability (values[1])
    			continue;
    		
    		double rating = pair.value();
    		ValueTriple triple = new ValueTriple(itemId, rating, values[1]);
			if (!Util.isUsed(referredRatingValue) || ratingFilter == null)
	        	triples.add(triple);
			else if (ratingFilter.accept(rating, referredRatingValue))
	        	triples.add(triple);
        }
        
        return triples;
	}
	
	
}
