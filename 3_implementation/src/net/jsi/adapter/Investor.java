package net.jsi.adapter;

import java.rmi.RemoteException;

import javax.swing.JOptionPane;

import net.hudup.core.client.Connector;
import net.hudup.core.client.Server;
import net.hudup.server.ext.MultitaskServer;
import net.jsi.remote.UniverseExt;
import net.jsi.remote.UniverseRemote;
import net.jsi.remote.ui.InvestorExt;

public class Investor {

	
	public Investor() {

	}


	public static void main(String[] args) {
		final Connector connector = Connector.connect();
		Server server = connector.getServer();
		if (server == null || !(server instanceof MultitaskServer)) {
			JOptionPane.showMessageDialog(null, "Imposible to connect server", "Imposible to connect server", JOptionPane.ERROR_MESSAGE);
			return;
		}

		MultitaskServer mserver = (MultitaskServer)server;
		UniverseRemote remoteUniverse = null;
		UniverseExt universe = new UniverseExt();
		try {
			remoteUniverse = mserver.getInvestor();
			universe.sync(remoteUniverse);
		}
		catch (RemoteException e) {
			JOptionPane.showMessageDialog(null, "Cannot retrieve remote investor", "Cannot retrieve remote investor", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			return;
		}
		
		InvestorExt investor = new InvestorExt(universe, remoteUniverse);
		
		investor.setVisible(true);

	}

	
}
