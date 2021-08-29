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
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class MarketImpl extends MarketAbstract implements QueryEstimator {

	
	private static final long serialVersionUID = 1L;


	private long timeStartPoint = 0;
	
	
	private double refLeverage = StockProperty.LEVERAGE;
	
	
	private double refUnitBias = StockProperty.UNIT_BIAS;
	
	
	private double balanceBase = 0;
	
	
	protected double balanceBias = 0;

	
	protected double marginFee = 0;
	
	
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
		
		return getBalanceBase() + profit;
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


	private static class Estimator0 extends EstimatorAbstract {
		
		private static final long serialVersionUID = 1L;

		protected Stock stock = null;
		
		protected MarketImpl market = null;
		
		public Estimator0(Stock stock, MarketImpl market) {
			this.stock = stock;
			this.market = market;
		}
		
		@Override
		public double getLeverage() {
			return stock.getLeverage();
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
			return market.queryPositiveROISum(timeInterval);
		}

		@Override
		public double getInvestAmount(long timeInterval) {
			return market.calcInvestAmount(timeInterval);
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
		return group != null ? new Estimator0(group, this) : null;
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
	
	
	public List<StockGroup> getGroups(long timeInterval) {
		List<StockGroup> newGroups = Util.newList(0);
		for (StockGroup group : this.groups) {
			if (group.containsStocks(timeInterval)) newGroups.add(group);
		}
		
		return newGroups;
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
				Stock stock = group.add(this.getTimeViewInterval(), takenTimePoint, volume);
				if (stock == null) remove(code, buy);
				
				return stock;
			}
		}
		else {
			if (!group.setPrice(price)) {
				if (group.getPrice(price.getTime()) ==  null) return null;
			}
			if (!Double.isNaN(refLeverage) && refLeverage != group.getLeverage())
				group.setLeverage(refLeverage, true);
			
			return group.add(this.getTimeViewInterval(), takenTimePoint, volume);
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
	
	
	public Price getPrice(String code, boolean buy, long timePoint) {
		StockGroup group = get(code, buy);
		return group != null ? group.getPrice(timePoint) : null;
	}
	
	
	public void updateEstimatedUnitBias(StockGroup group, long timeInterval) {
		double unitBias = group.estimateUnitBias(timeInterval);
		unitBias = Math.max(unitBias, StockAbstract.calcMaxUnitBias(this.refUnitBias, group.getLeverage(), this.refLeverage));
		group.setUnitBias(unitBias, true);
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
	
	
	@Override
	public double getUnitBias() {
		return refUnitBias;
	}
	
	
	public void setUnitBias(double refUnitBias) {
		this.refUnitBias = refUnitBias;
		
		MarketImpl placedMarket = getPlacedMarket();
		if (placedMarket != null && placedMarket != this) placedMarket.setUnitBias(refUnitBias);
	}
	
	
	@Override
	public double getLeverage() {
		return refLeverage;
	}
	
	
	public void setLeverage(double refLeverage) {
		this.refLeverage = refLeverage;
		
		MarketImpl placedMarket = getPlacedMarket();
		if (placedMarket != null && placedMarket != this) placedMarket.setLeverage(refLeverage);
	}
	
	
	@Override
	public void setTimeViewInterval(long timeViewInterval) {
		super.setTimeViewInterval(timeViewInterval);
		
		MarketImpl placedMarket = getPlacedMarket();
		if (placedMarket != null && placedMarket != this) placedMarket.setTimeViewInterval(timeViewInterval);
	}


	@Override
	public void setTimeValidInterval(long timeValidInterval) {
		super.setTimeValidInterval(timeValidInterval);
		
		MarketImpl placedMarket = getPlacedMarket();
		if (placedMarket != null && placedMarket != this) placedMarket.setTimeValidInterval(timeValidInterval);
	}


	public long getTimeStartPoint() {
		return timeStartPoint;
	}
	
	
	public void setTimeStartPoint(long timeStartPoint) {
		this.timeStartPoint = timeStartPoint;
		
		MarketImpl placedMarket = getPlacedMarket();
		if (placedMarket != null && placedMarket != this) placedMarket.setTimeStartPoint(timeStartPoint);
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
	
	
	public MarketImpl getPlacedMarket() {
		Universe u = this.getNearestUniverse();
		return u != null ? u.c(u.getPlacedMarket(this.getName())) : null;
	}
	
	
	public boolean open(Reader in) {
		reset();
		
		long timeViewInterval = StockProperty.TIME_VIEW_INTERVAL;
		long timeValidInterval = StockProperty.TIME_VALID_INTERVAL;
		setTimeViewInterval(0);
		setTimeValidInterval(0);
		try {
			MarketImpl market = this;
			BufferedReader reader = new BufferedReader(in);
			String line = null;
			boolean readHeader = false;
			boolean readInfo = false;
			boolean readDefaultCodes = false;
			boolean readMainStocks = false;
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
					timeViewInterval = Long.parseLong(fields[4]);
					timeValidInterval = Long.parseLong(fields[5]);
					this.setTimeStartPoint(Long.parseLong(fields[6]));
					this.refLeverage = Double.parseDouble(fields[7]);
					this.setName(fields[8]);

					readInfo = true;
					continue;
				}
				
				if (!readDefaultCodes) {
					if (!readInfo || !fields[0].equals(StockProperty.NOTCODE2)) continue;
					Universe u = this.getNearestUniverse();
					if (u != null) u.addDefaultStockCodes(Arrays.asList(fields).subList(1, fields.length));
					
					readDefaultCodes = true;
					continue;
				}
				
				if (!readMainStocks && fields[0].equals(StockProperty.NOTCODE3)) {
					readMainStocks = true;
					market = getPlacedMarket();
					if (market == null) break;
				}
				
				if (fields[0].equals(StockProperty.NOTCODE3)) continue;
				
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
				
				Price p = market.newPrice(price, low_price, high_price, price_date);
				if (market == this)
					p = market.newPrice(price, low_price, high_price, price_date);
				else
					p = getPrice(code, buy, price_date);
				p = p != null ? p : market.newPrice(price, low_price, high_price, price_date);
				
				if (taken_date == 0) {
					StockGroup group = market.get(code, buy);
					
					if (!p.isValid())
						continue;
					else {
						p.setTag(false);
						if (group != null) {
							p.setPriceRatio(group.getProperty().priceRatio);
							group.setPrice(p);
						}
						else {
							group = market.newGroup(code, buy, leverage, p);
							p.setPriceRatio(group.getProperty().priceRatio);
							market.add(group);
						}
						p.setTag(null);
					}
				}
				else if (price_date == taken_date && taken_price == price) {
					p.setTag(false);
					StockImpl stock = market.c(market.addStock(code, buy, leverage, volume, taken_date, p));
					p.setTag(null);
					if (stock == null) continue;
					
					stock.setUnitBias(unit_bias, true);
					stock.setStopLoss(stop_loss);
					stock.setTakeProfit(take_profit);
					stock.setCommitted(committed);
					
					StockGroup group = stock.getGroup();
					if (group != null) {
						group.setLeverage(leverage, true);
						group.setUnitBias(unit_bias, true);
						p.setPriceRatio(group.getProperty().priceRatio);
					}
				}
			}
			
			if (market != null) {
				List<StockGroup> removedGroups = Util.newList(0);
				for (StockGroup group : market.groups) {if (group.size() == 0) removedGroups.add(group);}
				for (StockGroup group : removedGroups) market.groups.remove(group);
			}
			
			setTimeViewInterval(timeViewInterval);
			setTimeValidInterval(timeValidInterval);
			applyPlaced();
			
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
			String info = StockProperty.NOTCODE1 + ", " + fm1 + ", " + fm1 + ", " + fm1 + ", " + fm2 + ", " + fm2 + ", " + fm2 + ", " + fm1 + ", " + getName() + "\n";
			info = String.format(info, balanceBase, balanceBias, marginFee, getTimeViewInterval(), getTimeValidInterval(), getTimeStartPoint(), refLeverage);
			writer.write(info);
			
			writer.write(StockProperty.NOTCODE2 + ", " + toCodesText(getDefaultStockCodes()) + "\n");
			writeGroups(groups, writer);
			
			writer.write(StockProperty.NOTCODE3 + "\n");
			MarketImpl placedMarket = getPlacedMarket();
			if (placedMarket != null) writeGroups(placedMarket.groups, writer);

			writer.flush();
			return true;
		}
		catch (Exception e) { e.printStackTrace();}
		
		return false;
	}
	
	
	private void writeGroups(Collection<StockGroup> groups, Writer writer) throws IOException {
		if (groups == null) return;
		
		for (StockGroup group : groups) {
			List<Price> prices = group.getPrices(0);
			for (Price price : prices) {
				StringBuffer buffer = new StringBuffer();
				buffer.append(group.code() + ", ");
				buffer.append(group.isBuy() + ", ");
				buffer.append(group.getLeverage() + ", ");
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
		
	}
	
	
	protected void applyPlaced(MarketImpl placedMarket, long timeViewInterval, long timeValidInterval) {
		if (placedMarket == null || placedMarket == this) return;
		
		List<Stock> placedStocks = placedMarket.getStocks(timeValidInterval);
		for (Stock ps : placedStocks) {
			StockImpl placedStock = placedMarket.c(ps);
			if (placedStock == null) continue;
			StockGroup thisGroup = get(placedStock.code(), placedStock.isBuy());
			if (thisGroup == null) continue;
			StockGroup placedGroup = placedMarket.get(placedStock.code(), placedStock.isBuy());
			if (placedGroup == null) continue;
			
			Price placedTakenPrice = placedStock.getTakenPrice(timeValidInterval);
			if (placedTakenPrice == null) continue;
			
			Price thisPrice = thisGroup.getPrice();
			boolean valid = ((thisPrice.get() <= placedTakenPrice.get() && placedStock.isBuy()) || (thisPrice.get() >= placedTakenPrice.get() && !placedStock.isBuy()));
			valid = valid && (placedTakenPrice.getTime() <= thisPrice.getTime());
			if (!valid) continue;
			
			if (!placedGroup.remove(ps)) continue;
			if (placedGroup.size() == 0) placedMarket.remove(placedGroup.getName(), placedGroup.isBuy());
			
			thisGroup.add(timeViewInterval, thisPrice.getTime(), placedStock.getVolume(timeViewInterval, false));
		}

	}
	

	public void applyPlaced() {
		applyPlaced(getPlacedMarket(), getTimeViewInterval(), getTimeValidInterval());
	}
	
	

}
