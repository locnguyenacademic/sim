/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.ea.ann;

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
	 * Getting identifier.
	 * @return identifier.
	 */
	int id();
	
	
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
	 * Clearing all neurons.
	 */
	void clear();
	
	
	/**
	 * Finding specified neuron.
	 * @param neuron specified neuron.
	 * @return specified neuron.
	 */
	int indexOf(Neuron neuron);
	
	
	/**
	 * Finding neuron by specified identifier.
	 * @param neuronId specified identifier.
	 * @return found neuron.
	 */
	int indexOf(int neuronId);
	
	
	/**
	 * Getting previous layer.
	 * @return previous layer.
	 */
	Layer getPrevLayer();
	
	
	/**
	 * Getting implicit previous layer.
	 * @return implicit previous layer.
	 */
	Layer getPrevLayerImplicit();

	
	/**
	 * Setting previous layer.
	 * @param prevLayer previous layer. It can be null.
	 * @return true if setting is successful.
	 */
	boolean setPrevLayer(Layer prevLayer);

	
	/**
	 * Getting next layer.
	 * @return next layer.
	 */
	Layer getNextLayer();
	
	
	/**
	 * Setting next layer.
	 * @param nextLayer next layer. It can be null.
	 * @return true if setting is successful.
	 */
	boolean setNextLayer(Layer nextLayer);
	
	
	/**
	 * Getting input rib layer.
	 * @return input rib layer.
	 */
	Layer getRibinLayer();
	
	
	/**
	 * Setting input rib layer.
	 * @param ribinLayer input rib layer. It can be null.
	 * @return true if setting is successful.
	 */
	boolean setRibinLayer(Layer ribinLayer);

	
	/**
	 * Getting output rib layer.
	 * @return output rib layer.
	 */
	Layer getRiboutLayer();
	
	
	/**
	 * Setting output rib layer.
	 * @param riboutLayer output rib layer. It can be null.
	 * @return true if setting is successful.
	 */
	boolean setRiboutLayer(Layer riboutLayer);
	
	
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
	
	
	/**
	 * Getting input values.
	 * @return input values.
	 */
	double[] getInput();
	
	
	/**
	 * Getting output values.
	 * @return output values.
	 */
	double[] getOutput();
	
	
}
