package net.hudup.logistic.mining;

import java.util.List;

import net.hudup.core.Util;

/**
 * This class represents a cluster created from a clustering algorithm {@link Clusterer}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class Cluster {
	
	/**
	 * 
	 */
	protected List<Integer> rowIdList = Util.newList();
	
	
	/**
	 * List of instances for clustering algorithms. Each instance is a double array.
	 */
	protected List<double[]> instances = Util.newList();
	
	
	/**
	 * Default constructor.
	 */
	public Cluster() {
	
	}
	
	
	/**
	 * Getting instance at specified index.
	 * @param idx specified index.
	 * @return instance at specified index.
	 */
	public double[] getInstance(int idx) {
		return instances.get(idx);
	}
	
	
	/**
	 * Getting number of instance.
	 * @return number of instance.
	 */
	public int numInstance() {
		return instances.size();
	}
	
	
	/**
	 * Adding an instance to cluster.
	 * @param instance instance added to cluster.
	 */
	public void addInstance(double[] instance) {
		instances.add(instance);
	}
	
	
	/**
	 * Getting list of id (s).
	 * @return list of id (s).
	 */
	public List<Integer> getIdList() {
		return rowIdList;
	}
	
	
	/**
	 * Converting cluster into matrix.
	 * @return matrix converted from cluster.
	 */
	public double[][] toMatrix() {
		double[][] matrix = new double[instances.size()][];
		
		for (int i = 0; i < instances.size(); i++) {
			double[] row = instances.get(i);
			matrix[i] = row;
		}
		
		return matrix;
	}
	
	
}


