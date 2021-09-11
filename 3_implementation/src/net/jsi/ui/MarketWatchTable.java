package net.jsi.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import net.jsi.Market;
import net.jsi.MarketImpl;
import net.jsi.Price;
import net.jsi.Stock;
import net.jsi.StockGroup;
import net.jsi.StockImpl;
import net.jsi.StockProperty;
import net.jsi.Universe;
import net.jsi.Util;

public class MarketWatchTable extends MarketTable {

	
	private static final long serialVersionUID = 1L;

	
	public MarketWatchTable(Market market, boolean forStock, MarketListener listener) {
		super(market, forStock, listener);
	}


	private boolean buy0(Stock stock) {
		if (stock == null) return false;
		MarketImpl m = m(); if (m == null) return false;
		Universe u = m.getNearestUniverse();
		if (u == null) return false;
		StockImpl s = m.c(stock); if (s == null) return false;
		
		MarketImpl market = u.c(m.getDualMarket());
		double volume = stock.getVolume(m.getTimeViewInterval(), true);
		if (market != null) {
			Stock added = market.addStock(stock.code(), stock.isBuy(), stock.getLeverage(), volume, s.getTakenTimePoint(m.getTimeViewInterval()));
			if (added == null)
				return false;
			else {
				added.setCommitted(stock.isCommitted());
				try {
					market.c(added).setStopLoss(s.getStopLoss());
					market.c(added).setTakeProfit(s.getTakeProfit());
				} catch (Exception e) {}
			}
		}

		StockGroup group = m.get(stock.code(), stock.isBuy());
		if (group == null) return false;
		group.remove(stock);
		if (group.size() == 0) m.remove(stock.code(), stock.isBuy());
		
		return true;
	}

	
	private void buy() {
		List<Stock> stocks = getSelectedStocks();
		boolean ret = false;
		for (Stock stock : stocks) {
			if (stock == null)
				continue;
			else if (stock instanceof StockGroup) {
				StockGroup group = (StockGroup)stock;
				List<Stock> rmStocks = Util.newList(group.size());
				for (int i = 0; i < group.size(); i++) rmStocks.add(group.get(i));
				for (Stock rmStock : rmStocks) {
					boolean ret0 = buy0(rmStock);
					ret = ret || ret0;
				}
			}
			else {
				boolean ret0 = buy0(stock);
				ret = ret || ret0;
			}
		}
		
		if (ret) update();
	}

	
	private boolean place0(Stock stock) {
		if (stock == null) stock = getSelectedStock();
		if (stock == null) return false;
		MarketImpl m = m();
		if (m == null) return false;
		MarketImpl placeMarket = m.getPlaceMarket();
		if (placeMarket == null) return false;
		
		Price price = (Price)stock.getPrice().clone();
		double volume = stock.getVolume(m.getTimeViewInterval(), false);
		if (price == null || volume == 0) return false;
		
		price.setTime(price.getTime() + StockProperty.TIME_UPDATE_PRICE_INTERVAL);
		placeMarket.getStore().addPriceWithoutDuplicateTime(stock.code(), price);
		Stock added = placeMarket.addStock(stock.code(), stock.isBuy(), stock.getLeverage(), volume, price.getTime());
		if (added != null) {
			added.setCommitted(stock.isCommitted());
			try {
				placeMarket.c(added).setStopLoss(stock.getStopLoss());
				placeMarket.c(added).setTakeProfit(stock.getTakeProfit());
			} catch (Exception e) {}
			
			return true;
		}
		else
			return false;
	}
	
	
	private void place() {
		List<Stock> stocks = getSelectedStocks();
		boolean ret = false;
		for (Stock stock : stocks) {
			if (stock == null)
				continue;
			else if (stock instanceof StockGroup) {
				StockGroup group = (StockGroup)stock;
				List<Stock> rmStocks = Util.newList(group.size());
				for (int i = 0; i < group.size(); i++) rmStocks.add(group.get(i));
				for (Stock rmStock : rmStocks) {
					boolean ret0 = place0(rmStock);
					ret = ret || ret0;
				}
			}
			else {
				boolean ret0 = place0(stock);
				ret = ret || ret0;
			}
		}
		
		if (ret) update();
	}

	
	@Override
	protected JPopupMenu createContextMenu() {
		JPopupMenu ctxMenu = super.createContextMenu();
		if (ctxMenu == null) return ctxMenu;
		Stock stock = getSelectedStock();
		if (stock == null) return ctxMenu;

		ctxMenu.addSeparator();
		
		JMenuItem miBuy = new JMenuItem("Buy/sell");
		miBuy.addActionListener( 
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					buy();
				}
			});
		ctxMenu.add(miBuy);
		
		JMenuItem miPlace = new JMenuItem("Place");
		miPlace.addActionListener( 
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					place();
				}
			});
		ctxMenu.add(miPlace);
	
		return ctxMenu;
	}

	
}



class MarketWatchPanel extends MarketPanel {


	private static final long serialVersionUID = 1L;

	
	public MarketWatchPanel(Market market, boolean forStock, MarketListener listener) {
		super(market, forStock, listener);
		btnResetLossProfits.setVisible(true);
		btnResetBiases.setVisible(true);
	}


	@Override
	protected MarketTable createMarketTable(Market market, boolean forStock, MarketListener listener) {
		return new MarketWatchTable(market, forStock, listener);
	}


}



class MarketWatchDialog extends MarketDialog {

	
	private static final long serialVersionUID = 1L;
	
	
	public MarketWatchDialog(Market market, boolean forStock, MarketListener listener, Component parent) {
		super(market, forStock, listener, parent);
		btnOK.setText("Close");
		btnCancel.setVisible(false);
	}


	@Override
	protected MarketPanel createMarketPanel(Market market, boolean forStock, MarketListener listener) {
		MarketPanel mp = new MarketWatchPanel(market, forStock, listener);
		if (mp.getMarketTable() != null && listener != null && StockProperty.RUNTIME_CASCADE)
			mp.getMarketTable().getModel2().addMarketListener(listener);
		return mp;
	}


}
