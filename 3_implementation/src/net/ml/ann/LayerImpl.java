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
	 * Constructor with activation function, previous layer, and next layer.
	 * @param activateRef activation function.
	 * @param prevLayer previous layer.
	 * @param nextLayer next layer.
	 */
	public LayerImpl(Function activateRef, Layer prevLayer, Layer nextLayer, Id idRef) {
		this.activateRef = activateRef;
		this.prevLayer = prevLayer;
		this.nextLayer = nextLayer;
		
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
	public boolean contains(Neuron neuron) {
		return neurons.contains(neuron);
	}

	
	@Override
	public int indexOf(Neuron neuron) {
		return neurons.indexOf(neuron);
	}

	
	@Override
	public Layer getNextLayer() {
		return nextLayer;
	}

	
	@Override
	public Layer setNextLayer(Layer nextLayer) {
		if (nextLayer == null) return null;
		
		for (int i = 0; i < size(); i++) {
			get(i).clearNextNeurons();
		}
		
		for (int i = 0; i < nextLayer.size(); i++) {
			nextLayer.get(i).clearPrevNeurons();
		}
		
		Layer oldNextLayer = this.nextLayer;
		this.nextLayer = nextLayer;
		this.nextLayer.assignPrevLayer(this);
		return oldNextLayer;
	}


	@Override
	public Layer assignNextLayer(Layer nextLayer) {
		Layer oldNextLayer = this.nextLayer;
		this.nextLayer = nextLayer;
		return oldNextLayer;
	}


	@Override
	public Layer getPrevLayer() {
		return prevLayer;
	}

	
	@Override
	public Layer setPrevLayer(Layer prevLayer) {
		if (prevLayer == null) return null;
		
		for (int i = 0; i < size(); i++) {
			get(i).clearPrevNeurons();
		}
		
		for (int i = 0; i < prevLayer.size(); i++) {
			prevLayer.get(i).clearNextNeurons();
		}
		
		Layer oldPrevLayer = this.prevLayer;
		this.prevLayer = prevLayer;
		this.prevLayer.assignNextLayer(this);
		return oldPrevLayer;
	}


	@Override
	public Layer assignPrevLayer(Layer prevLayer) {
		Layer oldPrevLayer = this.prevLayer;
		this.prevLayer = prevLayer;
		return oldPrevLayer;
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
