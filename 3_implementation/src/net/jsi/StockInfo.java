package net.jsi;

import java.io.Serializable;
import java.util.List;

public class StockInfo implements Serializable, Cloneable {


	private static final long serialVersionUID = 1L;


	protected PricePool pricePool = null;
	
	
	protected StockProperty property = new StockProperty();
	

	protected double leverage = StockProperty.LEVERAGE;
	
	
	public StockInfo(String code) {
		this.pricePool = StockInfoStore.getPricePool(code);
		this.pricePool = this.pricePool != null ? this.pricePool : new PricePool(code);
	}

	
	protected String code() {
		return pricePool.code();
	}
	
	
	protected int getPriceCount() {
		return pricePool.getPriceCount();
	}

	
	protected Price getLastPrice() {
		return pricePool.getLastPrice();
	}

	
	protected Price getPrice(long timeInterval, long timePoint) {
		return pricePool.getPrice(timeInterval, timePoint);
	}

	
	protected Price getPrice(long timePoint) {
		return pricePool.getPrice(timePoint);
	}
	
	
	protected Price getPriceAt(int index) {
		return pricePool.getPrice(index);
	}

	
	protected List<Price> getPrices(long timeInterval) {
		return pricePool.getPrices(timeInterval);
	}
	
	
	protected List<Price> getInternalPrices() {
		return pricePool.getInternalPrices();
	}
	
	
	protected boolean addPrice(Price price) {
		return pricePool.addPrice(price, property.maxPriceCount);
	}
	
	
	protected boolean removePrice(Price price) {
		return pricePool.removePrice(price);
	}
	
	
	protected boolean checkPricePossibleAdded(long timePoint) {
		return pricePool.checkPricePossibleAdded(timePoint);
	}
	
	
	protected boolean checkPricePossibleAdded2(long timePoint) {
		return pricePool.checkPricePossibleAdded2(timePoint);
	}

	
	protected double getLeverage() {
		return leverage;
	}
	
	
	protected boolean setLeverage(double leverage) {
		this.leverage = leverage;
		return true;
	}
	
	
	protected double getUnitBias() {
		return pricePool.getUnitBias();
	}
	
	
	protected boolean setUnitBias(double unitBias) {
		return pricePool.setUnitBias(unitBias);
	}

	
	protected StockProperty getProperty() {
		return property;
	}


}
