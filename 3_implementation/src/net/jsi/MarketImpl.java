package net.jsi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.List;

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
		return getBalance0(timeInterval) + balanceBias;
	}

	
	public double getBalanceBase() {
		return this.balanceBase;
	}
	
	
	public void setBalanceBase(double balanceBase) {
		this.balanceBase = balanceBase;
	}
	
	
	public double calcBalanceBias(double givenBalance, long timeInterval) {
		return givenBalance - getBalance0(timeInterval);
	}
	
	
	public double getBalanceBias() {
		return marginFee;
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
	
	
	public double calcMargin(double givenMargin, long timeInterval) {
		return givenMargin - getMargin0(timeInterval);
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
			if (group.code().equals(code) && group.isBuy() == buy) return i;
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
	
	
	private List<Stock> getAllStocks() {
		List<Stock> stocks = Util.newList(0);
		for (StockGroup group : groups) {
			stocks.addAll(group.getStocks(0));
		}
		
		return stocks;
	}
	
	
	private void reset() {
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
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (line.isEmpty()) continue;
				
				String[] fields = line.split(",");
				if (fields == null || fields.length < 14) continue;
				
				if (!readHeader) {
					readHeader = true;
					continue;
				}
				
				if (!readInfo) {
					if (!readHeader) continue;
					
					this.refLeverage = Double.parseDouble(fields[2].trim());
					this.refUnitBias = Double.parseDouble(fields[3].trim());
					this.balanceBase = Double.parseDouble(fields[4].trim());
					this.timeViewInterval = Long.parseLong(fields[5].trim());

					readInfo = true;
					continue;
				}
				
				String code = fields[0];
				boolean buy = Boolean.parseBoolean(fields[1].trim());
				double leverage = Double.parseDouble(fields[2].trim());
				double volume = Double.parseDouble(fields[3].trim());
				double taken_price = Double.parseDouble(fields[4].trim());
				long taken_date = Long.parseLong(fields[5].trim());
				double price = Double.parseDouble(fields[6].trim());
				double low_price = Double.parseDouble(fields[7].trim());
				double high_price = Double.parseDouble(fields[8].trim());
				long price_date = Long.parseLong(fields[9].trim());
				double unit_bias = Double.parseDouble(fields[10].trim());
				double stop_loss = Double.parseDouble(fields[11].trim());
				double take_profit = Double.parseDouble(fields[12].trim());
				boolean committed = Boolean.parseBoolean(fields[13].trim());
				
				if (taken_date > 0) {
					if (price_date != taken_date || taken_price != price) continue;
					
					Price p = newPrice(price, low_price, high_price, price_date);
					StockImpl stock = c(addStock(code, buy, leverage, volume, taken_date, p));
					if (stock == null) continue;
					
					stock.setUnitBias(unit_bias);
					stock.setStopLoss(stop_loss);
					stock.setTakeProfit(take_profit);
					stock.setCommitted(committed);
				}
				else if (price_date != 0) {
					StockGroup group = get(code, buy);
					if (group != null) {
						Price p = newPrice(price, low_price, high_price, price_date);
						group.setPrice(p);
					}
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
			
			String info = "-1, true, " + "%." + Util.DECIMAL_PRECISION + "f, %." + Util.DECIMAL_PRECISION + "f, %." + Util.DECIMAL_PRECISION + "f, " + timeViewInterval + ", 0, 0, 0, 0, 0, 0, 0, false\n";
			info = String.format(info, refLeverage, refUnitBias, balanceBase);
			writer.write(info);
			
			List<Stock> stocks = getAllStocks();
			for (Stock stock : stocks) {
				StockImpl s = c(stock);
				StringBuffer buffer = new StringBuffer();
				List<Price> prices = stock.getPrices(0);
				
				Price takenPrice = s.getTakenPrice(0);
				for (Price price : prices) {
					boolean flag = takenPrice instanceof TakenPrice ? ((TakenPrice)takenPrice).getPrice() == price : takenPrice == price;
					
					buffer.append(s.code() + ", ");
					buffer.append(s.isBuy() + ", ");
					buffer.append((flag ? Util.format(s.getLeverage()) : 0) + ", ");
					buffer.append((flag ? Util.format(s.getVolume(0, true)) : 0) + ", ");
					buffer.append((flag ? Util.format(s.getAverageTakenPrice(0)) : 0) + ", ");
					buffer.append((flag ? s.getTakenTimePoint(0) : 0) + ", ");
					buffer.append(Util.format(price.get()) + ", ");
					buffer.append(Util.format(price.getLow()) + ", ");
					buffer.append(Util.format(price.getHigh()) + ", ");
					buffer.append(price.getTime() + ", ");
					buffer.append((flag ? Util.format(s.getUnitBias()) : 0) + ", ");
					buffer.append((flag ? Util.format(s.getStopLoss()) : 0) + ", ");
					buffer.append((flag ? Util.format(s.getTakeProfit()) : 0) + ", ");
					buffer.append((flag ? s.isCommitted() : false) + "\n");
				}
				
				writer.write(buffer.toString());
			}
			
			writer.flush();
			
			return true;
		}
		catch (Exception e) { e.printStackTrace();}
		
		return false;
	}
	
	
}
