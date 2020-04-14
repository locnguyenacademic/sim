package net.hudup.logistic.inference;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import BayesianNetworks.BayesNet;
import BayesianNetworks.DiscreteVariable;
import BayesianNetworks.ProbabilityFunction;
import BayesianNetworks.ProbabilityVariable;
import InferenceGraphs.InferenceGraph;
import InferenceGraphs.InferenceGraphNode;
import QuasiBayesianInferences.QBInference;
import QuasiBayesianNetworks.QuasiBayesNet;
import net.hudup.alg.cf.bnet.BnetUtil;
import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.Pair;
import net.hudup.core.data.Profile;
import net.hudup.core.data.RatingVector;
import net.hudup.core.data.bit.BitData;
import net.hudup.core.data.bit.BitDataUtil;
import net.hudup.core.logistic.xURI;
import net.hudup.logistic.math.BitAndNotProbItemMatrix;
import net.hudup.logistic.math.BitDatasetStatsProcessor;

/**
 * This class is graph of binary Bayesian network.
 * 
 * @author Loc Nguyen
 * @version 10.0
 * 
 */
public class BnetBinaryGraph extends InferenceGraph implements Serializable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Node width.
	 */
	public final static int WIDTH = 50;

	
	/**
	 * Node height.
	 */
	public final static int HEIGHT = 20;

	
	/**
	 * Horizontal distance between two nodes.
	 */
	public final static int XDIS = WIDTH * 2;
	
	
	/**
	 * Vertical distance between two nodes.
	 */
	public final static int YDIS = HEIGHT * 8;
	
	
	
	/**
	 * Root bit item identifier.
	 */
	protected int rootBitItemId = 0;
	
	
	/**
	 * Map of binary items.
	 */
	protected Map<Integer, Pair> bitItemMap = null;

	
	/**
	 * Constructor with Bayesian network.
	 * @param bn Bayesian network.
	 */
	protected BnetBinaryGraph(BayesNet bn) {
		super(bn);
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * Create map of binary Bayesian graph lists from dataset.
	 * @param dataset specified dataset.
	 * @param minprob minimum probability.
	 * @return map of Bayesian graph lists.
	 */
	public static Map<Integer, List<BnetBinaryGraph>> create(Dataset dataset, double minprob) {
		BitData bitData = BitData.create(dataset);
		
		BitAndNotProbItemMatrix matrix = new BitAndNotProbItemMatrix();
		matrix.setup(bitData);
		
		Map<Integer, List<BnetBinaryGraph>> bgraphMap = create(matrix, minprob);
		
		bitData.clear();
		matrix.clear();
		bitData = null;
		matrix = null;
		
		return bgraphMap;
	}
	
	
	/**
	 * Create map of binary Bayesian graph lists without using (taking advantages of) probability matrix.
	 * @param dataset specified dataset.
	 * @param minprob minimum probability.
	 * @return map of binary Bayesian graph lists.
	 */
	public static Map<Integer, List<BnetBinaryGraph>> createNotUseMatrix(Dataset dataset, double minprob) {
		BitData bitData = BitData.create(dataset);
		
		Map<Integer, List<BnetBinaryGraph>> bgraphMap = create(bitData, minprob);
		
		bitData.clear();
		bitData = null;
		
		return bgraphMap;
	}

	
	/**
	 * Create map of binary Bayesian graph lists from AND-NOT probability matrix.
	 * @param matrix bit (binary) AND-NOT probability matrix.
	 * @param minprob minimum probability.
	 * @return map of binary Bayesian graph lists.
	 */
	private static Map<Integer, List<BnetBinaryGraph>> create(
			BitAndNotProbItemMatrix matrix,
			double minprob) {
		Map<Integer, List<BnetBinaryGraph>> result = Util.newMap();
		
		Set<Integer> bitIds = matrix.bitIds();
		for (int bitId : bitIds) {
			BnetBinaryGraph bgraph = create(matrix, bitId, minprob);
			if (bgraph == null)
				continue;
			
			Pair pair = bgraph.bitItemMap.get(bitId);
			int itemId = pair.key();
			
			List<BnetBinaryGraph> group = null;
			if (result.containsKey(itemId)) {
				group = result.get(itemId);
			}
			else {
				group = Util.newList();
				result.put(itemId, group);
			}
			
			group.add(bgraph);
		}
		
		return result;
	}
	
	
	/**
	 * Create map of binary Bayesian graph lists from bit data (binary data).
	 * @param bitData bit data (binary data).
	 * @param minprob minimum probability.
	 * @return map of binary Bayesian graph lists.
	 */
	private static Map<Integer, List<BnetBinaryGraph>> create(
			BitData bitData, 
			double minprob) {
		Map<Integer, List<BnetBinaryGraph>> result = Util.newMap();
		
		Set<Integer> bitIds = bitData.bitItemIds();
		for (int bitId : bitIds) {
			BnetBinaryGraph bgraph = create(bitData, bitId, minprob);
			if (bgraph == null)
				continue;
			
			Pair pair = bgraph.bitItemMap.get(bitId);
			int itemId = pair.key();
			
			List<BnetBinaryGraph> group = null;
			if (result.containsKey(itemId)) {
				group = result.get(itemId);
			}
			else {
				group = Util.newList();
				result.put(itemId, group);
			}
			
			group.add(bgraph);
		}
		
		return result;
	}

	
	/**
	 * Create map of binary Bayesian graph from AND-NOT probability matrix.
	 * @param matrix bit (binary) AND-NOT probability matrix.
	 * @param rootBitItemId root bit (binary) item id.
	 * @param minprob minimum probability.
	 * @return binary Bayesian graph.
	 */
	public static BnetBinaryGraph create(
			BitAndNotProbItemMatrix matrix, 
			int rootBitItemId,
			double minprob) {
		if (!matrix.contains(rootBitItemId, rootBitItemId))
			return null;
		double rootProb = matrix.getProb(rootBitItemId);
		if (rootProb < minprob)
			return null;
		
		QuasiBayesNet bn = new QuasiBayesNet();
		Map<Integer, Pair> bitItemMap = Util.newMap();
		
		List<ProbabilityVariable> varList = Util.newList();
		List<ProbabilityFunction> fList = Util.newList();
		
		String rootName = BnetUtil.createItemNodeName(rootBitItemId);
		ProbabilityVariable root = new ProbabilityVariable(
				bn, rootName, varList.size(), 
				new String[] {"1", "0"}, new Vector<String>());
		varList.add(root);

		double[] rootCPT = new double[] {rootProb, 1- rootProb};
		ProbabilityFunction rootf = new ProbabilityFunction(
				bn,
				new DiscreteVariable[] {root}, 
				rootCPT,
				new Vector<String>()); 
		fList.add(rootf);
		
		bitItemMap.put(rootBitItemId, matrix.getPair(rootBitItemId));
		
		Set<Integer> bitIds = matrix.bitIds();
		
		for (int bitId : bitIds) {
			if (bitId == rootBitItemId)
				continue;
			if (!matrix.contains(bitId, rootBitItemId))
				continue;
			
			double andProb = matrix.getAndProb(bitId, rootBitItemId);
			double conditionProb = andProb / rootProb;
			if (andProb < minprob)
				continue;
			//if (conditionProb < minprob)
			//	continue;
			double andNotProb = matrix.getAndNotProb(bitId, rootBitItemId);
			
			String varName = BnetUtil.createItemNodeName(bitId);
			ProbabilityVariable var = new ProbabilityVariable(
					bn, varName, varList.size(), 
					new String[] {"1", "0"}, new Vector<String>());
			varList.add(var);
			
			double[] varCPT = new double[4];
			varCPT[0] = conditionProb;
			varCPT[1] = andNotProb / (1f - rootProb);
			varCPT[2] = 1f - varCPT[0];
			varCPT[3] = 1f - varCPT[1];
			
			ProbabilityFunction f = new ProbabilityFunction(
					bn,
					new DiscreteVariable[] {var, root}, 
					varCPT,
					new Vector<String>()); 
			fList.add(f);
			
			bitItemMap.put(bitId, matrix.getPair(bitId));
		}
		
		bn.set_probability_variables(varList.toArray(new ProbabilityVariable[0]));
		bn.set_probability_functions(fList.toArray(new ProbabilityFunction[0]));
		bn.set_name("Bayesian network for item " + rootBitItemId);
		
		BnetBinaryGraph bsb = new BnetBinaryGraph(bn);
		bsb.rootBitItemId = rootBitItemId;;
		bsb.bitItemMap = bitItemMap;
		
		bsb.updateNodes();
		return bsb;
	}
	
	
	/**
	 * Create map of binary Bayesian graph from bit data (binary data).
	 * @param bitData bit (binary) data.
	 * @param rootBitItemId root bit (binary) item id.
	 * @param minprob minimum probability.
	 * @return binary Bayesian graph.
	 */
	public static BnetBinaryGraph create(
			BitData bitData, 
			int rootBitItemId,
			double minprob) {
		
		BitDatasetStatsProcessor processor = new BitDatasetStatsProcessor(bitData);
		
		double rootProb = processor.prob(rootBitItemId);
		if (rootProb < minprob)
			return null;
		
		QuasiBayesNet bn = new QuasiBayesNet();
		Map<Integer, Pair> bitItemMap = Util.newMap();
		
		List<ProbabilityVariable> varList = Util.newList();
		List<ProbabilityFunction> fList = Util.newList();
		
		String rootName = BnetUtil.createItemNodeName(rootBitItemId);
		ProbabilityVariable root = new ProbabilityVariable(
				bn, rootName, varList.size(), 
				new String[] {"1", "0"}, new Vector<String>()); // Please pay attention here.
		varList.add(root);

		double[] rootCPT = new double[] {rootProb, 1- rootProb};
		ProbabilityFunction rootf = new ProbabilityFunction(
				bn,
				new DiscreteVariable[] {root}, 
				rootCPT,
				new Vector<String>()); 
		fList.add(rootf);
		
		bitItemMap.put(rootBitItemId, bitData.get(rootBitItemId).pair());
		
		Set<Integer> bitIds = bitData.bitItemIds();
		
		for (int bitId : bitIds) {
			if (bitId == rootBitItemId)
				continue;
			
			double andProb = processor.probAnd(bitId, rootBitItemId);
			double conditionProb = andProb / rootProb;
			if (andProb < minprob)
				continue;
			//if (conditionProb < minprob)
			//	continue;
			double andNotProb = processor.probAndNot(bitId, rootBitItemId);
			double rconditionProb = andNotProb / (1.0 - rootProb);
			
			String varName = BnetUtil.createItemNodeName(bitId);
			ProbabilityVariable var = new ProbabilityVariable(
					bn, varName, varList.size(), 
					new String[] {"1", "0"}, new Vector<String>());
			varList.add(var);
			
			double[] varCPT = new double[4];
			varCPT[0] = conditionProb;
			varCPT[1] = rconditionProb;
			varCPT[2] = 1f - varCPT[0];
			varCPT[3] = 1f - varCPT[1];
			
			ProbabilityFunction f = new ProbabilityFunction(
					bn,
					new DiscreteVariable[] {var, root}, 
					varCPT,
					new Vector<String>()); 
			fList.add(f);
			
			bitItemMap.put(bitId, bitData.get(bitId).pair());
		}
		
		bn.set_probability_variables(varList.toArray(new ProbabilityVariable[0]));
		bn.set_probability_functions(fList.toArray(new ProbabilityFunction[0]));
		bn.set_name("Bayesian network for item " + rootBitItemId);
		
		BnetBinaryGraph bsb = new BnetBinaryGraph(bn);
		bsb.rootBitItemId = rootBitItemId;;
		bsb.bitItemMap = bitItemMap;
		
		bsb.updateNodes();
		return bsb;
	}


	/**
	 * Calculating marginal posterior probability.
	 * @param rating rating vector (often user rating vector).
	 * @param profile specified profile (not used in current version).
	 * @return marginal posterior probability.
	 */
	public double marginalPosterior(RatingVector rating, Profile profile) {
		return marginalPosterior(Pair.toPairList(rating));
	}
	
	
	
	/**
	 * Calculating marginal posterior probability.
	 * @param evList list of evidence pairs.
	 * @return marginal posterior probability.
	 */
	@SuppressWarnings("unchecked")
	public double marginalPosterior(List<Pair> evList) {
		Vector<InferenceGraphNode> nodes = this.get_nodes();
		for (InferenceGraphNode node : nodes) {
			node.clear_observation();

			String nodeName = node.get_name();
			int bitItemId = BnetUtil.extractItemId(nodeName);
			if (bitItemId == rootBitItemId)
				continue;
			
			for (Pair pair : evList) {
				int bitId = 
					BitDataUtil.findBitItemIdOf(bitItemMap, pair.key(), pair.value());
				
				if (bitId >= 0 && bitId == bitItemId) {
					node.set_observation_value("1");
					break;
				}
				
			}
		}
		
		
		return marginalPosterior(rootBitItemId);
		
	}
	
	
	/**
	 * Calculating marginal posterior probability.
	 * @param bitItemId bit (binary) item id.
	 * @return marginal posterior probability.
	 */
	public double marginalPosterior(int bitItemId) {
		QBInference qbi = new QBInference(get_bayes_net(), true);
        qbi.inference(BnetUtil.createItemNodeName(bitItemId));
        ProbabilityFunction result = qbi.get_result();
		
		double[] values = result.get_values();
		return values[0];
	}
	
	
	/**
	 * Calculating marginal posterior probability.
	 * @param pair specified pair of real item id and real rating value.
	 * @return marginal posterior probability.
	 */
	public double mariginalPosterior(Pair pair) {
		int bitItemId = BitDataUtil.findBitItemIdOf(
				bitItemMap, pair.key(), pair.value());
		
		return marginalPosterior(bitItemId);
	}
	
	
	/**
	 * Clearing observations.
	 */
	public void clearObservations() {
		@SuppressWarnings("unchecked")
		Vector<InferenceGraphNode> nodes = this.get_nodes();
		for (InferenceGraphNode node : nodes) {
			if (node.is_observed())
				node.clear_observation();
		}
	}
	
	
	/**
	 * Checking whether this Bayesian graph contains specified pair.
	 * @param pair specified pair of item id and its rating value
	 * @return whether contains specified pair.
	 */
	public boolean contains(Pair pair) {
		return BitDataUtil.findBitItemIdOf(bitItemMap, pair.key(), pair.value()) >= 0;
	}
	
	
	/**
	 * Checking whether this Bayesian graph contains specified bit item id.
	 * @param bitItemId binary item id
	 * @return whether contains specified bit item id.
	 */
	public boolean contains(int bitItemId) {
		return bitItemMap.containsKey(bitItemId);
	}
	
	
	/**
	 * Getting item id and its rating value of root node.
	 * @return pair of item id and its rating value of root node.
	 */
	public Pair getRootItemPair() {
		return bitItemMap.get(rootBitItemId);
	}
	
	
	/**
	 * Getting binary item id of root node.
	 * @return binary item id of root node.
	 */
	public int getRootBitItemId() {
		return rootBitItemId;
	}
	
	
	/**
	 * Getting item id and its rating value of arbitrary (binary) node.
	 * @param bitItemId bit (binary) item id representing arbitrary (binary) node.
	 * @return pair item id and its rating value of arbitrary node
	 */
	public Pair getItemPair(int bitItemId) {
		return bitItemMap.get(bitItemId);
	}
	
	
	/**
	 * Getting graph node of bit (binary) item id.
	 * @param bitItemId bit (binary) item id.
	 * @return {@link InferenceGraphNode} as graph node of bit (binary) item id.
	 */
	public InferenceGraphNode getBitItemNode(int bitItemId) {
		@SuppressWarnings("unchecked")
		Vector<InferenceGraphNode> nodes = this.get_nodes();
		for (InferenceGraphNode node : nodes) {
			String nodeName = node.get_name();
			if (BnetUtil.isAtt(nodeName))
				continue;
			
			int bitId = BnetUtil.extractItemId(nodeName);
			if (bitId == bitItemId)
				return node;
			
		}
		
		return null;
	}
	
	
	/**
	 * Getting graph node of real item id and its rating value specified a pair.
	 * @param pair pair of real item id and its rating value.
	 * @return {@link InferenceGraphNode} as graph node of real item id and its rating value specified a pair.
	 */
	public InferenceGraphNode getBitItemNode(Pair pair) {
		int bitItemId = BitDataUtil.findBitItemIdOf(
				bitItemMap, pair.key(), pair.value());
		if (bitItemId == -1)
			return null;
		
		return getBitItemNode(bitItemId);
	}
	
	
	/**
	 * Getting root graph node.
	 * @return {@link InferenceGraphNode} as root graph node.
	 */
	public InferenceGraphNode getRootNode() {
		return getBitItemNode(rootBitItemId);
	}
	
	
	/**
	 * Getting set of bit item id (s).
	 * @return set of bit item id (s).
	 */
	public Set<Integer> bitItemIds() {
		return bitItemMap.keySet();
	}
	
	
	@SuppressWarnings("unchecked")
	protected void updateNodes() {
		Vector<InferenceGraphNode> nodes = this.get_nodes();
		
		int xlength = (nodes.size() - 1) * XDIS;
		xlength = Math.max(xlength, XDIS);

		int countChild = 0;
		for (int i = 0; i < nodes.size(); i++) {
			InferenceGraphNode node = nodes.get(i);
			
			String nodeName = node.get_name();
			int bitId = BnetUtil.extractItemId(nodeName);
			node.add_variable_property(DataConfig.BITITEMID_FIELD + " = " + bitId);
			
			Pair pair = getItemPair(bitId);
			node.add_variable_property(DataConfig.ITEMID_FIELD + " = " + pair.key());
			node.add_variable_property(DataConfig.RATING_FIELD + " = " + pair.value());
			
			int x = (bitId == rootBitItemId ? xlength / 2 : countChild * XDIS);
			int y = (bitId == rootBitItemId ? 0 : YDIS);
			
			node.add_variable_property(DataConfig.POSITION_FIELD + " = (" + x + ", " + y + ")");
			
			if (bitId != rootBitItemId)
				countChild++;
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public static BnetBinaryGraph load(xURI uri) throws Exception {
		
		QuasiBayesNet bn = new QuasiBayesNet(uri.toURL());
		ProbabilityVariable[] vars = bn.get_probability_variables();
		int rootBitItemId = -1;
		Map<Integer, Pair> bitItemMap = Util.newMap();
		for (ProbabilityVariable var : vars) {
			Vector<String> props = var.get_properties();
			
			int bitItemId = -1;
			int itemId = -1;
			double rating = Constants.UNUSED;
			
			for (String prop : props) {
				prop.replaceAll("\\s", "");
				String[] array = prop.split("=");
				String attr = array[0].trim().toLowerCase();
				String value = array[1].trim().toLowerCase();
				
				if (attr.equals(DataConfig.BITITEMID_FIELD.toLowerCase())) {
					bitItemId = Integer.parseInt(value);
					
					ProbabilityFunction f = bn.get_probability_function(var.get_index());
					if (f.get_values().length == 2)
						rootBitItemId = bitItemId;
				}
				else if(attr.equals(DataConfig.ITEMID_FIELD.toLowerCase()))
					itemId = Integer.parseInt(value);
				else if(attr.equals(DataConfig.RATING_FIELD.toLowerCase()))
					rating = Double.parseDouble(value);
				
				
			}
			
			if (bitItemId != -1 && itemId != -1 && Util.isUsed(rating))
				bitItemMap.put(bitItemId, new Pair(itemId, rating));
			
			
		}
		
		BnetBinaryGraph bsb = new BnetBinaryGraph(bn);
		bsb.rootBitItemId = rootBitItemId;;
		bsb.bitItemMap = bitItemMap;
		
		return bsb;
		
	}
	
	
}


