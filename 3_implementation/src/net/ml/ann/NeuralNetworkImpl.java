/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.ml.ann;

import java.rmi.RemoteException;
import java.util.List;

import net.hudup.core.Util;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.Profile;

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
		this.activateRef = activateRef;
		
		this.memoryLayer = new LayerImpl(activateRef, null, null, idRef);
		for (int i = 0; i < nMemoryNeuron; i++) {
			this.memoryLayer.add(this.memoryLayer.newNeuron());
		}
		
		this.inputLayer = new LayerImpl(activateRef, this.memoryLayer, null, idRef);
		for (int i = 0; i < nMemoryNeuron; i++) {
			this.inputLayer.add(this.inputLayer.newNeuron());
		}
		this.memoryLayer.setNextLayer(this.inputLayer);
		
		this.hiddenLayers = Util.newList(nHiddenLayer);
		for (int l = 0; l < nHiddenLayer; l++) {
			Layer prevHiddenLayer = l == 0 ? this.inputLayer : this.hiddenLayers.get(l - 1);
			Layer hiddenLayer = new LayerImpl(activateRef, prevHiddenLayer, null, idRef);
			for (int i = 0; i < nHiddenNeuron; i++) {
				hiddenLayer.add(hiddenLayer.newNeuron());
			}
			
			prevHiddenLayer.setNextLayer(hiddenLayer);
		}

		Layer lastHiddenLayer = nHiddenLayer > 0 ? this.hiddenLayers.get(this.hiddenLayers.size() - 1) : null;
		this.outputLayer = new LayerImpl(activateRef, lastHiddenLayer, null, idRef);
		for (int i = 0; i < nMemoryNeuron; i++) {
			this.outputLayer.add(this.outputLayer.newNeuron());
		}
		lastHiddenLayer.setNextLayer(this.outputLayer);
		
		this.outputLayer.setNextLayer(memoryLayer);
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

	
	@Override
	public Layer getOutputLayer() throws RemoteException {
		return outputLayer;
	}

	
	@Override
	public boolean learn(Fetcher<Profile> input, Fetcher<Profile> output) throws RemoteException {
		return false;
	}

	
}
