/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.ml.ann;

import java.io.Serializable;

/**
 * This interface represents layer in neural network.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface Layer extends Serializable, Cloneable {

	
	/**
	 * Getting identifier reference.
	 * @return identifier reference.
	 */
	Id getIdRef();
	
	/**
	 * Create neuron.
	 * @return created neuron.
	 */
	Neuron newNeuron();

	
	/**
	 * Getting layer size.
	 * @return layer size.
	 */
	int size();
	
	
	/**
	 * Getting neuron at specified index.
	 * @param index specified index.
	 * @return neuron at specified index.
	 */
	Neuron get(int index);
	
	
	/**
	 * Adding neuron.
	 * @param neuron specified neuron.
	 * @return true if adding is successful.
	 */
	boolean add(Neuron neuron);

	
	/**
	 * Removing neuron at specified index.
	 * @param index specified index.
	 * @return previous neuron.
	 */
	Neuron remove(int index);
	
	
	/**
	 * Checking whether containing the specified neuron.
	 * @param neuron the specified neuron.
	 * @return whether containing the specified neuron.
	 */
	boolean contains(Neuron neuron);
	
	
	/**
	 * Finding specified neuron.
	 * @param neuron specified neuron.
	 * @return specified neuron.
	 */
	int indexOf(Neuron neuron);
	
	
	/**
	 * Getting next layer.
	 * @return next layer.
	 */
	Layer getNextLayer();
	
	
	/**
	 * Setting next layer.
	 * @param nextLayer next layer.
	 * @return the old next layer.
	 */
	Layer setNextLayer(Layer nextLayer);
	
	
	/**
	 * Assigning next layer.
	 * @param nextLayer next layer.
	 * @return the old next layer.
	 */
	Layer assignNextLayer(Layer nextLayer);

	
	/**
	 * Getting previous layer.
	 * @return previous layer.
	 */
	Layer getPrevLayer();
	
	
	/**
	 * Setting previous layer.
	 * @param prevLayer previous layer.
	 * @return the old previous layer.
	 */
	Layer setPrevLayer(Layer prevLayer);

	
	/**
	 * Assigning previous layer.
	 * @param prevLayer previous layer.
	 * @return the old previous layer.
	 */
	Layer assignPrevLayer(Layer prevLayer);

	
	/**
	 * Getting reference to activation function.
	 * @return reference to activation function.
	 */
	Function getActivateRef();
	
	
	/**
	 * Setting reference to activation function.
	 * @param activateRef reference to activation function.
	 * @return previous function reference.
	 */
	Function setActivateRef(Function activateRef);
	
	
}
