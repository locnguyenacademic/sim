/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.alg.cf;

import java.awt.Component;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;

import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.Profile;
import net.hudup.core.data.RatingVector;

/**
 * This abstract class implements basically the combined collaborative filtering algorithm that combines two measures.<br>
 * Author Ali Amer proposed such combination.
 * 
 * @author Ali Amer
 * @version 1.0
 *
 */
public abstract class NeighborCFTwosCombined extends NeighborCFExt {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Name of weight 1 field.
	 */
	public static final String COMBINED_WEIGHT1_FIELD = "combined_weight1";

	
	/**
	 * Default weight 1 field.
	 */
	public static final double COMBINED_WEIGHT1_DEFAULT = 0.5;

	
	/**
	 * Name of other measure field.
	 */
	public static final String OTHER_MEASURE = "other_measure";

	
	/**
	 * Name of weight 2 field.
	 */
	public static final String COMBINED_WEIGHT2_FIELD = "combined_weight2";

	
	/**
	 * Default weight 2 field.
	 */
	public static final double COMBINED_WEIGHT2_DEFAULT = 0.5;

	
	/**
	 * Combined min-max mode.
	 */
	public static final String COMBINED_MINMAX_MODE_FIELD = "combined_minmax";

	
	/**
	 * Default combined measure.
	 */
	public static final boolean COMBINED_MINMAX_MODE_DEFAULT = true;

	
	/**
	 * Combined type field.
	 */
	public static final String COMBINED_TYPE_FIELD = "combined_type";

	
	/**
	 * Additional combined type
	 */
	public static final int COMBINED_TYPE_ADD = 0;
	
	
	/**
	 * Multiplication combined type
	 */
	public static final int COMBINED_TYPE_MULTIPLY = 1;

	
	/**
	 * Dual CF algorithm.
	 */
	protected NeighborCFExt dualCF = createDualCF();
	
	
	/**
	 * Default constructor.
	 */
	public NeighborCFTwosCombined() {
		// TODO Auto-generated constructor stub
		this.dualCF.setConfig(this.getConfig());
	}

	
	@Override
	public synchronized void setup(Dataset dataset, Object...params) throws RemoteException {
		// TODO Auto-generated method stub
		super.setup(dataset, params);
		this.dualCF.setup(dataset, params);
	}


	@Override
	public synchronized void unsetup() throws RemoteException {
		// TODO Auto-generated method stub
		super.unsetup();
		this.dualCF.unsetup();
	}

	
	/**
	 * Getting the similarity measure.
	 * @return similar measure.
	 */
	public String getOtherSimilarMeasure() {
		String measure = config.getAsString(OTHER_MEASURE);
		if (measure == null)
			return getDefaultMeasure();
		else
			return measure;
	}
	
	
	/**
	 * Setting the similarity measure.
	 * @param measure the similarity measure.
	 */
	public void setOtherSimilarMeasure(String measure) {
		config.put(OTHER_MEASURE, measure);
	}

	
	@Override
	public boolean requireDiscreteRatingBins() {
		// TODO Auto-generated method stub
		return requireDiscreteRatingBins(getMeasure())
				|| requireDiscreteRatingBins(getOtherSimilarMeasure());
	}


	/**
	 * Creating supported combined types.
	 * @return list of supported combined types.
	 */
	public List<Integer> getSupportedCombinedTypes() {
		// TODO Auto-generated method stub
		Set<Integer> ctSet = Util.newSet();
		ctSet.add(COMBINED_TYPE_ADD);
		ctSet.add(COMBINED_TYPE_MULTIPLY);
		
		List<Integer> combinedTypes = Util.newList(ctSet.size());
		combinedTypes.addAll(ctSet);
		Collections.sort(combinedTypes);
		return combinedTypes;
	}

	
	@Override
	protected double sim0(String measure, RatingVector vRating1, RatingVector vRating2, Profile profile1, Profile profile2, Object...params) {
		// TODO Auto-generated method stub
		double similar = super.sim0(measure, vRating1, vRating2, profile1, profile2, params);
		String otherMeasure = config.getAsString(OTHER_MEASURE);
		if (otherMeasure == null) return similar;
		double otherSimilar = dualCF.sim0(otherMeasure, vRating1, vRating2, profile1, profile2, params);
		
		int combinedType = config.getAsInt(COMBINED_TYPE_FIELD);
		boolean minmax = config.getAsBoolean(COMBINED_MINMAX_MODE_FIELD);
		if (minmax) {
			if (similar >= otherSimilar)
				return operator(similar, otherSimilar, 0.67, 0.33, combinedType); //Author Ali Amer proposed these weights (0.67 and 0.33)
			else
				return operator(similar, otherSimilar, 0.33, 0.67, combinedType); //Author Ali Amer proposed these weights (0.67 and 0.33)
		}
		else {
			double w1 = config.getAsReal(COMBINED_WEIGHT1_FIELD);
			double w2 = config.getAsReal(COMBINED_WEIGHT2_FIELD);
			return operator(similar, otherSimilar, w1, w2, combinedType);
		}
	}


	/**
	 * Operating on two similarity measures with two weights.
	 * @param sim1 similarity measure 1.
	 * @param sim2 similarity measure 2.
	 * @param weight1 weight 1.
	 * @param weight2 weight 2.
	 * @param op operator.
	 * @return result of operating on two similarity measures with two weights.
	 */
	protected double operator(double sim1, double sim2, double weight1, double weight2, int op) {
		if (op == COMBINED_TYPE_ADD)
			return weight1*sim1 + weight2*sim2;
		else if (op == COMBINED_TYPE_MULTIPLY)
			return weight1*sim1 * weight2*sim2;
		else
			return Constants.UNUSED;
	}
	
	
	/**
	 * Getting minimum-maximum mode.
	 * @return true if minimum-maximum mode is used.
	 */
	public boolean isMinmax() {
		return getConfig().getAsBoolean(COMBINED_MINMAX_MODE_FIELD);
	}
	
	
	/**
	 * Setting minimum-maximum mode.
	 * @param minmax minimum-maximum mode.
	 */
	public void setMinmax(boolean minmax) {
		getConfig().put(COMBINED_MINMAX_MODE_FIELD, minmax);
	}

	
	@Override
	public DataConfig createDefaultConfig() {
		// TODO Auto-generated method stub
		DataConfig tempConfig = super.createDefaultConfig();
		tempConfig.put(MEASURE, AMER);
		tempConfig.put(COMBINED_WEIGHT1_FIELD, COMBINED_WEIGHT1_DEFAULT);
		tempConfig.put(OTHER_MEASURE, AMER2); tempConfig.addReadOnly(OTHER_MEASURE);
		tempConfig.put(COMBINED_WEIGHT2_FIELD, COMBINED_WEIGHT2_DEFAULT);
		tempConfig.put(COMBINED_MINMAX_MODE_FIELD, COMBINED_MINMAX_MODE_DEFAULT);
		tempConfig.put(COMBINED_TYPE_FIELD, COMBINED_TYPE_ADD); tempConfig.addReadOnly(COMBINED_TYPE_FIELD);

		DataConfig config = new DataConfig() {

			/**
			 * Serial version UID for serializable class. 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Serializable userEdit(Component comp, String key, Serializable defaultValue) {
				// TODO Auto-generated method stub
				if (key.equals(OTHER_MEASURE)) {
					String measure = getAsString(OTHER_MEASURE);
					measure = measure == null ? getDefaultMeasure() : measure;
					return (Serializable) JOptionPane.showInputDialog(
							comp, 
							"Please choose one similar measure", 
							"Choosing similar measure", 
							JOptionPane.INFORMATION_MESSAGE, 
							null, 
							getSupportedMeasures().toArray(), 
							measure);
				}
				else if (key.equals(COMBINED_TYPE_FIELD)) {
					int combinedType = getAsInt(COMBINED_TYPE_FIELD);
					combinedType = combinedType < 0 ? COMBINED_TYPE_ADD : combinedType;
					return (Serializable) JOptionPane.showInputDialog(
							comp, 
							"Please choose one combined type", 
							"Choosing combined type", 
							JOptionPane.INFORMATION_MESSAGE, 
							null, 
							getSupportedCombinedTypes().toArray(), 
							combinedType);
				}
				else 
					return tempConfig.userEdit(comp, key, defaultValue);
			}
			
		};
		
		config.putAll(tempConfig);
		return config;
	}
	
	
	/**
	 * Create dual CF algorithm.
	 * @return dual CF algorithm.
	 */
	protected abstract NeighborCFExt createDualCF();
	
	
}
