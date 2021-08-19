package net.jsi.ui;

import javax.swing.JPanel;

import net.jsi.Market;

public class MarketPanel extends JPanel {


	private static final long serialVersionUID = 1L;

	
	protected Market market = null;
	
	
	public MarketPanel(Market market) {
		this.market = market;
	}
	
	
	public Market getMarket() {
		return market;
	}
	
}
