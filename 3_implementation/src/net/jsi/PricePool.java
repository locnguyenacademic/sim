/**
 * JSI: JAGGED STRATEGY INVESTMENT 
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: jsi.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.jsi;

import java.io.Serializable;
import java.util.List;

/**
 * Price pool is essentially a series of prices according to time points.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class PricePool implements Serializable, Cloneable {


	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Code of stock.
	 */
	protected String code = StockProperty.NONAME;

	
	/**
	 * List of prices sorted according to time points.
	 */
	protected List<Price> prices = Util.newList(0);
	
	
	/**
	 * Unit bias is the bias for a volume of stock.
	 */
	protected double unitBias = StockProperty.UNIT_BIAS;
	
	
	/**
	 * Constructor with stock code.
	 * @param code stock code.
	 */
	public PricePool(String code) {
		this.code = code;
	}

	
	/**
	 * Getting code.
	 * @return stock code.
	 */
	public String code() {
		return code;
	}

	
	/**
	 * Renaming stock code.
	 * @param newCode new stock code.
	 * @return whether renaming is successful.
	 */
	protected boolean rename(String newCode) {
		if (newCode == null) return false;
		this.code = newCode;
		return true;
	}
	

	/**
	 * Getting size of prices list.
	 * @return size of prices list.
	 */
	public int size() {
		return prices.size();
	}
	
	
	/**
	 * Getting price by index.
	 * @param index specified index.
	 * @return price at specified index.
	 */
	public Price getByIndex(int index) {
		return prices.get(index);
	}
	
	
	/**
	 * Finding price by time point.
	 * @param timePoint specified time point
	 * @return index of the price at specified time point.
	 */
	public int lookup(long timePoint) {
		int n = prices.size();
		for (int i = n - 1; i >= 0 ; i--) {
			Price price = prices.get(i);
			if (price.getTime() == timePoint) return i;
		}
		
		return -1;
	}
	
	
	/**
	 * Checking if containing the specified price.
	 * @param price specified price.
	 * @return whether containing the specified price.
	 */
	public boolean contains(Price price) {
		return prices.contains(price);
	}
	
	
	/**
	 * Getting price by specified time point.
	 * @param timePoint specified time point.
	 * @return price at specified time point.
	 */
	public Price getByTimePoint(long timePoint) {
		int index = lookup(timePoint);
		return index >= 0 ? getByIndex(index) : null;
	}
	
	
	/**
	 * Getting prices list in given time interval.
	 * @param timeInterval time interval in miliseconds.
	 * @return prices list in given time interval.
	 */
	public List<Price> gets(long timeInterval) {
		if (timeInterval <= 0) return prices;
		
		List<Price> priceList = Util.newList(0);
		Price lastPrice = getLast();
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
	
	
	/**
	 * Getting last price which is current price.
	 * @return last price which is current price.
	 */
	public Price getLast() {
		if (prices.size() == 0)
			return null;
		else
			return prices.get(prices.size() - 1);
	}
	

	/**
	 * Getting price by time point and time interval.
	 * @param timeInterval specified time interval.
	 * @param timePoint specified time point.
	 * @return price at specified time point and in time interval.
	 */
	public Price get(long timeInterval, long timePoint) {
		Price lastPrice = getLast();
		if (lastPrice == null) return null;
		
		int n = prices.size();
		long lastTimePoint = lastPrice.getTime();
		for (int i = n - 1; i >= 0 ; i--) {
			Price price = prices.get(i);
			long pt = price.getTime(); 
			if (pt < timePoint)
				break;
			else if (pt > timePoint)
				continue;
			else if (timeInterval <= 0) 
				return price;
			else if (lastTimePoint - timePoint <= timeInterval)
				return price;
			else
				break;
		}
		
		return null;
	}

	
	/**
	 * Getting the first price in specified time interval.
	 * @param timeInterval specified time interval.
	 * @return the first price in specified time interval.
	 */
	protected Price getFirstWithin(long timeInterval) {
		return getFirstWithin(prices, timeInterval);
	}

	
	/**
	 * Getting the first price in time-sorted prices list and specified time interval.
	 * @param sortedPrices time-sorted prices list.
	 * @param timeInterval specified time interval.
	 * @return the first price in specified prices list and specified time interval.
	 */
	protected static Price getFirstWithin(List<Price> sortedPrices, long timeInterval) {
		if (sortedPrices == null || sortedPrices.size() == 0) return null;
		if (timeInterval <= 0) return sortedPrices.get(0);

		int n = sortedPrices.size();
		Price lastPrice = sortedPrices.get(n - 1);
		if (n == 1) return lastPrice;
		Price withinPrice = null;
		for (int i = n - 2; i >= 0 ; i--) {
			Price price = sortedPrices.get(i);
			if (lastPrice.getTime() - price.getTime() <= timeInterval)
				withinPrice = price;
			else if (withinPrice != null)
				return withinPrice;
		}
		
		return withinPrice != null ? withinPrice : lastPrice;
	}

	
	/**
	 * Getting the price around the specified time point.
	 * @param timePoint specified time point.
	 * @return the price around the specified time point.
	 */
	protected Price getAround(long timePoint) {
		if (prices.size() == 0 || timePoint < 0) return null;
		if (timePoint == 0) return prices.get(0);
		
		int n = prices.size();
		for (int i = n - 1; i >= 0 ; i--) {
			Price price = prices.get(i);
			if (price.getTime() <= timePoint) return price;
		}
		
		return prices.get(0);
	}
	
	
	/**
	 * Getting the internal price list.
	 * @return the internal price list {@link #prices}.
	 */
	public List<Price> getInternals() {
		return prices;
	}

	
	protected double[] getMinMax(long timeInterval) {
		List<Price> priceList = gets(timeInterval);
		if (priceList.size() == 0) return new double[] {0, 0};
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		for (Price price : priceList) {
			double v = price.get();
			min = Math.min(min, v);
			max = Math.max(max, v);
		}
		
		return new double[] {min, max};
	}
	
	
	public Price removeByIndex(int index) {
		return prices.remove(index);
	}
	
	
	public Price removeByTimePoint(long timePoint) {
		int index = lookup(timePoint);
		if (index >= 0)
			return removeByIndex(index);
		else
			return null;
	}
	
	
	public boolean remove(Price price) {
		return price != null ? prices.remove(price) : false;
	}
	
	
	protected boolean checkPricePossibleAdded(long timePoint) {
		Price lastPrice = getLast();
		if (lastPrice == null)
			return true;
		else
			return timePoint - lastPrice.getTime() >= StockProperty.TIME_UPDATE_PRICE_INTERVAL;
	}
	
	
	protected boolean checkPricePossibleAddedPrev(long timePoint) {
		if (prices.size() == 0) return false;
		if (prices.size() == 1) return true;
		
		Price lastPrice2 = prices.get(prices.size() - 2);
		return timePoint - lastPrice2.getTime() >= StockProperty.TIME_UPDATE_PRICE_INTERVAL;
	}

	
	public boolean add(Price price) {
		return add(price, StockProperty.MAX_PRICE_COUNT);
	}

	
	protected boolean add(Price price, int maxPriceCount) {
		return add(price, maxPriceCount, true);
	}
	
	
	protected boolean addWithoutDuplicateTime(Price price, int maxPriceCount) {
		return add(price, maxPriceCount, false);
	}
	
	
	private boolean add(Price price, int maxPriceCount, boolean duplicateTime) {
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
			else if (price.getTime() == p.getTime() && !duplicateTime)
				return false;
		}
		if (!added) prices.add(0, price);
		
		if (maxPriceCount > 0) {
			int index = prices.size() - maxPriceCount;
			if (index > 0) {
				List<Price> subList = Util.newList(0);
				subList.addAll(prices.subList(index, prices.size()));
				prices.clear();
				prices.addAll(subList);
			}
		}
		
		return true;
	}

	
	public double getUnitBias() {
		return unitBias;
	}
	
	
	public boolean setUnitBias(double unitBias) {
		if (Double.isNaN(unitBias)) return false; 
		this.unitBias = unitBias;
		return true;
	}


	public static class TakenStockPrice implements Serializable, Cloneable {

		/**
		 * Serial version UID for serializable class.
		 */
		private static final long serialVersionUID = 1L;
		
		public TakenPrice takenPrice = null;
		
		public StockImpl stock = null;
		
		public StockGroup group = null;
		
		public TakenStockPrice(StockGroup group, StockImpl stock, TakenPrice takenPrice) {
			this.group = group;
			this.stock = stock;
			this.takenPrice = takenPrice;
		}
		
	}
	
	
	public static List<TakenStockPrice> getTakenPrices(String code, Price price, Universe universe, long timeInterval) {
		List<TakenStockPrice> takenPrices = Util.newList(0);
		universe = universe != null ? universe : UniverseImpl.g();
		if (price == null || universe == null) return takenPrices;
		
		List<String> marketNames = universe.names();
		for (String marketName : marketNames) {
			Market market = universe.get(marketName);
			MarketImpl m = universe.c(market);
			if (m == null) continue;
			
			List<StockGroup> groups = Util.newList(0);
			groups.addAll(m.groups);
			MarketImpl watchMarket = m.getWatchMarket();
			if (watchMarket != null) groups.addAll(watchMarket.groups);
			MarketImpl trashMarket = m.getTrashMarket();
			if (trashMarket != null) groups.addAll(trashMarket.groups);
			
			for (StockGroup group : groups) {
				if (!group.code().equals(code)) continue;
				
				List<Stock> stocks = group.getStocks(timeInterval);
				for (Stock stock : stocks) {
					StockImpl s = group.c(stock);
					if (s == null) continue;
					Price p = s.getTakenPrice(timeInterval);
					if (p != null && p instanceof TakenPrice && p.checkRefEquals(price))
						takenPrices.add(new TakenStockPrice(group, s, (TakenPrice)p));
				}
			}
		}
		
		return takenPrices;
	}
	
	
	public static List<TakenStockPrice> getLastTakenPrices(String code, Universe universe, long timeInterval) {
		PricePool pricePool = StockInfoStore.getPricePool(code);
		if (pricePool == null || pricePool.size() == 0) return Util.newList(0);
		
		Price lastPrice = pricePool.getLast();
		int n = pricePool.size();
		for (int i = n - 1; i >= 0; i--) {
			Price price = pricePool.getByIndex(i);
			if (timeInterval > 0 && lastPrice.getTime() - price.getTime() > timeInterval) break;
			
			List<TakenStockPrice> takenPrices = getTakenPrices(code, price, universe, timeInterval);
			if (takenPrices != null && takenPrices.size() > 0) return takenPrices; 
		}
		
		return Util.newList(0);
	}
	
	
	public static void addSortedPrices(List<Price> targetSortedPrices, List<Price> addingSortedPrices) {
		if (addingSortedPrices.size() == 0) return;
		
		for (Price addingPrice : addingSortedPrices) targetSortedPrices.remove(addingPrice);
		
		int startIndex = 0;
		for (int i = 0; i < addingSortedPrices.size(); i++) {
			Price addingPrice = addingSortedPrices.get(i);
			int found = -1;
			for (int j = startIndex; j < targetSortedPrices.size(); j++) {
				Price targetPrice = targetSortedPrices.get(j);
				if (targetPrice.getTime() > addingPrice.getTime()) {
					found = j;
					break;
				}
			}
			
			if (found == -1) {
				targetSortedPrices.addAll(addingSortedPrices.subList(i, addingSortedPrices.size()));
				return;
			}
			else {
				targetSortedPrices.add(found, addingPrice);
				startIndex = found;
			}
		}
		
	}


	protected boolean sync(PricePool otherPricePool, long timeInterval, int maxPriceCount, boolean removeRedundant) {
		if (!this.code().equals(otherPricePool.code())) return false;
		this.unitBias = otherPricePool.unitBias;
		
		int n = otherPricePool.size();
		Price otherLastPrice = otherPricePool.getLast();
		for (int i = n - 1; i >= 0; i--) {
			Price otherPrice = otherPricePool.getByIndex(i);
			if (timeInterval > 0 && otherLastPrice.getTime() - otherPrice.getTime() > timeInterval)
				break;
			Price thisPrice = this.getByTimePoint(otherPrice.getTime());
			if (thisPrice == null)
				this.add(otherPrice, maxPriceCount);
			else
				thisPrice.copy(otherPrice);
		}
		
		if (!removeRedundant) return true;
		
		n = this.size();
		List<Price> removedPrices = Util.newList(0);
		Price thisLastPrice = this.getLast();
		for (int i = n - 1; i >= 0; i--) {
			Price thisPrice = this.getByIndex(i);
			if (timeInterval > 0 && thisLastPrice.getTime() - thisPrice.getTime() > timeInterval)
				break;
			if (otherPricePool.lookup(thisPrice.getTime()) < 0)
				removedPrices.add(thisPrice);
		}
		for (Price removedPrice : removedPrices) this.remove(removedPrice);
		
		return true;
	}
	
	
	protected boolean retain(PricePool referredPricePool, long timeInterval, boolean update) {
		if (!this.code().equals(referredPricePool.code())) return false;
		this.unitBias = referredPricePool.unitBias;

		int n = this.size();
		if (n == 0) return true;
		
		Price lastPrice = this.getLast();
		List<Price> removedPrices = Util.newList(0);
		int breakIndex = -1;
		for (int i = n - 1; i >= 0; i--) {
			Price thisPrice = this.getByIndex(i);
			if (timeInterval > 0 && lastPrice.getTime() - thisPrice.getTime() > timeInterval) {
				breakIndex = i;
				break;
			}
			
			Price referredPrice = referredPricePool.getByTimePoint(thisPrice.getTime());
			if (referredPrice == null)
				removedPrices.add(thisPrice);
			else if (update)
				thisPrice.copy(referredPrice);
		}
		
		if (breakIndex == n - 1)
			this.prices.clear();
		else if (breakIndex >= 0) {
			List<Price> subPrices = this.prices.subList(breakIndex + 1, n);
			this.prices.clear();
			this.prices.addAll(subPrices);
		}
		
		for (Price removedPrice : removedPrices) this.remove(removedPrice);
		
		return true;
	}
	
	
	protected void cut(long timeInterval) {
		List<Price> prices = Util.newList(0);
		if (timeInterval <= 0)
			prices.addAll(this.prices);
		else
			prices = gets(timeInterval);
		
		this.prices.clear();
		this.prices.addAll(prices);
	}
	
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
	
}
