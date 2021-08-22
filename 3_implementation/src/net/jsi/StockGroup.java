package net.jsi;

import java.util.List;

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
			if (!stock.isCommitted()) stock.setPrice(price);
		}
		return ret;
	}


	public int size() {
		return stocks.size();
	}
	
	
	public Stock get(int index) {
		return stocks.get(index);
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

	
	public Stock add(long timeInterval, double volume) {
		if (prices.size() == 0) return null;
		StockImpl stock = (StockImpl) newStock(volume);
		if (stock.isValid(timeInterval) && stocks.add(stock))
			return stock;
		else
			return null;
	}
	
	
	protected Stock newStock(double volume) {
		StockImpl stock = new StockImpl();
		stock.setBasicInfo(this);
		stock.volume = volume;
		stock.take();
		
		return stock;
	}
	
	
	public Stock remove(int index) {
		return stocks.remove(index);
	}
	
	
	public Stock set(int index, Stock stock) {
		return stocks.set(index, stock);
	}
	
	
	@Override
	public StockImpl c(Stock stock) {
		if (stock instanceof StockImpl)
			return (StockImpl)stock;
		else
			return null;
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
	public String name() {
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
	public Market getSuperMarket() {
		return null;
	}


	@Override
	public void setLeverage(double leverage) {
		if (leverage < 0 || leverage == this.leverage) return;
		this.leverage = leverage;
		for (Stock stock : stocks) stock.setLeverage(leverage);
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
