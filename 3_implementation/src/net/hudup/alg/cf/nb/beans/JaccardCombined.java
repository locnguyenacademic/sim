/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.alg.cf.nb.beans;

import java.awt.Component;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;

import net.hudup.alg.cf.nb.Measure;
import net.hudup.alg.cf.nb.NeighborCFExtUserBased;
import net.hudup.core.Util;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Profile;
import net.hudup.core.data.RatingVector;
import net.hudup.core.evaluate.recommend.Accuracy;

/**
 * Combined Jaccard measure.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class JaccardCombined extends NeighborCFExtUserBased {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Name of other measure field.
	 */
	protected static final String OTHER_MEASURE = "measure_other";

	
	/**
	 * Temporal value cache.
	 */
	protected Map<Integer, Object> valueCache2 = Util.newMap();

	
	/**
	 * Default constructor.
	 */
	public JaccardCombined() {

	}

	
	@Override
	protected void updateConfig(String measure) {
		super.updateConfig(measure);
		
		config.remove(MEASURE);
	}


	@Override
	protected double sim0(String measure, RatingVector vRating1, RatingVector vRating2, Profile profile1,
			Profile profile2, Object... params) {
		double jacc =  jaccard(vRating1, vRating2, profile1, profile2);
		String otherMeasure = config.getAsString(OTHER_MEASURE);
		if (otherMeasure == null)
			return jacc;
		else {
			double sim = super.sim0(otherMeasure, vRating1, vRating2, profile1, profile2, params);
			return Util.isUsed(sim) ? jacc*sim : jacc;
		}
	}

	
	@Override
	protected double[] improvedJaccardCalcSingularities(int columnId) {
		Task task = new Task() {
			
			@Override
			public Object perform(Object...params) {
				RatingVector columnVector = getColumnRating(columnId);
				if (columnVector == null || columnVector.size() == 0)
					return null;
				
				Set<Integer> columnIds = getColumnIds();
				double total = columnIds.size();
				if (total == 0) return null;
				int P = 0, N = 0, E = 0;
				for (int columnId : columnIds) {
					if (columnVector.isRated(columnId)) {
						double rating = columnVector.get(columnId).value;
						if (Accuracy.isRelevant(rating, ratingMedian))
							P++;
						else
							N++;
					}
					else
						E++;
				}
				
				return new double[] {1.0-(double)P/total, 1.0-(double)N/total, 1.0-(double)E/total};
			}
		};
		
		return (double[])cacheTask(columnId, this.valueCache2, task);
	}

	
	@Override
	public String getName() {
		String name = getConfig().getAsString(DUPLICATED_ALG_NAME_FIELD);
		if (name != null && !name.isEmpty())
			return name;
		else
			return "neighborcf_jaccard_combined";
	}


	@Override
	public DataConfig createDefaultConfig() {
		DataConfig tempConfig = super.createDefaultConfig();
		tempConfig.put(OTHER_MEASURE, Measure.COSINE);

		DataConfig config = new DataConfig() {

			/**
			 * Serial version UID for serializable class.
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Serializable userEdit(Component comp, String key, Serializable defaultValue) {
				if (key.equals(OTHER_MEASURE)) {
					String measure = getAsString(OTHER_MEASURE);
					measure = measure == null ? getDefaultMeasure() : measure;
					return (Serializable) JOptionPane.showInputDialog(
						comp, 
						"Please choose one other similar measure", 
						"Choosing other similar measure", 
						JOptionPane.INFORMATION_MESSAGE, 
						null, 
						getMainMeasures().toArray(), 
						measure);
				}
				else
					return tempConfig.userEdit(comp, key, defaultValue);
			}
			
		};

		config.putAll(tempConfig);
		
		return config;
	}


}
