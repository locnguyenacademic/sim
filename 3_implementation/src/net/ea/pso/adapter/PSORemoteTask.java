/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.ea.pso.adapter;

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
	 * New setting up method with mathematical expression of function.
	 * @param varNames variable names.
	 * @param funcExpr mathematical expression of function.
	 * @throws RemoteException if any error raises.
	 */
	void setup(List<String> varNames, String funcExpr) throws RemoteException;

	
}
