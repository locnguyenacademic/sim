package net.jsi;

import java.util.ArrayList;
import java.util.List;

public abstract class UniverseAbstract extends MarketAbstract implements Universe {

	
	private static final long serialVersionUID = 1L;

	
	protected List<Market> markets = new ArrayList<>();
	
	
	public UniverseAbstract() {
		
	}
	
	
	@Override
	public double getBalance() {
		double balance = 0;
		for (Market market : markets) {
			balance += market.getBalance();
		}
		return balance;
	}
	
	
	@Override
	public double getMargin() {
		double margin = 0;
		for (Market market : markets) {
			margin += market.getMargin();
		}
		return margin;
	}
	
	
	@Override
	public double getFreeMargin() {
		double freeMargin = 0;
		for (Market market : markets) {
			freeMargin += market.getFreeMargin();
		}
		return freeMargin;
	}


	@Override
	public double getProfit() {
		double profit = 0;
		for (Market market : markets) {
			profit += market.getProfit();
		}
		return profit;
	}


	@Override
	public int size() {
		return markets.size();
	}
	
	
	@Override
	public Market get(int index) {
		return markets.get(index);
	}

	
	@Override
	public boolean add(Market market) {
		return markets.add(market);
	}
	
	
	@Override
	public Market remove(int index) {
		return markets.remove(index);
	}
	
	
	@Override
	public Market newMarket(String name, double leverage, double unitBias) {
		return new MarketImpl(name, leverage, unitBias);
	}


	@Override
	public MarketImpl convert(Market market) {
		if (market instanceof MarketImpl)
			return (MarketImpl)market;
		else
			return null;
	}


	
}
