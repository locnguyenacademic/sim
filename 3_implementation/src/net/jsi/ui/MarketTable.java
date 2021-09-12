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
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.EventListenerList;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileFilter;
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
	
	
	protected void commit() {
		List<Stock> stocks = getSelectedStocks();
		boolean ret = false;
		for (Stock stock : stocks) {
			if (stock == null) continue;
			boolean oldCommit = stock.isCommitted();
			stock.setCommitted(!oldCommit);;
			ret = ret || (stock.isCommitted() != oldCommit);
		}
		
		if (ret) update();
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
	
	
	protected void properties(Stock stock) {
		StockPropertySetting setting = new StockPropertySetting(stock.getProperty(), this);
		setting.setVisible(true);
		StockProperty output = setting.getOutput();
		if (output != null) {
			stock.getProperty().set(output);
			update();
		}
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
							properties(stock);
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
		
		long timeInterval = market.getTimeViewInterval();
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
					List<EstimateStock> estimateStocks = estimator.estimateStocks(group.getStocks(timeInterval), timeInterval);
					estimators.put(group.code(), estimateStocks);
				}
			}
			
			List<Stock> stocks = market.getStocks(timeInterval);
			for (Stock stock : stocks) {
				if (stock.isCommitted() && !showCommit) continue;
				Vector<Object> row = toRow(stock);
				data.add(row);
			}
		}
		else {
			MarketImpl m = m();
			if (m != null) {
				List<StockGroup> groups = m.getGroups(timeInterval);
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
			if (stock != null && es != null) stock.setUnitBias(es.estimatedUnitBias);
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



class MarketPanel extends JPanel implements MarketListener {


	private static final long serialVersionUID = 1L;

	
	protected JButton btnTake;
	
	
	protected JButton btnSummary;
	
	
	protected JButton btnResetLossesProfits;

	
	protected JButton btnResetUnitBiases;

	
	protected JButton btnSortCodes;

	
	protected JCheckBox chkShowCommit;
	
	
	protected JLabel lblStartTime;
			
			
	protected JLabel lblBalance;
	
	
	protected JLabel lblEquity;

	
	protected JLabel lblMargin;

	
	protected JLabel lblFreeMargin;

	
	protected JLabel lblMarginLevel;

	
	protected JLabel lblProfit;

	
	protected JLabel lblROI;

	
	protected JLabel lblBias;

	
	protected JLabel lblEstInvest;

	
	protected MarketTable tblMarket = null;
	
	
	protected File file = null;
	
	
	protected boolean forStock = true;
	
	
	public MarketPanel(Market market, boolean forStock, MarketListener listener) {
		this.forStock = forStock;
		tblMarket = createMarketTable(market, forStock, listener);
		tblMarket.getModel2().addMarketListener(this);
		setLayout(new BorderLayout());
		
		MarketPanel thisPanel = this;
		MarketImpl m = tblMarket.m();

		JPanel header = new JPanel(new BorderLayout());
		add(header, BorderLayout.NORTH);

		JPanel toolbar1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		header.add(toolbar1, BorderLayout.WEST);
		
		lblStartTime = new JLabel(Util.formatSimple(new Date(m.getTimeStartPoint())) + " -- " + Util.formatSimple(new Date()));
		toolbar1.add(lblStartTime);

		JPanel toolbar2 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		header.add(toolbar2, BorderLayout.EAST);
		
		btnTake = new JButton("Take new");
		btnTake.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Stock stock = tblMarket.getSelectedStock();
				take(stock, false);
			}
		});
		btnTake.setMnemonic('n');
		toolbar2.add(btnTake);
		
		btnSummary = new JButton("Summary");
		btnSummary.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MarketSummary ms = new MarketSummary(getMarket(), StockProperty.RUNTIME_CASCADE ? tblMarket : null, thisPanel);
				ms.setVisible(true);
				if (!StockProperty.RUNTIME_CASCADE) tblMarket.update();
			}
		});
		btnSummary.setMnemonic('s');
		toolbar2.add(btnSummary);

		
		JPanel body = new JPanel(new BorderLayout());
		add(body, BorderLayout.CENTER);
		body.add(new JScrollPane(tblMarket), BorderLayout.CENTER);
		
		JPanel paneMarket = new JPanel(new BorderLayout());
		body.add(paneMarket, BorderLayout.SOUTH);
		
		JPanel paneMarketButtons = new JPanel();
		paneMarket.add(paneMarketButtons, BorderLayout.WEST);
		
		btnResetLossesProfits = new JButton("Reset losses / profits");
		btnResetLossesProfits.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tblMarket.resetAllStopLossTakeProfits();
				tblMarket.update();
			}
		});
		btnResetLossesProfits.setMnemonic('o');
		btnResetLossesProfits.setVisible(false);
		paneMarketButtons.add(btnResetLossesProfits);

		btnResetUnitBiases = new JButton("Reset unit biases");
		btnResetUnitBiases.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tblMarket.resetAllBiases();
				tblMarket.update();
			}
		});
		btnResetUnitBiases.setMnemonic('b');
		btnResetUnitBiases.setVisible(false);
		paneMarketButtons.add(btnResetUnitBiases);

		btnSortCodes = new JButton("Sort codes");
		btnSortCodes.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tblMarket.m().sortByCode();
				tblMarket.update();
			}
		});
		btnSortCodes.setMnemonic('c');
		btnSortCodes.setVisible(false);
		paneMarketButtons.add(btnSortCodes);

		chkShowCommit = new JCheckBox("Show/hide commit");
		chkShowCommit.setSelected(tblMarket.isShowCommit());
		chkShowCommit.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (tblMarket.isShowCommit() != chkShowCommit.isSelected()) {
					tblMarket.setShowCommit(chkShowCommit.isSelected());
					tblMarket.update();
				}
			}
		});
		paneMarket.add(chkShowCommit, BorderLayout.EAST);

		
		JPanel footer = new JPanel(new BorderLayout());
		add(footer, BorderLayout.SOUTH);
		
		JPanel footerRow1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		footer.add(footerRow1, BorderLayout.NORTH);
		
		footerRow1.add(lblBalance = new JLabel());
		footerRow1.add(new JLabel(" "));
		footerRow1.add(lblEquity = new JLabel());
		footerRow1.add(new JLabel(" "));
		footerRow1.add(lblMargin = new JLabel());
		footerRow1.add(new JLabel(" "));
		footerRow1.add(lblFreeMargin = new JLabel());
		footerRow1.add(new JLabel(" "));
		footerRow1.add(lblMarginLevel = new JLabel());

		JPanel footerRow2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		footer.add(footerRow2, BorderLayout.SOUTH);

		footerRow2.add(lblProfit = new JLabel());
		footerRow2.add(new JLabel(" "));
		footerRow2.add(lblROI = new JLabel());
		footerRow2.add(new JLabel(" "));
		footerRow2.add(lblBias = new JLabel());
		footerRow2.add(new JLabel(" "));
		footerRow2.add(lblEstInvest = new JLabel());
		
		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Component parent = thisPanel.getParent();
				if (!(parent instanceof InvestorTabbedPane)) return;
				
				if(SwingUtilities.isRightMouseButton(e) ) {
					JPopupMenu contextMenu = createContextMenu();
					if(contextMenu != null) contextMenu.show((Component)e.getSource(), e.getX(), e.getY());
				}
				else if (e.getClickCount() >= 2) {
					onDoubleClick();
				}
			}
		});
		
		update();
	}
	
	
	protected JPopupMenu createContextMenu() {
		JPopupMenu ctxMenu = new JPopupMenu();
		
		JMenuItem watchStocks = new JMenuItem("Watch stocks");
		watchStocks.addActionListener( 
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					watchStocks();
				}
			});
		ctxMenu.add(watchStocks);

		JMenuItem placeStocks = new JMenuItem("Place stocks");
		placeStocks.addActionListener( 
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					placeStocks();
				}
			});
		ctxMenu.add(placeStocks);

		return ctxMenu;
	}
	
	
	protected void onDoubleClick() {
		watchStocks();
	}
	
	
	private void watchStocks() {
		MarketWatchDialog dlgMarket = new MarketWatchDialog(tblMarket.getWatchMarket(), forStock, StockProperty.RUNTIME_CASCADE ? tblMarket : null, this);
		dlgMarket.setTitle("Watch stocks for market " + tblMarket.getMarket().getName());
		dlgMarket.setVisible(true);
		
		tblMarket.applyPlace();
	}
	
	
	private void placeStocks() {
		MarketPlaceDialog dlgMarket = new MarketPlaceDialog(tblMarket.getPlaceMarket(), forStock, StockProperty.RUNTIME_CASCADE ? tblMarket : null, this);
		dlgMarket.setTitle("Place stocks for market " + tblMarket.getMarket().getName());
		dlgMarket.setVisible(true);
		
		tblMarket.applyPlace();
	}

	
	protected Market getMarket() {
		return tblMarket.getMarket();
	}
	
	
	protected MarketTable getMarketTable() {
		return tblMarket;
	}
	
	
	protected File getWorkingDirectory() {
		return null;
	}
	
	
	protected MarketTable createMarketTable(Market market, boolean forStock, MarketListener listener) {
		MarketPanel thisPanel = this;
		return new MarketTable(market, forStock, listener) {
			private static final long serialVersionUID = 1L;

			private boolean moveStockToTrash(Stock stock) {
				if (stock == null) return false;
				MarketImpl m = m(); if (m == null) return false;
				StockImpl s = m.c(stock); if (s == null) return false;
				
				MarketImpl trashMarket = m.getTrashMarket();
				double volume = stock.getVolume(m.getTimeViewInterval(), true);
				if (trashMarket != null) {
					Stock added = trashMarket.addStock(stock.code(), stock.isBuy(), stock.getLeverage(), volume, s.getTakenTimePoint(m.getTimeViewInterval()));
					if (added == null)
						return false;
					else {
						added.setCommitted(stock.isCommitted());
						try {
							trashMarket.c(added).setStopLoss(s.getStopLoss());
							trashMarket.c(added).setTakeProfit(s.getTakeProfit());
						} catch (Exception e) {}
					}
				}

				StockGroup group = m.get(stock.code(), stock.isBuy());
				if (group == null) return false;
				group.remove(stock);
				if (group.size() == 0) m.remove(stock.code(), stock.isBuy());
				
				return true;
			}

			
			@Override
			protected void delete() {
				if (!getModel2().isForStock()) {
					super.delete();
					return;
				}
				
				int answer = JOptionPane.showConfirmDialog(thisPanel, "Would you like to move these stocks to trash?\nIf yes, they are moved to trash.\nIf no, they are deleted forever.", "Delete stocks", JOptionPane.YES_NO_OPTION);
				if (answer != JOptionPane.YES_OPTION) {
					super.deleteSelected();
					return;
				}
				
				List<Stock> stocks = getSelectedStocks();
				boolean ret = false;
				for (Stock stock : stocks) {
					if (stock == null) continue;
					boolean ret0 = moveStockToTrash(stock);
					ret = ret || ret0;
				}
				
				if (ret) update();
			}


			private boolean watch0(Stock stock) {
				if (stock == null) return false;
				MarketImpl m = m(); if (m == null) return false;
				StockImpl s = m.c(stock); if (s == null) return false;
				
				MarketImpl watchMarket = m.getWatchMarket();
				double volume = stock.getVolume(m.getTimeViewInterval(), true);
				if (watchMarket != null) {
					Stock added = watchMarket.addStock(stock.code(), stock.isBuy(), stock.getLeverage(), volume, s.getTakenTimePoint(m.getTimeViewInterval()));
					if (added == null)
						return false;
					else {
						added.setCommitted(stock.isCommitted());
						try {
							watchMarket.c(added).setStopLoss(s.getStopLoss());
							watchMarket.c(added).setTakeProfit(s.getTakeProfit());
						} catch (Exception e) {}
					}
				}

				StockGroup group = m.get(stock.code(), stock.isBuy());
				if (group == null) return false;
				group.remove(stock);
				if (group.size() == 0) m.remove(stock.code(), stock.isBuy());
				
				return true;
			}
			
			
			private void watch() {
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
							boolean ret0 = watch0(rmStock);
							ret = ret || ret0;
						}
					}
					else {
						boolean ret0 = watch0(stock);
						ret = ret || ret0;
					}
				}
				
				if (ret) update();
			}
			
			
			@Override
			protected JPopupMenu createContextMenu() {
				JPopupMenu ctxMenu = super.createContextMenu();
				Stock stock = getSelectedStock();
				if (stock == null) return ctxMenu;
				
				ctxMenu.addSeparator();
				
				JMenuItem miWatch = new JMenuItem("Watch");
				miWatch.addActionListener( 
					new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							watch();
						}
					});
				ctxMenu.add(miWatch);
				
				return ctxMenu;
			}
			
		};
	}
	
	
	protected void take(Stock input, boolean update) {
		if (update && input == null) return;
		StockTaker taker = new StockTaker(getMarket(), input, update, this);
		taker.setVisible(true);
		if (taker.getOutput() != null) tblMarket.update();
	}
	
	
	protected void update() {
		Market market = getMarket();
		MarketImpl m = tblMarket.m();

		long timeViewInterval = market.getTimeViewInterval();
		double balance = market.getBalance(timeViewInterval);
		double margin = market.getMargin(timeViewInterval);
		double freeMargin = market.getFreeMargin(timeViewInterval);
		double equity = margin + freeMargin;
		double profit = market.getProfit(timeViewInterval);
		double roi = market.getROI(timeViewInterval);
		double bias = getMarket().calcTotalBias(timeViewInterval);
		double estInvest = market.calcInvestAmount(timeViewInterval);
		
		if (m != null)
			lblStartTime.setText(Util.formatSimple(new Date(m.getTimeStartPoint())) + " -- " + Util.formatSimple(new Date()));

		lblBalance.setText("Balance: " + Util.format(balance));
		lblEquity.setText("Equity: " + Util.format(equity));
		lblMargin.setText("Margin: " + Util.format(margin));
		lblFreeMargin.setText("Free margin: " + Util.format(freeMargin));
		lblMarginLevel.setText("Margin level: " + Util.format((margin != 0 ? equity / margin : 0)*100) + "%");
		
		lblProfit.setText("Profit: " + Util.format(profit));
		lblROI.setText("ROI: " + Util.format(roi*100) + "%");
		lblBias.setText("Bias: " + Util.format(bias));
		lblEstInvest.setText("Est. invest: " + Util.format(estInvest));
	}


	@Override
	public void notify(MarketEvent evt) {
		update();
	}


	protected boolean onOpen() {
		JFileChooser fc = createFileChooser();
        if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return false;
        File file = fc.getSelectedFile();
        if (!file.exists() || file.isDirectory()) {
			JOptionPane.showMessageDialog(this, "Wrong file", "Wrong file", JOptionPane.ERROR_MESSAGE);
			return false;
        }
        
        boolean ret = open(file);
        if (ret)
            JOptionPane.showMessageDialog(this, "Success to open market \"" + getMarket().getName() + "\"", "Open market", JOptionPane.INFORMATION_MESSAGE);
		else
			JOptionPane.showMessageDialog(this, "Fail to open market \"" + getMarket().getName() + "\"", "Save market", JOptionPane.ERROR_MESSAGE);
        return ret;
	}

	
	protected boolean open(File file) {
		if (file == null || !file.exists() || file.isDirectory()) return false;
        try {
        	FileReader reader = new FileReader(file);
        	if (tblMarket.isShowCommit() != chkShowCommit.isSelected()) tblMarket.setShowCommit(chkShowCommit.isSelected());
        	boolean ret = tblMarket.open(reader);
            if (ret) this.file = file;

            reader.close();
            return ret;
        }
        catch (Exception e) {
        	e.printStackTrace();
        }
        
        return false;
	}
	
	
	protected void onSave() {
		JFileChooser fc = createFileChooser();
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        
        File file = fc.getSelectedFile();
        FileFilter filter = fc.getFileFilter();
        if (filter.getDescription().compareToIgnoreCase(StockProperty.JSI_DESC) == 0) {
        	int index = file.getName().indexOf(".");
        	if (index < 0)
        		file = new File(file.getAbsolutePath().concat("." + StockProperty.JSI_DESC));
        }

        boolean ret = save(file);
		if (ret)
            JOptionPane.showMessageDialog(this, "Success to save market \"" + getMarket().getName() + "\"", "Save market", JOptionPane.INFORMATION_MESSAGE);
		else
			JOptionPane.showMessageDialog(this, "Fail to save market \"" + getMarket().getName() + "\"", "Save market", JOptionPane.ERROR_MESSAGE);
	}
	
	
	protected boolean save(File file) {
		if (save0(file)) {
			this.file = file;
			return true;
		}
		else
			return false;
	}
	
	
	private boolean save0(File file) {
		if (file == null) return false;
        try {
        	FileWriter writer = new FileWriter(file);
        	boolean ret = tblMarket.save(writer);
        	writer.close();

            return ret;
        }
        catch (Exception e) {
        	e.printStackTrace();
        }
        
        return false;
	}
	
	
	protected File getFile() {
		return file;
	}
	
	
	protected void dispose() {
		Universe u = getMarket().getNearestUniverse();
		if (u != null) {
			MarketImpl m = u.c(getMarket());
			if (m != null) m.applyPlace();
		}
		
		String backupExt = "" + Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		if (this.file != null) {
			if (save(this.file)) {
				try {
					File backupFile = new File(file.getAbsolutePath() + "." + backupExt);
					save(backupFile);
				}
				catch (Exception e) {}
				
				return;
			}
		}
		else {
			File workingDir = getWorkingDirectory();
			if (workingDir != null && workingDir.exists() && workingDir.isDirectory()) {
				File file = new File(workingDir, getMarket().getName() + "." + StockProperty.JSI_EXT);
				if (save(file)) {
					try {
						File backupFile = new File(file.getAbsolutePath() + "." + backupExt);
						save(backupFile);
					}
					catch (Throwable e) {}
					
					return;
				}
			}
		}
		
		int ret = JOptionPane.showConfirmDialog(this, "Would you like to save market \"" + getMarket().getName() + "\"", "Save market", JOptionPane.YES_NO_OPTION);
		if (ret == JOptionPane.YES_OPTION) onSave();
	}
	
	
	private JFileChooser createFileChooser() {
		JFileChooser fc = new JFileChooser(".");
		FileFilter csv = null;
		fc.addChoosableFileFilter(csv = new FileFilter() {
			@Override
			public String getDescription() {
				return StockProperty.JSI_DESC;
			}
			
			@Override
			public boolean accept(File f) {
				try {
					if (f.isDirectory()) return true;
					String name = f.getName();
					int index = name.lastIndexOf('.');
					if (index < 0) return false;
					String ext = name.substring(index + 1);
					return ext != null && ext.compareToIgnoreCase(StockProperty.JSI_EXT) == 0;
				}
				catch (Exception e) {}
				
				return false;
			}
		});
		
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setFileFilter(csv);
		
		if (this.file != null) {
			fc.setCurrentDirectory(this.file.getParentFile());
			fc.setSelectedFile(this.file);
		}
		else {
			File curDir = new File(".");
			File workingDir = getWorkingDirectory();
			if (workingDir != null && workingDir.exists() && workingDir.isDirectory()) curDir = workingDir;

			fc.setCurrentDirectory(curDir);
			fc.setSelectedFile(new File(curDir, getMarket().getName() + "." + StockProperty.JSI_EXT));
		}

		return fc;
	}


}



class MarketDialog extends JDialog {

	
	private static final long serialVersionUID = 1L;
	
	
	protected JButton btnOK;
	
	
	protected JButton btnCancel;
	
	
	private boolean isPressOK = false;
	
	
	public MarketDialog(Market market, boolean forStock, MarketListener listener, Component parent) {
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
		setLocationRelativeTo(Util.getDialogForComponent(parent));
		
		setLayout(new BorderLayout());

		JPanel body = new JPanel(new BorderLayout());
		add(body, BorderLayout.CENTER);
		MarketPanel mp = createMarketPanel(market, forStock, listener);
		body.add(mp, BorderLayout.CENTER);
		
		JPanel footer = new JPanel();
		add(footer, BorderLayout.SOUTH);
		
		btnOK = new JButton("OK");
		btnOK.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				isPressOK = true;
				dispose();
			}
		});
		footer.add(btnOK);
		
		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		footer.add(btnCancel);
	}
	
	
	public boolean isPressOK() {
		return isPressOK;
	}
	
	
	protected MarketPanel createMarketPanel(Market market, boolean forStock, MarketListener listener) {
		MarketPanel mp = new MarketPanel(market, forStock, listener);
		if (mp.getMarketTable() != null && listener != null && StockProperty.RUNTIME_CASCADE)
			mp.getMarketTable().getModel2().addMarketListener(listener);
		return mp;
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



