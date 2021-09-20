package net.jsi.ui;

import java.io.File;
import java.rmi.RemoteException;

import net.jsi.Universe;
import net.jsi.UniverseRemote;

public class InvestorExt extends net.jsi.ui.Investor {


	private static final long serialVersionUID = 1L;

	
	protected UniverseRemote remoteUniverse = null;
	
	
	public InvestorExt(Universe universe, UniverseRemote remoteUniverse) {
		super(universe);
		this.remoteUniverse = remoteUniverse;
	}

	
	public InvestorExt(Universe universe) {
		this(universe, null);
	}
	
	
	@Override
	protected void initialize(File workingDir) {
		super.initialize(workingDir);
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
			if (remoteUniverse != null) remoteUniverse.sync(universe, false);
		}
		catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	
}
