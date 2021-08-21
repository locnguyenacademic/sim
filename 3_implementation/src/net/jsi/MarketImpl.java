package net.jsi;

import java.util.List;

public class MarketImpl extends MarketAbstract implements QueryEstimator {

	
	private static final long serialVersionUID = 1L;


	public final static String NONAME = "noname";

	
	protected double refLeverage = StockAbstract.LEVERAGE;
	
	
	protected double balance = 0;
	
	
	protected double marginBias = 0;
	
	
	protected double unitBias = StockAbstract.UNIT_BIAS;
	
	
	protected List<StockGroup> groups = Util.newList(0);
	
	
	public MarketImpl(String name, double refLeverage, double unitBias) {
		super(name);
		this.refLeverage = refLeverage;
		this.unitBias = unitBias;
	}
	
	
	@Override
	public double getBalance() {
		return balance;
	}

	
	public void setBalance(double balance) {
		this.balance = balance;
	}
	
	
	@Override
	public double getMargin(long timeInterval) {
		double margin = 0;
		for (StockGroup group : groups) {
			margin += group.getMargin(timeInterval);
		}
		return margin;
	}
	
	
	public void adjustMargin(double givenMargin) {
		marginBias = givenMargin > 0 ? givenMargin - getMargin(0) : 0;
	}
	
	
	public void adjustMargin() {
		marginBias = 0;
	}

	
	@Override
	public double getFreeMargin(long timeInterval) {
		return getBalance() + getProfit(timeInterval) - (getMargin(timeInterval) + (timeInterval > 0 ? 0 : marginBias));
	}
	
	
	@Override
	public double getTakenValue(long timeInterval) {
		double value = 0;
		for (StockGroup group : groups) {
			value += group.getTakenValue(timeInterval);
		}
		return value;
	}

	
	@Override
	public double getProfit(long timeInterval) {
		double profit = 0;
		for (StockGroup group : groups) {
			profit += group.getProfit(timeInterval);
		}
		return profit;
	}

	
	private double queryPositiveROISum(long timeInterval) {
		double sum = 0;
		for (StockGroup group : groups) {
			double roi = group.getROI(timeInterval);
			if (roi > 0) sum += roi;
		}
		return sum;
	}
	
	
	private double queryInvestAmount(long timeInterval) {
		double biasSum = 0;
		for (StockGroup group : groups) {
			biasSum += group.estimateUnitBias(0) * group.getVolume(0, true);
		}
		
		return getFreeMargin(timeInterval) - biasSum;
	}
	
	
	private class Estimator0 extends EstimatorAbstract {
		
		private static final long serialVersionUID = 1L;

		protected Stock stock = null;
		
		public Estimator0(Stock stock) {
			this.stock = stock;
		}
		
		@Override
		public Price getPrice() {
			return stock.getPrice();
		}

		@Override
		public double getAverageTakenPrice(long timeInterval) {
			return stock.getAverageTakenPrice(timeInterval);
		}

		@Override
		public double getLowPrice(long timeInterval) {
			return stock.getLowPrice(timeInterval);
		}

		@Override
		public double getHighPrice(long timeInterval) {
			return stock.getHighPrice(timeInterval);
		}

		@Override
		public double getUnitBias() {
			return stock.getUnitBias();
		}

		@Override
		public double getROI(long timeInterval) {
			return stock.getROI(timeInterval);
		}

		@Override
		public double getPositiveROISum(long timeInterval) {
			return queryPositiveROISum(timeInterval);
		}

		@Override
		public double getInvestAmount(long timeInterval) {
			return queryInvestAmount(timeInterval);
		}

		@Override
		public double estimateUnitBias(long timeInterval) {
			return stock.estimateUnitBias(timeInterval);
		}

		@Override
		public boolean isBuy() {
			return stock.isBuy();
		}

	}
	
	
	@Override
	public Estimator getEstimator(String code) {
		int index = lookup(code);
		if (index < 0) return null;
		StockGroup group = get(index);
		return group != null ? new Estimator0(group) : null;
	}
	
	
	public int size() {
		return groups.size();
	}
	
	
	public StockGroup get(int index) {
		return groups.get(index);
	}
	
	
	public StockGroup get(String code) {
		int index = lookup(code);
		if (index >= 0)
			return get(index);
		else
			return null;
	}
	
	
	public int lookup(String code) {
		for (int i = 0; i < groups.size(); i++) {
			StockGroup group = groups.get(i);
			if (group.code().equals(code)) return i;
		}
		
		return -1;
	}
	
	
	public boolean add(StockGroup group) {
		if (group == null || lookup(group.code()) >= 0)
			return false;
		else
			return groups.add(group);
	}
	
	
	public StockGroup remove(int index) {
		return groups.remove(index);
	}
	
	
	public StockGroup set(int index, StockGroup group) {
		if (group == null || lookup(group.code()) >= 0)
			return null;
		else {
			return groups.set(index, group);
		}
	}
	
	
	public StockGroup newGroup(String code, boolean buy, double leverage, Price price) {
		double unitBias = StockAbstract.calcMaxUnitBias(this.unitBias, leverage, this.refLeverage);
		final Market superMarket = this;
		StockGroup group = new StockGroup(code, buy, leverage, unitBias, price) {

			private static final long serialVersionUID = 1L;

			@Override
			public Market getSuperMarket() {
				return superMarket;
			}
			
		};
		group.timeViewInterval = this.timeViewInterval;
		return group;
	}

	
	public Stock lookupStock(String code, long timeInterval, long timePoint) {
		for (StockGroup group : groups) {
			if (!group.code().equals(code)) continue;
			int index = group.lookup(timeInterval, timePoint);
			if (index >= 0) return group.get(index);
		}
		
		return null;
	}
	
	
	public List<Stock> getStocks(String code, long timeInterval) {
		List<Stock> stocks = Util.newList(0);
		for (StockGroup group : groups) {
			if (!group.code().equals(code)) continue;
			stocks.addAll(group.getStocks(timeInterval));
		}

		return stocks;
	}
	
	
	public Stock addStock(String code, boolean buy, double refLeverage, double volume, Price price) {
		int found = lookup(code);
		StockGroup group = null;
		if (found < 0) {
			group = newGroup(code, buy, refLeverage <= 0 ? this.refLeverage : refLeverage, price);
			if (group == null || !add(group))
				return null;
		}
		else {
			group = get(found);
			if (!group.setPrice(price)) return null;
			if (refLeverage > 0 && refLeverage != group.getLeverage())
				group.setLeverage(refLeverage);
		}
		
		return group.add(volume);
	}
	
	
	public Stock addStock(String code, boolean buy, double volume, Price price) {
		return addStock(code, buy, 0, volume, price);
	}
	
	
	public Stock removeStock(String code, long timeInterval, long takenTimePoint) {
		int found = lookup(code);
		if (found < 0) return null;
		
		StockGroup group = get(found);
		int index = group.lookup(timeInterval, takenTimePoint);
		if (index >= 0)
			return group.remove(index);
		else
			return null;
	}
	
	
	public boolean setCommitted(Stock stock, boolean committed) {
		if (stock.isCommitted() == committed) return false;
		if (lookup(stock.code()) < 0) return false;
		
		stock.setCommitted(committed);
		if (stock.isCommitted() == committed) {
			if (stock.isCommitted())
				this.balance += stock.getProfit(0) + stock.getValue(0);
			else
				this.balance -= stock.getProfit(0) + stock.getValue(0);
			
			return true;
		}
		else
			return false;
	}
	
	
	public void setCommitted(StockGroup group, boolean committed) {
		for (Stock stock : group.stocks) setCommitted(stock, committed);
	}
	
	
	public void updateEstimatedUnitBias(StockGroup group, long timeInterval) {
		double unitBias = group.estimateUnitBias(timeInterval);
		unitBias = Math.max(unitBias, StockAbstract.calcMaxUnitBias(this.unitBias, group.getLeverage(), this.refLeverage));
		group.setUnitBias(unitBias);
	}


	@Override
	public List<Stock> getStocks(long timeInterval) {
		List<Stock> stocks = Util.newList(0);
		for (StockGroup group : groups) {
			for (Stock stock : group.stocks) {
				StockImpl si = c(stock);
				if (si != null && si.isValid(timeInterval)) stocks.add(stock);
			}
		}

		return stocks;
	}
	
	
	public double getUnitBias() {
		return unitBias;
	}
	
	
	public double getRefLeverage() {
		return refLeverage;
	}
	
	
}
