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
 * This interface represents neuron.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface Neuron extends Serializable, Cloneable {

	
	/**
	 * Getting identifier of neuron.
	 * @return identifier of neuron.
	 */
	int id();
	
	
	/**
	 * Getting input value.
	 * @return input value.
	 */
	double getInput();
	
	
	/**
	 * Setting input value.
	 * @param value input value.
	 */
	void setInput(double value);
	
	
	/**
	 * Getting bias.
	 * @return bias.
	 */
	double getBias();
	
	
	/**
	 * Setting bias.
	 * @param bias specified bias.
	 */
	void setBias(double bias);

	
	/**
	 * Getting output value.
	 * @return output value.
	 */
	double getOutput();
	
	
	/**
	 * Setting output value.
	 * @param value output value.
	 */
	void setOutput(double value);
	
	
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
	 * Getting previous neurons.
	 * @return previous neurons.
	 */
	WeightedNeuron[] getPrevNeurons();

		
	/**
	 * Getting next neurons.
	 * @return next neurons.
	 */
	WeightedNeuron[] getNextNeurons();

		
	/**
	 * Adding next neuron along with weight.
	 * @param neuron next neuron.
	 * @param weight next weight.
	 * @return true if adding is successful.
	 */
	boolean setNextNeuron(Neuron neuron, Weight weight);
	
	
	/**
	 * Removing next neuron.
	 * @param neuron next neuron.
	 * @return true if removing is successful.
	 */
	boolean removeNextNeuron(Neuron neuron);

	
	/**
	 * Clearing next neurons.
	 */
	void clearNextNeurons();
	
	
	/**
	 * Finding next neuron.
	 * @param neuron specified next neuron.
	 * @return next neuron.
	 */
	WeightedNeuron findNextNeuron(Neuron neuron);
	
	
	/**
	 * Finding next neuron by specified identifier.
	 * @param neuronId specified next neuron identifier.
	 * @return next neuron.
	 */
	WeightedNeuron findNextNeuron(int neuronId);

	
	/**
	 * Getting latent neurons.
	 * @return latent neurons.
	 */
	WeightedNeuron[] getLatentNeurons();

	
	/**
	 * Adding latent neuron along with weight.
	 * @param neuron latent neuron.
	 * @param weight latent weight.
	 * @return true if adding is successful.
	 */
	boolean setLatentNeuron(Neuron neuron, Weight weight);
	
	
	/**
	 * Removing latent neuron.
	 * @param neuron latent neuron.
	 * @return true if removing is successful.
	 */
	boolean removeLatentNeuron(Neuron neuron);

	
	/**
	 * Clearing latent neurons.
	 */
	void clearLatentNeurons();
	
	
	/**
	 * Finding latent neuron.
	 * @param neuron specified latent neuron.
	 * @return latent neuron.
	 */
	WeightedNeuron findLatentNeuron(Neuron neuron);

	
	/**
	 * Finding latent neuron by specified identifier.
	 * @param neuronId specified latent neuron identifier.
	 * @return latent neuron.
	 */
	WeightedNeuron findLatentNeuron(int neuronId);

	
	/**
	 * Resetting latent neurons.
	 */
	void resetLatentNeurons();
	
	
	/**
	 * Getting previous sibling neuron.
	 * @return previous sibling neuron.
	 */
	Neuron getPrevSibling();
	
	
	/**
	 * Getting next sibling neuron.
	 * @return next sibling neuron.
	 */
	Neuron getNextSibling();
	
	
	/**
	 * Getting main layer.
	 * @return main layer.
	 */
	Layer getLayer();
	
	
	/**
	 * Evaluating neuron output.
	 * @return neuron output.
	 */
	double eval();
	
	
}
