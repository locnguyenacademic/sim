package net.jsi.ui;

import javax.swing.JTable;

import net.jsi.Market;

public class MarketTable extends JTable {

	
	private static final long serialVersionUID = 1L;

	
	public MarketTable(Market market) {
		super();
		setModel(new MarketTableModel(market));
	}

	
	public MarketTableModel getModel2() {
		return (MarketTableModel) getModel();
	}
	
	
	public Market getMarket() {
		return getModel2().getMarket();
	}
	
	
}
