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
import net.hudup.core.Util;
import net.hudup.core.logistic.DSUtil;
import net.hudup.data.bit.BitData;
import net.hudup.data.bit.BitMatrix;
import net.hudup.logistic.mining.Cluster;
import net.hudup.logistic.mining.Clusterer;

/**
 * This is utility class for learning Bayesian network.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public final class BnetBinaryLearner {

	
	/**
	 * Clustered learning Bayesian network from bit (binary) data.
	 * @param bitData bit (binary) data.
	 * @param maxParents maximum number of parents of each node.
	 * @return list of Bayesian networks.
	 */
	public static List<Bnet> learning_clustered(BitData bitData, int maxParents) {
		List<Bnet> bnetList = Util.newList();
		
		List<DataBaseCases> dbcList = loadClusteredBitDbCases(bitData);
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
	 * Learning Bayesian network from bit (binary) data.
	 * @param bitData bit (binary) data.
	 * @param maxParents maximum number of parents of each node.
	 * @return list of Bayesian networks.
	 */
	public static List<Bnet> learning(BitData bitData, int maxParents) {
		List<DataBaseCases> dbcList = loadBitDbCases(bitData);

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
	 * Loading database cases from bit (binary) data.
	 * @param bitData bit (binary) data.
	 * @return list of database cases.
	 */
	public static List<DataBaseCases> loadBitDbCases(BitData bitData) {
		List<DataBaseCases> dbcList = Util.newList();
		
		BitMatrix bitSessionMatrix = bitData.createBitSessionMatrix(); 
		DataBaseCases dbc = createBitDbCases(bitSessionMatrix);
		
		dbcList.add(dbc);
		
		return dbcList;
	}
	
	
	/**
	 * Loading clustered database cases from bit (binary) data.
	 * @param bitData bit (binary) data.
	 * @return list of clustered database cases
	 */
	public static List<DataBaseCases> loadClusteredBitDbCases(BitData bitData) {
		List<DataBaseCases> dbcList = Util.newList();
		
		BitMatrix bitItemMatrix = bitData.createBitItemMatrix();
		
		Clusterer clusterer = new Clusterer(bitItemMatrix.matrix, bitItemMatrix.rowIdList);
		clusterer.buildClusters();
		List<Cluster> clusters = clusterer.getClusters();
		
		for (int i = 0; i < clusters.size(); i++) {
			Cluster cluster = clusters.get(i);
			
			List<Integer> subBitItemIdList = cluster.getIdList();
			if (subBitItemIdList.size() == 0)
				continue;
			
			BitData subBitData = bitData.getSub(subBitItemIdList);
			BitMatrix subBitSessionMatrix = subBitData.createBitSessionMatrix(); 
			
			DataBaseCases dbc = createBitDbCases(subBitSessionMatrix);
			
			dbcList.add(dbc);
			
		}
		
		return dbcList;
	}

	
	/**
	 * Creating database cases from bit (binary) session matrix.
	 * @param bitSessionMatrix bit (binary) session matrix.
	 * @return database cases from bit (binary) session matrix.
	 */
	public static DataBaseCases createBitDbCases(BitMatrix bitSessionMatrix) {
		
		CaseListMem caseList = createBitDbCaseList(bitSessionMatrix);
		
		return new DataBaseCases("Rating matrix database", caseList);
	}

	
	/**
	 * Creating case list from bit (binary) session matrix.
	 * @param bitSessionMatrix bit (binary) session matrix.
	 * @return case list from bit (binary) session matrix.
	 */
	protected static CaseListMem createBitDbCaseList(
			BitMatrix bitSessionMatrix) {
		
		NodeList nodeList = BnetBinaryUtil.createBitItemNodeList(
				bitSessionMatrix.columnIdList);

		Vector<int[]> cases = new Vector<int[]>();
		
		for (byte[] row : bitSessionMatrix.matrix) {
			cases.add(DSUtil.byteToInt(row));
		}
		
		CaseListMem caseList = new CaseListMem(nodeList);
		caseList.setCases(cases);
		
		return caseList;
	}


}
