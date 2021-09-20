package net.hudup.server.ext;

import java.io.File;
import java.rmi.RemoteException;

import net.hudup.core.client.ExtraService;
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
	 * Getting remote universe as investor.
	 * @return remote universe as investor.
	 * @throws RemoteException
	 */
	UniverseRemote getInvestor() throws RemoteException;
	
	
}



/**
 * This class is an implementation of a extra multi-task service.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
class ExtraMultitaskServiceImpl implements ExtraMultitaskService {

	
	/**
	 * Remote universe as the investor.
	 */
	protected UniverseRemoteImpl investor = null;
	
	
	/**
	 * Working directory.
	 */
	protected File workingDir = null;
	
	
	/**
	 * Server port.
	 */
	protected int serverPort = 0;
	
	
	/**
	 * Constructor with working directory and server port.
	 * @param workingDir working directory.
	 * @param serverPort server port.
	 */
	public ExtraMultitaskServiceImpl(File workingDir, int serverPort) {
		this.workingDir = workingDir;
		this.serverPort = serverPort;
	}
	
	
	@Override
	public UniverseRemote getInvestor() throws RemoteException {
		return investor;
	}


	@Override
	public boolean open() throws RemoteException {
		try {
			close();
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
		
		this.investor = new UniverseRemoteImpl(new UniverseImpl());
		this.investor.open(workingDir);
		this.investor.export(serverPort);

		return true;
	}
	
	
	@Override
	public void close() throws Exception {
		if (investor != null) {
			investor.save(new File(StockProperty.WORKING_DIRECTORY));
			investor.unexport();
		}
		investor = null;
	}

	
}
