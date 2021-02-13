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
public class LayerImpl implements Layer {


	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Internal identifier.
	 */
	protected Id idRef = new Id();
	
	
	/**
	 * Activation function reference.
	 */
	protected Function activateRef = null;

	
	/**
	 * Internal neurons.
	 */
	protected List<Neuron> neurons = Util.newList();
	
	
	/**
	 * Previous layer.
	 */
	protected Layer prevLayer = null;
	
	
	/**
	 * Next layer.
	 */
	protected Layer nextLayer = null;
	
	
	/**
	 * Constructor with activation function.
	 * @param activateRef activation function.
	 */
	public LayerImpl(Function activateRef, Id idRef) {
		this.activateRef = activateRef;
		if (idRef != null) this.idRef = idRef;
	}


	@Override
	public Id getIdRef() {
		return idRef;
	}
	
	
	@Override
	public Neuron newNeuron() {
		return new NeuronImpl(this);
	}

	
	@Override
	public int size() {
		return neurons.size();
	}

	
	@Override
	public Neuron get(int index) {
		return neurons.get(index);
	}

	
	@Override
	public boolean add(Neuron neuron) {
		return neurons.add(neuron);
	}

	
	@Override
	public Neuron remove(int index) {
		Neuron neuron = neurons.get(index);
		neuron.clearNextNeurons();
		neuron.clearPrevNeurons();
		
		if (neurons.size() == 1) return neurons.remove(index);
		
		if (index == neurons.size() - 1) {
			Neuron prev = neurons.get(index - 1);
			prev.setNextSiblingWeight(0);
		}
		if (index == 0) {
			Neuron next = neurons.get(index + 1);
			next.setPrevSiblingWeight(0);
		}

		return neurons.remove(index);
	}

	
	@Override
	public void clear() {
		while (neurons.size() > 0) {
			remove(0);
		}
	}


	@Override
	public int indexOf(Neuron neuron) {
		return neurons.indexOf(neuron);
	}

	
	@Override
	public int indexOf(int neuronId) {
		for (int i = 0; i < neurons.size(); i++) {
			if (neurons.get(i).id() == neuronId) return i;
		}
		
		return -1;
	}


	@Override
	public Layer getPrevLayer() {
		return prevLayer;
	}

	
	@Override
	public Layer setPrevLayer(Layer prevLayer) {
		if (prevLayer == this.prevLayer) return this.prevLayer;

		clearPrevNeurons();
		
		Layer oldPrevLayer = this.prevLayer;
		Layer oldPrevPrevLayer = null;
		if (oldPrevLayer != null) {
			oldPrevPrevLayer = oldPrevLayer.getPrevLayer();
			oldPrevLayer.clearPrevNeurons();
			oldPrevLayer.clearNextNeurons();
		}

		this.prevLayer = prevLayer;
		if (prevLayer == null) return oldPrevLayer;

		prevLayer.clearPrevNeurons();
		prevLayer.clearNextNeurons();
		prevLayer.assignNextLayer(this);
		for (int i = 0; i < prevLayer.size(); i++) {
			Neuron neuron = prevLayer.get(i);
			for (int j = 0; j < size(); j++) {
				neuron.setNextNeuron(get(j), new Weight(0));
			}
		}
		
		if (oldPrevPrevLayer == null) return oldPrevLayer;
		oldPrevPrevLayer.clearNextNeurons();
		oldPrevPrevLayer.assignNextLayer(prevLayer);
		prevLayer.assignPrevLayer(oldPrevPrevLayer);
		for (int i = 0; i < oldPrevPrevLayer.size(); i++) {
			Neuron neuron = oldPrevPrevLayer.get(i);
			for (int j = 0; j < prevLayer.size(); j++) {
				neuron.setNextNeuron(prevLayer.get(j), new Weight(0));
			}
		}
		
		return oldPrevLayer;
	}


	@Override
	public Layer assignPrevLayer(Layer prevLayer) {
		Layer oldPrevLayer = this.prevLayer;
		this.prevLayer = prevLayer;
		return oldPrevLayer;
	}


	@Override
	public void clearPrevNeurons() {
		for (Neuron neuron : neurons) {
			neuron.clearPrevNeurons();
		}
	}


	@Override
	public Layer getNextLayer() {
		return nextLayer;
	}

	
	@Override
	public Layer setNextLayer(Layer nextLayer) {
		if (nextLayer == this.nextLayer) return this.nextLayer;

		clearNextNeurons();
		
		Layer oldNextLayer = this.nextLayer;
		Layer oldNextNextLayer = null;
		if (oldNextLayer != null) {
			oldNextNextLayer = oldNextLayer.getNextLayer();
			oldNextLayer.clearPrevNeurons();
			oldNextLayer.clearNextNeurons();
		}

		this.nextLayer = nextLayer;
		if (nextLayer == null) return oldNextLayer;

		nextLayer.clearPrevNeurons();
		nextLayer.clearNextNeurons();
		nextLayer.assignPrevLayer(this);
		for (int i = 0; i < size(); i++) {
			Neuron neuron = get(i);
			for (int j = 0; j < nextLayer.size(); j++) {
				neuron.setNextNeuron(nextLayer.get(j), new Weight(0));
			}
		}
		
		if (oldNextNextLayer == null) return oldNextLayer;
		oldNextNextLayer.clearPrevNeurons();
		oldNextNextLayer.assignPrevLayer(nextLayer);
		nextLayer.assignNextLayer(oldNextNextLayer);
		for (int i = 0; i < oldNextNextLayer.size(); i++) {
			Neuron neuron = oldNextNextLayer.get(i);
			for (int j = 0; j < nextLayer.size(); j++) {
				neuron.setNextNeuron(nextLayer.get(j), new Weight(0));
			}
		}
		
		return oldNextLayer;
	}


	@Override
	public Layer assignNextLayer(Layer nextLayer) {
		Layer oldNextLayer = this.nextLayer;
		this.nextLayer = nextLayer;
		return oldNextLayer;
	}


	@Override
	public void clearNextNeurons() {
		for (Neuron neuron : neurons) {
			neuron.clearNextNeurons();
		}
	}


	@Override
	public Function getActivateRef() {
		return this.activateRef;
	}

	
	@Override
	public Function setActivateRef(Function activateRef) {
		return this.activateRef = activateRef;
	}

	
}
