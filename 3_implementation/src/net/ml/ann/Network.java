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
import java.util.List;

/**
 * This interface represents neural network.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface Network extends Remote, Serializable, Cloneable {

	
	/**
	 * Evaluating entire network.
	 * @param mainInput main input.
	 * @param auxInputs auxiliary inputs.
	 * @return array as output of output layer.
	 * @throws RemoteException if any error raises.
	 */
	double[] eval(double[] mainInput, List<double[]> auxInputs) throws RemoteException;
	
	
	/**
	 * Learning the neural network.
	 * @param learningRate learning rate.
	 * @param errorThreshold error threshold.
	 * @param maxIteration maximum iteration.
	 * @param mainSample main sample includes input and output.
	 * @param auxSamples auxiliary samples include inputs and outputs.
	 * @return learned biases.
	 * @throws RemoteException if any error raises.
	 */
	double[] learn(double learningRate, double errorThreshold, int maxIteration, Collection<double[]> mainSample, List<Collection<double[]>> auxSamples) throws RemoteException;
	
	
}
