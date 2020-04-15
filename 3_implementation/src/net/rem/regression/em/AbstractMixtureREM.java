package net.rem.regression.em;

import static net.rem.regression.RMAbstract.extractNumber;
import static net.rem.regression.RMAbstract.splitIndices;
import static net.rem.regression.em.REMImpl.R_CALC_VARIANCE_FIELD;

import java.awt.Color;
import java.rmi.RemoteException;
import java.util.List;

import flanagan.math.Fmath;
import flanagan.plot.PlotGraph;
import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.alg.MemoryBasedAlg;
import net.hudup.core.alg.MemoryBasedAlgRemote;
import net.hudup.core.data.AttributeList;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.Profile;
import net.hudup.core.logistic.Inspector;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.MathUtil;
import net.hudup.core.logistic.xURI;
import net.rem.em.EMRemote;
import net.rem.em.ExponentialEM;
import net.rem.regression.LargeStatistics;
import net.rem.regression.RM;
import net.rem.regression.RMAbstract;
import net.rem.regression.RMRemote;
import net.rem.regression.VarWrapper;
import net.rem.regression.em.ui.graph.Graph;
import net.rem.regression.em.ui.graph.PlotGraphExt;

/**
 * This abstract class implements partially expectation maximization (EM) algorithm for mixture regression models.
 * All algorithms that implement mixture regression model should derive from this class.
 *  
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public abstract class AbstractMixtureREM extends ExponentialEM implements RM, RMRemote, MemoryBasedAlg, MemoryBasedAlgRemote {

	
	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default value of on-cluster execution mode.
	 */
	protected static final String ON_CLUSTER_EXECUTE_FIELD = "oncluster_execute";

	
	/**
	 * Default value of on-cluster execution mode.
	 */
	protected static final boolean ON_CLUSTER_EXECUTE_DEFAULT = false;
	
	
	/**
	 * List of internal regression model as parameter.
	 */
	protected List<REMImpl> rems = null;

	
	/**
	 * Setting up this model from other model.
	 * @param other other model.
	 * @throws RemoteException if any error raises.
	 */
	public void setup(DefaultMixtureREM other) throws RemoteException {
		super.setup((Dataset)null, other);
	}

	
	@Override
	public Object learnStart(Object...info) throws RemoteException {
		boolean prepared = false;
		if (info == null || info.length == 0 || !(info[0] instanceof DefaultMixtureREM))
			prepared = prepareInternalData(this.sample);
		else
			prepared = prepareInternalData((AbstractMixtureREM)info[0]);
		if (!prepared) {
			clearInternalData();
			return null;
		}
		
		if (super.learnStart(info) == null) {
			clearInternalData();
			return null;
		}
		
		if(!adjustMixtureParameters()) {
			clearInternalData();
			return null;
		}
		
		return this.rems;
	}
	
	
	@Override
	public synchronized void unsetup() throws RemoteException {
		super.unsetup();
		if (this.rems != null) {
			for (REMImpl rem : this.rems)
				rem.unsetup();
		}
	}

	
	/**
	 * Preparing data with other regression mixture model.
	 * @param other other regression mixture model.
	 * @return true if data preparation is successful.
	 * @throws RemoteException if any error raises.
	 */
	protected boolean prepareInternalData(AbstractMixtureREM other) throws RemoteException {
		return prepareInternalData(this.sample);
	}
	
	
	/**
	 * Preparing data.
	 * @param inputSample specified sample.
	 * @return true if data preparation is successful.
	 * @throws RemoteException if any error raises.
	 */
	protected boolean prepareInternalData(Fetcher<Profile> inputSample) throws RemoteException {
		clearInternalData();
		DataConfig thisConfig = this.getConfig();
		
		List<String> indicesList = splitIndices(thisConfig.getAsString(R_INDICES_FIELD));
		if (indicesList.size() == 0) {
			AttributeList attList = getSampleAttributeList(inputSample);
			if (attList.size() < 2)
				return false;
			
			StringBuffer indices = new StringBuffer();
			for (int i = 1; i <= attList.size(); i++) {// For fair test
				if (i > 1)
					indices.append(", ");
				indices.append(i);
			}
			indicesList.add(indices.toString());
		}
		
		this.rems = Util.newList(indicesList.size());
		for (int i = 0; i < indicesList.size(); i++) {
			REMImpl rem = createREM();
			rem.getConfig().put(R_INDICES_FIELD, indicesList.get(i));
			rem.setup(inputSample);
			if(rem.attList != null) // if rem is set up successfully.
				this.rems.add(rem);
		}
		
		if (this.rems.size() == 0) {
			this.rems = null;
			return false;
		}
		else
			return true;
	}
	
	
	/**
	 * Creating internal regression model.
	 * @return internal regression model.
	 */
	protected REMImpl createREM() {
		REMImpl rem = new REMImpl() {

			/**
			 * Serial version UID for serializable class.
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public synchronized Object learnStart(Object...info) throws RemoteException {
				boolean prepared = prepareInternalData(sample);
				if (prepared)
					return prepared;
				else
					return null;
			}

			@Override
			protected Object transformRegressor(Object x, boolean inverse) {
				return getMixtureREM().transformRegressor(x, inverse);
			}

			@Override
			public Object transformResponse(Object z, boolean inverse) throws RemoteException {
				return getMixtureREM().transformResponse(z, inverse);
			}
			
		};
		rem.getConfig().put(R_CALC_VARIANCE_FIELD, true);
		
		return rem;
	}
	
	
	/**
	 * Getting this mixture regression expectation maximization model.
	 * @return this mixture regression expectation maximization model.
	 */
	protected AbstractMixtureREM getMixtureREM() {
		return this;
	}
	
	
	/**
	 * Clear all internal data.
	 * @throws RemoteException if any error raises.
	 */
	protected void clearInternalData() throws RemoteException {
		this.currentIteration = 0;
		this.estimatedParameter = this.currentParameter = this.previousParameter = null;
		
		if (this.rems != null) {
			for (REMImpl rem : this.rems) {
				rem.clearInternalData();
				rem.unsetup();
			}
			this.rems.clear();
			this.rems = null;
		}
		
		this.statistics = null;
	}

	
	/**
	 * Expectation method of this class does not change internal data.
	 */
	@Override
	protected Object expectation(Object currentParameter, Object...info) throws RemoteException {
		return expectation0(currentParameter, info);
	}

	
	/**
	 * This method implement expectation step (E-step) of EM in basically.
	 * @param currentParameter current parameter.
	 * @param info additional information.
	 * @return sufficient statistic given current parameter.
	 * @throws RemoteException if any error raises.
	 */
	protected Object expectation0(Object currentParameter, Object...info) throws RemoteException {
		if (currentParameter == null)
			return null;
		@SuppressWarnings("unchecked")
		List<ExchangedParameter> parameters = (List<ExchangedParameter>)currentParameter;
		List<LargeStatistics> stats = Util.newList(this.rems.size());
		for (int k = 0; k < this.rems.size(); k++) {
			REMImpl rem = this.rems.get(k);
			ExchangedParameter parameter = parameters.get(k);
//			if (rem.terminatedCondition(rem.getEstimatedParameter(), rem.getCurrentParameter(), rem.getPreviousParameter(), info)
//					&& rem.getLargeStatistics() != null)
//				continue;
				
			LargeStatistics stat = (LargeStatistics)rem.expectation(parameter);
			rem.setStatistics(stat);
			stats.add(stat);
			
			if (stat == null)
				LogUtil.error("Some regression models are failed in expectation");
		}
		
		return stats;
	}
	
	
	/**
	 * Maximization method of this class changes internal data.
	 */
	@Override
	protected Object maximization(Object currentStatistic, Object...info) throws RemoteException {
		if (currentStatistic == null)
			return null;
		@SuppressWarnings("unchecked")
		List<LargeStatistics> stats = (List<LargeStatistics>)currentStatistic;
		List<ExchangedParameter> parameters = Util.newList(this.rems.size());
		for (int k = 0; k < this.rems.size(); k++) {
			REMImpl rem = this.rems.get(k);
			LargeStatistics stat = stats.get(k);
//			if (rem.terminatedCondition(rem.getEstimatedParameter(), rem.getCurrentParameter(), rem.getPreviousParameter(), info)
//					&& rem.getLargeStatistics() != null)
//				continue;

			ExchangedParameter parameter = (ExchangedParameter)rem.maximization(stat);
			rem.setEstimatedParameter(parameter);
			parameters.add(parameter);
			
//			if (parameter == null)
//				logger.error("Some regression models are failed in expectation");
		}
		
		return parameters;
	}

	
	/**
	 * Initialization method of this class changes internal data.
	 */
	@Override
	protected Object initializeParameter() {
		List<ExchangedParameter> parameters = Util.newList(this.rems.size());
		for (int k = 0; k < this.rems.size(); k++) {
			REMImpl rem = this.rems.get(k);
			ExchangedParameter parameter = (ExchangedParameter)rem.initializeParameter();
			
			rem.setEstimatedParameter(parameter);
			rem.setCurrentParameter(parameter);
			rem.setPreviousParameter(null);
			rem.setStatistics(null);
			rem.setCurrentIteration(this.getCurrentIteration());

			parameters.add(parameter);
		}
		
		for (ExchangedParameter parameter : parameters) {
			parameter.setCoeff(1.0 / (double)this.rems.size());
			if (!Util.isUsed(parameter.getZVariance()))
				parameter.setZVariance(1.0);
		}
		return parameters;
	}

	
	@Override
	protected void permuteNotify() {
		super.permuteNotify();
		@SuppressWarnings("unchecked")
		List<ExchangedParameter> estimatedParameters = (List<ExchangedParameter>)getEstimatedParameter();
		@SuppressWarnings("unchecked")
		List<ExchangedParameter> currentParameters = (List<ExchangedParameter>)getCurrentParameter();
		@SuppressWarnings("unchecked")
		List<ExchangedParameter> previousParameters = (List<ExchangedParameter>)getPreviousParameter();
		
		for (int k = 0; k < this.rems.size(); k++) {
			REMImpl rem = this.rems.get(k);
			ExchangedParameter estimatedParameter = estimatedParameters.get(k);
			ExchangedParameter currentParameter = currentParameters.get(k);
			ExchangedParameter previousParameter = previousParameters != null ? previousParameters.get(k) : null;
			
			rem.setEstimatedParameter(estimatedParameter);
			rem.setCurrentParameter(currentParameter);
			rem.setPreviousParameter(previousParameter);
			rem.setCurrentIteration(this.getCurrentIteration());
		}
	}


	@Override
	protected void finishNotify() {
		super.finishNotify();
		@SuppressWarnings("unchecked")
		List<ExchangedParameter> estimatedParameters = (List<ExchangedParameter>)getEstimatedParameter();
		@SuppressWarnings("unchecked")
		List<ExchangedParameter> currentParameters = (List<ExchangedParameter>)getCurrentParameter();
		
		for (int k = 0; k < this.rems.size(); k++) {
			REMImpl rem = this.rems.get(k);
			ExchangedParameter estimatedParameter = estimatedParameters.get(k);
			ExchangedParameter currentParameter = currentParameters.get(k);
			
			rem.setEstimatedParameter(estimatedParameter);
			rem.setCurrentParameter(currentParameter);
			rem.setCurrentIteration(this.getCurrentIteration());
		}
	}


	/**
	 * Adjusting specified parameters based on specified statistics according to mixture model in one iteration.
	 * This method does not need a loop because both mean and variance were optimized in REM process and so the probabilities of components will be optimized in only one time.
	 * @return true if the adjustment process is successful.
	 * @throws RemoteException if any error raises.
	 */
	protected boolean adjustMixtureParameters() throws RemoteException {
		//Do nothing
		return true;
	}


	@Override
	protected boolean terminatedCondition(Object estimatedParameter, Object currentParameter, Object previousParameter, Object... info) {
		if (this.rems == null)
			return true;

		@SuppressWarnings("unchecked")
		List<ExchangedParameter> estimatedParameters = (List<ExchangedParameter>)estimatedParameter;
		@SuppressWarnings("unchecked")
		List<ExchangedParameter> currentParameters = (List<ExchangedParameter>)currentParameter;
		@SuppressWarnings("unchecked")
		List<ExchangedParameter> previousParameters = (List<ExchangedParameter>)previousParameter;
		
		boolean terminated = true;
		for (int k = 0; k < this.rems.size(); k++) {
			REMImpl rem = this.rems.get(k);
			ExchangedParameter pParameter = previousParameters != null ? previousParameters.get(k) : null;
			ExchangedParameter cParameter = currentParameters.get(k);
			ExchangedParameter eParameter = estimatedParameters.get(k);
			
			if (eParameter == null && cParameter == null)
				continue;
			else if (eParameter == null || cParameter == null)
				return false;
			
			terminated = terminated && rem.terminatedCondition(eParameter, cParameter, pParameter, info);
			if (!terminated)
				return false;
		}
		
		return terminated;
	}

	
	@Override
	public synchronized LargeStatistics getLargeStatistics() throws RemoteException {
		if (rems != null && rems.size() > 0)
			return rems.get(0).getLargeStatistics(); //Suppose all REMs have the same large statistics.
		else
			return null;
	}


	@Override
	public synchronized double executeByXStatistic(double[] xStatistic) throws RemoteException {
		if (this.rems == null || this.rems.size() == 0 || xStatistic == null)
			return Constants.UNUSED;
		
		double result = 0;
		for (REMImpl rem : this.rems) {
			ExchangedParameter parameter = (ExchangedParameter)rem.getParameter();
			
			double value = rem.executeByXStatistic(xStatistic);
			if (Util.isUsed(value))
				result += parameter.getCoeff() * value;
			else
				return Constants.UNUSED;
		}
		return result;
	}
	
	
	@Override
	public synchronized Object execute(Object input) throws RemoteException {
		if (this.rems == null || this.rems.size() == 0)
			return Constants.UNUSED;
		
		double result = 0;
		for (REMImpl rem : this.rems) {
			ExchangedParameter parameter = (ExchangedParameter)rem.getParameter();
			
			double value = extractNumber(rem.execute(input));
			if (Util.isUsed(value))
				result += parameter.getCoeff() * value;
			else
				return Constants.UNUSED;
		}
		return result;
	}

	
	/**
	 * Executing this algorithm by arbitrary input parameter.
	 * @param input arbitrary input parameter.
	 * @return result of execution. Return null if execution is failed.
	 * @throws RemoteException if any error raises.
	 */
	public Object executeIntel(Object...input) throws RemoteException {
		return execute(input);
	}

	
	@Override
	public synchronized String parameterToShownText(Object parameter, Object... info) throws RemoteException {
		if (parameter == null || !(parameter instanceof List<?>))
			return "";
		
		@SuppressWarnings("unchecked")
		List<ExchangedParameter> parameters = (List<ExchangedParameter>)parameter;
		StringBuffer buffer = new StringBuffer();
		for (int k = 0; k < rems.size(); k++) {
			if (k > 0)
				buffer.append(", ");
			String text = rems.get(k).parameterToShownText(parameters.get(k), info);
			buffer.append("{" + text + "}");
		}
		buffer.append(": ");
		buffer.append("t=" + MathUtil.format(getCurrentIteration()));
		
		return buffer.toString();
	}

	
	@Override
	public synchronized String getDescription() throws RemoteException {
		if (this.rems == null)
			return "";

		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < this.rems.size(); i++) {
			RM regression = this.rems.get(i);
			if (i > 0)
				buffer.append(", ");
			String text = "";
			if (regression != null)
				text = regression.getDescription();
			buffer.append("{" + text + "}");
		}
		buffer.append(": ");
		buffer.append("t=" + getCurrentIteration());
		
		return buffer.toString();
	}

	
	@Override
	public synchronized Inspector getInspector() {
		return RMAbstract.getInspector(this);
	}


	@Override
	public String[] getBaseRemoteInterfaceNames() throws RemoteException {
		return new String[] {EMRemote.class.getName(), RMRemote.class.getName(), MemoryBasedAlgRemote.class.getName()};
	}

	
	@Override
	public DataConfig createDefaultConfig() {
		DataConfig config = super.createDefaultConfig();
		config.put(R_INDICES_FIELD, R_INDICES_DEFAULT);
		return config;
	}

	
	@Override
	public synchronized VarWrapper extractRegressor(int index) throws RemoteException {
		if (this.rems == null || this.rems.size() == 0)
			return null;
		else
			return this.rems.get(0).extractRegressor(index); // Suppose all REMS have the same regressors.
	}


	@Override
	public synchronized List<VarWrapper> extractRegressors() throws RemoteException {
		if (this.rems == null || this.rems.size() == 0)
			return Util.newList();
		else
			return this.rems.get(0).extractRegressors(); // Suppose all REMS have the same regressors.
	}


	@Override
	public synchronized List<VarWrapper> extractSingleRegressors() throws RemoteException {
		if (this.rems == null || this.rems.size() == 0)
			return Util.newList();
		else
			return this.rems.get(0).extractSingleRegressors(); // Suppose all REMS have the same regressors.
	}


	@Override
	public synchronized double extractRegressorValue(Object input, int index) throws RemoteException {
		if (this.rems == null || this.rems.size() == 0)
			return Constants.UNUSED;
		else
			return this.rems.get(0).extractRegressorValue(input, index); // Suppose all REMS have the same regressors.
	}


	@Override
	public double[] extractRegressorValues(Object input) throws RemoteException {
		if (this.rems == null || this.rems.size() == 0)
			return null;
		else
			return this.rems.get(0).extractRegressorValues(input); // Suppose all REMS have the same regressors.
	}


	@Override
	public List<Double> extractRegressorStatistic(VarWrapper regressor) throws RemoteException {
		if (this.rems == null || this.rems.size() == 0)
			return Util.newList();
		else
			return this.rems.get(0).extractRegressorStatistic(regressor); // Suppose all REMS have the same regressors.
	}


	@Override
	public synchronized VarWrapper extractResponse() throws RemoteException {
		if (this.rems == null || this.rems.size() == 0)
			return null;
		else
			return this.rems.get(0).extractResponse(); // Suppose all REMS have the same response.
	}


	@Override
	public synchronized Object extractResponseValue(Object input) throws RemoteException {
		if (this.rems == null || this.rems.size() == 0)
			return null;
		else
			return this.rems.get(0).extractResponseValue(input); // Suppose all REMS have the same response.
	}


	/**
	 * Transforming independent variable X.
	 * In the most general case that each index is an mathematical expression, this method is not focused.
	 * @param x specified variable X.
	 * @param inverse if true, there is an inverse transformation.
	 * @return transformed value of X.
	 */
	protected Object transformRegressor(Object x, boolean inverse) {
		return x;
	}

	
	@Override
	public Object transformResponse(Object z, boolean inverse) throws RemoteException {
		return z;
	}
	
	
	@Override
	public synchronized Graph createRegressorGraph(VarWrapper regressor) throws RemoteException {
		if (this.rems == null || this.rems.size() == 0)
			return null;

		LargeStatistics stats = this.getLargeStatistics(); // Suppose all REMs has the same large statistics.
    	int ncurves = 1 + this.rems.size();
    	int npoints = stats.size();
    	double[][] data = PlotGraph.data(ncurves, npoints);
    	int[] popt = new int[1 + this.rems.size()];
    	int[] lopt = new int[1 + this.rems.size()];
    	
    	for(int i = 0; i < npoints; i++) {
            data[0][i] = stats.getXData().get(i)[regressor.getIndex()];
            data[1][i] = stats.getZData().get(i)[1];
        }
    	popt[0] = 1;
    	lopt[0] = 0;
    	
    	for (int k = 0; k < this.rems.size(); k++) {
    		ExchangedParameter parameter = this.rems.get(k).getExchangedParameter();
    		double coeff0 = parameter.getAlpha().get(0);
    		double coeff1 = parameter.getAlpha().get(regressor.getIndex());
    		
        	data[2*(k+1)][0] = Fmath.minimum(data[0]);
        	data[2*(k+1) + 1][0] = coeff0 + coeff1 * data[2*(k+1)][0];
        	
        	data[2*(k+1)][1] = Fmath.maximum(data[0]);
        	data[2*(k+1) + 1][1] = coeff0 + coeff1 * data[2*(k+1)][1];
        	
        	popt[k + 1] = 0;
        	lopt[k + 1] = 3;
    	}

    	PlotGraphExt pg = new PlotGraphExt(data);

    	pg.setGraphTitle("Regressor plot");
    	pg.setXaxisLegend(extractRegressor(regressor.getIndex()).toString());
    	pg.setYaxisLegend(extractResponse().toString());
    	pg.setPoint(popt);
    	pg.setLine(lopt);

    	pg.setBackground(Color.WHITE);
        return pg;
	}


	@Override
	public synchronized Graph createResponseGraph() throws RemoteException {
		if (this.rems == null || this.rems.size() == 0)
			return null;
		else
			return RMAbstract.createResponseGraph(this, this.getLargeStatistics()); // Suppose all REMs have the same large statistics.
	}


	@Override
	public synchronized Graph createErrorGraph() throws RemoteException {
		if (this.rems == null || this.rems.size() == 0)
			return null;
		else
			return RMAbstract.createErrorGraph(this, this.getLargeStatistics()); // Suppose all REMs have the same large statistics.
	}


	@Override
	public synchronized List<Graph> createResponseRalatedGraphs() throws RemoteException {
		if (this.rems == null || this.rems.size() == 0)
			return null;
		else
			return RMAbstract.createResponseRalatedGraphs(this);
	}


	@Override
	public synchronized double calcVariance() throws RemoteException {
		if (this.rems == null || this.rems.size() == 0)
			return Constants.UNUSED;
		else
			return RMAbstract.calcVariance(this, this.getLargeStatistics()); // Suppose all REMs have the same large statistics.
	}


	@Override
	public double calcR() throws RemoteException {
		if (this.rems == null || this.rems.size() == 0)
			return Constants.UNUSED;
		else
			return RMAbstract.calcR(this, this.getLargeStatistics()); // Suppose all REMs have the same large statistics.
	}


	@Override
	public double[] calcError() throws RemoteException {
		if (this.rems == null || this.rems.size() == 0)
			return null;
		else
			return RMAbstract.calcError(this, this.getLargeStatistics()); // Suppose all REMs have the same large statistics.
	}


	@Override
	public boolean saveLargeStatistics(xURI uri, int decimal) throws RemoteException {
		return RMAbstract.saveLargeStatistics(this, getLargeStatistics(), uri, decimal);
	}
	
	
}
