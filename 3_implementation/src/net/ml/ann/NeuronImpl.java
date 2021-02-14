/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.ml.ann;

import java.util.Arrays;
import java.util.List;

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
	 * Main layer.
	 */
	protected Layer layer = null;
	
	
	/**
	 * Input value.
	 */
	protected double input = 0;
	
	
	/**
	 * Bias.
	 */
	protected double bias = 0;

	
	/**
	 * Output value.
	 */
	protected double output = 0;
			
	
	/**
	 * Activation function reference.
	 */
	protected Function activateRef = null;
	
	
	/**
	 * Next neurons.
	 */
	protected List<WeightedNeuron> nextNeurons = Util.newList(0);
	
	
	/**
	 * Next neurons.
	 */
	protected List<WeightedNeuron> latentNeurons = Util.newList(0);

	
	/**
	 * Default constructor.
	 * @param layer this layer.
	 */
	public NeuronImpl(Layer layer) {
		this.layer = layer;
		this.id = layer.getIdRef().get();
		this.activateRef = layer.getActivateRef();
		
		if (layer != null && layer.getLatentLayer() != null)
			resetLatentNeurons();
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
	public double getBias() {
		return bias;
	}


	@Override
	public void setBias(double bias) {
		this.bias = bias;
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
	public WeightedNeuron[] getPrevNeurons() {
		List<WeightedNeuron> sources = Util.newList(0);
		if (layer == null) return sources.toArray(new WeightedNeuron[] {});
		
		Layer prevLayer = layer.getPrevLayer();
		if (prevLayer == null) return sources.toArray(new WeightedNeuron[] {});
		
		for (int i = 0; i < prevLayer.size(); i++) {
			Neuron prevNeuron = prevLayer.get(i);
			WeightedNeuron found = prevNeuron.findNextNeuron(this);
			if (found != null) {
				WeightedNeuron wn = new WeightedNeuron(prevNeuron, found.weight);
				sources.add(wn);
			}
		}
		
		return sources.toArray(new WeightedNeuron[] {});
	}
	

	@Override
	public WeightedNeuron[] getNextNeurons() {
		return nextNeurons.toArray(new WeightedNeuron[] {});
	}


	@Override
	public boolean setNextNeuron(Neuron neuron, Weight weight) {
		Layer nextLayer = layer != null ? layer.getNextLayer() : null;
		if (nextLayer == null || neuron == null || weight == null)
			return false;
		if (nextLayer.indexOf(neuron) < 0) return false;
		
		WeightedNeuron wn = findNextNeuron(neuron);
		if (wn == null) {
			wn = new WeightedNeuron(neuron, weight);
			nextNeurons.add(wn);
		}
		else {
			wn.weight.value = weight.value;
		}
		
		return true;
	}

	
	@Override
	public boolean removeNextNeuron(Neuron neuron) {
		if (neuron == null) return false;
		for (int i = 0; i < nextNeurons.size(); i++) {
			if (nextNeurons.get(i).neuron == neuron) {
				nextNeurons.remove(i);
				return true;
			}
		}

		return false;
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
	public WeightedNeuron findNextNeuron(Neuron neuron) {
		for (int i = 0; i < nextNeurons.size(); i++) {
			WeightedNeuron wn = nextNeurons.get(i);
			if (wn.neuron == neuron) return wn;
		}
		
		return null;
	}
	
	
	@Override
	public WeightedNeuron findNextNeuron(int neuronId) {
		for (int i = 0; i < nextNeurons.size(); i++) {
			WeightedNeuron wn = nextNeurons.get(i);
			if (wn.neuron != null && wn.neuron.id() == neuronId) return wn;
		}
		
		return null;
	}


	@Override
	public WeightedNeuron[] getLatentNeurons() {
		return latentNeurons.toArray(new WeightedNeuron[] {});
	}


	@Override
	public boolean setLatentNeuron(Neuron neuron, Weight weight) {
		Layer latentLayer = layer != null ? layer.getLatentLayer() : null;
		if (latentLayer == null || neuron == null || weight == null)
			return false;
		if (latentLayer.indexOf(neuron) < 0) return false;
		
		WeightedNeuron wn = findLatentNeuron(neuron);
		if (wn == null) {
			wn = new WeightedNeuron(neuron, weight);
			latentNeurons.add(wn);
		}
		else {
			wn.weight.value = weight.value;
		}
		
		return true;
	}


	@Override
	public boolean removeLatentNeuron(Neuron neuron) {
		if (neuron == null) return false;
		for (int i = 0; i < latentNeurons.size(); i++) {
			if (latentNeurons.get(i).neuron == neuron) {
				latentNeurons.remove(i);
				return true;
			}
		}

		return false;
	}


	@Override
	public WeightedNeuron findLatentNeuron(Neuron neuron) {
		for (int i = 0; i < latentNeurons.size(); i++) {
			WeightedNeuron wn = latentNeurons.get(i);
			if (wn.neuron == neuron) return wn;
		}
		
		return null;
	}


	@Override
	public WeightedNeuron findLatentNeuron(int neuronId) {
		for (int i = 0; i < latentNeurons.size(); i++) {
			WeightedNeuron wn = latentNeurons.get(i);
			if (wn.neuron != null && wn.neuron.id() == neuronId) return wn;
		}
		
		return null;
	}


	@Override
	public void clearLatentNeurons() {
		latentNeurons.clear();
	}


	@Override
	public void resetLatentNeurons() {
		latentNeurons.clear();
		Layer latentLayer = layer != null ? layer.getLatentLayer() : null;
		if (latentLayer == null) return; 
		
		for (int i = 0; i < latentLayer.size(); i++) {
			WeightedNeuron latentNeuron = new WeightedNeuron(latentLayer.get(i), new Weight(0));
			latentNeurons.add(latentNeuron);
		}
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
	public Neuron getNextSibling() {
		if (layer == null) return null;
		
		int index = layer.indexOf(this);
		if (index < 0 || index >= layer.size() - 1)
			return null;
		else
			return layer.get(index + 1);
	}

	
	@Override
	public Layer getLayer() {
		return layer;
	}


	@Override
	public double eval() {
		List<WeightedNeuron> sources = Util.newList(0);
		sources.addAll(Arrays.asList(getPrevNeurons()));
		sources.addAll(Arrays.asList(getLatentNeurons()));
		
		if (sources.size() == 0) {
			double out = getInput();
			setOutput(out);
			return out;
		}
		
		double in = getBias();
		for (WeightedNeuron source : sources) {
			in += source.weight.value * source.neuron.getOutput();
		}
		
		setInput(in);
		double out = getActivateRef().eval(in);
		setOutput(out);
		return out;
	}

	
}
