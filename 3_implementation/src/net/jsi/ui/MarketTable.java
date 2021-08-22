package net.jsi.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Vector;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import net.jsi.Estimator;
import net.jsi.Market;
import net.jsi.MarketImpl;
import net.jsi.Price;
import net.jsi.QueryEstimator;
import net.jsi.Stock;
import net.jsi.StockGroup;
import net.jsi.StockImpl;
import net.jsi.Universe;
import net.jsi.Util;

public class MarketTable extends JTable implements MarketListener {

	
	private static final long serialVersionUID = 1L;

	
	public MarketTable(Market market, boolean forStock) {
		super();
		setModel(new MarketTableModel(market, forStock));

		setAutoCreateRowSorter(true);
		setAutoResizeMode(AUTO_RESIZE_OFF);
		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(SwingUtilities.isRightMouseButton(e) ) {
					JPopupMenu contextMenu = createContextMenu();
					if(contextMenu != null)
						contextMenu.show((Component)e.getSource(), e.getX(), e.getY());
				}
				else if (e.getClickCount() >= 2) {
					view();
				}
			}
		});
		
		this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					view();
				}
				else if(e.getKeyCode() == KeyEvent.VK_F5) {
					update();
				}
			}
		});

		update();
	}

	
	private void updateStock(Stock stock, boolean update) {
		StockTaker taker = new StockTaker(getMarket(), stock, update, this);
		taker.setVisible(true);
		if (taker.getOutput() != null) update();
	}
	
	
	private void view() {
		Stock stock = getSelectedStock();
		if (stock == null) return;
		
		if (getModel2().isForStock()) {
			StockTaker taker = new StockTaker(getMarket(), stock, true, this);
			taker.setVisible(true);
			if (taker.getOutput() != null) update();
		}
		else {
			new MarketGroupSummary(getMarket(), stock.code(), stock.isBuy(), this).setVisible(true);
		}
	}
	
	
	private void delete(Stock stock) {
		Universe universe = getMarket().getNearestUniverse();
		if (universe == null) return;
		MarketImpl m = ((Universe)universe).c(getMarket());
		if (m == null) return;
		
		if (getModel2().isForStock()) {
			Stock removedStock = m.removeStock(stock.code(), stock.isBuy(), m.getTimeViewInterval(), m.c(stock).getTakenTimePoint(m.getTimeViewInterval()));
			if (removedStock == null) return;
			
			StockGroup group = m.get(stock.code(), stock.isBuy());
			if (group != null && group.size() == 0) m.remove(stock.code(), stock.isBuy());
			update();
		}
		else {
			if (m.remove(stock.code(), stock.isBuy()) != null) update();
		}
	}
	
	
	protected JPopupMenu createContextMenu() {
		JPopupMenu ctxMenu = new JPopupMenu();
		Stock stock = getSelectedStock();
		MarketTable tblMarket = this;

		if (!getModel2().isForStock()) {
			JMenuItem miView = new JMenuItem("View");
			miView.addActionListener( 
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						view();
					}
				});
			ctxMenu.add(miView);

			if (stock != null) {
				JMenuItem miDelete = new JMenuItem("Delete");
				miDelete.addActionListener( 
					new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							delete(stock);
						}
					});
				ctxMenu.add(miDelete);
			}

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

		
		JMenuItem miTake = new JMenuItem("Add new");
		miTake.addActionListener( 
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					updateStock(stock, false);
				}
			});
		ctxMenu.add(miTake);
		
		if (stock != null) {
			JMenuItem miAddPrice = new JMenuItem("Add price");
			miAddPrice.addActionListener( 
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						AddPrice addPrice = new AddPrice(getMarket(), stock, tblMarket);
						addPrice.setVisible(true);
						if (addPrice.getOutput() != null) update();
					}
				});
			ctxMenu.add(miAddPrice);

			JMenuItem miModify = new JMenuItem("Update");
			miModify.addActionListener( 
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						updateStock(stock, true);
					}
				});
			ctxMenu.add(miModify);
			
			JMenuItem miCommit = new JMenuItem(stock.isCommitted() ? "Uncommit" : "Commit");
			miCommit.addActionListener( 
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						stock.setCommitted(!stock.isCommitted());
						update();
					}
				});
			ctxMenu.add(miCommit);

			JMenuItem miDelete = new JMenuItem("Delete");
			miDelete.addActionListener( 
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						delete(stock);
					}
				});
			ctxMenu.add(miDelete);
		}

		ctxMenu.addSeparator();
		
		JMenuItem miSummary = new JMenuItem("Summary");
		miSummary.addActionListener( 
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					MarketSummary ms = new MarketSummary(getMarket(), tblMarket);
					ms.getMarketTable().getModel2().addMarketListener(tblMarket);
					ms.setVisible(true);
				}
			});
		ctxMenu.add(miSummary);

		if (stock != null) {
			JMenuItem miDetailedSummary = new JMenuItem("Detailed summary");
			miDetailedSummary.addActionListener( 
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						new MarketGroupSummary(getMarket(), stock.code(), stock.isBuy(), tblMarket).setVisible(true);
					}
				});
			ctxMenu.add(miDetailedSummary);
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

	
	public MarketTableModel getModel2() {
		return (MarketTableModel) getModel();
	}
	
	
	public Market getMarket() {
		return getModel2().getMarket();
	}
	
	
	public void update() {
		getModel2().update();
	}
	
	
	public Stock getSelectedStock() {
		int selectedRow = getSelectedRow();
		if (selectedRow < 0) return null;
		return (Stock) getValueAt(selectedRow, 0);
	}
	
	
	@Override
	public void notify(MarketEvent evt) {
		update();
	}


	@Override
	public TableCellRenderer getCellRenderer(int row, int column) {
		Object value = getValueAt(row, column);
		if (value == null)
			return super.getCellRenderer(row, column);
		else {
			TableCellRenderer renderer = getDefaultRenderer(value.getClass());
			if(renderer == null)
				return super.getCellRenderer(row, column);
			else
				return renderer;
		}
	}

	
	@Override
    public TableCellEditor getCellEditor(int row, int column) {
		Object value = getValueAt(row, column);
		if (value == null)
			return super.getCellEditor(row, column);
		else {
			TableCellEditor editor = getDefaultEditor(value.getClass());
			if(editor == null)
				return super.getCellEditor(row, column);
			else
				return editor;
		}
    }

}



class MarketTableModel extends DefaultTableModel implements TableModelListener {

	
	private static final long serialVersionUID = 1L;

	
	protected Market market = null;
	
	
	protected boolean forStock = true;
	
	
    protected EventListenerList listenerList = new EventListenerList();

    
    public MarketTableModel(Market market, boolean forStock) {
		this.market = market;
		this.forStock = forStock;
	}
	
	
	public Market getMarket() {
		return market;
	}
	
	
	public boolean isForStock() {
		return forStock;
	}
	
	
	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}

	
	@Override
	public void tableChanged(TableModelEvent e) {

	}
	

	public void update() {
		setDataVector(new Object[][] {}, new Object[] {});
		
		Vector<Vector<Object>> data = Util.newVector(0);
		
		if (forStock) {
			List<Stock> stocks = market.getStocks(market.getTimeViewInterval());
			for (Stock stock : stocks) {
				Vector<Object> row = toRow(stock);
				if (row != null) data.add(row);
			}
		}
		else {
			Universe universe = market.getNearestUniverse();
			if (universe != null) {
				MarketImpl mi = ((Universe)universe).c(market);
				for (int i = 0; i < mi.size(); i++) {
					StockGroup group = mi.get(i);
					Vector<Object> row = toRow(group);
					if (row != null) data.add(row);
				}
			}

		}
		
		setDataVector(data, toColumns());
		
		fireInvestorEvent(new MarketEvent(this));
	}
	
	
	private Vector<Object> toRow(Stock stock) {
		long timeViewInterval = market.getTimeViewInterval();
		Vector<Object> row = Util.newVector(0);
		Universe u = market.getNearestUniverse();
		
		if (forStock) { 
			StockImpl s = market.c(stock);
			if (s == null || !s.isValid(timeViewInterval)) return null;

			row.add(stock);
			row.add(stock.isBuy());
			row.add(Util.format(s.getTakenPrice(timeViewInterval).getDate()));
			row.add(s.getVolume(timeViewInterval, false));
			row.add(s.getAverageTakenPrice(timeViewInterval));
			
			Price price = s.getPrice();
			row.add(price.get());
			row.add(price.getLow());
			row.add(price.getHigh());
			
			row.add(s.getMargin(timeViewInterval));
			row.add(s.getProfit(timeViewInterval));
			row.add(s.isCommitted());
		}
		else {
			StockGroup group = (StockGroup)stock;
			
			row.add(group);
			row.add(group.isBuy());
			row.add(group.getLeverage() != 0 ? 1.0 / group.getLeverage() : "Infinity");
			row.add(group.getVolume(timeViewInterval, true));
			row.add(group.getTakenValue(timeViewInterval));
			row.add(group.getMargin(timeViewInterval));
			row.add(group.getProfit(timeViewInterval));
			row.add(Util.format(group.getROIByLeverage(timeViewInterval) * 100) + "%");
			
			if (u == null || u.lookup(market.name()) < 0) {
				row.add("");
			}
			else {
				int index = u.lookup(market.name());
				QueryEstimator query = u.query(index);
				Estimator estimator = query.getEstimator(group.code(), group.isBuy());
				
				if (estimator == null) {
					row.add("");
					row.add("");
				}
				else {
					String stopLoss = Util.format(estimator.estimateStopLoss(timeViewInterval));
					String takeProfit = Util.format(estimator.estimateTakeProfit(timeViewInterval));
					row.add(stopLoss + " / " + takeProfit);

					String volume = Util.format(estimator.estimateTakenVolume(timeViewInterval));
					String amount = Util.format(estimator.estimateTakenAmount(timeViewInterval));
					String totalAmount = Util.format(estimator.getInvestAmount(timeViewInterval));
					row.add(volume + " (" + amount + " / " + totalAmount + ")");
				}
			}
		}

		return row;
	}
	
	
	/**
	 * Getting list of column names.
	 * @return list of column names.
	 */
	private Vector<String> toColumns() {
		Vector<String> columns = Util.newVector(0);
		
		if (forStock) {
			columns.add("Code");
			columns.add("Buy");
			columns.add("Date");
			columns.add("Volume");
			columns.add("Taken price");
			columns.add("Price");
			columns.add("Low price");
			columns.add("High price");
			columns.add("Margin");
			columns.add("Profit");
			columns.add("Committed");
		}
		else {
			columns.add("Code");
			columns.add("Buy");
			columns.add("Leverage");
			columns.add("Volume");
			columns.add("Taken value");
			columns.add("Margin");
			columns.add("Profit");
			columns.add("ROI");
			columns.add("Est. stop loss / take profit");
			columns.add("Rec. buy/sell");
		}
		
		return columns;
	}


	public void addMarketListener(MarketListener listener) {
		if (listener == null) return;
		synchronized (listenerList) {
			listenerList.add(MarketListener.class, listener);
		}
    }

	
    public void removeMarketListener(MarketListener listener) {
		synchronized (listenerList) {
			listenerList.remove(MarketListener.class, listener);
		}
    }
	
    
    protected MarketListener[] getMarketListeners() {
		synchronized (listenerList) {
			return listenerList.getListeners(MarketListener.class);
		}
    }

    
    protected void fireInvestorEvent(MarketEvent evt) {
    	MarketListener[] listeners = getMarketListeners();
		for (MarketListener listener : listeners) {
			try {
				listener.notify(evt);
			}
			catch (Exception e) { }
		}

    }



}
