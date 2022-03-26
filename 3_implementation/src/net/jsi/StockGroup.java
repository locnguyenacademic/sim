/**
 * JSI: JAGGED STRATEGY INVESTMENT 
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: jsi.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.jsi;

import java.util.List;

public class StockGroup extends StockAbstract implements Market {

	
	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;

	
	protected List<Stock> stocks = Util.newList(0);

	
	public StockGroup(String code, boolean buy) {
		this(code, buy, Double.NaN, Double.NaN, null);
	}

	
	public StockGroup(String code, boolean buy, double leverage, double unitBias, Price price) {
		super(code, buy, price);
		setLeverage(leverage);
		setUnitBias(unitBias);
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


	protected void fixMargin(boolean fixed) {
		for (Stock stock : stocks) {
			if (stock instanceof StockImpl) ((StockImpl)stock).fixMargin(fixed);
		}
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
	public double calcOscill(long timeInterval) {
		return getPriceOscill(timeInterval) * getVolume(timeInterval, false);
	}

	
	@Override
	public double calcInvestAmount(long timeInterval) {
		double oscill = calcOscillAbs(timeInterval);
		double minmaxDev = calcMinMaxDev(timeInterval);
		double om = Math.max(calcBias(timeInterval), Math.max(oscill, minmaxDev));
		return getFreeMargin(timeInterval) - Math.max(om, getMargin(timeInterval));
	}


	@Override
	public double calcInvestAmountRisky(long timeInterval) {
		return getFreeMargin(timeInterval) - calcBias(timeInterval);
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

		
	protected Stock get(long takenTimePoint, Stock referredStock) {
		if (referredStock == null) return get(takenTimePoint);
		
		for (int i = 0; i < stocks.size(); i++) {
			Stock stock = stocks.get(i);
			if (!stock.code().equals(referredStock.code()))
				continue;
			else if (stock.isBuy() != referredStock.isBuy())
				continue;
			else if (!(stock instanceof StockImpl))
				continue;
			else if (((StockImpl)stock).getTakenTimePoint(0) != takenTimePoint)
				continue;
			else if (!(referredStock instanceof StockImpl))
				return stock;
			else if (((StockImpl)stock).getAverageTakenPrice(0) == ((StockImpl)referredStock).getAverageTakenPrice(0))
				return stock;
		}
		
		return null;
	}
	
	
	public Stock get(long takenTimePoint, double takenPrice) {
		if (Double.isNaN(takenPrice)) return get(takenTimePoint);
		
		for (int i = 0; i < stocks.size(); i++) {
			Stock stock = stocks.get(i);
			if (!(stock instanceof StockImpl))
				continue;
			else if (((StockImpl)stock).getTakenTimePoint(0) != takenTimePoint)
				continue;
			else if (((StockImpl)stock).getAverageTakenPrice(0) == takenPrice)
				return stock;
		}
		
		return null;
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
		if (getPriceCount() == 0) return null;
		StockImpl stock = (StockImpl) newStock(timeInterval, takenTimePoint, volume);
		if (stock == null)
			return null;
		else if (get(takenTimePoint, stock.getAverageTakenPrice(0)) != null)
			return null;
		else if (stocks.add(stock))
			return stock;
		else
			return null;
	}
	
	
	private Stock newStock(long timeInterval, long takenTimePoint, double volume) {
		StockGroup group = this;
		StockImpl stock = new StockImpl(code(), volume, isBuy()) {
			
			/**
			 * Serial version UID for serializable class.
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public StockGroup getGroup() {
				return group;
			}
		};
		stock.setBasicInfo(this);
		
		if (takenTimePoint > 0) stock.take(timeInterval, takenTimePoint);
		return stock.isValid(timeInterval) ? stock : null;
	}
	
	
	public Stock remove(int index) {
		return stocks.remove(index);
	}
	
	
	public boolean remove(Stock stock) {
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
	public boolean isCommitted() {
		if (stocks.size() == 0) return false;
		for (Stock stock : stocks) {
			if (!stock.isCommitted()) return false;
		}
		
		return true;
	}

	
	@Override
	public long getCommittedTimePoint() {
		long ctp = 0;
		if (stocks.size() == 0) return ctp;
		for (Stock stock : stocks) {
			ctp = Math.max(ctp, stock.getCommittedTimePoint());
		}
		
		return ctp;
	}


	@Override
	public void setCommitted(boolean committed) {
		for (Stock stock : stocks) stock.setCommitted(committed);
	}


	@Override
	public void setCommitted(boolean committed, long timePoint) {
		for (Stock stock : stocks) stock.setCommitted(committed, timePoint);
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
	public StockGroup getDualGroup() {
		Market thisMarket = getSuperMarket();
		if (thisMarket == null) return null;
		
		Market dualMarket = thisMarket.getDualMarket();
		if (dualMarket == null) {
			Universe u = getNearestUniverse();
			if (u == null) return null;
			
			Market market = u.get(thisMarket.getName());
			if (market == null) return null;
			
			Market watchMarket = u.getWatchMarket(thisMarket.getName());
			if (watchMarket == null || watchMarket == market) return null;
			
			dualMarket = thisMarket == market ? watchMarket : (thisMarket == watchMarket ? market : null);
		}
		if (dualMarket == null) return null;
		
		Universe u = dualMarket.getNearestUniverse();
		MarketImpl m = u != null ? u.c(dualMarket) : null;
		StockGroup dual = m != null ? m.get(code(), isBuy()) : null;
		return dual != null && dual != this ? dual : null;
	}
	
	
	@Override
	public StockGroup getGroup() {
		return this;
	}


	@Override
	public Market getDualMarket() {
		return getDualGroup();
	}

	
	@Override
	protected StockGroup getOtherGroup() {
		MarketImpl m = m();
		return m != null ? m.get(code(), !isBuy()) : null;
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
			List<String> codes = Util.newList(0);
			codes.add(code());
			return codes;
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


	@Override
	public void setExtraInfo(Stock stock) {
		
	}


	@Override
	public double getDividend(long timeInterval) {
		double dividend = 0;
		for (Stock stock : stocks) {
			if (!stock.isCommitted()) dividend += stock.getDividend(timeInterval);
		}
		return dividend;
	}


	private MarketImpl m() {
		Universe u = getNearestUniverse();
		return u != null ? u.c(getSuperMarket()) : null;
	}
	
	
	@Override
	public Price newPrice(double price, double lowPrice, double highPrice, long time) {
		Market superMarket = getSuperMarket();
		if (superMarket != null) return superMarket.newPrice(price, lowPrice, highPrice, time);
		
		Universe u = getNearestUniverse();
		if (u != null)
			return u.newPrice(price, lowPrice, highPrice, time);
		else
			return UniverseAbstract.newPrice0(price, lowPrice, highPrice, time);
	}


	@SuppressWarnings("unused")
	private boolean isSelectAsTakenPrice(Price price, long timeInterval) {
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
	public StockInfoStore getStore() {
		Market superMarket = getSuperMarket();
		if (superMarket == null)
			return null;
		else if (superMarket instanceof MarketImpl)
			return ((MarketImpl)superMarket).getStore();
		else {
			Universe u = getNearestUniverse();
			MarketImpl m = u != null ? u.c(superMarket) : null;
			return m != null ? m.getStore() : null;
		}
	}


}
