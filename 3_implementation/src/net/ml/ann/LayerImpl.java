/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.ml.ann;

import java.util.List;

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
	protected List<Neuron> neurons = Util.newList(0);
	
	
	/**
	 * Previous layer.
	 */
	protected Layer prevLayer = null;
	
	
	/**
	 * Next layer.
	 */
	protected Layer nextLayer = null;
	
	
	/**
	 * Latent layer.
	 */
	protected Layer latentLayer = null;
	
	
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
		neuron.clearLatentNeurons();
		
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

		Layer oldPrevLayer = this.prevLayer;
		Layer oldPrevPrevLayer = null;
		if (oldPrevLayer != null) {
			oldPrevPrevLayer = oldPrevLayer.getPrevLayer();
			clearNextNeurons(oldPrevLayer);
		}

		this.prevLayer = prevLayer;
		if (prevLayer == null) return oldPrevLayer;

		clearNextNeurons(prevLayer);
		prevLayer.assignNextLayer(this);
		for (int i = 0; i < prevLayer.size(); i++) {
			Neuron neuron = prevLayer.get(i);
			for (int j = 0; j < size(); j++) {
				neuron.setNextNeuron(get(j), new Weight(0));
			}
		}
		
		if (oldPrevPrevLayer == null) return oldPrevLayer;
		clearNextNeurons(oldPrevPrevLayer);
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
	public void assignPrevLayer(Layer prevLayer) {
		this.prevLayer = prevLayer;
	}


	@Override
	public Layer getNextLayer() {
		return nextLayer;
	}

	
	@Override
	public Layer setNextLayer(Layer nextLayer) {
		if (nextLayer == this.nextLayer) return this.nextLayer;

		clearNextNeurons(this);
		
		Layer oldNextLayer = this.nextLayer;
		Layer oldNextNextLayer = null;
		if (oldNextLayer != null) {
			oldNextNextLayer = oldNextLayer.getNextLayer();
			clearNextNeurons(oldNextLayer);
		}

		this.nextLayer = nextLayer;
		if (nextLayer == null) return oldNextLayer;

		clearNextNeurons(nextLayer);
		nextLayer.assignPrevLayer(this);
		for (int i = 0; i < size(); i++) {
			Neuron neuron = get(i);
			for (int j = 0; j < nextLayer.size(); j++) {
				neuron.setNextNeuron(nextLayer.get(j), new Weight(0));
			}
		}
		
		if (oldNextNextLayer == null) return oldNextLayer;
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
	public void assignNextLayer(Layer nextLayer) {
		this.nextLayer = nextLayer;
	}


	/**
	 * Clearing next neurons of specified layer.
	 * @param layer specified layer.
	 */
	private static void clearNextNeurons(Layer layer) {
		if (layer == null) return;
		for (int i = 0; i < layer.size(); i++) {
			layer.get(i).clearNextNeurons();
		}
	}
	
	
	@Override
	public Layer getLatentLayer() {
		return latentLayer;
	}


	@Override
	public Layer setLatentLayer(Layer latentLayer) {
		if (this.latentLayer == latentLayer) return this.latentLayer;
		
		Layer oldLatentLayer = this.latentLayer;
		this.latentLayer = latentLayer;
		
		for (Neuron neuron : neurons) {
			neuron.resetLatentNeurons();
		}
		
		return oldLatentLayer;
	}


	@Override
	public WeightedNeuron[] getLatentNextNeurons(Neuron latentNeuron) {
		List<WeightedNeuron> latentNextNeurons = Util.newList(0);
		if (latentNeuron == null || latentLayer == null || latentLayer.indexOf(latentNeuron) < 0)
			return latentNextNeurons.toArray(new WeightedNeuron[] {});
		
		for (Neuron neuron : neurons) {
			WeightedNeuron[] wns = neuron.getLatentNeurons();
			for (WeightedNeuron wn : wns) {
				if (wn.neuron == latentNeuron) {
					latentNextNeurons.add(new WeightedNeuron(neuron, wn.weight));
					break;
				}
			}
		}
		
		return latentNextNeurons.toArray(new WeightedNeuron[] {});
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
