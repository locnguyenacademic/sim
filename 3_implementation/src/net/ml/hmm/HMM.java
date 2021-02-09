/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.ml.hmm;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * The main interface of hidden Markov model (HMM), which aims to separate design and implementation.
 * There are only three core public interfaces: {@link HMM}, {@link Obs}, and {@link Factory}.
 * The public class {@link FactoryImpl} that creates the default implementation of this interface can be replaced by advanced class.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface HMM extends Remote, Serializable, Cloneable, AutoCloseable {

	
	/**
	 * Default value for maximum iteration of learning hidden Markov model (HMM).
	 */
	final static int LEARN_MAX_ITERATION_DEFAULT = 1000;

	
	/**
	 * Default value for terminated threshold of learning hidden Markov model (HMM).
	 */
	final static double LEARN_TERMINATED_THRESHOLD_DEFAULT = 0.001;

	
	/**
	 * Default value for terminated ratio mode of learning hidden Markov model (HMM).
	 */
	final static boolean LEARN_TERMINATED_RATIO_MODE_DEFAULT = true;

	
	/**
	 * Getting the number of states. Each state is coded by an integer.
	 * @return the number of states.
	 * @throws RemoteException if any error raises.
	 */
	int n() throws RemoteException;
	
	
	/**
	 * Getting the transition probability from state i to state j. Each state is coded by an integer.
	 * @param stateI state i.
	 * @param stateJ state j.
	 * @return transition probability from state i to state j.
	 * @throws RemoteException if any error raises.
	 */
	double a(int stateI, int stateJ) throws RemoteException;
	
	
	/**
	 * Getting the initial probability of state i. Each state is coded by an integer.
	 * @param stateI state i.
	 * @return initial probability of state i.
	 * @throws RemoteException if any error raises.
	 */
	double pi(int stateI) throws RemoteException;
	
	
	/**
	 * Getting the probability of specified observation at given state i. Each state is coded by an integer.
	 * @param stateI given state i.
	 * @param obs specified observation.
	 * @return probability of specified observation at given state i.
	 * @throws RemoteException if any error raises.
	 */
	double b(int stateI, Obs obs) throws RemoteException;
	
	
	/**
	 * Evaluating the probability of specified sequence of observations.
	 * @param obsSeq specified sequence of observations.
	 * @return probability of specified sequence of observations.
	 * @throws RemoteException if any error raises.
	 */
	double evaluate(List<Obs> obsSeq) throws RemoteException;
	
	
	/**
	 * Uncovering the most appropriate sequences of states of given sequence of observations. Each state is coded by an integer.
	 * @param obsSeq given sequence of observations.
	 * @return list of integers as the most appropriate sequences of states of given sequence of observations.
	 * @throws RemoteException if any error raises.
	 */
	List<Integer> uncover(List<Obs> obsSeq) throws RemoteException;
	
	
	/**
	 * Learning the hidden Markov model (HMM) from sequence of observations.
	 * @param obsSeq sequence of observations.
	 * @throws RemoteException if any error raises.
	 */
	void learn(List<Obs> obsSeq) throws RemoteException;
	
	
	/**
	 * Adding listener.
	 * @param listener specified listener.
	 * @throws RemoteException if any error raises.
	 */
	void addListener(HMMListener listener) throws RemoteException;

	
	/**
	 * Removing listener.
	 * @param listener specified listener.
	 * @throws RemoteException if any error raises.
	 */
    void removeListener(HMMListener listener) throws RemoteException;


	/**
	 * Getting configuration of this model.
	 * @return configuration of this model.
	 * @throws RemoteException if any error raises.
	 */
	HMMConfig getConfig() throws RemoteException;

	
	/**
	 * Setting configuration of this model.
	 * @param config specified configuration.
	 * @throws RemoteException if any error raises.
	 */
	void setConfig(HMMConfig config) throws RemoteException;
	
	
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
     * Exporting this hidden Markov model (HMM).
     * @param serverPort server port. Using port 0 if not concerning registry or naming.
     * @return stub as remote object. Return null if exporting fails.
     * @throws RemoteException if any error raises.
     */
    Remote export(int serverPort) throws RemoteException;
    
    
    /**
     * Unexporting this hidden Markov model (HMM).
     * @throws RemoteException if any error raises.
     */
    void unexport() throws RemoteException;

    
	@Override
    void close() throws Exception;


}
