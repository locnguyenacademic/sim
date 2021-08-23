package net.jsi;

public abstract class MarketAbstract implements Market {

	
	private static final long serialVersionUID = 1L;

	
	protected String name = StockProperty.NONAME;
	
	
	protected long timeViewInterval = StockProperty.TIME_VIEW_INTERVAL;

	
	public MarketAbstract() {

	}

	
	public MarketAbstract(String name) {
		this.name = name;
	}
	
	
	@Override
	public double getFreeMargin(long timeInterval) {
		return getBalance(timeInterval) + getProfit(timeInterval) - getMargin(timeInterval);
	}


	@Override
	public double getROI(long timeInterval) {
		double takenValue = getTakenValue(timeInterval);
		return takenValue != 0 ? getProfit(timeInterval) / takenValue : 0;
	}


	@Override
	public double getROIByLeverage(long timeInterval) {
		double margin = getMargin(timeInterval);
		return margin != 0 ? getProfit(timeInterval) / margin : 0;
	}


	@Override
	public double calcInvestAmount(long timeInterval) {
		return getFreeMargin(timeInterval) - calcTotalBias(timeInterval);
	}

	
	@Override
	public String name() {
		return name;
	}


	@Override
	public long getTimeViewInterval() {
		return timeViewInterval;
	}
	
	
	public void setTimeViewInterval(long timeViewInterval) {
		this.timeViewInterval = timeViewInterval;
	}
	
	
	@Override
	public StockImpl c(Stock stock) {
		if (stock instanceof StockImpl)
			return (StockImpl)stock;
		else
			return null;
	}


	@Override
	public Market getSuperMarket() {
		return null;
	}


	@Override
	public Universe getNearestUniverse() {
		Market superMarket = this;
		if (superMarket instanceof Universe) return (Universe)superMarket;

		while ((superMarket = superMarket.getSuperMarket()) != null) {
			if (superMarket instanceof Universe) return (Universe)superMarket;
		}
		
		return null;
	}
	
	
}
