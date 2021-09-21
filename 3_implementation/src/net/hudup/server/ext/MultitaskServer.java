package net.hudup.server.ext;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import net.hudup.core.client.ExtraService;
import net.hudup.core.client.PowerServer;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.xURI;
import net.hudup.server.PowerServerConfig;
import net.jsi.Universe;
import net.jsi.UniverseImpl;
import net.jsi.UniverseRemote;
import net.jsi.UniverseRemoteImpl;
import net.jsi.adapter.Investor;

/**
 * This class represents the multi-task server.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class MultitaskServer extends ExtendedServer {


	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Constructor with specified configuration.
	 * @param config specified configuration.
	 */
	public MultitaskServer(PowerServerConfig config) {
		super(config);
	}


	@Override
	protected ExtraService createExtraService() {
		try {
			return new ExtraMultitaskServiceImpl(this);
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		
		return null;
	}
	
	
	/**
	 * Getting remote universe.
	 * @return remote universe.
	 */
	protected UniverseRemote getUniverseRemote() {
		try {
			if (extraService == null || !(extraService instanceof ExtraMultitaskService))
				return null;
			else
				return ((ExtraMultitaskService)extraService).getUniverseRemote();
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
		
		return null;
	}
	
	
	/**
	 * Getting universe.
	 * @return universe.
	 */
	protected Universe getUniverse() {
		try {
			UniverseRemote remoteUniverse = getUniverseRemote();
			if (remoteUniverse == null)
				return null;
			else if (remoteUniverse instanceof UniverseRemoteImpl)
				return ((UniverseRemoteImpl)remoteUniverse).getUniverse();
			else
				return null;
		}
		catch (Exception e) {
			LogUtil.trace(e);
			return null;
		}
	}
	
	
	/**
	 * Show control panel.
	 */
	protected void showCP() {
		try {
			new ExtendedServerCP(this) {

				/**
				 * Default serial version UID.
				 */
				private static final long serialVersionUID = 1L;

				@Override
				protected void addToAppsMenu(JMenu mnApps) {
					JMenuItem mniInvestor = new JMenuItem(
						new AbstractAction("Financial investor") {
							
							/**
							 * Serial version UID for serializable class. 
							 */
							private static final long serialVersionUID = 1L;

							@Override
							public void actionPerformed(ActionEvent e) {
								try {
									UniverseRemote remoteUniverse = null;
									Universe universe = null;
									if (connectInfo.bindUri == null)
										universe = getUniverse();
									else {
										universe = new UniverseImpl();
										remoteUniverse = getUniverseRemote();
									}

									if (universe != null) new Investor(universe, remoteUniverse).setVisible(true);
								}
								catch (Exception ex) {
									LogUtil.trace(ex);
								}
							}
						});
					mniInvestor.setMnemonic('f');
					mnApps.add(mniInvestor);
				}
				
			};
		}
		catch (Throwable e) {
			//LogUtil.trace(e);
			LogUtil.error("Extended server fail to show control panel, caused by " + e.getMessage());
			
			/*
			 * It is possible that current Java environment does not support GUI.
			 * Use of GraphicsEnvironment.isHeadless() tests Java GUI.
			 * Hence, create control panel with console here or improve PowerServerCP to support console.
			 */
		}
	}

	
	/**
	 * Static method to create multitask server.
	 * @return multitask server.
	 */
	public static MultitaskServer create() {
		return (MultitaskServer) create(xURI.create(PowerServerConfig.serverConfig), new Creator() {
			@Override
			public PowerServer create(PowerServerConfig config) {
				return new MultitaskServer(config);
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
