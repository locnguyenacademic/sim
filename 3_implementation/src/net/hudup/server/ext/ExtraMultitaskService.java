package net.hudup.server.ext;

import java.io.File;
import java.rmi.RemoteException;

import net.hudup.core.client.ExtraServiceAbstract;
import net.hudup.core.client.PowerServer;
import net.hudup.core.logistic.LogUtil;
import net.jsi.StockProperty;
import net.jsi.UniverseImpl;
import net.jsi.UniverseRemote;
import net.jsi.UniverseRemoteImpl;

/**
 * This class represents a extra multi-task service.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class ExtraMultitaskService extends ExtraServiceAbstract {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Remote JSI universe.
	 */
	protected UniverseRemoteImpl jsiUniverseRemote = null;
	
	
	/**
	 * Constructor with working directory and server port.
	 * @param server power server.
	 */
	public ExtraMultitaskService(PowerServer server) {
		super(server);
	}
	

	/**
	 * Getting remote JSI universe.
	 * @return remote JSI universe.
	 */
	public UniverseRemote getJSIUniverseRemote() {
		return jsiUniverseRemote;
	}


	@Override
	public boolean open() throws RemoteException {
		try {
			close();
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
		
		jsiUniverseRemote = new UniverseRemoteImpl(new UniverseImpl()) {
			private static final long serialVersionUID = 1L;

			@Override
			protected boolean isAdminAccount() {
				return getThisService().isAdminAccount();
			}
		};
		jsiUniverseRemote.open(new File(StockProperty.WORKING_DIRECTORY));
		jsiUniverseRemote.export(server.getPort());

		return true;
	}
	
	
	/**
	 * Get this extra service.
	 * @return this extra service.
	 */
	private ExtraMultitaskService getThisService() {
		return this;
	}
	
	
	@Override
	public void close() throws Exception {
		if (jsiUniverseRemote != null) {
			synchronized (jsiUniverseRemote) {
				saveJSIUniverse();
				jsiUniverseRemote.unexport();
			}
		}
		jsiUniverseRemote = null;
	}


	/**
	 * Saving JSI universe.
	 */
	protected void saveJSIUniverse() {
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
