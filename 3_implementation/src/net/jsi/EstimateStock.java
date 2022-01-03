package net.jsi;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public class EstimateStock implements Serializable, Cloneable {
	
	
	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;

	
	public Stock stock = null;
	
	
	public double estimatedStopLoss = 0;
	
	
	public double estimatedTakeProfit = 0;
	
	
	public boolean estimated = false;
	
	
	public double estimatedPrice = 0;
	
	
	public double estimatedPriceMean = 0;
	
	
	public double estimatedUnitBias = StockProperty.UNIT_BIAS;
	
	
	public double estimatedUnitBiasFromData = StockProperty.UNIT_BIAS;

	
	public EstimateStock(Stock stock) {
		this.stock = stock;
	}
	
	
	public EstimateStock(Stock stock, double stopLoss, double takeProfit, boolean estimated) {
		this(stock);
		this.estimatedStopLoss = stopLoss;
		this.estimatedTakeProfit = takeProfit;
		this.estimated = estimated;
	}
	
	
	public double getAverageTakenPrice(long timeInterval) {
		if (stock instanceof StockImpl) {
			Price takenPrice = ((StockImpl)stock).getTakenPrice(timeInterval);
			return takenPrice != null ? takenPrice.get() : 0;
		}
		else {
			double value = 0;
			int n = 0;
			List<Stock> stocks = ((StockGroup)stock).getStocks(timeInterval);
			for (Stock stock : stocks) {
				if (!stock.isCommitted()) {
					value += stock.getTakenValue(timeInterval);
					n++;
				}
			}
			
			return n > 0 ? value/n : 0;
		}
	}
	
	
	public static EstimateStock get(String code, boolean buy, Collection<EstimateStock> estimateStocks) {
		if (estimateStocks == null) return null;
		for (EstimateStock es : estimateStocks) {
			if (es.estimated && es.stock != null && es.stock.code().equals(code) && es.stock.isBuy() == buy)
				return es;
		}

		return null;
	}
	
	
}


