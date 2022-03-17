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

	
	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;

	
	public EstimatorAbstract() {
		super();
	}


	private double getROIAdjusted(long timeInterval) {
		double roi = getROI(timeInterval);;
		
		List<Price> prices = getPrices(timeInterval);
		Price price = PricePool.getFirstWithin(prices, timeInterval);
		if (price == null) return roi;
		
		Price lastPrice = prices.get(prices.size() -  1);
		double oscillRatio = (lastPrice.get() - price.get()) / price.get();
		if (roi != 0)
			return (roi + oscillRatio) / 2;
		else {
			double takenValue = getAverageTakenPrice(timeInterval);
			if (takenValue == 0)
				return oscillRatio;
			else
				return oscillRatio / 2;
		}
	}
	
	
	@Override
	public double estimateUnitBias(long timeInterval) {
		return Math.max(estimateUnitBiasFromData(timeInterval), getUnitBias());
	}

	
	@Override
	public double estimateUnitBiasFromData(long timeInterval) {
		List<Price> prices = getPrices(timeInterval);
		if (prices.size() == 0)
			return getUnitBias();
		else
			return estimateUnitBiasFromData(prices);
	}


	public static double estimateUnitBiasFromData(List<Price> prices) {
		if (prices.size() == 0) return 0;
		
		double mean = 0;
		for (Price price : prices) mean += price.get();
		mean = mean / prices.size();
		
		double bias = 0;
		for (Price price : prices) {
			double d = price.get() - mean;
			bias += d*d;
		}
		
		return Math.sqrt(bias / prices.size());
	}

	
	@SuppressWarnings("unused")
	@Deprecated
	private static double estimateUnitBiasFromData0(List<Price> prices) {
		if (prices.size() == 0) return 0;
		
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
		
		return (bias1 + bias2) / 2;
	}

	
	public static double estimatePriceMeanFromData(List<Price> prices) {
		if (prices.size() == 0) return 0;
		
		double mean = 0;
		for (Price price : prices) mean += price.get();
		return mean / prices.size();
	}

	
	@SuppressWarnings("unused")
	@Deprecated
	private static double estimatePriceMeanFromData0(List<Price> prices) {
		if (prices.size() == 0) return 0;
		
		double mean1 = 0;
		double mean2 = 0;
		for (Price price : prices) {
			mean1 += price.get();
			mean2 += (price.getHigh() - price.getLow())/2.0;
		}
		
		return (mean1 + mean2) / (2*prices.size());
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
		double price = getPrice().get();
		double lowPrice = price;
		double roi = getROIAdjusted(timeInterval);
		double unitBias = estimateUnitBias(timeInterval);
		lowPrice -= Math.max(unitBias, roi < 0 ? -price*roi : 0);
		
		return Math.max(Math.min(lowPrice, getLowPriceMean(timeInterval)), getLowestPrice(timeInterval));
	}
	
	
	@Override
	public double estimateHighPrice(long timeInterval) {
		double price = getPrice().get();
		double highPrice = price;
		double roi = getROIAdjusted(timeInterval);
		double unitBias = estimateUnitBias(timeInterval);
		highPrice += Math.max(unitBias, roi > 0 ? price*roi : 0);
		
		return Math.min(Math.max(highPrice, getHighPriceMean(timeInterval)), getHighestPrice(timeInterval));
	}

	
	private double estimateUnitBiasAtCurrentPrice(long timeInterval) {
		Price p = getPrice();
		if (p == null) return 0;

		double price = p.get();
		double roi = getROIAdjusted(timeInterval);
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
		double bias = estimateUnitBiasAtCurrentPrice(timeInterval);
		double roi = getROIAdjusted(timeInterval);
		double newPrice = roi > 0 ? price + Math.min(bias, price*roi) : price - Math.min(bias, -price*roi); 

		return Math.max(Math.min(newPrice, estimateHighPrice(timeInterval)), estimateLowPrice(timeInterval));
	}


	public double estimatePriceMean(long timeInterval) {
		List<Price> prices = getPrices(timeInterval);
		return estimatePriceMeanFromData(prices);
	}
	
	
	@Override
	public double estimateStopLoss(long timeInterval) {
		Price p = getPrice();
		if (p == null) return 0;
		double price = p.get();
		double bias = estimateUnitBiasAtCurrentPrice(timeInterval);
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
		double bias = estimateUnitBiasAtCurrentPrice(timeInterval);
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
	public List<EstimateStock> estimateStocks(List<Stock> stocks, long timeInterval) {
		if (stocks == null || stocks.size() == 0) return Util.newList(0);
		
		List<EstimateStock> estimateStocks = Util.newList(stocks.size());
		for (Stock stock : stocks) {
			Estimator estimator = duplicate(this, stock);
			double stopLoss = estimator.estimateStopLoss(timeInterval);
			double takeProfit = estimator.estimateTakeProfit(timeInterval);
			EstimateStock es = new EstimateStock(stock, stopLoss, takeProfit, false);
			//es.ratio = estimator.getROI(timeInterval) / estimator.getPositiveROISum(timeInterval);
			es.estimatedPrice = estimator.estimatePrice(timeInterval);
			es.estimatedPriceMean = estimator.estimatePriceMean(timeInterval);
			es.estimatedUnitBias = estimator.estimateUnitBias(timeInterval);
			es.estimatedUnitBiasFromData = estimator.estimateUnitBiasFromData(timeInterval);
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
		if (p == null || getLeverage() == 0) return 0;
		
		double price = p.get();
		double roi = getROI(timeInterval); //This ROI is only used to calculate the investment ratio.
		if (roi <= 0 || roi > refGlobalPositiveROISum || refGlobalPositiveROISum <= 0 || refGlobalInvestAmount <= 0)
			return 0;
		
		double takenAmount = roi / refGlobalPositiveROISum * refGlobalInvestAmount;
		double price0 = price*getLeverage();
		double takenVolume = takenAmount / price0;
		if (takenVolume == 0) return 0;
		
		double bias = estimateUnitBiasAtCurrentPrice(timeInterval);
		int found = 0;
		double biasedPrice = price0 + bias;
		for (int i = 1; i <= takenVolume; i++) {
			if (i * biasedPrice > takenAmount) {
				found = i - 1;
				break;
			}
		}
		return found * price0;
	}
	
	
	@Override
	public double estimateInvestAmount(long timeInterval) {
		return estimateInvestAmount(timeInterval, getPositiveROISum(timeInterval), getInvestAmount(timeInterval));
	}


	@Override
	public double estimateInvestVolume(long timeInterval, double refGlobalPositiveROISum, double refGlobalInvestAmount) {
		Price p = getPrice();
		if (p == null || getLeverage() == 0)
			return 0;
		else
			return estimateInvestAmount(timeInterval, refGlobalPositiveROISum, refGlobalInvestAmount) / (p.get()*getLeverage());
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
		double unitBias = estimateUnitBiasAtCurrentPrice(timeInterval);
		double nextTakeProfit = Math.min(isBuy() ? takeProfit + unitBias : takeProfit - unitBias, isBuy() ? getHighestPrice(timeInterval) : getLowestPrice(timeInterval));

		double nearEstimatedPrice = estimatePrice((long)(timeInterval/StockProperty.TIME_VIEW_PERIOD_RATIO));
		double price = isBuy() ? Math.min(nearEstimatedPrice, p.get()) : Math.max(nearEstimatedPrice, p.get());
		
		Invest invest1 = new Invest(isBuy(), volume/2, price, stopLoss, takeProfit, takeProfit);
		Invest invest2 = new Invest(isBuy(), volume/2, price, stopLoss, takeProfit, nextTakeProfit);

		invest1.margin = invest2.margin = volume/2 * price * getLeverage();
		invest1.unitBias = invest2.unitBias = unitBias;
		
		double lowPrice = estimateLowPrice(timeInterval);
		invest1.lowPrice = invest2.lowPrice = lowPrice;
		
		double highPrice = estimateHighPrice(timeInterval);
		invest1.highPrice = invest2.highPrice = highPrice;
		
		return new Invest[] {invest1, invest2};
	}


	@Override
	public Invest[] estimateDualInvest(long timeInterval) {
		return estimateDualInvest(timeInterval, getPositiveROISum(timeInterval), getInvestAmount(timeInterval));
	}


	protected static Estimator duplicate(Estimator estimator, Stock stock) {
		return new EstimatorAbstract() {
			
			/**
			 * Serial version UID for serializable class.
			 */
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
	
	