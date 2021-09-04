package net.jsi.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Date;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import net.jsi.Market;
import net.jsi.Price;
import net.jsi.Stock;
import net.jsi.StockGroup;
import net.jsi.StockImpl;
import net.jsi.StockProperty;
import net.jsi.Util;

public class MarketPlaceTable extends MarketTable {

	
	private static final long serialVersionUID = 1L;

	
	public MarketPlaceTable(Market market, boolean forStock, MarketListener listener) {
		super(market, forStock, listener);
	}


	@Override
	protected MarketTableModel createModel(Market market, boolean forStock) {
		return new MarketTableModel(market, forStock) {

			private static final long serialVersionUID = 1L;

			@Override
			protected Vector<Object> toRow(Stock stock) {
				long timeViewInterval = market.getTimeViewInterval();
				Vector<Object> row = Util.newVector(0);
				StockImpl s = market.c(stock);

				row.add(stock);
				row.add(stock.isBuy());
				row.add(stock.getLeverage());
				row.add(stock.getVolume(timeViewInterval, stock instanceof StockGroup));
				
				if (stock instanceof StockGroup)
					row.add("");
				else if (s != null)
					row.add(Util.format(new Date(s.getTakenTimePoint(timeViewInterval))));
				else
					row.add("");
				
				Price price = stock.getPrice();
				row.add(price.get());
				row.add(Util.format(price.getLow()) + " / " + Util.format(price.getHigh()));
				
				if (stock instanceof StockGroup)
					row.add("");
				else if (s != null)
					row.add(Util.format(s.getStopLoss()) + " / " + Util.format(s.getTakeProfit()));
				else
					row.add("");
				
				row.add(stock.getUnitBias());
				row.add(s.isCommitted());

				return row;
			}

			@Override
			protected Vector<String> toColumns() {
				Vector<String> columns = Util.newVector(0);

				columns.add("Code");
				columns.add("Buy");
				columns.add("Leverage");
				columns.add("Volume");
				columns.add("Date");
				columns.add("Price");
				columns.add("Low/high prices");
				columns.add("Stop loss / take profit");
				columns.add("Unit bias");
				columns.add("Committed");

				return columns;
			}
			
		};
	}


	@Override
	protected void take(Stock stock, boolean update) {
		super.take(stock, update);
	}


	@Override
	protected void summary(Stock stock) {
		super.summary(stock);
	}


	@Override
	protected void delete() {
		super.delete();
	}


	@Override
	protected JPopupMenu createContextMenu() {
		return super.createContextMenu();
	}

	
}



class MarketPlacePanel extends MarketPanel {


	private static final long serialVersionUID = 1L;

	
	public MarketPlacePanel(Market market) {
		super(market);
	}


	@Override
	protected JPopupMenu createContextMenu() {
		return super.createContextMenu();
	}


	@Override
	protected void onDoubleClick() {
		super.onDoubleClick();
	}


	@Override
	protected MarketTable createMarketTable(Market market) {
		return  new MarketPlaceTable(market, true, null);
	}


	@Override
	protected void take(Stock input, boolean update) {
		super.take(input, update);
	}

	
	public static class MarketPlaceDialog extends JDialog {

		private static final long serialVersionUID = 1L;
		
		public MarketPlaceDialog(Market market, MarketListener listener, Component parent) {
			super(Util.getDialogForComponent(parent), "Market " + market.getName(), true);
			
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					super.windowClosing(e);
				}
			});
			
			addMouseListener(new MouseAdapter() { });
			
			setSize(600, 400);
			setLocationRelativeTo(null);
			//setJMenuBar(createMenuBar());
			
			setLayout(new BorderLayout());

			//JToolBar toolbar = createToolbar();
			//if (toolbar != null) add(toolbar, BorderLayout.NORTH);

			JPanel body = new JPanel(new BorderLayout());
			add(body, BorderLayout.CENTER);
			MarketPlacePanel mp = new MarketPlacePanel(market);
			if (mp.getMarketTable() != null && listener != null && StockProperty.RUNTIME_CASCADE)
				mp.getMarketTable().getModel2().addMarketListener(listener);
			body.add(mp, BorderLayout.CENTER);
			
			JPanel footer = new JPanel();
			add(footer, BorderLayout.SOUTH);
			
			JButton ok = new JButton("OK");
			ok.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});
			footer.add(ok);
		}
		
	}


}



