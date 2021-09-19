package net.hudup.server.ext;

import java.rmi.RemoteException;

import net.hudup.core.client.PowerServer;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.xURI;
import net.hudup.server.PowerServerConfig;
import net.jsi.remote.UniverseExt;
import net.jsi.remote.UniverseRemote;

public class MultitaskServerImpl extends ExtendedServer implements MultitaskServer {


	private static final long serialVersionUID = 1L;

	
	protected UniverseRemote investUniverse = null;
	
	
	public MultitaskServerImpl(PowerServerConfig config) {
		super(config);
		
		try {
			investUniverse = new UniverseExt();
			investUniverse.export(config.getServerPort());
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
	}


	@Override
	protected void shutdown() {
		super.shutdown();
		
		try {
			investUniverse.unexport();
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		
	}


	@Override
	public UniverseRemote getInvestor() throws RemoteException {
		return investUniverse;
	}

	
	/**
	 * Static method to create multitask server.
	 * @return multitask server.
	 */
	public static MultitaskServerImpl create() {
		return (MultitaskServerImpl) create(xURI.create(PowerServerConfig.serverConfig), new Creator() {
			@Override
			public PowerServer create(PowerServerConfig config) {
				return new MultitaskServerImpl(config);
			}
		});
	}
	
	
	/**
	 * Static method to create multitask server with specified configuration URI.
	 * @param srvConfigUri specified configuration URI.
	 * @param creator creator to create server.
	 * @return multitask server.
	 */
	public static PowerServer create(xURI srvConfigUri, Creator creator) {
		return ExtendedServer.create(srvConfigUri, creator);
	}


}
