/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.ea.ann;

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
	 * Internal identifier reference.
	 */
	protected Id idRef = new Id();
	
	
	/**
	 * Identifier.
	 */
	protected int id = -1;
	
	
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
	 * @param idRef identifier reference.
	 */
	public LayerImpl(Function activateRef, Id idRef) {
		this.activateRef = activateRef;
		if (idRef != null) this.idRef = idRef;
		this.id = this.idRef.get();
	}


	@Override
	public Id getIdRef() {
		return idRef;
	}


	@Override
	public int id() {
		return id;
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
	public Layer getPrevLayerImplicit() {
		return prevLayerImplicit;
	}
	
	
	@Override
	public boolean setPrevLayer(Layer prevLayer) {
		if (prevLayer == this.prevLayer) return false;
		if (this.prevLayer == null && this.prevLayerImplicit != null) return false;
		if (prevLayer != null && prevLayer == getRibinLayer()) return false;

		Layer oldPrevLayer = this.prevLayer;
		Layer oldPrevPrevLayer = null;
		if (oldPrevLayer != null) {
			oldPrevPrevLayer = oldPrevLayer.getPrevLayer();
			clearNextNeurons(oldPrevLayer);
		}

		this.prevLayer = prevLayer;
		if (prevLayer == null) return true;

		clearNextNeurons(prevLayer);
		((LayerImpl)prevLayer).nextLayer = this;
		for (int i = 0; i < prevLayer.size(); i++) {
			Neuron neuron = prevLayer.get(i);
			for (int j = 0; j < size(); j++) {
				neuron.setNextNeuron(get(j), new Weight(0));
			}
		}
		
		if (oldPrevPrevLayer == null) return true;
		clearNextNeurons(oldPrevPrevLayer);
		((LayerImpl)oldPrevPrevLayer).nextLayer = prevLayer;
		((LayerImpl)prevLayer).prevLayer = oldPrevPrevLayer;
		for (int i = 0; i < oldPrevPrevLayer.size(); i++) {
			Neuron neuron = oldPrevPrevLayer.get(i);
			for (int j = 0; j < prevLayer.size(); j++) {
				neuron.setNextNeuron(prevLayer.get(j), new Weight(0));
			}
		}
		
		return true;
	}


	@Override
	public Layer getNextLayer() {
		return nextLayer;
	}

	
	@Override
	public boolean setNextLayer(Layer nextLayer) {
		if (nextLayer == this.nextLayer) return false;
		if (nextLayer != null) {
			if (nextLayer == getRiboutLayer()) return false;
			if (nextLayer.getRibinLayer() == this) return false;
		}
		
		clearNextNeurons(this);
		
		Layer oldNextLayer = this.nextLayer;
		Layer oldNextNextLayer = null;
		if (oldNextLayer != null) {
			oldNextNextLayer = oldNextLayer.getNextLayer();
			clearNextNeurons(oldNextLayer);
		}

		this.nextLayer = nextLayer;
		if (nextLayer == null) return true;

		clearNextNeurons(nextLayer);
		((LayerImpl)nextLayer).prevLayer = this;
		for (int i = 0; i < size(); i++) {
			Neuron neuron = get(i);
			for (int j = 0; j < nextLayer.size(); j++) {
				neuron.setNextNeuron(nextLayer.get(j), new Weight(0));
			}
		}
		
		if (oldNextNextLayer == null) return true;
		((LayerImpl)oldNextNextLayer).prevLayer = nextLayer;
		((LayerImpl)nextLayer).nextLayer = oldNextNextLayer;
		for (int i = 0; i < oldNextNextLayer.size(); i++) {
			Neuron neuron = oldNextNextLayer.get(i);
			for (int j = 0; j < nextLayer.size(); j++) {
				neuron.setNextNeuron(nextLayer.get(j), new Weight(0));
			}
		}
		
		return true;
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
	public boolean setRibinLayer(Layer ribinLayer) {
		if (this.ribinLayer == ribinLayer) return false;
		if (ribinLayer != null) {
			if (ribinLayer.getNextLayer() != null) return false;
			if (ribinLayer == getPrevLayer()) return false;
		}
		
		this.ribinLayer = ribinLayer;
		if (ribinLayer == null) return true;
			
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
		
		return true;
	}

	
	@Override
	public Layer getRiboutLayer() {
		return riboutLayer;
	}


	@Override
	public boolean setRiboutLayer(Layer riboutLayer) {
		if (this.riboutLayer == riboutLayer) return false;
		if (riboutLayer != null && riboutLayer.getPrevLayer() != null) return false;
		if (riboutLayer != null) {
			if (riboutLayer.getPrevLayer() != null) return false;
			if (riboutLayer == getNextLayer()) return false;
		}
		
		Layer oldRiboutLayer = this.riboutLayer;
		this.riboutLayer = riboutLayer;
		for (Neuron neuron : neurons) ((NeuronImpl)neuron).riboutNeurons.clear();
		
		if (oldRiboutLayer != null) ((LayerImpl)oldRiboutLayer).prevLayerImplicit = null;
		if (riboutLayer == null) return true;
		
		for (Neuron neuron : neurons) {
			for (int i = 0; i < riboutLayer.size(); i++) {
				WeightedNeuron wn = new WeightedNeuron(riboutLayer.get(i), new Weight(0));
				((NeuronImpl)neuron).riboutNeurons.add(wn);
			}
		}
		
		((LayerImpl)riboutLayer).prevLayerImplicit = this;
		
		return true;
	}


	@Override
	public Function getActivateRef() {
		return this.activateRef;
	}

	
	@Override
	public Function setActivateRef(Function activateRef) {
		return this.activateRef = activateRef;
	}


	@Override
	public double[] getInput() {
		if (neurons.size() == 0) return null;
		double[] array = new double[neurons.size()];
		for (int j = 0; j < array.length; j++) {
			array[j] = neurons.get(j).getInput();
		}
		return array;
	}


	@Override
	public double[] getOutput() {
		if (neurons.size() == 0) return null;
		double[] array = new double[neurons.size()];
		for (int j = 0; j < array.length; j++) {
			array[j] = neurons.get(j).getOutput();
		}
		return array;
	}

	
//	/**
//	 * Getting previous neurons of specified neuron.
//	 * @param neuron specified neuron.
//	 * @return previous neurons of specified neuron.
//	 */
//	protected WeightedNeuron[] getImplicitPrevNeurons(Neuron neuron) {
//		if (neuron == null || prevLayer != null || prevLayerImplicit == null)
//			return new WeightedNeuron[] {};
//		
//		if (!neurons.contains(neuron)) return new WeightedNeuron[] {};
//		Layer rLayer = prevLayerImplicit.getRiboutLayer();
//		if (this != rLayer) return new WeightedNeuron[] {};
//		
//		List<WeightedNeuron> wns = Util.newList(0);
//		for (int i = 0; i < prevLayerImplicit.size(); i++) {
//			Neuron prevNeuron = prevLayerImplicit.get(i);
//			WeightedNeuron nw = prevNeuron.findRiboutNeuron(neuron);
//			if (nw != null) {
//				wns.add(new WeightedNeuron(prevNeuron, nw.weight));
//			}
//		}
//		
//		return wns.toArray(new WeightedNeuron[] {});
//	}


}
