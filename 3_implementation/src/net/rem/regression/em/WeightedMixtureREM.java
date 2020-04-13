package net.rem.regression.em;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;

import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.data.DataConfig;
import net.hudup.core.logistic.DSUtil;
import net.hudup.core.logistic.LogUtil;
import net.rem.regression.LargeStatistics;
import net.rem.regression.em.ExchangedParameter.NormalDisParameter;

/**
 * This class implements the mixture regression model with weighting mechanism.
 * In fact, weights are added to EM coefficients. In this current implementation, these weights are response probabilities P(Z|X) and regressor probabilities P(X).
 * The method {@link #adjustMixtureParameters()} is responsible for calculating these weights. 
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class WeightedMixtureREM extends DefaultMixtureREM {

	
	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public WeightedMixtureREM() {

	}


	@Override
	protected boolean adjustMixtureParameters() throws RemoteException {
		super.adjustMixtureParameters();
		
		for (REMImpl rem : rems) {
			ExchangedParameter parameter = null;
			LargeStatistics stat = null;
			try {
				parameter = (ExchangedParameter)rem.getParameter();
				stat = (LargeStatistics) rem.expectation(parameter, this.data);
			} 
			catch (Exception e) {LogUtil.trace(e);}
			
			NormalDisParameter xNormalDisParameter = new NormalDisParameter(stat);
			parameter.setXNormalDisParameter(xNormalDisParameter);
		}
		
		return true;
	}


	/**
	 * Determining weights.
	 * @param xStatistic specified X statistic.
	 * @return weights.
	 */
	@SuppressWarnings("unchecked")
	private List<Double> calcWeights(double[] xStatistic) {
		List<ExchangedParameter> parameters = null;
		try {
			parameters = (List<ExchangedParameter>)getParameter();
		} catch (Exception e) {LogUtil.trace(e);}
		if (parameters == null || parameters.size() == 0) return Util.newList();
		
		List<Double> weights = Util.newList(parameters.size());
		double sumWeight = 0;
		for (ExchangedParameter parameter : parameters) {
			double weight = ExchangedParameter.normalPDF(
				DSUtil.toDoubleList(Arrays.copyOfRange(xStatistic, 1, xStatistic.length)),
				parameter.getXNormalDisParameter().getMean(),
				parameter.getXNormalDisParameter().getVariance());
			weights.add(weight);
			sumWeight += weight;
		}
		
		if (sumWeight != 0) {
			for (int i = 0; i < weights.size(); i++)
				weights.set(i, weights.get(i) / sumWeight);
		}
		else {
			for (int i = 0; i < weights.size(); i++)
				weights.set(i, 1.0 / (double)weights.size());
		}
		
		return weights;
	}
	
	
	@Override
	public synchronized double executeByXStatistic(double[] xStatistic) throws RemoteException {
		if (this.rems == null || this.rems.size() == 0 || xStatistic == null)
			return Constants.UNUSED;
		
		List<Double> values = Util.newList(rems.size());
		List<Double> weights = calcWeights(xStatistic);
		List<Double> coeffs = Util.newList(rems.size());
		double sumCoeffs = 0;
		for (int i = 0; i < rems.size(); i++) {
			REMImpl rem = rems.get(i);
			ExchangedParameter parameter = rem.getExchangedParameter();
			
			double value = parameter.mean(xStatistic);
			values.add(value);
			
			double pdf = 1; //= ExchangedParameter.normalPDF(value, value, parameter.getZVariance());
			double coeff = parameter.getCoeff() * pdf;
			if (weights.size() == rems.size())
				coeff *= weights.get(i);
			
			coeffs.add(coeff);
			sumCoeffs += coeff;
		}
		
		double result = 0;
		for (int i = 0; i < rems.size(); i++) {
			result += (coeffs.get(i)/sumCoeffs) * values.get(i);
		}
		
		return (double)transformResponse(result, true);
	}


	@Override
	public synchronized Object execute(Object input) throws RemoteException {
		if (this.rems == null || this.rems.size() == 0)
			return Constants.UNUSED;
		
		double[] xStatistic = rems.get(0).extractAndTransformRegressorValues(input);
		return executeByXStatistic(xStatistic);
	}

	
	@Override
	public String getName() {
		String name = getConfig().getAsString(DUPLICATED_ALG_NAME_FIELD);
		if (name != null && !name.isEmpty())
			return name;
		else
			return "weighted_mixrem";
	}

	
	@Override
	public Alg newInstance() {
		WeightedMixtureREM weightedREM = new WeightedMixtureREM();
		weightedREM.getConfig().putAll((DataConfig)this.getConfig().clone());
		return weightedREM;
	}


//	/**
//	 * Adjusting weights of EM coefficients according to maximum mechanism.
//	 */
//	@SuppressWarnings({ "unchecked", "unused" })
//	@Deprecated
//	private void adjustWeights0() {
//		List<ExchangedParameter> parameters = null;
//		try {
//			parameters = (List<ExchangedParameter>)getParameter();
//		} catch (Exception e) {LogUtil.trace(e);}
//		if (parameters == null || parameters.size() == 0) return;
//		
//		int maxIdx = -1;
//		double maxCoeff = -1;
//		for (int k = 0; k < parameters.size(); k++) {
//			double coeff = parameters.get(k).getCoeff();
//			if (coeff > maxCoeff) {
//				maxCoeff = coeff;
//				maxIdx = k;
//			}
//		}
//		
//		if (maxIdx >= 0) {
//			for (int k = 0; k < parameters.size(); k++) {
//				if (k == maxIdx)
//					parameters.get(k).setCoeff(1);
//				else
//					parameters.get(k).setCoeff(0);
//			}
//		}
//	}
//	
//	
//	@Override
//	protected REMImpl createREM() {
//		return new WeightedREMExt();
//	}
//
//
//	/**
//	 * This class is an extension of regression expectation maximization algorithm with weighting mechanism.
//	 * @author Loc Nguyen
//	 * @version 1.0
//	 */
//	protected class WeightedREMExt extends REMExt {
//		
//		/**
//		 * Serial version UID for serializable class.
//		 */
//		private static final long serialVersionUID = 1L;
//
//		@Override
//		protected Object maximization(Object currentStatistic, Object... info) throws RemoteException {
//			ExchangedParameter parameter = (ExchangedParameter)super.maximization(currentStatistic, info);
//			
//			LargeStatistics stat = (LargeStatistics)currentStatistic;
//			List<Double> kCondProbs = null;
//			if (info != null && info.length > 0 && (info[0] instanceof List<?>)) {
//				@SuppressWarnings("unchecked")
//				List<Double> kCondProbTemp = (List<Double>)info[0];
//				kCondProbs = kCondProbTemp;
//			}
//			
//			NormalDisParameter xNormalDisParameter = null;
//			if (kCondProbs == null)
//				xNormalDisParameter = new NormalDisParameter(stat);
//			else
//				xNormalDisParameter = new NormalDisParameter(stat, kCondProbs);
//			parameter.setXNormalDisParameter(xNormalDisParameter);
//			
//			return parameter;
//		}
//		
//	}


}
