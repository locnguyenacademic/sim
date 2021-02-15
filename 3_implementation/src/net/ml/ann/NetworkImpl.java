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

import net.hudup.core.logistic.LogUtil;

/**
 * This class is default implementation of neural network.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class NetworkImpl implements Network {


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
		 * Memory layer.
		 */
		memory,
		
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
	 * Memory layer.
	 */
	protected Layer memoryLayer = null;
	
	
    /**
     * Flag to indicate whether algorithm learning process was started.
     */
    protected volatile boolean doStarted = false;
    
    
    /**
     * Flag to indicate whether algorithm learning process was paused.
     */
    protected volatile boolean doPaused = false;

    
	/**
	 * Default constructor.
	 */
	protected NetworkImpl() {
		
	}
	
	
	/**
	 * Constructor with number of neurons.
	 * @param activateRef activation function.
	 * @param nInputNeuron number of input neurons.
	 * @param nOutputNeuron number of output neurons.
	 * @param nHiddenLayer number of hidden layers.
	 * @param nHiddenNeuron number of hidden neurons.
	 * @param nMemoryNeuron number of memory neurons.
	 */
	public NetworkImpl(Function activateRef, int nInputNeuron, int nOutputNeuron, int nHiddenLayer, int nHiddenNeuron, int nMemoryNeuron) {
		nInputNeuron = nInputNeuron < 1 ? 1 : nInputNeuron;
		nOutputNeuron = nOutputNeuron < 1 ? 1 : nOutputNeuron;
		nHiddenLayer = nHiddenLayer < 0 ? 0 : nHiddenLayer;
		if (nHiddenLayer == 0) nHiddenNeuron = 0;
		nHiddenNeuron = nHiddenNeuron < 0 ? 0 : nHiddenNeuron;
		nMemoryNeuron = nMemoryNeuron < 0 ? 0 : nMemoryNeuron;
		
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
		
		if (nMemoryNeuron > 0 && nHiddenNeuron > 0) {
			this.memoryLayer = newLayer(nMemoryNeuron, this.outputLayer, this.hiddenLayers.get(0), null);
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
	public NetworkImpl(Function activateRef, int nInputNeuron, int nOutputNeuron, int nHiddenLayer, int nHiddenNeuron) {
		this(activateRef, nInputNeuron, nOutputNeuron, nHiddenLayer, nHiddenNeuron, 0);
	}
	
	
	/**
	 * Constructor with number of neurons.
	 * @param activateRef activation function.
	 * @param nInputNeuron number of input neurons.
	 * @param nOutputNeuron number of output neurons.
	 */
	public NetworkImpl(Function activateRef, int nInputNeuron, int nOutputNeuron) {
		this(activateRef, nInputNeuron, nOutputNeuron, 0, 0, 0);
	}

	
	/**
	 * Constructor with number of neurons.
	 * @param nInputNeuron number of input neurons.
	 * @param nOutputNeuron number of output neurons.
	 * @param nHiddenLayer number of hidden layers.
	 * @param nHiddenNeuron number of hidden neurons.
	 * @param nMemoryNeuron number of memory neurons.
	 */
	public NetworkImpl(int nInputNeuron, int nOutputNeuron, int nHiddenLayer, int nHiddenNeuron, int nMemoryNeuron) {
		this(new LogisticFunction(), nInputNeuron, nOutputNeuron, nHiddenLayer, nHiddenNeuron, nMemoryNeuron);
	}

	
	/**
	 * Constructor with number of neurons.
	 * @param nInputNeuron number of input neurons.
	 * @param nOutputNeuron number of output neurons.
	 * @param nHiddenLayer number of hidden layers.
	 * @param nHiddenNeuron number of hidden neurons.
	 */
	public NetworkImpl(int nInputNeuron, int nOutputNeuron, int nHiddenLayer, int nHiddenNeuron) {
		this(new LogisticFunction(), nInputNeuron, nOutputNeuron, nHiddenLayer, nHiddenNeuron, 0);
	}
	
	
	/**
	 * Constructor with number of neurons.
	 * @param nInputNeuron number of input neurons.
	 * @param nOutputNeuron number of output neurons.
	 */
	public NetworkImpl(int nInputNeuron, int nOutputNeuron) {
		this(new LogisticFunction(), nInputNeuron, nOutputNeuron, 0, 0, 0);
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
		if (latentLayer != null) layer.setRiboutLayer(latentLayer);

		return layer;
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
		
		if (memoryLayer != null && layer == memoryLayer)
			return LayerType.memory;

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
	 * Getting memory layer.
	 * @return memory layer.
	 */
	public Layer getMemoryLayer() {
		return memoryLayer;
	}

	
	/**
	 * Getting backbone which is main layers.
	 * @return backbone which is main layers.
	 */
	public List<Layer> getBackbone() {
		List<Layer> backbone = Util.newList(2);
		if (inputLayer == null || outputLayer == null || inputLayer.size() > 0 || outputLayer.size() > 0)
			return backbone;
		
		backbone.add(inputLayer);
		if (hiddenLayers.size() > 0) backbone.addAll(hiddenLayers);
		backbone.add(outputLayer);
		
		return backbone;
	}
	
	
	/**
	 * Getting list of input rib bones.
	 * @return list of input rib bones.
	 */
	public List<List<Layer>> getRibinbones() {
		List<List<Layer>> ribbones = Util.newList(0);
		
		List<Layer> backbone = getBackbone();
		for (Layer layer : backbone) {
			//if (layer == inputLayer) continue;
			Layer ribLayer = layer.getRibinLayer();
			if (ribLayer == null || ribLayer.size() == 0) continue;
			
			List<Layer> ribbone = Util.newList(0);
			ribbones.add(ribbone);
			ribbone.add(0, layer);
			while (ribLayer != null) {
				ribbone.add(0, ribLayer);
				ribLayer = ribLayer.getPrevLayer();
			}
		}
		
		return ribbones;
	}
	
	
	/**
	 * Getting list of output rib bones.
	 * @return list of output rib bones.
	 */
	public List<List<Layer>> getRiboutbones() {
		List<List<Layer>> ribbones = Util.newList(0);
		
		List<Layer> backbone = getBackbone();
		for (Layer layer : backbone) {
			//if (layer == inputLayer) continue;
			Layer ribLayer = layer.getRiboutLayer();
			if (ribLayer == null || ribLayer.size() == 0) continue;
			
			List<Layer> ribbone = Util.newList(0);
			ribbones.add(ribbone);
			ribbone.add(layer);
			while (ribLayer != null) {
				ribbone.add(ribLayer);
				ribLayer = ribLayer.getNextLayer();
			}
		}
		
		return ribbones;
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
	
	
	/**
	 * Getting non-empty layers.
	 * @return list of non-empty layers.
	 */
	private List<Layer> getNonemptyLayers() {
		List<Layer> layers = Util.newList(0);
		List<Layer> backbone = getBackbone();
		
		backbone.add(inputLayer);
		backbone.addAll(hiddenLayers);
		backbone.add(outputLayer);
		backbone.add(memoryLayer);
		
		for (Layer layer : backbone) {
			if (layer == null || layer.size() == 0) continue;
			
			layers.add(layer);
			
			Layer ribLayer = layer.getRibinLayer();
			while (ribLayer != null) {
				if (ribLayer.size() > 0) layers.add(ribLayer);
				ribLayer = ribLayer.getPrevLayer();
			}
			
			ribLayer = layer.getRiboutLayer();
			while (ribLayer != null) {
				if (ribLayer.size() > 0) layers.add(ribLayer);
				ribLayer = ribLayer.getNextLayer();
			}
		}
		
		return layers;
	}

	
	@Override
	public synchronized double[] eval(double[] mainInput, List<double[]> auxInputs) throws RemoteException {
		List<List<Layer>> ribbones = getRibinbones();
		int nRib = 0;
		if (ribbones != null && ribbones.size() > 0 && auxInputs != null && auxInputs.size() > 0)
			nRib = Math.min(ribbones.size(), auxInputs.size());
		for (int i = 0; i < nRib; i++) {
			eval(ribbones.get(i), auxInputs.get(i));
		}

		return eval(getBackbone(), mainInput);
	}


	/**
	 * Evaluating backbone with specified input.
	 * @param bone list of layers including input layer.
	 * @param input specified input.
	 * @return evaluated output.
	 */
	private static double[] eval(List<Layer> bone, double[] input) {
		if (bone.size() == 0) return null;
		
		if (input == null || input.length == 0) {
			input = new double[bone.size()];
			for (int j = 0; j < input.length; j++) input[j] = 0;
		}
		else if (input.length < bone.size()) {
			input = Arrays.copyOfRange(input, 0, bone.size());
		}

		for (int i = 0; i < bone.size(); i++) {
			Layer layer = bone.get(i);
			
			if (i == 0) {
				for (int j = 0; j < layer.size(); j++) {
					Neuron neuron = layer.get(j);
					neuron.setInput(input[i]);
					neuron.setOutput(input[i]);
				}
			}
			else {
				for (int j = 0; j < layer.size(); j++) {
					Neuron neuron = layer.get(j);
					neuron.eval();
				}
			}
		}
		
		Layer outputLayer = bone.get(bone.size() - 1);
		double[] output = new double[outputLayer.size()];
		for (int j = 0; j < output.length; j++) {
			output[j] = outputLayer.get(j).getOutput();
		}
		return output;
	}
	
	
	@Override
	public synchronized double[] learn(double learningRate, double errorThreshold, int maxIteration, Collection<double[]> mainSample, List<Collection<double[]>> auxSamples) throws RemoteException {
		if (mainSample == null || mainSample.size() == 0) return null;
		errorThreshold = errorThreshold < 0 ? 0 : errorThreshold;
		maxIteration = maxIteration < 0 ? 0 : maxIteration;
		
		List<Layer> backbone = getBackbone();
		if (backbone.size() < 2) return null;
		List<List<Layer>> ribbones = getRibinbones();
		int nRib = 0;
		if (ribbones != null && ribbones.size() > 0 && auxSamples != null && auxSamples.size() > 0)
			nRib = Math.min(ribbones.size(), auxSamples.size());
		
		double[] error = null;
		int iteration = 0;
		doStarted = true;
		while (doStarted && (maxIteration <= 0 || iteration < maxIteration)) {
			for (int i = 0; i < nRib; i++) {
				List<Layer> ribbon = ribbones.get(i);
				Collection<double[]> auxSample = auxSamples.get(i);
				if (ribbon != null && ribbon.size() >= 2 && auxSample != null && auxSample.size() > 0)
					learn(auxSample, ribbon, null, learningRate);
			}
			
			error = learn(mainSample, backbone, memoryLayer, learningRate);

			iteration ++;

			if (error == null || error.length == 0)
				doStarted = false;
			else {
				double errorMean = 0;
				for (double r : error) errorMean += Math.abs(r);
				errorMean = errorMean / error.length;
				
				if (errorMean < errorThreshold) doStarted = false; 
			}
			
			synchronized (this) {
				while (doPaused) {
					notifyAll();
					try {
						wait();
					} catch (Exception e) {LogUtil.trace(e);}
				}
			}

		}
		
		synchronized (this) {
			doStarted = false;
			doPaused = false;
			
			notifyAll();
		}
		
		return error;
	}

	
	/**
	 * Learning the backbone as neural network.
	 * @param sample sample includes input and output.
	 * @param bone list of layers including input layer.
	 * @param memoryLayer memory layer.
	 * @param learningRate learning rate.
	 * @return output error.
	 * @throws RemoteException if any error raises.
	 */
	private static double[] learn(Collection<double[]> sample, List<Layer> bone, Layer memoryLayer, double learningRate) {
		List<Layer> layers = Util.newList(bone.size());
		layers.addAll(bone);
		if (memoryLayer != null) layers.add(memoryLayer);
		for (Layer layer : layers) {
			for (int j = 0; j < layer.size(); j++) {
				Neuron neuron = layer.get(j);
				neuron.setInput(0);
				neuron.setOutput(0);
				
				if (layer.getRibinLayer() == null && layer.getRiboutLayer() == null)
					neuron.setBias(0);
			}
		}
		
		int nInput = bone.get(0).size();
		int nOutput = bone.get(bone.size() - 1).size();
		List<double[]> errors = Util.newList(0);
		for (double[] record : sample) {
			if (record == null || nInput > record.length) continue;
			double[] input = Arrays.copyOfRange(record, 0, nInput);
			double[] output = Arrays.copyOfRange(record, nInput, nInput + nOutput);
			
			//Calculating outputs.
			eval(bone, input);
			
			//Calculating errors.
			errors = calcErrors(bone, output);
			
			//Updating weights and biases.
			updateWeightsBiases(bone, errors, learningRate);
			
			//Updating weights and biases related to memory layer.
			if (memoryLayer != null && memoryLayer.size() > 0)
				updateWeightsBiasesTriple(memoryLayer, bone, errors, learningRate);
		}
		
		return errors.size() > 0 ? errors.get(errors.size() - 1) : null;
	}
	
	
	/**
	 * Calculating errors.
	 * @param bone list of layers including input layer.
	 * @param errors list of errors. Error list excludes input error and so it is 1 less than backbone. 
	 * @return list of errors.
	 */
	private static List<double[]> calcErrors(List<Layer> bone, double[] output) {
		List<double[]> errors = Util.newList(0);
		
		for (int i = bone.size() - 1; i >= 1; i--) {
			Layer layer = bone.get(i);
			Layer nextLayer = i < bone.size() - 1 ? bone.get(i + 1) : null;
			double[] error = new double[layer.size()];
			errors.add(0, error);
			
			for (int j = 0; j < layer.size(); j++) {
				Neuron neuron = layer.get(j);
				double out = neuron.getOutput();
				
				if (i == bone.size() - 1)
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
	 * @param bone list of layers including input layer.
	 * @param errors list of errors. Error list excludes input error and so it is 1 less than backbone. 
	 * @param learningRate learning rate.
	 */
	private static void updateWeightsBiases(List<Layer> bone, List<double[]> errors, double learningRate) {
		for (int i = 0; i < bone.size() - 1; i++) {
			Layer layer = bone.get(i);
			Layer nextLayer = bone.get(i + 1);
			double[] error = i > 0 ? errors.get(i - 1) : null;
			double[] nextError = errors.get(i);
			
			for (int j = 0; j < layer.size(); j++) {
				Neuron neuron = layer.get(j);
				double out = neuron.getOutput();
				
				WeightedNeuron[] targets = i == 0 ? neuron.getNextNeurons(nextLayer) : neuron.getNextNeurons();
				for (WeightedNeuron target : targets) {
					Weight nw = target.weight;
					int index = nextLayer.indexOf(target.neuron);
					nw.value += learningRate*nextError[index]*out;
				}
				
				if (i > 0)
					neuron.setBias(neuron.getBias() + learningRate*error[j]);
			}
			
			if (i == bone.size() - 1) {
				for (int j = 0; j < nextLayer.size(); j++) {
					Neuron neuron = nextLayer.get(j);
					neuron.setBias(neuron.getBias() + learningRate*nextError[j]);
				}
			}
		}
	}
	
	
	/**
	 * Update weights and biases of the triple including center layer.
	 * @param centerLayer specified center layer.
	 * @param bone list of layers including input layer.
	 * @param errors list of errors. Error list excludes input error and so it is 1 less than backbone. 
	 * @param learningRate learning rate.
	 */
	private static void updateWeightsBiasesTriple(Layer centerLayer, List<Layer> bone, List<double[]> errors, double learningRate) {
		Layer prevLayer = centerLayer.getPrevLayer();
		if (prevLayer == null) return;
		Layer nextLayer = centerLayer.getNextLayer();
		if (nextLayer == null) return;
		
		int nextErrorIndex = -1;
		for (int i = 0; i < bone.size(); i++) {
			if (nextLayer == bone.get(i)) {
				nextErrorIndex = i - 1;
				break;
			}
		}
		if (nextErrorIndex < 0) return;
		
		//Evaluating center neurons.
		for (int j = 0; j < centerLayer.size(); j++) centerLayer.get(j).eval();
		
		//Updating errors of center layer.
		double[] centerError = new double[centerLayer.size()];
		double[] nextError = errors.get(nextErrorIndex);
		for (int j = 0; j < centerLayer.size(); j++) {
			Neuron centerNeuron = centerLayer.get(j);
			double out = centerNeuron.getOutput();

			double rsum = 0;
			WeightedNeuron[] targets = centerNeuron.getNextNeurons();
			for (WeightedNeuron target : targets) {
				int index = nextLayer.indexOf(target.neuron);
				rsum += nextError[index] * target.weight.value;
			}
			
			centerError[j] = out * (1-out) * rsum;
		}
		
		List<Layer> newBackbone = Util.newList(3);
		newBackbone.add(prevLayer);
		newBackbone.add(centerLayer);
		newBackbone.add(nextLayer);
		List<double[]> newErrors = Util.newList(2);
		newErrors.add(centerError);
		newErrors.add(nextError);
		
		updateWeightsBiases(newBackbone, newErrors, learningRate);
	}
	
	
}
