/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.temp.ea.pso2;

import java.rmi.RemoteException;
import java.util.List;

import net.hudup.core.alg.ExecuteAsLearnAlgRemoteTask;

/**
 * This interface declares methods for remote particle swarm optimization (PSO) algorithm.
 * 
 * @author Loc Nguyen
 * @version 2.0
 *
 */
public interface PSORemoteTask extends ExecuteAsLearnAlgRemoteTask {


	/**
	 * New setting up method.
	 * @throws RemoteException if any error raises.
	 */
	void setup() throws RemoteException;

	
	/**
	 * Getting target function (cost function).
	 * @return target function (cost function).
	 * @throws RemoteException if any error raises.
	 */
	Function<?> getFunction() throws RemoteException;

	
	/**
	 * Setting target function (cost function).
	 * @param func target function (cost function).
	 * @throws RemoteException if any error raises.
	 */
	void setFunction(Function<?> func) throws RemoteException;

	
	/**
	 * Setting target function (cost function) with mathematical expression.
	 * @param varNames variable names.
	 * @param funcExpr mathematical expression of target function (cost function).
	 * @throws RemoteException if any error raises.
	 */
	void setFunction(List<String> varNames, String funcExpr) throws RemoteException;

	
	/**
	 * Getting PSO configuration.
	 * @return PSO configuration.
	 * @throws RemoteException if any error raises.
	 */
	PSOConfig<?> getPSOConfig() throws RemoteException;
	
	
	/**
	 * Setting PSO configuration.
	 * @param psoConfig PSO configuration.
	 * @throws RemoteException if any error raises.
	 */
	void setPSOConfig(PSOConfig<?> psoConfig) throws RemoteException;
	
	
}
