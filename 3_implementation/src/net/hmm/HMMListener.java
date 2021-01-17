package net.hmm;

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
	
	
}
