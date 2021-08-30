/**
 * JSI: JAGGED STRATEGY INVESTMENT 
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: jsi.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.jsi.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Reader;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.EventListenerList;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import net.jsi.EstimateStock;
import net.jsi.Estimator;
import net.jsi.Market;
import net.jsi.MarketImpl;
import net.jsi.Price;
import net.jsi.QueryEstimator;
import net.jsi.Stock;
import net.jsi.StockAbstract;
import net.jsi.StockGroup;
import net.jsi.StockImpl;
import net.jsi.StockProperty;
import net.jsi.TakenPrice;
import net.jsi.Universe;
import net.jsi.Util;

public class MarketTable extends JTable implements MarketListener {

	
	private static final long serialVersionUID = 1L;

	
	public MarketTable(Market market, boolean forStock, MarketListener listener) {
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
					if ((e.getModifiersEx() &  MouseEvent.SHIFT_DOWN_MASK) == MouseEvent.SHIFT_DOWN_MASK)
						summary(null);
					else
						view(null);
				}
			}
		});
		
		this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					if ((e.getModifiersEx() &  MouseEvent.SHIFT_DOWN_MASK) == MouseEvent.SHIFT_DOWN_MASK)
						summary(null);
					else
						view(null);
				}
				else if(e.getKeyCode() == KeyEvent.VK_F5) {
					update();
				}
			}
		});

		update();
		
		if (listener != null) getModel2().addMarketListener(listener);
	}

	
	private void updateStock(Stock stock, boolean update) {
		stock = stock != null ? stock : getSelectedStock();
		StockTaker taker = new StockTaker(getMarket(), stock, update, this);
		taker.setVisible(true);
		if (taker.getOutput() != null) update();
	}
	
	
	private void view(Stock stock) {
		if (getModel2().isForStock())
			updateStock(stock, true);
		else {
			stock = stock != null ? stock : getSelectedStock();
			if (stock != null) {
				new StockSummary(getMarket(), stock.code(), stock.isBuy(), null, this) {
					private static final long serialVersionUID = 1L;

					@Override
					protected List<EstimateStock> getEstimateStocks() {
						return Util.newList(0);
					}
				
				}.setVisible(true);
			}
		}
	}
	
	
	private void summary(Stock stock) {
		if (!getModel2().isForStock()) return;
		
		stock = stock != null ? stock : getSelectedStock();
		if (stock == null) return;
		String code = stock.code();
		new StockSummary(getMarket(), stock.code(), stock.isBuy(), stock, this) {
			private static final long serialVersionUID = 1L;

			@Override
			protected List<EstimateStock> getEstimateStocks() {
				return getModel2().getEstimateStocks(code);
			}
		
		}.setVisible(true);
	}
	
	
	private boolean delete0(Stock stock) {
		if (stock == null) return false;
		MarketImpl m = m(); if (m == null) return false;
		
		if (getModel2().isForStock()) {
			Stock removedStock = m.removeStock(stock.code(), stock.isBuy(), m.getTimeViewInterval(), m.c(stock).getTakenTimePoint(m.getTimeViewInterval()));
			if (removedStock == null) return false;
			
			StockGroup group = m.get(stock.code(), stock.isBuy());
			if (group != null && group.size() == 0) m.remove(stock.code(), stock.isBuy());
			
			return true;
		}
		else {
			return m.remove(stock.code(), stock.isBuy()) != null;
		}
	}

	
	private void delete() {
		List<Stock> stocks = getSelectedStocks();
		boolean ret = false;
		for (Stock stock : stocks) {
			if (stock == null) continue;
			boolean ret0 = delete0(stock);
			ret = ret || ret0;
		}
		
		if (ret) update();
	}
	
	
	private void setTakenPrice(Stock stock) {
		stock = stock != null ? stock : getSelectedStock(); if (stock == null) return;
		
		PriceList pl = new PriceList(getMarket(), stock, getMarket().getTimeViewInterval(), false, true, this);
		pl.setVisible(true);
		Price selectedPrice = pl.getOutput();
		if (selectedPrice == null) return;
		
		StockImpl s = c(stock);
		if (s != null) s.take(getMarket().getTimeViewInterval(), selectedPrice.getTime());
	}
	
	
	private void modifyPriceList(Stock stock) {
		stock = stock != null ? stock : getSelectedStock();
		if (stock == null) return;
		
		PriceList pl = new PriceList(getMarket(), stock, getMarket().getTimeViewInterval(), true, false, this);
		pl.setVisible(true);
		if (pl.isPressOK()) update();
	}
	
	private void settings(Stock stock) {
		if (getModel2().isForStock()) return;
		stock = stock != null ? stock : getSelectedStock();
		if (stock == null) return;
		StockGroup group = stock instanceof StockGroup ? (StockGroup)stock : null;
		if (group == null) return;

		JDialog dlgSettings = new JDialog(Util.getFrameForComponent(this), "Settings for stock group", true);
		dlgSettings.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dlgSettings.setSize(300, 200);
		dlgSettings.setLocationRelativeTo(Util.getFrameForComponent(this));
		dlgSettings.setLayout(new BorderLayout());
		
		
		JPanel header = new JPanel(new BorderLayout());
		dlgSettings.add(header, BorderLayout.NORTH);
		
		JPanel left = new JPanel(new GridLayout(0, 1));
		header.add(left, BorderLayout.WEST);
		
		left.add(new JLabel("Leverage: "));
		left.add(new JLabel("Unit bias: "));
		left.add(new JLabel("Price ratio: "));
		
		JPanel right = new JPanel(new GridLayout(0, 1));
		header.add(right, BorderLayout.CENTER);
		
		double leverage = group.getLeverage();
		JPanel paneLeverage = new JPanel(new BorderLayout());
		right.add(paneLeverage);
		JFormattedTextField txtLeverage = new JFormattedTextField(Util.getNumberFormatter());
		txtLeverage.setValue(leverage != 0 ? 1.0/leverage : 0);
		paneLeverage.add(txtLeverage, BorderLayout.CENTER);
		
		JPanel paneUnitBias = new JPanel(new BorderLayout());
		right.add(paneUnitBias);
		JFormattedTextField txtUnitBias = new JFormattedTextField(Util.getNumberFormatter());
		txtUnitBias.setValue(group.getUnitBias());
		paneUnitBias.add(txtUnitBias, BorderLayout.CENTER);
		//
		JButton btnUnitBias = new JButton("Estimate");
		btnUnitBias.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MarketImpl m = m();
				Estimator estimator = m.getEstimator(group.code(), group.isBuy());
				if (estimator != null)  txtUnitBias.setValue(estimator.estimateUnitBiasFromData(m.getTimeViewInterval()));
			}
		});
		paneUnitBias.add(btnUnitBias, BorderLayout.EAST);
		
		double priceRatio = group.getProperty().priceRatio;
		JPanel panePriceRatio = new JPanel(new BorderLayout());
		right.add(panePriceRatio);
		JFormattedTextField txtPriceRatio = new JFormattedTextField(Util.getNumberFormatter());
		txtPriceRatio.setValue(priceRatio);
		panePriceRatio.add(txtPriceRatio, BorderLayout.CENTER);
		
		
		JPanel footer = new JPanel();
		dlgSettings.add(footer, BorderLayout.SOUTH);
		
		JButton ok = new JButton("OK");
		ok.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				double leverage = txtLeverage.getValue() instanceof Number ? ((Number)txtLeverage.getValue()).doubleValue() : StockProperty.LEVERAGE;
				double unitBias = txtUnitBias.getValue() instanceof Number ? ((Number)txtUnitBias.getValue()).doubleValue() : StockProperty.UNIT_BIAS;
				double priceRatio = txtPriceRatio.getValue() instanceof Number ? ((Number)txtPriceRatio.getValue()).doubleValue() : StockProperty.PRICE_RATIO;
				
				group.setLeverage(leverage != 0 ? 1/leverage : leverage, true);
				group.setUnitBias(unitBias, true);
				group.getProperty().priceRatio = priceRatio;
				
				update();
				
				dlgSettings.dispose();
			}
		});
		footer.add(ok);
		
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dlgSettings.dispose();
			}
		});
		footer.add(cancel);
		
		dlgSettings.setVisible(true);
	}
	
	
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
	
				if (stock != null) {
					JMenuItem miDelete = new JMenuItem("Delete");
					miDelete.addActionListener( 
						new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								delete();
							}
						});
					ctxMenu.add(miDelete);
				}
	
				ctxMenu.addSeparator();
			}
			
			JMenuItem miModifyList = new JMenuItem("Modify price list");
			miModifyList.addActionListener( 
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						modifyPriceList(stock);
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
						StockPropertySetting setting = new StockPropertySetting(stock.getProperty(), tblMarket);
						setting.setVisible(true);
						StockProperty output = setting.getOutput();
						if (output != null) {
							stock.getProperty().set(output);
							update();
						}
					}
				});
			ctxMenu.add(miProperties);

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
						updateStock(stock, true);
					}
				});
			ctxMenu.add(miModify);
			
			ctxMenu.addSeparator();

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
						delete();
					}
				});
			ctxMenu.add(miDelete);
		}

		ctxMenu.addSeparator();
		
		JMenuItem miModifyList = new JMenuItem("Modify price list");
		miModifyList.addActionListener( 
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					modifyPriceList(stock);
				}
			});
		ctxMenu.add(miModifyList);
		
		if (stock != null) {
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
					MarketSummary ms = new MarketSummary(getMarket(), tblMarket, tblMarket);
					ms.getMarketTable().getModel2().addMarketListener(tblMarket);
					ms.setVisible(true);
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

	
	public MarketTableModel getModel2() {
		return (MarketTableModel) getModel();
	}
	
	
	public Market getMarket() {
		return getModel2().getMarket();
	}
	
	
	public Market getPlacedMarket() {
		Universe u = getMarket().getNearestUniverse();
		return u != null ? u.getPlacedMarket(getMarket().getName()) : null;
	}
	
	
	public void update() {
		getModel2().update();
	}
	
	
	public Stock getSelectedStock() {
		int selectedRow = getSelectedRow();
		if (selectedRow < 0) return null;
		return (Stock) getValueAt(selectedRow, 0);
	}
	
	
	public List<Stock> getSelectedStocks() {
		List<Stock> stocks = Util.newList(0);
		int[] selectedRows = getSelectedRows();
		for (int selectedRow : selectedRows) {
			Stock stock = (Stock) getValueAt(selectedRow, 0);
			if (stock != null) stocks.add(stock);
		}

		return stocks;
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

	
	protected boolean open(Reader reader) {
        try {
        	boolean ret = m().open(reader);
        	update();
        	
            return ret;
        }
        catch (Exception e) {
        	e.printStackTrace();
        }
        
        return false;
	}

	
	protected boolean save(Writer writer) {
        try {
        	boolean ret = m().save(writer);
        	writer.flush();
            return ret;
        }
        catch (Exception e) {
        	e.printStackTrace();
        }
        
        return false;
	}
	
	
	public MarketImpl m() {
		return getModel2().m();
	}


	private StockImpl c(Stock stock) {
		MarketImpl m = m();
		return m != null ? m.c(stock) : null;
	}
	
	
	public void applyPlaced() {
		getModel2().applyPlaced();
	}
	
	
}



class MarketTableModel extends DefaultTableModel implements MarketListener, TableModelListener {

	
	private static final long serialVersionUID = 1L;

	
	protected Market market = null;
	
	
	protected boolean forStock = true;
	
	
	protected Map<String, List<EstimateStock>> estimators = Util.newMap(0);
	
	
    protected EventListenerList listenerList = new EventListenerList();

    
    public MarketTableModel(Market market, boolean forStock) {
		this.market = market;
		this.forStock = forStock;
	}
	
	
	protected MarketImpl m() {
		Universe u = market.getNearestUniverse();
		return u != null ? u.c(market) : null;
	}


	public Market getMarket() {
		return market;
	}
	
	
	public boolean isForStock() {
		return forStock;
	}
	
	
	public List<EstimateStock> getEstimateStocks(String code) {
		if (estimators.containsKey(code))
			return estimators.get(code);
		else
			return Util.newList(0);
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
			estimators.clear();
			MarketImpl m = m();
			Universe u = m != null ? m.getNearestUniverse() : null;
			if (u != null) {
				QueryEstimator query = u.query(market.getName(), market);
				for (int i = 0; i < m.size(); i++) {
					StockGroup group = m.get(i);
					Estimator estimator = query.getEstimator(group.code(), group.isBuy());
					List<EstimateStock> estimateStocks = estimator.estimateStopLossTakeProfit(group.getStocks(group.getTimeValidInterval()), group.getTimeViewInterval());
					estimators.put(group.code(), estimateStocks);
				}
			}
			
			List<Stock> stocks = market.getStocks(market.getTimeViewInterval());
			for (Stock stock : stocks) {
				Vector<Object> row = toRow(stock);
				data.add(row);
			}
		}
		else {
			MarketImpl m = m();
			if (m != null) {
				List<StockGroup> groups = m.getGroups(market.getTimeValidInterval());
				for (StockGroup group : groups) {
					Vector<Object> row = toRow(group);
					if (row != null) data.add(row);
				}
			}

		}
		
		setDataVector(data, toColumns());
		
		fireInvestorEvent(new MarketEvent(this));
	}
	
	
	protected void applyPlaced() {
		MarketImpl m = m();
		if (m != null) {
			m.applyPlaced();
			update();
		}
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
			row.add(Util.format(price.getLow()) + " / " + Util.format(price.getHigh()));
			row.add(Util.format(s.getStopLoss()) + " / " + Util.format(s.getTakeProfit()));
			
			row.add(s.getMargin(timeViewInterval));
			row.add(s.getProfit(timeViewInterval));
			row.add(s.isCommitted());
			
			List<EstimateStock> estimateStocks = getEstimateStocks(stock.code());
			EstimateStock found = EstimateStock.get(stock.code(), stock.isBuy(), estimateStocks);
			if (found != null)
				row.add(Util.format(found.estimatedPrice) + " / " + Util.format(found.estimatedStopLoss) + " / " + Util.format(found.estimatedTakeProfit));
			else
				row.add("");
				
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
			
			if (u == null || u.lookup(market.getName()) < 0) {
				row.add("");
			}
			else {
				QueryEstimator query = u.query(market.getName(), market);
				Estimator estimator = query.getEstimator(group.code(), group.isBuy());
				
				if (estimator == null) {
					row.add("");
				}
				else {
					if (group.getLeverage() != 0) {
						String volume = Util.format(estimator.estimateInvestVolume(timeViewInterval));
						String amount = Util.format(estimator.estimateInvestAmount(timeViewInterval));
						String totalAmount = Util.format(estimator.getInvestAmount(timeViewInterval));
						row.add(volume + " (" + amount + " / " + totalAmount + ")");
					}
					else
						row.add("Infinity");
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
			columns.add("Low/high prices");
			columns.add("Stop loss / take profit");
			columns.add("Margin");
			columns.add("Profit");
			columns.add("Committed");
			columns.add("Est. price / stop loss / take profit");
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


	@Override
	public void notify(MarketEvent evt) {
		update();
	}


}


class MarketSummary extends JDialog {

	
	private static final long serialVersionUID = 1L;

	
	protected Market market = null;
	
	
	protected MarketTable tblMarket = null;
	
	
	public MarketSummary(Market market, MarketListener listener, Component component) {
		super(Util.getFrameForComponent(component), "Market summary", true);
		this.market = market;
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setSize(600, 400);
		setLocationRelativeTo(Util.getFrameForComponent(component));
		setLayout(new BorderLayout());
		
		JPanel body = new JPanel(new BorderLayout());
		add(body, BorderLayout.CENTER);
		
		tblMarket = new MarketTable(market, false, listener);
		body.add(new JScrollPane(tblMarket), BorderLayout.CENTER);
		
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
	
	
	public MarketTable getMarketTable() {
		return tblMarket;
	}
	
	
}



abstract class StockSummary extends JDialog {

	
	private static final long serialVersionUID = 1L;

	
	protected Market market = null;
	
	
	public StockSummary(Market market, String code, boolean buy, Stock stock, Component component) {
		super(Util.getFrameForComponent(component), stock != null ? "Stock summary" : "Stock group summary", true);
		this.market = market;
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setSize(300, 400);
		setLocationRelativeTo(Util.getFrameForComponent(component));
		setLayout(new BorderLayout());
		
		JPanel body = new JPanel(new BorderLayout());
		add(body, BorderLayout.CENTER);
		
		long timeViewInterval = market.getTimeViewInterval();
		JTextArea txtInfo = new JTextArea();
		txtInfo.setWrapStyleWord(true);
		txtInfo.setLineWrap(true);
		txtInfo.setEditable(false);
		body.add(new JScrollPane(txtInfo), BorderLayout.CENTER);

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
		
		
		Universe u = market.getNearestUniverse();
		if (u == null) return;
		MarketImpl m = u.c(market);
		if (m == null) return;
		StockGroup group = m.get(code, buy);
		if (group == null) return;

		int index = u.lookup(market.getName());
		if (index < 0) return;
		QueryEstimator query = u.query(market.getName(), market);
		Estimator estimator = query.getEstimator(code, buy);
		if (estimator == null) return;
		
		List<EstimateStock> estimateStocks = getEstimateStocks();
		StringBuffer info = new StringBuffer();
		info.append("Code: " + code + "\n");
		info.append("Buy: " + buy + "\n");
		
		if (stock == null || estimateStocks == null || estimateStocks.size() == 0) {
			info.append("Leverage: " + (group.getLeverage() != 0 ? Util.format(1.0 / group.getLeverage()) : "Infinity") + "\n");
			info.append("Volume: " + Util.format(group.getVolume(timeViewInterval, true)) + "\n");
			info.append("Taken value: " + Util.format(group.getTakenValue(timeViewInterval)) + "\n");
			info.append("Margin: " + Util.format(group.getMargin(timeViewInterval)) + "\n");
			info.append("Profit: " + Util.format(group.getProfit(timeViewInterval)) + "\n");
			info.append("ROI: " + Util.format(group.getROIByLeverage(timeViewInterval)*100) + "%\n");
			info.append("Unit bias (setting): " + Util.format(group.getUnitBias()) + "\n");
			
			info.append("\n");
			info.append("Estimated unit bias: " + Util.format(estimator.estimateUnitBias(timeViewInterval)) + "\n");
			info.append("Estimated total bias: " + Util.format(m.calcTotalBias(timeViewInterval)) + "\n");
			info.append("Estimated price: " + Util.format(estimator.estimatePrice(timeViewInterval)) + "\n");
			info.append("Estimated low price: " + Util.format(estimator.estimateLowPrice(timeViewInterval)) + "\n");
			info.append("Estimated high price: " + Util.format(estimator.estimateHighPrice(timeViewInterval)) + "\n");
	
			info.append("\n");
			if (group.getLeverage() != 0) {
				double investAmount = estimator.getInvestAmount(timeViewInterval);
				if (investAmount > 0) {
					info.append("Estimated invest amount: " + Util.format(investAmount) + "\n");
					double recInvestAmount = estimator.estimateInvestAmount(timeViewInterval);
					if (recInvestAmount > 0)
						info.append("Recommended invest amount: " + Util.format(recInvestAmount) + "\n");
					double recInvestVolume = estimator.estimateInvestVolume(timeViewInterval);
					if (recInvestVolume > 0)
						info.append("Recommended invest volume: " + Util.format(recInvestVolume) + "\n");
				}
			}
			else {
				info.append("Recommended invest amount: Free due to infinity leverage"+ "\n");
			}
			
			Estimator.Invest[] dualInvest = estimator.estimateDualInvest(timeViewInterval);
			if (dualInvest != null && dualInvest.length >= 2) {
				info.append("\n");
				
				info.append((dualInvest[0].buy ? "Buy" : "Sell") + "1\n");
				info.append("Volume: " + dualInvest[0].volume + "\n");
				info.append("Price: " + dualInvest[0].price + "\n");
				info.append("Margin: " + dualInvest[0].margin + "\n");
				info.append("Stop loss: " + dualInvest[0].stopLoss + "\n");
				info.append("Take profit: " + dualInvest[0].takeProfit + "\n");
				
				info.append((dualInvest[1].buy ? "Buy" : "Sell") + "1\n");
				info.append("Volume: " + dualInvest[1].volume + "\n");
				info.append("Price: " + dualInvest[1].price + "\n");
				info.append("Margin: " + dualInvest[1].margin + "\n");
				info.append("Stop loss: " + dualInvest[1].stopLoss + "\n");
				info.append("Take profit: " + dualInvest[1].takeProfit + "\n");
				info.append("Take profit (large): " + dualInvest[1].largeTakeProfit + "\n");
			}
		}
		else {
			StockImpl s = m.c(stock);
			info.append("Leverage: " + (stock.getLeverage() != 0 ? Util.format(1.0 / stock.getLeverage()) : "Infinity") + "\n");
			info.append("Volume: " + Util.format(stock.getVolume(timeViewInterval, true)) + "\n");
			if (s != null)
				info.append("Taken price: " + Util.format(s.getTakenPrice(timeViewInterval).get()) + "\n");
			info.append("Taken value: " + Util.format(stock.getTakenValue(timeViewInterval)) + "\n");
			info.append("Price: " + Util.format(stock.getPrice().get()) + "\n");
			info.append("Low price: " + Util.format(stock.getPrice().getLow()) + "\n");
			info.append("High price: " + Util.format(stock.getPrice().getHigh()) + "\n");
			info.append("Stop loss: " + Util.format(s.getStopLoss()) + "\n");
			info.append("Take profit: " + Util.format(s.getTakeProfit()) + "\n");
			info.append("Margin: " + Util.format(stock.getMargin(timeViewInterval)) + "\n");
			info.append("Profit: " + Util.format(stock.getProfit(timeViewInterval)) + "\n");
			info.append("ROI: " + Util.format(stock.getROIByLeverage(timeViewInterval)*100) + "%\n");
			info.append("Unit bias (setting): " + Util.format(s.getUnitBias()) + "\n");
			
			info.append("\n");
			info.append("Estimated unit bias: " + Util.format(estimator.estimateUnitBias(timeViewInterval)) + "\n");
			info.append("Estimated price: " + Util.format(estimator.estimatePrice(timeViewInterval)) + "\n");
			info.append("Estimated low price: " + Util.format(estimator.estimateLowPrice(timeViewInterval)) + "\n");
			info.append("Estimated high price: " + Util.format(estimator.estimateHighPrice(timeViewInterval)) + "\n");
			//
			if (estimateStocks == null || estimateStocks.size() == 0) {
				info.append("Estimated stop loss: " + Util.format(estimator.estimateStopLoss(timeViewInterval)) + "\n");
				info.append("Estimated take profit: " + Util.format(estimator.estimateTakeProfit(timeViewInterval)) + "\n");
			}
			else {
				EstimateStock found = EstimateStock.get(code, buy, estimateStocks);
				if (found == null) {
					info.append("Estimated stop loss: " + Util.format(estimator.estimateStopLoss(timeViewInterval)) + "\n");
					info.append("Estimated take profit: " + Util.format(estimator.estimateTakeProfit(timeViewInterval)) + "\n");
				}
				else {
					info.append("Estimated stop loss: " + Util.format(found.estimatedStopLoss) + "\n");
					info.append("Estimated take profit: " + Util.format(found.estimatedTakeProfit) + "\n");
				}
			}
		}
		txtInfo.setText(info.toString());
	}
	
	
	protected abstract List<EstimateStock> getEstimateStocks();
	
	
}



class AddPrice extends JDialog {


	private static final long serialVersionUID = 1L;


	protected JFormattedTextField txtPrice;
	
	
	protected JButton btnPrice;

	
	protected JFormattedTextField txtLowPrice;
	
	
	protected JButton btnLowPrice;
	
	
	protected JFormattedTextField txtHighPrice;
	
	
	protected JButton btnHighPrice;
	
	
	protected JFormattedTextField txtLastDate;
	
	
	protected JButton btnLastDateNow;

	
	protected JButton btnLastDateList;
			
	
	protected Market market = null;
	
	
	protected Stock input = null;

	
	protected Stock output = null;

	
	public AddPrice(Market market, Stock input, Component parent) {
		super(Util.getFrameForComponent(parent), "Add price", true);
		this.market = market;
		this.input = input;
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(350, 250);
		setLocationRelativeTo(Util.getFrameForComponent(parent));
		setLayout(new BorderLayout());
		
		JPanel header = new JPanel(new BorderLayout());
		add(header, BorderLayout.NORTH);
		
		JPanel left = new JPanel(new GridLayout(0, 1));
		header.add(left, BorderLayout.WEST);
		
		left.add(new JLabel("Price (*): "));
		left.add(new JLabel("Low price (*): "));
		left.add(new JLabel("High price (*): "));
		left.add(new JLabel("Last date: "));

		JPanel right = new JPanel(new GridLayout(0, 1));
		header.add(right, BorderLayout.CENTER);
		
		JPanel panePrice = new JPanel(new BorderLayout());
		right.add(panePrice);
		txtPrice = new JFormattedTextField(Util.getNumberFormatter());
		txtPrice.setValue(input.getPrice().get());
		panePrice.add(txtPrice, BorderLayout.CENTER);
		//
		btnPrice = new JButton("Estimate");
		btnPrice.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Estimator estimator = getEstimator();
				if (estimator != null)  txtPrice.setValue(estimator.estimatePrice(market.getTimeViewInterval()));
			}
		});
		panePrice.add(btnPrice, BorderLayout.EAST);
		
		JPanel paneLowPrice = new JPanel(new BorderLayout());
		right.add(paneLowPrice);
		txtLowPrice = new JFormattedTextField(Util.getNumberFormatter());
		txtLowPrice.setValue(input.getPrice().getLow());
		paneLowPrice.add(txtLowPrice, BorderLayout.CENTER);
		//
		btnLowPrice = new JButton("Estimate");
		btnLowPrice.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Estimator estimator = getEstimator();
				if (estimator != null)  txtLowPrice.setValue(estimator.estimateLowPrice(market.getTimeViewInterval()));
			}
		});
		paneLowPrice.add(btnLowPrice, BorderLayout.EAST);
		
		JPanel paneHighPrice = new JPanel(new BorderLayout());
		right.add(paneHighPrice);
		txtHighPrice = new JFormattedTextField(Util.getNumberFormatter());
		txtHighPrice.setValue(input.getPrice().getHigh());
		paneHighPrice.add(txtHighPrice, BorderLayout.CENTER);
		//
		btnHighPrice = new JButton("Estimate");
		btnHighPrice.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Estimator estimator = getEstimator();
				if (estimator != null)  txtHighPrice.setValue(estimator.estimateHighPrice(market.getTimeViewInterval()));
			}
		});
		paneHighPrice.add(btnHighPrice, BorderLayout.EAST);
		
		JPanel paneLastDate = new JPanel(new BorderLayout());
		right.add(paneLastDate);
		txtLastDate = new JFormattedTextField(Util.getDateFormatter());
		txtLastDate.setValue(new Date(input.getPrice().getDate().getTime() + StockProperty.TIME_UPDATE_PRICE_INTERVAL));
		paneLastDate.add(txtLastDate, BorderLayout.CENTER);
		//
		JPanel paneLastDate2 = new JPanel(new GridLayout(1, 0));
		paneLastDate.add(paneLastDate2, BorderLayout.EAST);
		btnLastDateNow = new JButton("Now");
		btnLastDateNow.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				txtLastDate.setValue(new Date());
			}
		});
		btnLastDateNow.setEnabled(true);
		paneLastDate2.add(btnLastDateNow);
		//
		btnLastDateList = new JButton("List");
		btnLastDateList.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listPrices();
			}
		});
		paneLastDate2.add(btnLastDateList);
		
		
		JPanel footer = new JPanel();
		add(footer, BorderLayout.SOUTH);
		
		JButton ok = new JButton("Add price");
		ok.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ok();
			}
		});
		footer.add(ok);
		
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		footer.add(cancel);
	}
	
	
	private MarketImpl m() {
		Universe u = market.getNearestUniverse();
		return u != null ? u.c(market) : null;
	}

	
	private Estimator getEstimator() {
		MarketImpl m = m();
		return m != null ? m.getEstimator(input.code(), input.isBuy()) : null;
	}


	private void listPrices() {
		if (input == null) return;
		
		PriceList pl = new PriceList(market, input, market.getTimeViewInterval(), false, false, this);
		pl.setVisible(true);
	}
	
	
	private boolean validateInput() {
		double price = txtPrice.getValue() instanceof Number ? ((Number)txtPrice.getValue()).doubleValue() : 0;
		if (price < 0) return false;

		double lowPrice = txtLowPrice.getValue() instanceof Number ? ((Number)txtLowPrice.getValue()).doubleValue() : 0;
		if (lowPrice < 0) return false;

		double highPrice = txtHighPrice.getValue() instanceof Number ? ((Number)txtHighPrice.getValue()).doubleValue() : 0;
		if (highPrice < 0) return false;
		
		if (price < lowPrice || price > highPrice) return false;
		
		Date lastDate = txtLastDate.getValue() instanceof Date ? (Date)txtLastDate.getValue() : null;
		Universe universe = market.getNearestUniverse();
		if (lastDate == null || input == null || universe == null) return false;
		StockImpl s = universe.c(input);
		if (s == null || !s.checkPriceTimePoint(lastDate.getTime()))
			return false;
		
		return true;
	}
	
	
	private void ok() {
		if (!validateInput()) {
			JOptionPane.showMessageDialog(this, "Invalid input", "Invalid input", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		MarketImpl m = m();
		if (m == null) return;
		StockImpl s = m.c(input); if (s == null) return;

		long lastTime = ((Date)txtLastDate.getValue()).getTime();
		Price price = m.newPrice(
				((Number)txtPrice.getValue()).doubleValue(), 
				((Number) txtLowPrice.getValue()).doubleValue(),
				((Number) txtHighPrice.getValue()).doubleValue(),
				lastTime);
		
		if (!s.setPrice(price)) return;

		m.applyPlaced();
		
		output = input;
		
		JOptionPane.showMessageDialog(this, "Add price successfully", "Add price", JOptionPane.INFORMATION_MESSAGE);
		dispose();
	}
	
	
	public Stock getOutput() {
		return output;
	}
	

}



class PriceListTable extends JTable {

	
	private static final long serialVersionUID = 1L;
	

//	private DateCellRenderer dateCellRenderer = new DateCellRenderer();
//	private DateCellEditor dateCellEditor = new DateCellEditor();
//	protected ReadOnlyCellRenderer readOnlyCellRenderer = new ReadOnlyCellRenderer();

	
	protected DateCellRenderer dateCellRenderer = new DateCellRenderer();
	
	
	public PriceListTable(Market market, Stock stock, long timeInterval) {
		super();
		setModel(new PriceListTableModel(stock, timeInterval) {

			private static final long serialVersionUID = 1L;

			@Override
			protected MarketImpl m() {
				Universe u = market.getNearestUniverse();
				return u != null ? u.c(market) : null;
			}
			
		});

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(SwingUtilities.isRightMouseButton(e) ) {
					JPopupMenu contextMenu = createContextMenu();
					if(contextMenu != null)
						contextMenu.show((Component)e.getSource(), e.getX(), e.getY());
				}
				else if (e.getClickCount() >= 2) {
					if (getSelectedColumn() == 1) edit(null);
				}
			}
		});

		
		setDefaultRenderer(Date.class, dateCellRenderer);
//		setDefaultEditor(Date.class, dateCellEditor);

		update();
	}


	private JPopupMenu createContextMenu() {
		JPopupMenu ctxMenu = new JPopupMenu();
		Price selectedPrice = getSelectedPrice();
		if (selectedPrice == null) return null;
		
		JMenuItem miEdit = new JMenuItem("Edit");
		miEdit.addActionListener( 
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					edit(selectedPrice);
				}
			});
		ctxMenu.add(miEdit);

		if (!getModel2().isSelectAsTakenPrice(selectedPrice)) {
			JMenuItem miDelete = new JMenuItem("Delete");
			miDelete.addActionListener( 
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						int selectedRow = getSelectedRow();
						if (selectedRow >= 0) getModel2().removeRow(selectedRow);
					}
				});
			ctxMenu.add(miDelete);
		}
		
		return ctxMenu;
	}

	
	private void edit(Price input) {
		input = input != null ? input : getSelectedPrice();
		if (input == null) return;
		JDialog editor = new JDialog(Util.getFrameForComponent(this), "Edit price", true);
		
		editor.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		editor.setSize(350, 250);
		editor.setLocationRelativeTo(Util.getFrameForComponent(this));
		editor.setLayout(new BorderLayout());
		
		JPanel header = new JPanel(new BorderLayout());
		editor.add(header, BorderLayout.NORTH);
		
		JPanel left = new JPanel(new GridLayout(0, 1));
		header.add(left, BorderLayout.WEST);
		
		left.add(new JLabel("Price (*): "));
		left.add(new JLabel("Low price (*): "));
		left.add(new JLabel("High price (*): "));
		left.add(new JLabel("Last date: "));

		JPanel right = new JPanel(new GridLayout(0, 1));
		header.add(right, BorderLayout.CENTER);
		
		JPanel panePrice = new JPanel(new BorderLayout());
		right.add(panePrice);
		JFormattedTextField txtPrice = new JFormattedTextField(Util.getNumberFormatter());
		txtPrice.setValue(input.get());
		panePrice.add(txtPrice, BorderLayout.CENTER);
		//
		JButton btnPrice = new JButton("Estimate");
		btnPrice.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Estimator estimator = getEstimator();
				if (estimator != null)  txtPrice.setValue(estimator.estimatePrice(getModel2().m().getTimeViewInterval()));
			}
		});
		panePrice.add(btnPrice, BorderLayout.EAST);
		
		JPanel paneLowPrice = new JPanel(new BorderLayout());
		right.add(paneLowPrice);
		JFormattedTextField txtLowPrice = new JFormattedTextField(Util.getNumberFormatter());
		txtLowPrice.setValue(input.getLow());
		paneLowPrice.add(txtLowPrice, BorderLayout.CENTER);
		//
		JButton btnLowPrice = new JButton("Estimate");
		btnLowPrice.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Estimator estimator = getEstimator();
				if (estimator != null)  txtLowPrice.setValue(estimator.estimateLowPrice(getModel2().m().getTimeViewInterval()));
			}
		});
		paneLowPrice.add(btnLowPrice, BorderLayout.EAST);
		
		JPanel paneHighPrice = new JPanel(new BorderLayout());
		right.add(paneHighPrice);
		JFormattedTextField txtHighPrice = new JFormattedTextField(Util.getNumberFormatter());
		txtHighPrice.setValue(input.getHigh());
		paneHighPrice.add(txtHighPrice, BorderLayout.CENTER);
		//
		JButton btnHighPrice = new JButton("Estimate");
		btnHighPrice.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Estimator estimator = getEstimator();
				if (estimator != null)  txtHighPrice.setValue(estimator.estimateHighPrice(getModel2().m().getTimeViewInterval()));
			}
		});
		paneHighPrice.add(btnHighPrice, BorderLayout.EAST);
		
		JPanel paneLastDate = new JPanel(new BorderLayout());
		right.add(paneLastDate);
		JFormattedTextField txtLastDate = new JFormattedTextField(Util.getDateFormatter());
		txtLastDate.setValue(new Date(input.getTime() + StockProperty.TIME_UPDATE_PRICE_INTERVAL));
		paneLastDate.add(txtLastDate, BorderLayout.CENTER);
		//
		JPanel paneLastDate2 = new JPanel(new GridLayout(1, 0));
		paneLastDate.add(paneLastDate2, BorderLayout.EAST);
		JButton btnLastDateNow = new JButton("Now");
		btnLastDateNow.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				txtLastDate.setValue(new Date());
			}
		});
		btnLastDateNow.setEnabled(true);
		paneLastDate2.add(btnLastDateNow);
		
		
		JPanel footer = new JPanel();
		editor.add(footer, BorderLayout.SOUTH);
		
		JButton ok = new JButton("OK");
		ok.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean check = true;
				double price = txtPrice.getValue() instanceof Number ? ((Number)txtPrice.getValue()).doubleValue() : 0;
				if (price < 0) check = check && false;

				double lowPrice = txtLowPrice.getValue() instanceof Number ? ((Number)txtLowPrice.getValue()).doubleValue() : 0;
				if (lowPrice < 0) check = check && false;

				double highPrice = txtHighPrice.getValue() instanceof Number ? ((Number)txtHighPrice.getValue()).doubleValue() : 0;
				if (highPrice < 0) check = check && false;
				
				if (price < lowPrice || price > highPrice) check = check && false;
				
				Date lastDate = txtLastDate.getValue() instanceof Date ? (Date)txtLastDate.getValue() : null;
				StockAbstract s = getModel2().getStock();
				if (lastDate == null || s == null /*|| !s.checkPriceTimePoint(lastDate.getTime())*/)
					check = check && false;
				
				if (!check) {
					JOptionPane.showMessageDialog(editor, "Invalid input", "Invalid input", JOptionPane.ERROR_MESSAGE);
					editor.dispose();
					return;
				}
				
				Price newPrice = getModel2().m().newPrice(price, lowPrice, highPrice, lastDate.getTime());
				int selectedRow = getSelectedRow();
				if (newPrice != null && selectedRow >= 0) {
					getModel2().setValueAt(newPrice, selectedRow);
				}

				editor.dispose();
			}
		});
		footer.add(ok);
		
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				editor.dispose();
			}
		});
		footer.add(cancel);
		
		editor.setVisible(true);
	}
	
	
	private Estimator getEstimator() {
		MarketImpl m = getModel2().m();
		if (m == null) return null;
		StockAbstract s = getModel2().getStock();
		return s!= null ? m.getEstimator(s.code(), s.isBuy()) : null;
	}

	
	public void update() {
		getModel2().update();
		
		if (getColumnModel().getColumnCount() > 0) {
			getColumnModel().getColumn(0).setMaxWidth(0);
			getColumnModel().getColumn(0).setMinWidth(0);
			getColumnModel().getColumn(0).setPreferredWidth(0);
		}
	}
	
	
	public void apply() {
		getModel2().apply();
	}
	
	
	public PriceListTableModel getModel2() {
		return (PriceListTableModel)getModel();
	}
	
	
	public void setEditable(boolean editable) {
		getModel2().editable = editable;
	}
	
	
	public boolean isEditable() {
		return getModel2().editable;
	}
	
	
	protected long getTimeInterval() {
		return getModel2().timeInterval;
	}
	
	
	protected List<TakenPrice> getTakenPrices() {
		return getModel2().getTakenPrices();
	}

	
	public Price getSelectedPrice() {
		int row = getSelectedRow();
		return row >= 0 ? getModel2().getPriceAt(row) : null;
	}
	
	
	public boolean isModified() {
		return getModel2().isModified();
	}
	
	
	@Override
	public TableCellRenderer getCellRenderer(int row, int column) {
		TableCellRenderer renderer = null;
		Object value = getValueAt(row, column);
		if (value == null)
			renderer = super.getCellRenderer(row, column);
		else {
			renderer = getDefaultRenderer(value.getClass());
			if(renderer == null) renderer = super.getCellRenderer(row, column);
		}
		
		try {
			if (value != null && renderer instanceof DefaultTableCellRenderer) {
				Price price = getModel2().getPriceAt(row);
				if (price != null && getModel2().isSelectAsTakenPrice(price)) {
					StockImpl stock = getModel2().c(getModel2().stock);
					Price takenPrice = stock != null ? stock.getTakenPrice(getTimeInterval()) : null;
					boolean selected = takenPrice instanceof TakenPrice ? (((TakenPrice)takenPrice).getPrice() == price) : false;
					if (selected)
						((DefaultTableCellRenderer)renderer).setBackground(new Color(128, 128, 128));
					else
						((DefaultTableCellRenderer)renderer).setBackground(new Color(200, 200, 200));
				}
				else
					((DefaultTableCellRenderer)renderer).setBackground(null);
			}
			else if (renderer instanceof DefaultTableCellRenderer)
				((DefaultTableCellRenderer)renderer).setBackground(null);
		}
		catch (Exception e) {}
		
		return renderer;
	}

	
//	@Override
//	public TableCellRenderer getCellRenderer(int row, int column) {
//		TableCellRenderer renderer = getDefaultRenderer(getModel2().getColumnClass(row, column));
//		if(renderer == null) renderer = super.getCellRenderer(row, column);
//		return renderer;
//	}

	
//	private TableCellRenderer getCellRenderer0(int row, int column) {
//		Object value = getValueAt(row, column);
//		if (value == null) return null;
//		
//		Price price = getModel2().getPriceAt(row);
//		return price != null && getModel2().isSelectAsTakenPrice(price) ? readOnlyCellRenderer : null;
//	}

	
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

	
//	@Override
//    public TableCellEditor getCellEditor(int row, int column) {
//    	TableCellEditor editor = getDefaultEditor(getModel2().getColumnClass(row, column));
//    	if(editor == null) editor = super.getCellEditor(row, column);
//    	return editor;
//    }

	
//	/**
//	 * This class represents read-only cell renderer.
//	 * @author Loc Nguyen
//	 * @version 1.0
//	 */
//	protected class ReadOnlyCellRenderer extends DefaultTableCellRenderer.UIResource {
//
//		/**
//		 * Serial version UID for serializable class.
//		 */
//		private static final long serialVersionUID = 1L;
//
//		@Override
//		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
//				int row, int column) {
//			Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
//			comp.setBackground(new Color(200, 200, 200));
//			
//			return comp;
//			
//		}
//		
//	}

	
	protected static class DateCellRenderer extends DefaultTableCellRenderer.UIResource {

		private static final long serialVersionUID = 1L;

		public void setValue(Object value) {
        	if ((value == null) || !(value instanceof Date))
        		setText("");
        	else
        		setText(Util.format((Date)value));
        }
    }

	
}



abstract class PriceListTableModel extends DefaultTableModel implements TableModelListener {

	
	private static final long serialVersionUID = 1L;
	
	
	protected Stock stock = null;
	
	
	protected StockGroup group = null;
	
	
	protected long timeInterval = 0;
	
	
	protected boolean modified = false;
	
	
	protected boolean modifiedDate = false;
	
	
	protected boolean editable = true;
	
	
	public PriceListTableModel(Stock stock, long timeInterval) {
		this.stock = stock;
		this.timeInterval = timeInterval;
		
		if (stock instanceof StockGroup)
			group = (StockGroup)stock;
		else {
			MarketImpl m = m();
			if (m != null)  group = m.get(stock.code(), stock.isBuy());
		}
		
		addTableModelListener(this);
	}
	
	
	protected List<TakenPrice> getTakenPrices() {
		List<TakenPrice> takenPrices = Util.newList(0);
		if (group == null) {
			StockImpl s = c(stock);
			if (s == null) return takenPrices;
			Price price = s.getTakenPrice(timeInterval);
			if (price != null && price instanceof TakenPrice) takenPrices.add((TakenPrice)price);
		}
		else {
			List<Stock> stocks = group.getStocks(timeInterval);
			for (Stock stock : stocks) {
				StockImpl s = c(stock);
				if (s == null) continue;
				Price price = s.getTakenPrice(timeInterval);
				if (price != null && price instanceof TakenPrice)
					takenPrices.add((TakenPrice)price);
			}
		}
		
		return takenPrices;
	}
	
	
	protected boolean isSelectAsTakenPrice(Price price) {
		if (price == null) return false;
		List<TakenPrice> takenPrices = getTakenPrices();
		if (takenPrices.size() == 0) return false;
		
		for (TakenPrice takenPrice : takenPrices) {
			if (takenPrice.checkRefEquals(price)) return true;
		}
		
		return false;
	}
	
	
	public void apply() {
		if (!modified) return;
		
		List<Price> stockPrices = getStockPrices();
		List<Price> prices = Util.newList(0);
		for (Price stockPrice : stockPrices) {
			int row = rowOf(stockPrice);
			Price price = rowPriceOf(stockPrice);
			if (row < 0 && !isSelectAsTakenPrice(stockPrice)) continue;
			
			prices.add(stockPrice);
			if (price != null && price.isValid()) stockPrice.copy(price);
		}
		
		Collections.sort(prices, new Comparator<Price>() {

			@Override
			public int compare(Price o1, Price o2) {
				if (o1.getTime() < o2.getTime())
					return -1;
				else if (o1.getTime() == o2.getTime())
					return 0;
				else
					return 1;
			}
			
		});
		
		
		List<Price> internalPrices = getInternalPrices();
		if (internalPrices != null) {
			internalPrices.clear();
			internalPrices.addAll(prices);
		}
		
		modified = false;
		boolean flag = modifiedDate;
		modifiedDate = false;

		MarketImpl m = m();
		if (m == null) return;
		m.applyPlaced();
		
		if (flag) {
			MarketImpl placedMarket = m.getPlacedMarket();
			if (placedMarket != null && placedMarket != m) {
				StockGroup group = placedMarket.get(getGroup().code(), getGroup().isBuy());
				if (group != null) group.resortPrices();
			}
		}
		
	}
	
	
	private List<Price> getInternalPrices() {
		Stock s = group != null ? group : stock;
		return s != null ? s.getPrices(0) : null;
	}
	
	
	public void update() {
		Vector<Vector<Object>> data = Util.newVector(0);
		List<Price> prices = stock.getPrices(timeInterval);
		for (Price price : prices) {
			Vector<Object> row = toRow(price);
			data.add(row);
		}
		
		setDataVector(data, toColumns());

		modified = false;
		modifiedDate = false;
	}
	
	
	protected abstract MarketImpl m();
	
	
	protected StockAbstract getStock() {
		if (group != null)
			return group;
		else
			return stock != null ? c(stock) : null;
	}
	
	
	protected StockGroup getGroup() {
		return group;
	}
	
	
	protected StockImpl c(Stock stock) {
		MarketImpl m = m();
		return m != null ? m.c(stock) : null;
	}
	
	
	protected Price getPriceAt(int row) {
		return (Price)getValueAt(row, 0);
	}
	
	
	protected List<Price> getStockPrices() {
		if (group != null)
			return group.getPrices(timeInterval);
		else
			return stock.getPrices(timeInterval);
	}
	
	
	protected List<Price> getTablePrices() {
		List<Price> prices = Util.newList(0);
		int n = getRowCount();
		for (int i = 0; i < n; i++) {
			Price price = getPriceAt(i);
			if (price != null) prices.add(price);
		}
		
		return prices;
	}
	
	
	protected void setValueAt(Price price, int row) {
		if (price == null || row < 0 | row > getRowCount()) return;
		setValueAt(price.getDate(), row, 1);
		setValueAt(price.get(), row, 2);
		setValueAt(price.getLow(), row, 3);
		setValueAt(price.getHigh(), row, 4);
	}
	
	
	@Override
	public void setValueAt(Object aValue, int row, int column) {
		if (column == 0) {
			
		}
		else if (column == 1) {
			if (aValue == null)
				aValue = 0;
			else if (!(aValue instanceof Date)) {
				try {
					SimpleDateFormat df = new SimpleDateFormat(Util.DATE_FORMAT);
					aValue = df.parse(aValue.toString());
				}
				catch (Exception e) {
					aValue = new Date();
				}
			}
		}
		else if (aValue == null)
			aValue = Double.valueOf(0);
		else if (!(aValue instanceof Number)) {
			try {
				aValue = Double.parseDouble(aValue.toString());
			}
			catch (Exception e) {
				aValue = Double.valueOf(0);
			}
		}
			
		super.setValueAt(aValue, row, column);
		modified = true;
		if (column == 1) {
			modifiedDate = true;
		}
	}

	
	@Override
	public boolean isCellEditable(int row, int column) {
		if (editable && column != 0 && column != 1)
			return super.isCellEditable(row, column);
		else
			return false;
	}


	public boolean isModified() {
		return modified;
	}

	
	private int rowOf(Price price) {
		for (int i = 0; i < getRowCount(); i++) {
			Price p = getPriceAt(i);
			if (p == price) return i;
		}
		
		return -1;
	}
	
	
	private Price rowPriceOf(Price stockPrice) {
		if (stockPrice == null) return null;
		int row = rowOf(stockPrice);
		return row >= 0 ? getRowPriceAt(row) : null;
	}
	
	
	private Price getRowPriceAt(int row) {
		Object date = getValueAt(row, 1);
		Object price = getValueAt(row, 2);
		Object lowPrice = getValueAt(row, 3);
		Object highPrice = getValueAt(row, 4);
		if (date != null && date instanceof Date &&
			price != null && price instanceof Number &&
			lowPrice != null && lowPrice instanceof Number &&
			highPrice != null && highPrice instanceof Number)
			return m().newPrice(((Number)price).doubleValue(), 
					((Number)lowPrice).doubleValue(), 
					((Number)highPrice).doubleValue(),
					((Date)date).getTime());
		else
			return null;
	}
	
	
//	/**
//	 * Getting the class of value at specified row and specified column.
//	 * @param row specified row.
//	 * @param column specified column.
//	 * @return class of value at specified row and specified column.
//	 */
//	public Class<?> getColumnClass(int row, int column) {
//		Object value = getValueAt(row, column);
//		if (value == null)
//			return getColumnClass(column);
//		else
//			return value.getClass();
//	}

	
	@Override
	public void tableChanged(TableModelEvent e) {
		modified = true;
		//modifiedDate = true;
	}


	private Vector<Object> toRow(Price price) {
		Vector<Object> row = Util.newVector(0);
		
		row.add(price);
		row.add(price.getDate());
		row.add(price.get());
		row.add(price.getLow());
		row.add(price.getHigh());
		
		return row;
	}
	
	
	/**
	 * Getting list of column names.
	 * @return list of column names.
	 */
	private Vector<String> toColumns() {
		Vector<String> columns = Util.newVector(0);
		columns.add("");
		columns.add("Date");
		columns.add("Price");
		columns.add("Low price");
		columns.add("High price");
		
		return columns;
	}
	

}



class PriceList extends JDialog {


	private static final long serialVersionUID = 1L;


	protected PriceListTable tblPriceList = null;
	
	
	protected Price output = null;
	
	
	protected boolean pressOK = false;
	
	
	protected boolean editMode = false;
	
	
	protected boolean selectMode = false;
	
	
	protected Price selectPrice = null;

	
	public PriceList(Market market, Stock stock, long timeInterval, boolean editMode, boolean selectMode, Component parent) {
		super(Util.getFrameForComponent(parent), "Price list", true);
		this.tblPriceList = new PriceListTable(market, stock, timeInterval);
		this.editMode = editMode;
		this.selectMode = selectMode;
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(400, 300);
		setLocationRelativeTo(Util.getFrameForComponent(parent));
		setLayout(new BorderLayout());
		
		
		JPanel header = new JPanel();
		add(header, BorderLayout.NORTH);
		
		JLabel info = new JLabel(stock.code() + " in " + (stock.isBuy() ? "Buy" : "Sell"));
		header.add(info);
		
		
		JPanel body = new JPanel(new BorderLayout());
		add(body, BorderLayout.CENTER);
		
		tblPriceList.setEditable(editMode);
		body.add(new JScrollPane(tblPriceList), BorderLayout.CENTER);
		
		
		JPanel footer = new JPanel();
		add(footer, BorderLayout.SOUTH);
		
		JButton ok = new JButton(editMode ? (selectMode ? "Apply and select" : "Apply") : (selectMode ? "Select" : "Close"));
		ok.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ok();
			}
		});
		footer.add(ok);
		
		JButton refresh = new JButton("Refresh");
		refresh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tblPriceList.update();
			}
		});
		if(editMode) footer.add(refresh);

		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		if (editMode || selectMode) footer.add(cancel);
	}
	
	
	private void ok() {
		if (editMode) {
			tblPriceList.apply();
		}
		
		if (selectMode) {
			output = tblPriceList.getSelectedPrice();
		}
		
		pressOK = true;
		
		dispose();
	}
	
	
	public boolean isPressOK() {
		return pressOK;
	}
	
	
	@Override
	public void dispose() {
		if (editMode) {
			
		}
		
		if (selectMode) {
			
		}
		
		super.dispose();
	}
	
	
	public Price getOutput() {
		return output;
	}
	
	
}
