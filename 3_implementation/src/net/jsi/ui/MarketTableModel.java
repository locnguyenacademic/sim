package net.jsi.ui;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import net.jsi.Market;

public class MarketTableModel extends DefaultTableModel implements TableModelListener {

	
	private static final long serialVersionUID = 1L;

	
	protected Market market = null;
	
	
	public MarketTableModel(Market market) {
		this.market = market;
	}
	
	
	public Market getMarket() {
		return market;
	}
	
	
	@Override
	public void tableChanged(TableModelEvent e) {

	}
	

}
