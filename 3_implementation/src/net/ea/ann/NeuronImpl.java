/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.ea.ann;

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
	 * Output rib neurons.
	 */
	protected List<WeightedNeuron> riboutNeurons = Util.newList(0);

	
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
	public WeightedNeuron[] getPrevNeurons(Layer prevLayer) {
		if (layer == null || prevLayer == null || prevLayer == layer.getPrevLayer())
			return getPrevNeurons();
		
		if (!(layer instanceof LayerImpl)) return new WeightedNeuron[] {};
		Layer prevLayerImplicit = layer.getPrevLayerImplicit();
		if (prevLayer == prevLayerImplicit)
			return getPrevNeuronsImplicit();
		else
			return new WeightedNeuron[] {};
	}


	@Override
	public WeightedNeuron[] getPrevNeuronsImplicit() {
		if (layer == null || !(layer instanceof LayerImpl)) return new WeightedNeuron[] {};
		
		Layer prevLayerImplicit = layer.getPrevLayerImplicit();
		if (prevLayerImplicit == null || prevLayerImplicit.getRiboutLayer() != this)
			return new WeightedNeuron[] {};
		
		List<WeightedNeuron> wns = Util.newList(0);
		for (int i = 0; i < prevLayerImplicit.size(); i++) {
			Neuron prevNeuron = prevLayerImplicit.get(i);
			WeightedNeuron nw = prevNeuron.findRiboutNeuron(this);
			if (nw != null) {
				wns.add(new WeightedNeuron(prevNeuron, nw.weight));
			}
		}
		
		return wns.toArray(new WeightedNeuron[] {});
	}


	@Override
	public WeightedNeuron[] getNextNeurons() {
		return nextNeurons.toArray(new WeightedNeuron[] {});
	}


	@Override
	public WeightedNeuron[] getNextNeurons(Layer nextLayer) {
		if (nextLayer == null || layer == null || layer.getNextLayer() == nextLayer)
			return getNextNeurons();
		else if (nextLayer == layer.getRiboutLayer())
			return riboutNeurons.toArray(new WeightedNeuron[] {});
		else
			return new WeightedNeuron[] {};
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
	public WeightedNeuron[] getRibinNeurons() {
		Layer ribinLayer = layer != null ? layer.getRibinLayer() : null;
		if (ribinLayer == null) return new WeightedNeuron[] {};

		List<WeightedNeuron> ribinNeurons = Util.newList(0);
		for (int i = 0; i < ribinLayer.size(); i++) {
			Neuron ribinNeuron = ribinLayer.get(i);
			WeightedNeuron[] wns = ribinNeuron.getNextNeurons();
			for (WeightedNeuron wn : wns) {
				if (wn.neuron == this) {
					ribinNeurons.add(new WeightedNeuron(ribinNeuron, wn.weight));
				}
			}
		}

		return ribinNeurons.toArray(new WeightedNeuron[] {});
	}


	@Override
	public boolean setRibinNeuron(Neuron ribinNeuron, Weight weight) {
		Layer ribinLayer = layer != null ? layer.getRibinLayer() : null;
		if (ribinLayer == null || ribinNeuron == null || weight == null) return false;
		if (ribinLayer.indexOf(ribinNeuron) < 0) return false;
		
		return ribinNeuron.setNextNeuron(this, weight);
	}


	@Override
	public boolean removeRibinNeuron(Neuron ribinNeuron) {
		Layer ribinLayer = layer != null ? layer.getRibinLayer() : null;
		if (ribinLayer == null || ribinNeuron == null) return false;
		if (ribinLayer.indexOf(ribinNeuron) < 0) return false;

		return ribinNeuron.removeNextNeuron(this);
	}


	@Override
	public WeightedNeuron findRibinNeuron(Neuron ribinNeuron) {
		Layer ribinLayer = layer != null ? layer.getRibinLayer() : null;
		if (ribinLayer == null || ribinNeuron == null) return null;
		if (ribinLayer.indexOf(ribinNeuron) < 0) return null;

		WeightedNeuron wn = ribinNeuron.findNextNeuron(this);
		if (wn == null)
			return null;
		else
			return new WeightedNeuron(ribinNeuron, wn.weight);
	}


	@Override
	public WeightedNeuron findRibinNeuron(int ribinNeuronId) {
		Layer ribinLayer = layer != null ? layer.getRibinLayer() : null;
		if (ribinLayer == null) return null;
		int index = ribinLayer.indexOf(ribinNeuronId);
		if (index < 0) return null;

		Neuron ribinNeuron = ribinLayer.get(index);
		WeightedNeuron wn = ribinNeuron.findNextNeuron(this);
		if (wn == null)
			return null;
		else
			return new WeightedNeuron(ribinNeuron, wn.weight);
	}


	@Override
	public void clearRibinNeurons() {
		Layer ribinLayer = layer != null ? layer.getRibinLayer() : null;
		if (ribinLayer == null) return;
		for (int i = 0; i < ribinLayer.size(); i++) {
			ribinLayer.get(i).removeNextNeuron(this);
		}
	}


	@Override
	public WeightedNeuron[] getRiboutNeurons() {
		return riboutNeurons.toArray(new WeightedNeuron[] {});
	}


	@Override
	public boolean setRiboutNeuron(Neuron riboutNeuron, Weight weight) {
		Layer ribinLayer = layer != null ? layer.getRiboutLayer() : null;
		if (ribinLayer == null || riboutNeuron == null || weight == null)
			return false;
		if (ribinLayer.indexOf(riboutNeuron) < 0) return false;
		
		WeightedNeuron wn = findRiboutNeuron(riboutNeuron);
		if (wn == null) {
			wn = new WeightedNeuron(riboutNeuron, weight);
			riboutNeurons.add(wn);
		}
		else {
			wn.weight.value = weight.value;
		}
		
		return true;
	}


	@Override
	public boolean removeRiboutNeuron(Neuron riboutNeuron) {
		if (riboutNeuron == null) return false;
		for (int i = 0; i < riboutNeurons.size(); i++) {
			if (riboutNeurons.get(i).neuron == riboutNeuron) {
				riboutNeurons.remove(i);
				return true;
			}
		}

		return false;
	}


	@Override
	public WeightedNeuron findRiboutNeuron(Neuron riboutNeuron) {
		for (int i = 0; i < riboutNeurons.size(); i++) {
			WeightedNeuron wn = riboutNeurons.get(i);
			if (wn.neuron == riboutNeuron) return wn;
		}
		
		return null;
	}


	@Override
	public WeightedNeuron findRiboutNeuron(int riboutNeuronId) {
		for (int i = 0; i < riboutNeurons.size(); i++) {
			WeightedNeuron wn = riboutNeurons.get(i);
			if (wn.neuron != null && wn.neuron.id() == riboutNeuronId) return wn;
		}
		
		return null;
	}


	@Override
	public void clearRiboutNeurons() {
		riboutNeurons.clear();
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
		sources.addAll(Arrays.asList(getRibinNeurons()));
		sources.addAll(Arrays.asList(getPrevNeuronsImplicit()));
		
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
