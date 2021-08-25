/**
 * JSI: JAGGED STRATEGY INVESTMENT 
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: jsi.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.jsi.ui;

import java.io.Serializable;
import java.util.EventObject;

public class MarketEvent extends EventObject implements Serializable, Cloneable {

	
	private static final long serialVersionUID = 1L;


	public MarketEvent(Object source) {
		super(source);
	}


}
