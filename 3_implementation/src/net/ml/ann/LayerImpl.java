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
	 * Implicit previous layer.
	 */
	protected Layer prevLayerImplicit = null;

	
	/**
	 * Next layer.
	 */
	protected Layer nextLayer = null;
	
	
	/**
	 * Input rib layer.
	 */
	protected Layer ribinLayer = null;

	
	/**
	 * Output rib layer.
	 */
	protected Layer riboutLayer = null;
	
	
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
		neuron.clearRiboutNeurons();
		
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
		if (this.prevLayer == null && this.prevLayerImplicit != null) return this.prevLayer;

		Layer oldPrevLayer = this.prevLayer;
		Layer oldPrevPrevLayer = null;
		if (oldPrevLayer != null) {
			oldPrevPrevLayer = oldPrevLayer.getPrevLayer();
			clearNextNeurons(oldPrevLayer);
		}

		this.prevLayer = prevLayer;
		if (prevLayer == null) return oldPrevLayer;

		clearNextNeurons(prevLayer);
		((LayerImpl)prevLayer).nextLayer = this;
		for (int i = 0; i < prevLayer.size(); i++) {
			Neuron neuron = prevLayer.get(i);
			for (int j = 0; j < size(); j++) {
				neuron.setNextNeuron(get(j), new Weight(0));
			}
		}
		
		if (oldPrevPrevLayer == null) return oldPrevLayer;
		clearNextNeurons(oldPrevPrevLayer);
		((LayerImpl)oldPrevPrevLayer).nextLayer = prevLayer;
		((LayerImpl)prevLayer).prevLayer = oldPrevPrevLayer;
		for (int i = 0; i < oldPrevPrevLayer.size(); i++) {
			Neuron neuron = oldPrevPrevLayer.get(i);
			for (int j = 0; j < prevLayer.size(); j++) {
				neuron.setNextNeuron(prevLayer.get(j), new Weight(0));
			}
		}
		
		return oldPrevLayer;
	}


	@Override
	public Layer getNextLayer() {
		return nextLayer;
	}

	
	@Override
	public Layer setNextLayer(Layer nextLayer) {
		if (nextLayer == this.nextLayer) return this.nextLayer;
		if (getRibinLayer() != null && getRibinLayer() == getPrevLayer())
			return this.nextLayer;

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
		((LayerImpl)nextLayer).prevLayer = this;
		for (int i = 0; i < size(); i++) {
			Neuron neuron = get(i);
			for (int j = 0; j < nextLayer.size(); j++) {
				neuron.setNextNeuron(nextLayer.get(j), new Weight(0));
			}
		}
		
		if (oldNextNextLayer == null) return oldNextLayer;
		((LayerImpl)oldNextNextLayer).prevLayer = nextLayer;
		((LayerImpl)nextLayer).nextLayer = oldNextNextLayer;
		for (int i = 0; i < oldNextNextLayer.size(); i++) {
			Neuron neuron = oldNextNextLayer.get(i);
			for (int j = 0; j < nextLayer.size(); j++) {
				neuron.setNextNeuron(nextLayer.get(j), new Weight(0));
			}
		}
		
		return oldNextLayer;
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
	public Layer getRibinLayer() {
		return ribinLayer;
	}


	@Override
	public Layer setRibinLayer(Layer ribinLayer) {
		if (this.ribinLayer == ribinLayer) return this.ribinLayer;
		if (ribinLayer != null && ribinLayer.getNextLayer() != null) return this.ribinLayer;
		
		Layer oldRibinLayer = this.ribinLayer;
		this.ribinLayer = ribinLayer;
		if (ribinLayer == null) return oldRibinLayer;
			
		clearNextNeurons(ribinLayer);
		Layer oldNextLayer = ribinLayer.getNextLayer();
		if (oldNextLayer != null) clearNextNeurons(oldNextLayer);

		((LayerImpl)ribinLayer).nextLayer = this;
		for (int i = 0; i < ribinLayer.size(); i++) {
			Neuron ribbinNeuron = ribinLayer.get(i);
			for (int j = 0; j < size(); j++) {
				ribbinNeuron.setNextNeuron(get(j), new Weight(0));
			}
		}
		
		return oldRibinLayer;
	}

	
	@Override
	public Layer getRiboutLayer() {
		return riboutLayer;
	}


	@Override
	public Layer setRiboutLayer(Layer riboutLayer) {
		if (this.riboutLayer == riboutLayer) return this.riboutLayer;
		if (riboutLayer != null && riboutLayer.getPrevLayer() != null) return this.riboutLayer;
		
		Layer oldRiboutLayer = this.riboutLayer;
		this.riboutLayer = riboutLayer;
		for (Neuron neuron : neurons) ((NeuronImpl)neuron).riboutNeurons.clear();
		
		if (oldRiboutLayer != null) ((LayerImpl)oldRiboutLayer).prevLayerImplicit = null;
		if (riboutLayer == null) return oldRiboutLayer;
		
		for (Neuron neuron : neurons) {
			for (int i = 0; i < riboutLayer.size(); i++) {
				WeightedNeuron wn = new WeightedNeuron(riboutLayer.get(i), new Weight(0));
				((NeuronImpl)neuron).riboutNeurons.add(wn);
			}
		}
		
		((LayerImpl)riboutLayer).prevLayerImplicit = this;
		
		return oldRiboutLayer;
	}


	/**
	 * Getting previous neurons of specified neuron.
	 * @param neuron specified neuron.
	 * @return previous neurons of specified neuron.
	 */
	protected WeightedNeuron[] getImplicitPrevNeurons(Neuron neuron) {
		if (neuron == null || prevLayer != null || prevLayerImplicit == null)
			return new WeightedNeuron[] {};
		
		if (!neurons.contains(neuron)) return new WeightedNeuron[] {};
		Layer rLayer = prevLayerImplicit.getRiboutLayer();
		if (this != rLayer) return new WeightedNeuron[] {};
		
		List<WeightedNeuron> wns = Util.newList(0);
		for (int i = 0; i < prevLayerImplicit.size(); i++) {
			Neuron prevNeuron = prevLayerImplicit.get(i);
			WeightedNeuron nw = prevNeuron.findRiboutNeuron(neuron);
			if (nw != null) {
				wns.add(new WeightedNeuron(prevNeuron, nw.weight));
			}
		}
		
		return wns.toArray(new WeightedNeuron[] {});
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
