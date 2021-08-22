package net.jsi;

import java.io.Serializable;
import java.util.List;

public interface Estimator extends Serializable, Cloneable {


	Price getPrice();
	
	
	List<Price> getPrices(long timeInterval);

	
	double getAverageTakenPrice(long timeInterval);
	
	
	double getUnitBias();
	
	
	double getROI(long timeInterval);
	
	
	double getPositiveROISum(long timeInterval);
	
	
	double getInvestAmount(long timeInterval);

	
	double estimateUnitBias(long timeInterval);
	
	
	double estimateLowPrice(long timeInterval);
	
	
	double estimateHighPrice(long timeInterval);

	
	double estimatePrice(long timeInterval);

	
	double estimateStopLoss(long timeInterval);

	
	double estimateTakeProfit(long timeInterval);


	double estimateTakenAmount(long timeInterval, double refGlobalPositiveROISum, double refGlobalInvestedAmount);

		
	double estimateTakenAmount(long timeInterval);
	
	
	double estimateTakenVolume(long timeInterval, double refGlobalPositiveROISum, double refGlobalInvestedAmount);

	
	double estimateTakenVolume(long timeInterval);
	
	
	boolean isBuy();
	
	
}
