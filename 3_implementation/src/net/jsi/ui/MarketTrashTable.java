package net.jsi.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import net.jsi.EstimateStock;
import net.jsi.Market;
import net.jsi.MarketImpl;
import net.jsi.Stock;
import net.jsi.StockImpl;
import net.jsi.StockProperty;
import net.jsi.Util;

public class MarketTrashTable extends MarketTable {

	
	private static final long serialVersionUID = 1L;

	
	public MarketTrashTable(Market market, boolean group, MarketListener listener) {
		super(market, group, listener);
	}


	@Override
	protected void view(Stock stock) {
		stock = stock != null ? stock : getSelectedStock();
		if (stock == null && !getModel2().isGroup()) return;
		
		new StockDescription(getMarket(), stock.code(), stock.isBuy(), getModel2().isGroup() ? null : stock, this) {
			private static final long serialVersionUID = 1L;

			@Override
			protected List<EstimateStock> getEstimateStocks() {
				return Util.newList(0);
			}
		
		}.setVisible(true);
	}

	
	private void recover() {
		doTasksOnSelected(new Task() {
			@Override
			public boolean doOne(Stock stock) {
				return recover0(stock);
			}
		}, true);
	}
	
	
	private boolean recover0(Stock stock) {
		MarketImpl m = m(); if (m == null) return false;
		MarketImpl dualMarket = m.getDualMarket() instanceof MarketImpl ? (MarketImpl)m.getDualMarket() : null;
		return MarketImpl.recover(stock, m, dualMarket);
	}
	
	
	@Override
	protected MarketSummary createMarketSummary() {
		return new MarketSummary(getMarket(), StockProperty.RUNTIME_CASCADE ? this : null, this) {

			private static final long serialVersionUID = 1L;

			@Override
			protected MarketTable createMarketTable(Market market, MarketListener listener) {
				return new MarketTrashTable(market, true, listener);
			}
			
		};
	}


	@Override
	protected JPopupMenu createContextMenu() {
		JPopupMenu ctxMenu = new JPopupMenu();
		Stock stock = getSelectedStock();

		if (getModel2().isGroup()) {
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
							recover();
						}
					});
				ctxMenu.add(miRecover);

				JMenuItem miDelete = new JMenuItem("Delete forever");
				miDelete.addActionListener( 
					new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							deleteWithConfirm();
						}
					});
				ctxMenu.add(miDelete);
	
				ctxMenu.addSeparator();
			}
			
			JMenuItem mniSortCodes = new JMenuItem(
				new AbstractAction("Sort codes") {
					private static final long serialVersionUID = 1L;

					@Override
					public void actionPerformed(ActionEvent e) {
						sortCodes();
					}
				});
			ctxMenu.add(mniSortCodes);

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
			StockImpl s = c(stock);

			JMenuItem miRecover = new JMenuItem("Recover");
			miRecover.addActionListener( 
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						recover();
					}
				});
			ctxMenu.add(miRecover);

			int selectedCount = getSelectedRowCount();
			JMenuItem miCommit = new JMenuItem(selectedCount == 1 ? (stock.isCommitted() ? "Uncommit" : "Commit") : "Switch commit");
			miCommit.addActionListener( 
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						commit();
					}
				});
			ctxMenu.add(miCommit);

			JMenuItem miFixMargin = new JMenuItem(s != null && s.isFixedMargin() ? "Unfix margin" : "Fix margin");
			miFixMargin.addActionListener( 
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						toggleFixMargin(stock);
					}
				});
			ctxMenu.add(miFixMargin);

			JMenuItem miDelete = new JMenuItem("Delete foreover");
			miDelete.addActionListener( 
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						deleteWithConfirm();
					}
				});
			ctxMenu.add(miDelete);

			ctxMenu.addSeparator();
			
			JMenuItem miDesc = new JMenuItem("Description");
			miDesc.addActionListener( 
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						description(stock);
					}
				});
			ctxMenu.add(miDesc);
		}
		
		JMenuItem miSummary = new JMenuItem("Market summary");
		miSummary.addActionListener( 
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					marketSummary();
				}
			});
		ctxMenu.add(miSummary);

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

	
	public MarketTrashPanel(Market market, boolean group, MarketListener superListener) {
		super(market, group, superListener);
		btnTake.setVisible(false);
		btnReestimateLossesProfits.setVisible(false);
		btnReestimateUnitBiases.setVisible(false);
		
		ActionListener[] als = btnSummary.getActionListeners();
		for (ActionListener al : als) {
			btnSummary.removeActionListener(al);
		}
		btnSummary.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tblMarket.marketSummary();
			}
		});
	
	}


	@Override
	protected MarketTable createMarketTable(Market market, boolean group, MarketListener superListener) {
		return new MarketTrashTable(market, group, superListener);
	}


}



class MarketTrashDialog extends MarketDialog {

	
	private static final long serialVersionUID = 1L;
	
	
	public MarketTrashDialog(Market market, boolean group, MarketListener superListener, Component parent) {
		super(market, group, superListener, parent);
		btnCancel.setText("Close");
	}


	@Override
	protected MarketPanel createMarketPanel(Market market, boolean group, MarketListener superListener) {
		return new MarketTrashPanel(market, group, superListener);
	}


}
