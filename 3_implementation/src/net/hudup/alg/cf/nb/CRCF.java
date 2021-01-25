/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.alg.cf.nb;

import java.rmi.RemoteException;
import java.util.Map;
import java.util.Set;

import net.hudup.core.Util;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.RatingVector;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.NextUpdate;

/**
 * This class implements the covering reduction collaborative filtering (CRFCF) algorithm.
 * CRFCF is developed by Zhipeng Zhang, Yao Zhang, Yonggong Ren and is implemented by Loc Nguyen.
 * 
 * @author Zhipeng Zhang, Yao Zhang, Yonggong Ren.
 * @version 1.0
 *
 */
@NextUpdate
public class CRCF extends NeighborCFExtUserBased {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default value for niche threshold.
	 */
	public static final double NICHE_THRESHOLD_DEFAULT = 0.5;
	
	
	/**
	 * Neighbor candidate threshold.
	 */
	public static final String NEIGHBOR_CANDIDATE_THRESHOLD_FIELD = "neighbor_candidate_threshold";
	
	
	/**
	 * Default value for neighbor candidate threshold.
	 */
	public static final double NEIGHBOR_CANDIDATE_THRESHOLD_DEFAULT = 0.5;

	
	/**
	 * Niche item set.
	 */
	protected Set<Integer> nicheSet = Util.newSet();
	

	/**
	 * user correlation matrix.
	 */
	protected Map<Integer, Map<Integer, Double>> cm = Util.newMap();
	
	
	/**
	 * Constructor.
	 */
	public CRCF() {

	}

	
	@Override
	public synchronized void setup(Dataset dataset, Object... params) throws RemoteException {
		super.setup(dataset, params);
		
		Fetcher<RatingVector> vRatings = dataset.fetchUserRatings();
		calcNicheSet(vRatings);
		calcCorrelation(vRatings);
		
		vRatings.close();
	}


	@Override
	public synchronized void unsetup() throws RemoteException {
		super.unsetup();
		
		nicheSet.clear();
		cm.clear();
	}


	/**
	 * Extracting neighbors of a given user.
	 * @param userId given user identifier.
	 * @return neighbors of a given user and its candidate neighbors.
	 */
	protected Set<Integer> extractNeighbors(int userId) {
		if (userId < 0) return Util.newSet();
		
		Set<Integer> nb = Util.newSet();
		double cnThreshold = config.getAsReal(NEIGHBOR_CANDIDATE_THRESHOLD_FIELD);
		cnThreshold = Util.isUsed(cnThreshold) ? cnThreshold : NEIGHBOR_CANDIDATE_THRESHOLD_DEFAULT;
		Set<Integer> cn = Util.newSet();
		
		return nb;
	}
	
	
	/**
	 * Calculating user correlation matrix from user rating matrix.
	 * @param vRatings user rating matrix.
	 */
	protected void calcCorrelation(Fetcher<RatingVector> vRatings) {
		if (vRatings == null) return;

		cm.clear();
	}
	
	
	/**
	 * Calculating niche item set from rating matrix.
	 * @param vRatings user rating matrix.
	 * @return niche set.
	 */
	protected void calcNicheSet(Fetcher<RatingVector> vRatings) {
		if (vRatings == null) return;
		
		nicheSet.clear();
		double nicheThreshold = calcNicheThreshold(vRatings);
		
		try {
			while (vRatings.next()) {
				RatingVector user = vRatings.pick();
				if (user == null) continue;
				
			}
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		finally {
			try {
				vRatings.reset();
			} catch (Throwable e) {LogUtil.trace(e);}
		}
		
	}
	
	
	/**
	 * Calculating niche threshold form user rating matrix.
	 * @param vRatings user rating matrix.
	 * @return niche threshold.
	 */
	private double calcNicheThreshold(Fetcher<RatingVector> vRatings) {
		if (vRatings == null) return NICHE_THRESHOLD_DEFAULT;
		
		try {
			while (vRatings.next()) {
				RatingVector user = vRatings.pick();
				if (user == null) continue;
				
			}
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		finally {
			try {
				vRatings.reset();
			} catch (Throwable e) {LogUtil.trace(e);}
		}

		return NICHE_THRESHOLD_DEFAULT;
	}
	
	
	/**
	 * Implementing the covering reduction algorithm (CRA) to extract an irreducible covering of the specified domain.
	 * @param d specified domain.
	 * @return irreducible covering of the specified domain.
	 */
	protected Set<Set<Integer>> cra(Set<Integer> d) {
		if (d == null || d.size() == 0) return Util.newSet();
		
		return Util.newSet();
	}
	
	
	@Override
	public DataConfig createDefaultConfig() {
		DataConfig config = super.createDefaultConfig();
		config.put(NEIGHBOR_CANDIDATE_THRESHOLD_FIELD, NEIGHBOR_CANDIDATE_THRESHOLD_DEFAULT);
		return config;
	}


}
