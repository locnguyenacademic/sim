/**
 * JSI: JAGGED STRATEGY INVESTMENT 
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: jsi.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.jsi.adapter;

import java.io.File;
import java.rmi.Remote;
import java.rmi.RemoteException;

import net.hudup.core.AppAbstract;
import net.hudup.core.client.ConnectInfo;
import net.hudup.core.client.PowerServer;
import net.jsi.StockProperty;
import net.jsi.Universe;
import net.jsi.UniverseImpl;
import net.jsi.UniverseRemote;
import net.jsi.UniverseRemoteImpl;
import net.jsi.ui.Investor;

/**
 * This class represents JSI application.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class JSIApp extends AppAbstract {

	
	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Power server.
	 */
	protected PowerServer server = null;
	
	
	/**
	 * Remote JSI universe.
	 */
	protected UniverseRemote jsiUniverseRemote = null;

	
	/**
	 * Constructor with server, application creator, and remote universe.
	 * @param server power server.
	 * @param jsiAppor application creator.
	 * @param jsiUniverseRemote remote JSI universe.
	 */
	public JSIApp(PowerServer server, JSIAppor jsiAppor, UniverseRemote jsiUniverseRemote) {
		super(jsiAppor);
		this.server = server;
		this.jsiUniverseRemote = jsiUniverseRemote;
	}

	
	@Override
	public String getDesc() throws RemoteException {
		return "Financial investor (JSI)";
	}

	
	@Override
	protected boolean discard0() {
		if (jsiUniverseRemote == null) return false;
		
		synchronized (jsiUniverseRemote) {
			saveJSIUniverseLocal();
			try {
				jsiUniverseRemote.unexport();
			} catch (Throwable e) {net.jsi.Util.trace(e);}
			jsiUniverseRemote = null;
			
			return true;
		}
	}
	
	
	@Override
	public boolean serverTask() throws RemoteException {
		saveJSIUniverseLocal();
		return true;
	}

	
	@Override
	public void show(ConnectInfo connectInfo) throws RemoteException {
		if (connectInfo == null) return;
		
		try {
			UniverseRemote remoteUniverse = null;
			Universe universe = null;
			if (connectInfo.bindUri == null)
				universe = jsiUniverseRemote != null ? ((UniverseRemoteImpl)jsiUniverseRemote).getUniverse() : null;
			else {
				universe = new UniverseImpl();
				remoteUniverse = jsiUniverseRemote;
			}

			if (universe != null) new Investor(universe, remoteUniverse, connectInfo.bindUri == null).setVisible(true);
		}
		catch (Exception e) {
			net.jsi.Util.trace(e);
		}
	}

	
	@Override
	public Remote getRemoteObject() throws RemoteException {
		return jsiUniverseRemote;
	}


	/**
	 * Saving JSI universe.
	 */
	private void saveJSIUniverseLocal() {
		File workingDir = new File(StockProperty.WORKING_DIRECTORY);
		synchronized (jsiUniverseRemote) {
			if (jsiUniverseRemote instanceof UniverseRemoteImpl) {
				((UniverseRemoteImpl)jsiUniverseRemote).apply();
				((UniverseRemoteImpl)jsiUniverseRemote).save(workingDir);
			}
		}
		
		try {
			if (jsiUniverseRemote instanceof UniverseRemoteImpl)
				((UniverseRemoteImpl)jsiUniverseRemote).saveBackup(workingDir);
		}
		catch (Throwable e) {}
	}


}
