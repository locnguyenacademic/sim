package net.hudup.sparse;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import net.hudup.alg.cf.mf.SvdGradientCF;
import net.hudup.alg.cf.mf.SvdGradientKB;
import net.hudup.alg.cf.stat.BayesLookupTableCF;
import net.hudup.alg.cf.stat.BayesLookupTableKB;
import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.RecommendParam;
import net.hudup.core.alg.Recommender;
import net.hudup.core.alg.cf.CFAnnotation;
import net.hudup.core.alg.cf.MemoryBasedCF;
import net.hudup.core.alg.cf.NeighborCFUserBased;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.FetcherUtil;
import net.hudup.core.data.Rating;
import net.hudup.core.data.RatingMatrix;
import net.hudup.core.data.RatingVector;
import net.hudup.core.data.Snapshot;
import net.hudup.core.logistic.NextUpdate;
import net.hudup.data.SnapshotImpl;

/**
 * This class is a utility class for solving sparse problem. It is now needs to be improved.
 * In current implementation, it has completion methods to fulfill missing values in sparse data.
 * Sparse data has missing values that specified by {@link Constants#UNUSED}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@NextUpdate
public class SparseProcessor {

	
	/**
	 * Default constructor.
	 */
	public SparseProcessor() {
		
	}
	
	
	/**
	 * Calculating mean of specified vector.
	 * @param vector specified vector.
	 * @return mean of specified vector.
	 */
	private static double mean(double[] vector) {
		double sum = 0;
		double count = 0;
		int n = vector.length;
		for (int i = 0; i < n; i++) {
			double v = vector[i];
			if (Util.isUsed(v)) {
				sum += v;
				count++;
			}
		}
		
		return sum / count;
	}
	
	
	/**
	 * Complete sparse matrix by heuristic method.
	 * @param matrix sparse matrix.
	 * @return whether estimating successfully.
	 */
	public boolean heuristicComplete(double[][] matrix) {
		int m = matrix.length;
		
		for (int i = 0; i < m; i++) {
			double[] row = matrix[i];
			
			int n = row.length;
			for (int j = 0; j < n; j++) {
				double value = row[j];
				if (Util.isUsed(value))
					continue;
				
				double sum = 0;
				double count = 0;
				for (int k = 0; k < m; k++) {
					double v = matrix[k][j];
					if (Util.isUsed(v)) {
						sum += v;
						count++;
					}
				}
				
				double mean = 0;
				if (count == 0)
					mean = mean(row);
				else
					mean = sum / count;
				
				row[j] = mean;
				
			} // End columns
		
		} // End rows
		
		
		return true;
	}
	

	/**
	 * Completing sparse matrix by column mean.
	 * @param matrix sparse matrix.
	 * @return whether estimating successfully.
	 */
	public boolean columnMeanComplete(double[][] matrix) {
		int m = matrix.length;
		if (m == 0)
			return false;
		
		int n = matrix[0].length;
		double[] means = new double[n];
		Arrays.fill(means, Constants.UNUSED);
		
		for (int j = 0; j < n; j++) {
			double sum = 0;
			int count = 0;
			for (int i = 0; i < m; i++) {
				double value = matrix[i][j];
				
				if (Util.isUsed(value)) {
					sum += value;
					count ++;
				}
					
			}
			if (count != 0)
				means[j] = sum / (double)count;
		}
		
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				double value = matrix[i][j];
				
				if (!Util.isUsed(value))
					matrix[i][j] = means[j];
			}
		}
		
		return true;
	}
	
	
	/**
	 * Completing sparse matrix by filling specified value.
	 * @param matrix sparse matrix.
	 * @param value specified value.
	 * @return whether filling successfully
	 */
	public boolean fillValueComplete(double[][] matrix, double value) {
		int m = matrix.length;
		if (m == 0)
			return false;

		for (int i = 0; i < m; i++) {
			int n = matrix[i].length;
			for (int j = 0; j < n; j++) {
				double v = matrix[i][j];
				if (!Util.isUsed(v))
					matrix[i][j] = value;
			}
		}
		
		return true;
	}

	
	/**
	 * Completing sparse snapshot by specified algorithm.
	 * @param snapshot specified snapshot.
	 * @param completeMethod specified algorithm.
	 * @return whether estimating successfully.
	 */
	public boolean algComplete(Snapshot snapshot, Alg completeMethod) {
		
		if (completeMethod == null)
			completeMethod = new NeighborCFUserBased();
		else if (!(completeMethod instanceof Recommender) ||
				completeMethod.getClass().getAnnotation(CFAnnotation.class) == null) {
			throw new RuntimeException("Not implement yet for none-collaborative filtering algorithm");
		}
		else if (completeMethod instanceof SvdGradientCF) {
			return svdComplete(snapshot, (SvdGradientCF)completeMethod);
		}
		else if (completeMethod instanceof BayesLookupTableCF) {
			return bayesLutComplete(snapshot, (BayesLookupTableCF)completeMethod);
		}
		
		Recommender recommender = (Recommender) completeMethod;
		try {
			if (recommender instanceof MemoryBasedCF)
				recommender.setup((Snapshot)snapshot.clone());
			else
				recommender.setup(snapshot);
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				recommender.unsetup();
			}
			catch (Throwable e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			return false;
		}
		
		
		Set<Integer> totalItemIds = Util.newSet(); // total item id (s)
		FetcherUtil.fillCollection(totalItemIds, snapshot.fetchItemIds(), true);
		Fetcher<RatingVector> userRatings = snapshot.fetchUserRatings();
		
		try {
			while (userRatings.next()) {
				RatingVector userRating = userRatings.pick();
				if (userRating == null || userRating.size() == 0)
					continue;
				
				Set<Integer> ratedItemIds = userRating.fieldIds(true);
				List<Integer> unratedItemIds = Util.newList();
				for (int itemId : totalItemIds) {
					if (!ratedItemIds.contains(itemId))
						unratedItemIds.add(itemId);
				}
				
				if (unratedItemIds.size() == 0)
					continue;
				
				Set<Integer> queryIds = Util.newSet();
				queryIds.addAll(unratedItemIds); // query items are unrated items
				
				RatingVector result = recommender.estimate(new RecommendParam(userRating), queryIds);
				Set<Integer> resultIds = Util.newSet();
				if (result != null)
					resultIds = result.fieldIds();
				
				for (int resultId : resultIds) {
					Rating rating = new Rating(Constants.UNUSED);
					snapshot.putRating(userRating.id(), resultId, rating);
						
					double ratingValue = result.get(resultId).value;
					rating.value = ratingValue;
				}
				
				// Filling mean value for items not estimated
				double mean = userRating.mean();
				queryIds.removeAll(resultIds);
				for (int queryId : queryIds) {
					Rating rating = new Rating(Constants.UNUSED);
					snapshot.putRating(userRating.id(), queryId, rating);
						
					rating.value = mean;
				}
				
			}
			
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (userRatings != null)
					userRatings.close();
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
			
			try {
				recommender.unsetup();
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
		}
		
		
		return true;
	}
	
	
	/**
	 * Complete sparse rating matrix by specified algorithm.
	 * @param userMatrix user rating matrix.
	 * @param completeMethod specified algorithm.
	 * @param updateMetadata flag to indicate whether updating meta-data.
	 * @return whether estimating successfully.
	 */
	public boolean algComplete(RatingMatrix userMatrix, Alg completeMethod, boolean updateMetadata) {
		if (completeMethod == null)
			completeMethod = new NeighborCFUserBased();
		else if (!(completeMethod instanceof Recommender) ||
				completeMethod.getClass().getAnnotation(CFAnnotation.class) == null) {
			throw new RuntimeException("Not implement yet for none-collaborative filtering algorithm");
		}
		else if (completeMethod instanceof net.hudup.alg.cf.stat.MeanItemCF) {
			boolean result = columnMeanComplete(userMatrix.matrix);
			if (updateMetadata)
				userMatrix.updateMetadata();
			return result;
		}
		else if (completeMethod instanceof SvdGradientCF) {
			return svdComplete(userMatrix, (SvdGradientCF) completeMethod);
		}
		else if (completeMethod instanceof BayesLookupTableCF) {
			throw new RuntimeException("Not implement yet");
		}
		
		SnapshotImpl snapshot = SnapshotImpl.create(userMatrix, true);
		if (snapshot == null)
			return false;
		if (!algComplete(snapshot, completeMethod))
			return false;
		
		RatingMatrix newMatrix = snapshot.createUserMatrix();
		snapshot.clear();
		if (newMatrix == null)
			return false;
		
		userMatrix.clear();
		userMatrix.assign(newMatrix);
		return true;
	}
	
	
	/**
	 * Completing sparse snapshot by SVD collaborative filtering algorithm {@link SvdGradientCF}.
	 * @param snapshot sparse snapshot.
	 * @param svdCf SVD collaborative filtering algorithm.
	 * @return whether estimating successfully.
	 */
	private boolean svdComplete(Snapshot snapshot, SvdGradientCF svdCf) {
		
		boolean result = true;
		try {
			svdCf.setup(snapshot);
			
			SvdGradientKB gradientKb = (SvdGradientKB)svdCf.getKBase();
			List<Integer> userIds = gradientKb.getUserIds();
			List<Integer> itemIds = gradientKb.getItemIds();
			
			for (int userId : userIds) {
				
				for (int itemId : itemIds) {
					Rating rating = snapshot.getRating(userId, itemId);
					if (rating != null && rating.isRated())
						continue;
					
					double ratingValue = gradientKb.estimate(userId, itemId);
					if (!Util.isUsed(ratingValue)) {
						RatingVector vRating = snapshot.getItemRating(itemId);
						ratingValue = vRating.mean();
					}
					
					if (rating == null) {
						rating = new Rating(ratingValue);
						snapshot.putRating(userId, itemId, rating);
					}
					else
						rating.value = ratingValue;
				}
			}
		}
		catch (Throwable e) {
			e.printStackTrace();
			result = false;
		}
		finally {
			try {
				svdCf.unsetup();
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	
	/**
	 * Completing sparse user rating matrix by SVD collaborative filtering algorithm {@link SvdGradientCF}.
	 * @param userMatrix sparse user rating matrix.
	 * @param svdCf SVD collaborative filtering algorithm {@link SvdGradientCF}.
	 * @param updateMetadata flag to indicate whether updating meta-data.
	 * @return whether estimating successfully.
	 */
	private boolean svdComplete(RatingMatrix userMatrix, SvdGradientCF svdCf) {
		
		boolean result = true;
		try {
			svdCf.setup0(userMatrix);
			
			SvdGradientKB gradientKb = (SvdGradientKB)svdCf.getKBase();
			RatingMatrix newMatrix = gradientKb.createUserRatingMatrix();
			if (newMatrix == null)
				result = false;
			else
				userMatrix.assign(newMatrix);
		}
		catch (Throwable e) {
			e.printStackTrace();
			result = false;
		}
		finally {
			try {
				svdCf.unsetup();
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
		}
		
		return result;

	}


	/**
	 * Completing sparse snapshot by Bayesian lookup table collaborative filtering algorithm {@link BayesLookupTableCF}.
	 * @param snapshot sparse snapshot.
	 * @param bayesLutCf Bayesian lookup table collaborative filtering algorithm {@link BayesLookupTableCF}.
	 * @return whether estimating successfully.
	 */
	private boolean bayesLutComplete(Snapshot snapshot, BayesLookupTableCF bayesLutCf) {
		
		boolean result = true;
		try {
			bayesLutCf.setup(snapshot);
			
			BayesLookupTableKB bayesLutKb = (BayesLookupTableKB)bayesLutCf.getKBase();
			Set<Integer> userIds = bayesLutKb.getUserIds();
			List<Integer> itemIds = bayesLutKb.getItemIds();
			
			for (int userId : userIds) {
				
				for (int itemId : itemIds) {
					Rating rating = snapshot.getRating(userId, itemId);
					if (rating != null && rating.isRated())
						continue;
					
					double ratingValue = bayesLutKb.estimate(userId, itemId);
					if (!Util.isUsed(ratingValue)) {
						RatingVector vRating = snapshot.getItemRating(itemId);
						ratingValue = vRating.mean();
					}
					
					if (rating == null) {
						rating = new Rating(ratingValue);
						snapshot.putRating(userId, itemId, rating);
					}
					else
						rating.value = ratingValue;
				}
			}
		}
		catch (Throwable e) {
			e.printStackTrace();
			result = false;
		}
		finally {
			try {
				bayesLutCf.unsetup();
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}


}
