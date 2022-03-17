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

/**
 * This class represents stock information.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class StockInfo implements Serializable, Cloneable {


	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;


	protected PricePool pricePool = null;
	
	
	protected StockProperty property = new StockProperty();
	

	protected double leverage = StockProperty.LEVERAGE;
	
	
	public StockInfo(String code) {
		this.pricePool = referPricePool(code);
		this.pricePool = this.pricePool != null ? this.pricePool : new PricePool(code);
	}

	
	private StockInfo(String code, boolean flag) {
		this.pricePool = new PricePool(code);
	}
	
	
	protected PricePool referPricePool(String code) {
		return StockInfoStore.getCreatePricePool(code);
	}
	
	
	public String code() {
		return pricePool.code();
	}
	
	
	public PricePool getPricePool() {
		return pricePool;
	}
	
	
	public int getPriceCount() {
		return pricePool.size();
	}

	
	protected boolean containsPrice(Price price) {
		return pricePool.contains(price);
	}
	
	
	public int lookup(long timePoint) {
		return pricePool.lookup(timePoint);
	}

	
	public Price getLastPrice() {
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

	
	public List<Price> getPrices(long timeInterval) {
		return pricePool.gets(timeInterval);
	}
	
	
	protected List<Price> getInternalPrices() {
		return pricePool.getInternals();
	}
	
	
	public Price getFirstPriceWithin(long timeInterval) {
		return pricePool.getFirstWithin(timeInterval);
	}
	
	
	public boolean addPrice(Price price) {
		return pricePool.add(price, property.maxPriceCount);
	}
	
	
	protected boolean addPriceWithoutDuplicateTime(Price price) {
		return pricePool.addWithoutDuplicateTime(price, property.maxPriceCount);
	}

	
	protected boolean removePrice(Price price) {
		return pricePool.remove(price);
	}
	
	
	protected boolean checkPricePossibleAdded(long timePoint) {
		return pricePool.checkPricePossibleAdded(timePoint);
	}
	
	
	protected boolean checkPricePossibleAddedPrev(long timePoint) {
		return pricePool.checkPricePossibleAddedPrev(timePoint);
	}

	
	public double getLeverage() {
		return leverage;
	}
	
	
	protected boolean setLeverage(double leverage) {
		if (Double.isNaN(leverage)) return false;
		this.leverage = leverage;
		return true;
	}
	
	
	public double getUnitBias() {
		return pricePool.getUnitBias();
	}
	
	
	protected boolean setUnitBias(double unitBias) {
		return pricePool.setUnitBias(unitBias);
	}

	
	public StockProperty getProperty() {
		return property;
	}


	private boolean setBasicInfo(StockInfo otherInfo) {
		if (!this.code().equals(otherInfo.code())) return false;
		
		this.leverage = otherInfo.leverage;
		this.property.sync(otherInfo.getProperty());
		return true;
	}
	
	
	protected boolean sync(StockInfo otherInfo, long timeInterval, boolean removeRedundant) {
		if (!this.code().equals(otherInfo.code())) return false;
		
		setBasicInfo(otherInfo);
		return this.pricePool.sync(otherInfo.pricePool, timeInterval, this.property.maxPriceCount, removeRedundant);
	}
	
	
	protected boolean retain(StockInfo referredInfo, long timeInterval, boolean update) {
		if (!this.code().equals(referredInfo.code())) return false;
		
		setBasicInfo(referredInfo);
		return this.pricePool.retain(referredInfo.pricePool, timeInterval, update);
	}

		
	@SuppressWarnings("unused")
	private boolean sync(StockInfo otherInfo, long timeInterval, int maxPriceCount, boolean removeRedundant) {
		if (!this.code().equals(otherInfo.code())) return false;
		
		setBasicInfo(otherInfo);
		this.property.maxPriceCount = maxPriceCount;
		return this.pricePool.sync(otherInfo.pricePool, timeInterval, maxPriceCount, removeRedundant);
	}

	
	protected void cutPrices(long timeInterval) {
		pricePool.cut(timeInterval);
	}

	
	protected StockInfo pricePoolSync() {
		StockInfo si = new StockInfo(this.code(), false);
		
		si.setBasicInfo(this);
		boolean synced = si.pricePool.sync(this.pricePool, 0, 0, true);
		if (synced)
			return si;
		else
			return null;
	}
	
		
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}


}
