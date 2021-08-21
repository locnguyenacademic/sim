package net.jsi;

public abstract class EstimatorAbstract implements Estimator {

	
	private static final long serialVersionUID = 1L;

	
	@Override
	public double estimateLowPrice(long timeInterval) {
		double minLowPrice = getLowPrice(timeInterval);
		double lowPrice = minLowPrice;
		double unitBias = estimateBiasAveragePerUnit(timeInterval);
		if (unitBias > 0) {
			double roi = getROI(timeInterval);
			double price = getPrice().get();
			lowPrice = price - Math.max(unitBias, price * (roi > 0 ? 0 : -roi));
		}
		
		return Math.max(minLowPrice, lowPrice);
	}
	
	
	@Override
	public double estimateHighPrice(long timeInterval) {
		double maxHighPrice = getHighPrice(timeInterval);
		double highPrice = maxHighPrice;
		double unitBias = estimateBiasAveragePerUnit(timeInterval);
		if (unitBias > 0) {
			double roi = getROI(timeInterval);
			double price = getPrice().get();
			highPrice = price + Math.max(unitBias, price * (roi < 0 ? 0 : roi));
		}
		
		return Math.min(maxHighPrice, highPrice);
	}

	
	@Override
	public double estimateStopLoss(long timeInterval) {
		Price p = getPrice();
		double takenPrice = getAverageTakenPrice(0);
		boolean buy = isBuy();
		if (p == null || takenPrice <= 0) return buy ? estimateLowPrice(timeInterval) : estimateHighPrice(timeInterval);
		
		double price = p.get();
		double stopLoss = price;
		double roi = getROI(timeInterval);
		double unitBias = estimateBiasAveragePerUnit(timeInterval);
		if (buy) {
			double lowPrice = estimateLowPrice(timeInterval);
			stopLoss += price <= lowPrice ? 0 : price*roi;
			stopLoss -= unitBias;
			stopLoss = Math.min(stopLoss, lowPrice);
			return Math.max(stopLoss, takenPrice);
		}
		else {
			double highPrice = estimateHighPrice(timeInterval);
			stopLoss -= price >= highPrice ? 0 : price*roi;
			stopLoss += unitBias;
			stopLoss = Math.max(stopLoss, highPrice);
			return Math.min(stopLoss, takenPrice);
		}
	}
	
	
	@Override
	public double estimateTakeProfit(long timeInterval) {
		Price p = getPrice();
		double takenPrice = getAverageTakenPrice(0);
		boolean buy = isBuy();
		if (p == null || takenPrice <= 0) return buy ? estimateHighPrice(timeInterval) : estimateLowPrice(timeInterval);
		
		double price = p.get();
		double takeProfit = price;
		double roi = getROI(timeInterval);
		double unitBias = estimateBiasAveragePerUnit(timeInterval);
		if (buy) {
			double highPrice = estimateHighPrice(timeInterval);
			takeProfit += price >= highPrice ? 0 : price*roi;
			takeProfit += unitBias;
			takeProfit = Math.max(takeProfit, highPrice);
			return Math.max(takeProfit, takenPrice);
		}
		else {
			double lowPrice = estimateLowPrice(timeInterval);
			takeProfit -= price <= lowPrice ? 0 : price*roi;
			takeProfit -= unitBias;
			takeProfit = Math.min(takeProfit, lowPrice);
			return Math.min(takeProfit, takenPrice);
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

		double unitBias = estimateBiasAveragePerUnit(timeInterval);
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
