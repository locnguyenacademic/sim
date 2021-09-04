/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.ea.pso.adapter;

import java.rmi.RemoteException;
import java.util.List;

import net.hudup.core.alg.AllowNullTrainingSet;
import net.hudup.core.alg.ExecuteAsLearnAlgRemoteWrapper;
import net.hudup.core.logistic.BaseClass;
import net.hudup.core.logistic.Inspector;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.ui.DescriptionDlg;
import net.hudup.core.logistic.ui.UIUtil;

/**
 * The class is a wrapper of remote particle swarm optimization (PSO) algorithm.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
@BaseClass //The annotation is very important which prevent Firer to instantiate the wrapper without referred remote object. This wrapper is not normal algorithm.
public class PSORemoteWrapper extends ExecuteAsLearnAlgRemoteWrapper implements PSO, PSORemote, AllowNullTrainingSet {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Constructor with specified remote PSO algorithm.
	 * @param remotePSO remote PSO algorithm.
	 */
	public PSORemoteWrapper(PSORemote remotePSO) {
		super(remotePSO);
	}

	
	/**
	 * Constructor with specified remote PSO algorithm and exclusive mode.
	 * @param remotePSO remote PSO algorithm.
	 * @param exclusive exclusive mode.
	 */
	public PSORemoteWrapper(PSORemote remotePSO, boolean exclusive) {
		super(remotePSO, exclusive);
	}

	
	@Override
	public void setup() throws RemoteException {
		((PSORemote)this.remoteAlg).setup();
	}


	@Override
	public void setup(List<String> varNames, String funcExpr) throws RemoteException {
		((PSORemote)this.remoteAlg).setup(varNames, funcExpr);
	}


	@Override
	public Inspector getInspector() {
		String desc = "";
		try {
			desc = getDescription();
		} catch (Exception e) {LogUtil.trace(e);}
		
		return new DescriptionDlg(UIUtil.getDialogForComponent(null), "Inspector", desc);
	}


}
