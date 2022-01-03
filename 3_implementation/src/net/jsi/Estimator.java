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


	class Invest implements Serializable, Cloneable {

		/**
		 * Serial version UID for serializable class.
		 */
		private static final long serialVersionUID = 1L;
		
		public boolean buy = true;
		
		public double volume = 0;
		
		public double price = 0;
		
		public double lowPrice = 0;

		public double highPrice = 0;

		public double unitBias = StockProperty.UNIT_BIAS;
		
		public double margin = 0;

		public double stopLoss = 0;
		
		public double takeProfit = 0;
		
		public double largeTakeProfit = 0;

		public Invest() {
			
		}
		
		public Invest(boolean buy, double volume, double price, double stopLoss, double takeProfit, double largeTakeProfit) {
			this.buy = buy;
			this.volume = volume;
			this.price = price;
			this.stopLoss = stopLoss;
			this.takeProfit = takeProfit;
			this.largeTakeProfit = largeTakeProfit;
		}
		
	}

	
	double getLeverage();
	
	
	Price getPrice();
	
	
	List<Price> getPrices(long timeInterval);

	
	double getAverageTakenPrice(long timeInterval);
	
	
	double getUnitBias();
	
	
	double getROI(long timeInterval);
	
	
	double getPositiveROISum(long timeInterval);
	
	
	double getInvestAmount(long timeInterval);

	
	double estimateUnitBias(long timeInterval);
	
	
	double estimateUnitBiasFromData(long timeInterval);

	
	double estimateLowPrice(long timeInterval);
	
	
	double estimateHighPrice(long timeInterval);

	
	double estimatePrice(long timeInterval);

	
	double estimatePriceMean(long timeInterval);

		
	double estimateStopLoss(long timeInterval);

	
	double estimateTakeProfit(long timeInterval);

	
	List<EstimateStock> estimateStocks(List<Stock> stocks, long timeInterval);
	

	double estimateInvestAmount(long timeInterval, double refGlobalPositiveROISum, double refGlobalInvestAmount);

		
	double estimateInvestAmount(long timeInterval);
	
	
	double estimateInvestVolume(long timeInterval, double refGlobalPositiveROISum, double refGlobalInvestAmount);

	
	double estimateInvestVolume(long timeInterval);
	
	
	Invest[] estimateDualInvest(long timeInterval, double refGlobalPositiveROISum, double refGlobalInvestAmount);
	
	
	Invest[] estimateDualInvest(long timeInterval);

	
	boolean isBuy();
	
	
}
