package net.jsi.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import net.jsi.Market;
import net.jsi.MarketImpl;
import net.jsi.Price;
import net.jsi.Stock;
import net.jsi.StockProperty;

public class MarketWatchTable extends MarketTable {

	
	private static final long serialVersionUID = 1L;

	
	public MarketWatchTable(Market market, boolean forStock, MarketListener listener) {
		super(market, forStock, listener);
	}


	@Override
	protected void commit(Stock stock) {
		JOptionPane.showMessageDialog(this, "Commit function is unabled for watch mode", "Turn off commit", JOptionPane.INFORMATION_MESSAGE);
		return;
	}


	private Stock place(Stock stock) {
		if (stock == null) stock = getSelectedStock();
		if (stock == null) return null;
		MarketImpl m = m();
		if (m == null) return null;
		MarketImpl placeMarket = m().getPlaceMarket();
		if (placeMarket == null) return null;
		
		Price price = (Price)stock.getPrice().clone();
		double volume = stock.getVolume(m.getTimeViewInterval(), false);
		if (price == null || volume == 0) return null;
		
		price.setTime(price.getTime() + StockProperty.TIME_UPDATE_PRICE_INTERVAL);
		placeMarket.getStore().addPriceWithoutDuplicate(stock.code(), price);
		Stock added = placeMarket.addStock(stock.code(), stock.isBuy(), stock.getLeverage(), volume, price.getTime());
		if (added != null) {
			//m.removeStock(added.code(), added.isBuy(), m.getTimeViewInterval(), stock.getPrice().getTime());
			update();
		}
		
		return added;
	}
	
	
	@Override
	protected JPopupMenu createContextMenu() {
		JPopupMenu ctxMenu = super.createContextMenu();
		if (ctxMenu == null || !getModel2().isForStock()) return ctxMenu;
		Stock stock = getSelectedStock();
		if (stock == null) return ctxMenu;

		ctxMenu.addSeparator();
		
		JMenuItem miPlace = new JMenuItem("Place");
		miPlace.addActionListener( 
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					place(stock);
				}
			});
		ctxMenu.add(miPlace);
	
		return ctxMenu;
	}

	
}



class MarketWatchPanel extends MarketPanel {


	private static final long serialVersionUID = 1L;

	
	public MarketWatchPanel(Market market) {
		super(market);
	}


	@Override
	protected MarketTable createMarketTable(Market market) {
		return new MarketWatchTable(market, true, null);
	}


}



class MarketWatchDialog extends MarketDialog {

	
	private static final long serialVersionUID = 1L;
	
	
	public MarketWatchDialog(Market market, MarketListener listener, Component parent) {
		super(market, listener, parent);
	}


	@Override
	protected MarketPanel createMarketPanel(Market market) {
		return new MarketWatchPanel(market);
	}
	
	
}
