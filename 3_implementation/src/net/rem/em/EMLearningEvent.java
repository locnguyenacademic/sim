package net.rem.em;

import java.io.Serializable;

import net.hudup.core.PluginStorage;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.SetupAlgEvent;
import net.hudup.core.data.Dataset;
import net.hudup.core.logistic.LogUtil;

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
	protected int currentIteration = 0;

	
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
	 * @param em the EM algorithm as the source of this event. This EM algorithm is invalid in remote call because the source is transient variable.
	 * @param type event type.
	 * @param trainingDataset training dataset.
	 * @param currentIteration current iteration.
	 * @param currentStatistic current sufficient statistic.
	 * @param currentParameter current parameter.
	 * @param estimatedParameter estimated parameter of algorithm as setup result.
	 */
	public EMLearningEvent(EM em, Type type, Dataset trainingDataset,
			int currentIteration, int maxIteration, Serializable currentStatistic,
			Serializable currentParameter, Serializable estimatedParameter) {
		// TODO Auto-generated constructor stub
		super(em, type, em.getName(), trainingDataset, estimatedParameter, currentIteration, maxIteration);
		this.currentIteration = currentIteration;
		this.currentStatistics = currentStatistic;
		this.currentParameter = currentParameter; 
		this.estimatedParameter = estimatedParameter;
	}

	
	/**
	 * Getting source as EM algorithm. This method cannot be called remotely because the source is transient variable.
	 * @return source as EM algorithm.
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private EM getEM() {
		Object source = getSource();
		if (source == null)
			return null;
		else if (source instanceof EM)
			return (EM)source;
		else
			return null;
	}

	
	@Override
	public String translate() {
		Alg alg = PluginStorage.getNormalAlgReg().query(getAlgName());
		if ((alg == null) || !(alg instanceof EM))
			return "";
		else
			return translate((EM)alg, false);
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
			LogUtil.trace(e);
		}
		
		return buffer.toString();
	}
	
	
}
