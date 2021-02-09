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
	 * Create and add neuron.
	 * @return created and added neuron.
	 */
	Neuron addNew();

	
	/**
	 * Adding neuron.
	 * @param neuron specified neuron.
	 * @return true if adding is successful.
	 */
	boolean add(Neuron neuron);

	
	/**
	 * Setting neuron at specified index.
	 * @param index specified index.
	 * @param neuron specified neuron
	 * @return previous neuron.
	 */
	Neuron set(int index, Neuron neuron);
	
	
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
	 * Getting next layer.
	 * @return next layer.
	 */
	Layer getNextLayer();
	
	
	/**
	 * Getting previous layer.
	 * @return previous layer.
	 */
	Layer getPrevLayer();
	
	
	/**
	 * Getting reference to activation function.
	 * @return reference to activation function.
	 */
	Function getActivateRef();
	
	
	/**
	 * Setting reference to activation function.
	 * @param funcRef reference to activation function.
	 * @return previous function reference.
	 */
	Function setActivateRef(Function funcRef);
	
	
}
