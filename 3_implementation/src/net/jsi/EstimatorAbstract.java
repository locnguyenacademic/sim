/**
 * JSI: JAGGED STRATEGY INVESTMENT 
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: jsi.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.jsi;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public abstract class EstimatorAbstract implements Estimator {

	
	private static final long serialVersionUID = 1L;

	
	public EstimatorAbstract() {
		super();
	}


	@Override
	public double estimateUnitBias(long timeInterval) {
		List<Price> prices = getPrices(timeInterval);
		if (prices.size() == 0) return getUnitBias();
		
		double mean1 = 0;
		double mean2 = 0;
		for (Price price : prices) {
			mean1 += price.get();
			mean2 += (price.getHigh() - price.getLow())/2.0;
		}
		mean1 = mean1 / prices.size();
		mean2 = mean2 / prices.size();
		
		double bias1 = 0;
		double bias2 = 0;
		for (Price price : prices) {
			double d1 = price.get() - mean1;
			double d2 = (price.getHigh() - price.getLow())/2.0 - mean2;
			bias1 += d1*d1;
			bias2 += d2*d2;
		}
		
		bias1 = Math.sqrt(bias1 / prices.size());
		bias2 = Math.sqrt(bias2 / prices.size());
		
		return Math.max((bias1 + bias2) / 2, getUnitBias());
	}

	
	private double getLowestPrice(long timeInterval) {
		Price price = getExtremePrice(true, timeInterval);
		return price != null ? price.get() : 0;
	}
	
	
	private double getLowPriceMean(long timeInterval) {
		return getExtremePriceMean(true, timeInterval);
	}

	
	private double getHighestPrice(long timeInterval) {
		Price price = getExtremePrice(false, timeInterval);
		return price != null ? price.get() : 0;
	}
	
	
	private double getHighPriceMean(long timeInterval) {
		return getExtremePriceMean(false, timeInterval);
	}

	
	private Price getExtremePrice(boolean low, long timeInterval) {
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
	
	
	private double getExtremePriceMean(boolean low, long timeInterval) {
		List<Price> priceList = getPrices(timeInterval);
		if (priceList.size() == 0) return 0;
		double mean = 0;
		for (Price price : priceList) mean += low ? price.getLow() : price.getHigh();
		
		return mean / priceList.size();
	}

	
	@Override
	public double estimateLowPrice(long timeInterval) {
		double price = getPrice().get();;
		double lowPrice = price;
		double roi = getROI(timeInterval);
		double unitBias = estimateUnitBias(timeInterval);
		lowPrice -= Math.max(unitBias, roi < 0 ? -price*roi : 0);
		
		return Math.max(Math.min(lowPrice, getLowPriceMean(timeInterval)), getLowestPrice(timeInterval));
	}
	
	
	@Override
	public double estimateHighPrice(long timeInterval) {
		double price = getPrice().get();
		double highPrice = price;
		double roi = getROI(timeInterval);
		double unitBias = estimateUnitBias(timeInterval);
		highPrice += Math.max(unitBias, roi > 0 ? price*roi : 0);
		
		return Math.min(Math.max(highPrice, getHighPriceMean(timeInterval)), getHighestPrice(timeInterval));
	}

	
	private double estimateBiasAtCurrentPrice(long timeInterval) {
		Price p = getPrice();
		if (p == null) return 0;

		double price = p.get();
		double roi = getROI(timeInterval);
		double estimateUnitBias = estimateUnitBias(timeInterval);
		double lowPrice = estimateLowPrice(timeInterval);
		double highPrice = estimateHighPrice(timeInterval);
		double bias = estimateUnitBias;
		if (price <= lowPrice) {
			if (roi > 0)
				bias = Math.max(price*roi, estimateUnitBias);
			else
				bias = Math.min(-price*roi, estimateUnitBias);
		}
		else if (price >= highPrice) {
			if (roi > 0)
				bias = Math.min(price*roi, estimateUnitBias);
			else 
				bias = Math.max(-price*roi, estimateUnitBias);
		}
		else {
			bias = ((roi > 0 ? price*roi : -price*roi) + estimateUnitBias) / 2;
		}
		
		return Math.max(Math.min(bias, estimateUnitBias), getUnitBias());
	}
	
	
	@Override
	public double estimatePrice(long timeInterval) {
		Price p = getPrice();
		if (p == null) return 0;

		double price = p.get();
		double bias = estimateBiasAtCurrentPrice(timeInterval);
		double roi = getROI(timeInterval);
		double newPrice = roi > 0 ? price + Math.min(bias, price*roi) : price - Math.min(bias, -price*roi); 

		return Math.max(Math.min(newPrice, estimateHighPrice(timeInterval)), estimateLowPrice(timeInterval));
	}


	@Override
	public double estimateStopLoss(long timeInterval) {
		Price p = getPrice();
		if (p == null) return 0;
		double price = p.get();
		double bias = estimateBiasAtCurrentPrice(timeInterval);
		double takenPrice = getAverageTakenPrice(timeInterval);
		double leverage = getLeverage();
		if (leverage == 0)
			takenPrice = 0;
		else if (leverage >= 1)
			takenPrice = takenPrice * leverage;
		else
			takenPrice = takenPrice * (1-leverage);
		
		double stopLoss = price;
		if (isBuy()) {
			stopLoss = Math.max(price - bias, takenPrice);
			stopLoss = Math.max(stopLoss, estimateLowPrice(timeInterval));
		}
		else {
			stopLoss = Math.min(price + bias, takenPrice);
			stopLoss = Math.min(stopLoss, estimateHighPrice(timeInterval));
		}
		
		return stopLoss;
	}
	
	
	@Override
	public double estimateTakeProfit(long timeInterval) {
		Price p = getPrice();
		if (p == null) return 0;
		double price = p.get();
		double bias = estimateBiasAtCurrentPrice(timeInterval);
		double takenPrice = getAverageTakenPrice(timeInterval);
		double leverage = getLeverage();
		if (leverage == 0)
			takenPrice = 0;
		else if (leverage >= 1)
			takenPrice = takenPrice * leverage;
		else
			takenPrice = takenPrice * (1-leverage);
		
		double takeProfit = price;
		if (isBuy()) {
			takeProfit = Math.max(price + bias, takenPrice);
			takeProfit = Math.min(takeProfit, estimateHighPrice(timeInterval));
			takeProfit = Math.max(takeProfit, takenPrice);
		}
		else {
			takeProfit = Math.min(price - bias, takenPrice);
			takeProfit = Math.max(takeProfit, estimateLowPrice(timeInterval));
			takeProfit = Math.min(takeProfit, takenPrice);
		}
		
		return takeProfit;
	}

	
	@Override
	public List<EstimateStock> estimateStopLossTakeProfit(List<Stock> stocks, long timeInterval) {
		if (stocks == null || stocks.size() == 0) return Util.newList(0);
		
		List<EstimateStock> estimateStocks = Util.newList(stocks.size());
		for (Stock stock : stocks) {
			Estimator estimator = duplicate(this, stock);
			double stopLoss = estimator.estimateStopLoss(timeInterval);
			double takeProfit = estimator.estimateTakeProfit(timeInterval);
			EstimateStock es = new EstimateStock(stock, stopLoss, takeProfit, false);
			es.estimatedPrice = estimator.estimatePrice(timeInterval);
			estimateStocks.add(es);
		}
		Collections.sort(estimateStocks, new Comparator<EstimateStock>() {
			@Override
			public int compare(EstimateStock o1, EstimateStock o2) {
				if (o1.getAverageTakenPrice(0) < o2.getAverageTakenPrice(0))
					return 1;
				else if (o1.getAverageTakenPrice(0) == o2.getAverageTakenPrice(0))
					return 0;
				else
					return -1;
			}
		});
		
		
		for (int i = 0; i < estimateStocks.size(); i++) {
			EstimateStock es = estimateStocks.get(i);
			Price esp = es.stock.getPrice();
			es.estimated = true;
			if (i == 0 || es.estimated ||  esp == null) continue;
			
			List<EstimateStock> list = Util.newList(0);
			for (int j = i + 1; j < estimateStocks.size(); j++) {
				EstimateStock esNext = estimateStocks.get(j);
				if (esNext.estimated)
					continue;
				else if (esNext.estimatedTakeProfit < es.estimatedStopLoss)
					list.add(esNext);
			}
			if (list.size() == 0) continue;
			
			Collections.sort(list, new Comparator<EstimateStock>() {
				@Override
				public int compare(EstimateStock o1, EstimateStock o2) {
					if (o1.stock.getTakenValue(0) < o2.stock.getTakenValue(0))
						return -1;
					else if (o1.stock.getTakenValue(0) == o2.stock.getTakenValue(0))
						return 0;
					else
						return 1;
				}
			});
			
			double sum = 0;
			double loss = esp.get() - es.estimatedStopLoss;
			for (EstimateStock s : list) {
				Price p = s.stock.getPrice();
				s.estimated = true;
				if (p == null) continue;
				
				s.estimatedTakeProfit = es.estimatedStopLoss;
				sum += s.estimatedTakeProfit - s.stock.getPrice().get();
				if (sum >= loss) break;
			}
		}
		
		List<EstimateStock> newEstimateStocks = Util.newList(estimateStocks.size());
		newEstimateStocks.addAll(estimateStocks);
		for (int i = 0; i < estimateStocks.size(); i++) {
			EstimateStock es = estimateStocks.get(i);
			int index = stocks.indexOf(es.stock);
			newEstimateStocks.set(index, es);
		}

		return newEstimateStocks;
	}

	
	@Override
	public double estimateInvestAmount(long timeInterval, double refGlobalPositiveROISum, double refGlobalInvestAmount) {
		Price p = getPrice();
		if (p == null) return 0;
		
		double price = p.get();
		double roi = getROI(timeInterval);
		if (roi <= 0 || roi > refGlobalPositiveROISum || refGlobalPositiveROISum <= 0 || refGlobalInvestAmount <= 0)
			return 0;
		
		double takenAmount = roi / refGlobalPositiveROISum * refGlobalInvestAmount;
		double takenVolume = takenAmount / price;
		if (takenVolume == 0) return 0;
		
		double bias = estimateBiasAtCurrentPrice(timeInterval);
		int found = 0;
		for (int i = 1; i <= takenVolume; i++) {
			if (i*(price+bias) > takenAmount) found = i - 1;
		}
		return found * price;
	}
	
	
	@Override
	public double estimateInvestAmount(long timeInterval) {
		return estimateInvestAmount(timeInterval, getPositiveROISum(timeInterval), getInvestAmount(timeInterval));
	}


	@Override
	public double estimateInvestVolume(long timeInterval, double refGlobalPositiveROISum, double refGlobalInvestAmount) {
		Price p = getPrice();
		if (p == null)
			return 0;
		else
			return estimateInvestAmount(timeInterval, refGlobalPositiveROISum, refGlobalInvestAmount) / p.get();
	}


	@Override
	public double estimateInvestVolume(long timeInterval) {
		return estimateInvestVolume(timeInterval, getPositiveROISum(timeInterval), getInvestAmount(timeInterval));
	}
	
	
	
	
	@Override
	public Invest[] estimateDualInvest(long timeInterval, double refGlobalPositiveROISum, double refGlobalInvestAmount) {
		double volume = estimateInvestVolume(timeInterval, refGlobalPositiveROISum, refGlobalInvestAmount);
		if (volume <= 0) return new Invest[] {};
		Price p = getPrice();
		if (p == null) return new Invest[] {};

		double stopLoss = estimateStopLoss(timeInterval);
		double takeProfit = estimateTakeProfit(timeInterval);
		double unitBias = estimateBiasAtCurrentPrice(timeInterval);
		double nextTakeProfit = Math.min(isBuy() ? takeProfit + unitBias : takeProfit - unitBias, isBuy() ? getHighestPrice(timeInterval) : getLowestPrice(timeInterval));

		Invest invest1 = new Invest(isBuy(), volume/2, p.get(), stopLoss, takeProfit, takeProfit);
		invest1.margin = volume/2 * p.get() * getLeverage();
		Invest invest2 = new Invest(isBuy(), volume/2, p.get(), stopLoss, takeProfit, nextTakeProfit);
		invest2.margin = volume/2 * p.get() * getLeverage();
		return new Invest[] {invest1, invest2};
	}


	@Override
	public Invest[] estimateDualInvest(long timeInterval) {
		return estimateDualInvest(timeInterval, getPositiveROISum(timeInterval), getInvestAmount(timeInterval));
	}


	protected static Estimator duplicate(Estimator estimator, Stock stock) {
		return new EstimatorAbstract() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isBuy() {
				return stock.isBuy();
			}
			
			@Override
			public double getLeverage() {
				return stock.getLeverage();
			}
			
			@Override
			public double getUnitBias() {
				return stock.getUnitBias();
			}
			
			@Override
			public double getROI(long timeInterval) {
				return stock.getROI(timeInterval);
			}
			
			@Override
			public List<Price> getPrices(long timeInterval) {
				return stock.getPrices(timeInterval);
			}
			
			@Override
			public Price getPrice() {
				return stock.getPrice();
			}
			
			@Override
			public double getAverageTakenPrice(long timeInterval) {
				return stock.getAverageTakenPrice(timeInterval);
			}

			@Override
			public double getPositiveROISum(long timeInterval) {
				return estimator.getPositiveROISum(timeInterval);
			}
			
			@Override
			public double getInvestAmount(long timeInterval) {
				return estimator.getInvestAmount(timeInterval);
			}
			
		};
		
	}
	
	
}
	
	