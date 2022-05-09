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

import net.hudup.core.TaskAbstract;
import net.hudup.core.client.ConnectInfo;
import net.hudup.core.client.PowerServer;
import net.jsi.StockProperty;
import net.jsi.Universe;
import net.jsi.UniverseImpl;
import net.jsi.UniverseRemote;
import net.jsi.UniverseRemoteImpl;
import net.jsi.ui.Investor;

/**
 * This class represents JSI task.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class JSITask extends TaskAbstract {

	
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
	protected UniverseRemoteImpl jsiUniverseRemote = null;

	
	/**
	 * Constructor with server.
	 * @param server power server.
	 */
	public JSITask(PowerServer server, JSITasker jsiTasker, UniverseRemoteImpl jsiUniverseRemote) {
		super(jsiTasker);
		this.server = server;
		this.jsiUniverseRemote = jsiUniverseRemote;
	}

	
	@Override
	public String getDesc() throws RemoteException {
		return "Financial investor (JSI)";
	}

	
	/**
	 * Getting JSI tasker.
	 * @return JSI tasker.
	 */
	private JSITasker getJSITasker() {
		return (JSITasker)tasker;
	}
	
	
	@Override
	public boolean discard() throws RemoteException {
		JSITasker jsiTasker = getJSITasker();
		if (jsiTasker == null || this != jsiTasker.jsiTask)
			return discard0();
		else
			return jsiTasker.discard(this);
	}

	
	/**
	 * Discard this JSI task.
	 * @return true if discarding is successful.
	 */
	protected boolean discard0() {
		if (jsiUniverseRemote == null) return false;
		
		synchronized (jsiUniverseRemote) {
			saveJSIUniverse();
			try {
				jsiUniverseRemote.unexport();
			} catch (Throwable e) {net.jsi.Util.trace(e);}
			jsiUniverseRemote = null;
			
			return true;
		}
	}
	
	
	@Override
	public boolean serverDo() throws RemoteException {
		saveJSIUniverse();
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
	private void saveJSIUniverse() {
		File workingDir = new File(StockProperty.WORKING_DIRECTORY);
		synchronized (jsiUniverseRemote) {
			jsiUniverseRemote.apply();
			jsiUniverseRemote.save(workingDir);
		}
		
		try {
			jsiUniverseRemote.saveBackup(workingDir);
		}
		catch (Throwable e) {}
	}


}
