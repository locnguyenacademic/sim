/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.ml.ann;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.List;

import net.hudup.core.Util;

/**
 * This class is default implementation of neural network.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class NeuralNetworkImpl implements NeuralNetwork {


	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Layer type.
	 * @author Loc Nguyen
	 * @version 1.0
	 */
	public static enum LayerType {
		
		/**
		 * Memory layer.
		 */
		memory,
		
		/**
		 * Input layer.
		 */
		input,
		
		/**
		 * Hidden layer.
		 */
		hidden,
		
		/**
		 * Output layer.
		 */
		output,
		
		/**
		 * Unknown layer.
		 */
		unknown,
		
	}
	
	
	/**
	 * Internal identifier.
	 */
	protected Id idRef = new Id();
	
	
	/**
	 * Activation function reference.
	 */
	protected Function activateRef = null;

	
	/**
	 * Memory layer.
	 */
	protected Layer memoryLayer = null;
	
	
	/**
	 * Input layer.
	 */
	protected Layer inputLayer = null;

	
	/**
	 * Memory layer.
	 */
	protected List<Layer> hiddenLayers = Util.newList();

	
	/**
	 * Output layer.
	 */
	protected Layer outputLayer = null;
	

	/**
	 * Constructor with number of neurons.
	 * @param activateRef activation function.
	 * @param nMemoryNeuron number of memory neurons.
	 * @param nInputNeuron number of input neurons.
	 * @param nOutputNeuron number of output neurons.
	 * @param nHiddenLayer number of hidden layers.
	 * @param nHiddenNeuron number of hidden neurons.
	 */
	public NeuralNetworkImpl(Function activateRef, int nMemoryNeuron, int nInputNeuron, int nOutputNeuron, int nHiddenLayer, int nHiddenNeuron) {
		nMemoryNeuron = nMemoryNeuron < 0 ? 0 : nMemoryNeuron;
		nInputNeuron = nInputNeuron < 1 ? 1 : nInputNeuron;
		nOutputNeuron = nOutputNeuron < 1 ? 1 : nOutputNeuron;
		nHiddenLayer = nHiddenLayer < 0 ? 0 : nHiddenLayer;
		if (nHiddenLayer == 0) nHiddenNeuron = 0;
		nHiddenNeuron = nHiddenNeuron < 0 ? 0 : nHiddenNeuron;
		
		this.activateRef = activateRef;
		
		if (nMemoryNeuron > 0)
			this.memoryLayer = newLayer(null, null, nMemoryNeuron);
		this.inputLayer = newLayer(this.memoryLayer, null, nInputNeuron);
		
		if (nHiddenNeuron > 0) {
			this.hiddenLayers = Util.newList(nHiddenLayer);
			for (int l = 0; l < nHiddenLayer; l++) {
				Layer prevHiddenLayer = l == 0 ? this.inputLayer : this.hiddenLayers.get(l - 1);
				Layer hiddenLayer = newLayer(prevHiddenLayer, null, nHiddenNeuron);
				this.hiddenLayers.add(hiddenLayer);
			}
		}
		
		Layer preOutputLayer = this.hiddenLayers.size() > 0 ? this.hiddenLayers.get(this.hiddenLayers.size() - 1) : this.inputLayer;
		this.outputLayer = newLayer(preOutputLayer, this.memoryLayer, nOutputNeuron);
	}

	
	/**
	 * Getting non-empty layers.
	 * @return list of non-empty layers.
	 */
	private List<Layer> getNonemptyLayers() {
		List<Layer> layers = Util.newList();
		
		if (memoryLayer != null && memoryLayer.size() > 0) layers.add(memoryLayer);
		if (inputLayer != null && inputLayer.size() > 0) layers.add(inputLayer);
		
		for (Layer hiddenLayer : hiddenLayers) {
			if (hiddenLayer.size() > 0) layers.add(hiddenLayer);
		}
		
		if (outputLayer != null && outputLayer.size() > 0) layers.add(outputLayer);

		return layers;
	}
	
	
	/**
	 * Constructor with number of neurons.
	 * @param activateRef activation function.
	 * @param nInputNeuron number of input neurons.
	 * @param nOutputNeuron number of output neurons.
	 * @param nHiddenLayer number of hidden layers.
	 * @param nHiddenNeuron number of hidden neurons.
	 */
	public NeuralNetworkImpl(Function activateRef, int nInputNeuron, int nOutputNeuron, int nHiddenLayer, int nHiddenNeuron) {
		this(activateRef, 0, nInputNeuron, nOutputNeuron, nHiddenLayer, nHiddenNeuron);
	}
	
	
	/**
	 * Constructor with number of neurons.
	 * @param activateRef activation function.
	 * @param nInputNeuron number of input neurons.
	 * @param nOutputNeuron number of output neurons.
	 */
	public NeuralNetworkImpl(Function activateRef, int nInputNeuron, int nOutputNeuron) {
		this(activateRef, 0, nInputNeuron, nOutputNeuron, 0, 0);
	}

	
	/**
	 * Constructor with number of neurons.
	 * @param nMemoryNeuron number of memory neurons.
	 * @param nInputNeuron number of input neurons.
	 * @param nOutputNeuron number of output neurons.
	 * @param nHiddenLayer number of hidden layers.
	 * @param nHiddenNeuron number of hidden neurons.
	 */
	public NeuralNetworkImpl(int nMemoryNeuron, int nInputNeuron, int nOutputNeuron, int nHiddenLayer, int nHiddenNeuron) {
		this(new LogisticFunction(), nMemoryNeuron, nInputNeuron, nOutputNeuron, nHiddenLayer, nHiddenNeuron);
	}

	
	/**
	 * Constructor with number of neurons.
	 * @param nInputNeuron number of input neurons.
	 * @param nOutputNeuron number of output neurons.
	 * @param nHiddenLayer number of hidden layers.
	 * @param nHiddenNeuron number of hidden neurons.
	 */
	public NeuralNetworkImpl(int nInputNeuron, int nOutputNeuron, int nHiddenLayer, int nHiddenNeuron) {
		this(new LogisticFunction(), 0, nInputNeuron, nOutputNeuron, nHiddenLayer, nHiddenNeuron);
	}
	
	
	/**
	 * Constructor with number of neurons.
	 * @param nInputNeuron number of input neurons.
	 * @param nOutputNeuron number of output neurons.
	 */
	public NeuralNetworkImpl(int nInputNeuron, int nOutputNeuron) {
		this(new LogisticFunction(), 0, nInputNeuron, nOutputNeuron, 0, 0);
	}

	
	/**
	 * Creating new layer.
	 * @param prevLayer previous layer.
	 * @param nextLayer next layer.
	 * @param nNeuron number of neurons.
	 * @return new layer.
	 */
	private Layer newLayer(Layer prevLayer, Layer nextLayer, int nNeuron) {
		LayerImpl layer = new LayerImpl(activateRef, idRef);
		nNeuron = nNeuron < 0 ? 0 : nNeuron;
		for (int i = 0; i < nNeuron; i++) {
			layer.add(layer.newNeuron());
		}
		
		if (prevLayer != null) prevLayer.setNextLayer(layer);
		if (nextLayer != null) layer.setNextLayer(nextLayer);

		return layer;
	}
	
	
	@Override
	public Layer getMemoryLayer() throws RemoteException {
		return memoryLayer;
	}

	
	@Override
	public Layer getInputLayer() throws RemoteException {
		return inputLayer;
	}

	
	@Override
	public int getHiddenLayerCount() throws RemoteException {
		return hiddenLayers.size();
	}

	
	@Override
	public Layer getHiddenLayer(int index) throws RemoteException {
		return hiddenLayers.get(index);
	}

	
	/**
	 * Getting index of hidden layer.
	 * @param layer hidden layer.
	 * @return index of hidden layer.
	 */
	protected int hiddenIndexOf(Layer layer) {
		if (layer == null) return -1;
		
		for (int i = 0; i < hiddenLayers.size(); i++) {
			Layer hiddenLayer = hiddenLayers.get(i);
			if (layer == hiddenLayer) return i;
		}
		
		return -1;
	}
	
	
	@Override
	public Layer getOutputLayer() throws RemoteException {
		return outputLayer;
	}

	
	/**
	 * Getting type of specified layer.
	 * @param layer specified layer.
	 * @return type of specified layer.
	 */
	protected LayerType typeOf(Layer layer) {
		if (layer == null) return LayerType.unknown;
		
		if (memoryLayer != null && layer == memoryLayer)
			return LayerType.memory;
		if (inputLayer != null && layer == inputLayer)
			return LayerType.input;
		if (outputLayer != null && layer == outputLayer)
			return LayerType.output;
		
		for (Layer hiddenLayer : hiddenLayers) {
			if (layer == hiddenLayer) return LayerType.hidden;
		}
		
		return LayerType.unknown;
	}
	
	
	@Override
	public Neuron findNeuron(int neuronId) throws RemoteException {
		List<Layer> layers = getNonemptyLayers();
		for (Layer layer : layers) {
			int index = layer.indexOf(neuronId);
			if (index >= 0) return layer.get(index);
		}
		
		return null;
	}
	
	
	@Override
	public synchronized boolean learn(Collection<double[]> sample, int nInput, int nOutput) throws RemoteException {
		return false;
	}

	
}
