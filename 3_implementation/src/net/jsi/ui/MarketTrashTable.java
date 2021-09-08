package net.jsi.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import net.jsi.EstimateStock;
import net.jsi.Market;
import net.jsi.MarketImpl;
import net.jsi.Stock;
import net.jsi.StockGroup;
import net.jsi.StockImpl;
import net.jsi.StockProperty;
import net.jsi.Util;

public class MarketTrashTable extends MarketTable {

	
	private static final long serialVersionUID = 1L;

	
	public MarketTrashTable(Market market, boolean forStock, MarketListener listener) {
		super(market, forStock, listener);
	}


	@Override
	protected void view(Stock stock) {
		stock = stock != null ? stock : getSelectedStock();
		if (stock == null && getModel2().isForStock()) return;
		
		new StockSummary(getMarket(), stock.code(), stock.isBuy(), getModel2().isForStock() ? stock : null, this) {
			private static final long serialVersionUID = 1L;

			@Override
			protected List<EstimateStock> getEstimateStocks() {
				return Util.newList(0);
			}
		
		}.setVisible(true);
	}

	
	private boolean recover0(Stock stock) {
		if (stock == null) return false;
		MarketImpl m = m(); if (m == null) return false;
		StockImpl s = m.c(stock); if (s == null) return false;
		MarketImpl dualMarket = m.getDualMarket() instanceof MarketImpl ? (MarketImpl)m.getDualMarket() : null;
		if (dualMarket == null) return false;
		
		double volume = stock.getVolume(m.getTimeViewInterval(), true);
		Stock added = dualMarket.addStock(stock.code(), stock.isBuy(), stock.getLeverage(), volume, s.getTakenTimePoint(m.getTimeViewInterval()));
		if (added == null) return false;

		Stock removedStock = m.removeStock(stock.code(), stock.isBuy(), m.getTimeViewInterval(), s.getTakenTimePoint(m.getTimeViewInterval()));
		if (removedStock == null) return false;
		StockGroup group = m.get(stock.code(), stock.isBuy());
		if (group != null && group.size() == 0) m.remove(stock.code(), stock.isBuy());
		return true;
	}
	
	
	private void recover() {
		List<Stock> stocks = getSelectedStocks();
		boolean ret = false;
		for (Stock stock : stocks) {
			if (stock == null) continue;
			boolean ret0 = recover0(stock);
			ret = ret || ret0;
		}
		
		if (ret) update();

	}
	
	
	private void recoverGroup() {
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
					boolean ret0 = recover0(rmStock);
					ret = ret || ret0;
				}
			}
		}
		
		if (ret) update();
	}
	
	
	
	@Override
	protected JPopupMenu createContextMenu() {
		JPopupMenu ctxMenu = new JPopupMenu();
		Stock stock = getSelectedStock();
		MarketTable tblMarket = this;

		if (!getModel2().isForStock()) {
			if (stock != null) {
				JMenuItem miView = new JMenuItem("View");
				miView.addActionListener( 
					new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							view(stock);
						}
					});
				ctxMenu.add(miView);
	
				JMenuItem miRecover = new JMenuItem("Recover");
				miRecover.addActionListener( 
					new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							recoverGroup();
						}
					});
				ctxMenu.add(miRecover);

				JMenuItem miDelete = new JMenuItem("Delete forever");
				miDelete.addActionListener( 
					new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							delete();
						}
					});
				ctxMenu.add(miDelete);
	
				ctxMenu.addSeparator();
			}
			
			JMenuItem miRefresh = new JMenuItem("Refresh");
			miRefresh.addActionListener( 
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						update();
					}
				});
			ctxMenu.add(miRefresh);

			return ctxMenu;
		}

		
		if (stock != null) {
			JMenuItem miRecover = new JMenuItem("Recover");
			miRecover.addActionListener( 
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						recover();
					}
				});
			ctxMenu.add(miRecover);

			JMenuItem miDelete = new JMenuItem("Delete foreover");
			miDelete.addActionListener( 
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						delete();
					}
				});
			ctxMenu.add(miDelete);

			ctxMenu.addSeparator();
			
			JMenuItem miDetailedSummary = new JMenuItem("Summary");
			miDetailedSummary.addActionListener( 
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						summary(stock);
					}
				});
			ctxMenu.add(miDetailedSummary);
		}
		
		JMenuItem miSummary = new JMenuItem("Market summary");
		miSummary.addActionListener( 
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					MarketSummary ms = new MarketSummary(getMarket(), StockProperty.RUNTIME_CASCADE ? tblMarket : null, tblMarket) {

						private static final long serialVersionUID = 1L;

						@Override
						protected MarketTable createMarketTable(Market market, MarketListener listener) {
							return new MarketTrashTable(market, false, listener);
						}
						
					};
					
					ms.setVisible(true);
					
					if (!StockProperty.RUNTIME_CASCADE) tblMarket.update();
				}
			});
		ctxMenu.add(miSummary);

		ctxMenu.addSeparator();
		
		JMenuItem miRefresh = new JMenuItem("Refresh");
		miRefresh.addActionListener( 
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					update();
				}
			});
		ctxMenu.add(miRefresh);

		
		return ctxMenu;
	}

	
}



class MarketTrashPanel extends MarketPanel {


	private static final long serialVersionUID = 1L;

	
	public MarketTrashPanel(Market market, boolean forStock, MarketListener listener) {
		super(market, forStock, listener);
		this.btnTake.setVisible(false);
		
		ActionListener[] als = this.btnSummary.getActionListeners();
		for (ActionListener al : als) {
			this.btnSummary.removeActionListener(al);
		}
		MarketTrashPanel thisPanel = this;
		btnSummary.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				MarketSummary ms = new MarketSummary(getMarket(), StockProperty.RUNTIME_CASCADE ? tblMarket : null, thisPanel) {

					private static final long serialVersionUID = 1L;

					@Override
					protected MarketTable createMarketTable(Market market, MarketListener listener) {
						return new MarketTrashTable(market, false, listener);
					}
					
				};
				
				ms.setVisible(true);
				if (!StockProperty.RUNTIME_CASCADE) tblMarket.update();
			}
		});
	
		this.lblStartTime.setVisible(false);
		this.lblBalance.setVisible(false);
		this.lblEquity.setVisible(false);
		this.lblMargin.setVisible(false);
		this.lblFreeMargin.setVisible(false);
		this.lblMarginLevel.setVisible(false);
		this.lblProfit.setVisible(false);
		this.lblROI.setVisible(false);
		this.lblBias.setVisible(false);
		this.lblEstInvest.setVisible(false);
	}


	@Override
	protected void update() {

	}


	@Override
	protected MarketTable createMarketTable(Market market, boolean forStock, MarketListener listener) {
		return new MarketTrashTable(market, forStock, listener);
	}


}



class MarketTrashDialog extends MarketDialog {

	
	private static final long serialVersionUID = 1L;
	
	
	public MarketTrashDialog(Market market, boolean forStock, MarketListener listener, Component parent) {
		super(market, forStock, listener, parent);
		btnOK.setText("Close");
		btnCancel.setVisible(false);
	}


	@Override
	protected MarketPanel createMarketPanel(Market market, boolean forStock, MarketListener listener) {
		MarketPanel mp = new MarketTrashPanel(market, forStock, listener);
		if (mp.getMarketTable() != null && listener != null && StockProperty.RUNTIME_CASCADE)
			mp.getMarketTable().getModel2().addMarketListener(listener);
		return mp;
	}


	
	
}
