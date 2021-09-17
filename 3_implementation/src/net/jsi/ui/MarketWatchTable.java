package net.jsi.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import net.jsi.Market;
import net.jsi.MarketImpl;
import net.jsi.Stock;
import net.jsi.Universe;

public class MarketWatchTable extends MarketTable {

	
	private static final long serialVersionUID = 1L;

	
	public MarketWatchTable(Market market, boolean group, MarketListener listener) {
		super(market, group, listener);
	}


	private void buy() {
		int count = doTasksOnSelected(new Task() {
			@Override
			public boolean doOne(Stock stock) {
				return buy0(stock);
			}
		}, true);
		
		JOptionPane.showMessageDialog(this, "Buy/sell " + count + " stock (s)", "Already buy/sell", JOptionPane.INFORMATION_MESSAGE);
	}


	private boolean buy0(Stock stock) {
		MarketImpl m = m();
		Universe u = m != null ? m.getNearestUniverse() : null;
		return MarketImpl.buy(stock, m, u != null ? u.c(m.getDualMarket()) : null);
	}

	
	private void place() {
		int count = doTasksOnSelected(new Task() {
			@Override
			public boolean doOne(Stock stock) {
				return place0(stock);
			}
		}, true);
		
		JOptionPane.showMessageDialog(this, "Placing " + count + " stock (s)", "Already place", JOptionPane.INFORMATION_MESSAGE);
	}

	
	private boolean place0(Stock stock) {
		return MarketImpl.place(stock, m(), getPlaceMarket());
	}
	
	
	@Override
	protected MarketImpl getPlaceMarket() {
		MarketImpl placeMarket = super.getPlaceMarket();
		if (placeMarket != null) return placeMarket;
		
		MarketImpl m = m();
		Universe u = m != null ? m.getNearestUniverse() : null;
		MarketImpl dualMarket = u != null ? u.c(m.getDualMarket()) : null;
		return dualMarket != null ? dualMarket.getPlaceMarket() : null;
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

	
	public MarketWatchPanel(Market market, boolean group, MarketListener superListener) {
		super(market, group, superListener);
		btnReestimateLossesProfits.setVisible(true);
		btnReestimateUnitBiases.setVisible(true);
	}


	@Override
	protected MarketTable createMarketTable(Market market, boolean group, MarketListener superListener) {
		return new MarketWatchTable(market, group, superListener);
	}


}



class MarketWatchDialog extends MarketDialog {

	
	private static final long serialVersionUID = 1L;
	
	
	public MarketWatchDialog(Market market, boolean group, MarketListener superListener, Component parent) {
		super(market, group, superListener, parent);
		btnCancel.setText("Close");
	}


	@Override
	protected MarketPanel createMarketPanel(Market market, boolean group, MarketListener superListener) {
		return new MarketWatchPanel(market, group, superListener);
	}


}
