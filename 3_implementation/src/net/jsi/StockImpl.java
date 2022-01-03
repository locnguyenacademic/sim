/**
 * JSI: JAGGED STRATEGY INVESTMENT 
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: jsi.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.jsi;

import java.util.List;

public class StockImpl extends StockAbstract {

	
	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;

	
	private double volume = 0;
	
	
	private double unitMargin = Double.NaN;
	
	
	private TakenPrice takenPrice = null;
	
	
	private Commit commit = new Commit(false, 0);
	
	
	private double stopLoss = 0;
	
	
	private double takeProfit = 0;

	
	public StockImpl(String code, double volume, boolean buy) {
		super(code, buy, null);
		this.volume = volume;
		
		take();
	}
	
	
	public boolean take(long timeInterval, long takenTimePoint, double realPrice) {
		Price price = getPrice(timeInterval, takenTimePoint);
		if (price == null) {
			takenPrice = null;
			return false;
		}
		else
			return take(price, realPrice);
	}
	
	
	public boolean take(long timeInterval, long takenTimePoint) {
		return take(timeInterval, takenTimePoint, Double.NaN);
	}
	
	
	public boolean take() {
		Price lastPrice = getPrice();
		if (lastPrice == null)
			return false;
		else
			return take(lastPrice, Double.NaN);
	}
	
	
	private boolean take(Price price, double realPrice) {
		if (isCommitted())
			return false;
		else if (price == null) {
			takenPrice = null;
			return false;
		}
		else {
			takenPrice = new TakenPrice(price, realPrice);
			setExtraForTakenPrice(takenPrice);
			return true;
		}
	}
	
	
	private void setExtraForTakenPrice(TakenPrice takenPrice) {
		if (isBuy()) {

		}
		else {
			
		}
	}
	
	
	@Override
	public boolean setUnitBias(double unitBias) {
		if (!isCommitted())
			return super.setUnitBias(unitBias);
		else
			return false;
	}


	@Override
	public boolean isCommitted() {
		return commit != null && commit.committed;
	}
	

	@Override
	public long getCommittedTimePoint() {
		if (commit == null)
			return 0;
		else
			return commit.timePoint;
	}
	
	
	@Override
	public void setCommitted(boolean committed) {
		Price price = getPrice();
		long timePoint = price != null ? price.getTime() : System.currentTimeMillis();
		setCommitted(committed, timePoint);
	}


	@Override
	public void setCommitted(boolean committed, long timePoint) {
		if (commit == null)
			commit = new Commit(committed, committed ? timePoint : 0);
		else {
			commit.committed = committed;
			commit.timePoint = committed ? timePoint : 0;
		}
	}


	public boolean isValid(long timeInterval) {
		if (takenPrice == null || getPriceCount() == 0 || volume <= 0)
			return false;
		else {
			long priceTime = getPrice().getTime();
			long takenTime = takenPrice.getTime();
			if (timeInterval <= 0)
				return priceTime >= takenTime;
			else
				return (priceTime >= takenTime) && (priceTime - takenTime <= timeInterval);
		}
	}
	
	
	@Override
	public boolean setPrice(Price price) {
		if (!isCommitted())
			return super.setPrice(price);
		else
			return false;
	}


	/*
	 * As a convention, stock price is bid price.
	 */
	public Price getTakenPrice(long timeInterval) {
		if (takenPrice == null) return null;
		if (timeInterval <= 0) return takenPrice;
		
		Price lastPrice = getPrice();
		if (lastPrice == null || lastPrice.getTime() - takenPrice.getTime() > timeInterval)
			return null;
		else
			return takenPrice;
	}
	
	
	protected double getRealTakenPrice(long timeInterval) {
		Price takenPrice = getTakenPrice(timeInterval);
		if (takenPrice != null && takenPrice instanceof TakenPrice)
			return ((TakenPrice)takenPrice).queryReal();
		else
			return Double.NaN;
	}
	
	
	@Override
	public double getTakenValue(long timeInterval) {
		Price takenPrice = getTakenPrice(timeInterval);
		return takenPrice != null ? volume * takenPrice.get() : 0;
	}
	
	
	@Override
	public double getAverageTakenPrice(long timeInterval) {
		Price takenPrice = getTakenPrice(timeInterval);
		return takenPrice != null ? takenPrice.get() : 0;
	}


	public double getAverageTakenPriceByLeverage(long timeInterval) {
		return getAverageTakenPrice(timeInterval) * getLeverage();
	}
	
	
	/*
	 * As a convention, stock price is bid price.
	 */
	private Price getTakenPricePrev(long timeInterval) {
		if (takenPrice == null || timeInterval <= 0) return null;
		
		Price lastPrice = getPrice();
		if (lastPrice == null || lastPrice.getTime() - takenPrice.getTime() <= timeInterval)
			return null;
		else
			return takenPrice;
	}
	
	
	@SuppressWarnings("unused")
	private double getTakenValuePrev(long timeInterval) {
		Price takenPrice = getTakenPricePrev(timeInterval);
		return takenPrice != null ? volume * takenPrice.get() : 0;
	}

	
	@Override
	public double getMargin(long timeInterval) {
		double takenValue = getTakenValue(timeInterval);
		if (takenValue == 0)
			return 0;
		else if (Double.isNaN(unitMargin))
			return takenValue * getLeverage();
		else
			return volume * unitMargin;
	}
	

	public double getFixedUnitMargin() {
		return unitMargin;
	}
	
	
	public void setFixedUnitMargin(double unitMargin) {
		this.unitMargin = unitMargin;
	}
	
	
	public boolean isFixedMargin() {
		return !Double.isNaN(unitMargin);
	}
	
	
	public void fixMargin(boolean fixed) {
		if (fixed) {
			if (Double.isNaN(this.unitMargin)) this.unitMargin = getMargin(0);
		}
		else if (!Double.isNaN(this.unitMargin))
			this.unitMargin = Double.NaN;
	}
	
	
	/*
	 * As a convention, stock price is bid price.
	 */
	@Override
	public double getValue(long timeInterval) {
		List<Price> prices = getPrices(timeInterval);
		if (prices.size() == 0) return 0;
		
		Price last = prices.get(prices.size() - 1);
		return volume * last.get();
	}
	
	
	/*
	 * As a convention, stock price is bid price.
	 */
	@Override
	public double getProfit(long timeInterval) {
		Price takenPrice = getTakenPrice(timeInterval);
		if (takenPrice == null) return 0;
		
		double takenValue = getTakenValue(timeInterval);
		double value = getValue(timeInterval);
		return (isBuy() ? value-takenValue : takenValue-value) - getProperty().spread - getFee(timeInterval);
	}
	
	
	protected double getFee(long timeInterval) {
		long interval = getCommittedTimePoint() - getTakenTimePoint(timeInterval);
		int days = (int) (interval / (1000*3600*24) + 0.5);
		days = days > 0 ? days : 0;

		StockProperty property = getProperty();
		return property.commission + volume*(days*property.swap + property.getExtraFee(days));
	}
	
	
	@Override
	public double getVolume(long timeInterval, boolean countCommitted) {
		Price takenPrice = getTakenPrice(timeInterval);
		if (takenPrice == null)
			return 0;
		else if (countCommitted || !isCommitted())
			return volume;
		else
			return 0;
	}


	public void setVolume(double volume) {
		this.volume = volume;
	}
	
	
	public long getTakenTimePoint(long timeInterval) {
		Price takenPrice = getTakenPrice(timeInterval);
		if (takenPrice != null)
			return takenPrice.getTime();
		else
			return 0;
	}
	
	
	@Override
	public double getStopLoss() {
		return stopLoss;
	}
	
	
	public void setStopLoss(double stopLoss) {
		this.stopLoss = stopLoss;
	}
	
	
	@Override
	public double getTakeProfit() {
		return takeProfit;
	}
	
	
	public void setTakeProfit(double takeProfit) {
		this.takeProfit = takeProfit;
	}


	@Override
	public double getDividend(long timeInterval) {
		StockProperty property = getProperty();
		if (property == null) return 0;
		long takenTimePoint = getTakenTimePoint(timeInterval);
		if (takenTimePoint <= 0) return 0;
		
		long dividendTimePoint = property.getDividendTime();
		long lastTimePoint = getPrice().getTime();
		if (isCommitted()) lastTimePoint = getCommittedTimePoint();
		if (dividendTimePoint > lastTimePoint)
			return property.getDividend() * volume;
		if (timeInterval > 0 && lastTimePoint - dividendTimePoint > timeInterval)
			return 0;
		else
			return property.getDividend() * volume;
	}
	

	@Override
	public void setExtraInfo(Stock stock) {
		setCommitted(stock.isCommitted());
		setStopLoss(stock.getStopLoss());
		setTakeProfit(stock.getTakeProfit());
		
		if (stock instanceof StockImpl) this.unitMargin = ((StockImpl)stock).unitMargin;
	}


	@Override
	public StockGroup getGroup() {
		Universe g = UniverseImpl.g();
		if (g == null) return null;
		
		for (int i = 0; i < g.size(); i++) {
			MarketImpl m = g.c(g.get(i));
			if (m == null) continue;
			
			StockGroup group = m.get(code(), isBuy());
			if (group != null) return group;
		}
		
		return null;
	}


	@Override
	public StockInfoStore getStore() {
		StockGroup group = getGroup();
		return group != null ? group.getStore() : null;
	}


	protected boolean copyRealTakenPrice(StockImpl source, long timeInterval) {
		if (source == null) return false;
		Price thisTakenPrice = this.getTakenPrice(timeInterval);
		Price sourceTakenPrice = source.getTakenPrice(timeInterval);
		if (thisTakenPrice == null || sourceTakenPrice == null)
			return false;
		else if (thisTakenPrice instanceof TakenPrice && sourceTakenPrice instanceof TakenPrice) {
			((TakenPrice)thisTakenPrice).setReal(((TakenPrice)sourceTakenPrice).queryReal());
			return true;
		}
		else
			return false;
	}


}
