/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.ea.ann;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * This interface represents neural network.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface Network extends Remote, Serializable, Cloneable, AutoCloseable {

	
	/**
	 * Default value for maximum iteration of learning neural network.
	 */
	final static int LEARN_MAX_ITERATION_DEFAULT = 1000;

	
	/**
	 * Default value for terminated threshold of learning neural network.
	 */
	final static double LEARN_TERMINATED_THRESHOLD_DEFAULT = 0.001;

	
	/**
	 * Default value for learning rate.
	 */
	final static double LEARN_RATE_DEFAULT = 1.0;

	
	/**
	 * Evaluating entire network.
	 * @param inputRecord input record for evaluating.
	 * @param refresh refresh mode.
	 * @return array as output of output layer.
	 * @throws RemoteException if any error raises.
	 */
	double[] eval(Record inputRecord, boolean refresh) throws RemoteException;
	
	
	/**
	 * Learning the neural network.
	 * @param sample sample for learning.
	 * @return learned error.
	 * @throws RemoteException if any error raises.
	 */
	double[] learn(Iterable<Record> sample) throws RemoteException;
	
	
	/**
	 * Adding listener.
	 * @param listener specified listener.
	 * @throws RemoteException if any error raises.
	 */
	void addListener(NetworkListener listener) throws RemoteException;

	
	/**
	 * Removing listener.
	 * @param listener specified listener.
	 * @throws RemoteException if any error raises.
	 */
    void removeListener(NetworkListener listener) throws RemoteException;


	/**
	 * Getting configuration of this network.
	 * @return configuration of this network.
	 * @throws RemoteException if any error raises.
	 */
    NetworkConfig getConfig() throws RemoteException;

	
	/**
	 * Setting configuration of this network.
	 * @param config specified configuration.
	 * @throws RemoteException if any error raises.
	 */
	void setConfig(NetworkConfig config) throws RemoteException;
	
	
	/**
	 * Pause doing.
	 * @return true if pausing is successful.
	 * @throws RemoteException if any error raises.
	 */
	boolean doPause() throws RemoteException;


	/**
	 * Resume doing.
	 * @return true if resuming is successful.
	 * @throws RemoteException if any error raises.
	 */
	boolean doResume() throws RemoteException;


	/**
	 * Stop doing.
	 * @return true if stopping is successful.
	 * @throws RemoteException if any error raises.
	 */
	boolean doStop() throws RemoteException;

	/**
	 * Checking whether in doing mode.
	 * @return whether in doing mode.
	 * @throws RemoteException if any error raises.
	 */
	boolean isDoStarted() throws RemoteException;


	/**
	 * Checking whether in paused mode.
	 * @return whether in paused mode.
	 * @throws RemoteException if any error raises.
	 */
	boolean isDoPaused() throws RemoteException;


	/**
	 * Checking whether in running mode.
	 * @return whether in running mode.
	 * @throws RemoteException if any error raises.
	 */
	boolean isDoRunning() throws RemoteException;

	
	/**
     * Exporting this network.
     * @param serverPort server port. Using port 0 if not concerning registry or naming.
     * @return stub as remote object. Return null if exporting fails.
     * @throws RemoteException if any error raises.
     */
    Remote export(int serverPort) throws RemoteException;
    
    
    /**
     * Unexporting this network.
     * @throws RemoteException if any error raises.
     */
    void unexport() throws RemoteException;

    
	@Override
    void close() throws Exception;


}
