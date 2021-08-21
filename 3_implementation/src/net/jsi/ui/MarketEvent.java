package net.jsi.ui;

import java.io.Serializable;
import java.util.EventObject;

public class MarketEvent extends EventObject implements Serializable, Cloneable {

	
	private static final long serialVersionUID = 1L;


	public MarketEvent(Object source) {
		super(source);
	}


}
