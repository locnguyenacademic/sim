package net.jsi;

import java.util.List;

public abstract class EstimatorAbstract implements Estimator {

	
	private static final long serialVersionUID = 1L;

	
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
		lowPrice -= Math.max(estimateUnitBias(timeInterval), roi < 0 ? -price*roi : 0);
		
		return Math.max(Math.min(lowPrice, getLowPriceMean(timeInterval)), getLowestPrice(timeInterval));
	}
	
	
	@Override
	public double estimateHighPrice(long timeInterval) {
		double price = getPrice().get();
		double highPrice = price;
		double roi = getROI(timeInterval);
		highPrice += Math.max(estimateUnitBias(timeInterval), roi > 0 ? price*roi : 0);
		
		return Math.min(Math.max(highPrice, getHighPriceMean(timeInterval)), getHighestPrice(timeInterval));
	}

	
	private double estimateBiasAtCurrentPrice(long timeInterval) {
		Price p = getPrice();
		if (p == null) return 0;

		double price = p.get();
		double roi = getROI(timeInterval);
		double unitBias = estimateUnitBias(timeInterval);
		double lowPrice = estimateLowPrice(timeInterval);
		double highPrice = estimateHighPrice(timeInterval);
		double bias = unitBias;
		if (price <= lowPrice) {
			if (roi > 0)
				bias = Math.min(price*roi, unitBias);
			else
				bias = Math.max(-price*roi, unitBias);
		}
		else if (price >= highPrice) {
			if (roi > 0)
				bias = Math.max(price*roi, unitBias);
			else 
				bias = -price*roi + unitBias;
		}
		else {
			if (roi > 0)
				bias = unitBias;
			else
				bias = Math.max(-price*roi, unitBias);
		}
		
		return Math.max(Math.min(bias, unitBias), getUnitBias());
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
		double takenPrice = getAverageTakenPrice(timeInterval);
		if (takenPrice <= 0) 
			return 0;
		else {
			double bias = estimateBiasAtCurrentPrice(timeInterval);
			if (isBuy())
				return Math.max(takenPrice - bias, takenPrice);
			else
				return Math.min(takenPrice + bias, takenPrice + estimateUnitBias(timeInterval));
		}
	}
	
	
	@Override
	public double estimateTakeProfit(long timeInterval) {
		double takenPrice = getAverageTakenPrice(timeInterval);
		if (takenPrice <= 0) 
			return 0;
		else {
			double bias = estimateBiasAtCurrentPrice(timeInterval);
			if (isBuy())
				return Math.min(takenPrice + bias, takenPrice + estimateUnitBias(timeInterval));
			else
				return Math.max(takenPrice - bias, takenPrice);
		}
	}


	@Override
	public double estimateTakenAmount(long timeInterval, double refGlobalPositiveROISum, double refGlobalInvestAmount) {
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
	public double estimateTakenAmount(long timeInterval) {
		return estimateTakenAmount(timeInterval, getPositiveROISum(timeInterval), getInvestAmount(timeInterval));
	}


	@Override
	public double estimateTakenVolume(long timeInterval, double refGlobalPositiveROISum, double refGlobalInvestAmount) {
		Price p = getPrice();
		if (p == null)
			return 0;
		else
			return estimateTakenAmount(timeInterval, refGlobalPositiveROISum, refGlobalInvestAmount) / p.get();
	}


	@Override
	public double estimateTakenVolume(long timeInterval) {
		return estimateTakenVolume(timeInterval, getPositiveROISum(timeInterval), getInvestAmount(timeInterval));
	}
	
	
}
