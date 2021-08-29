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
	
	
	protected Map<String, Market> placedMarkets = Util.newMap(0);
	
	
	protected Set<String> defaultStockCodes = Util.newSet(0);
	
	
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
		if (market == null || lookup(market.getName()) >= 0)
			return false;
		else {
			boolean ret = markets.add(market);
			if (ret) {
				if (placedMarkets.containsKey(market.getName())) placedMarkets.remove(market.getName());
				Market placedMarket = newPlacedMarket(market);
				if (placedMarket != null) placedMarkets.put(placedMarket.getName(), placedMarket);
			}
			
			return ret;
		}
	}
	
	
	@Override
	public Market remove(int index) {
		Market removedMarket = markets.remove(index);
		if (removedMarket != null && placedMarkets.containsKey(removedMarket.getName()))
			placedMarkets.remove(removedMarket.getName());
		
		return removedMarket;
	}
	
	
	@Override
	public Market set(int index, Market market) {
		if (market == null || lookup(market.getName()) >= 0)
			return null;
		else {
			Market previousMarket = markets.set(index, market);
			if (previousMarket != null) {
				placedMarkets.remove(previousMarket.getName());
				Market placedMarket = newPlacedMarket(market);
				if (placedMarket != null) placedMarkets.put(placedMarket.getName(), placedMarket);
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

	
	private Market newPlacedMarket(Market market) {
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
		
//		MarketImpl m = c(market);
//		if (m == null) return placedMarket;
//		
//		for (StockGroup group : m.groups) {
//			StockGroup placedGroup = placedMarket.get(group.code(), group.isBuy());
//			if (placedGroup == null) {
//				placedGroup = new StockGroup(group.code(), group.isBuy(), group.getLeverage(), group.getUnitBias(), null);
//				placedGroup.setBasicInfo(group);
//				placedGroup.stocks.clear();
//				placedMarket.add(placedGroup);
//			}
//			else
//				placedGroup.setBasicInfo(group);
//		}
		
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
		
		Universe u = getNearestUniverse();
		if (u != null && u != this)
			codes.addAll(u.getSupportStockCodes());
		else {
			List<Stock> stocks = getStocks(0);
			for (Stock stock : stocks) codes.add(stock.code());
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
		this.placedMarkets.clear();
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
		if (placedMarkets.containsKey(name))
			return placedMarkets.get(name);
		else
			return null;
	}
	
	
}
