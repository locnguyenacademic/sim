package net.jsi.adapter;

import javax.swing.JOptionPane;

import net.hudup.core.client.Connector;
import net.hudup.core.client.ExtraService;
import net.hudup.core.client.PowerServer;
import net.hudup.core.client.Server;
import net.hudup.server.ext.ExtraMultitaskService;
import net.jsi.Universe;
import net.jsi.UniverseImpl;
import net.jsi.UniverseRemote;

public class Investor extends net.jsi.ui.Investor {

	
	private static final long serialVersionUID = 1L;


	public Investor(Universe universe, UniverseRemote remoteUniverse) {
		super(universe, remoteUniverse);
	}


	public Investor(Universe universe) {
		super(universe);
	}


	public static void main(String[] args) {
		try {
			final Connector connector = Connector.connect();
			Server server = connector.getServer();
			if (server == null || !(server instanceof PowerServer)) {
				JOptionPane.showMessageDialog(null, "Imposible to connect server.\nTherefore running local investor.", "Local investtor", JOptionPane.WARNING_MESSAGE);
				new net.jsi.ui.Investor(new UniverseImpl()).setVisible(true);
				return;
			}
			
			ExtraService extraService = ((PowerServer)server).getExtraService();
			if (extraService == null || !(extraService instanceof ExtraMultitaskService)) {
				JOptionPane.showMessageDialog(null, "Imposible to connect server.\nTherefore running local investor.", "Local investtor", JOptionPane.WARNING_MESSAGE);
				new net.jsi.ui.Investor(new UniverseImpl()).setVisible(true);
				return;
			}
	
			ExtraMultitaskService mserver = (ExtraMultitaskService)extraService;
			UniverseRemote remoteUniverse = null;
			UniverseImpl universe = new UniverseImpl();
			try {
				remoteUniverse = mserver.getUniverseRemote();
				universe.sync(remoteUniverse, false);
			}
			catch (Exception e) {
				e.printStackTrace();
				new net.jsi.ui.Investor(new UniverseImpl()).setVisible(true);
				return;
			}
			
			new Investor(universe, remoteUniverse).setVisible(true);
		}
		catch (Throwable e) {
			new net.jsi.ui.Investor(new UniverseImpl()).setVisible(true);
		}
		
	}

	
}
