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
 * Auto-increased identifier.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class Id implements Serializable, Cloneable {


	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Internal identifier.
	 */
	private int id = 0;
	
	
	/**
	 * Constructor.
	 */
	public Id() {

	}

	
	/**
	 * Increase identifier.
	 * @return increment and returning the identifier.
	 */
	public int get() {
		id++;
		return id;
	}
	
	
}
