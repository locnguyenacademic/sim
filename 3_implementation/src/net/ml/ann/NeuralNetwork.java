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

import net.hudup.core.data.Fetcher;
import net.hudup.core.data.Profile;

/**
 * This interface represents neural network.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface NeuralNetwork extends Remote, Serializable, Cloneable {

	
	/**
	 * Getting memory layer.
	 * @return memory layer.
	 * @throws RemoteException if any error raises.
	 */
	Layer getMemoryLayer() throws RemoteException;
	
	
	/**
	 * Getting input layer.
	 * @return input layer.
	 * @throws RemoteException if any error raises.
	 */
	Layer getInputLayer() throws RemoteException;
	
	
	/**
	 * Getting count of hidden layers.
	 * @return count of hidden layers.
	 * @throws RemoteException if any error raises.
	 */
	int getHiddenLayerCount() throws RemoteException;
	
	
	/**
	 * Getting hidden layer at specified index.
	 * @param index specified index.
	 * @return hidden layer at specified index.
	 * @throws RemoteException if any error raises.
	 */
	Layer getHiddenLayer(int index) throws RemoteException;
	
	
	/**
	 * Getting output layer.
	 * @return output layer.
	 * @throws RemoteException if any error raises.
	 */
	Layer getOutputLayer() throws RemoteException;
	
	
	/**
	 * Learning the neural network.
	 * @param input input sample.
	 * @param output output sample.
	 * @return true if learning process is successful.
	 * @throws RemoteException if any error raises.
	 */
	boolean learn(Fetcher<Profile> input, Fetcher<Profile> output) throws RemoteException;
	
	
}
