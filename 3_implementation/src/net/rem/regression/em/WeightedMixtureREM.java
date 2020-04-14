package net.rem.regression.em;

import java.rmi.RemoteException;

import net.hudup.core.alg.Alg;
import net.hudup.core.data.DataConfig;
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


	@Override
	public String getName() {
		String name = getConfig().getAsString(DUPLICATED_ALG_NAME_FIELD);
		if (name != null && !name.isEmpty())
			return name;
		else
			return "mixrem_weighted";
	}

	
	@Override
	public Alg newInstance() {
		WeightedMixtureREM weightedREM = new WeightedMixtureREM();
		weightedREM.getConfig().putAll((DataConfig)this.getConfig().clone());
		return weightedREM;
	}


	@Override
	public DataConfig createDefaultConfig() {
		DataConfig config = super.createDefaultConfig();
		config.put(MAX_EXECUTE_FIELD, MAX_EXECUTE_DEFAULT);
		return config;
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


}
