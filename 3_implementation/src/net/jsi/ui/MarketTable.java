/**
 * JSI: JAGGED STRATEGY INVESTMENT 
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: jsi.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.jsi.ui;

import java.awt.BorderLayout;
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
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.EventListenerList;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
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
import net.jsi.StockGroup;
import net.jsi.StockImpl;
import net.jsi.StockProperty;
import net.jsi.Universe;
import net.jsi.Util;

public class MarketTable extends JTable implements MarketListener {

	
	private static final long serialVersionUID = 1L;

	
	public MarketTable(Market market, boolean forStock, MarketListener listener) {
		super();
		setModel(createModel(market, forStock));

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

	
	protected MarketTableModel createModel(Market market, boolean forStock) {
		return new MarketTableModel(market, forStock);
	}
	
	
	protected void take(Stock stock, boolean update) {
		stock = stock != null ? stock : getSelectedStock();
		StockTaker taker = new StockTaker(getMarket(), stock, update, this);
		taker.setVisible(true);
		if (taker.getOutput() != null) update();
	}
	
	
	protected void view(Stock stock) {
		if (getModel2().isForStock())
			take(stock, true);
		else {
			stock = stock != null ? stock : getSelectedStock();
			if (stock == null) return;
			new StockSummary(getMarket(), stock.code(), stock.isBuy(), null, this) {
				private static final long serialVersionUID = 1L;

				@Override
				protected List<EstimateStock> getEstimateStocks() {
					return Util.newList(0);
				}
			
			}.setVisible(true);
		}
	}
	
	
	protected void summary(Stock stock) {
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
	
	
	protected void commit(Stock stock) {
		stock = stock != null ? stock : getSelectedStock();
		if (stock == null) return;
		stock.setCommitted(!stock.isCommitted());
		update();
	}
	
	
	private boolean delete0(Stock stock) {
		if (stock == null) return false;
		MarketImpl m = m(); if (m == null) return false;
		
		if (getModel2().isForStock()) {
			StockGroup group = m.get(stock.code(), stock.isBuy());
			if (group == null) return false;
			group.remove(stock);
			if (group.size() == 0) m.remove(stock.code(), stock.isBuy());
			
			return true;
		}
		else {
			return m.remove(stock.code(), stock.isBuy()) != null;
		}
	}

	
	protected void deleteSelected() {
		List<Stock> stocks = getSelectedStocks();
		boolean ret = false;
		for (Stock stock : stocks) {
			if (stock == null) continue;
			boolean ret0 = delete0(stock);
			ret = ret || ret0;
		}
		
		if (ret) update();
	}
	
	
	protected void delete() {
		int answer= JOptionPane.showConfirmDialog(this, "Are you sure to delete the stock (s)", "Removal confirmation", JOptionPane.YES_NO_OPTION);
		if (answer != JOptionPane.YES_OPTION) return;
		deleteSelected();
	}
	
	
	protected Stock addPrice(Stock stock) {
		AddPrice addPrice = new AddPrice(getMarket(), stock, this);
		addPrice.setVisible(true);
		Stock output = addPrice.getOutput();
		if (output != null) update();
		
		return output;
	}
	
	
	protected void setTakenPrice(Stock stock) {
		stock = stock != null ? stock : getSelectedStock(); if (stock == null) return;
		
		PriceListPartial pl = new PriceListPartial(getMarket(), stock, getMarket().getTimeViewInterval(), false, true, this);
		pl.setVisible(true);
		Price selectedPrice = pl.getOutput();
		if (selectedPrice == null) return;
		
		StockImpl s = c(stock);
		if (s != null) {
			s.take(getMarket().getTimeViewInterval(), selectedPrice.getTime());
			update();
		}
	}
	
	
	protected void priceList(Stock stock) {
		stock = stock != null ? stock : getSelectedStock();
		if (stock == null) return;
		
		PriceListPartial pl = new PriceListPartial(getMarket(), stock, getMarket().getTimeViewInterval(), true, false, this);
		pl.setVisible(true);
		if (pl.isPressOK()) update();
	}
	
	
	protected void settings(Stock stock) {
		stock = stock != null ? stock : getSelectedStock();
		if (stock == null) return;
		
		StockGroup group0 = null;
		if (stock instanceof StockGroup)
			group0 = (StockGroup)stock;
		else {
			StockImpl s = c(stock);
			group0 = s != null ? s.getGroup() : null;
		}
		if (group0 == null) return;
		
		StockGroup group = group0;

		JDialog dlgSettings = new JDialog(Util.getDialogForComponent(this), "Settings for stock group", true);
		dlgSettings.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dlgSettings.setSize(300, 200);
		dlgSettings.setLocationRelativeTo(Util.getDialogForComponent(this));
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
				
				group.setLeverage(leverage != 0 ? 1/leverage : leverage);
				group.setUnitBias(unitBias);
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
	
				JMenuItem miAddPrice = new JMenuItem("Add price");
				miAddPrice.addActionListener( 
					new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							addPrice(stock);
						}
					});
				ctxMenu.add(miAddPrice);

				JMenuItem miDelete = new JMenuItem("Delete");
				miDelete.addActionListener( 
					new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							delete();
						}
					});
				ctxMenu.add(miDelete);
	
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

			JMenuItem miCommit = new JMenuItem(stock.isCommitted() ? "Uncommit" : "Commit");
			miCommit.addActionListener( 
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						commit(stock);
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
		else
			ctxMenu.addSeparator();
		
		JMenuItem miSummary = new JMenuItem("Market summary");
		miSummary.addActionListener( 
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					MarketSummary ms = new MarketSummary(getMarket(), StockProperty.RUNTIME_CASCADE ? tblMarket : null, tblMarket);
					ms.setVisible(true);
					
					if (!StockProperty.RUNTIME_CASCADE) tblMarket.update();
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

	
	public MarketTableModel getModel2() {
		return (MarketTableModel) getModel();
	}
	
	
	protected Market getMarket() {
		return getModel2().getMarket();
	}
	
	
	protected Market getWatchMarket() {
		MarketImpl m = m();
		return m != null ? m.getWatchMarket() : null;
	}
	
	
	protected Market getPlaceMarket() {
		MarketImpl m = m();
		return m != null ? m.getPlaceMarket() : null;
	}
	
	
	protected Market getTrashMarket() {
		MarketImpl m = m();
		return m != null ? m.getTrashMarket() : null;
	}
	
	
	public void update() {
		getModel2().update();
		
		int lastColumn = getColumnCount() - 1;
		if (getModel2().isForStock() && lastColumn > 0) {
			getColumnModel().getColumn(lastColumn).setMaxWidth(0);
			getColumnModel().getColumn(lastColumn).setMinWidth(0);
			getColumnModel().getColumn(lastColumn).setPreferredWidth(0);
		}
	}
	
	
	public void resetAllStopLossTakeProfits() {
		getModel2().resetAllStopLossTakeProfits();
	}

	
	public void resetAllBiases() {
		getModel2().resetAllBiases();
	}

	
	public boolean isShowCommit() {
		return getModel2().showCommit;
	}
	
	
	public void setShowCommit(boolean showCommit) {
		getModel2().showCommit = showCommit;
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
	
	
	protected MarketImpl m() {
		return getModel2().m();
	}


	protected StockImpl c(Stock stock) {
		MarketImpl m = m();
		return m != null ? m.c(stock) : null;
	}
	
	
	public boolean applyPlace() {
		return getModel2().applyPlace();
	}
	
	
}



class MarketTableModel extends DefaultTableModel implements MarketListener, TableModelListener {

	
	private static final long serialVersionUID = 1L;

	
	protected Market market = null;
	
	
	protected boolean forStock = true;
	
	
	protected Map<String, List<EstimateStock>> estimators = Util.newMap(0);
	
	
    protected EventListenerList listenerList = new EventListenerList();
    
    
    protected boolean showCommit = false;

    
    public MarketTableModel(Market market, boolean forStock) {
		this.market = market;
		this.forStock = forStock;
		
		this.addTableModelListener(this);
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
	
	
	protected EstimateStock getEstimateStock(int row) {
		if (!isForStock()) return null;
		Object v = getValueAt(row, getColumnCount() - 1);
		if (v != null && v instanceof EstimateStock)
			return (EstimateStock)v;
		else
			return null;
	}
	

	protected Stock getStock(int row) {
		return (Stock)getValueAt(row, 0);
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
				if (query == null) query = m;
				for (int i = 0; i < m.size(); i++) {
					StockGroup group = m.get(i);
					if (group.isCommitted() && !showCommit) continue;
					Estimator estimator = query.getEstimator(group.code(), group.isBuy());
					List<EstimateStock> estimateStocks = estimator.estimateStocks(group.getStocks(group.getTimeViewInterval()), group.getTimeViewInterval());
					estimators.put(group.code(), estimateStocks);
				}
			}
			
			List<Stock> stocks = market.getStocks(market.getTimeViewInterval());
			for (Stock stock : stocks) {
				if (stock.isCommitted() && !showCommit) continue;
				Vector<Object> row = toRow(stock);
				data.add(row);
			}
		}
		else {
			MarketImpl m = m();
			if (m != null) {
				List<StockGroup> groups = m.getGroups(market.getTimeViewInterval());
				for (StockGroup group : groups) {
					if (group.isCommitted() && !showCommit) continue;
					Vector<Object> row = toRow(group);
					if (row != null) data.add(row);
				}
			}

		}
		
		setDataVector(data, toColumns());
		
		fireMarketEvent(new MarketEvent(this));
	}
	
	
	protected void resetAllStopLossTakeProfits() {
		if (!isForStock()) return;
		for (int row = 0; row < getRowCount(); row++) {
			StockImpl stock = m().c(getStock(row));
			EstimateStock es = getEstimateStock(row);
			if (stock == null || es == null) continue;
			
			stock.setStopLoss(es.estimatedStopLoss);
			stock.setTakeProfit(es.estimatedTakeProfit);
			
			String value = Util.format(es.estimatedStopLoss) + " / " + Util.format(es.estimatedTakeProfit);
			setValueAt(value, row, 7);
		}
	}
	
	
	protected void resetAllBiases() {
		if (!isForStock()) return;
		for (int row = 0; row < getRowCount(); row++) {
			Stock stock = getStock(row);
			EstimateStock es = getEstimateStock(row);
			if (stock != null && es != null) stock.setUnitBias(es.estimatedBias);
		}
	}
	
	
	protected boolean applyPlace() {
		MarketImpl m = m();
		if (m != null) {
			boolean ret = m.applyPlace();
			update();
			return ret;
		}
		else
			return false;
	}
	
	
	@Override
	public void setValueAt(Object aValue, int row, int column) {
		super.setValueAt(aValue, row, column);
	}

	
	protected Vector<Object> toRow(Stock stock) {
		long timeViewInterval = market.getTimeViewInterval();
		Vector<Object> row = Util.newVector(0);
		Universe u = market.getNearestUniverse();
		
		if (forStock) { 
			StockImpl s = market.c(stock);
			if (s == null || !s.isValid(timeViewInterval)) return null;

			row.add(stock);
			row.add(stock.isBuy());
			row.add(Util.format(s.getTakenPrice(timeViewInterval).getDate()));
			row.add(s.getVolume(timeViewInterval, true));
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
			row.add(found);
				
		}
		else {
			StockGroup group = (StockGroup)stock;
			
			row.add(group);
			row.add(group.isBuy());
			row.add(group.getLeverage() != 0 ? 1.0 / group.getLeverage() : "Infinity");
			row.add(group.getVolume(timeViewInterval, false));
			row.add(group.getTakenValue(timeViewInterval));
			row.add(group.getMargin(timeViewInterval));
			row.add(group.getProfit(timeViewInterval));
			row.add(Util.format(group.getROIByLeverage(timeViewInterval) * 100) + "%");
			
			if (u == null || u.lookup(market.getName()) < 0) {
				row.add("");
			}
			else {
				QueryEstimator query = u.query(market.getName(), market);
				if (query == null) query = m();
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
	protected Vector<String> toColumns() {
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
			columns.add("");
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


	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}

	
	@Override
	public void tableChanged(TableModelEvent e) {

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

    
    protected void fireMarketEvent(MarketEvent evt) {
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



class AddPrice extends JDialog {


	private static final long serialVersionUID = 1L;


	protected JFormattedTextField txtPrice;
	
	
	protected JButton btnPrice;

	
	protected JFormattedTextField txtLowPrice;
	
	
	protected JButton btnLowPrice;
	
	
	protected JFormattedTextField txtHighPrice;
	
	
	protected JButton btnHighPrice;
	
	
	protected JFormattedTextField txtAltPrice;
	
	
	protected JButton btnAltPrice;
	
	
	protected JFormattedTextField txtLastDate;
	
	
	protected JButton btnLastDateNow;

	
	protected JButton btnLastDateList;
			
	
	protected Market market = null;
	
	
	protected Stock input = null;

	
	protected Stock output = null;

	
	public AddPrice(Market market, Stock input, Component parent) {
		super(Util.getDialogForComponent(parent), "Add price", true);
		this.market = market;
		this.input = input;
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(350, 250);
		setLocationRelativeTo(Util.getDialogForComponent(parent));
		setLayout(new BorderLayout());
		
		JPanel header = new JPanel(new BorderLayout());
		add(header, BorderLayout.NORTH);
		
		JPanel left = new JPanel(new GridLayout(0, 1));
		header.add(left, BorderLayout.WEST);
		
		left.add(new JLabel("Price (*): "));
		left.add(new JLabel("Low price (*): "));
		left.add(new JLabel("High price (*): "));
		//left.add(new JLabel("Alt price: "));
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
		
		JPanel paneAltPrice = new JPanel(new BorderLayout());
		//right.add(paneAltPrice);
		txtAltPrice = new JFormattedTextField(Util.getNumberFormatter());
		txtAltPrice.setValue(input.getPrice().getAlt());
		paneAltPrice.add(txtAltPrice, BorderLayout.CENTER);
		//
		btnAltPrice = new JButton("Estimate");
		btnAltPrice.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		paneAltPrice.add(btnAltPrice, BorderLayout.EAST);

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
		
		PriceListPartial pl = new PriceListPartial(market, input, market.getTimeViewInterval(), false, false, this);
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
		
		return true;
	}
	
	
	private void ok() {
		if (!validateInput()) {
			JOptionPane.showMessageDialog(this, "Invalid input", "Invalid input", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		MarketImpl m = m();
		if (m == null) return;

		long lastTime = ((Date)txtLastDate.getValue()).getTime();
		Price price = m.newPrice(
				((Number)txtPrice.getValue()).doubleValue(), 
				((Number) txtLowPrice.getValue()).doubleValue(),
				((Number) txtHighPrice.getValue()).doubleValue(),
				lastTime);
		
		if (!input.setPrice(price)) return;

		m.applyPlace();
		
		output = input;
		
		JOptionPane.showMessageDialog(this, "Add price successfully", "Add price", JOptionPane.INFORMATION_MESSAGE);
		dispose();
	}
	
	
	public Stock getOutput() {
		return output;
	}
	

}



