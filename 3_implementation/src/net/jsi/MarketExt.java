package net.jsi;

public class MarketExt extends MarketImpl {

	
	private static final long serialVersionUID = 1L;

	
	protected MarketImpl placedMarket = null;
	
	
	public MarketExt(String name, double refLeverage, double unitBias) {
		super(name, refLeverage, unitBias);
		
		MarketExt thisMarket = this;
		this.placedMarket = new MarketImpl(name, refLeverage, unitBias) {

			private static final long serialVersionUID = 1L;

			@Override
			public Market getSuperMarket() {
				return thisMarket.getSuperMarket();
			}

			@Override
			public double getBalanceBase() {
				return thisMarket.calcInvestAmount(thisMarket.getTimeViewInterval());
			}

			@Override
			public Market getDualMarket() {
				return thisMarket;
			}

		};
		placedMarket.setTimeViewInterval(getTimeViewInterval());
		placedMarket.setTimeValidInterval(getTimeValidInterval());
	}


	@Override
	public MarketImpl getPlacedMarket() {
		return placedMarket;
	}


	@Override
	public Market getDualMarket() {
		return getPlacedMarket();
	}
	
	
}
