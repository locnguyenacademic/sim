/**
 * JSI: JAGGED STRATEGY INVESTMENT 
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: jsi.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.jsi.adapter;

import net.jsi.Universe;
import net.jsi.UniverseRemoteImpl;

/**
 * This class represents a remote universe for application creator.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
@Deprecated
public class UniverseRemoteForAppor extends UniverseRemoteImpl {

	
	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Constructor with universe.
	 * @param universe specified universe.
	 */
	public UniverseRemoteForAppor(Universe universe) {
		super(universe);
	}

	
	@Override
	protected boolean isAdminAccount() {
		return true;
	}

	
}
