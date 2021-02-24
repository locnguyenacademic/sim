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
	 * Getting previous neurons.
	 * @param prevLayer previous layer.
	 * @return previous neurons.
	 */
	WeightedNeuron[] getPrevNeurons(Layer prevLayer);

		
	/**
	 * Getting implicit previous neurons.
	 * @return implicit previous neurons.
	 */
	WeightedNeuron[] getPrevNeuronsImplicit();

	
	/**
	 * Getting next neurons.
	 * @return next neurons.
	 */
	WeightedNeuron[] getNextNeurons();

		
	/**
	 * Getting next neurons.
	 * @param nextLayer next layer.
	 * @return next neurons.
	 */
	WeightedNeuron[] getNextNeurons(Layer nextLayer);

	
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
	 * Getting input rib neurons.
	 * @return input rib neurons.
	 */
	WeightedNeuron[] getRibinNeurons();

	
	/**
	 * Adding input rib neuron along with weight.
	 * @param ribinNeuron input rib neuron.
	 * @param weight input rib weight.
	 * @return true if adding is successful.
	 */
	boolean setRibinNeuron(Neuron ribinNeuron, Weight weight);
	
	
	/**
	 * Removing input rib neuron.
	 * @param ribinNeuron input rib neuron.
	 * @return true if removing is successful.
	 */
	boolean removeRibinNeuron(Neuron ribinNeuron);

	
	/**
	 * Clearing input rib neurons.
	 */
	void clearRibinNeurons();
	
	
	/**
	 * Finding input rib neuron.
	 * @param ribinNeuron specified input rib neuron.
	 * @return input rib neuron.
	 */
	WeightedNeuron findRibinNeuron(Neuron ribinNeuron);

	
	/**
	 * Finding input rib neuron by specified identifier.
	 * @param ribinNeuronId specified input rib neuron identifier.
	 * @return input rib neuron.
	 */
	WeightedNeuron findRibinNeuron(int ribinNeuronId);

	
	/**
	 * Getting output rib neurons.
	 * @return output rib neurons.
	 */
	WeightedNeuron[] getRiboutNeurons();

	
	/**
	 * Adding output rib neuron along with weight.
	 * @param riboutNeuron output rib neuron.
	 * @param weight output rib weight.
	 * @return true if adding is successful.
	 */
	boolean setRiboutNeuron(Neuron riboutNeuron, Weight weight);
	
	
	/**
	 * Removing output rib neuron.
	 * @param riboutNeuron output rib neuron.
	 * @return true if removing is successful.
	 */
	boolean removeRiboutNeuron(Neuron riboutNeuron);

	
	/**
	 * Clearing output rib neurons.
	 */
	void clearRiboutNeurons();
	
	
	/**
	 * Finding output rib neuron.
	 * @param riboutNeuron specified output rib neuron.
	 * @return output rib neuron.
	 */
	WeightedNeuron findRiboutNeuron(Neuron riboutNeuron);

	
	/**
	 * Finding output rib neuron by specified identifier.
	 * @param riboutNeuronId specified output rib neuron identifier.
	 * @return output rib neuron.
	 */
	WeightedNeuron findRiboutNeuron(int riboutNeuronId);

	
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
