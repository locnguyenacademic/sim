package net.jsi;

import java.util.List;

public class StockImpl extends StockAbstract {

	
	private static final long serialVersionUID = 1L;

	
	protected double volume = 0;
	
	
	private TakenPrice takenPrice = null;
	
	
	protected boolean committed = false;
	
	
	protected double stopLoss = 0;
	
	
	protected double takeProfit = 0;

	
	public StockImpl() {
		
	}
	
	
	public StockImpl(double volume, boolean buy, Price price) {
		super(buy, price);
		this.volume = volume;
		take(price);
	}
	
	
	protected boolean reset(double volume, Price price) {
		this.prices.clear();
		this.takenPrice = null;
		this.committed = false;
		this.volume = volume;
		take(price);
		
		return true;
	}

	
	public boolean take(Price price) {
		if (takenPrice != null || committed) return false;
		boolean notNullPrice = price != null;
		if (notNullPrice && !checkPrice(price)) return false;
		
		if (notNullPrice)
			takenPrice = new TakenPrice(price);
		else {
			Price lastPrice = getPrice();
			if (lastPrice == null)
				return false;
			else {
				takenPrice = new TakenPrice(lastPrice);
				price = lastPrice;
			}
		}
		
		if (buy)
			takenPrice.setExtra(getSpread());
		
		if (!notNullPrice)
			return true;
		else if (setPrice(price))
			return true;
		else {
			takenPrice = null;
			return false;
		}
	}
	
	
	public boolean take() {
		return take(null);
	}
	
	
	@Override
	public boolean setUnitBias(double unitBias) {
		if (!committed)
			return super.setUnitBias(unitBias);
		else
			return false;
	}


	@Override
	public boolean isCommitted() {
		return committed;
	}
	
	
	@Override
	public void setCommitted(boolean committed) {
		this.committed = committed;
	}


	public boolean isValid(long timeInterval) {
		if (takenPrice == null || prices.size() == 0 || volume <= 0)
			return false;
		else if (timeInterval <= 0)
			return true;
		else
			return (System.currentTimeMillis() - takenPrice.getTime() < timeInterval);
	}
	
	
	@Override
	public boolean setPrice(Price price) {
		if (!committed)
			return super.setPrice(price);
		else
			return false;
	}


	public Price getTakenPrice(long timeInterval) {
		if (takenPrice == null) return null;
		if (timeInterval <= 0) return takenPrice;
		
		Price lastPrice = getPrice();
		if (lastPrice == null || lastPrice.getTime() - takenPrice.getTime() > timeInterval)
			return null;
		else
			return takenPrice;
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


	@Override
	public double getMargin(long timeInterval) {
		return getTakenValue(timeInterval) * leverage;
	}
	
	
	@Override
	public double getValue(long timeInterval) {
		List<Price> prices = getPrices(timeInterval);
		if (prices.size() == 0) return 0;
		
		Price last = prices.get(prices.size() - 1);
		if (buy)
			return volume * last.get();
		else
			return volume * (last.get() + getSpread());
	}
	
	
	@Override
	public double getProfit(long timeInterval) {
		Price takenPrice = getTakenPrice(timeInterval);
		if (takenPrice == null) return 0;
		
		double takenValue = getTakenValue(timeInterval);
		return (buy ? getValue(timeInterval)-takenValue : takenValue-getValue(timeInterval)) - getFee(timeInterval);
	}
	
	
	public double getFee(long timeInterval) {
		return getTotalSwap(timeInterval) + (timeInterval == 0 ? commission : 0);
	}
	
	
	protected double getTotalSwap(long timeInterval) {
		List<Price> prices = getPrices(timeInterval);
		if (prices.size() <= 1)
			return 0;
		else {
			int days = (int) ((prices.get(prices.size()-1).getTime() - prices.get(0).getTime()) / (1000*3600*24) + 0.5);
			return days*swap*volume;
		}
	}
	
	
	@Override
	public double getVolume(long timeInterval, boolean ignoreCommitted) {
		Price takenPrice = getTakenPrice(timeInterval);
		if (takenPrice == null)
			return 0;
		else if (!ignoreCommitted || !isCommitted())
			return volume;
		else
			return 0;
	}


	@Override
	public double estimateBiasAveragePerUnit(long timeInterval) {
		List<Price> prices = getPrices(timeInterval);
		if (prices.size() == 0) return unitBias;
		double bias = 0;
		for (Price price : prices) {
			bias += (price.getHigh() - price.getLow()) / 2.0;
			bias = Math.max(bias, 0);
		}
		
		return Math.max(bias/prices.size(), unitBias);
	}


	@Override
	public boolean isBuy() {
		return buy;
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
	
	
	public double getStopLoss() {
		return stopLoss;
	}
	
	
	public void setStopLoss(double stopLoss) {
		this.stopLoss = stopLoss;
	}
	
	
	public double getTakeProfit() {
		return takeProfit;
	}
	
	
	public void setTakeProfit(double takeProfit) {
		this.takeProfit = takeProfit;
	}
	
	
	public Price newPrice(double price, double lowPrice, double highPrice, long time) {
		return new PriceImpl(price, lowPrice, highPrice, time);
	}
	
	
}
