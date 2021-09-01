/**
 * JSI: JAGGED STRATEGY INVESTMENT 
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: jsi.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.jsi;

import java.util.List;

public abstract class MarketAbstract implements Market {

	
	private static final long serialVersionUID = 1L;

	
	protected String name = StockProperty.NONAME;
	
	
	private long timeViewInterval = StockProperty.TIME_VIEW_INTERVAL;

	
	private long timeValidInterval = StockProperty.TIME_VALID_INTERVAL;

	
	public MarketAbstract() {

	}

	
	public MarketAbstract(String name) {
		this.name = name;
	}
	
	
	@Override
	public double getFreeMargin(long timeInterval) {
		return getBalance(timeInterval) + getProfit(timeInterval) - getMargin(timeInterval);
	}


	@Override
	public double getROI(long timeInterval) {
		double takenValue = getTakenValue(timeInterval);
		return takenValue != 0 ? getProfit(timeInterval) / takenValue : 0;
	}


	@Override
	public double getROIByLeverage(long timeInterval) {
		double margin = getMargin(timeInterval);
		return margin != 0 ? getProfit(timeInterval) / margin : 0;
	}


	@Override
	public double calcInvestAmount(long timeInterval) {
		return getFreeMargin(timeInterval) - calcTotalBias(timeInterval);
	}

	
	@Override
	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	@Override
	public long getTimeViewInterval() {
		return timeViewInterval;
	}
	
	
	public void setTimeViewInterval(long timeViewInterval) {
		this.timeViewInterval = timeViewInterval;
	}
	
	
	@Override
	public long getTimeValidInterval() {
		return timeValidInterval;
	}


	public void setTimeValidInterval(long timeValidInterval) {
		this.timeValidInterval = timeValidInterval;
	}
	
	
	@Override
	public StockImpl c(Stock stock) {
		if (stock instanceof StockImpl)
			return (StockImpl)stock;
		else
			return null;
	}


	@Override
	public Universe getNearestUniverse() {
		Market superMarket = this;
		if (superMarket instanceof Universe) return (Universe)superMarket;

		while ((superMarket = superMarket.getSuperMarket()) != null) {
			if (superMarket instanceof Universe) return (Universe)superMarket;
		}
		
		return null;
	}


	@Override
	public List<String> getSupportStockCodes() {
		Universe u = getNearestUniverse();
		return u != null ? u.getSupportStockCodes() : Util.newList(0);
	}


	@Override
	public List<String> getDefaultStockCodes() {
		Universe u = getNearestUniverse();
		return u != null ? u.getDefaultStockCodes() : Util.newList(0);
	}
	
	
	protected void reset() {
		this.timeViewInterval = StockProperty.TIME_VIEW_INTERVAL;
		this.timeValidInterval = StockProperty.TIME_VALID_INTERVAL;
	}
	
	
	public static String toCodesText(List<String> codes) {
		if (codes == null || codes.size() == 0) return "";
		
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < codes.size(); i++) {
			if ( i > 0) buffer.append(", ");
			buffer.append(codes.get(i));
		}
		
		return buffer.toString();
	}


	@Override
	public StockInfoStore getStore() {
		Universe u = getNearestUniverse();
		return u != null ? u.getStore() : null;
	}

	
	protected StockInfo getStoreInfo(String code) {
		StockInfoStore store = getStore();
		if (store == null)
			return null;
		else {
			StockInfo info = store.get(code);
			return info != null ? info : store.create(code);
		}
	}
	

}
