package net.jsi;

public class UniverseImpl extends UniverseAbstract {


	private static final long serialVersionUID = 1L;

	
	public UniverseImpl() {
		if (StockProperty.g == null) StockProperty.g = this;
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
