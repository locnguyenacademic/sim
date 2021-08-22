package net.jsi;

import java.util.List;

public class MarketImpl extends MarketAbstract implements QueryEstimator {

	
	private static final long serialVersionUID = 1L;


	public final static String NONAME = "noname";

	
	protected double refLeverage = StockProperty.LEVERAGE;
	
	
	private double balance = 0;
	
	
	private double balanceBias = 0;

	
	private double marginBias = 0;
	
	
	protected double unitBias = StockProperty.UNIT_BIAS;
	
	
	protected List<StockGroup> groups = Util.newList(0);
	
	
	public MarketImpl(String name, double refLeverage, double unitBias) {
		super(name);
		this.refLeverage = refLeverage;
		this.unitBias = unitBias;
	}
	
	
	private double getBalance0(long timeInterval) {
		double profit = 0;
		for (StockGroup group : groups) {
			List<Stock> stocks = group.getStocks(timeInterval);
			for (Stock stock : stocks) {
				if (stock.isCommitted())
					profit += stock.getProfit(timeInterval) + stock.getValue(timeInterval);
			}
		}
		
		return balance + profit;
	}

	
	@Override
	public double getBalance(long timeInterval) {
		return getBalance0(timeInterval) + balanceBias;
	}

	
	public void setBaseBalance(double baseBalance) {
		this.balance = baseBalance;
	}
	
	
	public void adjustBalance(double givenBalance, long timeInterval) {
		balanceBias = givenBalance - getBalance0(timeInterval);
	}
	
	
	public void adjustBalance() {
		balanceBias = 0;
	}

	
	public double getBalanceBias() {
		return marginBias;
	}

	
	private double getMargin0(long timeInterval) {
		double margin = 0;
		for (StockGroup group : groups) {
			margin += group.getMargin(timeInterval);
		}
		return margin;
	}

	
	@Override
	public double getMargin(long timeInterval) {
		return getMargin0(timeInterval) + marginBias;
	}
	
	
	public void adjustMargin(double givenMargin, long timeInterval) {
		marginBias = givenMargin - getMargin0(timeInterval);
	}
	
	
	public void adjustMargin() {
		marginBias = 0;
	}

	
	public double getMarginBias() {
		return marginBias;
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
	
	
	@Override
	public double calcTotalBias(long timeInterval) {
		double biasSum = 0;
		for (StockGroup group : groups) {
			biasSum += group.estimateUnitBias(timeInterval) * group.getVolume(timeInterval, false);
		}
		
		return biasSum;
	}


	private double estimateInvestAmount0(long timeInterval) {
		return calcInvestAmount(timeInterval);
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
		public List<Price> getPrices(long timeInterval) {
			return stock.getPrices(timeInterval);
		}

		@Override
		public double getAverageTakenPrice(long timeInterval) {
			return stock.getAverageTakenPrice(timeInterval);
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
			return estimateInvestAmount0(timeInterval);
		}

		@Override
		public boolean isBuy() {
			return stock.isBuy();
		}

	}
	
	
	@Override
	public Estimator getEstimator(String code, boolean buy) {
		int index = lookup(code, buy);
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
	
	
	public StockGroup get(String code, boolean buy) {
		int index = lookup(code, buy);
		if (index >= 0)
			return get(index);
		else
			return null;
	}
	
	
	public int lookup(String code, boolean buy) {
		for (int i = 0; i < groups.size(); i++) {
			StockGroup group = groups.get(i);
			if (group.code().equals(code) && group.isBuy() == buy) return i;
		}
		
		return -1;
	}
	
	
	public boolean add(StockGroup group) {
		if (group == null || lookup(group.code(), group.isBuy()) >= 0)
			return false;
		else
			return groups.add(group);
	}
	
	
	public StockGroup remove(int index) {
		return groups.remove(index);
	}
	
	
	public StockGroup remove(String code, boolean buy) {
		int index = lookup(code, buy);
		if (index >= 0)
			return remove(index);
		else
			return null;
	}
	
	
	public StockGroup set(int index, StockGroup group) {
		if (group == null || lookup(group.code(), group.isBuy()) >= 0)
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
		StockGroup group = get(code, buy);
		if (group == null) {
			group = newGroup(code, buy, Double.isNaN(refLeverage) ? this.refLeverage : refLeverage, price);
			if (group == null || !add(group))
				return null;
			else {
				Stock stock = group.add(this.timeViewInterval, volume);
				if (stock == null) remove(code, buy);
				
				return stock;
			}
		}
		else {
			if (!group.setPrice(price)) return null;
			if (!Double.isNaN(refLeverage) && refLeverage != group.getLeverage())
				group.setLeverage(refLeverage);
			
			return group.add(this.timeViewInterval, volume);
		}
		
	}
	
	
	public Stock addStock(String code, boolean buy, double volume, Price price) {
		return addStock(code, buy, Double.NaN, volume, price);
	}
	
	
	public Stock removeStock(String code, boolean buy, long timeInterval, long takenTimePoint) {
		int found = lookup(code, buy);
		if (found < 0) return null;
		
		StockGroup group = get(found);
		int index = group.lookup(timeInterval, takenTimePoint);
		if (index >= 0)
			return group.remove(index);
		else
			return null;
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
	
	
	public Price newPrice(double price, double lowPrice, double highPrice, long time) {
		return new PriceImpl(price, lowPrice, highPrice, time);
	}
	
	
}
