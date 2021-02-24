/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.ea.ann;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.EventListener;

/**
 * This interface represents listener for neural network.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface NetworkListener extends EventListener, Remote, Serializable, Cloneable {

	
	/**
	 * Receiving information event.
	 * @param evt information event.
	 * @throws RemoteException if any error raises.
	 */
	void receivedInfo(NetworkInfoEvent evt) throws RemoteException;
	
	
	/**
	 * Receiving learning event.
	 * @param evt learning event.
	 * @throws RemoteException if any error raises.
	 */
	void receivedDo(NetworkDoEvent evt) throws RemoteException;


}
