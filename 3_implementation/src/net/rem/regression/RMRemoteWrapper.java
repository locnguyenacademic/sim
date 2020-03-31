package net.rem.regression;

import java.rmi.RemoteException;
import java.util.List;

import net.hudup.core.alg.Alg;
import net.hudup.core.alg.ExecutableAlgRemoteWrapper;
import net.hudup.core.data.DataConfig;
import net.hudup.core.logistic.BaseClass;
import net.hudup.core.logistic.Inspector;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.xURI;
import net.rem.regression.em.ui.graph.Graph;

/**
 * The class is a wrapper of remote regression algorithm. This is a trick to use RMI object but not to break the defined programming architecture.
 * In fact, RMI mechanism has some troubles or it it affect negatively good architecture.
 * For usage, an algorithm as REM will has a pair: REM stub (remote regression algorithm) and REM wrapper (normal regression algorithm).
 * The server creates REM stub (remote regression algorithm) and the client creates and uses the REM wrapper as normal regression algorithm.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
@BaseClass //The annotation is very important which prevent Firer to instantiate the wrapper without referred remote object. This wrapper is not normal algorithm.
public class RMRemoteWrapper extends ExecutableAlgRemoteWrapper implements RM, RMRemote {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Constructor with specified remote regression model.
	 * @param remoteRM specified remote regression model.
	 */
	public RMRemoteWrapper(RMRemote remoteRM) {
		super(remoteRM);
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * Constructor with specified remote regression model and exclusive mode.
	 * @param remoteRM specified remote regression model.
	 * @param exclusive specified exclusive mode.
	 */
	public RMRemoteWrapper(RMRemote remoteRM, boolean exclusive) {
		super(remoteRM, exclusive);
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public synchronized Inspector getInspector() {
		// TODO Auto-generated method stub
		return RMAbstract.getInspector(this);
	}


	@Override
	public String[] getBaseRemoteInterfaceNames() throws RemoteException {
		// TODO Auto-generated method stub
		return new String[] {RMRemote.class.getName()};
	}

	
	@Override
	public Object extractResponseValue(Object input) throws RemoteException {
		// TODO Auto-generated method stub
		return ((RMRemote)remoteAlg).extractResponseValue(input);
	}

	
	@Override
	public LargeStatistics getLargeStatistics() throws RemoteException {
		// TODO Auto-generated method stub
		return ((RMRemote)remoteAlg).getLargeStatistics();
	}

	
	@Override
	public double executeByXStatistic(double[] xStatistic) throws RemoteException {
		// TODO Auto-generated method stub
		return ((RMRemote)remoteAlg).executeByXStatistic(xStatistic);
	}

	
	@Override
	public VarWrapper extractRegressor(int index) throws RemoteException {
		// TODO Auto-generated method stub
		return ((RMRemote)remoteAlg).extractRegressor(index);
	}

	
	@Override
	public List<VarWrapper> extractRegressors() throws RemoteException {
		// TODO Auto-generated method stub
		return ((RMRemote)remoteAlg).extractRegressors();
	}

	
	@Override
	public List<VarWrapper> extractSingleRegressors() throws RemoteException {
		// TODO Auto-generated method stub
		return ((RMRemote)remoteAlg).extractSingleRegressors();
	}

	
	@Override
	public double extractRegressorValue(Object input, int index) throws RemoteException {
		// TODO Auto-generated method stub
		return ((RMRemote)remoteAlg).extractRegressorValue(input, index);
	}

	
	@Override
	public List<Double> extractRegressorStatistic(VarWrapper regressor) throws RemoteException {
		// TODO Auto-generated method stub
		return ((RMRemote)remoteAlg).extractRegressorStatistic(regressor);
	}

	
	@Override
	public VarWrapper extractResponse() throws RemoteException {
		// TODO Auto-generated method stub
		return ((RMRemote)remoteAlg).extractResponse();
	}

	
	@Override
	public Object transformResponse(Object z, boolean inverse) throws RemoteException {
		// TODO Auto-generated method stub
		return ((RMRemote)remoteAlg).transformResponse(z, inverse);
	}

	
	@Override
	public Graph createRegressorGraph(VarWrapper regressor) throws RemoteException {
		// TODO Auto-generated method stub
		return ((RMRemote)remoteAlg).createRegressorGraph(regressor);
	}

	
	@Override
	public Graph createResponseGraph() throws RemoteException {
		// TODO Auto-generated method stub
		return ((RMRemote)remoteAlg).createResponseGraph();
	}

	
	@Override
	public Graph createErrorGraph() throws RemoteException {
		// TODO Auto-generated method stub
		return ((RMRemote)remoteAlg).createErrorGraph();
	}

	
	@Override
	public List<Graph> createResponseRalatedGraphs() throws RemoteException {
		// TODO Auto-generated method stub
		return ((RMRemote)remoteAlg).createResponseRalatedGraphs();
	}

	
	@Override
	public double calcVariance() throws RemoteException {
		// TODO Auto-generated method stub
		return ((RMRemote)remoteAlg).calcVariance();
	}

	
	@Override
	public double calcR() throws RemoteException {
		// TODO Auto-generated method stub
		return ((RMRemote)remoteAlg).calcR();
	}

	
	@Override
	public double[] calcError() throws RemoteException {
		// TODO Auto-generated method stub
		return ((RMRemote)remoteAlg).calcError();
	}

	
	@Override
	public boolean saveLargeStatistics(xURI uri, int decimal) throws RemoteException {
		// TODO Auto-generated method stub
		return ((RMRemote)remoteAlg).saveLargeStatistics(uri, decimal);
	}

	
	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		if (remoteAlg instanceof RMAbstract) {
			RMAbstract newRM = (RMAbstract) ((RMAbstract)remoteAlg).newInstance();
			return new RMRemoteWrapper(newRM, exclusive);
		}
		else {
			LogUtil.warn("newInstance() returns itselfs and so does not return new object");
			return this;
		}
	}


	@Override
	public DataConfig createDefaultConfig() {
		// TODO Auto-generated method stub
		if (remoteAlg instanceof RM)
			return ((RM)remoteAlg).createDefaultConfig();
		else {
			LogUtil.warn("Wrapper of remote RM algorithm does not support createDefaultConfig()");
			return null;
		}
	}


}
