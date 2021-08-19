package net.jsi;

import java.util.ArrayList;
import java.util.List;

public class StockGroup extends StockAbstract implements Market {

	
	private static final long serialVersionUID = 1L;

	
	protected List<Stock> stocks = new ArrayList<>();
	
	
	public StockGroup(String code, boolean buy, double leverage, double unitBias, Price price) {
		super(buy, null);
		this.code = code;
		this.leverage = leverage;
		this.unitBias = unitBias;
		if (price == null) price = new Price();
		setPrice(price);
	}

	
	@Override
	public double getBalance() {
		return 0;
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
	public double getMargin(long timeInterval) {
		double margin = 0;
		for (Stock stock : stocks) {
			if (!stock.isCommitted()) margin += stock.getMargin(timeInterval);
		}
		return margin;
	}
	
	
	@Override
	public double getMargin() {
		return getMargin(0);
	}


	@Override
	public double getFreeMargin() {
		return getBalance() + getProfit() - getMargin();
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
	public double getProfit() {
		return getProfit(0);
	}


	@Override
	public double getROI(long timeInterval) {
		double profit = 0;
		double takenPrice = 0;
		for (Stock stock : stocks) {
			profit += stock.getProfit(timeInterval);
			takenPrice += stock.getTakenValue(timeInterval);
		}
		return profit / takenPrice;
	}


	@Override
	public double getROI() {
		return getROI(0);
	}


	public double getROIByLeverage(long timeInterval) {
		double profit = 0;
		double takenPrice = 0;
		for (Stock stock : stocks) {
			profit += stock.getProfit(timeInterval);
			takenPrice += stock.getMargin(timeInterval);
		}
		return profit / takenPrice;
	}

	
	@Override
	public double getPositiveROISum(long timeInterval) {
		double sum = 0;
		for (Stock stock : stocks) {
			double roi = stock.getROI(timeInterval);
			if (roi > 0) sum += roi;
		}
		return sum;
	}


	@Override
	public boolean setUnitBias(double unitBias) {
		boolean ret = super.setUnitBias(unitBias);
		if (!ret) return false;
		for (Stock stock : stocks) {
			ret = ret && stock.setUnitBias(unitBias);
		}
		
		return ret;
	}


	@Override
	public double estimateUnitBias(long timeInterval) {
		if (stocks.size() == 0) return unitBias;
		double bias = 0;
		for (Stock stock : stocks) {
			bias += stock.estimateUnitBias(timeInterval);
		}
		return Math.max(bias/stocks.size(), unitBias);
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


	public Stock addStock(double volume) {
		if (prices.size() == 0) return null;
		StockImpl stock = (StockImpl)newStock();
		stock.volume = volume;
		stock.taken(getPrice());
		if (stock.isValid()) stocks.add(stock);
		
		return stock;
	}
	
	
	public Stock newStock() {
		StockImpl stock = new StockImpl();
		stock.copyProperties(this);
		stock.prices.clear();
		return stock;
	}
	
	
	public Stock lookupStock(long timePoint) {
		for (Stock stock : stocks) {
			if (stock instanceof StockImpl) {
				Price takenPrice = ((StockImpl)stock).getTakenPrice(0);
				if (takenPrice != null && takenPrice.time() == timePoint) return stock;
			}
		}
		
		return null;
	}
	
	
	public List<Stock> getStocks(long timeInterval) {
		List<Stock> list = new ArrayList<>();
		for (Stock stock : stocks) {
			if (stock instanceof StockImpl) {
				Price takenPrice = ((StockImpl)stock).getTakenPrice(timeInterval);
				if (takenPrice != null) list.add(stock);
			}
		}
		
		return list;
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
	
	
}
