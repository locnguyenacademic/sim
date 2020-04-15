package net.rem.regression.em;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.data.DataConfig;
import net.hudup.core.logistic.DSUtil;
import net.rem.regression.LargeStatistics;
import net.rem.regression.Statistics;
import net.rem.regression.em.ExchangedParameter.NormalDisParameter;

/**
 * This class is an extension of regression expectation maximization algorithm with joint distribution of regressors.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class JointREM extends REMImpl {

	
	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public JointREM() {

	}


	@Override
	protected Object maximization(Object currentStatistic, Object... info) throws RemoteException {
		LargeStatistics stat = (LargeStatistics)currentStatistic;
		if (stat == null || stat.isEmpty())
			return null;
		List<double[]> xStatistic = stat.getXData();
		List<double[]> zStatistic = stat.getZData();
		int N = zStatistic.size();
		int n = xStatistic.get(0).length; //1, x1, x2,..., x(n-1)
		ExchangedParameter currentParameter = (ExchangedParameter)getCurrentParameter();
		
		List<Double> alpha = calcCoeffsByStatistics(xStatistic, zStatistic);
		if (alpha == null || alpha.size() == 0) { //If cannot calculate alpha by matrix calculation.
			if (currentParameter != null)
				alpha = DSUtil.toDoubleList(currentParameter.getAlpha()); //clone alpha
			else { //Used for initialization so that regression model is always determined.
				alpha = DSUtil.initDoubleList(n, 0.0);
				double alpha0 = 0;
				for (int i = 0; i < N; i++)
					alpha0 += zStatistic.get(i)[1];
				alpha.set(0, alpha0 / (double)N); //constant function z = c
			}
		}
		
		ExchangedParameter newParameter = new ExchangedParameter(alpha);

		List<Double> kCondProbs = null;
		if (info != null && info.length > 0 && (info[0] instanceof List<?>)) {
			@SuppressWarnings("unchecked")
			List<Double> kCondProbTemp = (List<Double>)info[0];
			kCondProbs = kCondProbTemp;
		}
		NormalDisParameter xNormalDisParameter = null;
		if (kCondProbs == null)
			xNormalDisParameter = new NormalDisParameter(stat);
		else
			xNormalDisParameter = new NormalDisParameter(stat, kCondProbs);
		newParameter.setXNormalDisParameter(xNormalDisParameter);
		
		if (currentParameter != null)
			newParameter.setCoeff(currentParameter.getCoeff());
		if (getConfig().getAsBoolean(R_CALC_VARIANCE_FIELD))
			newParameter.setZVariance(newParameter.estimateZVariance(stat));
		else {
			if (currentParameter == null)
				newParameter.setZVariance(Constants.UNUSED);
			else if (Util.isUsed(currentParameter.getZVariance()))
				newParameter.setZVariance(newParameter.estimateZVariance(stat));
			else
				newParameter.setZVariance(Constants.UNUSED);
		}
		
		
		return newParameter;
	}


	@Override
	protected Statistics estimate(Statistics stat, ExchangedParameter parameter) {
		NormalDisParameter xNormalDisParameter = parameter.getXNormalDisParameter();
		
		double zValue = stat.getZStatistic();
		double[] xVector = stat.getXStatistic();
		double zStatistic = Constants.UNUSED;
		double[] xStatistic = new double[xVector.length];
		
		for (int j = 0; j < xVector.length; j++) {
			if (j == 0 || Util.isUsed(xVector[j]))
				xStatistic[j] = xVector[j]; // xVector[j] = 1 always
			else
				xStatistic[j] = xNormalDisParameter.getMean().get(j-1);
		}

		if (Util.isUsed(zValue))
			zStatistic = zValue;
		else
			zStatistic = parameter.mean(xStatistic);
		
		return new Statistics(zStatistic, xStatistic);
	}

	
	@Override
	protected ExchangedParameter initializeParameterWithoutData(int regressorNumber, boolean random) {
		Random rnd = new Random();
		List<Double> alpha0 = Util.newList(regressorNumber + 1);
		for (int j = 0; j < regressorNumber + 1; j++) {
			alpha0.add(random ? rnd.nextDouble() : 0.0);
		}
		ExchangedParameter parameter = new ExchangedParameter(alpha0);
		
		List<Double> mean = Util.newList(regressorNumber);
		for (int j = 0; j < regressorNumber; j++) {
			mean.add(random ? rnd.nextDouble() : 0.0);
		}

		List<double[]> variance = Util.newList(regressorNumber);
		for (int i = 0; i < regressorNumber; i++) {
			double[] kVariance = new double[regressorNumber];
			Arrays.fill(kVariance, 0);
			variance.add(kVariance);
			mean.add(random ? rnd.nextDouble() : 0.0);
		}
		for (int i = 0; i < regressorNumber; i++) {
			for (int j = 0; j < regressorNumber; j++) {
				if (j > i)
					variance.get(i)[j] = random ? rnd.nextDouble() : 0.0;
				else if (i == j)
					variance.get(i)[j] = 1;
				else
					variance.get(i)[j] = variance.get(j)[i];
			}
		}
		
		NormalDisParameter xNormalDisParameter = new NormalDisParameter(mean, variance);
		parameter.setXNormalDisParameter(xNormalDisParameter);
		
		return parameter;
	}

	
	@Override
	public String getName() {
		String name = getConfig().getAsString(DUPLICATED_ALG_NAME_FIELD);
		if (name != null && !name.isEmpty())
			return name;
		else
			return "rem_joint";
	}

	
	@Override
	public Alg newInstance() {
		JointREM jointREM = new JointREM();
		jointREM.getConfig().putAll((DataConfig)this.getConfig().clone());
		return jointREM;
	}


}
