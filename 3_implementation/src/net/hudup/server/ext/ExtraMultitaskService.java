package net.hudup.server.ext;

import java.io.File;
import java.rmi.RemoteException;

import net.hudup.core.client.ExtraService;
import net.hudup.core.client.ExtraServiceAbstract;
import net.hudup.core.client.PowerServer;
import net.hudup.core.logistic.LogUtil;
import net.jsi.StockProperty;
import net.jsi.UniverseImpl;
import net.jsi.UniverseRemote;
import net.jsi.UniverseRemoteImpl;

/**
 * This interface represents a extra multi-task service.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface ExtraMultitaskService extends ExtraService {

	
	/**
	 * Getting remote universe.
	 * @return remote universe.
	 * @throws RemoteException if any error raises.
	 */
	UniverseRemote getUniverseRemote() throws RemoteException;
	
	
}



/**
 * This class is an implementation of a extra multi-task service.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
class ExtraMultitaskServiceImpl extends ExtraServiceAbstract implements ExtraMultitaskService {

	
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
	public ExtraMultitaskServiceImpl(PowerServer server) {
		super(server);
	}
	
	
	@Override
	public UniverseRemote getUniverseRemote() throws RemoteException {
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
	private ExtraMultitaskServiceImpl getThisService() {
		return this;
	}
	
	
	@Override
	public void close() throws Exception {
		if (remoteUniverse != null) {
			if (isAdminAccount()) remoteUniverse.save(new File(StockProperty.WORKING_DIRECTORY));
			remoteUniverse.unexport();
		}
		remoteUniverse = null;
	}



}
