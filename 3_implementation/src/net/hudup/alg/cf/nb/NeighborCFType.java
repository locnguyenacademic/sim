/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.alg.cf.nb;

import java.awt.Component;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;

import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.Profile;
import net.hudup.core.data.Rating;
import net.hudup.core.data.RatingVector;
import net.hudup.core.logistic.LogUtil;

/**
 * This class implements nearest neighbors collaborative filtering algorithms with type support.
 * The ideology of type support is developed by Hael Al-bashiri and Norazuwa Binti Salehudin.
 * 
 * @author Hael Al-bashiri, Norazuwa Binti Salehudinn. Developed by Loc Nguyen
 * @version 1.0
 *
 */
public abstract class NeighborCFType extends NeighborCFExt {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Dataset name.
	 */
	protected static final String DATASET_FIELD = "dataset";

	
	/**
	 * Default dataset.
	 */
	protected static final String DATASET_MOVIELENS100K = "movielens100k";

	
	/**
	 * Default dataset.
	 */
	protected static final String DATASET_MOVIELENS1M = "movielens1m";

	
	/**
	 * Frequency mode.
	 */
	protected static final String FREQUENCY_FIELD = "frequency";

	
	/**
	 * Default value for frequency mode.
	 */
	protected static final boolean FREQUENCY_DEFAULT = false;

	
	/**
	 * Map of user rating vector, which supports type.
	 */
	protected Map<Integer, RatingVector> userTypeMap = Util.newMap();
	
	
	/**
	 * Map of item rating vector, which supports type.
	 */
	protected Map<Integer, RatingVector> itemTypeMap = Util.newMap();
	
	
	/**
	 * Default constructor.
	 */
	public NeighborCFType() {
		
	}


	@Override
	public synchronized void setup(Dataset dataset, Object... params) throws RemoteException {
		super.setup(dataset, params);
		
		try {
			Fetcher<RatingVector> users = dataset.fetchUserRatings();
			while (users.next()) {
				RatingVector user = users.pick();
				if (user == null) continue;
				
				RatingVector tRating = extractUserTypeRating(user);
				if (tRating != null) userTypeMap.put(tRating.id(), tRating);
			}
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
		
		try {
			Fetcher<RatingVector> items = dataset.fetchItemRatings();
			while (items.next()) {
				RatingVector item = items.pick();
				if (item == null) continue;
				
				RatingVector tRating = extractItemTypeRating(item);
				if (tRating != null) itemTypeMap.put(tRating.id(), tRating);
			}
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
	}
	
	
	@Override
	public synchronized void unsetup() throws RemoteException {
		super.unsetup();
		
		userTypeMap.clear();
		itemTypeMap.clear();
	}


	/**
	 * Getting dataset types.
	 * @return dataset types.
	 */
	public List<String> getDatasetTypes() {
		Set<String> dSet = Util.newSet();
		dSet.add(DATASET_MOVIELENS100K);
		dSet.add(DATASET_MOVIELENS1M);
		
		List<String> datasetTypes = Util.newList();
		datasetTypes.addAll(dSet);
		Collections.sort(datasetTypes);
		return datasetTypes;

	}
	
	
	@Override
	protected double sim0(String measure, RatingVector vRating1, RatingVector vRating2, Profile profile1, Profile profile2, Object... params) {
		RatingVector tRating1 = convertToTypeVector(vRating1);
		RatingVector tRating2 = convertToTypeVector(vRating2);
		if (config.getAsBoolean(FREQUENCY_FIELD)) {
			Collection<Rating> ratings = tRating1.gets();
			for (Rating rating : ratings) rating.value = rating.ratedDate;
			
			ratings = tRating2.gets();
			for (Rating rating : ratings) rating.value = rating.ratedDate;
		}
		
		if (tRating1 == null || tRating2 == null || tRating1.size() == 0 || tRating2.size() == 0)
			return Constants.UNUSED;
		else
			return super.sim0(measure, tRating1, tRating2, profile1, profile2, params);
	}


	/**
	 * Converting user/item rating vector to type vector.
	 * @param vRating user/item rating vector.
	 * @return
	 */
	protected abstract RatingVector convertToTypeVector(RatingVector vRating);
	
	
	/**
	 * Getting type support.
	 * @return type support.
	 */
	protected TypeSupport getTypeSupport() {
		String dataset = config.getAsString(DATASET_FIELD);
		if (dataset == null)
			return null;
		else if (dataset.equals(DATASET_MOVIELENS100K) || dataset.equals(DATASET_MOVIELENS1M)) {
			return new TypeSupport() {
				
				@Override
				public int getUserTypeCount() {
					return 21;
				}
				
				@Override
				public int getItemTypeCount() {
					return 19;
				}
				
				@Override
				public boolean[] extractUserType(Profile user) {
					if (user == null) return null;
					
					boolean[] type = new boolean[21];
					for (int i = 0; i < 21; i++) type[i] = false;
					type[user.getValueAsInt(3)] = true;
					
					return type;
				}
				
				@Override
				public boolean[] extractItemType(Profile item) {
					if (item == null) return null;
					
					boolean[] type = new boolean[19];
					for (int i = 0; i < 19; i++) type[i] = item.getValueAsBoolean(i + 5);
					
					return type;
				}
			};
		}
		else
			return null;
	}
	
	
	/**
	 * Fetching user type vectors.
	 * @return collection of user type vectors.
	 */
	public Collection<RatingVector> fetchUserTypes() {
		return userTypeMap.values();
	}
	
	
	/**
	 * Fetching item type vectors.
	 * @return collection of item type vectors.
	 */
	public Collection<RatingVector> fetchItemTypes() {
		return itemTypeMap.values();
	}
	
	
	/**
	 * Extracting type rating vector of given user rating vector.
	 * @param vRating specified user rating vector.
	 * @return type rating vector of given user rating vector. 
	 */
	protected RatingVector extractUserTypeRating(RatingVector vRating) {
		TypeSupport ts = getTypeSupport();
		if (ts == null) return null;
		
		RatingVector vType = new RatingVector(vRating.id());
		Set<Integer> fieldIds = vRating.fieldIds(true);
		for (int fieldId : fieldIds) {
			Profile profile = dataset.getItemProfile(fieldId);
			if (profile == null) continue;
			
			boolean[] types = ts.extractItemType(profile);
			if (types == null || types.length == 0) continue;
			
			double value = vRating.get(fieldId).value;
			for (int i = 0; i < types.length; i++) {
				if (!types[i]) continue;
				Rating rating = null;
				if (vType.contains(i)) {
					rating = vType.get(i);
					rating.value = (rating.value * rating.ratedDate + value) / (rating.ratedDate + 1);
					rating.ratedDate = rating.ratedDate + 1;
				}
				else {
					rating = new Rating(value);
					rating.ratedDate = 1;
					vType.put(i, rating);
				}
			}
		}
		
		return vType.size() > 0 ? vType : null;
	}
	
	
	/**
	 * Extracting type rating vector of given item rating vector.
	 * @param vRating specified item rating vector.
	 * @return type rating vector of given item rating vector. 
	 */
	protected RatingVector extractItemTypeRating(RatingVector vRating) {
		TypeSupport ts = getTypeSupport();
		if (ts == null) return null;
		
		RatingVector vType = new RatingVector(vRating.id());
		Set<Integer> fieldIds = vRating.fieldIds(true);
		for (int fieldId : fieldIds) {
			Profile profile = dataset.getUserProfile(fieldId);
			if (profile == null) continue;
			
			boolean[] types = ts.extractUserType(profile);
			if (types == null || types.length == 0) continue;
			
			double value = vRating.get(fieldId).value;
			for (int i = 0; i < types.length; i++) {
				if (!types[i]) continue;
				Rating rating = null;
				if (vType.contains(i)) {
					rating = vType.get(i);
					rating.value = (rating.value * rating.ratedDate + value) / (rating.ratedDate + 1);
					rating.ratedDate = rating.ratedDate + 1;
				}
				else {
					rating = new Rating(value);
					rating.ratedDate = 1;
					vType.put(i, rating);
				}
			}
		}
		
		return vType.size() > 0 ? vType : null;
	}
	
	
	@Override
	public DataConfig createDefaultConfig() {
		DataConfig tempConfig = super.createDefaultConfig();
		tempConfig.put(DATASET_FIELD, DATASET_MOVIELENS100K);
		tempConfig.put(FREQUENCY_FIELD, FREQUENCY_DEFAULT);

		DataConfig config = new DataConfig() {

			/**
			 * Serial version UID for serializable class. 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Serializable userEdit(Component comp, String key, Serializable defaultValue) {
				if (key.equals(DATASET_FIELD)) {
					String dataset = getAsString(DATASET_FIELD);
					dataset = dataset == null ? DATASET_MOVIELENS100K : dataset;
					return (Serializable) JOptionPane.showInputDialog(
						comp, 
						"Please choose one dataset", 
						"Choosing dataset", 
						JOptionPane.INFORMATION_MESSAGE, 
						null, 
						getDatasetTypes().toArray(), 
						dataset);
				}
				else 
					return tempConfig.userEdit(comp, key, defaultValue);
			}
			
		};

		config.putAll(tempConfig);

		return config;
	}


	/**
	 * This interface provides methods to process on user types and item types.
	 * @author Loc Nguyen
	 * @version 1.0
	 */
	public static interface TypeSupport {
		
		
		/**
		 * Getting number of user type.
		 * @return number of user type.
		 */
		int getUserTypeCount();
		
		
		/**
		 * Getting number of item type.
		 * @return number of item type.
		 */
		int getItemTypeCount();
		
		
		/**
		 * Extracting type vector of given user profile.
		 * @param user specified user profile.
		 * @return type vector of given user profile. 
		 */
		boolean[] extractUserType(Profile user);
		
		
		/**
		 * Extracting type vector of given item profile.
		 * @param item specified item profile.
		 * @return type vector of given item profile. 
		 */
		boolean[] extractItemType(Profile item);
		
		
	}
	
	
}
