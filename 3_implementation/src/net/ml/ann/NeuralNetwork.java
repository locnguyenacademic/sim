/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.ml.ann;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;

/**
 * This interface represents neural network.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface NeuralNetwork extends Remote, Serializable, Cloneable {

	
	/**
	 * Evaluating entire network with specified input.
	 * @param input specified input.
	 * @return array as output of output layer.
	 * @throws RemoteException if any error raises.
	 */
	double[] eval(double[] input) throws RemoteException;
	
	
	/**
	 * Learning the neural network.
	 * @param sample sample includes input and output.
	 * @param learningRate learning rate.
	 * @return true if learning process is successful.
	 * @throws RemoteException if any error raises.
	 */
	boolean learn(Collection<double[]> sample, double learningRate) throws RemoteException;
	
	
}
