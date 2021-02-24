/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.ea.ann;

import java.io.Serializable;

/**
 * This class represents a pair of neuron and associated weight.
 * @author Loc Nguyen
 * @version 1.0
 */
public class WeightedNeuron implements Serializable, Cloneable {


	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Neuron.
	 */
	public Neuron neuron = null;
	
	
	/**
	 * Associated weight.
	 */
	public Weight weight = new Weight(0);
	
	
	/**
	 * Constructor with specified neuron and associated weight.
	 * @param neuron specified neuron.
	 * @param weight associated weight.
	 */
	public WeightedNeuron(Neuron neuron, Weight weight) {
		this.neuron = neuron;
		this.weight = weight;
	}
	
	
}

