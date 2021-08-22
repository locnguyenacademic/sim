package net.jsi;

import java.util.List;

import net.hudup.core.Util;

public abstract class StockAbstract extends EstimatorAbstract implements Stock {

	
	private static final long serialVersionUID = 1L;


	public StockProperty property = new StockProperty();
	
	
	protected List<Price> prices = Util.newList();
	
	
	protected double leverage = StockProperty.LEVERAGE;
	
	
	protected boolean buy = true;
	
	
	protected double unitBias = StockProperty.UNIT_BIAS;
	
	
	protected String code = StockProperty.NONAME;
	
	
	public StockAbstract() {
		
	}
	
	
	public StockAbstract(boolean buy, Price price) {
		this.buy = buy;
		this.setPrice(price);
	}
	
	
	@Override
	public boolean setPrice(Price price) {
		if (!checkPrice(price))
			return false;
		else if (prices.size() == 0)
			return prices.add(price);
		else {
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

			int index = prices.size() - property.maxPriceCount;
			if (index > 0) {
				List<Price> subList = prices.subList(index, prices.size());
				prices.clear();
				prices.addAll(subList);
			}
			return added;
		}
	}
	
	
	public boolean checkPrice(Price price) {
		if (price == null || !price.isValid())
			return false;
		else if (prices.size() == 0)
			return true;
		else
			return checkPriceTimePoint(price.getTime());
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
	
	
	public long getPriceTimePoint() {
		Price price = getPrice();
		if (price != null)
			return price.getTime();
		else
			return 0;
	}
	
	
	public void setPriceTimePoint(long priceTimePoint) {
		if (!checkPriceTimePointPrevious(priceTimePoint)) return;
		getPrice().setTime(priceTimePoint);
	}
	
	
	public boolean checkPriceTimePoint(long priceTimePoint) {
		if (priceTimePoint < 0)
			return false;
		else if (prices.size() == 0)
			return true;
		else
			return (priceTimePoint - getPrice().getTime() >= property.timeUpdatePriceInterval);
	}
	
	
	public boolean checkPriceTimePointPrevious(long priceTimePoint) {
		if (priceTimePoint < 0)
			return false;
		else if (prices.size() <= 1)
			return true;
		else
			return (priceTimePoint - prices.get(prices.size() - 2).getTime() >= property.timeUpdatePriceInterval);
	}

	
	@Override
	public double getROI(long timeInterval) {
		double takenValue = getTakenValue(timeInterval);
		if (takenValue == 0) return 0;
		return getProfit(timeInterval) / takenValue;
	}
	
	
	@Override
	public double getROIByLeverage(long timeInterval) {
		double margin = getMargin(timeInterval);
		if (margin == 0) return 0;
		return getProfit(timeInterval) / margin;
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
		//Referred leverage (base leverage) is often larger than the specified leverage.
		if (leverage == 0 && refBaseLeverage == 0)
			return unitBias;
		else if (leverage == 0 && refBaseLeverage != 0)
			return unitBias / refBaseLeverage;
		else
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
	public boolean isBuy() {
		return buy;
	}
	
	
	@Override
	public double getLeverage() {
		return leverage;
	}


	@Override
	public void setLeverage(double leverage) {
		if (leverage >= 0) this.leverage = leverage;
	}
	
	
	@Override
	public void setBasicInfo(Stock stock) {
		if (!(stock instanceof StockAbstract)) return;
		StockAbstract sa = (StockAbstract)stock;
		
		this.property = sa.property;
		this.buy = sa.buy;
		this.prices = sa.prices;
		this.leverage = sa.leverage;
		this.unitBias = sa.unitBias;
		this.code = sa.code;
	}


	@Override
	public StockProperty getProperty() {
		return property;
	}


	@Override
	public void setProperty(StockProperty property) {
		this.property = property;
	}


	@Override
	public String toString() {
		return code;
	}
	
	
	public static Stock empty() {
		return new StockAbstract() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public double getAverageTakenPrice(long timeInterval) {
				return 0;
			}
			
			@Override
			public double estimateUnitBias(long timeInterval) {
				return 0;
			}
			
			@Override
			public void setCommitted(boolean committed) {
				
			}
			
			@Override
			public boolean isCommitted() {
				return false;
			}
			
			@Override
			public double getVolume(long timeInterval, boolean countCommitted) {
				return 0;
			}
			
			@Override
			public double getValue(long timeInterval) {
				return 0;
			}
			
			@Override
			public double getTakenValue(long timeInterval) {
				return 0;
			}
			
			@Override
			public double getProfit(long timeInterval) {
				return 0;
			}
			
			@Override
			public double getMargin(long timeInterval) {
				return 0;
			}
		};
	}
	
	
	
}
