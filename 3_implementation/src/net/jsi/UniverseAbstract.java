package net.jsi;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public abstract class UniverseAbstract extends MarketAbstract implements Universe {

	
	private static final long serialVersionUID = 1L;

	
	protected List<Market> markets = Util.newList(0);
	
	
	public UniverseAbstract() {
		
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
			nameList.add(market.name());
		}
		return nameList;
	}


	@Override
	public Market get(int index) {
		return markets.get(index);
	}

	
	@Override
	public QueryEstimator query(int index) {
		Market market = get(index);
		MarketImpl mi = c(market);
		if (mi != null)
			return mi;
		else
			return null;
	}


	@Override
	public int lookup(String name) {
		for (int i = 0; i < markets.size(); i++) {
			if (markets.get(i).name().equals(name)) return i;
		}
		return -1;
	}


	@Override
	public boolean add(Market market) {
		if (market == null || lookup(market.name()) >= 0)
			return false;
		else
			return markets.add(market);
	}
	
	
	@Override
	public Market remove(int index) {
		return markets.remove(index);
	}
	
	
	@Override
	public Market set(int index, Market market) {
		if (market == null || lookup(market.name()) >= 0)
			return null;
		else
			return markets.set(index, market);
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
		market.timeViewInterval = this.timeViewInterval;
		return market;
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
		codes.add("AAPL");
		codes.add("ABBV");
		codes.add("BTCUSD");
		codes.add("ETHUSD");
		codes.add("KO");
		codes.add("MSFT");
		codes.add("MRK");
		codes.add("INTC");
		codes.add("PFE");
		codes.add("XAUUSD");
		
		List<Stock> stocks = getStocks(0);
		for (Stock stock : stocks) codes.add(stock.code());
		
		List<String> codeList = Util.newList(codes.size());
		codeList.addAll(codes);
		Collections.sort(codeList);
		return codeList;
	}


	
}
