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
	
	
	private MarketImpl placedMarket = null;
	
	
	public MarketImpl(String name, double refLeverage, double unitBias) {
		this(name, refLeverage, unitBias, true);
	}
	
	
	private MarketImpl(String name, double refLeverage, double unitBias, boolean createPlacedMarket) {
		super(name);
		this.refLeverage = refLeverage;
		this.refUnitBias = unitBias;
		
		if (createPlacedMarket) placedMarket = newPlacedMarket();
	}

	
	protected MarketImpl newPlacedMarket() {
		MarketImpl thisMarket = this;
		this.placedMarket = new MarketImpl(thisMarket.getName(), thisMarket.getLeverage(), thisMarket.getUnitBias(), false) {

			private static final long serialVersionUID = 1L;

			@Override
			public Market getSuperMarket() {
				return thisMarket.getSuperMarket();
			}

			@Override
			public Market getDualMarket() {
				return thisMarket;
			}

			@Override
			public double getBalanceBase() {
				return thisMarket.calcInvestAmount(thisMarket.getTimeViewInterval());
			}

		};
		placedMarket.setTimeViewInterval(getTimeViewInterval());
		placedMarket.setTimeValidInterval(getTimeValidInterval());
		
		return placedMarket;
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
	
	
	private StockGroup newGroup(String code, boolean buy, long timePoint) {
		StockInfo info = getStore().get(code);
		if (info == null) return null;
		Price price = info.getPriceByTimePoint(timePoint);
		if (price == null) return null;
		
		final Market superMarket = this;
		StockGroup group = new StockGroup(code, buy) {
			private static final long serialVersionUID = 1L;
			
			@Override
			public Market getSuperMarket() {
				return superMarket;
			}
		};
		
		return group;
	}
	
	
	private StockGroup newGroup(String code, boolean buy, double leverage, Price price) {
		if (price == null) return null;
		StockInfo info = getStore().get(code);
		if (info == null) return null;
		if (!info.checkPricePossibleAdded(price.getTime())) return null;

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
	
	
	private Stock addStock(String code, boolean buy, double refLeverage, double volume, long takenTimePoint, Price price) {
		StockInfo info = getStore().get(code);
		if (info == null) return null;
		
		StockGroup group = get(code, buy);
		if (group == null) {
			if (price == null)
				group = newGroup(code, buy, takenTimePoint);
			else
				group = newGroup(code, buy, Double.isNaN(refLeverage) ? this.refLeverage : refLeverage, price);
			if (group == null) return null;
			
			if (!add(group)) {
				info.removePrice(price);
				return null;
			}
			
			Stock stock = group.add(getTimeViewInterval(), takenTimePoint, volume);
			if (stock == null) {
				remove(code, buy);
				info.removePrice(price);
			}
			
			return stock;
		}
		else {
			if (price != null) {
				if (!group.setPrice(price)) return null;
				takenTimePoint = group.getPriceTimePoint();
			}
			if (!Double.isNaN(refLeverage)) group.setLeverage(refLeverage);
			
			return group.add(this.getTimeViewInterval(), takenTimePoint, volume);
		}
		
	}

	
	public Stock addStock(String code, boolean buy, double refLeverage, double volume, Price price) {
		return addStock(code, buy, refLeverage, volume, 0, price);
	}
	
	
	private Stock addStock(String code, boolean buy, double volume, long takenTimePoint) {
		return addStock(code, buy, Double.NaN, volume, takenTimePoint, null);
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
		if (placedMarket != null)
			return placedMarket;
		else {
			Universe u = getNearestUniverse();
			return u != null ? u.c(u.getPlacedMarket(this.getName())) : null;
		}
	}


	@Override
	public Market getDualMarket() {
		return getPlacedMarket();
	}
	
	
	@Override
	public Market getSuperMarket() {
		return getNearestUniverse();
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
			boolean readPricesStart = false;
			boolean readPrices = false;
			boolean readStocksStart = false;
			boolean readStocks = false;
			boolean readPlacedStocksStart = false;
			boolean readPlacedStocks = false;
			boolean readPropertiesStart = false;
			boolean readProperties = false;
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
				
				if (!readPrices) {
					if (!readDefaultCodes) continue;
					if (!readPricesStart && fields[0].equals(StockProperty.NOTCODE3))
						readPricesStart = true;
					else if (!fields[0].equals(StockProperty.NOTCODE3))
						readPrices(this, fields);
					else
						readPrices = true;
					
					continue;
				}
				
				if (!readStocks) {
					if (!readPrices) continue;
					if (!readStocksStart && fields[0].equals(StockProperty.NOTCODE4))
						readStocksStart = true;
					else if (!fields[0].equals(StockProperty.NOTCODE4))
						readStocks(this, fields);
					else
						readStocks = true;
					
					continue;
				}
				
				if (!readPlacedStocks) {
					if (!readStocks) continue;
					if (!readPlacedStocksStart && fields[0].equals(StockProperty.NOTCODE5))
						readPlacedStocksStart = true;
					else if (!fields[0].equals(StockProperty.NOTCODE5))
						readStocks(getPlacedMarket(), fields);
					else
						readPlacedStocks = true;
					
					continue;
				}

				if (!readProperties) {
					if (!readPlacedStocks) continue;
					if (!readPropertiesStart && fields[0].equals(StockProperty.NOTCODE6))
						readPropertiesStart = true;
					else if (!fields[0].equals(StockProperty.NOTCODE6))
						readProperties(this, fields);
					else
						readProperties = true;
					
					continue;
				}
				
			} //End while
			
			compactGroups(market);
			compactGroups(getPlacedMarket());
			
			setTimeViewInterval(timeViewInterval);
			setTimeValidInterval(timeValidInterval);
			applyPlaced();
			
			return true;
		}
		catch (Exception e) { e.printStackTrace();}
		
		return false;
	}
	
	
	private static void compactGroups(MarketImpl market) {
		if (market == null) return;
		List<StockGroup> removedGroups = Util.newList(0);
		for (StockGroup group : market.groups) {if (group.size() == 0) removedGroups.add(group);}
		for (StockGroup group : removedGroups) market.groups.remove(group);
	}
	
	
	private static void readPrices(MarketImpl market, String[] fields) {
		String code = fields[0];
		double leverage = Double.parseDouble(fields[1]);
		double price = Double.parseDouble(fields[2]);
		double lowPrice = Double.parseDouble(fields[3]);
		double highPrice = Double.parseDouble(fields[4]);
		double openPrice = Double.parseDouble(fields[5]);
		long priceDate = Long.parseLong(fields[6]);
		double unitBias = Double.parseDouble(fields[7]);
		
		StockInfo si = market.getStore().get(code);
		if (si == null) return;
		
		Price p = market.newPrice(price, lowPrice, highPrice, priceDate);
		p.setOpen(openPrice);
		si.addPrice(p);
		si.setLeverage(leverage);
		si.setUnitBias(unitBias);
	}
	
	
	private static void readStocks(MarketImpl market, String[] fields) {
		String code = fields[0];
		boolean buy = Boolean.parseBoolean(fields[1]);
		double volume = Double.parseDouble(fields[2]);
		long takenDate = Long.parseLong(fields[3]);
		double stopLoss = Double.parseDouble(fields[4]);
		double takeProfit = Double.parseDouble(fields[5]);
		boolean committed = Boolean.parseBoolean(fields[6]);
		
		StockInfo si = market.getStore().get(code);
		if (si == null) return;
		
		Stock stock = market.addStock(code, buy, volume, takenDate);
		if (stock != null) {
			StockImpl s = market.c(stock);
			if (s != null) {
				s.setStopLoss(stopLoss);
				s.setTakeProfit(takeProfit);
			}
			
			stock.setCommitted(committed);
		}
	}
	
	
	private static void readProperties(MarketImpl market, String[] fields) {
		String code = fields[0];
		StockInfo si = market.getStore().get(code);
		if (si != null) si.getProperty().parseText(Arrays.asList(fields));
	}

	
	public boolean save(Writer out) {
		try {
			BufferedWriter writer = new BufferedWriter(out);
			
			String header = "code, buy, leverage, volume, taken_price, taken_date, price, low_price, high_price, price_date, unit_bias, stop_loss, take_profit, committed\n";
			writer.write(header);
			
			String info = StockProperty.NOTCODE1 + ", " +
				Util.format(balanceBase) + ", " +
				Util.format(balanceBias) + ", " +
				Util.format(marginFee) + ", " +
				getTimeViewInterval() + ", " +
				getTimeValidInterval() + ", " +
				getTimeStartPoint() + ", " +
				Util.format(refLeverage) + ", " + 
				getName() + "\n";
			writer.write(info);
			
			writer.write(StockProperty.NOTCODE2 + ", " + toCodesText(getDefaultStockCodes()) + "\n");
			
			writer.write(StockProperty.NOTCODE3 + "\n");
			writePrices(getStore(), writer);
			writer.write(StockProperty.NOTCODE3 + "\n");
			
			writer.write(StockProperty.NOTCODE4 + "\n");
			if (placedMarket != null) writeStocks(this, writer);
			writer.write(StockProperty.NOTCODE4 + "\n");

			MarketImpl placedMarket = getPlacedMarket();
			writer.write(StockProperty.NOTCODE5 + "\n");
			if (placedMarket != null) writeStocks(placedMarket, writer);
			writer.write(StockProperty.NOTCODE5 + "\n");

			writer.write(StockProperty.NOTCODE6 + "\n");
			writeProperties(getStore(), writer);
			writer.write(StockProperty.NOTCODE6 + "\n");

			writer.flush();
			return true;
		}
		catch (Exception e) { e.printStackTrace();}
		
		return false;
	}
	
	
	private static void writePrices(StockInfoStore store, Writer writer) throws IOException {
		if (store == null) return;
		Set<String> codes = store.codes();
		for (String code : codes) {
			StockInfo info = store.get(code);
			for (int i = 0; i < info.getPriceCount(); i++) {
				Price price = info.getPriceByIndex(i);
				StringBuffer buffer = new StringBuffer();
				
				buffer.append(code + ", ");
				buffer.append(Util.format(info.getLeverage()) + ", ");
				buffer.append(Util.format(price.get()) + ", ");
				buffer.append(Util.format(price.getLow()) + ", ");
				buffer.append(Util.format(price.getHigh()) + ", ");
				buffer.append(Util.format(price.getOpen()) + ", ");
				buffer.append(price.getTime() + ", ");
				buffer.append(Util.format(info.getUnitBias()) + "\n");
				
				writer.write(buffer.toString());
			}
		}
	}
	
	
	private static void writeStocks(MarketImpl market, Writer writer) throws IOException {
		for (StockGroup group : market.groups) {
			for (int i = 0; i < group.size(); i++) {
				Stock stock = group.get(i);
				StockImpl s = market.c(stock);
				if (s == null) continue;
				Price takenPrice = s.getTakenPrice(0);
				if (takenPrice == null) continue;
				StringBuffer buffer = new StringBuffer();
				
				buffer.append(stock.code() + ", ");
				buffer.append(stock.isBuy() + ", ");
				buffer.append(Util.format(stock.getVolume(0, true)) + ", ");
				buffer.append(takenPrice.getTime() + ", ");
				buffer.append(Util.format(stock.getStopLoss()) + ", ");
				buffer.append(Util.format(stock.getTakeProfit()) + ", ");
				buffer.append(stock.isCommitted() + "\n");
				
				writer.write(buffer.toString());
			}
		}

	}
	
	
	private static void writeProperties(StockInfoStore store, Writer writer) throws IOException {
		if (store == null) return;
		Set<String> codes = store.codes();
		for (String code : codes) {
			StockInfo info = store.get(code);
			StockProperty property = info.getProperty();
			if (property == null) continue;
			
			StringBuffer buffer = new StringBuffer();
			buffer.append(info.code() + ", ");
			buffer.append(property.toText() + "\n");
			writer.write(buffer.toString());

		}
	}
	
	
	protected static void applyPlaced(MarketImpl market, MarketImpl placedMarket, long timeValidInterval) {
		if (placedMarket == null || placedMarket == market) return;
		
		List<Stock> placedStocks = placedMarket.getStocks(timeValidInterval);
		for (Stock ps : placedStocks) {
			if (ps.isCommitted()) continue;
			
			StockImpl placedStock = placedMarket.c(ps);
			if (placedStock == null) continue;
			StockGroup thisGroup = market.get(placedStock.code(), placedStock.isBuy());
			if (thisGroup == null) continue;
			StockGroup placedGroup = placedMarket.get(placedStock.code(), placedStock.isBuy());
			if (placedGroup == null) continue;
			
			Price placedTakenPrice = placedStock.getTakenPrice(timeValidInterval);
			if (placedTakenPrice == null) continue;
			
			Price thisPrice = thisGroup.getPrice();
			boolean valid = ((thisPrice.get() <= placedTakenPrice.get() && placedStock.isBuy()) || (thisPrice.get() >= placedTakenPrice.get() && !placedStock.isBuy()));
			valid = valid && (thisPrice.getTime() > placedTakenPrice.getTime());
			if (!valid) continue;
			
			Stock added = thisGroup.add(timeValidInterval, placedTakenPrice.getTime(), placedStock.getVolume(timeValidInterval, false));
			if (added == null) continue;

			placedGroup.remove(ps);
			if (placedGroup.size() == 0) placedMarket.remove(placedGroup.getName(), placedGroup.isBuy());
		}

	}
	

	public void applyPlaced() {
		applyPlaced(this, getPlacedMarket(), getTimeValidInterval());
	}
	
	
}
