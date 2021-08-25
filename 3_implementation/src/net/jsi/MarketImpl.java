/**
 * JSI: JAGGED STRATEGY INVESTMENT 
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: jsi.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.jsi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class MarketImpl extends MarketAbstract implements QueryEstimator {

	
	private static final long serialVersionUID = 1L;


	protected double refLeverage = StockProperty.LEVERAGE;
	
	
	protected double balanceBase = 0;
	
	
	protected double balanceBias = 0;

	
	protected double marginFee = 0;
	
	
	protected double refUnitBias = StockProperty.UNIT_BIAS;
	
	
	protected List<StockGroup> groups = Util.newList(0);
	
	
	public MarketImpl(String name, double refLeverage, double unitBias) {
		super(name);
		this.refLeverage = refLeverage;
		this.refUnitBias = unitBias;
	}
	
	
	private double getBalance0(long timeInterval) {
		double profit = 0;
		for (StockGroup group : groups) {
			List<Stock> stocks = group.getStocks(timeInterval);
			for (Stock stock : stocks) {
				if (stock.isCommitted())
					profit += stock.getProfit(timeInterval) + stock.getValue(timeInterval);
			}
		}
		
		return balanceBase + profit;
	}

	
	@Override
	public double getBalance(long timeInterval) {
		return getBalance0(timeInterval) - balanceBias;
	}

	
	public double getBalanceBase() {
		return this.balanceBase;
	}
	
	
	public void setBalanceBase(double balanceBase) {
		this.balanceBase = balanceBase;
	}
	
	
	public double calcBalanceBias(double providedBalance, long timeInterval) {
		return getBalance0(timeInterval) - providedBalance;
	}
	
	
	public double getBalanceBias() {
		return balanceBias;
	}

	
	public void setBalanceBias(double balanceBias) {
		this.balanceBias = balanceBias;
	}

	
	private double getMargin0(long timeInterval) {
		double margin = 0;
		for (StockGroup group : groups) {
			margin += group.getMargin(timeInterval);
		}
		return margin;
	}

	
	@Override
	public double getMargin(long timeInterval) {
		return getMargin0(timeInterval) + marginFee;
	}
	
	
	public double calcMarginBias(double newMargin, long timeInterval) {
		return getMargin0(timeInterval) - newMargin;
	}
	
	
	public double getMarginFee() {
		return marginFee;
	}

	
	public void setMarginFee(double marginFee) {
		this.marginFee = marginFee;
	}

	
	@Override
	public double getTakenValue(long timeInterval) {
		double value = 0;
		for (StockGroup group : groups) {
			value += group.getTakenValue(timeInterval);
		}
		return value;
	}

	
	@Override
	public double getProfit(long timeInterval) {
		double profit = 0;
		for (StockGroup group : groups) {
			profit += group.getProfit(timeInterval);
		}
		return profit;
	}

	
	private double queryPositiveROISum(long timeInterval) {
		double sum = 0;
		for (StockGroup group : groups) {
			double roi = group.getROI(timeInterval);
			if (roi > 0) sum += roi;
		}
		return sum;
	}
	
	
	@Override
	public double calcTotalBias(long timeInterval) {
		double biasSum = 0;
		for (StockGroup group : groups) {
			biasSum += group.estimateUnitBias(timeInterval) * group.getVolume(timeInterval, false);
		}
		
		return biasSum;
	}


	private double estimateInvestAmount0(long timeInterval) {
		return calcInvestAmount(timeInterval);
	}
	
	
	private class Estimator0 extends EstimatorAbstract {
		
		private static final long serialVersionUID = 1L;

		protected Stock stock = null;
		
		public Estimator0(Stock stock) {
			this.stock = stock;
		}
		
		@Override
		public Price getPrice() {
			return stock.getPrice();
		}

		@Override
		public List<Price> getPrices(long timeInterval) {
			return stock.getPrices(timeInterval);
		}

		@Override
		public double getAverageTakenPrice(long timeInterval) {
			return stock.getAverageTakenPrice(timeInterval);
		}

		@Override
		public double getUnitBias() {
			return stock.getUnitBias();
		}

		@Override
		public double getROI(long timeInterval) {
			return stock.getROI(timeInterval);
		}

		@Override
		public double getPositiveROISum(long timeInterval) {
			return queryPositiveROISum(timeInterval);
		}

		@Override
		public double getInvestAmount(long timeInterval) {
			return estimateInvestAmount0(timeInterval);
		}

		@Override
		public boolean isBuy() {
			return stock.isBuy();
		}

	}
	
	
	@Override
	public Estimator getEstimator(String code, boolean buy) {
		int index = lookup(code, buy);
		if (index < 0) return null;
		StockGroup group = get(index);
		return group != null ? new Estimator0(group) : null;
	}
	
	
	public int size() {
		return groups.size();
	}
	
	
	public StockGroup get(int index) {
		return groups.get(index);
	}
	
	
	public StockGroup get(String code, boolean buy) {
		int index = lookup(code, buy);
		if (index >= 0)
			return get(index);
		else
			return null;
	}
	
	
	public int lookup(String code, boolean buy) {
		for (int i = 0; i < groups.size(); i++) {
			StockGroup group = groups.get(i);
			if (group.code().equals(code) && group.isBuy() == buy)
				return i;
		}
		
		return -1;
	}
	
	
	public boolean add(StockGroup group) {
		if (group == null || lookup(group.code(), group.isBuy()) >= 0)
			return false;
		else
			return groups.add(group);
	}
	
	
	public StockGroup remove(int index) {
		return groups.remove(index);
	}
	
	
	public StockGroup remove(String code, boolean buy) {
		int index = lookup(code, buy);
		if (index >= 0)
			return remove(index);
		else
			return null;
	}
	
	
	public StockGroup set(int index, StockGroup group) {
		if (group == null || lookup(group.code(), group.isBuy()) >= 0)
			return null;
		else {
			return groups.set(index, group);
		}
	}
	
	
	public StockGroup newGroup(String code, boolean buy, double leverage, Price price) {
		double unitBias = StockAbstract.calcMaxUnitBias(this.refUnitBias, leverage, this.refLeverage);
		final Market superMarket = this;
		StockGroup group = new StockGroup(code, buy, leverage, unitBias, price) {

			private static final long serialVersionUID = 1L;

			@Override
			public Market getSuperMarket() {
				return superMarket;
			}
			
		};
		return group;
	}

	
	public Stock getStock(String code, long timeInterval, long takenTimePoint) {
		for (StockGroup group : groups) {
			if (!group.code().equals(code)) continue;
			int index = group.lookup(timeInterval, takenTimePoint);
			if (index >= 0) return group.get(index);
		}
		
		return null;
	}
	
	
	public List<Stock> getStocks(String code, long timeInterval) {
		List<Stock> stocks = Util.newList(0);
		for (StockGroup group : groups) {
			if (!group.code().equals(code)) continue;
			stocks.addAll(group.getStocks(timeInterval));
		}

		return stocks;
	}
	
	
	public Stock addStock(String code, boolean buy, double refLeverage, double volume, long takenTimePoint, Price price) {
		StockGroup group = get(code, buy);
		if (group == null) {
			group = newGroup(code, buy, Double.isNaN(refLeverage) ? this.refLeverage : refLeverage, price);
			if (group == null || !add(group))
				return null;
			else {
				Stock stock = group.add(this.timeViewInterval, takenTimePoint, volume);
				if (stock == null) remove(code, buy);
				
				return stock;
			}
		}
		else {
			if (!group.setPrice(price)) return null;
			if (!Double.isNaN(refLeverage) && refLeverage != group.getLeverage())
				group.setLeverage(refLeverage);
			
			return group.add(this.timeViewInterval, takenTimePoint, volume);
		}
		
	}

	
	public Stock addStock(String code, boolean buy, double refLeverage, double volume, Price price) {
		return addStock(code, buy, refLeverage, volume, 0, price);
	}
	
	
	public Stock addStock(String code, boolean buy, double volume, Price price) {
		return addStock(code, buy, Double.NaN, volume, price);
	}
	
	
	public Stock removeStock(String code, boolean buy, long timeInterval, long takenTimePoint) {
		int found = lookup(code, buy);
		if (found < 0) return null;
		
		StockGroup group = get(found);
		int index = group.lookup(timeInterval, takenTimePoint);
		if (index >= 0)
			return group.remove(index);
		else
			return null;
	}
	
	
	public void updateEstimatedUnitBias(StockGroup group, long timeInterval) {
		double unitBias = group.estimateUnitBias(timeInterval);
		unitBias = Math.max(unitBias, StockAbstract.calcMaxUnitBias(this.refUnitBias, group.getLeverage(), this.refLeverage));
		group.setUnitBias(unitBias);
	}


	@Override
	public List<Stock> getStocks(long timeInterval) {
		List<Stock> stocks = Util.newList(0);
		for (StockGroup group : groups) {
			for (Stock stock : group.stocks) {
				StockImpl si = c(stock);
				if (si != null && si.isValid(timeInterval)) stocks.add(stock);
			}
		}

		return stocks;
	}
	
	
	public double getUnitBias() {
		return refUnitBias;
	}
	
	
	public double getRefLeverage() {
		return refLeverage;
	}
	
	
	public void setRefLeverage(double refLeverage) {
		this.refLeverage = refLeverage;
	}
	
	
	public Price newPrice(double price, double lowPrice, double highPrice, long time) {
		return new PriceImpl(price, lowPrice, highPrice, time);
	}
	
	
	@Override
	public List<String> getSupportStockCodes() {
		Universe u = getNearestUniverse();
		if (u != null)
			return u.getSupportStockCodes();
		else {
			List<Stock> stocks = getAllStocks();
			Set<String> codes = Util.newSet(0);
			for (Stock stock : stocks) codes.add(stock.code());
			
			return Util.sort(codes);
		}
	}


	private List<Stock> getAllStocks() {
		List<Stock> stocks = Util.newList(0);
		for (StockGroup group : groups) {
			stocks.addAll(group.getStocks(0));
		}
		
		return stocks;
	}
	
	
	@Override
	protected void reset() {
		super.reset();
		refLeverage = StockProperty.LEVERAGE;
		balanceBase = 0;
		balanceBias = 0;
		marginFee = 0;
		refUnitBias = StockProperty.UNIT_BIAS;
		groups.clear();
	}
	
	
	public boolean open(Reader in) {
		reset();
		
		try {
			BufferedReader reader = new BufferedReader(in);
			String line = null;
			boolean readHeader = false;
			boolean readInfo = false;
			boolean readDefaultCodes = false;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (line.isEmpty()) continue;
				
				String[] fields = line.split(Util.NOSPACE_DEFAULT_SEP);
				if (fields == null || fields.length < 1) continue;
				for (int i = 0; i < fields.length; i++) fields[i] = fields[i].trim();
				
				if (!readHeader) {
					readHeader = true;
					continue;
				}
				
				if (!readInfo) {
					if (!readHeader || !fields[0].equals(StockProperty.NOTCODE1)) continue;

					this.balanceBase = Double.parseDouble(fields[1]);
					this.balanceBias = Double.parseDouble(fields[2]);
					this.marginFee = Double.parseDouble(fields[3]);
					this.timeViewInterval = Long.parseLong(fields[4]);
					this.refLeverage = Double.parseDouble(fields[5]);
					this.setName(fields[6]);

					readInfo = true;
					continue;
				}
				
				if (!readDefaultCodes) {
					if (!readInfo || !fields[0].equals(StockProperty.NOTCODE2)) continue;
					Universe u = getNearestUniverse();
					if (u != null) u.addDefaultStockCodes(Arrays.asList(fields).subList(1, fields.length));
					
					readDefaultCodes = true;
					continue;
				}
				
				String code = fields[0];
				boolean buy = Boolean.parseBoolean(fields[1]);
				double leverage = Double.parseDouble(fields[2]);
				double volume = Double.parseDouble(fields[3]);
				double taken_price = Double.parseDouble(fields[4]);
				long taken_date = Long.parseLong(fields[5]);
				double price = Double.parseDouble(fields[6]);
				double low_price = Double.parseDouble(fields[7]);
				double high_price = Double.parseDouble(fields[8]);
				long price_date = Long.parseLong(fields[9]);
				double unit_bias = Double.parseDouble(fields[10]);
				double stop_loss = Double.parseDouble(fields[11]);
				double take_profit = Double.parseDouble(fields[12]);
				boolean committed = Boolean.parseBoolean(fields[13]);
				
				if (price_date == 0) {
					StockGroup group = get(code, buy);
					if (group != null) {
						Price p = newPrice(price, low_price, high_price, price_date);
						group.setPrice(p);
					}
				}
				else if (price_date == taken_date && taken_price == price) {
					Price p = newPrice(price, low_price, high_price, price_date);
					StockImpl stock = c(addStock(code, buy, leverage, volume, taken_date, p));
					if (stock == null) continue;
					
					stock.setUnitBias(unit_bias);
					stock.setStopLoss(stop_loss);
					stock.setTakeProfit(take_profit);
					stock.setCommitted(committed);
				}
			}
				
			return true;
		}
		catch (Exception e) { e.printStackTrace();}
		
		return false;
	}
	
	
	public boolean save(Writer out) {
		try {
			BufferedWriter writer = new BufferedWriter(out);
			
			String header = "code, buy, leverage, volume, taken_price, taken_date, price, low_price, high_price, price_date, unit_bias, stop_loss, take_profit, committed\n";
			writer.write(header);
			
			String fm1 = "%." + Util.DECIMAL_PRECISION + "f";
			String fm2 = "%d";
			String info = StockProperty.NOTCODE1 + ", " + fm1 + ", " + fm1 + ", " + fm1 + ", " + fm2 + ", " + fm1 + ", " + getName() + "\n";
			info = String.format(info, balanceBase, balanceBias, marginFee, timeViewInterval, refLeverage);
			writer.write(info);
			
			writer.write(StockProperty.NOTCODE2 + ", " + toCodesText(getDefaultStockCodes()) + "\n");

			for (StockGroup group : groups) {
				List<Price> prices = group.getPrices(0);
				for (Price price : prices) {
					StringBuffer buffer = new StringBuffer();
					buffer.append(group.code() + ", ");
					buffer.append(group.isBuy() + ", ");
					buffer.append("0, ");
					buffer.append("0, ");
					buffer.append("0, ");
					buffer.append("0, ");
					buffer.append(Util.format(price.get()) + ", ");
					buffer.append(Util.format(price.getLow()) + ", ");
					buffer.append(Util.format(price.getHigh()) + ", ");
					buffer.append(price.getTime() + ", ");
					buffer.append("0, ");
					buffer.append("0, ");
					buffer.append("0, ");
					buffer.append("false\n");
					writer.write(buffer.toString());
				}
				
				for (Stock stock : group.stocks) {
					StockImpl s = c(stock);
					if (s == null) continue;
					Price takenPrice = s.getTakenPrice(0);
					if (takenPrice == null) continue;
					
					StringBuffer buffer = new StringBuffer();
					buffer.append(s.code() + ", ");
					buffer.append(s.isBuy() + ", ");
					buffer.append(Util.format(s.getLeverage()) + ", ");
					buffer.append(Util.format(s.getVolume(0, true)) + ", ");
					buffer.append(Util.format(takenPrice.get()) + ", ");
					buffer.append(takenPrice.getTime() + ", ");
					buffer.append(Util.format(takenPrice.get()) + ", ");
					buffer.append(Util.format(takenPrice.getLow()) + ", ");
					buffer.append(Util.format(takenPrice.getHigh()) + ", ");
					buffer.append(takenPrice.getTime() + ", ");
					buffer.append(Util.format(s.getUnitBias()) + ", ");
					buffer.append(Util.format(s.getStopLoss()) + ", ");
					buffer.append(Util.format(s.getTakeProfit()) + ", ");
					buffer.append(s.isCommitted() + "\n");
					writer.write(buffer.toString());
				}
			}
			
			writer.flush();
			return true;
		}
		catch (Exception e) { e.printStackTrace();}
		
		return false;
	}
	
	
}
