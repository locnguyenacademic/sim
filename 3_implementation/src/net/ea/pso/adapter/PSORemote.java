/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.ea.pso.adapter;

import java.rmi.RemoteException;

import net.hudup.core.alg.ExecuteAsLearnAlgRemote;

/**
 * This interface represents remote particle swarm optimization (PSO) algorithm.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface PSORemote extends PSORemoteTask, ExecuteAsLearnAlgRemote {


	@Override
	void setup() throws RemoteException;

	
}
