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

	
	private void setExtraForTakenPrice(TakenPrice takenPrice) {
		if (buy) {
			takenPrice.setExtra(property.spread);
		}
		else {
			
		}
	}
	
	
	public boolean take(long timeInterval, long takenTimePoint) {
		if (committed) return false;
		Price price = getPrice(timeInterval, takenTimePoint);
		if (price == null) return false;
		takenPrice = new TakenPrice(price);
		
		setExtraForTakenPrice(takenPrice);

		return true;
	}
	
	
	public boolean take(Price price) {
		if (committed) return false;
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
		
		setExtraForTakenPrice(takenPrice);
		
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
			return volume * (last.get() + property.spread);
	}
	
	
	@Override
	public double getProfit(long timeInterval) {
		Price takenPrice = getTakenPrice(timeInterval);
		if (takenPrice == null) return 0;
		
		double takenValue = getTakenValue(timeInterval);
		return (buy ? getValue(timeInterval)-takenValue : takenValue-getValue(timeInterval)) - getFee(timeInterval);
	}
	
	
	public double getFee(long timeInterval) {
		return getTotalSwap(timeInterval) + property.commission; // + (timeInterval == 0 ? property.commission : 0);
	}
	
	
	protected double getTotalSwap(long timeInterval) {
		List<Price> prices = getPrices(timeInterval);
		if (prices.size() <= 1)
			return 0;
		else {
			int days = (int) ((prices.get(prices.size()-1).getTime() - prices.get(0).getTime()) / (1000*3600*24) + 0.5);
			return days*property.swap*volume;
		}
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


}
