/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.ml.ann;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

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
		 * Latent layer.
		 */
		latent,
		
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
	 * Input layer.
	 */
	protected Layer inputLayer = null;

	
	/**
	 * Memory layer.
	 */
	protected List<Layer> hiddenLayers = Util.newList(0);

	
	/**
	 * Output layer.
	 */
	protected Layer outputLayer = null;
	

	/**
	 * Latent layer.
	 */
	protected Layer latentLayer = null;
	
	
	/**
	 * Default constructor.
	 */
	protected NeuralNetworkImpl() {
		
	}
	
	
	/**
	 * Constructor with number of neurons.
	 * @param activateRef activation function.
	 * @param nInputNeuron number of input neurons.
	 * @param nOutputNeuron number of output neurons.
	 * @param nHiddenLayer number of hidden layers.
	 * @param nHiddenNeuron number of hidden neurons.
	 * @param nLatentNeuron number of memory neurons.
	 */
	public NeuralNetworkImpl(Function activateRef, int nInputNeuron, int nOutputNeuron, int nHiddenLayer, int nHiddenNeuron, int nLatentNeuron) {
		nInputNeuron = nInputNeuron < 1 ? 1 : nInputNeuron;
		nOutputNeuron = nOutputNeuron < 1 ? 1 : nOutputNeuron;
		nHiddenLayer = nHiddenLayer < 0 ? 0 : nHiddenLayer;
		if (nHiddenLayer == 0) nHiddenNeuron = 0;
		nHiddenNeuron = nHiddenNeuron < 0 ? 0 : nHiddenNeuron;
		nLatentNeuron = nLatentNeuron < 0 ? 0 : nLatentNeuron;
		
		this.activateRef = activateRef;
		
		this.inputLayer = newLayer(nInputNeuron, null, null, null);
		
		if (nHiddenNeuron > 0) {
			this.hiddenLayers = Util.newList(nHiddenLayer);
			for (int l = 0; l < nHiddenLayer; l++) {
				Layer prevHiddenLayer = l == 0 ? this.inputLayer : this.hiddenLayers.get(l - 1);
				Layer hiddenLayer = newLayer(nHiddenNeuron, prevHiddenLayer, null, null);
				this.hiddenLayers.add(hiddenLayer);
			}
		}
		
		Layer preOutputLayer = this.hiddenLayers.size() > 0 ? this.hiddenLayers.get(this.hiddenLayers.size() - 1) : this.inputLayer;
		this.outputLayer = newLayer(nOutputNeuron, preOutputLayer, null, null);
		
		if (nLatentNeuron > 0) {
			this.latentLayer = newLayer(nLatentNeuron, this.outputLayer, null, null);
			if (nHiddenNeuron > 0)
				this.hiddenLayers.get(0).setLatentLayer(this.latentLayer);
		}
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
		this(activateRef, nInputNeuron, nOutputNeuron, nHiddenLayer, nHiddenNeuron, 0);
	}
	
	
	/**
	 * Constructor with number of neurons.
	 * @param activateRef activation function.
	 * @param nInputNeuron number of input neurons.
	 * @param nOutputNeuron number of output neurons.
	 */
	public NeuralNetworkImpl(Function activateRef, int nInputNeuron, int nOutputNeuron) {
		this(activateRef, nInputNeuron, nOutputNeuron, 0, 0, 0);
	}

	
	/**
	 * Constructor with number of neurons.
	 * @param nInputNeuron number of input neurons.
	 * @param nOutputNeuron number of output neurons.
	 * @param nHiddenLayer number of hidden layers.
	 * @param nHiddenNeuron number of hidden neurons.
	 * @param nLatentNeuron number of memory neurons.
	 */
	public NeuralNetworkImpl(int nInputNeuron, int nOutputNeuron, int nHiddenLayer, int nHiddenNeuron, int nLatentNeuron) {
		this(new LogisticFunction(), nInputNeuron, nOutputNeuron, nHiddenLayer, nHiddenNeuron, nLatentNeuron);
	}

	
	/**
	 * Constructor with number of neurons.
	 * @param nInputNeuron number of input neurons.
	 * @param nOutputNeuron number of output neurons.
	 * @param nHiddenLayer number of hidden layers.
	 * @param nHiddenNeuron number of hidden neurons.
	 */
	public NeuralNetworkImpl(int nInputNeuron, int nOutputNeuron, int nHiddenLayer, int nHiddenNeuron) {
		this(new LogisticFunction(), nInputNeuron, nOutputNeuron, nHiddenLayer, nHiddenNeuron, 0);
	}
	
	
	/**
	 * Constructor with number of neurons.
	 * @param nInputNeuron number of input neurons.
	 * @param nOutputNeuron number of output neurons.
	 */
	public NeuralNetworkImpl(int nInputNeuron, int nOutputNeuron) {
		this(new LogisticFunction(), nInputNeuron, nOutputNeuron, 0, 0, 0);
	}

	
	/**
	 * Getting type of specified layer.
	 * @param layer specified layer.
	 * @return type of specified layer.
	 */
	public LayerType typeOf(Layer layer) {
		if (layer == null) return LayerType.unknown;
		
		if (inputLayer != null && layer == inputLayer)
			return LayerType.input;
		if (outputLayer != null && layer == outputLayer)
			return LayerType.output;
		
		for (Layer hiddenLayer : hiddenLayers) {
			if (layer == hiddenLayer) return LayerType.hidden;
		}
		
		if (latentLayer != null && layer == latentLayer)
			return LayerType.latent;

		return LayerType.unknown;
	}
	
	
	/**
	 * Getting input layer.
	 * @return input layer.
	 */
	public Layer getInputLayer() {
		return inputLayer;
	}

	
	/**
	 * Getting hidden layers.
	 * @return array of hidden layers.
	 */
	public Layer[] getHiddenLayers() {
		return hiddenLayers.toArray(new Layer[] {});
	}

	
	/**
	 * Getting index of hidden layer.
	 * @param layer hidden layer.
	 * @return index of hidden layer.
	 */
	public int hiddenIndexOf(Layer layer) {
		if (layer == null) return -1;
		
		for (int i = 0; i < hiddenLayers.size(); i++) {
			Layer hiddenLayer = hiddenLayers.get(i);
			if (layer == hiddenLayer) return i;
		}
		
		return -1;
	}
	
	
	/**
	 * Getting output layer.
	 * @return output layer.
	 */
	public Layer getOutputLayer() {
		return outputLayer;
	}

	
	/**
	 * Getting latent layer.
	 * @return latent layer.
	 */
	public Layer getLatentLayer() {
		return latentLayer;
	}

	
	/**
	 * Finding neuron by specified identifier.
	 * @param neuronId specified identifier.
	 * @return found neuron.
	 */
	public Neuron findNeuron(int neuronId) {
		List<Layer> layers = getNonemptyLayers();
		for (Layer layer : layers) {
			int index = layer.indexOf(neuronId);
			if (index >= 0) return layer.get(index);
		}
		
		return null;
	}
	
	
	@Override
	public synchronized double[] eval(double[] input) throws RemoteException {
		if (inputLayer == null || outputLayer == null) return null;

		for (int i = 0; i < inputLayer.size(); i++) {
			Neuron neuron = inputLayer.get(i);
			neuron.setInput(input[i]);
			neuron.setOutput(input[i]);
		}
		
		List<Layer> layers = Util.newList(0);
		layers.addAll(hiddenLayers);
		layers.add(outputLayer);
		
		//Calculating outputs.
		for (Layer layer : layers) {
			for (int j = 0; j < layer.size(); j++) {
				Neuron neuron = layer.get(j);
				neuron.eval();
			}
		}

		double[] output = new double[outputLayer.size()];
		for (int i = 0; i < output.length; i++) {
			output[i] = outputLayer.get(i).getOutput();
		}
		return output;
	}


	@Override
	public synchronized boolean learn(Collection<double[]> sample, double learningRate) throws RemoteException {
		if (inputLayer == null || outputLayer == null) return false;
		int nInput = inputLayer.size();
		int nOutput = outputLayer.size();
		if (nInput == 0 || nOutput == 0) return false;
		
		List<Layer> layers = getNonemptyLayers();
		for (Layer layer : layers) {
			for (int j = 0; j < layer.size(); j++) {
				Neuron neuron = layer.get(j);
				neuron.setInput(0);
				neuron.setOutput(0);
				neuron.setBias(0);
			}
		}
		
		
		for (double[] record : sample) {
			double[] input = Arrays.copyOfRange(record, 0, nInput);
			double[] output = Arrays.copyOfRange(record, nInput, nInput + nOutput);
			
			//Calculating outputs.
			eval(input);
			
			layers.clear();
			layers.add(inputLayer);
			layers.addAll(hiddenLayers);
			layers.add(outputLayer);
			
			//Calculating errors.
			List<double[]> errors = calcErrors(layers, output);
			
			//Updating weights and biases.
			updateWeightsBiases(layers, errors, learningRate);
			
			//Updating weights and biases related to latent layer.
			updateWeightsBiasesLatent(layers, errors, learningRate);
		}
		
		return true;
	}

	
	/**
	 * Calculating errors.
	 * @param layers list of layers including input layer.
	 * @param output specified output.
	 * @return list of errors.
	 */
	private List<double[]> calcErrors(List<Layer> layers, double[] output) {
		List<double[]> errors = Util.newList(0);
		
		for (int i = layers.size() - 1; i >= 1; i--) {
			Layer layer = layers.get(i);
			Layer nextLayer = i < layers.size() - 1 ? layers.get(i + 1) : null;
			double[] error = new double[layer.size()];
			errors.add(0, error);
			
			for (int j = 0; j < layer.size(); j++) {
				Neuron neuron = layer.get(j);
				double out = neuron.getOutput();
				
				if (i == layers.size() - 1)
					error[j] = out * (1-out) * (output[j]-out);
				else {
					double rsum = 0;
					double[] nextError = errors.get(1);
					WeightedNeuron[] targets = neuron.getNextNeurons();
					for (WeightedNeuron target : targets) {
						int index = nextLayer.indexOf(target.neuron);
						rsum += nextError[index] * target.weight.value;
					}
					
					error[j] = out * (1-out) * rsum;
				}
			}
		}
		
		return errors;
	}
	
	
	/**
	 * Updating weights and biases.
	 * @param layers list of layers including input layer.
	 * @param errors list of errors.
	 * @param learningRate learning rate.
	 */
	private void updateWeightsBiases(List<Layer> layers, List<double[]> errors, double learningRate) {
		for (int i = 0; i < layers.size() - 1; i++) {
			Layer layer = layers.get(i);
			Layer nextLayer = layers.get(i + 1);
			double[] error = i > 0 ? errors.get(i - 1) : null;
			double[] nextError = errors.get(i);
			
			for (int j = 0; j < layer.size(); j++) {
				Neuron neuron = layer.get(j);
				double out = neuron.getOutput();
				
				WeightedNeuron[] targets = neuron.getNextNeurons();
				for (WeightedNeuron target : targets) {
					Weight nw = target.weight;
					int index = nextLayer.indexOf(target.neuron);
					nw.value += learningRate*nextError[index]*out;
				}
				
				if (i > 0)
					neuron.setBias(neuron.getBias() + learningRate*error[j]);
			}
			
			if (i == layers.size() - 1) {
				for (int j = 0; j < nextLayer.size(); j++) {
					Neuron neuron = nextLayer.get(j);
					neuron.setBias(neuron.getBias() + learningRate*nextError[j]);
				}
			}
		}
	}
	
	
	/**
	 * Updating weights and biases of latent layer.
	 * @param layers list of layers including input layer.
	 * @param errors list of errors.
	 * @param learningRate learning rate.
	 */
	private void updateWeightsBiasesLatent(List<Layer> layers, List<double[]> errors, double learningRate) {
		Layer attachedLayer = getLatentAttachedLayer();
		if (attachedLayer == null) return;
		
		Layer prevLatentLayer = latentLayer.getPrevLayer();
		if (prevLatentLayer == null) return;
		
		int attachedErrorIndex = -1;
		for (int i = 0; i < layers.size(); i++) {
			if (attachedLayer == layers.get(i)) {
				attachedErrorIndex = i - 1;
				break;
			}
		}
		if (attachedErrorIndex < 0) return;
		
		//Evaluating latent neurons.
		for (int j = 0; j < latentLayer.size(); j++) latentLayer.get(j).eval();

		
		//Updating errors of latent layer.
		double[] latentError = new double[latentLayer.size()];
		double[] attachedError = errors.get(attachedErrorIndex);
		for (int j = 0; j < latentLayer.size(); j++) {
			Neuron latentNeuron = latentLayer.get(j);
			double out = latentNeuron.getOutput();

			double rsum = 0;
			WeightedNeuron[] targets = attachedLayer.getLatentNextNeurons(latentNeuron);
			for (WeightedNeuron target : targets) {
				int index = attachedLayer.indexOf(target.neuron);
				rsum += attachedError[index] * target.weight.value;
			}
			
			latentError[j] = out * (1-out) * rsum;
		}

		
		//Updating weights of latent layer and its previous layer.
		for (int j = 0; j < prevLatentLayer.size(); j++) {
			Neuron neuron = prevLatentLayer.get(j);
			double out = neuron.getOutput();
			
			WeightedNeuron[] targets = neuron.getNextNeurons();
			for (WeightedNeuron target : targets) {
				Weight nw = target.weight;
				int index = latentLayer.indexOf(target.neuron);
				nw.value += learningRate*latentError[index]*out;
			}
		}
		
		for (int j = 0; j < latentLayer.size(); j++) {
			Neuron latentNeuron = latentLayer.get(j);
			double out = latentNeuron.getOutput();
			
			WeightedNeuron[] targets = attachedLayer.getLatentNextNeurons(latentNeuron);
			for (WeightedNeuron target : targets) {
				Weight nw = target.weight;
				int index = attachedLayer.indexOf(target.neuron);
				nw.value += learningRate*attachedError[index]*out;
			}
			
			latentNeuron.setBias(latentNeuron.getBias() + learningRate*latentError[j]);
		}
	}

		
	/**
	 * Creating new layer.
	 * @param nNeuron number of neurons.
	 * @param prevLayer previous layer.
	 * @param nextLayer next layer.
	 * @param latentLayer latent layer.
	 * @return new layer.
	 */
	private Layer newLayer(int nNeuron, Layer prevLayer, Layer nextLayer, Layer latentLayer) {
		LayerImpl layer = new LayerImpl(activateRef, idRef);
		nNeuron = nNeuron < 0 ? 0 : nNeuron;
		for (int i = 0; i < nNeuron; i++) {
			layer.add(layer.newNeuron());
		}
		
		if (prevLayer != null) prevLayer.setNextLayer(layer);
		if (nextLayer != null) layer.setNextLayer(nextLayer);
		if (latentLayer != null) layer.setLatentLayer(latentLayer);

		return layer;
	}
	

	/**
	 * Getting non-empty layers.
	 * @return list of non-empty layers.
	 */
	protected List<Layer> getNonemptyLayers() {
		List<Layer> layers = Util.newList(0);
		
		if (latentLayer != null && latentLayer.size() > 0) layers.add(latentLayer);
		if (inputLayer != null && inputLayer.size() > 0) layers.add(inputLayer);
		
		for (Layer hiddenLayer : hiddenLayers) {
			if (hiddenLayer != null && hiddenLayer.size() > 0) layers.add(hiddenLayer);
		}
		
		if (outputLayer != null && outputLayer.size() > 0) layers.add(outputLayer);

		return layers;
	}

	
	/**
	 * Getting layer that attaches to latent layers.
	 * @return layer that attaches to latent layers.
	 */
	protected Layer getLatentAttachedLayer() {
		if (latentLayer == null || latentLayer.size() == 0) return null;

		List<Layer> layers = getNonemptyLayers();
		for (Layer layer : layers) {
			if (layer == inputLayer || layer == outputLayer || layer == latentLayer)
				continue;
			
			if (layer.getLatentLayer() == latentLayer)
				return layer;
		}
		
		return null;
	}
	
	
}
