/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.ml.ann;

import java.io.Serializable;
import java.util.List;

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
	 * Getting previous neuron count.
	 * @return previous neuron count.
	 */
	int getPrevNeuronCount();

	
	/**
	 * Getting previous neuron at specified index.
	 * @param index specified index.
	 * @return previous neuron at specified index.
	 */
	Neuron getPrevNeuron(int index);
	
	
	/**
	 * Getting previous weight at specified index.
	 * @param index specified index.
	 * @return previous weight at specified index.
	 */
	Weight getPrevWeight(int index);
	
	
	/**
	 * Getting previous weighted neuron at specified index.
	 * @param index specified index.
	 * @return previous weighted neuron at specified index.
	 */
	WeightedNeuron getPrevWeightedNeuron(int index);

	
	/**
	 * Adding previous neuron along with weight.
	 * @param neuron previous neuron.
	 * @param weight previous weight.
	 * @return true if adding is successful.
	 */
	boolean setPrevNeuron(Neuron neuron, Weight weight);
	
	
	/**
	 * Removing previous neuron.
	 * @param neuron previous neuron.
	 * @return true if removing is successful.
	 */
	boolean removePrevNeuron(Neuron neuron);

	
	/**
	 * Clearing previous neurons.
	 */
	void clearPrevNeurons();
	
	
	/**
	 * Finding the specified previous neuron.
	 * @param neuron specified previous neuron.
	 * @return index of specified previous neuron.
	 */
	int prevIndexOf(Neuron neuron);

		
	/**
	 * Finding previous neuron by specified identifier.
	 * @param neuronId specified previous neuron.
	 * @return index of found previous neuron.
	 */
	int prevIndexOf(int neuronId);

	
	/**
	 * Getting next neuron count.
	 * @return next neuron count.
	 */
	int getNextNeuronCount();

	
	/**
	 * Getting next neuron at specified index.
	 * @param index specified index.
	 * @return next neuron at specified index.
	 */
	Neuron getNextNeuron(int index);
	
	
	/**
	 * Getting next weight at specified index.
	 * @param index specified index.
	 * @return next weight at specified index.
	 */
	Weight getNextWeight(int index);
	
	
	/**
	 * Getting next weighted neuron at specified index.
	 * @param index specified index.
	 * @return next weighted neuron at specified index.
	 */
	WeightedNeuron getNextWeightedNeuron(int index);
	
	
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
	 * Finding the specified next neuron.
	 * @param neuron specified next neuron.
	 * @return index of specified next neuron.
	 */
	int nextIndexOf(Neuron neuron);

	
	/**
	 * Finding next neuron by specified identifier.
	 * @param neuronId specified next neuron.
	 * @return index of found next neuron.
	 */
	int nextIndexOf(int neuronId);
	
	
	/**
	 * Getting previous sibling neuron.
	 * @return previous sibling neuron.
	 */
	Neuron getPrevSibling();
	
	
	/**
	 * Getting previous sibling weight.
	 * @return previous sibling weight.
	 */
	double getPrevSiblingWeight();
	
	
	/**
	 * Setting previous sibling weight.
	 * @param weight previous sibling weight.
	 * @return true if setting is successful.
	 */
	void setPrevSiblingWeight(double weight);
	
	
	/**
	 * Getting next sibling neuron.
	 * @return next sibling neuron.
	 */
	Neuron getNextSibling();
	
	
	/**
	 * Getting next sibling weight.
	 * @return next sibling weight.
	 */
	double getNextSiblingWeight();
	
	
	/**
	 * Setting next sibling weight.
	 * @param weight next sibling weight.
	 * @return true if setting is successful.
	 */
	void setNextSiblingWeight(double weight);
	
	
	/**
	 * Getting from neurons.
	 * @return from neurons.
	 */
	List<WeightedNeuron> getFromPrevNeurons();

		
	/**
	 * Getting to neurons.
	 * @return to neurons.
	 */
	List<WeightedNeuron> getFromNextNeurons();

		
	/**
	 * Getting current layer.
	 * @return current layer.
	 */
	Layer getLayer();
	
	
}
