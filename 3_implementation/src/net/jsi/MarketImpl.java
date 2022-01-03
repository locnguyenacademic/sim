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
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MarketImpl extends MarketAbstract implements QueryEstimator {

	
	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;


	private long timeStartPoint = 0;
	
	
	private double refLeverage = StockProperty.LEVERAGE;
	
	
	private double refUnitBias = StockProperty.UNIT_BIAS;
	
	
	private double balanceBase = 0;
	
	
	private double balanceBias = 0;

	
	private double marginFee = 0;
	
	
	private double credit = 0;

	
	protected List<StockGroup> groups = Util.newList(0);
	
	
	private MarketImpl watchMarket = null;
	
	
	private MarketImpl placeMarket = null;
	
	
	private MarketImpl trashMarket = null;

	
	public MarketImpl(String name, double refLeverage, double unitBias) {
		this(name, refLeverage, unitBias, true);
	}
	
	
	protected MarketImpl(String name, double refLeverage, double unitBias, boolean createAssocMarkets) {
		super(name);
		this.refLeverage = refLeverage;
		this.refUnitBias = unitBias;
		
		if (createAssocMarkets) {
			this.watchMarket = newWatchMarket();
			this.placeMarket = newPlaceMarket();
			this.trashMarket = newTrashMarket();
		}
	}

	
	protected MarketImpl newWatchMarket() {
		MarketImpl thisMarket = this;
		MarketImpl watchMarket = new MarketImpl(thisMarket.getName(), thisMarket.getLeverage(), thisMarket.getUnitBias(), false) {

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
		watchMarket.setTimeViewInterval(getTimeViewInterval());
		watchMarket.setTimeValidInterval(getTimeValidInterval());
		
		return watchMarket;
	}
	
	
	protected MarketImpl newPlaceMarket() {
		MarketImpl thisMarket = this;
		MarketImpl placeMarket = new MarketImpl(thisMarket.getName(), thisMarket.getLeverage(), thisMarket.getUnitBias(), false) {

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

			@Override
			public StockInfoStore getStore() {
				Universe u = getNearestUniverse();
				return u != null ? u.getCreatePlaceStore(getName()) : null;
			}

		};
		placeMarket.setTimeViewInterval(getTimeViewInterval());
		placeMarket.setTimeValidInterval(getTimeValidInterval());
		
		return placeMarket;
	}

	
	protected MarketImpl newTrashMarket() {
		MarketImpl thisMarket = this;
		MarketImpl trashMarket = new MarketImpl(thisMarket.getName(), thisMarket.getLeverage(), thisMarket.getUnitBias(), false) {

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
		trashMarket.setTimeViewInterval(getTimeViewInterval());
		trashMarket.setTimeValidInterval(getTimeValidInterval());
		
		return trashMarket;
	}

	
	private double sumOfNetMargins(long timeInterval) {
		double margin = 0;
		for (StockGroup group : groups) {
			List<Stock> stocks = group.getStocks(timeInterval);
			for (Stock stock : stocks) {
				double profit = stock.getProfit(timeInterval);
				margin += stock.getMargin(timeInterval) + (profit > 0 ? 0 : -profit);
			}
		}
		
		return margin;
	}
	
	
	private double getBalance0(long timeInterval) {
		double balance = getBalanceBase();
		if (timeInterval > 0) balance -= sumOfNetMargins(0) - sumOfNetMargins(timeInterval);
		
		double profit = 0;
		for (StockGroup group : groups) {
			List<Stock> stocks = group.getStocks(timeInterval);
			for (Stock stock : stocks) {
				if (stock.isCommitted()) profit += stock.getProfit(timeInterval) + stock.getMargin(timeInterval);
			}
		}
		
		return balance + profit;
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

	
	public void fixMargin(boolean fixed) {
		for (StockGroup group : groups) group.fixMargin(fixed);
	}


	protected double getCredit() {
		return credit;
	}
	
	
	protected void setCredit(double credit) {
		this.credit = credit;
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

	
	public double calcPositiveROISumOverStocks(long timeInterval) {
		double sum = 0;
		for (StockGroup group : groups) {
			for (Stock stock : group.stocks) {
				double roi = stock.getROI(timeInterval);
				if (roi > 0) sum += roi;
			}
		}
		return sum;
	}
	
	
	@Override
	public double calcBias(long timeInterval) {
		double biasSum = 0;
		for (StockGroup group : groups) {
			biasSum += group.calcBias(timeInterval);
		}
		
		return biasSum;
	}


	@Override
	public double calcOscill(long timeInterval) {
		double oscillSum = 0;
		for (StockGroup group : groups) {
			oscillSum += group.calcOscill(timeInterval);
		}
		
		return oscillSum;
	}

	
	@Override
	public double calcOscillAbs(long timeInterval) {
		double oscillSum = 0;
		for (StockGroup group : groups) {
			oscillSum += group.calcOscillAbs(timeInterval);
		}
		
		return oscillSum;
	}


	@Override
	public double calcOscillRatio(long timeInterval) {
		if (groups.size() == 0) return 0;
		double oscillRatio = 0;
		for (StockGroup group : groups) {
			oscillRatio += group.calcOscillRatio(timeInterval);
		}
		
		return oscillRatio / groups.size();
	}


	@Override
	public double calcOscillRatioAbs(long timeInterval) {
		if (groups.size() == 0) return 0;
		double oscillRatio = 0;
		for (StockGroup group : groups) {
			oscillRatio += group.calcOscillRatioAbs(timeInterval);
		}
		
		return oscillRatio / groups.size();
	}


	@Override
	public double calcMinMaxDev(long timeInterval) {
		double minmaxDevSum = 0;
		for (StockGroup group : groups) {
			minmaxDevSum += group.calcMinMaxDev(timeInterval);
		}
		
		return minmaxDevSum;
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
			return market.calcPositiveROISumOverStocks(timeInterval);
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
	
	
	protected StockGroup remove(int index) {
		return groups.remove(index);
	}
	
	
	protected StockGroup remove(String code, boolean buy) {
		int index = lookup(code, buy);
		if (index >= 0)
			return remove(index);
		else
			return null;
	}
	
	
	public boolean remove(StockGroup group) {
		return groups.remove(group);
	}
	
	
	public StockGroup set(int index, StockGroup group) {
		if (group == null || lookup(group.code(), group.isBuy()) >= 0)
			return null;
		else {
			return groups.set(index, group);
		}
	}
	
	
	private StockGroup newGroup(String code, boolean buy, long timePoint) {
		StockInfo info = getStore().getCreate(code);
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
		StockInfo info = getStore().getCreate(code);
		if (info == null) return null;
		if (!info.checkPricePossibleAdded(price.getTime())) {
			Price p = info.getPrice(0, price.getTime());
			if (p == null)
				return null;
			else
				price = p;
		}

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
	
	
	private Stock addStock(String code, boolean buy, double refLeverage, double volume, long takenTimePoint, Price price, double realTakenPrice) {
		StockInfo info = getStore().getCreate(code);
		if (info == null) return null;
		
		Stock stock = null;
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
			
			stock = group.add(getTimeViewInterval(), takenTimePoint, volume);
			if (stock == null) {
				remove(code, buy);
				info.removePrice(price);
			}
		}
		else {
			if (price != null) {
				if (!group.setPrice(price)) return null;
				takenTimePoint = group.getPriceTimePoint();
			}
			if (!Double.isNaN(refLeverage)) group.setLeverage(refLeverage);
			
			stock = group.add(this.getTimeViewInterval(), takenTimePoint, volume);
		}
		
		
		if (stock != null && stock instanceof StockImpl && !Double.isNaN(realTakenPrice)) {
			Price takenPrice = ((StockImpl)stock).getTakenPrice(0);
			if (takenPrice != null && takenPrice instanceof TakenPrice) ((TakenPrice)takenPrice).setReal(realTakenPrice);
		}
		return stock;
	}

	
	public Stock addStock(String code, boolean buy, double refLeverage, double volume, Price price, double realTakenPrice) {
		return addStock(code, buy, refLeverage, volume, 0, price, realTakenPrice);
	}
	
	
	public Stock addStock(String code, boolean buy, double refLeverage, double volume, Price price) {
		return addStock(code, buy, refLeverage, volume, 0, price, Double.NaN);
	}

	
	public Stock addStock(String code, boolean buy, double volume, Price price, double realTakenPrice) {
		return addStock(code, buy, Double.NaN, volume, 0, price, realTakenPrice);
	}

	
	public Stock addStock(String code, boolean buy, double volume, Price price) {
		return addStock(code, buy, Double.NaN, volume, 0, price, Double.NaN);
	}

	
	public Stock addStock(String code, boolean buy, double refLeverage, double volume, long takenTimePoint, double realTakenPrice) {
		return addStock(code, buy, refLeverage, volume, takenTimePoint, null, realTakenPrice);
	}

	
	public Stock addStock(String code, boolean buy, double refLeverage, double volume, long takenTimePoint) {
		return addStock(code, buy, refLeverage, volume, takenTimePoint, null, Double.NaN);
	}

	
	public Stock addStock(String code, boolean buy, double volume, long takenTimePoint, double realTakenPrice) {
		return addStock(code, buy, Double.NaN, volume, takenTimePoint, realTakenPrice);
	}

	
	public Stock addStock(String code, boolean buy, double volume, long takenTimePoint) {
		return addStock(code, buy, Double.NaN, volume, takenTimePoint, Double.NaN);
	}

	
	public Stock removeStock(String code, boolean buy, long timeInterval, long takenTimePoint) {
		int found = lookup(code, buy);
		if (found < 0) return null;
		
		StockGroup group = get(found);
		int index = group.lookup(timeInterval, takenTimePoint);
		if (index >= 0) {
			Stock removedStock = group.remove(index);
			if (removedStock != null && group.size() == 0) remove(group);
			return removedStock;
		}
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
	
	
	private static EstimateStock lookup(Map<String, List<EstimateStock>> estimators, Stock stock) {
		List<EstimateStock> esList = estimators.get(stock.code());
		if (esList == null) return null;
		for (EstimateStock es : esList) {
			if (es.stock == stock) return es;
		}
		
		return null;
	}
	
	
	protected Map<String, List<EstimateStock>> retrieveStockEstimators(long timeInterval) {
		Map<String, List<EstimateStock>> estimators = Util.newMap(0);
		for (int i = 0; i < size(); i++) {
			StockGroup group = get(i);
			if (group.isCommitted()) continue;
			Estimator estimator = getEstimator(group.code(), group.isBuy());
			List<EstimateStock> estimateStocks = estimator.estimateStocks(group.getStocks(timeInterval), timeInterval);
			estimators.put(group.code(), estimateStocks);
		}
		
		return estimators;
	}
	
	
	public void resetAllStopLossesTakeProfits(long timeInterval) {
		Map<String, List<EstimateStock>> estimators = retrieveStockEstimators(timeInterval);
		List<Stock> stocks = getStocks(timeInterval);
		for (Stock stock : stocks) {
			EstimateStock es = lookup(estimators, stock);
			if (es == null) continue;
			StockImpl s = c(stock);
			if (s != null) {
				s.setStopLoss(es.estimatedStopLoss);
				s.setTakeProfit(es.estimatedTakeProfit);
			}
		}
	}
	
	
	@Override
	public double getUnitBias() {
		return refUnitBias;
	}
	
	
	public void setUnitBias(double refUnitBias) {
		this.refUnitBias = refUnitBias;
		
		MarketImpl watchMarket = getWatchMarket();
		if (watchMarket != null && watchMarket != this) watchMarket.setUnitBias(refUnitBias);
		
		MarketImpl placeMarket = getPlaceMarket();
		if (placeMarket != null && placeMarket != this) placeMarket.setUnitBias(refUnitBias);
		
		MarketImpl trashMarket = getTrashMarket();
		if (trashMarket != null && trashMarket != this) trashMarket.setUnitBias(refUnitBias);
	}
	
	
	public void resetAllUnitBiases(long timeInterval) {
		Map<String, List<EstimateStock>> estimators = retrieveStockEstimators(timeInterval);
		List<Stock> stocks = getStocks(timeInterval);
		for (Stock stock : stocks) {
			EstimateStock es = lookup(estimators, stock); if (es == null) continue;
			StockImpl s = c(stock);
			if (s != null) s.setUnitBias(es.estimatedUnitBiasFromData);
		}
	}
	
	
	public boolean resetUnitBiasTimeFrame(String code, boolean buy, double timeFrameUnitBias, long timeInterval) {
		StockGroup group = get(code, buy);
		if (group == null) return false;
		Estimator estimator = getEstimator(code, buy);
		if (estimator == null) return false;
		
		double estUnitBias = estimator.estimateUnitBiasFromData(timeInterval);
		return group.setUnitBias(Math.max(estUnitBias, timeFrameUnitBias));
	}

	
	@Override
	public double getLeverage() {
		return refLeverage;
	}
	
	
	public void setLeverage(double refLeverage) {
		this.refLeverage = refLeverage;
		
		MarketImpl watchMarket = getWatchMarket();
		if (watchMarket != null && watchMarket != this) watchMarket.setLeverage(refLeverage);
		
		MarketImpl placeMarket = getPlaceMarket();
		if (placeMarket != null && placeMarket != this) placeMarket.setLeverage(refUnitBias);
		
		MarketImpl trashMarket = getTrashMarket();
		if (trashMarket != null && trashMarket != this) trashMarket.setLeverage(refUnitBias);
	}
	
	
	@Override
	public void setTimeViewInterval(long timeViewInterval) {
		super.setTimeViewInterval(timeViewInterval);
		
		MarketImpl watchMarket = getWatchMarket();
		if (watchMarket != null && watchMarket != this) watchMarket.setTimeViewInterval(timeViewInterval);
		
		MarketImpl placeMarket = getPlaceMarket();
		if (placeMarket != null && placeMarket != this) placeMarket.setTimeViewInterval(timeViewInterval);
		
		MarketImpl trashMarket = getTrashMarket();
		if (trashMarket != null && trashMarket != this) trashMarket.setTimeViewInterval(timeViewInterval);
	}


	@Override
	public void setTimeValidInterval(long timeValidInterval) {
		super.setTimeValidInterval(timeValidInterval);
		
		MarketImpl watchMarket = getWatchMarket();
		if (watchMarket != null && watchMarket != this) watchMarket.setTimeValidInterval(timeValidInterval);
		
		MarketImpl placeMarket = getPlaceMarket();
		if (placeMarket != null && placeMarket != this) placeMarket.setTimeValidInterval(timeValidInterval);
		
		MarketImpl trashMarket = getTrashMarket();
		if (trashMarket != null && trashMarket != this) trashMarket.setTimeValidInterval(timeValidInterval);
	}


	public long getTimeStartPoint() {
		return timeStartPoint;
	}
	
	
	public void setTimeStartPoint(long timeStartPoint) {
		this.timeStartPoint = timeStartPoint;
		
		MarketImpl watchMarket = getWatchMarket();
		if (watchMarket != null && watchMarket != this) watchMarket.setTimeStartPoint(timeStartPoint);
		
		MarketImpl placeMarket = getPlaceMarket();
		if (placeMarket != null && placeMarket != this) placeMarket.setTimeStartPoint(timeStartPoint);
		
		MarketImpl trashMarket = getTrashMarket();
		if (trashMarket != null && trashMarket != this) trashMarket.setTimeStartPoint(timeStartPoint);
	}

	
	protected void setBasicInfo(MarketImpl market) {
		this.setTimeViewInterval(market.getTimeViewInterval());
		this.setTimeValidInterval(market.getTimeValidInterval());
		this.setTimeStartPoint(market.getTimeStartPoint());
		this.setLeverage(market.getLeverage());
		this.setUnitBias(market.getUnitBias());
		this.setBalanceBase(market.getBalanceBase());
		this.setBalanceBias(market.getBalanceBias());
		this.setMarginFee(market.getMarginFee());
		this.setCredit(market.getCredit());
	}
	
	
	@Override
	public List<String> getSupportStockCodes() {
		Universe u = getNearestUniverse();
		if (u != null)
			return u.getSupportStockCodes();
		else {
			Set<String> codes = Util.newSet(0);
			for (StockGroup group : groups) codes.add(group.code());
			
			return Util.sort(codes);
		}
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
	
	
	public MarketImpl getWatchMarket() {
		return watchMarket;
	}


	@Override
	public Market getDualMarket() {
		return getWatchMarket();
	}
	
	
	@Override
	public Market getSuperMarket() {
		return getNearestUniverse();
	}


	public MarketImpl getPlaceMarket() {
		return placeMarket;
	}


	public MarketImpl getTrashMarket() {
		return trashMarket;
	}

	
	public StockInfoStore getStore() {
		Universe u = getNearestUniverse();
		return u != null ? u.getCreateStore(getName()) : null;
	}

	
	public String countText(boolean showCommitted) {
		List<Stock> stocks = getStocks(0);
		int uncommittedCount = 0;
		for (Stock stock : stocks) uncommittedCount += stock.isCommitted() ? 0 : 1;
		return (showCommitted ? stocks.size() : uncommittedCount) + "/" + stocks.size() + " (" + size() + ")";
	}
	
	
	public void sortCodes() {
		Collections.sort(groups, new Comparator<StockGroup>() {

			@Override
			public int compare(StockGroup o1, StockGroup o2) {
				return o1.code().compareToIgnoreCase(o2.code());
			}
			
		});
	}
	
	
	public boolean sync(Market otherMarket, long timeInterval, boolean removeRedundant) {
		MarketImpl other = null;
		if (otherMarket instanceof MarketImpl)
			other = (MarketImpl)otherMarket;
		else {
			Universe u = this.getNearestUniverse();
			other = u != null ? (MarketImpl)u.c(otherMarket) : null;
		}
		if (other == null) return false;
		
		sync(this, other, timeInterval, removeRedundant, true);
		
		if (this.getWatchMarket() != null && other.getWatchMarket() != null && this.getWatchMarket() != other.getWatchMarket())
			sync(this.getWatchMarket(), other.getWatchMarket(), timeInterval, removeRedundant, false);
		
		if (this.getPlaceMarket() != null && other.getPlaceMarket() != null && this.getPlaceMarket() != other.getPlaceMarket())
			sync(this.getPlaceMarket(), other.getPlaceMarket(), timeInterval, removeRedundant, true);
		
		if (this.getTrashMarket() != null && other.getTrashMarket() != null && this.getTrashMarket() != other.getTrashMarket())
			sync(this.getTrashMarket(), other.getTrashMarket(), timeInterval, removeRedundant, false);
		
		this.setBasicInfo(other);
		return true;
	}
	
	
	private static boolean sync(MarketImpl thisMarket, MarketImpl otherMarket, long timeInterval, boolean removeRedundant, boolean syncStore) {
		if (!thisMarket.getName().equals(otherMarket.getName())) return false;
		if (syncStore) thisMarket.getStore().sync(otherMarket.getStore(), timeInterval, removeRedundant);
		
		for (int i = 0; i < otherMarket.size(); i++) {
			StockGroup otherGroup = otherMarket.get(i);
			String code = otherGroup.code();
			boolean buy = otherGroup.isBuy();
			
			StockGroup thisGroup = thisMarket.get(code, buy);
			if (thisGroup == null) thisGroup = newGroup(thisMarket, code, buy);
			for (int j = 0; j < otherGroup.size(); j++) {
				StockImpl otherStock = otherMarket.c(otherGroup.get(j));
				if (otherStock == null) continue;
				
				long takenTimePoint = otherStock.getTakenTimePoint(0);
				StockImpl thisStock = thisMarket.c(thisGroup.get(takenTimePoint, otherStock));
				if (thisStock == null) {
					Stock added = thisMarket.addStock(code, buy, otherStock.getLeverage(), otherStock.getVolume(0, true), takenTimePoint, otherStock.getRealTakenPrice(0));
					thisStock = thisMarket.c(added);
				}
				else {
					thisStock.setVolume(otherStock.getVolume(0, true));
					if (thisStock.getTakenTimePoint(0) != takenTimePoint) thisStock.take(0, takenTimePoint);
				}
				if (thisStock != null) thisStock.setExtraInfo(otherStock);
			}
		}
		
		if (!removeRedundant) return true;
		
		List<StockGroup> removedGroups = Util.newList(0);
		for (int i = 0; i < thisMarket.size(); i++) {
			StockGroup thisGroup = thisMarket.get(i);
			String code = thisGroup.code();
			boolean buy = thisGroup.isBuy();

			StockGroup otherGroup = otherMarket.get(code, buy);
			if (otherGroup == null) {
				removedGroups.add(thisGroup);
				continue;
			}
			
			for (int j = 0; j < thisGroup.size(); j++) {
				StockImpl thisStock = thisMarket.c(thisGroup.get(j));
				if (thisStock == null) continue;
				
				long takenTimePoint = thisStock.getTakenTimePoint(0);
				StockImpl otherStock = otherMarket.c(otherGroup.get(takenTimePoint));
				if (otherStock == null) thisGroup.remove(thisStock);
			}
			
			if (thisGroup.size() == 0) removedGroups.add(thisGroup);
		}
		
		for (StockGroup removedGroup : removedGroups) thisMarket.remove(removedGroup);
		
		return true;
	}

	
	private static StockGroup newGroup(MarketImpl thisMarket, String code, boolean buy) {
		StockInfo info = thisMarket.getStore().getCreate(code);
		if (info == null) return null;
		
		StockGroup group = new StockGroup(code, buy) {
			private static final long serialVersionUID = 1L;
			
			@Override
			public Market getSuperMarket() {
				return thisMarket;
			}
		};
		
		return group;
	}


	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}


	public boolean apply() {
		return applyPlace(this, getTimeViewInterval());
	}
	
	
	public static boolean applyPlace(MarketImpl market, long timeValidInterval) {
		boolean ret = false;
		if (market == null) return ret;
		
		MarketImpl placeMarket = market.getPlaceMarket();
		List<Stock> placeStocks = placeMarket != null ? placeMarket.getStocks(timeValidInterval) : Util.newList(0);
		for (Stock stock : placeStocks) {
			if (stock.isCommitted()) continue;
			StockImpl placeStock = placeMarket.c(stock);
			if (placeStock == null) continue;
			StockGroup placeGroup = placeMarket.get(placeStock.code(), placeStock.isBuy());
			if (placeGroup == null) continue;
			double volume = placeStock.getVolume(timeValidInterval, false);
			if (volume <= 0) continue;

			Price placeTakenPrice = placeStock.getTakenPrice(timeValidInterval);
			if (placeTakenPrice == null) continue;

			Price lastPrice = market.getStore().getLastPrice(placeStock.code());
			boolean valid = false;
			if (lastPrice == null)
				valid = true;
			else {
				valid = ((lastPrice.get() <= placeTakenPrice.get() && placeStock.isBuy()) || (lastPrice.get() >= placeTakenPrice.get() && !placeStock.isBuy()));
				valid = valid && (lastPrice.getTime() > placeTakenPrice.getTime());
			}
			if (!valid) continue;
			
			if (lastPrice == null) {
				lastPrice = market.newPrice(placeTakenPrice.get(), placeTakenPrice.getLow(), placeTakenPrice.getHigh(), placeTakenPrice.getTime());
				boolean added = market.getStore().addPrice(placeStock.code(), lastPrice);
				if (!added) continue;
			}
			
			Stock added = market.addStock(placeStock.code(),
				placeStock.isBuy(),
				volume,
				lastPrice.getTime(),
				placeStock.getRealTakenPrice(0));
			if (added == null) continue;
			added.setExtraInfo(placeStock);

			placeGroup.remove(stock);
			if (placeGroup.size() == 0) placeMarket.remove(placeGroup.getName(), placeGroup.isBuy());
			
			ret = true;
		}
		
		return ret;
	}
	

	public static boolean watch(Stock stock, MarketImpl market, MarketImpl watchMarket) {
		if (stock == null || market == null || watchMarket == null || market == watchMarket) return false;
		StockImpl s = market.c(stock); if (s == null) return false;
		
		double volume = stock.getVolume(market.getTimeViewInterval(), true);
		if (watchMarket != null) {
			Stock added = watchMarket.addStock(stock.code(), stock.isBuy(), stock.getLeverage(), volume, s.getTakenTimePoint(market.getTimeViewInterval()), s.getRealTakenPrice(0));
			if (added == null) return false;
			added.setExtraInfo(stock);
		}

		return remove(stock, market);
	}
		

	public static boolean place(Stock stock, MarketImpl market, MarketImpl placeMarket) {
		if (stock == null || stock instanceof StockGroup || market == null || placeMarket == null || market == placeMarket) return false;
		if (stock.isCommitted()) return false;
		
		Price price = null;
		try {price = (Price)stock.getPrice().clone();} catch (Throwable e) {Util.trace(e);}
		if (price == null) return false;
		
		double volume = stock.getVolume(market.getTimeViewInterval(), false);
		if (price == null || volume <= 0) return false;
		StockImpl s = market.c(stock);
		double realTakenPrice = s != null ? s.getRealTakenPrice(0) : Double.NaN; 
				
		price.setTime(price.getTime() + StockProperty.TIME_UPDATE_PRICE_INTERVAL);
		placeMarket.getStore().addPriceWithoutDuplicateTime(stock.code(), price);
		Stock added = placeMarket.addStock(stock.code(), stock.isBuy(), stock.getLeverage(), volume, price.getTime(), realTakenPrice);
		if (added != null) {
			added.setExtraInfo(stock);
			return true;
		}
		else
			return false;
	}
	
	
	public static boolean buy(Stock stock, MarketImpl market, MarketImpl targetMarket) {
		if (stock == null || market == null || targetMarket == null || market == targetMarket) return false;
		if (stock.isCommitted()) return false;
		StockImpl s = market.c(stock); if (s == null) return false;
		double volume = stock.getVolume(market.getTimeViewInterval(), true);
		if (volume <= 0) return false;
		
		Stock added = targetMarket.addStock(stock.code(), stock.isBuy(), stock.getLeverage(), volume, s.getTakenTimePoint(market.getTimeViewInterval()), s.getRealTakenPrice(0));
		if (added == null) return false;
		added.setExtraInfo(stock);

		return MarketImpl.remove(stock, market);
	}
	
	
	public static boolean move(Stock stock, MarketImpl market, MarketImpl targetMarket) {
		if (stock == null || market == null || targetMarket == null || market == targetMarket) return false;
		StockImpl s = market.c(stock); if (s == null) return false;
		
		double volume = stock.getVolume(market.getTimeViewInterval(), true);
		Stock added = targetMarket.addStock(stock.code(), stock.isBuy(), stock.getLeverage(), volume, s.getTakenTimePoint(market.getTimeViewInterval()), s.getRealTakenPrice(0));
		if (added == null) return false;
		added.setExtraInfo(stock);

		return MarketImpl.remove(stock, market);

	}
	
	
	public static boolean remove(Stock stock, MarketImpl market) {
		if (market == null || stock == null) return false;
		
		if (stock instanceof StockGroup) {
			return market.remove(stock.code(), stock.isBuy()) != null;
		}
		else {
			StockGroup group = market.get(stock.code(), stock.isBuy());
			if (group == null) return false;
			if (!group.remove(stock)) return false;
			if (group.size() == 0)
				return market.remove(stock.code(), stock.isBuy()) != null;
			else
				return true;
		}
	}
	
	
	public static boolean recover(Stock stock, MarketImpl market, MarketImpl targetMarket) {
		if (market == null || targetMarket == null) return false;
		StockImpl s = market.c(stock); if (s == null) return false;

		double volume = stock.getVolume(market.getTimeViewInterval(), true);
		Stock added = targetMarket.addStock(stock.code(), stock.isBuy(), stock.getLeverage(), volume, s.getTakenTimePoint(market.getTimeViewInterval()), s.getRealTakenPrice(0));
		if (added == null) return false;
		added.setExtraInfo(stock);

		return MarketImpl.remove(stock, market);
		
	}
	
	
	private static double fromLeverage(String leverageText) {
		double leverage = StockProperty.LEVERAGE;
		try {
			leverage = Double.parseDouble(leverageText);
			if (leverage != 0 && leverage > 1) leverage = 1.0 / leverage;
		}
		catch (Exception e) {}
		
		return leverage;
	}
	
	
	private static String toLeverage(double leverage) {
		if (leverage != 0 && leverage < 1) leverage = 1.0 / leverage;
		return Util.format(leverage);
	}
	
	
	public static String readMarketName(File file) {
		try {
			FileReader in = new FileReader(file);
			String marketName = readMarketName(in);
			in.close();
			return marketName;
		}
		catch (Throwable e) {Util.trace(e);}
		
		return null;
	}
	
	
	public static String readMarketName(Reader in) {
		try {
			BufferedReader reader = new BufferedReader(in);
			String line = null;
			boolean readHeader = false;
			boolean readInfoStart = false;
			String marketName = null;
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
				else if (!readInfoStart && fields[0].equals(StockProperty.NOTCODE1)) {
					readInfoStart = true;
					continue;
				}
				else if (readInfoStart && !fields[0].equals(StockProperty.NOTCODE1)) {
					if (fields.length > 8) marketName = fields[8];
					break;
				}
			}
			
			return marketName.isEmpty() ? null : marketName;
		}
		catch (Throwable e) {
			Util.trace(e);
		}
		
		return null;
	}
	
	
	public boolean read(Reader in) {
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

			boolean readInfoStart = false;
			boolean readInfo = false;
			boolean readInfo1 = false;
			boolean readInfo2 = false;
			
			boolean readCodesStart = false;
			boolean readCodes = false;
			boolean readCodes1 = false;
			boolean readCodes2 = false;
			
			boolean readPricesStart = false;
			boolean readPrices = false;
			
			boolean readStocksStart = false;
			boolean readStocks = false;
			
			boolean readWatchStocksStart = false;
			boolean readWatchStocks = false;
			
			boolean readPlaceStocksStart = false;
			boolean readPlaceStocks = false;
			
			boolean readPropertiesStart = false;
			boolean readProperties = false;
			
			boolean readTrashStart = false;
			boolean readTrash = false;
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
					if (!readHeader) continue;
					if (!readInfoStart && fields[0].equals(StockProperty.NOTCODE1))
						readInfoStart = true;
					else if (!fields[0].equals(StockProperty.NOTCODE1)) {
						if (!readInfo1) {
							readInfo1 = true;
							
							this.balanceBase = Double.parseDouble(fields[0]);
							this.balanceBias = Double.parseDouble(fields[1]);
							this.marginFee = Double.parseDouble(fields[2]);
							this.credit = Double.parseDouble(fields[3]);
							timeViewInterval = Long.parseLong(fields[4]);
							timeValidInterval = Long.parseLong(fields[5]);
							this.setTimeStartPoint(Long.parseLong(fields[6]));
							this.refLeverage = fromLeverage(fields[7]);
							this.setName(fields[8]);
						}
						else if (!readInfo2) {
							readInfo2 = true;
						}
					}
					else
						readInfo = true;
					
					continue;
				}
				
				if (!readCodes) {
					if (!readInfo) continue;
					if (!readCodesStart && fields[0].equals(StockProperty.NOTCODE2))
						readCodesStart = true;
					else if (!fields[0].equals(StockProperty.NOTCODE2)) {
						Universe u = getNearestUniverse();
						if (u == null) continue;
						if (!readCodes1) {
							readCodes1 = true;
							u.addDefaultStockCodes(Arrays.asList(fields));
						}
						else if (!readCodes2) {
							readCodes2 = true;
							u.addDefaultCategories(Arrays.asList(fields));
						}
					}
					else
						readCodes = true;
					
					continue;
				}
				
				if (!readPrices) {
					if (!readCodes) continue;
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
						readRefStocks(this, fields);
					else
						readStocks = true;
					
					continue;
				}
				
				if (!readWatchStocks) {
					if (!readStocks) continue;
					if (!readWatchStocksStart && fields[0].equals(StockProperty.NOTCODE5))
						readWatchStocksStart = true;
					else if (!fields[0].equals(StockProperty.NOTCODE5))
						readRefStocks(getWatchMarket(), fields);
					else
						readWatchStocks = true;
					
					continue;
				}

				if (!readPlaceStocks) {
					if (!readWatchStocks) continue;
					if (!readPlaceStocksStart && fields[0].equals(StockProperty.NOTCODE6))
						readPlaceStocksStart = true;
					else if (!fields[0].equals(StockProperty.NOTCODE6))
						readStocks(getPlaceMarket(), fields);
					else
						readPlaceStocks = true;
					
					continue;
				}
				
				if (!readProperties) {
					if (!readWatchStocks) continue;
					if (!readPropertiesStart && fields[0].equals(StockProperty.NOTCODE7))
						readPropertiesStart = true;
					else if (!fields[0].equals(StockProperty.NOTCODE7))
						readProperties(this, fields);
					else
						readProperties = true;
					
					continue;
				}
				
				if (!readTrash) {
					if (!readProperties) continue;
					if (!readTrashStart && fields[0].equals(StockProperty.NOTCODE8))
						readTrashStart = true;
					else if (!fields[0].equals(StockProperty.NOTCODE8))
						readRefStocks(getTrashMarket(), fields);
					else
						readTrash = true;
					
					continue;
				}

			} //End while
			
			compactGroups(market);
			compactGroups(getWatchMarket());
			
			setTimeViewInterval(timeViewInterval);
			setTimeValidInterval(timeValidInterval);
			apply();
			
			return true;
		}
		catch (Exception e) {Util.trace(e);}
		
		return false;
	}
	
	
	private static void compactGroups(MarketImpl market) {
		if (market == null) return;
		List<StockGroup> removedGroups = Util.newList(0);
		for (StockGroup group : market.groups) {if (group.size() == 0) removedGroups.add(group);}
		for (StockGroup group : removedGroups) market.groups.remove(group);
	}
	
	
	private void readPrices(MarketImpl market, String[] fields) {
		String code = fields[0];
		double leverage = fromLeverage(fields[1]);
		double price = Double.parseDouble(fields[2]);
		double lowPrice = Double.parseDouble(fields[3]);
		double highPrice = Double.parseDouble(fields[4]);
		double altPrice = Double.parseDouble(fields[5]);
		long priceDate = Long.parseLong(fields[6]);
		double unitBias = Double.parseDouble(fields[7]);

		if (Double.isNaN(leverage)) {
			PricePool pricePool = StockInfoStore.getCreatePricePool(code);
			if (pricePool == null || pricePool.lookup(priceDate) >= 0) return;
			Price p = market.newPrice(price, lowPrice, highPrice, priceDate);
			pricePool.add(p);
		}
		else {
			StockInfo si = market.getStore().getCreate(code);
			if (si == null) return;
			if (si.getPrice(0, priceDate) != null) {
				si.setLeverage(leverage);
				si.setUnitBias(unitBias);
			}
			else {
				Price p = market.newPrice(price, lowPrice, highPrice, priceDate);
				p.setAlt(altPrice);
				si.addPrice(p);
				si.setLeverage(leverage);
				si.setUnitBias(unitBias);
			}
		}
	}
	
	
	private static void readRefStocks(MarketImpl market, String[] fields) {
		String code = fields[0];
		boolean buy = Boolean.parseBoolean(fields[1]);
		double volume = Double.parseDouble(fields[2]);
		long takenDate = Long.parseLong(fields[3]);
		double realTakenPrice = Double.parseDouble(fields[4]);
		double unitMargin = Double.parseDouble(fields[5]);
		double stopLoss = Double.parseDouble(fields[6]);
		double takeProfit = Double.parseDouble(fields[7]);
		boolean committed = Boolean.parseBoolean(fields[8]);
		long committed_date = Long.parseLong(fields[9]);
		
		Stock stock = market.addStock(code, buy, volume, takenDate, realTakenPrice);
		if (stock == null) return;
		
		stock.setCommitted(committed);
		stock.setCommitted(committed, committed_date);
		
		StockImpl s = market.c(stock);
		if (s != null) {
			s.setStopLoss(stopLoss);
			s.setTakeProfit(takeProfit);
			s.setFixedUnitMargin(unitMargin);
		}
	}
	
	
	private static void readStocks(MarketImpl market, String[] fields) {
		String code = fields[0];
		boolean buy = Boolean.parseBoolean(fields[1]);
		double leverage = fromLeverage(fields[2]);
		double volume = Double.parseDouble(fields[3]);
		double price = Double.parseDouble(fields[4]);
		double lowPrice = Double.parseDouble(fields[5]);
		double highPrice = Double.parseDouble(fields[6]);
		double altPrice = Double.parseDouble(fields[7]);
		long priceDate = Long.parseLong(fields[8]);
		double realTakenPrice = Double.parseDouble(fields[9]);
		double unitMargin = Double.parseDouble(fields[10]);
		double unitBias = Double.parseDouble(fields[11]);
		double stopLoss = Double.parseDouble(fields[12]);
		double takeProfit = Double.parseDouble(fields[13]);
		boolean committed = Boolean.parseBoolean(fields[14]);
		long committed_date = Long.parseLong(fields[15]);
		
		Price p = market.newPrice(price, lowPrice, highPrice, priceDate);
		p.setAlt(altPrice);

		Stock stock = market.addStock(code, buy, leverage, volume, p, realTakenPrice);
		if (stock == null) return;
		
		stock.setUnitBias(unitBias);
		stock.setCommitted(committed, committed_date);
		
		StockImpl s = market.c(stock);
		if (s != null) {
			s.setStopLoss(stopLoss);
			s.setTakeProfit(takeProfit);
			s.setFixedUnitMargin(unitMargin);
		}
	}

	
	private static void readProperties(MarketImpl market, String[] fields) {
		String code = fields[0];
		StockInfo si = market.getStore().getCreate(code);
		if (si != null) si.getProperty().parseText(Arrays.asList(fields));
	}

	
	public boolean write(Writer out) {
		try {
			BufferedWriter writer = new BufferedWriter(out);
			
			String header = "code, buy, leverage, volume, taken_price, taken_date, real_taken_price, margin, price, low_price, high_price, alt_price, price_date, unit_bias, stop_loss, take_profit, committed, committed_date\n";
			writer.write(header);
			
			writer.write(StockProperty.NOTCODE1 + "\n");
			String info = Util.format(balanceBase) + ", " +
				Util.format(balanceBias) + ", " +
				Util.format(marginFee) + ", " +
				Util.format(credit) + ", " +
				getTimeViewInterval() + ", " +
				getTimeValidInterval() + ", " +
				getTimeStartPoint() + ", " +
				toLeverage(refLeverage) + ", " + 
				getName() + "\n";
			writer.write(info);
			writer.write(StockProperty.NOTCODE1 + "\n");
			
			writer.write(StockProperty.NOTCODE2 + "\n");
			writer.write(toCodesText(getDefaultStockCodes()) + "\n");
			Universe u = getNearestUniverse();
			if (u != null)
				writer.write(toCodesText(u.getDefaultCategories()) + "\n");
			writer.write(StockProperty.NOTCODE2 + "\n");
			
			writer.write(StockProperty.NOTCODE3 + "\n");
			writePrices(this, writer);
			writer.write(StockProperty.NOTCODE3 + "\n");
			
			writer.write(StockProperty.NOTCODE4 + "\n");
			if (watchMarket != null) writeRefStocks(this, writer);
			writer.write(StockProperty.NOTCODE4 + "\n");

			MarketImpl watchMarket = getWatchMarket();
			writer.write(StockProperty.NOTCODE5 + "\n");
			if (watchMarket != null) writeRefStocks(watchMarket, writer);
			writer.write(StockProperty.NOTCODE5 + "\n");

			MarketImpl placeMarket = getPlaceMarket();
			writer.write(StockProperty.NOTCODE6 + "\n");
			if (placeMarket != null) writeStocks(placeMarket, writer);
			writer.write(StockProperty.NOTCODE6 + "\n");

			writer.write(StockProperty.NOTCODE7 + "\n");
			writeProperties(getStore(), writer);
			writer.write(StockProperty.NOTCODE7 + "\n");

			MarketImpl trashMarket = getTrashMarket();
			writer.write(StockProperty.NOTCODE8 + "\n");
			if (trashMarket != null) writeRefStocks(trashMarket, writer);
			writer.write(StockProperty.NOTCODE8 + "\n");

			writer.flush();
			return true;
		}
		catch (Throwable e) {Util.trace(e);}
		
		return false;
	}
	
	
	private static void writePrices(MarketImpl market, Writer writer) throws IOException {
		StockInfoStore store = market.getStore();
		if (store == null) return;
		
		Set<String> codes = store.codes();
		for (String code : codes) {
			StockInfo info = store.get(code);
			for (int i = 0; i < info.getPriceCount(); i++) {
				Price price = info.getPriceByIndex(i);
				StringBuffer buffer = new StringBuffer();
				
				buffer.append(code + ", ");
				buffer.append(toLeverage(info.getLeverage()) + ", ");
				buffer.append(Util.format(price.get()) + ", ");
				buffer.append(Util.format(price.getLow()) + ", ");
				buffer.append(Util.format(price.getHigh()) + ", ");
				buffer.append(Util.format(price.getAlt()) + ", ");
				buffer.append(price.getTime() + ", ");
				buffer.append(Util.format(info.getUnitBias()) + "\n");
				
				writer.write(buffer.toString());
			}
		}
		
		Universe u = market.getNearestUniverse();
		if (u != null && u.lookup(market.getName()) > 0) return;
		
		Set<String> priceCodes = Util.newSet(0);
		priceCodes.addAll(StockInfoStore.getPricePoolCodes());
		priceCodes.removeAll(codes);
		for (String priceCode : priceCodes) {
			PricePool pricePool = StockInfoStore.getPricePool(priceCode);
			for (int i = 0; i < pricePool.size(); i++) {
				Price price = pricePool.getByIndex(i);
				StringBuffer buffer = new StringBuffer();
				
				buffer.append(priceCode + ", ");
				buffer.append(Double.NaN + ", ");
				buffer.append(Util.format(price.get()) + ", ");
				buffer.append(Util.format(price.getLow()) + ", ");
				buffer.append(Util.format(price.getHigh()) + ", ");
				buffer.append(Util.format(price.getAlt()) + ", ");
				buffer.append(price.getTime() + ", ");
				buffer.append("0\n");
				
				writer.write(buffer.toString());
			}
		}
	}
	
	
	private static void writeRefStocks(MarketImpl market, Writer writer) throws IOException {
		for (StockGroup group : market.groups) {
			for (int i = 0; i < group.size(); i++) {
				Stock stock = group.get(i);
				StockImpl s = market.c(stock);
				if (s == null) continue;
				Price takenPrice = s.getTakenPrice(0);
				if (takenPrice == null) continue;
				double realPrice = Double.NaN;
				if (takenPrice != null && takenPrice instanceof TakenPrice) realPrice = ((TakenPrice)takenPrice).queryReal();
				StringBuffer buffer = new StringBuffer();
				
				buffer.append(stock.code() + ", ");
				buffer.append(stock.isBuy() + ", ");
				buffer.append(Util.format(stock.getVolume(0, true)) + ", ");
				buffer.append(takenPrice.getTime() + ", ");
				buffer.append((!Double.isNaN(realPrice) ?  Util.format(realPrice) : realPrice) + ", ");
				buffer.append((!Double.isNaN(s.getFixedUnitMargin()) ?  Util.format(s.getFixedUnitMargin()) : s.getFixedUnitMargin()) + ", ");
				buffer.append(Util.format(stock.getStopLoss()) + ", ");
				buffer.append(Util.format(stock.getTakeProfit()) + ", ");
				buffer.append(stock.isCommitted() + ", ");
				buffer.append(stock.getCommittedTimePoint() + "\n");
				
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
				StringBuffer buffer = new StringBuffer();
				
				buffer.append(stock.code() + ", ");
				buffer.append(stock.isBuy() + ", ");
				buffer.append(toLeverage(stock.getLeverage()) + ", ");
				buffer.append(Util.format(stock.getVolume(0, true)) + ", ");
				
				Price price = stock.getPrice();
				buffer.append(Util.format(price.get()) + ", ");
				buffer.append(Util.format(price.getLow()) + ", ");
				buffer.append(Util.format(price.getHigh()) + ", ");
				buffer.append(Util.format(price.getAlt()) + ", ");
				buffer.append(s.getTakenTimePoint(0) + ", ");
				buffer.append(Double.NaN + ", ");
				buffer.append((!Double.isNaN(s.getFixedUnitMargin()) ?  Util.format(s.getFixedUnitMargin()) : s.getFixedUnitMargin()) + ", ");
				buffer.append(Util.format(stock.getUnitBias()) + ", ");
				buffer.append(Util.format(stock.getStopLoss()) + ", ");
				buffer.append(Util.format(stock.getTakeProfit()) + ", ");
				buffer.append(stock.isCommitted() + ", ");
				buffer.append(stock.getCommittedTimePoint() + "\n");
				
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
			if (info.getProperty() == null || info.getPriceCount() == 0) continue;
			
			StringBuffer buffer = new StringBuffer();
			buffer.append(info.code() + ", ");
			buffer.append(property.toText() + "\n");
			writer.write(buffer.toString());

		}
	}
	
	
}
