package net.rem.regression.em;

import static net.rem.regression.em.REMImpl.R_CALC_VARIANCE_FIELD;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;

import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.data.DataConfig;
import net.hudup.core.logistic.DSUtil;
import net.rem.regression.LargeStatistics;
import net.rem.regression.em.ExchangedParameter.NormalDisParameter;

/**
 * This class implements the mixture regression model with joint distribution of regressors.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class JointMixtureREM extends DefaultMixtureREM {

	
	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public JointMixtureREM() {

	}

	
	@Override
	protected Object expectation(Object currentParameter, Object... info) throws RemoteException {
		@SuppressWarnings("unchecked")
		List<ExchangedParameter> parameters = (List<ExchangedParameter>)currentParameter;
		@SuppressWarnings("unchecked")
		List<LargeStatistics> stats = (List<LargeStatistics>)super.expectation0(currentParameter, info);
		if (stats == null) return null;
		
		//Adjusting large statistics.
		int N = stats.get(0).getZData().size(); //Suppose all models have the same data.
		int n = stats.get(0).getXData().get(0).length;  //Suppose all models have the same data.
		List<double[]> xData = Util.newList(N);
		List<double[]> zData = Util.newList(N);
		for (int i = 0; i < N; i++) {
			double[] xVector = new double[n];
			Arrays.fill(xVector, 0.0);
			xVector[0] = 1;
			xData.add(xVector);
			
			double[] zVector = new double[2];
			zVector[0] = 1;
			zVector[1] = 0;
			zData.add(zVector);
		}
		
		
		//Estimating X statistics and calculating weights for each regression model.
		int K = this.rems.size();
		List<List<Double>> weights = Util.newList(this.rems.size()); //K lists of weights.
		for (int k = 0; k < K; k++) {
			ExchangedParameter parameter = parameters.get(k); 
			LargeStatistics stat = stats.get(k);
			List<Double> kWeights = Util.newList(N); //The kth list of weights.
			weights.add(kWeights);
			
			double coeff = parameter.getCoeff();
			List<Double> mean = parameter.getXNormalDisParameter().getMean();
			List<double[]> variance = parameter.getXNormalDisParameter().getVariance();
			for (int i = 0; i < N; i++) {
				if (!Util.isUsedAll(this.data.getXData().get(i))) {
					double[] xVector = stat.getXData().get(i);
					double pdf = ExchangedParameter.normalPDF(
						DSUtil.toDoubleList(Arrays.copyOfRange(xVector, 1, xVector.length)),
						mean,
						variance);
					
					kWeights.add(coeff*pdf);
				}
				else
					kWeights.add(1.0); //Not necessary to calculate the probabilities.
			}
		}
		
		List<List<Double>> newCoeffs = Util.newList(N);
		for (int i = 0; i < N; i++) {
			List<Double> kNewCoeffs = Util.newList(K);
			newCoeffs.add(kNewCoeffs);
			
			double kSumCoeffs = 0;
			for (int k = 0; k < K; k++) {
				kSumCoeffs += weights.get(k).get(i);
			}
			
			if (kSumCoeffs == 0 || !Util.isUsed(kSumCoeffs)) {
				double w = 1.0 / (double)K;
				for (int k = 0; k < K; k++) {
					kNewCoeffs.add(w);
				}
			}
			else {
				for (int k = 0; k < K; k++) {
					kNewCoeffs.add(weights.get(k).get(i) / kSumCoeffs);
				}
			}
		}
		weights.clear();

		for (int k = 0; k < K; k++) {
			LargeStatistics stat = stats.get(k);
			for (int i = 0; i < N; i++) {
				double[] zVector = zData.get(i);
				double zValue = stat.getZData().get(i)[1];
				if (!Util.isUsed(this.data.getZData().get(i)[1]))
					zVector[1] += newCoeffs.get(i).get(k) * zValue;
				else
					zVector[1] = zValue; 
				
				double[] xVector = xData.get(i);
				for (int j = 1; j < n; j++) {
					double xValue = stat.getXData().get(i)[j];
					if (!Util.isUsed(this.data.getXData().get(i)[j]))
						xVector[j] += newCoeffs.get(i).get(k) * xValue; // This assignment is right with assumption of same P(Y=k).
					else
						xVector[j] = xValue;
				}
			}
		}
		
		//All regression models have the same large statistics.
		stats.clear();
		LargeStatistics stat = new LargeStatistics(xData, zData);
		for (REMImpl rem : this.rems) {
			rem.setStatistics(stat);
			stats.add(stat);
		}
		
		return stats;
	}


	@Override
	protected REMImpl createREM() {
		JointREMExt rem = new JointREMExt();
		rem.getConfig().put(EM_EPSILON_FIELD, this.getConfig().get(EM_EPSILON_FIELD));
		rem.getConfig().put(EM_MAX_ITERATION_FIELD, this.getConfig().get(EM_MAX_ITERATION_FIELD));
		rem.getConfig().put(R_INDICES_FIELD, this.getConfig().get(R_INDICES_FIELD));
		rem.getConfig().put(R_CALC_VARIANCE_FIELD, true);
		return rem;
	}


	/**
	 * This class is an extension of joint regression expectation maximization algorithm.
	 * @author Loc Nguyen
	 * @version 1.0
	 */
	protected class JointREMExt extends JointREM {
		
		/**
		 * Serial version UID for serializable class.
		 */
		private static final long serialVersionUID = 1L;
		
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
			
			List<double[]> uStatistic = xStatistic;
			List<double[]> vStatistic = zStatistic;
			List<Double> kCondProbs = null;
			if (info != null && info.length > 0 && (info[0] instanceof List<?>)) {
				@SuppressWarnings("unchecked")
				List<Double> kCondProbTemp = (List<Double>)info[0];
				kCondProbs = kCondProbTemp;
				
				uStatistic = Util.newList(xStatistic.size());
				vStatistic = Util.newList(zStatistic.size());
				for (int i = 0; i < N; i++) {
					double[] uVector = new double[n];
					uStatistic.add(uVector);
					double[] vVector = new double[2];
					vStatistic.add(vVector);
					
					for (int j = 0; j < n; j++) {
						uVector[j] = xStatistic.get(i)[j] * kCondProbs.get(i); 
					}
					vVector[0] = 1;
					vVector[1] = zStatistic.get(i)[1] * kCondProbs.get(i); 
				}
			}
			
			List<Double> alpha = calcCoeffsByStatistics(uStatistic, vStatistic);
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
			if (kCondProbs == null) {
				newParameter.setZVariance(newParameter.estimateZVariance(stat));
			}
			else {
				double sumCondProb = 0;
				for (int i = 0; i < N; i++) {
					sumCondProb += kCondProbs.get(i);
				}
				
				double sumZVariance = 0;
				for (int i = 0; i < N; i++) {
					double d = zStatistic.get(i)[1] - ExchangedParameter.mean(alpha, xStatistic.get(i));
					sumZVariance += d*d*kCondProbs.get(i);
				}
				
				newParameter.setCoeff(sumCondProb/N);
				if (sumCondProb != 0)
					newParameter.setZVariance(sumZVariance/sumCondProb);
				else
					newParameter.setZVariance(1.0); //Fixing zero probabilities.
			}
			
			NormalDisParameter xNormalDisParameter = null;
			if (kCondProbs == null)
				xNormalDisParameter = new NormalDisParameter(stat);
			else
				xNormalDisParameter = new NormalDisParameter(stat, kCondProbs);
			newParameter.setXNormalDisParameter(xNormalDisParameter);

			return newParameter;
		}

		@Override
		protected Object transformRegressor(Object x, boolean inverse) {
			return getMixtureREM().transformRegressor(x, inverse);
		}

		@Override
		public Object transformResponse(Object z, boolean inverse) throws RemoteException {
			return getMixtureREM().transformResponse(z, inverse);
		}
		
	}

	
	@Override
	public String getName() {
		String name = getConfig().getAsString(DUPLICATED_ALG_NAME_FIELD);
		if (name != null && !name.isEmpty())
			return name;
		else
			return "mixrem_joint";
	}

	
	@Override
	public Alg newInstance() {
		JointMixtureREM jointREM = new JointMixtureREM();
		jointREM.getConfig().putAll((DataConfig)this.getConfig().clone());
		return jointREM;
	}


	@Override
	public DataConfig createDefaultConfig() {
		DataConfig config = super.createDefaultConfig();
		config.put(ON_CLUSTER_EXECUTE_FIELD, ON_CLUSTER_EXECUTE_DEFAULT);
		return config;
	}


}
