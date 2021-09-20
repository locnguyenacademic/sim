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
import net.jsi.ui.InvestorExt;

public class Investor extends InvestorExt {

	
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
				JOptionPane.showMessageDialog(null, "Imposible to connect server.\nSo, running local investor.", "Local investtor", JOptionPane.WARNING_MESSAGE);
				net.jsi.ui.Investor.main(args);
				return;
			}
			
			ExtraService extraService = ((PowerServer)server).getExtraService();
			if (extraService == null || !(extraService instanceof ExtraMultitaskService)) {
				JOptionPane.showMessageDialog(null, "Imposible to connect server.\nSo, running local investor.", "Local investtor", JOptionPane.WARNING_MESSAGE);
				net.jsi.ui.Investor.main(args);
				return;
			}
	
			ExtraMultitaskService mserver = (ExtraMultitaskService)extraService;
			UniverseRemote remoteUniverse = null;
			UniverseImpl universe = new UniverseImpl();
			try {
				remoteUniverse = mserver.getInvestor();
				universe.sync(remoteUniverse, false);
			}
			catch (Exception e) {
				e.printStackTrace();
				net.jsi.ui.Investor.main(args);
				return;
			}
			
			new Investor(universe, remoteUniverse).setVisible(true);
		}
		catch (Exception e) {
			net.jsi.ui.Investor.main(args);
		}
		
	}

	
}
