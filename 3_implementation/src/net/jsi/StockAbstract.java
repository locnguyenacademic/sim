package net.jsi;

import java.util.List;

import net.hudup.core.Util;

public abstract class StockAbstract extends EstimatorAbstract implements Stock {

	
	private static final long serialVersionUID = 1L;


	public final static String NONAME = "noname";
	
	
	public static int MAX_PRICE_COUNT = 1000;
	
	
	public static double LEVERAGE = 0.05;
	
	
	public static double UNIT_BIAS = LEVERAGE*100/2;
	
	
	public static long TIME_UPDATE_PRICE_INTERVAL = 1000*3600*24;
	
	
	public static long TIME_VIEW_INTERVAL = TIME_UPDATE_PRICE_INTERVAL*10;

	
	public int maxPriceCount = MAX_PRICE_COUNT;
	
	
	protected boolean buy = true;
	
	
	protected double swap = 0;
	
	
	protected double spread = 0;
	
	
	public double commission = 0;
	
	
	//public long timeUpdatePriceInterval = TIME_UPDATE_PRICE_INTERVAL;
	
	
	protected List<Price> prices = Util.newList();
	
	
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
			//Price lastPrice = prices.get(prices.size() -  1);
			//if (price.time() - lastPrice.time() < timeUpdatePriceInterval) return false;
			boolean added = false;
			for (int i = 0; i < prices.size(); i++) {
				Price p = prices.get(i);
				if (p.getTime() > price.getTime()) {
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
			if (price.getTime() == timePoint) return price;
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
			if (lastPrice.getTime() - price.getTime() <= timeInterval)
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
	public double getLeverage() {
		return leverage;
	}


	@Override
	public double setLeverage(double leverage) {
		if (leverage <= 0) return 0;
		double oldLeverage = this.leverage;
		this.leverage = leverage;
		return oldLeverage;
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
		//this.timeUpdatePriceInterval = sa.timeUpdatePriceInterval;
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
