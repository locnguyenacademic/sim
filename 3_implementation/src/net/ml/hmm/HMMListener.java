/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.ml.hmm;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.EventListener;

/**
 * This interface represents listener for hidden Markov model (HMM).
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface HMMListener extends EventListener, Remote, Serializable, Cloneable {

	
	/**
	 * Receiving information event.
	 * @param evt information event.
	 * @throws RemoteException if any error raises.
	 */
	void receivedInfo(HMMInfoEvent evt) throws RemoteException;
	
	
	/**
	 * Receiving learning event.
	 * @param evt learning event.
	 * @throws RemoteException if any error raises.
	 */
	void receivedDo(HMMDoEvent evt) throws RemoteException;


}
