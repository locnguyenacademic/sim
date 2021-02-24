/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.ea.ann;

import java.io.Serializable;
import java.util.Map;

/**
 * This class is sample record for learning neural network.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class Record implements Serializable, Cloneable {


	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Backbone input.
	 */
	public double[] input = null;
	
	
	/**
	 * Backbone output.
	 */
	public double[] output = null;
	
	
	/**
	 * Rib input.
	 */
	public Map<Integer, double[]> ribInput = Util.newMap(0);
	
	
	/**
	 * Rib output.
	 */
	public Map<Integer, double[]> ribOutput = Util.newMap(0);

	
	/**
	 * Default constructor.
	 */
	public Record() {

	}

	
}
