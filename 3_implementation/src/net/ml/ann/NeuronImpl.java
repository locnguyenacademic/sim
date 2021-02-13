/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.ml.ann;

import java.util.List;

import net.hudup.core.Util;

/**
 * This class is default implementation of neuron.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class NeuronImpl implements Neuron {


	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Identifier.
	 */
	protected int id = -1;
	
	
	/**
	 * This layer.
	 */
	protected Layer layer = null;
	
	
	/**
	 * Input value.
	 */
	protected double input = 0;
	
	
	/**
	 * Output value.
	 */
	protected double output = 0;
			
	
	/**
	 * Activation function reference.
	 */
	protected Function activateRef = null;
	
	
	/**
	 * Previous neurons
	 */
	protected List<WeightedNeuron> prevNeurons = Util.newList();
	
	
	/**
	 * Next neurons
	 */
	protected List<WeightedNeuron> nextNeurons = Util.newList();
	
	
	/**
	 * Previous sibling weight.
	 */
	protected double prevSiblingWeight = 0;

	
	/**
	 * Next sibling weight.
	 */
	protected double nextSiblingWeight = 0;
	
	
	/**
	 * Default constructor.
	 * @param layer this layer.
	 */
	public NeuronImpl(Layer layer) {
		this.layer = layer;
		this.id = layer.getIdRef().get();
		this.activateRef = layer.getActivateRef();
	}

	
	@Override
	public int id() {
		return id;
	}

	
	@Override
	public double getInput() {
		return input;
	}

	
	@Override
	public void setInput(double value) {
		this.input = value;
	}

	@Override
	public double getOutput() {
		return output;
	}

	
	@Override
	public void setOutput(double value) {
		this.output = value;
	}

	
	@Override
	public Function getActivateRef() {
		return activateRef;
	}

	
	@Override
	public Function setActivateRef(Function activateRef) {
		return this.activateRef = activateRef;
	}

	
	@Override
	public int getPrevNeuronCount() {
		return prevNeurons.size();
	}

	
	@Override
	public Neuron getPrevNeuron(int index) {
		return prevNeurons.get(index).neuron;
	}

	
	@Override
	public Weight getPrevWeight(int index) {
		return prevNeurons.get(index).weight;
	}

	
	@Override
	public WeightedNeuron getPrevWeightedNeuron(int index) {
		return prevNeurons.get(index);
	}


	@Override
	public boolean setPrevNeuron(Neuron neuron, Weight weight) {
		Layer prevLayer = layer != null ? layer.getPrevLayer() : null;
		if (prevLayer == null || neuron == null || weight == null)
			return false;
		if (prevLayer.indexOf(neuron) < 0) return false;
		
		int index = prevIndexOf(neuron);
		WeightedNeuron wn = null;
		if (index < 0) {
			wn = new WeightedNeuron(neuron, weight);
			prevNeurons.add(wn);
		}
		else {
			wn = prevNeurons.get(index);
			wn.weight = weight;
		}
		
		return true;
	}

	
	@Override
	public boolean removePrevNeuron(Neuron neuron) {
		if (neuron == null || !prevNeurons.contains(neuron)) return false;
		
		prevNeurons.remove(neuron);
		
		return true;
	}

	
	@Override
	public void clearPrevNeurons() {
		List<WeightedNeuron> wns = Util.newList(this.prevNeurons.size());
		wns.addAll(this.prevNeurons);
		
		for (WeightedNeuron wn : wns) {
			removePrevNeuron(wn.neuron);
		}
		
		this.prevNeurons.clear();
	}


	@Override
	public int prevIndexOf(Neuron neuron) {
		for (int i = 0; i < prevNeurons.size(); i++) {
			if (prevNeurons.get(i) == neuron) return i;
		}
		
		return -1;
	}
	
	
	@Override
	public int prevIndexOf(int neuronId) {
		for (int i = 0; i < prevNeurons.size(); i++) {
			Neuron neuron = prevNeurons.get(i).neuron;
			if (neuron != null && neuron.id() == neuronId) return i;
		}
		
		return -1;
	}


	@Override
	public int getNextNeuronCount() {
		return nextNeurons.size();
	}

	
	@Override
	public Neuron getNextNeuron(int index) {
		return nextNeurons.get(index).neuron;
	}

	
	@Override
	public Weight getNextWeight(int index) {
		return nextNeurons.get(index).weight;
	}

	
	@Override
	public WeightedNeuron getNextWeightedNeuron(int index) {
		return nextNeurons.get(index);
	}


	@Override
	public boolean setNextNeuron(Neuron neuron, Weight weight) {
		Layer nextLayer = layer != null ? layer.getNextLayer() : null;
		if (nextLayer == null || neuron == null || weight == null)
			return false;
		if (nextLayer.indexOf(neuron) < 0) return false;
		
		int index = nextIndexOf(neuron);
		WeightedNeuron wn = null;
		if (index < 0) {
			wn = new WeightedNeuron(neuron, weight);
			nextNeurons.add(wn);
		}
		else {
			wn = nextNeurons.get(index);
			wn.weight = weight;
		}
		
		return true;
	}

	
	@Override
	public boolean removeNextNeuron(Neuron neuron) {
		if (neuron == null || !nextNeurons.contains(neuron)) return false;
		
		nextNeurons.remove(neuron);
		
		return true;
	}

	
	@Override
	public void clearNextNeurons() {
		List<WeightedNeuron> wns = Util.newList(this.nextNeurons.size());
		wns.addAll(this.nextNeurons);
		
		for (WeightedNeuron wn : wns) {
			removeNextNeuron(wn.neuron);
		}
		
		this.nextNeurons.clear();
	}


	@Override
	public int nextIndexOf(Neuron neuron) {
		for (int i = 0; i < nextNeurons.size(); i++) {
			if (nextNeurons.get(i) == neuron) return i;
		}
		
		return -1;
	}
	
	
	@Override
	public int nextIndexOf(int neuronId) {
		for (int i = 0; i < nextNeurons.size(); i++) {
			Neuron neuron = nextNeurons.get(i).neuron;
			if (neuron != null && neuron.id() == neuronId) return i;
		}
		
		return -1;
	}


	@Override
	public Neuron getPrevSibling() {
		if (layer == null) return null;
		
		int index = layer.indexOf(this);
		if (index <= 0)
			return null;
		else
			return layer.get(index - 1);
	}

	
	@Override
	public double getPrevSiblingWeight() {
		return prevSiblingWeight;
	}


	@Override
	public void setPrevSiblingWeight(double weight) {
		this.prevSiblingWeight = weight;
	}


	@Override
	public Neuron getNextSibling() {
		if (layer == null) return null;
		
		int index = layer.indexOf(this);
		if (index < 0 || index >= layer.size() - 1)
			return null;
		else
			return layer.get(index + 1);
	}

	
	@Override
	public double getNextSiblingWeight() {
		return nextSiblingWeight;
	}


	@Override
	public void setNextSiblingWeight(double weight) {
		this.nextSiblingWeight = weight;
	}


	@Override
	public List<WeightedNeuron> getFromPrevNeurons() {
		List<WeightedNeuron> fromNeurons = Util.newList();
		if (layer == null) return fromNeurons;
		
		Layer prevLayer = layer.getPrevLayer();
		if (prevLayer == null) return fromNeurons;
		
		for (int i = 0; i < prevLayer.size(); i++) {
			Neuron prevNeuron = prevLayer.get(i);
			int index = prevNeuron.nextIndexOf(this);
			if (index >= 0) {
				WeightedNeuron wn = new WeightedNeuron(prevNeuron, prevNeuron.getNextWeight(index));
				fromNeurons.add(wn);
			}
		}
		
		return fromNeurons;
	}
	

	@Override
	public List<WeightedNeuron> getFromNextNeurons() {
		List<WeightedNeuron> toNeurons = Util.newList();
		if (layer == null) return toNeurons;
		
		Layer nextLayer = layer.getNextLayer();
		if (nextLayer == null) return toNeurons;
		
		for (int i = 0; i < nextLayer.size(); i++) {
			Neuron nextNeuron = nextLayer.get(i);
			int index = nextNeuron.prevIndexOf(this);
			if (index >= 0) {
				WeightedNeuron wn = new WeightedNeuron(nextNeuron, nextNeuron.getPrevWeight(index));
				toNeurons.add(wn);
			}
		}
		
		return toNeurons;
	}
	
	
	@Override
	public Layer getLayer() {
		return layer;
	}

	
}
