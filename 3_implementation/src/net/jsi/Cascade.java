package net.jsi;

import java.io.Serializable;

public class Cascade implements Serializable, Cloneable {


	private static final long serialVersionUID = 1L;

	
	private boolean cascade = false;
	
	
	public Cascade(boolean cascade) {
		this.cascade = cascade;
	}
	
	
	public boolean is() {
		return cascade;
	}
	

}
