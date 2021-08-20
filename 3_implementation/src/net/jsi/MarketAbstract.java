package net.jsi;

public abstract class MarketAbstract implements Market {

	
	private static final long serialVersionUID = 1L;

	
	public final static String NONAME = "noname";
	
	
	protected String name = NONAME;
	
	
	protected long timeViewInterval = StockAbstract.TIME_VIEW_INTERVAL;

	
	public MarketAbstract() {

	}

	
	public MarketAbstract(String name) {
		this.name = name;
	}
	
	
	@Override
	public double getROI(long timeInterval) {
		double takenValue = getTakenValue(timeInterval);
		return takenValue > 0 ? getProfit(timeInterval) / takenValue : 0;
	}


	@Override
	public String name() {
		return name;
	}


	@Override
	public long getTimeViewInterval() {
		return timeViewInterval;
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
