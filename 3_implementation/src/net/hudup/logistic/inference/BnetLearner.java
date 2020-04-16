/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.logistic.inference;

import java.util.List;
import java.util.Vector;

import elvira.Bnet;
import elvira.CaseListMem;
import elvira.NodeList;
import elvira.database.DataBaseCases;
import elvira.learning.DELearning;
import elvira.learning.K2Learning;
import elvira.learning.K2Metrics;
import net.hudup.alg.cf.bnet.BnetUtil;
import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.DatasetUtil2;
import net.hudup.core.data.RatingMatrix;
import net.hudup.core.data.Scanner;
import net.hudup.core.data.Snapshot;
import net.hudup.logistic.mining.Cluster;
import net.hudup.logistic.mining.Clusterer;
import net.hudup.sparse.Reducer;
import net.hudup.sparse.SparseProcessor;

/**
 * This is utility class for learning Bayesian network.
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
public class BnetLearner {
	
	
	/**
	 * Clustered learning Bayesian network from dataset.
	 * @param dataset specified dataset.
	 * @param maxParents specified maximum parents of a node.
	 * @param bnetNodeNumber number of Bayesian network nodes.
	 * @param dimReduceRatio dimension reduction ratio.
	 * @param completeMethod method to complete missing data.
	 * @return list of Bayesian networks as result.
	 */
	public static List<Bnet> learning_clustered(Dataset dataset, int maxParents, int bnetNodeNumber, double dimReduceRatio, Alg completeMethod) {
		List<Bnet> bnetList = Util.newList();
		
		List<DataBaseCases> dbcList = loadClusteredDbCases(dataset, bnetNodeNumber, dimReduceRatio, completeMethod);
		for (DataBaseCases dbc : dbcList) {
			K2Learning k2 = new K2Learning(
					dbc, 
					dbc.getNodeList(),
					maxParents,
					new K2Metrics(dbc));
			k2.learning();
			
			DELearning de = new DELearning(dbc, k2.getOutput());
		    de.learning();
		    
		    Bnet bnet = de.getOutput();
		    NodeList nodelList = bnet.getNodeList();
		    if (nodelList.size() < 2)
		    	continue;
	    
		    bnetList.add(bnet);
		    
		}
		
		return bnetList;
	}

	
	
	/**
	 * Learning Bayesian network from dataset.
	 * @param dataset specified dataset.
	 * @param maxParents specified maximum parents of a node.
	 * @param dimReduceRatio dimension reduction ratio.
	 * @param completeMethod method to complete missing data.
	 * @return list of Bayesian networks as result.
	 */
	public static List<Bnet> learning(Dataset dataset, int maxParents, double dimReduceRatio, Alg completeMethod) {
		Snapshot snapshot = null;
		if (dataset instanceof Snapshot)
			snapshot = (Snapshot) dataset.clone();
		else 
			snapshot = (Snapshot) ((Scanner)dataset).catchup();
			
		new SparseProcessor().algComplete(snapshot, completeMethod);
		
		List<DataBaseCases> dbcList = loadDbCases(snapshot, dimReduceRatio);

		snapshot.clear();
		return learning(dbcList, maxParents);
	}

	
	/**
	 * Learning Bayesian network from list of database cases.
	 * @param dbcList list of database cases.
	 * @param maxParents specified maximum parents of a node.
	 * @return list of Bayesian networks as result.
	 */
	public static List<Bnet> learning(List<DataBaseCases> dbcList, int maxParents) {
		List<Bnet> bnetList = Util.newList();
		
		for (DataBaseCases dbc : dbcList) {
			K2Learning k2 = new K2Learning(
					dbc, 
					dbc.getNodeList(),
					maxParents,
					new K2Metrics(dbc));
			k2.learning();
			
			DELearning de = new DELearning(dbc, k2.getOutput());
		    de.learning();
		    
		    Bnet bnet = de.getOutput();
	    
		    NodeList nodelList = bnet.getNodeList();
		    if (nodelList.size() >= 2)
		    	bnetList.add(bnet);
		}
		
		
		return bnetList;
	}
	
	
	/**
	 * Loading database cases from dataset.
	 * @param dataset specified dataset.
	 * @param reduceRatio dimension reduction ratio.
	 * @return list of database cases.
	 */
	public static List<DataBaseCases> loadDbCases(Dataset dataset, double reduceRatio) {
		List<DataBaseCases> dbcList = Util.newList();
		RatingMatrix userMatrix = dataset.createUserMatrix();
		userMatrix = new Reducer(reduceRatio).simplyReduce(userMatrix, false);
		
		DataBaseCases dbc = createDbCases(				
				userMatrix, 
				dataset.getConfig().getNumberRatingsPerItem(), 
				(int)dataset.getConfig().getMinRating());
		dbcList.add(dbc);
		
		return dbcList;
	}


	/**
	 * Loading clustered database cases from dataset.
	 * @param dataset specified dataset.
	 * @param bnetNodeNumber number of Bayesian network nodes.
	 * @param reduceRatio dimension reduction ratio.
	 * @param completeMethod method to complete missing data.
	 * @return list of clustered database cases as result.
	 */
	public static List<DataBaseCases> loadClusteredDbCases(Dataset dataset, int bnetNodeNumber, double reduceRatio, Alg completeMethod) {
		List<DataBaseCases> dbcList = Util.newList();
		
		RatingMatrix itemMatrix = dataset.createItemMatrix();
		itemMatrix = new Reducer(reduceRatio).simplyReduce(itemMatrix, false);
		if (itemMatrix.rowIdList.size() == 0)
			return dbcList;
		
		Clusterer clusterer = new Clusterer(itemMatrix.matrix, itemMatrix.rowIdList);
		clusterer.buildClustersByBnetNodeNumber(bnetNodeNumber);
		List<Cluster> clusters = clusterer.getClusters();
		
		RatingMatrix userMatrix = dataset.createUserMatrix();
		for (int i = 0; i < clusters.size(); i++) {
			Cluster cluster = clusters.get(i);
			
			List<Integer> subItemIdList = cluster.getIdList();
			RatingMatrix subUserMatrix = userMatrix.createSub(subItemIdList, false);
			subUserMatrix = new Reducer(reduceRatio).simplyReduce(subUserMatrix, false);
			
			if (subUserMatrix.matrix.length < 2 || subItemIdList.size() < 2)
				continue;
			
			SparseProcessor sparser = new SparseProcessor();
			if (completeMethod == null)
				sparser.columnMeanComplete(subUserMatrix.matrix);
			else
				sparser.algComplete(subUserMatrix, completeMethod, false);
			
			DataBaseCases dbc = createDbCases(				
					subUserMatrix, 
					dataset.getConfig().getNumberRatingsPerItem(), 
					(int)dataset.getConfig().getMinRating());
			
			dbcList.add(dbc);
			
		}
		
		return dbcList;
	}

	
	/**
	 * Creating database cases from user rating matrix.
	 * @param userMatrix user rating matrix.
	 * @param numberRatingsPerItem number of ratings per item.
	 * @param minRating minimum rating value.
	 * @return database cases created from user rating matrix.
	 */
	public static DataBaseCases createDbCases(
			RatingMatrix userMatrix,
			int numberRatingsPerItem,
			int minRating) {
		
		CaseListMem caseList = createDbCaseList(
				userMatrix, 
				numberRatingsPerItem, 
				minRating);
		
		return new DataBaseCases("Rating matrix database", caseList);
	}
	
	
	/**
	 * Creating case list from user rating matrix.
	 * @param userMatrix user rating matrix.
	 * @param numberRatingsPerItem number of ratings per item.
	 * @param minRating minimum rating value.
	 * @return case list created from user rating matrix.
	 */
	protected static CaseListMem createDbCaseList(
			RatingMatrix userMatrix,
			int numberRatingsPerItem,
			int minRating) {
		
		NodeList nodeList = BnetUtil.createItemNodeList(
				userMatrix.columnIdList, 
				numberRatingsPerItem, 
				minRating);

		Vector<int[]> cases = new Vector<int[]>();
		
		for (double[] row : userMatrix.matrix) {
			cases.add(DatasetUtil2.zeroBasedRatingValueOf(row, minRating));
		}
		
		CaseListMem caseList = new CaseListMem(nodeList);
		caseList.setCases(cases);
		
		return caseList;
	}

	
}


