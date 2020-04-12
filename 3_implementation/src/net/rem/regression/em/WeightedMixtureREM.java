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
	public Object learnStart(Object... info) throws RemoteException {
		Object result = super.learnStart(info);
		if (result == null) return null;
		
		adjustWeightNormalDistribution();
		
		return result;
	}

	
	/**
	 * Adjusting weights of coefficients according to normal distribution.
	 */
	protected void adjustWeightNormalDistribution() {
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
	}

	
	/**
	 * Adjusting weights of coefficients according to maximum mechanism.
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	@Deprecated
	private void adjustWeightMax() {
		List<ExchangedParameter> parameters = null;
		try {
			parameters = (List<ExchangedParameter>)getParameter();
		} catch (Exception e) {LogUtil.trace(e);}
		if (parameters == null || parameters.size() == 0) return;
		
		int maxIdx = -1;
		double maxCoeff = -1;
		for (int k = 0; k < parameters.size(); k++) {
			double coeff = parameters.get(k).getCoeff();
			if (coeff > maxCoeff) {
				maxCoeff = coeff;
				maxIdx = k;
			}
		}
		
		if (maxIdx >= 0) {
			for (int k = 0; k < parameters.size(); k++) {
				if (k == maxIdx)
					parameters.get(k).setCoeff(1);
				else
					parameters.get(k).setCoeff(0);
			}
		}
	}
	
	
	@Override
	protected REMImpl createREM() {
		return new WeightedREMExt();
	}


	/**
	 * This class is an extension of regression expectation maximization algorithm with weighting mechanism.
	 * @author Loc Nguyen
	 * @version 1.0
	 */
	protected class WeightedREMExt extends REMExt {
		
		/**
		 * Serial version UID for serializable class.
		 */
		private static final long serialVersionUID = 1L;

		@Override
		protected Object maximization(Object currentStatistic, Object... info) throws RemoteException {
			ExchangedParameter parameter = (ExchangedParameter)super.maximization(currentStatistic, info);
			
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
			
			return parameter;
		}
		
	}
	
	
	/**
	 * Determining extra coefficients.
	 * @param xStatistic specified X statistic.
	 * @return extra coefficients.
	 */
	@SuppressWarnings("unchecked")
	private List<Double> calcExtraCoeffs(double[] xStatistic) {
		List<ExchangedParameter> parameters = null;
		try {
			parameters = (List<ExchangedParameter>)getParameter();
		} catch (Exception e) {LogUtil.trace(e);}
		
		List<Double> extraCoeffs = Util.newList(parameters.size());
		double sumExtraCoeffs = 0;
		for (ExchangedParameter parameter : parameters) {
			double extraCoeff = ExchangedParameter.normalPDF(
					DSUtil.toDoubleList(Arrays.copyOfRange(xStatistic, 1, xStatistic.length)),
					parameter.getXNormalDisParameter().getMean(),
					parameter.getXNormalDisParameter().getVariance());
			extraCoeffs.add(extraCoeff);
			sumExtraCoeffs += extraCoeff;
		}
		
		if (sumExtraCoeffs != 0) {
			for (int i = 0; i < extraCoeffs.size(); i++)
				extraCoeffs.set(i, extraCoeffs.get(i) / sumExtraCoeffs);
		}
		else {
			for (int i = 0; i < extraCoeffs.size(); i++)
				extraCoeffs.set(i, 1.0 / (double)extraCoeffs.size());
		}
		
		return extraCoeffs;
	}
	
	
	@Override
	public synchronized double executeByXStatistic(double[] xStatistic) throws RemoteException {
		if (this.rems == null || this.rems.size() == 0 || xStatistic == null)
			return Constants.UNUSED;
		
		List<Double> extraCoeffs = calcExtraCoeffs(xStatistic);
		double[] coeffs = new double[rems.size()];
		double sumCoeffs = 0;
		for (int i = 0; i < coeffs.length; i++) {
			ExchangedParameter parameter = rems.get(i).getExchangedParameter();
			coeffs[i] = parameter.getCoeff()*extraCoeffs.get(i);
			sumCoeffs += coeffs[i]; 
		}
		for (int i = 0; i < coeffs.length; i++) {
			coeffs[i] = coeffs[i] / sumCoeffs;
		}
		
		double result = 0;
		for (int i = 0; i < rems.size(); i++) {
			REMImpl rem = rems.get(i);
			ExchangedParameter parameter = rem.getExchangedParameter();
			
			double value = rem.executeByXStatistic(xStatistic);
			if (!Util.isUsed(value)) return Constants.UNUSED;
			
			double coeff = (parameter.getCoeff() + extraCoeffs.get(i)) / 2;
			result += coeff * value;
		}
		return result;
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


}
