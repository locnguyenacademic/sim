package net.rem.em;

import java.io.Serializable;

import net.hudup.core.alg.SetupAlgEvent;
import net.hudup.core.data.Dataset;
import net.hudup.core.logistic.NextUpdate;

/**
 * This class represents events when expectation maximization (EM) algorithm runs.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class EMLearningEvent extends SetupAlgEvent {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Current iteration.
	 */
	protected Serializable currentIteration = 0;

	
	/**
	 * Sufficient statistics.
	 */
	protected Serializable currentStatistics = null;
	
	
	/**
	 * Current parameter.
	 */
	protected Serializable currentParameter = null;

	
	/**
	 * Estimated parameter.
	 */
	protected Serializable estimatedParameter = null;

	
	/**
	 * Default constructor with the source as EM algorithm.
	 * @param em the EM algorithm as the source of this event.
	 * @param type event type.
	 * @param trainingDataset training dataset.
	 * @param currentIteration current iteration.
	 * @param currentStatistic current sufficient statistic.
	 * @param currentParameter current parameter.
	 * @param estimatedParameter estimated parameter of algorithm as setup result.
	 */
	@NextUpdate
	public EMLearningEvent(EM em, Type type, Dataset trainingDataset,
			int currentIteration, Serializable currentStatistic,
			Serializable currentParameter, Serializable estimatedParameter) {
		// TODO Auto-generated constructor stub
		super(em, type, null, trainingDataset, estimatedParameter); //The EM wrapper can solve the problem of different hosts.
		this.alg = (EM) getSource();
		this.currentIteration = currentIteration;
		this.currentStatistics = currentStatistic;
		this.currentParameter = currentParameter; 
		this.estimatedParameter = estimatedParameter;
	}

	
	@Override
	public String translate() {
		return translate((EM) getSource(), false);
	}
	
	
	/**
	 * Translating this event into text with specified EM algorithm.
	 * @param em EM algorithm.
	 * @return translated text of this event.
	 */
	public String translate(EM em) {
		return translate(em, false);
	}
	
	
	/**
	 * Translate this event into text.
	 * @param em EM algorithm.
	 * @param showStatistic whether sufficient statistic is shown.
	 * This parameter is established because sufficient statistic often as a collection (fetcher) is very large.
	 * @return translated text from content of this event.
	 */
	protected String translate(EM em, boolean showStatistic) {
		if (em == null) return "";
		
		StringBuffer buffer = new StringBuffer();
		try { 
			buffer.append("At the " + currentIteration + " iteration");
			buffer.append(" of algorithm \"" + em.getName() + "\"");
			if (getTrainingDataset() != null) {
				String mainUnit = getTrainingDataset().getConfig().getMainUnit();
				String datasetName = getTrainingDataset().getConfig().getAsString(mainUnit);
				if (datasetName != null)
					buffer.append(" on training dataset \"" + datasetName + "\"");
			}

			if (currentStatistics != null && showStatistic) {
				buffer.append("\nCurrent statistic:");
				buffer.append("\n  " + currentStatistics.toString());
			}
			
			if (currentParameter != null) {
				buffer.append("\nCurrent parameter:");
				buffer.append("\n  " + em.parameterToShownText(currentParameter));
			}
			
			if (estimatedParameter != null) {
				buffer.append("\nEstimated parameter:");
				buffer.append("\n  " + em.parameterToShownText(estimatedParameter));
			}
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		
		return buffer.toString();
	}
	
	
}
