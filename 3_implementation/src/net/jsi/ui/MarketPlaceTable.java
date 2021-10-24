package net.jsi.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import net.jsi.Market;
import net.jsi.MarketImpl;
import net.jsi.Price;
import net.jsi.Stock;
import net.jsi.StockGroup;
import net.jsi.StockImpl;
import net.jsi.StockInfoStore;
import net.jsi.StockProperty;
import net.jsi.Universe;
import net.jsi.Util;

public class MarketPlaceTable extends MarketTable {

	
	private static final long serialVersionUID = 1L;

	
	public MarketPlaceTable(Market market, boolean group, MarketListener listener) {
		super(market, group, listener);
	}


	@Override
	protected MarketTableModel createModel(Market market, boolean group) {
		return new MarketTableModel(market, group) {

			private static final long serialVersionUID = 1L;

			@Override
			protected Vector<Object> toRow(Stock stock) {
				long timeViewInterval = market.getTimeViewInterval();
				Vector<Object> row = Util.newVector(0);
				StockImpl s = market.c(stock);

				row.add(stock);
				row.add(stock.isBuy());
				row.add(stock.getLeverage() != 0 ? 1/stock.getLeverage() : 0);
				row.add(stock.getVolume(timeViewInterval, !(stock instanceof StockGroup)));
				
				if (stock instanceof StockGroup)
					row.add(new Time(0));
				else if (s != null)
					row.add(new Time(s.getTakenTimePoint(timeViewInterval)));
				else
					row.add(new Time(0));
				
				if (stock instanceof StockGroup)
					row.add(((StockGroup)stock).getTakenValue(timeViewInterval));
				else
					row.add(stock.getAverageTakenPrice(timeViewInterval));
				
				Price price = stock.getPrice();
				row.add(price.get());
				row.add(new Pair(price.getLow(), price.getHigh()));
				
				if (stock instanceof StockGroup)
					row.add(new Pair(Double.NaN, Double.NaN));
				else if (s != null)
					row.add(new Pair(s.getStopLoss(), s.getTakeProfit()));
				else
					row.add(new Pair(Double.NaN, Double.NaN));
				
				row.add(stock.getUnitBias());
				row.add(null);

				return row;
			}

			@Override
			protected Vector<String> toColumns() {
				Vector<String> columns = Util.newVector(0);

				columns.add("Code");
				columns.add("Buy");
				columns.add("Leverage");
				columns.add("Volume");
				columns.add("Taken date");
				if (isGroup())
					columns.add("Taken value");
				else
					columns.add("Taken price");
				columns.add("Price");
				columns.add("Low/high prices");
				columns.add("Stop loss / take profit");
				columns.add("Unit bias (setting)");
				columns.add("");

				return columns;
			}
			
			@Override
			public Class<?> getColumnClass(int columnIndex) {
				if (columnIndex == 0 || columnIndex == getColumnCount() - 1)
					return super.getColumnClass(columnIndex);
				else if (columnIndex == 1)
					return Boolean.class;
				else if (columnIndex == 4)
					return Time.class;
				else if (columnIndex == 7 || columnIndex == 8)
					return Pair.class;
				else
					return Double.class;
			}
			
		};
	}


	private class PlaceStockTaker extends StockTaker {

		private static final long serialVersionUID = 1L;
		
		protected boolean addPrice = false;
		
		public PlaceStockTaker(Market market, Stock input, boolean update, Component parent) {
			super(market, input, update, parent);
		}

		@Override
		protected void update() {
			super.update();
			chkAddPrice.setEnabled(addPrice);
		}
		
		@Override
		protected void switchSelector() {
			this.dispose();
			PlaceStockSelector selector = new PlaceStockSelector(market, input, update, parent);
			selector.addPrice = this.addPrice;
			selector.setVisible(true);
			this.setOutput(selector.getOutput());
		}
		
	}
	
	
	private class PlaceStockSelector extends StockSelector {

		private static final long serialVersionUID = 1L;
		
		protected boolean addPrice = false;
		
		public PlaceStockSelector(Market market, Stock input, boolean update, Component parent) {
			super(market, input, update, parent);
		}

		@Override
		protected void switchTaker() {
			this.dispose();
			PlaceStockTaker taker = new PlaceStockTaker(market, input, update, parent);
			taker.addPrice = this.addPrice;
			taker.setVisible(true);
			this.setOutput(taker.getOutput());
		}

	}
	
	
	@Override
	protected void take(Stock stock, boolean update) {
		stock = stock != null ? stock : getSelectedStock();
		PlaceStockTaker taker = new PlaceStockTaker(getMarket(), stock, update, this);
		taker.setVisible(true);
		if (taker.getOutput() != null) update();
	}


	@Override
	protected Stock addPrice(Stock stock) {
		Stock output = super.addPrice(stock);
		StockImpl s = c(stock);
		if (s == null) return output;
		
		Price lastPrice = s.getPrice();
		s.take(getMarket().getTimeViewInterval(), lastPrice.getTime());
		
		return output;
	}


	@Override
	protected MarketSummary createMarketSummary() {
		return new MarketSummary(getMarket(), StockProperty.RUNTIME_CASCADE ? this : null, this) {
			private static final long serialVersionUID = 1L;

			@Override
			protected MarketTable createMarketTable(Market market, MarketListener listener) {
				return new MarketPlaceTable(market, true, listener);
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
	
				if (stock != null) {
					JMenuItem miDelete = new JMenuItem("Delete");
					miDelete.addActionListener( 
						new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								deleteWithConfirm();
							}
						});
					ctxMenu.add(miDelete);
				}
	
				ctxMenu.addSeparator();
			
				JMenuItem miPriceList = new JMenuItem("Price list");
				miPriceList.addActionListener( 
					new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							priceList(stock);
						}
					});
				ctxMenu.add(miPriceList);
				
				JMenuItem miSettings = new JMenuItem("Settings");
				miSettings.addActionListener( 
					new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							settings(stock);
						}
					});
				ctxMenu.add(miSettings);
	
				JMenuItem miProperties = new JMenuItem("Properties");
				miProperties.addActionListener( 
					new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							properties(stock);
						}
					});
				ctxMenu.add(miProperties);

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

		
		JMenuItem miTake = new JMenuItem("Add new");
		miTake.addActionListener( 
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					take(stock, false);
				}
			});
		ctxMenu.add(miTake);
		
		if (stock != null) {
			StockImpl s = c(stock);

			JMenuItem miAddPrice = new JMenuItem("Add price");
			miAddPrice.addActionListener( 
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						addPrice(stock);
					}
				});
			ctxMenu.add(miAddPrice);

			JMenuItem miSetTakenPrice = new JMenuItem("Set taken price");
			miSetTakenPrice.addActionListener( 
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						setTakenPrice(stock);
					}
				});
			ctxMenu.add(miSetTakenPrice);

			JMenuItem miModify = new JMenuItem("Update");
			miModify.addActionListener( 
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						take(stock, true);
					}
				});
			ctxMenu.add(miModify);
			
			ctxMenu.addSeparator();

			JMenuItem miFixMargin = new JMenuItem(s != null && s.isFixedMargin() ? "Unfix margin" : "Fix margin");
			miFixMargin.addActionListener( 
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						toggleFixMargin(stock);
					}
				});
			ctxMenu.add(miFixMargin);

			JMenuItem miDelete = new JMenuItem("Delete");
			miDelete.addActionListener( 
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						deleteWithConfirm();
					}
				});
			ctxMenu.add(miDelete);

			ctxMenu.addSeparator();
			
			JMenuItem miModifyList = new JMenuItem("Price list");
			miModifyList.addActionListener( 
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						priceList(stock);
					}
				});
			ctxMenu.add(miModifyList);
			
			JMenuItem miSettings = new JMenuItem("Settings");
			miSettings.addActionListener( 
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						settings(stock);
					}
				});
			ctxMenu.add(miSettings);

			JMenuItem miProperties = new JMenuItem("Properties");
			miProperties.addActionListener( 
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						properties(stock);
					}
				});
			ctxMenu.add(miProperties);

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
		else
			ctxMenu.addSeparator();
		
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



class MarketPlacePanel extends MarketPanel {


	private static final long serialVersionUID = 1L;

	
	public MarketPlacePanel(Market market, boolean group, MarketListener superListener) {
		super(market, group, superListener);
		
		ActionListener[] als = btnSummary.getActionListeners();
		for (ActionListener al : als) btnSummary.removeActionListener(al);
		btnSummary.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tblMarket.marketSummary();
			}
		});
	}


	@Override
	protected MarketTable createMarketTable(Market market, boolean group, MarketListener superListener) {
		return new MarketPlaceTable(market, group, superListener);
	}


}



class MarketPlaceDialog extends MarketDialog {

	
	private static final long serialVersionUID = 1L;
	
	
	public MarketPlaceDialog(Market market, boolean group, MarketListener superListener, Component parent) {
		super(market, group, superListener, parent);
		btnCancel.setText("Close");
	}


	@Override
	protected MarketPanel createMarketPanel(Market market, boolean group, MarketListener superListener) {
		return new MarketPlacePanel(market, group, superListener);
	}


}



class MarketPlacePanel2 extends MarketPlacePanel {


	private static final long serialVersionUID = 1L;

	
	protected StockInfoStore prevStore;
	
	
	public MarketPlacePanel2(Market market, boolean group, MarketListener superListener) {
		super(market, group, superListener);
	}

	
	@Override
	protected MarketTable createMarketTable(Market market, boolean group, MarketListener superListener) {
		this.prevStore = null;
		if (market == null) return null;
		
		Universe  u = market.getNearestUniverse();
		MarketImpl m = u != null ? u.c(market) : null;
		if (m == null) return new MarketPlaceTable(market, group, superListener);
		
		try {
			MarketImpl dualMarket = u.c(m.getDualMarket());
			if (dualMarket != null) {
				this.prevStore = m.getStore().pricePoolSync();
				//m.getStore().cutPrices(m.getTimeViewInterval());
				m.getStore().sync(dualMarket.getStore(), m.getTimeViewInterval(), false);
				return new MarketPlaceTable(m, group, superListener);
			}
			else
				return new MarketPlaceTable(market, group, superListener);
		}
		catch (Throwable e) {
			this.prevStore = null;
			Util.trace(e);
		}
		
		return new MarketPlaceTable(market, group, superListener);
	}


	@Override
	protected void autoBackup() {
		MarketImpl m = tblMarket.m();
		
		//if (prevStore != null) m.getStore().sync(prevStore, 0, true);
		if (prevStore != null) m.getStore().retain(prevStore, m.getTimeViewInterval(), false);
	}


}



class MarketPlaceDialog2 extends MarketPlaceDialog {

	
	private static final long serialVersionUID = 1L;
	
	
	public MarketPlaceDialog2(Market market, boolean group, MarketListener superListener, Component parent) {
		super(market, group, superListener, parent);
	}


	@Override
	protected MarketPanel createMarketPanel(Market market, boolean group, MarketListener superListener) {
		return new MarketPlacePanel2(market, group, superListener);
	}


	@Override
	public void dispose() {
		paneMarket.autoBackup();
		super.dispose();
	}


}






