/**
 * JSI: JAGGED STRATEGY INVESTMENT 
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: jsi.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.jsi;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class UniverseAbstract extends MarketAbstract implements Universe {

	
	private static final long serialVersionUID = 1L;

	
	protected List<Market> markets = Util.newList(0);
	
	
	protected Set<String> defaultStockCodes = Util.newSet(0);
	
	
	protected Set<String> defaultCategories = Util.newSet(0);

	
	protected Map<String, StockInfoStore> stores = Util.newMap(0);
	
	
	protected Map<String, StockInfoStore> placeStores = Util.newMap(0);

	
	public UniverseAbstract() {
		addDefaultStockCodes(Util.newSet(0));
		addDefaultCategories(Util.newSet(0));
	}
	
	
	@Override
	public double getBalance(long timeInterval) {
		double balance = 0;
		for (Market market : markets) {
			balance += market.getBalance(timeInterval);
		}
		return balance;
	}
	
	
	@Override
	public double getMargin(long timeInterval) {
		double margin = 0;
		for (Market market : markets) {
			margin += market.getMargin(timeInterval);
		}
		return margin;
	}
	
	
	@Override
	public double getProfit(long timeInterval) {
		double profit = 0;
		for (Market market : markets) {
			profit += market.getProfit(timeInterval);
		}
		return profit;
	}


	@Override
	public double getTakenValue(long timeInterval) {
		double profit = 0;
		for (Market market : markets) {
			profit += market.getTakenValue(timeInterval);
		}
		return profit;
	}

	
	@Override
	public double calcTotalBias(long timeInterval) {
		double biasSum = 0;
		for (Market market : markets) {
			biasSum += market.calcTotalBias(timeInterval);
		}
		
		return biasSum;
	}


	@Override
	public double calcTotalPriceOscill(long timeInterval) {
		double oscillSum = 0;
		for (Market market : markets) {
			oscillSum += market.calcTotalPriceOscill(timeInterval);
		}
		
		return oscillSum;
	}


	@Override
	public double getPriceOscillRatio(long timeInterval) {
		if (markets.size() == 0) return 0;
		double oscillRatio = 0;
		for (Market market : markets) {
			oscillRatio += market.getPriceOscillRatio(timeInterval);
		}
		
		return oscillRatio / markets.size();
	}


	@Override
	public int size() {
		return markets.size();
	}
	
	
	@Override
	public List<String> names() {
		List<String> nameList = Util.newList(size());
		for (Market market : markets) {
			nameList.add(market.getName());
		}
		return nameList;
	}


	@Override
	public Market get(int index) {
		return markets.get(index);
	}

	
	@Override
	public Market get(String name) {
		int index = lookup(name);
		if (index >= 0)
			return get(index);
		else
			return null;
	}
	
	
	@Override
	public QueryEstimator query(String name, Market refMarket) {
		Market market = get(name);
		Market watchMarket = getWatchMarket(name);
		Market placeMarket = getPlaceMarket(name);
		
		QueryEstimator refEstimator = refMarket != null && refMarket instanceof QueryEstimator ? (QueryEstimator)refMarket : null;
		QueryEstimator estimator = market != null && market instanceof QueryEstimator ? (QueryEstimator)market : null;
		QueryEstimator watchEstimator = watchMarket != null && watchMarket instanceof QueryEstimator ? (QueryEstimator)watchMarket : null;
		QueryEstimator placeEstimator = placeMarket != null && placeMarket instanceof QueryEstimator ? (QueryEstimator)placeMarket : null;

		if (refEstimator != null) {
			if (refMarket.getName().equals(name))
				return refEstimator;
			else
				return null;
		}
		else if (estimator != null)
			return estimator;
		else
			return watchEstimator != null ? watchEstimator : placeEstimator;
	}


	@Override
	public int lookup(String name) {
		for (int i = 0; i < markets.size(); i++) {
			if (markets.get(i).getName().equals(name)) return i;
		}
		return -1;
	}


	@Override
	public boolean add(Market market) {
		if (market == null || lookup(market.getName()) >= 0)
			return false;
		else
			return markets.add(market);
	}
	
	
	@Override
	public Market remove(int index) {
		return markets.remove(index);
	}
	
	
	@Override
	public Market set(int index, Market market) {
		if (market == null || lookup(market.getName()) >= 0)
			return null;
		else
			return markets.set(index, market);
	}


	@Override
	public Market newMarket(String name, double leverage, double unitBias) {
		Market superMarket = this;
		MarketImpl market = new MarketImpl(name, leverage, unitBias) {

			private static final long serialVersionUID = 1L;

			@Override
			public Market getSuperMarket() {
				return superMarket;
			}
			
		};
		market.setTimeViewInterval(this.getTimeViewInterval());
		market.setTimeValidInterval(this.getTimeValidInterval());
		return market;
	}

	
	@Override
	public MarketImpl c(Market market) {
		if (market == null)
			return null;
		if (market instanceof MarketImpl)
			return (MarketImpl)market;
		else
			return null;
	}


	@Override
	public List<Stock> getStocks(long timeInterval) {
		List<Stock> stocks = Util.newList(0);
		for (Market market : markets) {
			stocks.addAll(market.getStocks(timeInterval));
		}

		return stocks;
	}


	@Override
	public List<String> getSupportStockCodes() {
		Set<String> codes = Util.newSet(0);
		codes.addAll(StockInfoStore.getPricePoolCodes());
		codes.addAll(defaultStockCodes);
		return Util.sort(codes);
	}


	@Override
	public List<String> getDefaultStockCodes() {
		return Util.sort(defaultStockCodes);
	}


	@Override
	public void addDefaultStockCodes(Collection<String> defaultStockCodes) {
		this.defaultStockCodes.clear();
		
		this.defaultStockCodes.add("AAPL");
		this.defaultStockCodes.add("ABBV");
		this.defaultStockCodes.add("BTCUSD");
		this.defaultStockCodes.add("ETHUSD");
		this.defaultStockCodes.add("KO");
		this.defaultStockCodes.add("MSFT");
		this.defaultStockCodes.add("MRK");
		this.defaultStockCodes.add("INTC");
		this.defaultStockCodes.add("PFE");
		this.defaultStockCodes.add("XAUUSD");
		
		for (String code : defaultStockCodes) {
			if (code != null && !code.isEmpty() && !code.equals(StockProperty.NOTCODE1) && !code.equals(StockProperty.NOTCODE1))
				this.defaultStockCodes.add(code);
		}
	}


	@Override
	public List<String> getDefaultCategories() {
		return Util.sort(defaultCategories);
	}


	@Override
	public void addDefaultCategories(Collection<String> defaultCategories) {
		this.defaultCategories.clear();
		
		this.defaultCategories.add("crypto");
		this.defaultCategories.add("energy");
		this.defaultCategories.add("forex");
		this.defaultCategories.add("index");
		this.defaultCategories.add("mbc");
		this.defaultCategories.add("stock");
		this.defaultCategories.add(StockProperty.CATEGORY_UNDEFINED);
		
		for (String category : defaultCategories) {
			if (category != null && !category.isEmpty()) this.defaultCategories.add(category);
		}
	}


	@Override
	protected void reset() {
		super.reset();
		this.markets.clear();
		this.defaultStockCodes.clear();
	}
	
	
	@Override
	public double getLeverage() {
		return StockProperty.LEVERAGE;
	}


	@Override
	public double getUnitBias() {
		return StockProperty.UNIT_BIAS;
	}


	@Override
	public Market getWatchMarket(String name) {
		Market market = get(name);
		if (market != null && market instanceof MarketImpl)
			return ((MarketImpl)market).getWatchMarket();
		else
			return null;
	}


	@Override
	public Market getPlaceMarket(String name) {
		Market market = get(name);
		if (market != null && market instanceof MarketImpl)
			return ((MarketImpl)market).getPlaceMarket();
		else
			return null;
	}


	@Override
	public StockInfoStore getCreateStore(String name) {
		if (stores.containsKey(name))
			return stores.get(name);
		else {
			StockInfoStore store = new StockInfoStore();
			stores.put(name, store);
			return store;
		}
	}
	
	
	@Override
	public StockInfoStore getCreatePlaceStore(String name) {
		if (placeStores.containsKey(name)) return placeStores.get(name);

		StockInfoStore placeStore = new StockInfoStore() {
			private static final long serialVersionUID = 1L;

			@Override
			protected StockInfo create(String code) {
				if (code == null || code.isEmpty()) return null;
				StockInfo si = new StockInfo(code) {
					
					private static final long serialVersionUID = 1L;

					@Override
					protected PricePool referPricePool(String code) {
						return StockInfoStore.getCreatePlacePricePool(code);
					}
					
					
				};
				return set(code, si);
			}
			
		};
		placeStores.put(name, placeStore);
		return placeStore;
	}


	@Override
	public PricePool getPricePool(String code) {
		return StockInfoStore.getPricePool(code);
	}


	@Override
	public PricePool getCreatePricePool(String code) {
		return StockInfoStore.getCreatePricePool(code);
	}


	@Override
	public PricePool removePricePool(String code) {
		return StockInfoStore.removePricePool(code);
	}


	@Override
	public Price newPrice(double price, double lowPrice, double highPrice, long time) {
		return newPrice0(price, lowPrice, highPrice, time);
	}
	
	
	protected static Price newPrice0(double price, double lowPrice, double highPrice, long time) {
		return new PriceImpl(price, lowPrice, highPrice, time);
	}


	@Override
	public long getTimeViewInterval() {
		long maxInterval = 0;
		for (Market market : markets) {
			maxInterval = Math.max(maxInterval,  market.getTimeViewInterval());
		}
		this.timeViewInterval = maxInterval;
		return maxInterval;
	}


	@Override
	public void setTimeViewInterval(long timeViewInterval) {
		this.timeViewInterval = timeViewInterval;
		for (Market market : markets) {
			MarketImpl m = c(market);
			if (m != null) m.setTimeViewInterval(timeViewInterval);
		}
	}


	@Override
	public long getTimeValidInterval() {
		long maxInterval = 0;
		for (Market market : markets) {
			maxInterval = Math.max(maxInterval,  market.getTimeValidInterval());
		}
		this.timeValidInterval = maxInterval;
		return maxInterval;
	}
	
	
	@Override
	public void setTimeValidInterval(long timeValidInterval) {
		this.timeValidInterval = timeValidInterval;
		for (Market market : markets) {
			MarketImpl m = c(market);
			if (m != null) m.setTimeValidInterval(timeValidInterval);
		}
	}


	public void setBasicInfo(Universe other, boolean removeRedundant) {
		setTimeViewInterval(other.getTimeViewInterval());
		setTimeValidInterval(other.getTimeValidInterval());
		
		if (removeRedundant) defaultStockCodes.clear();
		addDefaultStockCodes(other.getDefaultStockCodes());
		
		if (removeRedundant) defaultCategories.clear();
		addDefaultCategories(other.getDefaultCategories());
	}
	
	
	public void setBasicInfo(UniverseRemote other, boolean removeRedundant) {
		try {
			setTimeViewInterval(other.getTimeViewInterval());
			setTimeValidInterval(other.getTimeValidInterval());
			
			if (removeRedundant) defaultStockCodes.clear();
			addDefaultStockCodes(other.getDefaultStockCodes());
			
			if (removeRedundant) defaultCategories.clear();
			addDefaultCategories(other.getDefaultCategories());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}


	private Market newMarket(File file) {
		if (file == null || !file.exists() || file.isDirectory()) return null;
		String fileName = file.getName();
		if (fileName == null || fileName.isEmpty()) return null;
		
		String marketName = null;
		int index = fileName.lastIndexOf(".");
		if (index < 0)
			marketName = fileName;
		else
			marketName = fileName.substring(0, index);
		if (marketName == null | marketName.isEmpty()) return null;
		
		MarketImpl market = (MarketImpl)newMarket(marketName, getLeverage(), getUnitBias());
        try {
        	FileReader reader = new FileReader(file);
        	market.read(reader);
        	reader.close();
        	return market;
        }
        catch (Exception e) {
        	e.printStackTrace();
        }
        
        return null;
	}

	
	@Override
	public void open(File workingDir) {
		if (workingDir.exists() && workingDir.isFile()) return;
		try {
			if (!workingDir.exists()) {
				File parent = workingDir.getParentFile();
				if (parent != null && !parent.exists()) parent.mkdir();
				workingDir.mkdir();
			}
		}
		catch (Exception e) { }
		if (!workingDir.exists()) return;
		
		String[] fileNames = workingDir.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if (name == null || name.isEmpty()) return false;
				int index = name.lastIndexOf(".");
				if (index < 0) return false;
				String ext = name.substring(index + 1);
				return ext != null && !ext.isEmpty() && ext.compareToIgnoreCase(StockProperty.JSI_EXT) == 0;
			}
		});
		
		for (String fileName : fileNames) {
			File file = new File(workingDir, fileName);
			Market market = newMarket(file);
			if (market != null) add(market);
		}
	}


	@Override
	public void save(File workingDir) {
		if (workingDir.exists() && workingDir.isFile()) return;
		try {
			if (!workingDir.exists()) {
				File parent = workingDir.getParentFile();
				if (parent != null && !parent.exists()) parent.mkdir();
				workingDir.mkdir();
			}
		}
		catch (Exception e) { }
		if (!workingDir.exists()) return;
		
		for (int i = 0; i < size(); i++) {
			MarketImpl market = c(get(i));
			if (market == null) continue;
			
			try {
				File file = new File(workingDir, market.getName() + "." + StockProperty.JSI_EXT);
				FileWriter writer = new FileWriter(file);
				market.write(writer);
				writer.close();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}


	@Override
	public boolean sync(UniverseRemote remoteUniverse, long timeInterval, boolean removeRedundant) {
		try {
			if (!this.getName().equals(remoteUniverse.getName())) return false;
			this.setBasicInfo(remoteUniverse, removeRedundant);
			
			List<String> marketNames = remoteUniverse.getMarketNames();
			for (String marketName : marketNames) {
				Market remoteMarket = remoteUniverse.getMarket(marketName);
				if (remoteMarket == null || !(remoteMarket instanceof MarketImpl)) continue;
				
				Market market = newMarket(remoteMarket.getName(), remoteMarket.getLeverage(), remoteMarket.getUnitBias());
				if (market instanceof MarketImpl) ((MarketImpl)market).sync(remoteMarket, timeInterval, removeRedundant);
				this.add(market);
			}
			
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	

}
