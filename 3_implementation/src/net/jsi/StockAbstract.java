/**
 * JSI: JAGGED STRATEGY INVESTMENT 
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: jsi.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.jsi;

import java.util.List;

public abstract class StockAbstract extends EstimatorAbstract implements Stock {

	
	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;


	private StockInfo info = null;
	
	
	private boolean buy = true;
	
	
	public StockAbstract(String code, boolean buy) {
		this(code, buy, null);
	}

	
	public StockAbstract(String code, boolean buy, Price price) {
		this.buy = buy;
		
		StockInfoStore store = getStore();
		this.info = store != null ? store.getCreate(code) : new StockInfo(code);
		
		if (price != null) this.setPrice(price);
	}
	
	
	@Override
	public boolean setPrice(Price price) {
		if (price == null)
			return false;
		else if (info.checkPricePossibleAdded(price.getTime()))
			return info.addPrice(price);
		else
			return info.containsPrice(price);
	}
	
	
	@Override
	public boolean addPrice(Price price) {
		return info.addPrice(price);
	}
	
	
	public int getPriceCount() {
		return info.getPriceCount();
	}
	
	
	@Override
	public Price getPrice() {
		return info.getLastPrice();
	}
	
	
	@Override
	public Price getPriceByTimePoint(long timePoint) {
		return info.getPriceByTimePoint(timePoint);
	}
	
	
	protected Price getPriceByIndex(int index) {
		return info.getPriceByIndex(index);
	}
	
	
	protected Price getPrice(long timeInterval, long timePoint) {
		return info.getPrice(timeInterval, timePoint);
	}
	
	
	@Override
	public List<Price> getPrices(long timeInterval) {
		return info.getPrices(timeInterval);
	}
	
	
	@Override
	public List<Price> getInternalPrices() {
		return info.getInternalPrices();
	}


	public long getPriceTimePoint() {
		Price price = getPrice();
		if (price != null)
			return price.getTime();
		else
			return 0;
	}
	
	
	public boolean setPriceTimePoint(long priceTimePoint) {
		Price price = getPrice();
		if (price == null)
			return false;
		else if (info.checkPricePossibleAddedPrev(priceTimePoint)) {
			price.setTime(priceTimePoint);
			return true;
		}
		else
			return false;
	}
	
	
	@Override
	public double getROI(long timeInterval) {
		double takenValue = getTakenValue(timeInterval);
		if (takenValue == 0) return 0;
		return getProfit(timeInterval) / takenValue;
	}
	
	
	@Override
	public double getROIByLeverage(long timeInterval) {
		double margin = getMargin(timeInterval);
		if (margin == 0) return 0;
		return getProfit(timeInterval) / margin;
	}
	
	
	@Override
	public double getUnitBias() {
		return info.getUnitBias();
	}
	
	
	@Override
	public boolean setUnitBias(double unitBias) {
		return info.setUnitBias(unitBias);
	}
	
	
	public double calcBias(long timeInterval) {
		return estimateUnitBias(timeInterval) * getVolume(timeInterval, false);
	}
	
	
	@Override
	public double getPriceOscill(long timeInterval) {
		Price price = info.getFirstPriceWithin(timeInterval);
		if (price == null)
			return 0;
		else
			return getPrice().get() - price.get();
	}
	
	
	public double calcOscill(long timeInterval) {
		return getPriceOscill(timeInterval) * getVolume(timeInterval, false);
	}

	
	public double calcOscillAbs(long timeInterval) {
		return Math.abs(calcOscill(timeInterval));
	}

	
	@Override
	public double calcOscillRatio(long timeInterval) {
		Price price = info.getFirstPriceWithin(timeInterval);
		if (price == null)
			return 0;
		else
			return (getPrice().get() - price.get()) / price.get();
	}


	@Override
	public double calcOscillRatioAbs(long timeInterval) {
		return Math.abs(calcOscillRatio(timeInterval));
	}

	
	public static double calcMaxUnitBias(double unitBias, double leverage, double refBaseLeverage) {
		//Referred leverage (base leverage) is often larger than the specified leverage.
		if (leverage == 0 && refBaseLeverage == 0)
			return unitBias;
		else if (leverage == 0 && refBaseLeverage != 0)
			return unitBias / refBaseLeverage;
		else
			return Math.max(unitBias, unitBias*refBaseLeverage/leverage);
	}
	
	
	@Override
	public double getPriceMinMaxDev(long timeInterval) {
		double[] minmax = info.getPricePool().getMinMax(timeInterval);
		return (minmax[1] - minmax[0]) / 2;
	}

	
	public double calcMinMaxDev(long timeInterval) {
		return getPriceMinMaxDev(timeInterval) * getVolume(timeInterval, false);
	}

	
	@Override
	public double getPositiveROISum(long timeInterval) {
		double roi = getROI(timeInterval);
		return roi > 0 ? roi : 0;
	}


	@Override
	public double getInvestAmount(long timeInterval) {
		return getProfit(timeInterval) - getMargin(timeInterval);
	}


	@Override
	public String code() {
		return info.code();
	}
	
	
	@Override
	public boolean isBuy() {
		return buy;
	}
	
	
	@Override
	public double getLeverage() {
		return info.getLeverage();
	}


	@Override
	public boolean setLeverage(double leverage) {
		return info.setLeverage(leverage);
	}
	
	
	@Override
	public void setBasicInfo(Stock stock) {
		if (!(stock instanceof StockAbstract)) return;
		StockAbstract sa = (StockAbstract)stock;
		
		this.buy = sa.buy;
		this.info = sa.info;
	}


	@Override
	public StockProperty getProperty() {
		return info.getProperty();
	}

	
	@Override
	public long getDividendTimePoint(long timeInterval) {
		double dividend = getDividend(timeInterval);
		if (dividend > 0)
			return getProperty().getDividendTime();
		else
			return 0;
	}
	
	
	@Override
	public String getCategory() {
		return getProperty().getCategory();
	}


	public abstract StockGroup getGroup();
	
	
	public StockGroup getDualGroup() {
		StockGroup group = getGroup();
		return group != null ? group.getDualGroup() : null;
	}
	
	
	protected StockGroup getOtherGroup() {
		StockGroup group = getGroup();
		return group != null ? group.getOtherGroup() : null;
	}
	

	@Override
	public String toString() {
		return info.code();
	}
	
	
}
