/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.temp.rem;

import java.rmi.RemoteException;
import java.util.List;

import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.alg.DuplicatableAlg;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.Profile;
import net.hudup.core.logistic.NextUpdate;
import net.hudup.core.logistic.xURI;
import net.rem.regression.LargeStatistics;
import net.rem.regression.RMAbstract;
import net.rem.regression.VarWrapper;
import net.rem.regression.em.ui.graph.Graph;

/**
 * This class implements regression model with correlation in case of missing data, called COR algorithm.
 *  
 * @author Loc Nguyen
 * @version 1.0
 *
 */
@NextUpdate
@Deprecated
public class CorRegression extends RMAbstract implements DuplicatableAlg {

	
	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * List of X vectors
	 */
	protected List<List<Double>> xVectors = Util.newList();
	
	
	/**
	 * Z vector.
	 */
	protected List<Double> zVector = Util.newList();

	
	/**
	 * Default constructor.
	 */
	public CorRegression() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	@Override
	protected Object learn0() throws RemoteException {
		int n = xIndices.size();
		List<double[]> A = Util.newList(n);
		List<Double> b = Util.newList(n);
		
		for (int i = 0; i < n; i++) {
			double covXiZ = cor(xVectors.get(i), zVector);
			b.add(covXiZ);
			
			double[] vector = new double[n];
			A.add(vector);
		}
		
		for (int i = 0; i < n; i++) {
			for (int j = i; j < n; j++) {
				double covXiXj = cor(xVectors.get(i), xVectors.get(j));
				A.get(i)[j] = covXiXj;
			}
		}

		//Due to correlation matrix is symmetric
		for (int i = n-1; i >= 1; i--) {
			for (int j = i-1; j >= 0; j--) {
				A.get(i)[j] = A.get(j)[i];
			}
		}
		
		this.coeffs = RMAbstract.solve(A, b);
		if (this.coeffs == null)
			return null;
		
		//Adjusting intercept, improved later
		double sumZ = 0;
		int nZ = 0;
		for (int i = 0; i < zVector.size(); i++) {
			double z = zVector.get(i) != null ? zVector.get(i) : Constants.UNUSED; 
			if (Util.isUsed(z)) {
				sumZ += zVector.get(i);
				nZ++;
			}
		}
		this.coeffs.set(0, this.coeffs.get(0) + sumZ / (double)nZ);
		//Adjusting intercept, improved later
		
		return this.coeffs;
	}

	
	/**
	 * Calculating the correlation between two vectors. This method will be re-implemented in future.
	 * @param xVector specified vector x.
	 * @param yVector specified vector y.
	 * @return the correlation between two vectors.
	 */
	private double cor(List<Double> xVector, List<Double> yVector) {
		if (xVector.size() == 0 || yVector.size() == 0)
			return 0;
		
		int N = Math.min(xVector.size(), yVector.size());
		List<Integer> U = Util.newList(N);
		double meanX = 0, meanY = 0;
		for (int i = 0; i < N; i++) {
			double x = xVector.get(i) != null ? xVector.get(i) : Constants.UNUSED; 
			double y = yVector.get(i) != null ? yVector.get(i) : Constants.UNUSED; 
			if (!Util.isUsed(x) || !Util.isUsed(y))
				continue;
			
			meanX += x;
			meanY += y;
			U.add(i);
		}
		if (U.size() == 0)
			return 0;
		
		meanX = meanX / (double)U.size();
		meanY = meanY / (double)U.size();
		double cov = 0;
		double varX = 0;
		double varY = 0;
		for (int i = 0; i < U.size(); i++) {
			int k = U.get(i);
			double x = xVector.get(k) - meanX;
			double y = yVector.get(k) - meanY;
			cov += x * y;
			varX += x * x;
			varY += y * y;
		}

		if (varX != 0 && varY != 0) {
			return cov / (Math.sqrt(varX*varY));
		}
		else {
			if (varX == 0 && varY == 0)
				return 1;
			else
				return 0;
		}
	}

	
	@SuppressWarnings("unchecked")
	@Override
	protected boolean prepareInternalData() throws RemoteException {
		// TODO Auto-generated method stub
		if (!super.prepareInternalData())
			return false;
		
		//Begin extracting data
		while (((Fetcher<Profile>)sample).next()) {
			Profile profile = ((Fetcher<Profile>)sample).pick(); //profile = (x1, x2,..., x(n-1), z)
			if (profile == null)
				continue;
			
			List<Double> xVector0 = null;
			if (this.xVectors.size() <= 0) {
				xVector0 = Util.newList();
				this.xVectors.add(xVector0);
			}
			xVector0 = this.xVectors.get(0);
			xVector0.add(1.0);
			
			for (int j = 1; j < this.xIndices.size(); j++) {
				List<Double> xVector = null;
				if (this.xVectors.size() <= j) {
					xVector = Util.newList();
					this.xVectors.add(xVector);
				}
				xVector = this.xVectors.get(j);

				double value = extractRegressorValue(profile, j);
				xVector.add((double)transformRegressor(value, false));
			}
			
			double lastValue = extractNumber(extractResponseValue(profile));
			this.zVector.add((double)transformResponse(lastValue, false));
		}
		((Fetcher<Profile>)sample).reset();
		//End extracting data
		
		return true;
	}

	
	@Override
	public synchronized void unsetup() throws RemoteException {
		super.unsetup();
		xVectors.clear();
		zVector.clear();
	}

	
	@Override
	protected void clearInternalData() {
		// TODO Auto-generated method stub
		super.clearInternalData();
		xVectors.clear();
		zVector.clear();
	}


	@Override
	public LargeStatistics getLargeStatistics() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String getName() {
		// TODO Auto-generated method stub
		String name = getConfig().getAsString(DUPLICATED_ALG_NAME_FIELD);
		if (name != null && !name.isEmpty())
			return name;
		else
			return "corr";
	}

	
	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub
		getConfig().put(DUPLICATED_ALG_NAME_FIELD, name);
	}


	@Override
	public DataConfig createDefaultConfig() {
		DataConfig config = super.createDefaultConfig();
		config.addReadOnly(DUPLICATED_ALG_NAME_FIELD);
		return config;
	}


	@Override
	public List<VarWrapper> extractRegressors() throws RemoteException {
		return null;
	}


	@Override
	public List<VarWrapper> extractSingleRegressors() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<Double> extractRegressorStatistic(VarWrapper regressor) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public double executeByXStatistic(double[] xStatistic) throws RemoteException {
		// TODO Auto-generated method stub
		return Constants.UNUSED;
	}


	@Override
	public Graph createRegressorGraph(VarWrapper regressor) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Graph createResponseGraph() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Graph createErrorGraph() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<Graph> createResponseRalatedGraphs() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public double calcVariance() throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public double calcR() throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public double[] calcError() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public boolean saveLargeStatistics(xURI uri, int decimal) throws RemoteException {
		// TODO Auto-generated method stub
		return RMAbstract.saveLargeStatistics(this, getLargeStatistics(), uri, decimal);
	}
	
	
}
