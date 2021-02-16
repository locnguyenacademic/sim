/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.ml.hmm;

import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * This abstract is the partial wrapper of standard hidden Markov model (HMM) specified by the interface {@link HMM}.
 * In other words, it is an partial implementation of the interface {@link HMM}.
 * The core HMM is defined by its sub-class.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public abstract class HMMWrapper implements HMM {
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Maximum iteration of learning hidden Markov model (HMM).
	 */
	public final static String LEARN_MAX_ITERATION_FIELD = "learn_max_iteration";
	
	
	/**
	 * Terminated threshold of learning hidden Markov model (HMM).
	 */
	public final static String LEARN_TERMINATED_THRESHOLD_FIELD = "learn_terminated_threshold";

	
	/**
	 * Terminated ratio mode of learning hidden Markov model (HMM).
	 */
	public final static String LEARN_TERMINATED_RATIO_MODE_FIELD = "learn_terminated_ratio_mode";

	
	/**
	 * Internal (core) hidden Markov model (HMM).
	 */
	protected Object hmm = null;
	
	
	/**
	 * Configuration.
	 */
	protected HMMConfig config = new HMMConfig();
	
	
	/**
	 * Flag to indicate whether this hidden Markov model was exported.
	 */
	protected boolean exported = false;
	
	
	/**
	 * Constructor with specified hidden Markov model (HMM).
	 * @param hmm specified hidden Markov model (HMM).
	 */
	protected HMMWrapper(Object hmm) {
		this.hmm = hmm;
		this.config.put(LEARN_MAX_ITERATION_FIELD, LEARN_MAX_ITERATION_DEFAULT);
		this.config.put(LEARN_TERMINATED_THRESHOLD_FIELD, LEARN_TERMINATED_THRESHOLD_DEFAULT);
		this.config.put(LEARN_TERMINATED_RATIO_MODE_FIELD, LEARN_TERMINATED_RATIO_MODE_DEFAULT);
	}


	@Override
	public HMMConfig getConfig() throws RemoteException {
		return config;
	}


	@Override
	public void setConfig(HMMConfig config) throws RemoteException {
		if (config != null) this.config.putAll(config);
	}


	@Override
	public synchronized Remote export(int serverPort) throws RemoteException {
		if (exported) return null;
		
		Remote stub = null;
		try {
			stub = UnicastRemoteObject.exportObject(this, serverPort);
		}
		catch (Exception e) {
			try {
				if (stub != null) UnicastRemoteObject.unexportObject(this, true);
			}
			catch (Exception e2) {}
			stub = null;
		}
	
		exported = stub != null;
		return stub;
	}


	@Override
	public synchronized void unexport() throws RemoteException {
		if (!exported) return;

		try {
        	UnicastRemoteObject.unexportObject(this, true);
			exported = false;
		}
		catch (NoSuchObjectException e) {
			exported = false;
			Util.trace(e);
		}
		catch (Throwable e) {
			Util.trace(e);
		}
	}

	
	@Override
	public void close() throws Exception {
		try {
			if ((hmm != null) && (hmm instanceof AutoCloseable))
				((AutoCloseable)hmm).close();
			hmm = null;
		}
		catch (Throwable e) {
			Util.trace(e);
		}
		
		try {
			unexport();
		}
		catch (Throwable e) {
			Util.trace(e);
		}
	}


	@Override
	protected void finalize() throws Throwable {
		try {
			close();
		} catch (Throwable e) {}
		
//		super.finalize();
	}


	@Override
	public String toString() {
		return hmm != null ? hmm.toString() : super.toString();
	}


}
