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
	 * Remote universe.
	 */
	protected UniverseRemoteImpl remoteUniverse = null;
	
	
	/**
	 * Constructor with working directory and server port.
	 * @param server power server.
	 */
	public ExtraMultitaskService(PowerServer server) {
		super(server);
	}
	

	/**
	 * Getting remote universe.
	 * @return remote universe.
	 */
	public UniverseRemote getUniverseRemote() {
		return remoteUniverse;
	}


	@Override
	public boolean open() throws RemoteException {
		try {
			close();
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
		
		remoteUniverse = new UniverseRemoteImpl(new UniverseImpl()) {
			private static final long serialVersionUID = 1L;

			@Override
			protected boolean isAdminAccount() {
				return getThisService().isAdminAccount();
			}
		};
		remoteUniverse.open(new File(StockProperty.WORKING_DIRECTORY));
		remoteUniverse.export(server.getPort());

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
		if (remoteUniverse != null) {
			remoteUniverse.save(new File(StockProperty.WORKING_DIRECTORY));
			remoteUniverse.unexport();
		}
		remoteUniverse = null;
	}


}
