package net.jsi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface UniverseRemoteGetter extends Remote {
	

	UniverseRemote getUniverseRemote(String account, String passwords) throws RemoteException;


}
