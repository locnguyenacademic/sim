/**
 * JSI: JAGGED STRATEGY INVESTMENT 
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: jsi.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.jsi;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class UniverseAbstract extends MarketAbstract implements Universe {

	
	private static final long serialVersionUID = 1L;

	
	protected List<Market> markets = Util.newList(0);
	
	
	protected Map<String, Market> backupPlacedMarkets = Util.newMap(0);
	
	
	protected Set<String> defaultStockCodes = Util.newSet(0);
	
	
	protected StockInfoStore store = new StockInfoStore();
	
	
	public UniverseAbstract() {
		addDefaultStockCodes(Util.newSet(0));
	}
	
	
	@Override
	public double getBalance(long timeInterval) {
		double balance = 0;
		for (Market market : markets) {
			balance += market.getBalance(timeInterval);
		}
		return balance;
	}
	
	
	@Override
	public double getMargin(long timeInterval) {
		double margin = 0;
		for (Market market : markets) {
			margin += market.getMargin(timeInterval);
		}
		return margin;
	}
	
	
	@Override
	public double getProfit(long timeInterval) {
		double profit = 0;
		for (Market market : markets) {
			profit += market.getProfit(timeInterval);
		}
		return profit;
	}


	@Override
	public double getTakenValue(long timeInterval) {
		double profit = 0;
		for (Market market : markets) {
			profit += market.getTakenValue(timeInterval);
		}
		return profit;
	}

	
	@Override
	public double calcTotalBias(long timeInterval) {
		double biasSum = 0;
		for (Market market : markets) {
			biasSum += market.calcTotalBias(timeInterval);
		}
		
		return biasSum;
	}


	@Override
	public int size() {
		return markets.size();
	}
	
	
	@Override
	public List<String> names() {
		List<String> nameList = Util.newList(size());
		for (Market market : markets) {
			nameList.add(market.getName());
		}
		return nameList;
	}


	@Override
	public Market get(int index) {
		return markets.get(index);
	}

	
	@Override
	public Market get(String name) {
		int index = lookup(name);
		if (index >= 0)
			return get(index);
		else
			return null;
	}
	
	
	@Override
	public QueryEstimator query(String name, Market refMarket) {
		Market market = get(name);
		if (market == null)
			return null;
		else if (refMarket == null || refMarket == market)
			return c(market);
		else {
			Market placedMarket = getPlacedMarket(name);
			return placedMarket == refMarket ? c(placedMarket) : null;
		}
	}


	@Override
	public int lookup(String name) {
		for (int i = 0; i < markets.size(); i++) {
			if (markets.get(i).getName().equals(name)) return i;
		}
		return -1;
	}


	@Override
	public boolean add(Market market) {
		if (market == null || lookup(market.getName()) >= 0) return false;
		
		boolean ret = markets.add(market);
		
		if (ret && !(market instanceof MarketImpl)) {
			if (backupPlacedMarkets.containsKey(market.getName())) backupPlacedMarkets.remove(market.getName());
			Market placedMarket = newBackupPlacedMarket(market);
			if (placedMarket != null) backupPlacedMarkets.put(placedMarket.getName(), placedMarket);
		}
		
		return ret;
	}
	
	
	@Override
	public Market remove(int index) {
		Market removedMarket = markets.remove(index);
		if (removedMarket != null && !(removedMarket instanceof MarketImpl) && backupPlacedMarkets.containsKey(removedMarket.getName()))
			backupPlacedMarkets.remove(removedMarket.getName());
		
		return removedMarket;
	}
	
	
	@Override
	public Market set(int index, Market market) {
		if (market == null || lookup(market.getName()) >= 0)
			return null;
		else {
			Market previousMarket = markets.set(index, market);
			if (previousMarket != null && !(previousMarket instanceof MarketImpl)) {
				backupPlacedMarkets.remove(previousMarket.getName());
				Market placedMarket = newBackupPlacedMarket(market);
				if (placedMarket != null) backupPlacedMarkets.put(placedMarket.getName(), placedMarket);
			}
			
			return previousMarket;
		}
	}


	@Override
	public Market newMarket(String name, double leverage, double unitBias) {
		Market superMarket = this;
		MarketImpl market = new MarketImpl(name, leverage, unitBias) {

			private static final long serialVersionUID = 1L;

			@Override
			public Market getSuperMarket() {
				return superMarket;
			}
			
		};
		market.setTimeViewInterval(this.getTimeViewInterval());
		market.setTimeValidInterval(this.getTimeValidInterval());
		return market;
	}

	
	private static Market newBackupPlacedMarket(Market market) {
		MarketImpl placedMarket = new MarketImpl(market.getName(), market.getLeverage(), market.getUnitBias()) {

			private static final long serialVersionUID = 1L;

			@Override
			public Market getSuperMarket() {
				return market.getSuperMarket();
			}

			@Override
			public double getBalanceBase() {
				return market.calcInvestAmount(market.getTimeViewInterval());
			}
			
		};
		placedMarket.setTimeViewInterval(market.getTimeViewInterval());
		placedMarket.setTimeValidInterval(market.getTimeValidInterval());
		
		return placedMarket;
	}
	
	

	@Override
	public MarketImpl c(Market market) {
		if (market == null)
			return null;
		if (market instanceof MarketImpl)
			return (MarketImpl)market;
		else
			return null;
	}


	@Override
	public List<Stock> getStocks(long timeInterval) {
		List<Stock> stocks = Util.newList(0);
		for (Market market : markets) {
			stocks.addAll(market.getStocks(timeInterval));
		}

		return stocks;
	}


	@Override
	public List<String> getSupportStockCodes() {
		Set<String> codes = Util.newSet(0);
		
		for (Market market : markets) {
			MarketImpl m = c(market);
			if (m == null) continue;
			for (int i = 0; i < m.size(); i++) {
				StockGroup group = m.get(i);
				codes.add(group.code());
			}
		}

		codes.addAll(defaultStockCodes);
		return Util.sort(codes);
	}


	@Override
	public List<String> getDefaultStockCodes() {
		return Util.sort(defaultStockCodes);
	}


	@Override
	public void addDefaultStockCodes(Collection<String> defaultStockCodes) {
		this.defaultStockCodes.clear();
		
		this.defaultStockCodes.add("AAPL");
		this.defaultStockCodes.add("ABBV");
		this.defaultStockCodes.add("BTCUSD");
		this.defaultStockCodes.add("ETHUSD");
		this.defaultStockCodes.add("KO");
		this.defaultStockCodes.add("MSFT");
		this.defaultStockCodes.add("MRK");
		this.defaultStockCodes.add("INTC");
		this.defaultStockCodes.add("PFE");
		this.defaultStockCodes.add("XAUUSD");
		
		for (String code : defaultStockCodes) {
			if (code != null && !code.isEmpty() && !code.equals(StockProperty.NOTCODE1) && !code.equals(StockProperty.NOTCODE1))
				this.defaultStockCodes.add(code);
		}
	}


	@Override
	protected void reset() {
		super.reset();
		this.markets.clear();
		this.backupPlacedMarkets.clear();
		this.defaultStockCodes.clear();
	}
	
	
	@Override
	public double getLeverage() {
		return StockProperty.LEVERAGE;
	}


	@Override
	public double getUnitBias() {
		return StockProperty.UNIT_BIAS;
	}


	@Override
	public Market getPlacedMarket(String name) {
		Market market = get(name);
		if (market != null && market instanceof MarketImpl)
			return ((MarketImpl)market).getPlacedMarket();
		else if (backupPlacedMarkets.containsKey(name))
			return backupPlacedMarkets.get(name);
		else
			return null;
	}


	@Override
	public StockInfoStore getStore() {
		return store;
	}
	
	
	@Override
	public Price newPrice(double price, double lowPrice, double highPrice, long time) {
		return newPrice0(price, lowPrice, highPrice, time);
	}
	
	
	protected static Price newPrice0(double price, double lowPrice, double highPrice, long time) {
		return new PriceImpl(price, lowPrice, highPrice, time);
	}


	@Override
	public long getTimeViewInterval() {
		long maxInterval = 0;
		for (Market market : markets) {
			maxInterval = Math.max(maxInterval,  market.getTimeViewInterval());
		}
		this.timeViewInterval = maxInterval;
		return maxInterval;
	}


	@Override
	public void setTimeViewInterval(long timeViewInterval) {
		this.timeViewInterval = timeViewInterval;
		for (Market market : markets) {
			MarketImpl m = c(market);
			if (m != null) m.setTimeViewInterval(timeViewInterval);
		}
	}


	@Override
	public long getTimeValidInterval() {
		long maxInterval = 0;
		for (Market market : markets) {
			maxInterval = Math.max(maxInterval,  market.getTimeValidInterval());
		}
		this.timeValidInterval = maxInterval;
		return maxInterval;
	}
	
	
	@Override
	public void setTimeValidInterval(long timeValidInterval) {
		this.timeValidInterval = timeValidInterval;
		for (Market market : markets) {
			MarketImpl m = c(market);
			if (m != null) m.setTimeValidInterval(timeValidInterval);
		}
	}


}
