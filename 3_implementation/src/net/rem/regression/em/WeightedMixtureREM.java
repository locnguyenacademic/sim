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
	 * Extra list of normal distribution parameters.
	 */
	protected List<NormalDisParameter> normalDisParameters = Util.newList();
	
	
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
	 * Adjusting weights of coefficients according to maximum mechanism.
	 */
	@SuppressWarnings("unchecked")
	protected void adjustWeightMax() {
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
	
	
	/**
	 * Adjusting weights of coefficients according to normal distribution.
	 * @param outParameters output parameters.
	 */
	protected void adjustWeightNormalDistribution() {
		if (rems == null || rems.size() == 0) return;
		
		List<NormalDisParameter> tempDisParameters = Util.newList();
		for (REMImpl rem : rems) {
			LargeStatistics stat= null;
			try {
				stat = (LargeStatistics) rem.expectation(rem.getParameter(), this.data);
			} 
			catch (Exception e) {LogUtil.trace(e);}
			if (stat == null) return;
			
			NormalDisParameter disParameter = estimateNormalParameter(stat);
			if (disParameter != null) tempDisParameters.add(disParameter);
		}
		if (tempDisParameters.size() != rems.size()) return;
		
		this.normalDisParameters.clear();
		this.normalDisParameters.addAll(tempDisParameters);
	}

	
	/**
	 * Estimating normal distribution parameter.
	 * @param stat given a large statistics.
	 * @return parameter of normal distribution parameter given a large statistics.
	 */
	private NormalDisParameter estimateNormalParameter(LargeStatistics stat) {
		if (stat == null) return null;
		List<double[]> xData = stat.getXData();
		if (xData == null || xData.size() == 0) return null;
		
		int n = xData.get(0).length - 1;
		if (n <= 0) return null;
		
		List<Double> xMean = DSUtil.initDoubleList(n, 0);
		int N = xData.size();
		for (int i = 0; i < N; i++) {
			double[] x = xData.get(i);
			for (int j = 0; j < n; j++) {
				xMean.set(j, xMean.get(j) + x[j+1]);
			}
		}
		for (int j = 0; j < n; j++) {
			xMean.set(j, xMean.get(j) / (double)N);
		}
		
		
		List<double[]> xVariance = Util.newList(n);
		for (int i = 0; i < n; i++) {
			double[] x = new double[n];
			Arrays.fill(x, 0);
			xVariance.add(x);
		}
		
		for (int i = 0; i < N; i++) {
			double[] d = xData.get(i);
			for (int j = 0; j < n; j++) {d[j+1] = d[j+1] - xMean.get(j);}
			
			for (int j = 0; j < n; j++) {
				double[] x = xVariance.get(j);
				for (int k = 0; k < n; k++) {
					x[k] = x[k] + d[j+1]*d[k+1];
				}
			}
		}
		
		for (int j = 0; j < n; j++) {
			double[] x = xVariance.get(j);
			for (int k = 0; k < n; k++) {
				x[k] = x[k] / (double)N;
			}
		}
		
		
		return new NormalDisParameter(xMean, xVariance);
	}
	
	
	/**
	 * Determining extra coefficients.
	 * @param xStatistic specified X statistic.
	 * @return extra coefficients.
	 */
	private List<Double> calcExtraCoeffs(double[] xStatistic) {
		if (normalDisParameters == null || normalDisParameters.size() != rems.size())
			return Util.newList();
		
		List<Double> extraCoeffs = Util.newList(normalDisParameters.size());
		double sumExtraCoeffs = 0;
		for (NormalDisParameter parameter : normalDisParameters) {
			double extraCoeff = ExchangedParameter.normalPDF(
					DSUtil.toDoubleList(Arrays.copyOfRange(xStatistic, 1, xStatistic.length)),
					parameter.getMean(), parameter.getVariance());
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
		if (normalDisParameters == null || normalDisParameters.size() != rems.size())
			return super.executeByXStatistic(xStatistic);
		
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
		if (normalDisParameters == null || normalDisParameters.size() != rems.size() || rems.size() == 0)
			return super.execute(input);
		
		double[] xStatistic = rems.get(0).extractAndTransformRegressorValues(input);
		return executeByXStatistic(xStatistic);
	}

	
	/**
	 * This class represents parameter of multivariate normal distribution.
	 * @author Loc Nguyen
	 * @version 1.0
	 */
	protected class NormalDisParameter {
		
		/**
		 * Mean.
		 */
		protected List<Double> mean = Util.newList();
		
		/**
		 * Variance.
		 */
		protected List<double[]> variance = Util.newList();
		
		/**
		 * Constructor of specified mean and variance.
		 * @param mean specified mean.
		 * @param variance specified variance.
		 */
		public NormalDisParameter(List<Double> mean, List<double[]> variance) {
			this.mean = mean;
			this.variance = variance;
		}
		
		/**
		 * Getting mean.
		 * @return
		 */
		public List<Double> getMean() {
			return mean;
		}
		
		/**
		 * Getting variance.
		 * @return variance.
		 */
		public List<double[]> getVariance() {
			return variance;
		}
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
		WeightedMixtureREM crispREM = new WeightedMixtureREM();
		crispREM.getConfig().putAll((DataConfig)this.getConfig().clone());
		return crispREM;
	}


}
