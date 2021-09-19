package net.jsi.remote.ui;

import java.rmi.RemoteException;

import net.jsi.Universe;
import net.jsi.remote.UniverseRemote;
import net.jsi.ui.Investor;

public class InvestorExt extends Investor {


	private static final long serialVersionUID = 1L;

	
	protected UniverseRemote remoteUniverse = null;
	
	
	public InvestorExt(Universe universe, UniverseRemote remoteUniverse) {
		super(universe);
	}

	
	@Override
	public void dispose() {
		super.dispose();
		onSync();
	}


	@Override
	protected void onSaveAs() {
		super.onSaveAs();
		onSync();
	}


	@Override
	protected void onSave() {
		super.onSave();
		onSync();
	}


	private void onSync() {
		try {
			this.remoteUniverse.sync(universe);
		}
		catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	
}
