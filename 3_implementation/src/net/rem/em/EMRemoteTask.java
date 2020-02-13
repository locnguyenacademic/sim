/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.rem.em;

import java.rmi.RemoteException;

import net.hudup.core.alg.ExecutableAlgRemoteTask;

/**
 * This interface declares methods for remote expectation maximization (EM) algorithm.
 * 
 * @author Loc Nguyen
 * @version 12.0
 *
 */
public interface EMRemoteTask extends ExecutableAlgRemoteTask {


	/**
	 * Getting current statistics.
	 * @return current statistics. Return null if the algorithm does not run yet or run failed.
	 * @throws RemoteException if any error raises.
	 */
	Object getStatistics() throws RemoteException;


}
