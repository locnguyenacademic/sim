package net.jsi;

public abstract class EstimatorAbstract implements Estimator {

	
	private static final long serialVersionUID = 1L;

	
	@Override
	public double estimateLowPrice(long timeInterval) {
		double baseLowPrice = getLowPrice(timeInterval);
		double lowPrice = baseLowPrice;
		double unitBias = estimateUnitBias(timeInterval);
		if (unitBias > 0) {
			double roi = getROI(timeInterval);
			double price = getPrice().get();
			lowPrice = price - Math.max(unitBias, price * (roi > 0 ? 0 : -roi));
		}
		
		return Math.max(baseLowPrice, lowPrice);
	}
	
	
	@Override
	public double estimateHighPrice(long timeInterval) {
		double baseHighPrice = getHighPrice(timeInterval);
		double highPrice = baseHighPrice;
		double unitBias = estimateUnitBias(timeInterval);
		if (unitBias > 0) {
			double roi = getROI(timeInterval);
			double price = getPrice().get();
			highPrice = price + Math.max(unitBias, price * (roi < 0 ? 0 : roi));
		}
		
		return Math.min(baseHighPrice, highPrice);
	}

	
	@Override
	public double estimateStopLoss(long timeInterval, double refMaxUnitBias) {
		Price p = getPrice();
		boolean buy = isBuy();
		if (p == null) return buy ? estimateLowPrice(timeInterval) : estimateHighPrice(timeInterval);
		
		double price = p.get();
		double stopLoss = price;
		double roi = getROI(timeInterval);
		double unitBias = estimateUnitBias(timeInterval);
		if (buy) {
			if (roi > 0) stopLoss -= price*roi;
			stopLoss -= Math.max(unitBias, refMaxUnitBias);
			stopLoss = Math.min(stopLoss, estimateLowPrice(timeInterval));
			
			return stopLoss < 0 ? 0 : stopLoss;
		}
		else {
			if (roi > 0) stopLoss += price*roi;
			stopLoss += Math.max(unitBias, refMaxUnitBias);
			double highPrice = estimateHighPrice(timeInterval);
			stopLoss = Math.max(stopLoss, highPrice);
			
			return Math.min(stopLoss, highPrice + unitBias);
		}
	}
	
	
	@Override
	public double estimateStopLoss(long timeInterval) {
		return estimateStopLoss(timeInterval, 0);
	}


	@Override
	public double estimateTakeProfit(long timeInterval) {
		Price p = getPrice();
		boolean buy = isBuy();
		if (p == null) return buy ? estimateHighPrice(timeInterval) : estimateLowPrice(timeInterval);
		
		double price = p.get();
		double takeProfit = price;
		double roi = getROI(timeInterval);
		double unitBias = estimateUnitBias(timeInterval);
		if (buy) {
			if (roi > 0) takeProfit += price*roi;
			double highPrice = estimateHighPrice(timeInterval);
			takeProfit = Math.max(takeProfit, highPrice);
			
			return Math.min(takeProfit, highPrice + unitBias);
		}
		else {
			if (roi > 0) takeProfit -= price*roi;
			takeProfit = Math.min(takeProfit, estimateLowPrice(timeInterval));
			
			return takeProfit < 0 ? 0 : takeProfit;
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
		
		double bias = 0;
		boolean buy = isBuy();
		if (buy) {
			double highPrice = estimateHighPrice(timeInterval);
			if (price >= highPrice) return 0;
			bias = price - estimateLowPrice(timeInterval);
		}
		else {
			double lowPrice = estimateLowPrice(timeInterval);
			if (price <= lowPrice) return 0;
			bias = estimateHighPrice(timeInterval) - price;
		}

		double unitBias = estimateUnitBias(timeInterval);
		bias = Math.max(bias, unitBias);
		
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
		return estimateTakenAmount(timeInterval, refGlobalPositiveROISum, refGlobalInvestAmount) / getPrice().get();
	}


	@Override
	public double estimateTakenVolume(long timeInterval) {
		return estimateTakenVolume(timeInterval, getPositiveROISum(timeInterval), getInvestAmount(timeInterval));
	}
	
	
}
