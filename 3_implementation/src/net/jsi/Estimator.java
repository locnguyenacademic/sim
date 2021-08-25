/**
 * JSI: JAGGED STRATEGY INVESTMENT 
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: jsi.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
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


	double estimateInvestAmount(long timeInterval, double refGlobalPositiveROISum, double refGlobalInvestAmount);

		
	double estimateInvestAmount(long timeInterval);
	
	
	double estimateInvestVolume(long timeInterval, double refGlobalPositiveROISum, double refGlobalInvestAmount);

	
	double estimateInvestVolume(long timeInterval);
	
	
	boolean isBuy();
	
	
}
