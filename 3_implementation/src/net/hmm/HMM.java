/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hmm;

import java.io.Serializable;
import java.rmi.Remote;
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
	 * Getting the number of states. Each state is coded by an integer.
	 * @return the number of states.
	 * @throws Exception if any error raises.
	 */
	int n() throws Exception;
	
	
	/**
	 * Getting the transition probability from state i to state j. Each state is coded by an integer.
	 * @param stateI state i.
	 * @param stateJ state j.
	 * @return transition probability from state i to state j.
	 * @throws Exception if any error raises.
	 */
	double a(int stateI, int stateJ) throws Exception;
	
	
	/**
	 * Getting the initial probability of state i. Each state is coded by an integer.
	 * @param stateI state i.
	 * @return initial probability of state i.
	 * @throws Exception if any error raises.
	 */
	double pi(int stateI) throws Exception;
	
	
	/**
	 * Getting the probability of specified observation at given state i. Each state is coded by an integer.
	 * @param stateI given state i.
	 * @param obs specified observation.
	 * @return probability of specified observation at given state i.
	 * @throws Exception if any error raises.
	 */
	double b(int stateI, Obs obs) throws Exception;
	
	
	/**
	 * Evaluating the probability of specified sequence of observations.
	 * @param obsSeq specified sequence of observations.
	 * @return probability of specified sequence of observations.
	 * @throws Exception if any error raises.
	 */
	double evaluate(List<Obs> obsSeq) throws Exception;
	
	
	/**
	 * Uncovering the most appropriate sequences of states of given sequence of observations. Each state is coded by an integer.
	 * @param obsSeq given sequence of observations.
	 * @return list of integers as the most appropriate sequences of states of given sequence of observations.
	 * @throws Exception if any error raises.
	 */
	List<Integer> uncover(List<Obs> obsSeq) throws Exception;
	
	
	/**
	 * Learning the hidden Markov model (HMM) from sequence of observations.
	 * @param obsSeq sequence of observations.
	 * @throws Exception if any error raises.
	 */
	void learn(List<Obs> obsSeq) throws Exception;
	
	
}
