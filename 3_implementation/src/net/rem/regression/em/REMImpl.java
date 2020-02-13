package net.rem.regression.em;

import static net.rem.regression.RMAbstract.createProfile;
import static net.rem.regression.RMAbstract.extractNumber;
import static net.rem.regression.RMAbstract.extractSingleVariables;
import static net.rem.regression.RMAbstract.extractVariable;
import static net.rem.regression.RMAbstract.extractVariableValue;
import static net.rem.regression.RMAbstract.extractVariables;
import static net.rem.regression.RMAbstract.findIndex;
import static net.rem.regression.RMAbstract.notSatisfy;
import static net.rem.regression.RMAbstract.parseIndices;
import static net.rem.regression.RMAbstract.solve;

import java.awt.Component;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;

import javax.swing.JOptionPane;

import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.DuplicatableAlg;
import net.hudup.core.data.AttributeList;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.MemFetcher;
import net.hudup.core.data.Pointer;
import net.hudup.core.data.Profile;
import net.hudup.core.logistic.DSUtil;
import net.hudup.core.logistic.LogUtil;
import net.hudup.data.ui.DatasetLoader;
import net.rem.regression.LargeStatistics;
import net.rem.regression.Statistics;
import net.rem.regression.VarWrapper;
import net.rem.regression.ui.RegressResponseChooser;

/**
 * This class implements default expectation maximization algorithm for regression model in case of missing data, called REM algorithm {@link REM}.
 * Exactly, it is default implementation of REM algorithm {@link REM}.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class REMImpl extends REMAbstract implements DuplicatableAlg {

	
	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Name of variance calculation field.
	 */
	public final static String R_CALC_VARIANCE_FIELD = "r_calc_variance";

	
	/**
	 * Default value variance calculation field.
	 */
	public final static boolean R_CALC_VARIANCE_DEFAULT = false;

	
	/**
	 * Internal data is the original data for learning. It can have missing values.
	 */
	protected LargeStatistics data = null;
	
	
	/**
	 * Indices for X data.
	 * Each element of xIndixes is an index which is an array of objects. 
	 * In current implementation, only the first object of the array is used.
	 * Such first object can be number, variable name (field name), or mathematical expression.
	 * If it is a number called number index, the 0 index always points to 1 and the 1 index points the first field (index 0) of profile.
	 * In other words, number index starts with 1 when users specify.
	 */
	protected List<Object[]> xIndices = Util.newList(); //Object list for parsing mathematical expressions in the most general case.
	
	 
	/**
	 * Indices for Z data.
	 * Each element of zIndices is an index which is an array of objects. Note, xIndices has only two indices. 
	 * In current implementation, only the first object of the array is used.
	 * Such first object can be number, variable name (field name), or mathematical expression.
	 * If it is a number called number index, the 0 index always points to 1 and the 1 index points the first field (index 0) of profile.
	 * In other words, number index starts with 1 when users specify.
	 */
	protected List<Object[]> zIndices = Util.newList(); //Object list for parsing mathematical expressions in the most general case.
	
	
	/**
	 * Attribute list for all variables: all X, Y, and z.
	 * This variable is also used as the indicator of successful learning (not null).
	 */
	protected AttributeList attList = null;
	
	
	/**
	 * Default constructor.
	 */
	public REMImpl() {
		// TODO Auto-generated constructor stub
		super();
	}
	
	
	@Override
	protected Object learn(Object...info) throws RemoteException {
		// TODO Auto-generated method stub
		Object resulted = null;
		if (prepareInternalData(this.sample))
			resulted = super.learn();
		if (resulted == null)
			clearInternalData();

		return resulted;
	}


	/**
	 * Preparing data.
	 * @param inputSample specified sample.
	 * @return true if data preparation is successful.
	 * @throws RemoteException if any error raises.
	 */
	protected boolean prepareInternalData(Fetcher<Profile> inputSample) throws RemoteException {
		clearInternalData();
		
		this.attList = getSampleAttributeList(inputSample);
		if (this.attList.size() < 2)
			return false;

		//Begin parsing indices
		String cfgIndices = this.getConfig().getAsString(R_INDICES_FIELD);
		if (this.xIndices == null) this.xIndices = Util.newList();
		if (this.zIndices == null) this.zIndices = Util.newList();
		if (!parseIndices(cfgIndices, this.attList.size(), this.xIndices, this.zIndices)) //parsing indices
			return false;
		//End parsing indices
		
		//Begin checking existence of values.
		boolean zExists = false;
		boolean[] xExists = new boolean[this.xIndices.size() - 1]; //profile = (x1, x2,..., x(n-1), z)
		Arrays.fill(xExists, false);
		while (inputSample.next()) {
			Profile profile = inputSample.pick(); //profile = (x1, x2,..., x(n-1), z)
			if (profile == null)
				continue;
			
			double lastValue = extractNumber(extractResponseValue(profile));
			if (Util.isUsed(lastValue))
				zExists = zExists || true; 
			
			for (int j = 1; j < this.xIndices.size(); j++) {
				double value = extractRegressorValue(profile, j);
				if (Util.isUsed(value))
					xExists[j - 1] = xExists[j - 1] || true;
			}
		}
		inputSample.reset();
		List<Object[]> xIndicesTemp = Util.newList();
		xIndicesTemp.add(this.xIndices.get(0)); //adding -1
		for (int j = 1; j < this.xIndices.size(); j++) {
			if (xExists[j - 1])
				xIndicesTemp.add(this.xIndices.get(j)); //only use variables having at least one value.
		}
		if (!zExists || xIndicesTemp.size() < 2) //Please pay attention here.
			return false;
		this.xIndices = xIndicesTemp;
		//End checking existence of values.
		
		//Begin extracting data
		List<double[]> xData = Util.newList();
		List<double[]> zData = Util.newList();
		while (inputSample.next()) {
			Profile profile = inputSample.pick(); //profile = (x1, x2,..., x(n-1), z)
			if (profile == null)
				continue;
			
			double[] xVector = new double[this.xIndices.size()]; //1, x1, x2,..., x(n-1)
			double[] zVector = new double[2]; //1, z
			xVector[0] = 1.0;
			zVector[0] = 1.0;
			
			double lastValue = extractNumber(extractResponseValue(profile));
			if (!Util.isUsed(lastValue))
				zVector[1] = Constants.UNUSED;
			else
				zVector[1] = (double)transformResponse(lastValue, false);
			
			for (int j = 1; j < this.xIndices.size(); j++) {
				double value = extractRegressorValue(profile, j);
				if (!Util.isUsed(value))
					xVector[j] = Constants.UNUSED;
				else
					xVector[j] = (double)transformRegressor(value, false);
			}
			
			zData.add(zVector);
			xData.add(xVector);
		}
		inputSample.reset();
		//End extracting data
		
		if (xData.size() == 0 || zData.size() == 0)
			return false;
		else {
			this.data = new LargeStatistics(xData, zData);
			return true;
		}
	}
	
	
	/**
	 * Setting internal data.
	 * @param xIndices specified X indices.
	 * @param zIndices specified Z indices.
	 * @param attList specified attribute list.
	 * @param data specified data.
	 * @return true if setting successful.
	 */
	protected boolean prepareInternalData(List<Object[]> xIndices, List<Object[]> zIndices, AttributeList attList, LargeStatistics data) {
		clearInternalData();
		this.xIndices = xIndices;
		this.zIndices = zIndices;
		this.attList = attList;
		this.data = data;
		return true;
	}
	
	
	/**
	 * Clear all internal data.
	 */
	protected void clearInternalData() {
		this.currentIteration = 0;
		this.currentParameter = this.estimatedParameter = null;
		if (this.xIndices != null) this.xIndices.clear();
		if (this.zIndices != null) this.zIndices.clear();
		this.attList = null;
		
		if (this.statistics != null && (this.statistics instanceof LargeStatistics))
			((LargeStatistics)this.statistics).clear();
		this.statistics = null;
		
		if (this.data != null)
			this.data.clear();
		this.data = null;
	}
	
	
	/**
	 * Expectation method of this class does not change internal data.
	 */
	@Override
	protected Object expectation(Object currentParameter, Object...info) throws RemoteException {
		// TODO Auto-generated method stub
		if (currentParameter == null)
			return null;
		List<Double> alpha = ((ExchangedParameter)currentParameter).getAlpha();
		List<double[]> betas = ((ExchangedParameter)currentParameter).getBetas();
		LargeStatistics data = null;
		if (info != null && info.length > 0 && (info[0] instanceof LargeStatistics))
			data = (LargeStatistics)info[0];
		else
			data = this.data;
		
		int N = data.getZData().size();
		List<double[]> zStatistic = Util.newList(N);
		List<double[]> xStatistic = Util.newList(N);
		for (int i = 0; i < N; i++) {
			Statistics stat0 = new Statistics(data.getZData().get(i)[1], data.getXData().get(i));
			Statistics stat = estimate(stat0, alpha, betas);
			if (stat == null)
				return null;
			
			stat = (stat.checkValid() ? stat : null);
			if (stat == null)
				return null;
			zStatistic.add(new double[] {1.0, stat.getZStatistic()});
			xStatistic.add(stat.getXStatistic());
		}
		
		return new LargeStatistics(xStatistic, zStatistic);
	}

	
	/**
	 * Expectation method of this class does not change internal data.
	 */
	@Override
	protected Object maximization(Object currentStatistic, Object...info) throws RemoteException {
		// TODO Auto-generated method stub
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
		
		List<double[]> betas = Util.newList(n);
		for (int j = 0; j < n; j++) {
			if (j == 0) {
				double[] beta0 = new double[2];
				beta0[0] = 1;
				beta0[1] = 0;
				betas.add(beta0);
				continue;
			}
			
			List<double[]> Z = Util.newList(N);
			List<Double> x = Util.newList(N);
			for (int i = 0; i < N; i++) {
				Z.add(zStatistic.get(i));
				x.add(xStatistic.get(i)[j]);
			}
			List<Double> beta = calcCoeffs(Z, x);
			if (beta == null || beta.size() == 0) {
				if (currentParameter != null)
					beta = DSUtil.toDoubleList(currentParameter.getBetas().get(j));
				else { //Used for initialization so that regression model is always determined.
					beta = DSUtil.initDoubleList(2, 0);
					double beta0 = 0;
					for (int i = 0; i < N; i++)
						beta0 += xStatistic.get(i)[j];
					beta.set(0, beta0 / (double)N); //constant function x = c
				}
			}
			betas.add(DSUtil.toDoubleArray(beta));
		}
		
		ExchangedParameter newParameter = new ExchangedParameter(alpha, betas);
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
	
	
	/**
	 * Estimating statistics with specified parameters alpha and beta. This method does not change internal data.
	 * Balance process is removed because it is over-fitting or not stable. Balance process is the best in some cases.
	 * @param stat specified statistics.
	 * @param alpha specified alpha parameter.
	 * @param betas specified alpha parameters.
	 * @return estimated statistics with specified parameters alpha and beta. Return null if any error raises.
	 */
	protected Statistics estimate(Statistics stat, List<Double> alpha, List<double[]> betas) {
		double zValue = stat.getZStatistic();
		double[] xVector = stat.getXStatistic();
		double zStatistic = Constants.UNUSED;
		double[] xStatistic = new double[xVector.length];
		
		if (Util.isUsed(zValue)) {
			zStatistic = zValue;
			
			//Estimating missing xij (xStatistic) by equation 5 and zi (zStatistic) above, based on current parameter.
			for (int j = 0; j < xVector.length; j++) {
				if (Util.isUsed(xVector[j]))
					xStatistic[j] = xVector[j];
				else
					xStatistic[j] = betas.get(j)[0] + betas.get(j)[1] * zStatistic;
			}
			
			return new Statistics(zStatistic, xStatistic);
		}
		
		//Estimating missing zi (zStatistic) by equation 7, based on current parameter.
		double a = 0, b = 0, c = 0;
		List<Integer> U = Util.newList(); //Indices of missing values.
		for (int j = 0; j < xVector.length; j++) {
			if (Util.isUsed(xVector[j])) {
				b += alpha.get(j) * xVector[j];
			}
			else {
				a += alpha.get(j) * betas.get(j)[0];
				c += alpha.get(j) * betas.get(j)[1];
				U.add(j);
			}
		}
		if (c != 1) {
			zStatistic = (a + b) / (1.0 - c);
		}
		else {
			LogUtil.info("Cannot estimate statistic for Z by expectation (#estimate), stop estimating for this statistic here because use of other method is wrong.");
			return null;
		}
		
		//Estimating missing xij (xStatistic) by equation 5 and estimated zi (zStatistic) above, based on current parameter.
		for (int j = 0; j < xVector.length; j++) {
			if (Util.isUsed(xVector[j]))
				xStatistic[j] = xVector[j];
			else
				xStatistic[j] = betas.get(j)[0] + betas.get(j)[1] * zStatistic;
		}
		
		//Balance process is removed because it is not necessary. Balance process is the best in some cases. So list U is not used.
		return new Statistics(zStatistic, xStatistic);
	}

	
	/**
	 * Estimating statistics with specified parameters alpha and beta. This method does not change internal data.
	 * Balance process is removed because it is over-fitting or not stable. Balance process is the best in some cases.
	 * This method is as good as than {@link #estimate(Statistics, List, List)} method but it is not stable for long regression model having many regressors
	 * because solving a set of many equations can cause approximate solution or non-solution problem.   
	 * @param stat specified statistics.
	 * @param alpha specified alpha parameter.
	 * @param betas specified alpha parameters.
	 * @return estimated statistics with specified parameters alpha and beta. Return null if any error raises.
	 */
	@Deprecated
	protected Statistics estimateInverse(Statistics stat, List<Double> alpha, List<double[]> betas) {
		double zValue = stat.getZStatistic();
		double[] xVector = stat.getXStatistic();
		double zStatistic = Constants.UNUSED;
		double[] xStatistic = new double[xVector.length];
		
		if (Util.isUsed(zValue)) {
			zStatistic = zValue;
			//Estimating missing xij (xStatistic) by equation 5 and zi (zStatistic) above, based on current parameter.
			for (int j = 0; j < xVector.length; j++) {
				if (Util.isUsed(xVector[j]))
					xStatistic[j] = xVector[j];
				else
					xStatistic[j] = betas.get(j)[0] + betas.get(j)[1] * zStatistic;
			}
			
			return new Statistics(zStatistic, xStatistic);
		}
		
		List<Integer> U = Util.newList();
		double b = 0;
		for (int j = 0; j < xVector.length; j++) {
			if (Util.isUsed(xVector[j])) {
				b += alpha.get(j) * xVector[j];
				xStatistic[j] = xVector[j]; //existent xij
			}
			else
				U.add(j);
		}

		if (U.size() > 0) {
			//Estimating missing xij (xStatistic) by equation 8, based on current parameter.
			List<double[]> A = Util.newList(U.size());
			List<Double> y = Util.newList(U.size());
			
			for (int i = 0; i < U.size(); i++) {
				double[] aRow = new double[U.size()];
				A.add(aRow);
				for (int j = 0; j < U.size(); j++) {
					if (i == j)
						aRow[j] = betas.get(U.get(i))[1] * alpha.get(U.get(j)) - 1;
					else
						aRow[j] = betas.get(U.get(i))[1] * alpha.get(U.get(j));
				}
				double yi = -betas.get(U.get(i))[0] - betas.get(U.get(i))[1] * b;
				y.add(yi);
			}
			
			List<Double> solution = solve(A, y); //solve Ax = y
			if (solution != null) {
				for (int j = 0; j < U.size(); j++) {
					int k = U.get(j);
					xStatistic[k] = solution.get(j);
				}
			}
			else {
				LogUtil.info("Cannot estimate statistic for X by expectation (#estimateInverse), stop estimating for this statistic here because use of other method is wrong.");
				return null;
			}
		}
		
		//Estimating missing zi (zStatistic) by equation 4, based on current parameter.
		zStatistic = 0;
		for (int j = 0; j < xStatistic.length; j++) {
			zStatistic += alpha.get(j) * xStatistic[j];
		}
		
		//Balance process is removed because it is not necessary. Balance process is the best in some cases. So list U is not used.
		return new Statistics(zStatistic, xStatistic);
	}

	
	/**
	 * Balancing missing values zi (xStatistic) and xij (xValues). This method does not change internal data.
	 * @param alpha alpha coefficients.
	 * @param betas beta coefficients.
	 * @param zStatistic statistic for Z variable.
	 * @param xStatistic statistic for X variables.
	 * @param U list of missing X values.
	 * @param inverse if true, this is inverse mode.
	 * @return balanced statistics for Z and X variables. Return null if any error raises.
	 */
	@Deprecated
	protected Statistics balanceStatistics(List<Double> alpha, List<double[]> betas,
			double zStatistic, double[] xStatistic,
			List<Integer> U, boolean inverse) {

		double zStatisticNext = Constants.UNUSED;
		double[] xStatisticNext = new double[xStatistic.length];
		int t = 0;
		int maxIteration = getConfig().getAsInt(EM_MAX_ITERATION_FIELD);
		maxIteration = (maxIteration <= 0) ? EM_MAX_ITERATION : maxIteration;
		double threshold = getConfig().getAsReal(EM_EPSILON_FIELD);
		while (t < maxIteration) {
			if (!inverse) {
				zStatisticNext = 0;
				for (int j = 0; j < xStatistic.length; j++)
					zStatisticNext += alpha.get(j) * xStatistic[j];
				
				for (int j = 0; j < xStatistic.length; j++) {
					if (!U.contains(j))
						xStatisticNext[j] = xStatistic[j];
					else
						xStatisticNext[j] = betas.get(j)[0] + betas.get(j)[1] * zStatisticNext;
				}
				
			}
			else {
				for (int j = 0; j < xStatistic.length; j++) {
					if (!U.contains(j))
						xStatisticNext[j] = xStatistic[j];
					else
						xStatisticNext[j] = betas.get(j)[0] + betas.get(j)[1] * zStatistic;
				}
				
				zStatisticNext = 0;
				for (int j = 0; j < xStatistic.length; j++)
					zStatisticNext += alpha.get(j) * xStatisticNext[j];
			}
			
			t++;
			
			//Testing approximation
			boolean approx = !notSatisfy(zStatisticNext, zStatistic, threshold);
			for (int j = 0; j < xStatistic.length; j++) {
				approx = approx && !notSatisfy(xStatisticNext[j], xStatistic[j], threshold);
				if (!approx) break;
			}
			
			zStatistic = zStatisticNext;
			xStatistic = xStatisticNext;
			zStatisticNext = Constants.UNUSED;
			xStatisticNext = new double[xStatistic.length];
			
			if (approx) break;
		} //If the likelihood function is too acute, the loop can be infinite.
		
		return new Statistics(zStatistic, xStatistic);
	}

	
	/**
	 * Initialization method of this class does not change internal data.
	 */
	@Override
	protected Object initializeParameter() {
		// TODO Auto-generated method stub
		int n = this.data.getXData().get(0).length;
		ExchangedParameter parameter0 = initializeAlphaBetas(n, false);
		
		LargeStatistics completeData = getCompleteData(this.data);
		if (completeData == null)
			return parameter0;
			
		try {
			ExchangedParameter parameter = (ExchangedParameter) maximization(completeData);
			return (parameter != null ? parameter : parameter0); 
		}
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return parameter0;
	}

	
	@Override
	protected boolean terminatedCondition(Object estimatedParameter, Object currentParameter, Object previousParameter, Object... info) {
		// TODO Auto-generated method stub
		double threshold = getConfig().getAsReal(EM_EPSILON_FIELD);
		
		return ((ExchangedParameter)estimatedParameter).terminatedCondition(
				threshold, 
				(ExchangedParameter)currentParameter, 
				(ExchangedParameter)previousParameter);
	}

	
	@Override
	public synchronized double executeByXStatistic(double[] xStatistic) throws RemoteException {
		if (xStatistic == null)
			return Constants.UNUSED;

		ExchangedParameter parameter = this.getExchangedParameter(); 
		if (parameter == null)
			return Constants.UNUSED;
		List<Double> alpha = parameter.getAlpha();

		Statistics stat = estimate(new Statistics(Constants.UNUSED, xStatistic), alpha, parameter.getBetas());
		if (stat == null)
			return Constants.UNUSED;
		else
			return (double)transformResponse(stat.getZStatistic(), true);
	}
	
	
	/**
	 * Executing by X statistics without transform. Please review method {@link #executeByXStatistic(double[])} to understand this method.
	 * In fact, this method does not apply method {@link #transformResponse(Object, boolean)} to the calculated result.
	 * @param xStatistic X statistics (regressors). The first element of this X statistics is 1.
	 * @return result of execution without transform. Return null if execution is failed.
	 */
	protected synchronized double executeByXStatisticWithoutTransform(double[] xStatistic) {
		if (xStatistic == null)
			return Constants.UNUSED;

		ExchangedParameter parameter = this.getExchangedParameter(); 
		if (parameter == null)
			return Constants.UNUSED;
		List<Double> alpha = parameter.getAlpha();

		Statistics stat = estimate(new Statistics(Constants.UNUSED, xStatistic), alpha, parameter.getBetas());
		if (stat == null)
			return Constants.UNUSED;
		else
			return stat.getZStatistic();
	}

	
	/**
	 * This method can be used to estimate Z value with incomplete profile. The input is often profile but it can be array of real values.
	 * In other words, it is possible to test with incomplete testing data.
	 */
	@Override
	public synchronized Object execute(Object input) throws RemoteException {
		// TODO Auto-generated method stub
		double[] xStatistic = extractRegressorValues(input);
		return executeByXStatistic(xStatistic);
	}
	
	
	/**
	 * Executing this algorithm by arbitrary input parameter.
	 * @param input arbitrary input parameter.
	 * @return result of execution. Return null if execution is failed.
	 * @throws RemoteException if any error raises.
	 */
	protected Object executeIntel(Object...input) throws RemoteException {
		return execute(input);
	}
	
	
	/**
	 * Getting attribute list.
	 * @return attribute list.
	 */
	protected AttributeList getAttributeList() {
		return this.attList;
	}
	
	
	/**
	 * Getting internal data. Actually, this method returns the current statistics.
	 * @return internal data which is the current statistics.
	 */
	protected synchronized LargeStatistics getData() {
		return this.data;
	}
	
	
	/**
	 * Testing whether missing values are fulfilled.
	 * @return true if missing values are fulfilled.
	 */
	protected synchronized boolean isMissingDataFilled() {
		return (this.statistics != null && this.statistics != this.data);
	}
	
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		String name = getConfig().getAsString(DUPLICATED_ALG_NAME_FIELD);
		if (name != null && !name.isEmpty())
			return name;
		else
			return "rem";
	}

	
	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub
		getConfig().put(DUPLICATED_ALG_NAME_FIELD, name);
	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		REMImpl em = new REMImpl();
		em.getConfig().putAll((DataConfig)this.getConfig().clone());
		return em;
	}

	
	@Override
	public DataConfig createDefaultConfig() {
		// TODO Auto-generated method stub
		DataConfig tempConfig = super.createDefaultConfig();
		tempConfig.put(R_INDICES_FIELD, R_INDICES_DEFAULT);
		tempConfig.put(R_CALC_VARIANCE_FIELD, R_CALC_VARIANCE_DEFAULT); //This attribute is used for testing
		tempConfig.addReadOnly(DUPLICATED_ALG_NAME_FIELD);
		
		DataConfig config = new DataConfig() {

			/**
			 * Default serial version UID.
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Serializable userEdit(Component comp, String key, Serializable defaultValue) {
				// TODO Auto-generated method stub
				if (key.equals(R_INDICES_FIELD)) {
					if (attList != null) {
						RegressResponseChooser chooser = new RegressResponseChooser(comp, attList);
						return chooser.getIndices();
					}
					
					DatasetLoader datasetLoader = new DatasetLoader(comp, DataConfig.SAMPLE_UNIT);
					Dataset dataset = datasetLoader.getDataset();
					if ((dataset == null) || (dataset instanceof Pointer)) {
						JOptionPane.showMessageDialog(
								comp, "Null dataset or pointer dataset", "Invalid dataset", JOptionPane.ERROR_MESSAGE);
						return null;
					}
					
					Fetcher<Profile> sample = dataset.fetchSample();
					AttributeList attributes = getSampleAttributeList(sample);
					try {
						sample.close();
					} catch (Exception e) { e.printStackTrace(); }
					
					if (attributes == null || attributes.size() == 0) {
						JOptionPane.showMessageDialog(
								comp, "Empty attribute list", "Invalid attribute list", JOptionPane.ERROR_MESSAGE);
						return null;
					}

					RegressResponseChooser chooser = new RegressResponseChooser(comp, attributes);
					return chooser.getIndices();
				}
				else
					return super.userEdit(comp, key, defaultValue);
			}
			
		};
		
		config.putAll(tempConfig);
		return config;
	}

	
	@Override
	public VarWrapper extractRegressor(int index) throws RemoteException {
		// TODO Auto-generated method stub
		return extractVariable(attList, xIndices, index);
	}

	
	@Override
	public List<VarWrapper> extractRegressors() throws RemoteException {
		// TODO Auto-generated method stub
		return extractVariables(attList, xIndices);
	}


	@Override
	public List<VarWrapper> extractSingleRegressors() throws RemoteException {
		// TODO Auto-generated method stub
		return extractSingleVariables(attList, xIndices);
	}


	@Override
	public double extractRegressorValue(Object input, int index) throws RemoteException {
		// TODO Auto-generated method stub
		if (input == null)
			return Constants.UNUSED;
		else if (input instanceof Profile)
			return extractVariableValue(input, null, xIndices, index);
		else
			return extractVariableValue(input, attList, xIndices, index);
	}


	/**
	 * Extract regressors from input object.
	 * @param input specified input object.
	 * @return list of values of regressors from input object.
	 * @throws RemoteException if any error raises.
	 */
	protected double[] extractRegressorValues(Object input) throws RemoteException {
		Profile profile = null;
		if (input instanceof Profile)
			profile = (Profile)input;
		else
			profile = createProfile(this.attList, input);
		if (profile == null)
			return null;
		
		double[] xStatistic = new double[this.xIndices.size()];
		xStatistic[0] = 1;
		for (int j = 1; j < this.xIndices.size(); j++) {
			double xValue = extractRegressorValue(profile, j);
			if (Util.isUsed(xValue))
				xStatistic[j] = (double)transformRegressor(xValue, false);
			else
				xStatistic[j] = Constants.UNUSED;
		}
		
		return xStatistic;
	}
	
	
	@Override
	public synchronized List<Double> extractRegressorStatistic(VarWrapper regressor) throws RemoteException {
		// TODO Auto-generated method stub
		LargeStatistics stats = getLargeStatistics();
		if (stats != null)
			return stats.getXColumnStatistic(regressor.getIndex());
		else
			return Util.newList();
	}


	@Override
	public VarWrapper extractResponse() throws RemoteException {
		// TODO Auto-generated method stub
		return extractVariable(attList, zIndices, 1);
	}


	@Override
	public synchronized Object extractResponseValue(Object input) throws RemoteException {
		// TODO Auto-generated method stub
		if (input == null)
			return Constants.UNUSED;
		else if (input instanceof Profile)
			return extractVariableValue(input, null, zIndices, 1);
		else
			return extractVariableValue(input, attList, zIndices, 1);
	}


	/**
	 * Calculating variance from specified sample.
	 * @param inputSample specified sample.
	 * @return variance from specified sample. This variance is also called 
	 */
	protected synchronized double variance(Fetcher<Profile> inputSample) {
		double ss = 0;
		int count = 0;
		
		try {
			while (inputSample.next()) {
				Profile profile = inputSample.pick();
				if (profile == null)
					continue;
				
				double zValue = extractNumber(extractResponseValue(profile));
				double executedValue = extractNumber(execute(profile)); //Synchronize due to execute method.
				if (Util.isUsed(zValue) && Util.isUsed(executedValue)) {
					double d = executedValue - zValue;
					ss += d*d;
					count++;
				}
			}
			inputSample.reset();
			
			if (count == 0)
				return Constants.UNUSED;
			else
				return ss / (double)count;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return Constants.UNUSED;
	}
	
	
	/**
	 * Estimating given sample.
	 * @param inputSample given sample.
	 * @return estimated sample.
	 * @throws RemoteException if any error raises.
	 */
	protected synchronized Fetcher<Profile> fulfill(Fetcher<Profile> inputSample) throws RemoteException {
		// TODO Auto-generated method stub
		if (this.getParameter() == null)
			return null;
		
		REMImpl em = (REMImpl)this.newInstance();
		LargeStatistics stat = null;
		try {
			if (em.prepareInternalData(inputSample))
				stat = (LargeStatistics) this.expectation(this.getParameter(), em.getData());
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			stat = null;
		}
		if (stat == null)
			return null;
		
		int N = stat.getZData().size();
		AttributeList attRef = getSampleAttributeList(inputSample);
		List<Profile> profiles = Util.newList();
		for (int i = 0; i < N; i++) {
			Profile profile = new Profile(attRef);
			double[] xvector = stat.getXData().get(i);
			double z = stat.getZData().get(i)[1];
			profile.setValue(attRef.size() - 1, z);
			
			for (int j = 0; j < attRef.size() - 1; j++) {
				int foundX = findIndex(em.xIndices, j);
				if (foundX >= 0)
					profile.setValue(j, xvector[foundX]);
			}
			profiles.add(profile);
		}
		em.clearInternalData();

		return new MemFetcher<>(profiles);
	}


	
}


