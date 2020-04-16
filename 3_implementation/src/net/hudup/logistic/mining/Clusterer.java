/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.logistic.mining;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.data.DataConfig;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.parser.TextParserUtil;
import weka.clusterers.EM;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

/**
 * This class represents a clustering algorithm (clusterer, clustering tool).
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class Clusterer {
	
	
	/**
	 * Maximum number of iterations.
	 */
	public final static int MAX_ITERS = 100;
	
	
	/**
	 * Instances (database, training set) contains rows.
	 */
	protected Instances instances = null;
	
	
	/**
	 * List of row id (s).
	 */
	protected List<Integer> rowIdList = Util.newList();
	
	
	/**
	 * Map of clusters.
	 */
	protected Map<Integer, Cluster> clusterMap = Util.newMap();
	
	
	/**
	 * Constructor with instances (database, training set).
	 * @param instances instances (database, training set) for clustering algorithms to learn clusters.
	 */
	public Clusterer(Instances instances) {
		this.instances = instances;
	}
	
	
	/**
	 * Constructor with specified matrix and list of row identifiers.
	 * @param matrix specified matrix.
	 * @param rowIdList list of row identifiers.
	 */
	public Clusterer(double[][] matrix, List<Integer> rowIdList) {
		this.rowIdList.clear();
		this.rowIdList.addAll(rowIdList);
		
		FastVector attrs = new FastVector();
		int columns = matrix[0].length;
		for (int i = 0; i < columns; i++) {
			String nodeName = DataConfig.ATTRIBUTE_FIELD + TextParserUtil.CONNECT_SEP + i;
			Attribute attr = new Attribute(nodeName);
			
			attrs.addElement(attr);
		}
		
		Instances instances = new Instances("bayesinsts", attrs, 100);
		for (int i = 0; i < matrix.length; i++) {
			double[] row = matrix[i];
			
			Instance instance = new Instance(attrs.size());
			for (int j = 0; j < row.length; j++) {
				double value = row[j];
				if (Util.isUsed(value))
					instance.setValue(j, value);
				else
					instance.setMissing(j);
			}
			
			instances.add(instance);
		}
		
		this.instances = instances;
	}

	
	/**
	 * Constructor with bit (binary) matrix.
	 * @param bitMatrix bit (binary) matrix.
	 * @param bitRowIdList list of bit (binary) rows.
	 */
	public Clusterer(byte[][] bitMatrix, List<Integer> bitRowIdList) {
		this.rowIdList.clear();
		this.rowIdList.addAll(bitRowIdList);
		
		FastVector attrs = new FastVector();
		int columns = bitMatrix[0].length;
		for (int i = 0; i < columns; i++) {
			String nodeName = DataConfig.ATTRIBUTE_FIELD + TextParserUtil.CONNECT_SEP + i;
			Attribute attr = new Attribute(nodeName);
			
			attrs.addElement(attr);
		}
		
		Instances instances = new Instances("bayesinsts", attrs, 100);
		for (int i = 0; i < bitMatrix.length; i++) {
			byte[] row = bitMatrix[i];
			
			Instance instance = new Instance(attrs.size());
			for (int j = 0; j < row.length; j++) {
				byte value = row[j];
				instance.setValue(j, value);
			}
			
			instances.add(instance);
		}
		
		this.instances = instances;
	}

	
	/**
	 * Building clusters with maximum node number per Bayesian network.
	 * @param bnetNodeNumber maximum node number per Bayesian network.
	 */
	public void buildClustersByBnetNodeNumber(int bnetNodeNumber) {
		if (bnetNodeNumber <= 0)
			buildClusters();
		else {
			int n = this.instances.numInstances();
			buildClusters(n / bnetNodeNumber);
		}
	}
	
	
	/**
	 * Building clusters.
	 */
	public void buildClusters() {
		buildClusters(-1);
	}
	
	
	/**
	 * Building clusters with pre-defined number of clusters.
	 * @param numCluster pre-defined number of clusters.
	 */
	public void buildClusters(int numCluster) {
		EM em = new EM();
		this.clusterMap.clear();
		
		try {
			em.setMaxIterations(MAX_ITERS);
			em.setNumClusters(numCluster);
			
			em.buildClusterer(this.instances);
			numCluster = em.numberOfClusters();
			if (numCluster == 0)
				return;
			
			int n = this.instances.numInstances();
			for (int i = 0; i < n; i++) {
				Instance instance = this.instances.instance(i);
				Integer clusterIdx = em.clusterInstance(instance);
				
				Cluster cluster = null;
				if (!this.clusterMap.containsKey(clusterIdx)) {
					cluster = new Cluster();
					this.clusterMap.put(clusterIdx, cluster);
				}
				else {
					cluster = this.clusterMap.get(clusterIdx);
				}
				
				int rowId = this.rowIdList.get(i);
				cluster.getIdList().add(rowId);
				cluster.addInstance(toRow(instance));
				
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LogUtil.trace(e);
		}
		
	}
	
	
	/**
	 * Converting specified instance into double array.
	 * @param instance specified instance
	 * @return row row converted from specified instance. 
	 */
	private static double[] toRow(Instance instance) {
		double[] drow = instance.toDoubleArray();
		
		for (int i = 0; i < drow.length; i++) {
			if (instance.isMissing(i))
				drow[i] = Constants.UNUSED;
		}
		return drow;
	}
	

	/**
	 * Getting list of clusters.
	 * @return list of clusters.
	 */
	public List<Cluster> getClusters() {
		Collection<Cluster> clusters = clusterMap.values();
		
		List<Cluster> result = Util.newList();
		for (Cluster cluster : clusters) {
			if (cluster.rowIdList.size() >= 2 && cluster.instances.size() >= 2)
				result.add(cluster);
		}
		
		return result;
	}
	
	
	/**
	 * Converting specified row to instance.
	 * @param row specified row
	 * @param numAttr number of attributes.
	 * @return instance of row.
	 */
	public static Instance toInstance(double[] row, int numAttr) {
		Instance instance = new Instance(numAttr);
		for (int j = 0; j < row.length; j++) {
			double value = row[j];
			if (Util.isUsed(value))
				instance.setValue(j, value);
			else
				instance.setMissing(j);
		}
		
		return instance;
	}
	
	
}

