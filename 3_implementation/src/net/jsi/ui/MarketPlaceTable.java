package net.jsi.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import net.jsi.Market;
import net.jsi.Stock;
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
				return super.toRow(stock);
			}

			@Override
			protected Vector<String> toColumns() {
				return super.toColumns();
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
		return super.createMarketTable(market);
	}


	@Override
	protected void take(Stock input, boolean update) {
		//super.take(input, update);
		JOptionPane.showMessageDialog(this, "This function not implemented yet", "Not implemented yet", JOptionPane.WARNING_MESSAGE);
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



