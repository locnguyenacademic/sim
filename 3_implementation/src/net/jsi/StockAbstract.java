package net.jsi;

import java.util.ArrayList;
import java.util.List;

import net.hudup.core.Util;

public abstract class StockAbstract extends EstimatorAbstract implements Stock {

	
	private static final long serialVersionUID = 1L;


	public final static String NONAME = "noname";
	
	
	public static int MAX_PRICE_COUNT = 1000;
	
	
	public static double LEVERAGE = 0.05;
	
	
	public static double UNIT_BIAS = LEVERAGE*100/2;
	
	
	public static long TIME_TAKEN_INTERVAL = 1000*3600*24;
	
	
	public static long TIME_VIEWED_INTERVAL = TIME_TAKEN_INTERVAL*7;

	
	public int maxPriceCount = MAX_PRICE_COUNT;
	
	
	protected boolean buy = true;
	
	
	protected double swap = 0;
	
	
	protected double spread = 0;
	
	
	public double commission = 0;
	
	
	public long timeTakenInterval = TIME_TAKEN_INTERVAL;
	
	
	public long timeViewedInterval = TIME_VIEWED_INTERVAL;

	
	protected List<Price> prices = new ArrayList<>();
	
	
	public double leverage = LEVERAGE;
	
	
	protected double dividend = 0;
	
	
	protected double unitBias = UNIT_BIAS;
	
	
	protected String code = NONAME;
	
	
	public StockAbstract() {
		
	}
	
	
	public StockAbstract(boolean buy, Price price) {
		this.buy = buy;
		this.setPrice(price);
	}
	
	
	@Override
	public boolean setPrice(Price price) {
		if (price == null)
			return false;
		else if (prices.size() == 0) {
			return prices.add(price);
		}
		else {
			Price lastPrice = prices.get(prices.size() -  1);
			if (price.time() - lastPrice.time() < timeTakenInterval) return false;
			boolean added = false;
			for (int i = 0; i < prices.size(); i++) {
				Price p = prices.get(i);
				if (p.time() > price.time()) {
					try {
						prices.add(i, price);
						added = true;
					} catch (Exception e) {}
					
					break;
				}
			}
			if (!added) added = prices.add(price);
			if (!added) return false;

			int index = prices.size() - maxPriceCount;
			if (index > 0) {
				List<Price> subList = prices.subList(index, prices.size());
				prices.clear();
				prices.addAll(subList);
			}
			return added;
		}
	}
	
	
	@Override
	public Price getPrice() {
		if (prices.size() == 0)
			return null;
		else
			return prices.get(prices.size() - 1);
	}
	
	
	@Override
	public Price getPrice(long timePoint) {
		for (Price price : prices) {
			if (price.time() == timePoint) return price;
		}
		return null;
	}
	
	
	@Override
	public List<Price> getPrices(long timeInterval) {
		if (timeInterval <= 0) return prices;
		
		List<Price> priceList = Util.newList();
		Price lastPrice = getPrice();
		if (lastPrice == null) return priceList;
		for (Price price : prices) {
			if (lastPrice.time() - price.time() <= timeInterval)
				priceList.add(price);
		}
		
		return priceList;
	}
	
	
	@Override
	public double getLowPrice(long timeInterval) {
		Price price = getExtremePrice(true, timeInterval);
		return price != null ? price.get() : 0;
	}
	
	
	@Override
	public double getHighPrice(long timeInterval) {
		Price price = getExtremePrice(false, timeInterval);
		return price != null ? price.get() : 0;
	}
	
	
	protected Price getExtremePrice(boolean low, long timeInterval) {
		Price found = null;
		List<Price> priceList = getPrices(timeInterval);
		for (Price price : priceList) {
			if (found == null)
				found = price;
			else
				found = (low ? price.get()<found.get() : price.get()>found.get()) ? price : found;
		}
		
		return found;
	}
	
	
	@Override
	public double getUnitBias() {
		return unitBias;
	}
	
	
	@Override
	public boolean setUnitBias(double unitBias) {
		this.unitBias = unitBias > 0 ? unitBias : 0;
		return true;
	}
	
	
	public static double calcMaxUnitBias(double unitBias, double leverage, double refBaseLeverage) {
		return Math.max(unitBias, unitBias*refBaseLeverage/leverage);
	}
	
	
	@Override
	public double getPositiveROISum(long timeInterval) {
		double roi = getROI(timeInterval);
		return roi > 0 ? roi : 0;
	}


	@Override
	public double getInvestAmount(long timeInterval) {
		return getProfit(timeInterval) - getMargin(timeInterval);
	}


	@Override
	public String code() {
		return code;
	}
	
	
	@Override
	public void copyProperties(Stock stock) {
		if (!(stock instanceof StockAbstract)) return;
		StockAbstract sa = (StockAbstract)stock;
		
		this.maxPriceCount = sa.maxPriceCount;
		this.buy = sa.buy;
		this.swap = sa.swap;
		this.spread = sa.spread;
		this.commission = sa.commission;
		this.timeTakenInterval = sa.timeTakenInterval;
		this.timeViewedInterval = sa.timeViewedInterval;
		this.prices = sa.prices;
		this.leverage = sa.leverage;
		this.dividend = sa.dividend;
		this.unitBias = sa.unitBias;
		this.code = sa.code;
	}


	@Override
	public String toString() {
		return code;
	}
	
	
}
