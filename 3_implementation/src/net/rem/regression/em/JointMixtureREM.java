package net.rem.regression.em;

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
		List<LargeStatistics> stats = (List<LargeStatistics>)super.expectation(currentParameter, info);
		
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
				double[] xVector = stat.getXData().get(i);
				boolean modified = false;
				for (int j = 1; j < n; j++) {
					if (!Util.isUsed(this.data.getXData().get(i)[j])) {
						modified = true;
						xVector[j] = (xVector[j] + mean.get(j-1)) / 2.0; //Combine mean and inverse regression model. Pay attention here.
					}
				}
				
				if (modified) {
					double pdf = ExchangedParameter.normalPDF(
						DSUtil.toDoubleList(Arrays.copyOfRange(xVector, 1, xVector.length)),
						mean,
						variance);
					kWeights.add(coeff*pdf);
				}
				else
					kWeights.add(1.0);
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
			for (int k = 0; k < K; k++) {
				kNewCoeffs.add(weights.get(k).get(i) / kSumCoeffs);
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
		return new JointREM();
	}


	/**
	 * This class is an extension of regression expectation maximization algorithm with joint distribution of regressors.
	 * @author Loc Nguyen
	 * @version 1.0
	 */
	protected class JointREM extends REMExt {
		
		/**
		 * Serial version UID for serializable class.
		 */
		private static final long serialVersionUID = 1L;

		@Override
		protected Object maximization(Object currentStatistic, Object... info) throws RemoteException {
			ExchangedParameter parameter = (ExchangedParameter)super.maximization(currentStatistic, info);
			
			LargeStatistics stat = (LargeStatistics)currentStatistic;
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
			parameter.setXNormalDisParameter(xNormalDisParameter);
			
			return parameter;
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
		config.put(MAX_EXECUTE_FIELD, MAX_EXECUTE_DEFAULT);
		return config;
	}


}
