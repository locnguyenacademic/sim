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
	 * This class represents a pair of neuron and associated weight.
	 * @author Loc Nguyen
	 * @version 1.0
	 */
	protected class WeightNeuron {
		
		/**
		 * Neuron.
		 */
		public Neuron neuron = null;
		
		/**
		 * Associated weight.
		 */
		public double weight = 0;
		
		/**
		 * Constructor with specified neuron and associated weight.
		 * @param neuron specified neuron.
		 * @param weight associated weight.
		 */
		public WeightNeuron(Neuron neuron, double weight) {
			this.neuron = neuron;
			this.weight = weight;
		}
		
	}
	
	
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
	 * Next neurons
	 */
	protected List<WeightNeuron> nextNeurons = Util.newList();
	
	
	/**
	 * Previous neurons
	 */
	protected List<WeightNeuron> prevNeurons = Util.newList();
	
	
	/**
	 * Next sibling weight.
	 */
	protected double nextSiblingWeight = 0;
	
	
	/**
	 * Next sibling weight.
	 */
	protected double prevSiblingWeight = 0;

	
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
	public int getNextNeuronCount() {
		return nextNeurons.size();
	}

	
	@Override
	public Neuron getNextNeuron(int index) {
		return nextNeurons.get(index).neuron;
	}

	
	@Override
	public double getNextWeight(int index) {
		return nextNeurons.get(index).weight;
	}

	
	@Override
	public boolean addNextNeuron(Neuron neuron, double weight) {
		Layer nextLayer = layer != null ? layer.getNextLayer() : null;
		if (nextLayer == null || neuron == null || weight == Double.NaN)
			return false;
		if (!nextLayer.contains(neuron)) return false;
		
		int index = nextIndexOf(neuron);
		WeightNeuron wn = null;
		boolean result = true;
		if (index < 0) {
			wn = new WeightNeuron(neuron, weight);
			result = nextNeurons.add(wn);
		}
		else {
			wn = nextNeurons.get(index);
			wn.weight = weight;
			result = true;
		}
		
		return result && neuron.addPrevNeuron(this, weight);
	}

	
	@Override
	public boolean removeNextNeuron(Neuron neuron) {
		if (neuron == null) return false;
		
		boolean result = false;
		for (int i = 0; i < nextNeurons.size(); i++) {
			if (nextNeurons.get(i) == neuron) {
				nextNeurons.remove(i);
				result = true;
				break;
			}
		}
		if (!result) return false;
		
		Layer nextLayer = layer != null ? layer.getNextLayer() : null;
		if (nextLayer != null && nextLayer.contains(neuron))
			return neuron.removePrevNeuron(this);
		else
			return true;
	}

	
	@Override
	public void clearNextNeurons() {
		List<WeightNeuron> wns = Util.newList(this.nextNeurons.size());
		wns.addAll(this.nextNeurons);
		
		for (WeightNeuron wn : wns) {
			removeNextNeuron(wn.neuron);
		}
		
		this.nextNeurons.clear();
	}


	@Override
	public boolean containsNextNeuron(Neuron neuron) {
		return nextIndexOf(neuron) >= 0;
	}

	
	@Override
	public int nextIndexOf(Neuron neuron) {
		for (int i = 0; i < nextNeurons.size(); i++) {
			if (nextNeurons.get(i) == neuron) return i;
		}
		
		return -1;
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
	public double getPrevWeight(int index) {
		return prevNeurons.get(index).weight;
	}

	
	@Override
	public boolean addPrevNeuron(Neuron neuron, double weight) {
		Layer prevLayer = layer != null ? layer.getPrevLayer() : null;
		if (prevLayer == null || neuron == null || weight == Double.NaN)
			return false;
		if (!prevLayer.contains(neuron)) return false;
		
		int index = prevIndexOf(neuron);
		WeightNeuron wn = null;
		boolean result = true;
		if (index < 0) {
			wn = new WeightNeuron(neuron, weight);
			result = prevNeurons.add(wn);
		}
		else {
			wn = prevNeurons.get(index);
			wn.weight = weight;
			result = true;
		}
		
		return result && neuron.addNextNeuron(this, weight);
	}

	
	@Override
	public boolean removePrevNeuron(Neuron neuron) {
		if (neuron == null) return false;
		
		boolean result = false;
		for (int i = 0; i < prevNeurons.size(); i++) {
			if (prevNeurons.get(i) == neuron) {
				prevNeurons.remove(i);
				result = true;
				break;
			}
		}
		if (!result) return false;
		
		Layer prevLayer = layer != null ? layer.getPrevLayer() : null;
		if (prevLayer != null && prevLayer.contains(neuron))
			return neuron.removeNextNeuron(this);
		else
			return true;
	}

	
	@Override
	public void clearPrevNeurons() {
		List<WeightNeuron> wns = Util.newList(this.prevNeurons.size());
		wns.addAll(this.prevNeurons);
		
		for (WeightNeuron wn : wns) {
			removePrevNeuron(wn.neuron);
		}
		
		this.prevNeurons.clear();
	}


	@Override
	public boolean containsPrevNeuron(Neuron neuron) {
		return prevIndexOf(neuron) >= 0;
	}

	
	@Override
	public int prevIndexOf(Neuron neuron) {
		for (int i = 0; i < prevNeurons.size(); i++) {
			if (prevNeurons.get(i) == neuron) return i;
		}
		
		return -1;
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
	public Neuron getPreviousSibling() {
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
	public Layer getLayer() {
		return layer;
	}

	
}
