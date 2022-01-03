package net.jsi;

public class UniverseImpl extends UniverseAbstract {


	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;

	
	private static Universe g = null;
	
	
	public static Universe g() {
		return g;
	}
	
	
	public UniverseImpl() {
		if (g == null) g = this;
	}


	@Override
	public Market getSuperMarket() {
		return null;
	}


	@Override
	public Market getDualMarket() {
		return null;
	}

	
}
