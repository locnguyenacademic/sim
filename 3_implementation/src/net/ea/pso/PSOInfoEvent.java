/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.ea.pso;

import java.io.Serializable;

/**
 * This interface represents information event about PSO.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface PSOInfoEvent extends Serializable, Cloneable {

	
	/**
	 * Getting information.
	 * @return information.
	 */
	String getInfo();
	
	
	/**
	 * Setting information.
	 * @param info specified information.
	 */
	void setInfo(String info);
	
	
}
