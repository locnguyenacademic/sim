package net.jsi;

public class UniverseExt extends UniverseImpl {


	private static final long serialVersionUID = 1L;

	
	public UniverseExt() {

	}

	
	@Override
	public boolean add(Market market) {
		if (market == null || lookup(market.getName()) >= 0)
			return false;
		else
			return markets.add(market);
	}


	@Override
	public Market remove(int index) {
		return markets.remove(index);
	}
	
	
	@Override
	public Market set(int index, Market market) {
		if (market == null || lookup(market.getName()) >= 0)
			return null;
		else
			return markets.set(index, market);
	}


	@Override
	public Market newMarket(String name, double leverage, double unitBias) {
		Market superMarket = this;
		MarketExt market = new MarketExt(name, leverage, unitBias) {

			private static final long serialVersionUID = 1L;

			@Override
			public Market getSuperMarket() {
				return superMarket;
			}
			
		};
		market.setTimeViewInterval(this.getTimeViewInterval());
		market.setTimeValidInterval(this.getTimeValidInterval());
		return market;
	}


	@Override
	public Market getPlacedMarket(String name) {
		Market market = get(name);
		if (market != null && market instanceof MarketExt)
			return ((MarketExt)market).getPlacedMarket();
		else
			return null;
	}
	
	
}
