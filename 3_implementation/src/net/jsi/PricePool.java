package net.jsi;

import java.io.Serializable;
import java.util.List;

public class PricePool implements Serializable, Cloneable {


	private static final long serialVersionUID = 1L;

	
	protected String code = StockProperty.NONAME;

	
	protected List<Price> prices = Util.newList(0);
	
	
	protected double unitBias = StockProperty.UNIT_BIAS;
	
	
	public PricePool(String code) {
		this.code = code;
	}

	
	protected String code() {
		return code;
	}


	protected int getPriceCount() {
		return prices.size();
	}
	
	
	protected Price getPrice(int index) {
		return prices.get(index);
	}
	
	
	protected int lookupPrice(long timePoint) {
		int n = prices.size();
		for (int i = n - 1; i >= 0 ; i--) {
			Price price = prices.get(i);
			if (price.getTime() == timePoint) return i;
		}
		
		return -1;
	}
	
	
	protected Price getPrice(long timePoint) {
		int index = lookupPrice(timePoint);
		return index >= 0 ? getPrice(index) : null;
	}
	
	
	protected List<Price> getPrices(long timeInterval) {
		if (timeInterval <= 0) return prices;
		
		List<Price> priceList = Util.newList(0);
		Price lastPrice = getLastPrice();
		if (lastPrice == null) return prices;
		
		long lastTime = lastPrice.getTime();
		int n = prices.size();
		for (int i = n - 1; i >= 0 ; i--) {
			Price price = prices.get(i);
			if (lastTime - price.getTime() <= timeInterval)
				priceList.add(0, price);
			else
				break;
		}
		
		return priceList;
	}
	
	
	protected Price getLastPrice() {
		if (prices.size() == 0)
			return null;
		else
			return prices.get(prices.size() - 1);
	}
	

	protected Price getPrice(long timeInterval, long timePoint) {
		Price lastPrice = getLastPrice();
		if (lastPrice == null) return null;
		
		int n = prices.size();
		for (int i = n - 1; i >= 0 ; i--) {
			Price price = prices.get(i);
			if (price.getTime() != timePoint)
				continue;
			else if (timeInterval <= 0) 
				return price;
			else if (lastPrice.getTime() - timePoint <= timeInterval)
				return price;
			else
				break;
		}
		
		return null;
	}

	
	protected List<Price> getInternalPrices() {
		return prices;
	}

	
	protected Price removePrice(int index) {
		return prices.remove(index);
	}
	
	
	protected Price removePrice(long timePoint) {
		int index = lookupPrice(timePoint);
		if (index >= 0)
			return removePrice(index);
		else
			return null;
	}
	
	
	protected boolean removePrice(Price price) {
		return price != null ? prices.remove(price) : false;
	}
	
	
	protected boolean checkPricePossibleAdded(long timePoint) {
		Price lastPrice = getLastPrice();
		if (lastPrice == null)
			return true;
		else
			return timePoint - lastPrice.getTime() >= StockProperty.TIME_UPDATE_PRICE_INTERVAL;
	}
	
	
	protected boolean checkPricePossibleAdded2(long timePoint) {
		if (prices.size() == 0) return false;
		if (prices.size() == 1) return true;
		
		Price lastPrice2 = prices.get(prices.size() - 2);
		return timePoint - lastPrice2.getTime() >= StockProperty.TIME_UPDATE_PRICE_INTERVAL;
	}

	
	protected boolean addPrice(Price price, int maxPriceCount) {
		if (price == null || !price.isValid()) return false;
		
		int n = prices.size();
		boolean added = false;
		for (int i = n - 1; i >= 0 ; i--) {
			Price p = prices.get(i);
			if (price.getTime() > p.getTime()) {
				prices.add(i + 1, price);
				added = true;
				break;
			}
		}
		if (!added) prices.add(0, price);
		
		if (maxPriceCount > 0) {
			int index = prices.size() - maxPriceCount;
			if (index > 0) {
				List<Price> subList = prices.subList(index, prices.size());
				prices.clear();
				prices.addAll(subList);
			}
		}
		
		return true;
	}
	
	
	protected double getUnitBias() {
		return unitBias;
	}
	
	
	protected boolean setUnitBias(double unitBias) {
		if (Double.isNaN(unitBias)) return false; 
		this.unitBias = unitBias;
		return true;
	}


}
