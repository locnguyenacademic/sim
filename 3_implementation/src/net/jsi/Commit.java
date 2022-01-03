package net.jsi;

import java.io.Serializable;
import java.util.Date;

public class Commit implements Serializable, Cloneable {


	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;

	
	public boolean committed = false;
	
	
	public long timePoint = 0;
	
	
	public Commit(boolean committed, long timePoint) {
		this.committed = committed;
		this.timePoint = timePoint;
	}

	
	public Date getDate() {
		return new Date(timePoint);
	}
	
	
}
