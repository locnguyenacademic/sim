package net.jsi;

import java.io.Serializable;
import java.util.List;

public class StockInfo implements Serializable, Cloneable {


	private static final long serialVersionUID = 1L;


	protected PricePool pricePool = null;
	
	
	protected StockProperty property = new StockProperty();
	

	protected double leverage = StockProperty.LEVERAGE;
	
	
	public StockInfo(String code) {
		this.pricePool = referPricePool(code);
		this.pricePool = this.pricePool != null ? this.pricePool : new PricePool(code);
	}

	
	protected PricePool referPricePool(String code) {
		return StockInfoStore.getPricePool(code);
	}
	
	
	protected String code() {
		return pricePool.code();
	}
	
	
	public PricePool getPricePool() {
		return pricePool;
	}
	
	protected int getPriceCount() {
		return pricePool.size();
	}

	
	protected boolean containsPrice(Price price) {
		return pricePool.contains(price);
	}
	
	
	public int lookup(long timePoint) {
		return pricePool.lookup(timePoint);
	}

	
	protected Price getLastPrice() {
		return pricePool.getLast();
	}

	
	protected Price getPrice(long timeInterval, long timePoint) {
		return pricePool.get(timeInterval, timePoint);
	}

	
	protected Price getPriceByTimePoint(long timePoint) {
		return pricePool.getByTimePoint(timePoint);
	}
	
	
	protected Price getPriceByIndex(int index) {
		return pricePool.getByIndex(index);
	}

	
	protected List<Price> getPrices(long timeInterval) {
		return pricePool.gets(timeInterval);
	}
	
	
	protected List<Price> getInternalPrices() {
		return pricePool.getInternals();
	}
	
	
	protected boolean addPrice(Price price) {
		return pricePool.add(price, property.maxPriceCount);
	}
	
	
	protected boolean addPriceWithoutDuplicate(Price price) {
		return pricePool.addWithoutDuplicate(price, property.maxPriceCount);
	}

	
	protected boolean removePrice(Price price) {
		return pricePool.remove(price);
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
		if (Double.isNaN(leverage)) return false;
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
