/**
 * SIM: MACHINE LEARNING ALGORITHMS FRAMEWORK
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: sim.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.ea.ann;

import java.util.EventObject;

/**
 * This class is an implementation of information event for neural network.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class NetworkInfoEventImpl extends EventObject implements NetworkInfoEvent {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Information.
	 */
	protected String info = "";
	
	
	/**
	 * Constructor with information.
	 * @param source source of this event.
	 * @param info specified information.
	 */
	public NetworkInfoEventImpl(Object source, String info) {
		super(source);
		if (info != null) this.info = info;
	}

	
	@Override
	public String getInfo() {
		return info;
	}

	
	@Override
	public void setInfo(String info) {
		this.info = info;
	}

	
}
