package net.jsi;

import java.rmi.Remote;
import java.rmi.RemoteException;

@Deprecated
public interface UniverseRemoteGetter extends Remote {
	

	/**
	 * Getting remote universe.
	 * @param account specified account.
	 * @param passwords specified password.
	 * @return remote universe.
	 * @throws RemoteException if any error raises.
	 */
	UniverseRemote getUniverseRemote(String account, String passwords) throws RemoteException;


}
