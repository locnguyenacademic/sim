/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup;

import net.hudup.core.Firer;
import net.hudup.core.alg.AlgRemote;
import net.hudup.core.alg.AlgRemoteWrapper;

/**
 * This is advanced plug-in manager which derives from {@link Firer}.
 * 
 * @author Loc Nguyen
 * @version 2.0
 *
 */
public class SimFirer extends Firer {

	
	@Override
	public void fireSimply() {
		super.fireSimply();
	}

	
	@Override
	public AlgRemoteWrapper wrap(AlgRemote remoteAlg, boolean exclusive) {
//		if (remoteAlg instanceof RMRemote)
//			return new RMRemoteWrapper((RMRemote)remoteAlg, exclusive);
//		else if (remoteAlg instanceof EMRemote)
//			return new EMRemoteWrapper((EMRemote)remoteAlg, exclusive);
//		else if (remoteAlg instanceof PSORemote)
//			return new PSORemoteWrapper((PSORemote)remoteAlg, exclusive);
//		else
			return super.wrap(remoteAlg, exclusive);
	}

	
}
