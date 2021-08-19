package net.jsi;

import java.util.ArrayList;
import java.util.List;

public class MarketImpl extends MarketAbstract {

	
	private static final long serialVersionUID = 1L;


	public final static String NONAME = "noname";

	
	protected double leverage = StockAbstract.LEVERAGE;
	
	
	protected double balance = 0;
	
	
	protected double marginBias = 0;
	
	
	protected double unitBias = StockAbstract.UNIT_BIAS;
	
	
	protected List<StockGroup> groups = new ArrayList<>();
	
	
	public MarketImpl(String name, double leverage, double unitBias) {
		super(name);
		this.leverage = leverage;
		this.unitBias = unitBias;
	}
	
	
	@Override
	public double getBalance() {
		return balance;
	}

	
	public void setBalance(double balance) {
		this.balance = balance;
	}
	
	
	protected double getMargin0() {
		double margin = 0;
		for (StockGroup group : groups) {
			margin += group.getMargin(0);
		}
		return margin;
	}
	
	
	@Override
	public double getMargin() {
		return getMargin0() + marginBias;
	}
	
	
	public void adjustMargin(double givenMargin) {
		marginBias = givenMargin - getMargin0();
	}
	
	
	@Override
	public double getProfit() {
		double profit = 0;
		for (StockGroup group : groups) {
			profit += group.getProfit(0);
		}
		return profit;
	}

	
	private double queryPositiveROISum() {
		double sum = 0;
		for (StockGroup group : groups) {
			double roi = group.getROI(0);
			if (roi > 0) sum += roi;
		}
		return sum;
	}
	
	
	public double queryInvestAmount() {
		double biasSum = 0;
		for (StockGroup group : groups) {
			biasSum += group.estimateUnitBias(0);
		}
		
		return getFreeMargin() - biasSum;
	}
	
	
	public Estimator queryEstimator(String code) {
		int index = lookup(code);
		if (index < 0) return null;
		final StockGroup group = get(index);
		
		return new EstimatorAbstract() {
			private static final long serialVersionUID = 1L;

			@Override
			public Price getPrice() {
				return group.getPrice();
			}

			@Override
			public double getLowPrice(long timeInterval) {
				return group.getLowPrice(timeInterval);
			}

			@Override
			public double getHighPrice(long timeInterval) {
				return group.getHighPrice(timeInterval);
			}

			@Override
			public double getUnitBias() {
				return group.getUnitBias();
			}

			@Override
			public double getROI(long timeInterval) {
				return group.getROI(timeInterval);
			}

			@Override
			public double getPositiveROISum(long timeInterval) {
				return queryPositiveROISum();
			}

			@Override
			public double getInvestAmount(long timeInterval) {
				return queryInvestAmount();
			}

			@Override
			public double estimateUnitBias(long timeInterval) {
				return group.estimateUnitBias(timeInterval);
			}

			@Override
			public boolean isBuy() {
				return group.isBuy();
			}
			
		};
	}
	
	
	public int size() {
		return groups.size();
	}
	
	
	public StockGroup get(int index) {
		return groups.get(index);
	}
	
	
	public int lookup(String code) {
		for (int i = 0; i < groups.size(); i++) {
			StockGroup group = groups.get(i);
			if (group.code().equals(code)) return i;
		}
		
		return -1;
	}
	
	
	public boolean add(StockGroup group) {
		if (lookup(group.code()) < 0)
			return groups.add(group);
		else
			return false;
	}
	
	
	public StockGroup remove(int index) {
		return groups.remove(index);
	}
	
	
	public StockGroup newGroup(String code, boolean buy, double leverage, Price price) {
		double unitBias = StockAbstract.calcMaxUnitBias(this.unitBias, leverage, this.leverage);
		return new StockGroup(code, buy, leverage, unitBias, price);
	}

	
	public Stock lookupStock(String code, long timePoint) {
		for (StockGroup group : groups) {
			if (!group.code().equals(code)) continue;
			Stock stock = group.lookupStock(timePoint);
			if (stock != null) return stock;
		}
		
		return null;
	}
	
	
	public List<Stock> getStocks(String code, long timeInterval) {
		List<Stock> stocks = new ArrayList<>();
		for (StockGroup group : groups) {
			if (!group.code().equals(code)) continue;
			stocks.addAll(group.getStocks(timeInterval));
		}

		return stocks;
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
	
	
	public void updateUnitBias(StockGroup group, long timeInterval) {
		double unitBias = group.estimateUnitBias(timeInterval);
		group.setUnitBias(unitBias);
	}
	
	
	public StockImpl convert(Stock stock) {
		if (stock instanceof StockImpl)
			return (StockImpl)stock;
		else
			return null;
	}
	
	
}
