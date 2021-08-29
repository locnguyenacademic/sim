/**
 * JSI: JAGGED STRATEGY INVESTMENT 
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: jsi.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.jsi;

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
	public boolean setUnitBias(double unitBias) {
		if (unitBias == getUnitBias()) return false;
		
		boolean ret = super.setUnitBias(unitBias);
		if (!ret) return false;
		for (Stock stock : stocks) {
			ret = ret && stock.setUnitBias(unitBias);
		}
		
		StockGroup other = getOtherGroup();
		if (other != null) other.setUnitBias(unitBias);

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
		return Math.max(n > 0 ? bias/n : 0, unitBias);
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
			StockGroup other = getOtherGroup();
			if (other != null) {
				other.setLeverage(this.getLeverage());
				other.setUnitBias(this.getUnitBias());
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
	public void setLeverage(double leverage) {
		if (leverage < 0 || leverage == this.leverage) return;
		this.leverage = leverage;
		for (Stock stock : stocks) stock.setLeverage(leverage);
		
		StockGroup other = getOtherGroup();
		if (other != null) other.setLeverage(leverage);
	}
	
	
	protected StockGroup getOtherGroup() {
		Market market = getOtherMarket();
		if (market == null)
			return null;
		else {
			Universe universe = getNearestUniverse();
			MarketImpl m = universe.c(market);
			StockGroup other = m.get(code(), isBuy());
			return other != null && other != this ? other : null;
		}
	}
	
	
	private Market getOtherMarket() {
		Market thisMarket = getSuperMarket();
		if (thisMarket == null) return null;
		
		Universe universe = getNearestUniverse();
		if (universe == null) return null;
		
		Market market = universe.get(thisMarket.getName());
		if (market == null) return null;
		
		Market placedMarket = universe.getPlacedMarket(thisMarket.getName());
		if (placedMarket == null || placedMarket == market) return null;
		
		return thisMarket == market ? placedMarket : (thisMarket == placedMarket ? market : null);
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
