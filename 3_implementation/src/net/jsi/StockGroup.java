/**
 * JSI: JAGGED STRATEGY INVESTMENT 
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: jsi.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.jsi;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class StockGroup extends StockAbstract implements Market {

	
	private static final long serialVersionUID = 1L;

	
	protected List<Stock> stocks = Util.newList(0);

	
	public StockGroup(String code, boolean buy, double leverage, double unitBias, Price price) {
		super(buy, null);
		this.code = code;
		this.leverage = leverage;
		this.unitBias = unitBias;
		if (price == null) price = new PriceImpl();
		setPrice(price);
	}

	
	@Override
	public double getBalance(long timeInterval) {
		double profit = 0;
		for (Stock stock : stocks) {
			if (stock.isCommitted())
				profit += stock.getProfit(timeInterval) + stock.getValue(timeInterval);
			else
				profit -= stock.getProfit(timeInterval) + stock.getValue(timeInterval);
		}
		
		return profit;
	}


	@Override
	public double getTakenValue(long timeInterval) {
		double value = 0;
		for (Stock stock : stocks) {
			if (!stock.isCommitted()) value += stock.getTakenValue(timeInterval);
		}
		return value;
	}


	@Override
	public double getAverageTakenPrice(long timeInterval) {
		double value = 0;
		int n = 0;
		for (Stock stock : stocks) {
			if (!stock.isCommitted()) {
				value += stock.getTakenValue(timeInterval);
				n++;
			}
		}
		
		return n > 0 ? value/n : 0;
	}


	@Override
	public double getMargin(long timeInterval) {
		double margin = 0;
		for (Stock stock : stocks) {
			if (!stock.isCommitted()) margin += stock.getMargin(timeInterval);
		}
		return margin;
	}
	
	
	@Override
	public double getFreeMargin(long timeInterval) {
		return getBalance(timeInterval) + getProfit(timeInterval) - getMargin(timeInterval);
	}


	@Override
	public double getValue(long timeInterval) {
		double profit = 0;
		for (Stock stock : stocks) {
			if (!stock.isCommitted()) profit += stock.getValue(timeInterval);
		}
		return profit;
	}


	@Override
	public double getProfit(long timeInterval) {
		double profit = 0;
		for (Stock stock : stocks) {
			if (!stock.isCommitted()) profit += stock.getProfit(timeInterval);
		}
		return profit;
	}

	
	@Override
	public double getPositiveROISum(long timeInterval) {
		double sum = 0;
		for (Stock stock : stocks) {
			double roi = !stock.isCommitted() ? stock.getROI(timeInterval) : 0;
			if (roi > 0) sum += roi;
		}
		return sum;
	}


	@Override
	public boolean setUnitBias(double unitBias, boolean cascade) {
		if (unitBias == getUnitBias()) return false;
		
		boolean ret = super.setUnitBias(unitBias, cascade);
		if (!ret) return false;
		for (Stock stock : stocks) {
			ret = ret && stock.setUnitBias(unitBias, cascade);
		}
		
		if (cascade) {
			StockGroup other = getDualGroup();
			if (other != null) other.setUnitBias(unitBias, false);
		}
		
		return ret;
	}


	@Override
	public double getVolume(long timeInterval, boolean countCommitted) {
		double volume = 0;
		for (Stock stock : stocks) {
			if (countCommitted || !isCommitted())
				volume += stock.getVolume(timeInterval, countCommitted);
		}
		
		return volume;
	}
	
	
	@Override
	public double estimateUnitBias(long timeInterval) {
		double bias = 0;
		int n = 0;
		for (Stock stock : stocks) {
			if (!isCommitted()) {
				bias += stock.estimateUnitBias(timeInterval);
				n++;
			}
		}
		return Math.max(n > 0 ? bias/n : 0, getUnitBias());
	}


	@Override
	public double calcTotalBias(long timeInterval) {
		return estimateUnitBias(timeInterval) * getVolume(timeInterval, false);
	}


	@Override
	public double calcInvestAmount(long timeInterval) {
		return getFreeMargin(timeInterval) - calcTotalBias(timeInterval);
	}


	@Override
	public boolean setPrice(Price price) {
		boolean ret = super.setPrice(price);
		if (!ret) return false;
		for (Stock stock : stocks) {
			if (stock.isCommitted()) continue;
			
			StockImpl s = c(stock);
			if (s != null && s.prices == this.prices)
				continue;
			else
				stock.setPrice(price);
		}
		return ret;
	}


	public int size() {
		return stocks.size();
	}
	
	
	public Stock get(int index) {
		return stocks.get(index);
	}
	
	
	public Stock get(long timeInterval, long takenTimePoint) {
		int index = lookup(timeInterval, takenTimePoint);
		if (index >= 0)
			return get(index);
		else
			return null;
	}
	
	
	public Stock get(long takenTimePoint) {
		return get(0, takenTimePoint);
	}

		
	public int lookup(long timeInterval, long takenTimePoint) {
		for (int i = 0; i < stocks.size(); i++) {
			Stock stock = stocks.get(i);
			if (stock instanceof StockImpl) {
				if (((StockImpl)stock).getTakenTimePoint(timeInterval) == takenTimePoint) return i;
			}
		}
		
		return -1;
	}
	
	
	public int lookup(long takenTimePoint) {
		return lookup(0, takenTimePoint);
	}
	
	
	public List<Stock> getStocks(long timeInterval) {
		List<Stock> list = Util.newList(0);
		for (Stock stock : stocks) {
			if (stock instanceof StockImpl) {
				Price takenPrice = ((StockImpl)stock).getTakenPrice(timeInterval);
				if (takenPrice != null) list.add(stock);
			}
		}
		
		return list;
	}

	
	public boolean containsStocks(long timeInterval) {
		for (Stock stock : stocks) {
			if (stock instanceof StockImpl) {
				Price takenPrice = ((StockImpl)stock).getTakenPrice(timeInterval);
				if (takenPrice != null) return true;
			}
		}
		
		return false;
	}
	
	
	public Stock add(long timeInterval, long takenTimePoint, double volume) {
		if (prices.size() == 0) return null;
		StockImpl stock = (StockImpl) newStock(timeInterval, takenTimePoint, volume);
		if (stock == null) return null;
		
		if (stock.isValid(timeInterval) && stocks.add(stock)) {
			StockGroup other = getDualGroup();
			if (other != null) {
				other.setLeverage(this.getLeverage(), false);
				other.setUnitBias(this.getUnitBias(), false);
			}
			
			return stock;
		}
		else
			return null;
	}
	
	
	public Stock newStock(long timeInterval, long takenTimePoint, double volume) {
		StockGroup group = this;
		StockImpl stock = new StockImpl() {
			private static final long serialVersionUID = 1L;

			@Override
			protected StockGroup getGroup() {
				return group;
			}
		};
		
		stock.setBasicInfo(this);
		stock.volume = volume;
		
		if (takenTimePoint <= 0)
			return stock.take() ? stock : null;
		else
			return stock.take(timeInterval, takenTimePoint) ? stock : null;
	}
	
	
	public Stock remove(int index) {
		return stocks.remove(index);
	}
	
	
	protected boolean remove(Stock stock) {
		return stocks.remove(stock);
	}
	
	
	public Stock set(int index, Stock stock) {
		return stocks.set(index, stock);
	}
	
	
	@Override
	public StockImpl c(Stock stock) {
		if (stock instanceof StockImpl)
			return (StockImpl)stock;
		else {
			MarketImpl m = m();
			return m != null ? m.c(stock) : null;
		}
	}
	
	
	@Override
	public boolean isBuy() {
		return buy;
	}


	@Override
	public boolean isCommitted() {
		if (stocks.size() == 0) return false;
		for (Stock stock : stocks) {
			if (!stock.isCommitted()) return false;
		}
		
		return true;
	}

	
	@Override
	public void setCommitted(boolean committed) {
		for (Stock stock : stocks) stock.setCommitted(committed);
	}


	@Override
	public String getName() {
		return code();
	}


	@Override
	public long getTimeViewInterval() {
		Market market = getSuperMarket();
		if (market == null)
			return 0;
		else
			return market.getTimeViewInterval();
	}


	@Override
	public long getTimeValidInterval() {
		Market market = getSuperMarket();
		if (market == null)
			return 0;
		else
			return market.getTimeValidInterval();
	}


	@Override
	public Market getSuperMarket() {
		return null;
	}


	@Override
	public void setLeverage(double leverage, boolean cascade) {
		if (leverage < 0 || leverage == this.leverage) return;
		this.leverage = leverage;
		for (Stock stock : stocks) stock.setLeverage(leverage, cascade);
		
		if (cascade) {
			StockGroup other = getDualGroup();
			if (other != null) other.setLeverage(leverage, false);
		}
	}
	
	
	@Override
	protected StockGroup getDualGroup() {
		Market thisMarket = getSuperMarket();
		if (thisMarket == null) return null;
		
		Market dualMarket = thisMarket.getDualMarket();
		if (dualMarket == null) {
			Universe u = getNearestUniverse();
			if (u == null) return null;
			
			Market market = u.get(thisMarket.getName());
			if (market == null) return null;
			
			Market placedMarket = u.getPlacedMarket(thisMarket.getName());
			if (placedMarket == null || placedMarket == market) return null;
			
			dualMarket = thisMarket == market ? placedMarket : (thisMarket == placedMarket ? market : null);
		}
		if (dualMarket == null) return null;
		
		Universe u = dualMarket.getNearestUniverse();
		MarketImpl m = u != null ? u.c(dualMarket) : null;
		StockGroup other = m != null ? m.get(code(), isBuy()) : null;
		return other != null && other != this ? other : null;
	}
	
	
	@Override
	public Market getDualMarket() {
		return getDualGroup();
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
	
	
	@Override
	public List<String> getSupportStockCodes() {
		Universe u = getNearestUniverse();
		if (u != null)
			return u.getSupportStockCodes();
		else {
			Set<String> codes = Util.newSet(0);
			for (Stock stock : stocks) codes.add(stock.code());
			
			return Util.sort(codes);
		}
	}


	@Override
	public List<String> getDefaultStockCodes() {
		Universe u = getNearestUniverse();
		return u != null ? u.getDefaultStockCodes() : Util.newList(0);
	}

	
	@Override
	public double getStopLoss() {
		double stopLoss = 0;
		boolean visited = false;
		for (Stock stock : stocks) {
			if (stock.isCommitted()) continue;
			if (!visited) {
				stopLoss = stock.getStopLoss();
				visited = true;
			}
			else {
				stopLoss = Math.min(stopLoss, stock.getStopLoss());
			}
		}
		
		return stopLoss != Double.MAX_VALUE ? stopLoss : 0;
	}


	@Override
	public double getTakeProfit() {
		double takeProfit = 0;
		for (Stock stock : stocks) {
			if (!stock.isCommitted()) takeProfit = Math.max(takeProfit, stock.getTakeProfit());
		}
		return takeProfit;
	}


	private MarketImpl m() {
		Universe u = getNearestUniverse();
		return u != null ? u.c(getSuperMarket()) : null;
	}
	
	
	public Price newPrice(double price, double lowPrice, double highPrice, long time) {
		Universe u = getNearestUniverse();
		if (u == null) return new PriceImpl(price, lowPrice, highPrice, time);
		
		MarketImpl m = u.c(getSuperMarket());
		if (m != null) 
			return m.newPrice(price, lowPrice, highPrice, time);
		else
			return new PriceImpl(price, lowPrice, highPrice, time);
	}


	protected void addPrices(List<Price> prices, long timeInterval) {
		List<Price> newPrices = Util.newList(0);
		for (Price price : prices) {
			if (this.prices.contains(price)) continue;
			Price price0 = price instanceof TakenPrice ? ((TakenPrice)price).getPrice() : null;
			if (price0 != null && this.prices.contains(price0)) continue;
			
			newPrices.add(price);
		}
		if (newPrices.size() == 0) return;
		
		this.prices.addAll(newPrices);
		Collections.sort(this.prices, new Comparator<Price>() {
			@Override
			public int compare(Price o1, Price o2) {
				long time1 = o1.getTime();
				long time2 = o2.getTime();
				if (time1 < time2)
					return -1;
				else if (time1 == time2)
					return 0;
				else
					return 1;
			}
		});
		if (this.prices.size() == 0) return;
		
		long thisTime = getPrice().getTime();
		List<Price> removedPrices = Util.newList(0);
		for (Price price : this.prices) {
			if (isSelectAsTakenPrice(price, timeInterval))
				continue;
			else if (thisTime - price.getTime() > timeInterval)
				removedPrices.add(price);
			else
				break;
		}
		
		for (Price removedPrice : removedPrices) this.prices.remove(removedPrice);
	}
	
	
	protected boolean isSelectAsTakenPrice(Price price, long timeInterval) {
		if (price == null) return false;
		
		MarketImpl m = m();
		for (Stock stock : stocks) {
			StockImpl s = m.c(stock);
			Price takenPrice = s != null ? s.getTakenPrice(timeInterval) : null;
			if (takenPrice != null && takenPrice.checkRefEquals(price)) return true;
		}
		
		return false;
	}
	
	
	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	
}
