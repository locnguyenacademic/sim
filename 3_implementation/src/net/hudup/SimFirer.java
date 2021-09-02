/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup;

import net.ea.pso.adapter.PSORemote;
import net.ea.pso.adapter.PSORemoteWrapper;
import net.hudup.core.Constants;
import net.hudup.core.Firer;
import net.hudup.core.alg.AlgRemote;
import net.hudup.core.alg.AlgRemoteWrapper;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.xURI;
import net.rem.em.EMRemote;
import net.rem.em.EMRemoteWrapper;
import net.rem.regression.RMRemote;
import net.rem.regression.RMRemoteWrapper;

/**
 * This is advanced plug-in manager which derives from {@link Firer}.
 * 
 * @author Loc Nguyen
 * @version 2.0
 *
 */
public class SimFirer extends Firer {

	
	/**
	 * Directory of Join Stock Investment (JSI) tool.
	 */
	public final static String  JSI_DIRECTORY = Constants.WORKING_DIRECTORY + "/jsi";

	
	@Override
	public void fireSimply() {
		super.fireSimply();
		
		try {
			UriAdapter adapter = new UriAdapter(Constants.WORKING_DIRECTORY);
			
			xURI working = xURI.create(JSI_DIRECTORY);
			if (!adapter.exists(working)) adapter.create(working, true);
			
			adapter.close();

		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
	}

	
	@Override
	public AlgRemoteWrapper wrap(AlgRemote remoteAlg, boolean exclusive) {
		if (remoteAlg instanceof RMRemote)
			return new RMRemoteWrapper((RMRemote)remoteAlg, exclusive);
		else if (remoteAlg instanceof EMRemote)
			return new EMRemoteWrapper((EMRemote)remoteAlg, exclusive);
		else if (remoteAlg instanceof PSORemote)
			return new PSORemoteWrapper((PSORemote)remoteAlg, exclusive);
		else
			return super.wrap(remoteAlg, exclusive);
	}

	
}
