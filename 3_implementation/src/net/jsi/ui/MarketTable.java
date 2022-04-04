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
import java.io.Serializable;
import java.io.Writer;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.AbstractAction;
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
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileFilter;
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
import net.jsi.StockGroup;
import net.jsi.StockImpl;
import net.jsi.StockProperty;
import net.jsi.Universe;
import net.jsi.Util;

public class MarketTable extends JTable implements MarketListener {

	
	private static final long serialVersionUID = 1L;

	
	/**
	 * Highlight cell renderer.
	 */
	private RedmarkCellRenderer redmarkCellRenderer = new RedmarkCellRenderer();

	
	public MarketTable(Market market, boolean group, MarketListener listener) {
		super();
		setModel(createModel(market, group));
		if (listener != null) getModel2().addMarketListener(listener);

		setAutoCreateRowSorter(true);
		setAutoResizeMode(AUTO_RESIZE_OFF);
		getTableHeader().setReorderingAllowed(false);
		
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
						description(null);
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
						description(null);
					else
						view(null);
				}
				else if(e.getKeyCode() == KeyEvent.VK_F5) {
					update();
				}
			}
		});

		update();
		
	}

	
	protected MarketTableModel createModel(Market market, boolean group) {
		return new MarketTableModel(market, group);
	}
	
	
	protected interface Task {
		
		boolean doOne(Stock stock);
		
	}
	
	
	protected int doTasksOnSelected(Task task, boolean deep) {
		int count = 0;
		List<Stock> stocks = getSelectedStocks();
		boolean ret = false;
		for (Stock stock : stocks) {
			if (stock == null)
				continue;
			else if (deep && stock instanceof StockGroup) {
				StockGroup group = (StockGroup)stock;
				List<Stock> doStocks = Util.newList(group.size());
				for (int i = 0; i < group.size(); i++) doStocks.add(group.get(i));
				for (Stock doStock : doStocks) {
					boolean ret0 = task.doOne(doStock);
					if (ret0) count++;
					ret = ret || ret0;
				}
			}
			else {
				boolean ret0 = task.doOne(stock);
				if (ret0) count++;
				ret = ret || ret0;
			}
		}
		
		if (ret) update();
		
		return count;
	}
	
	
	protected void take(Stock stock, boolean update) {
		stock = stock != null ? stock : getSelectedStock();
		StockTaker taker = new StockTaker(getMarket(), stock, update, this);
		taker.setVisible(true);
		if (taker.getOutput() != null) update();
	}
	
	
	protected void view(Stock stock) {
		stock = stock != null ? stock : getSelectedStock();
		if (stock == null) return;
		
		if (stock instanceof StockGroup) {
			new StockDescription(getMarket(), stock.code(), stock.isBuy(), null, this) {
				private static final long serialVersionUID = 1L;

				@Override
				protected List<EstimateStock> getEstimateStocks() {
					return Util.newList(0);
				}
			
			}.setVisible(true);
		}
		else
			take(stock, true);
	}
	
	
	protected void description(Stock stock) {
		stock = stock != null ? stock : getSelectedStock();
		if (stock == null) return;
		if (stock instanceof StockGroup) return;
		
		String code = stock.code();
		boolean buy = stock.isBuy();
		new StockDescription(getMarket(), stock.code(), buy, stock, this) {
			private static final long serialVersionUID = 1L;

			@Override
			protected List<EstimateStock> getEstimateStocks() {
				return getModel2().getEstimateStocks(code, buy);
			}
		
		}.setVisible(true);
	}
	
	
	protected void commit() {
		List<Stock> committedStocks = Util.newList(0);
		doTasksOnSelected(new Task() {
			@Override
			public boolean doOne(Stock stock) {
				boolean ret = commit0(stock);
				if(stock.isCommitted()) committedStocks.add(stock);
				return ret;
			}
		}, false);
		
		if (committedStocks.size() == 0) return;
		MarketImpl m = m(); if (m == null) return;
		int answer= JOptionPane.showConfirmDialog(this, "Would you like to remove committed stocks", "Removal confirmation", JOptionPane.YES_NO_OPTION);
		if (answer != JOptionPane.YES_OPTION) return;
		
		double profitSum = 0;
		long timeInterval = m.getTimeViewInterval();
		for (Stock stock : committedStocks) {
			double profit = stock.getProfit(timeInterval) + stock.getMargin(timeInterval);
			if (moveStockToTrash(stock)) profitSum += profit;
		}
		m.setBalanceBase(m.getBalanceBase() + profitSum);
	}

	
	private boolean commit0(Stock stock) {
		if (stock == null) return false;
		boolean oldCommit = stock.isCommitted();
		stock.setCommitted(!oldCommit);;
		return (stock.isCommitted() != oldCommit);
	}
	
	
	protected void toggleFixMargin(Stock stock) {
		if (stock == null) stock = getSelectedStock();
		StockImpl s = c(stock);
		boolean ret = toggleFixMargin(s, this);
		if (ret) update();
	}
	
	
	public static boolean toggleFixMargin(StockImpl stock, Component comp) {
		if (stock == null) return false;
		int answer= JOptionPane.showConfirmDialog(comp, "Are you sure to " + (stock.isFixedMargin() ? "unfix" : "fix") + " margin of " + stock.code() + "?", "Fix/Unfix margin confirmation", JOptionPane.YES_NO_OPTION);
		if (answer != JOptionPane.YES_OPTION) return false;
		
		if (stock.isFixedMargin())
			stock.fixMargin(false);
		else {
			String txtFixedMargin = JOptionPane.showInputDialog(comp, "Enter fixed unit margin", Util.format(stock.getAverageTakenPriceByLeverage(0), Util.DECIMAL_PRECISION_LONG));
			double fixedMargin = Double.NaN;
			try {
				fixedMargin = Double.parseDouble(txtFixedMargin);
				stock.setFixedUnitMargin(fixedMargin);
			}
			catch (Throwable e) {
				Util.trace(e);
				return false;
			}
		}
		
		return true;
	}
	
	
	protected void deleteWithConfirm() {
		int answer= JOptionPane.showConfirmDialog(this, "Are you sure to delete the stock (s)", "Removal confirmation", JOptionPane.YES_NO_OPTION);
		if (answer == JOptionPane.YES_OPTION) delete();
	}
	
	
	protected void delete() {
		doTasksOnSelected(new Task() {
			@Override
			public boolean doOne(Stock stock) {
				return delete0(stock);
			}
		}, false);
	}
	
	
	private boolean delete0(Stock stock) {
		return MarketImpl.remove(stock, m());
	}

	
	protected boolean moveStockToTrash(Stock stock) {
		MarketImpl m = m(); if (m == null) return false;
		return MarketImpl.move(stock, m, m.getTrashMarket());
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
		if (pl.isApplied()) update();
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
		//left.add(new JLabel("Price ratio: "));
		
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
		//right.add(panePriceRatio);
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
	
	
	protected boolean resetUnitBiasByTimeFrame() {
		int selectedRow = getSelectedRow();
		if (selectedRow < 0) return false;
		
		Stock stock = getModel2().getStock(convertRowIndexToModel(selectedRow));
		String tfUnitBiasText = JOptionPane.showInputDialog(this, "Enter time frame unit bias", stock.getUnitBias());
		double tfUnitBias = Double.NaN;
		try {
			tfUnitBias = Double.parseDouble(tfUnitBiasText);
		}
		catch (Exception e) {}
		if (Double.isNaN(tfUnitBias) || tfUnitBias < 0) {
			JOptionPane.showMessageDialog(this, "Invalid time frame unit bias", "Invalid unit bias", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		return getModel2().resetUnitBiasByTimeFrame(convertRowIndexToModel(selectedRow), tfUnitBias);
		
	}
	
	
	protected void properties(Stock stock) {
		StockPropertySetting setting = new StockPropertySetting(stock.code(), stock.getProperty(), this);
		setting.setVisible(true);
		StockProperty output = setting.getOutput();
		if (output != null) {
			stock.getProperty().set(output);
			update();
		}
	}
	
	
	protected void marketSummary() {
		MarketSummary ms = createMarketSummary();
		ms.setVisible(true);
		
		if (ms.isPressOK())
			update();
		else {
			int answer= JOptionPane.showConfirmDialog(this, "Would you like to to refresh stocks?", "Refresh confirmation", JOptionPane.YES_NO_OPTION);
			if (answer == JOptionPane.YES_OPTION) update();
		}

	}
	
	
	protected MarketSummary createMarketSummary() {
		return new MarketSummary(getMarket(), StockProperty.RUNTIME_CASCADE ? this : null, this);
	}
	
	
	protected void sortCodes() {
		Investor g = Investor.g();
		if (g == null) {
			JOptionPane.showMessageDialog(this, "Cannot retrieve global investor", "Cannot retrieve investor", JOptionPane.ERROR_MESSAGE);
			return;
		}
		MarketImpl m = m();
		if (m == null) return;
		
		m.sortCodes();
		try {
			if (g.remoteUniverse != null && !g.inServer) g.remoteUniverse.sortCodes(m.getName());
		} catch (Exception e) {Util.trace(e);}
		update();
	}
	
	
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
							deleteWithConfirm();
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

			JMenuItem mniResetUnitBiasesTimeFrame = new JMenuItem(
				new AbstractAction("Reset T-frame bias") {
					private static final long serialVersionUID = 1L;

					@Override
					public void actionPerformed(ActionEvent e) {
						JOptionPane.showMessageDialog(null, "This function not implemented yet");
						//resetUnitBiasByTimeFrame();
					}
				});
			ctxMenu.add(mniResetUnitBiasesTimeFrame);

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

	
	public MarketTableModel getModel2() {
		return (MarketTableModel) getModel();
	}
	
	
	protected Market getMarket() {
		return getModel2().getMarket();
	}
	
	
	protected MarketImpl getWatchMarket() {
		MarketImpl m = m();
		return m != null ? m.getWatchMarket() : null;
	}
	
	
	protected MarketImpl getPlaceMarket() {
		MarketImpl m = m();
		return m != null ? m.getPlaceMarket() : null;
	}
	
	
	protected MarketImpl getTrashMarket() {
		MarketImpl m = m();
		return m != null ? m.getTrashMarket() : null;
	}
	
	
	public void update() {
		int selectedRow = getSelectedRow();
		
		getModel2().update();
		init();
		
		if (selectedRow >= 0 && selectedRow < getRowCount()) {try {setRowSelectionInterval(selectedRow, selectedRow);} catch (Throwable e) {}}
	}
	
	
	private void init() {
		int lastColumn = getColumnCount() - 1;
		if (lastColumn > 0) {
			getColumnModel().getColumn(lastColumn).setMaxWidth(0);
			getColumnModel().getColumn(lastColumn).setMinWidth(0);
			getColumnModel().getColumn(lastColumn).setPreferredWidth(0);
		}
	}
	
	
	public void resetAllStopLossTakeProfits() {
		getModel2().resetAllStopLossTakeProfits();
	}

	
	public void resetAllUnitBiases() {
		getModel2().resetAllUnitBiases();
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
			else if (getModel2().isGroup() ? column == 7 : column == 11) {//Leverage ROI cell
				try {
					Stock stock = (Stock) getValueAt(row, 0);
					if (stock.isCommitted())
						return renderer;
					else
						return redmarkCellRenderer;
				}
				catch (Exception e) {
					Util.trace(e);
				}
				return renderer;
			}
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

	
	@Override
	public String getToolTipText(MouseEvent event) {
		return super.getToolTipText(event);
	}


	protected boolean open(Reader reader) {
        try {
        	boolean ret = m().read(reader);
        	update();
        	
            return ret;
        }
        catch (Exception e) {
			Util.trace(e);
        }
        
        return false;
	}

	
	protected boolean save(Writer writer) {
        try {
        	boolean ret = m().write(writer);
        	writer.flush();
            return ret;
        }
        catch (Exception e) {
			Util.trace(e);
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
	
	
	public boolean apply() {
		int selectedRow = getSelectedRow();

		boolean applied = getModel2().apply();
		init();
		
		if (selectedRow >= 0 && selectedRow < getRowCount()) {try {setRowSelectionInterval(selectedRow, selectedRow);} catch (Throwable e) {}}
		return applied;
	}
	
	
	/**
	 * This class represents highlight cell renderer according to pool.
	 * @author Loc Nguyen
	 * @version 1.0
	 */
	private class RedmarkCellRenderer extends DefaultTableCellRenderer {

		/**
		 * Serial version UID for serializable class.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Default background color.
		 */
		private Color defaultBackgroundColor = null;
		
		/**
		 * Default selected background color.
		 */
		private Color defaultSelectedBackgroundColor = null;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			defaultBackgroundColor = defaultBackgroundColor != null ? defaultBackgroundColor : table.getBackground();
			defaultSelectedBackgroundColor = defaultSelectedBackgroundColor != null ? defaultSelectedBackgroundColor : table.getSelectionBackground();
			
			MarketImpl m = m();
			if (m == null) return comp;
			Stock stock = getModel2().getStock(convertRowIndexToModel(row));
			if (stock == null) return comp;
			
			double lroi = stock.getROIByLeverage(m.getTimeViewInterval());
			if (lroi <= -1) {
				if (!isSelected)
					comp.setBackground(new Color(255, 0, 0));
				else
					comp.setBackground(new Color(200, 0, 200));
			}
			else {
				if (!isSelected)
					comp.setBackground(defaultBackgroundColor);
				else
					comp.setBackground(defaultSelectedBackgroundColor);
			}
			return comp;
		}
		
	}
	

}



class MarketTableModel extends DefaultTableModel implements MarketListener, TableModelListener {

	
	private static final long serialVersionUID = 1L;

	
	protected Market market = null;
	
	
	protected boolean group = false;
	
	
	protected Map<String, Estimator> estimators = Util.newMap(0);

	
	protected Map<String, List<EstimateStock>> stockEstimators = Util.newMap(0);
	
	
    protected boolean showCommit = false;

    
    public MarketTableModel(Market market, boolean group) {
		this.market = market;
		this.group = group;
		
		this.addTableModelListener(this);
	}
	
	
	protected MarketImpl m() {
		Universe u = market.getNearestUniverse();
		return u != null ? u.c(market) : null;
	}


	public Market getMarket() {
		return market;
	}
	
	
	public boolean isGroup() {
		return group;
	}
	
	
	public List<EstimateStock> getEstimateStocks(String code, boolean buy) {
		String key = StockProperty.keyOf(code, buy);
		if (stockEstimators.containsKey(key))
			return stockEstimators.get(key);
		else
			return Util.newList(0);
	}
	
	
	protected EstimateStock getEstimateStock(int row) {
		Object v = getValueAt(row, getColumnCount() - 1);
		if (v != null && v instanceof EstimateStock)
			return (EstimateStock)v;
		else
			return null;
	}
	

	protected Estimator getEstimator(int row) {
		Object v = getValueAt(row, getColumnCount() - 1);
		if (v != null && v instanceof Estimator)
			return (Estimator)v;
		else
			return null;
	}

	
	protected Stock getStock(int row) {
		return (Stock)getValueAt(row, 0);
	}
	

	public void update() {
		setDataVector(new Object[][] {}, new Object[] {});
		MarketImpl m = m();
		
		Universe u = m != null ? m.getNearestUniverse() : null;
		QueryEstimator query = u != null ? u.query(market.getName(), market) : m;
		query = query != null ? query : m;
		estimators.clear();
		stockEstimators.clear();
		long timeInterval = market.getTimeViewInterval();
		if (m != null) {
			for (int i = 0; i < m.size(); i++) {
				StockGroup group = m.get(i);
				if (group.isCommitted() && !showCommit) continue;
				
				String key = StockProperty.keyOf(group.code(), group.isBuy());
				Estimator estimator = query.getEstimator(group.code(), group.isBuy());
				estimators.put(key, estimator);
				
				List<EstimateStock> estimateStocks = estimator.estimateStocks(group.getStocks(timeInterval), timeInterval);
				stockEstimators.put(key, estimateStocks);
			}
		}
		
		Vector<Vector<Object>> data = Util.newVector(0);
		if (isGroup()) {
			List<StockGroup> groups = m.getGroups(timeInterval);
			for (StockGroup group : groups) {
				if (group.isCommitted() && !showCommit) continue;
				Vector<Object> row = toRow(group);
				if (row != null) data.add(row);
			}
		}
		else {
			List<Stock> stocks = market.getStocks(timeInterval);
			for (Stock stock : stocks) {
				if (stock.isCommitted() && !showCommit) continue;
				Vector<Object> row = toRow(stock);
				data.add(row);
			}
		}
		
		setDataVector(data, toColumns());
		
		fireMarketEvent(new MarketEvent(this));
	}
	
	
	protected void resetAllStopLossTakeProfits() {
		for (int row = 0; row < getRowCount(); row++) {
			EstimateStock es = getEstimateStock(row);
			if (es != null) setStopLossTakeProfit(row, es.estimatedStopLoss, es.estimatedTakeProfit);
		}
		
		fireMarketEvent(new MarketEvent(this));
	}
	
	
	protected void resetAllUnitBiases() {
		for (int row = 0; row < getRowCount(); row++) {
			EstimateStock es = getEstimateStock(row);
			if (es != null) setUnitBias(row, es.estimatedUnitBiasFromData);
		}
		
		fireMarketEvent(new MarketEvent(this));
	}
	
	
	protected boolean resetUnitBiasByTimeFrame(int row, double tfUnitBias) {
		Stock stock = getStock(row);
		if (stock == null) return false;
		
		if (isGroup()) {
			return false;
		}
		else {
			EstimateStock es = getEstimateStock(row);
			if (es == null) return false;
			return setUnitBias(row, Math.max(es.estimatedUnitBiasFromData, tfUnitBias));
		}
	}

	
	protected boolean apply() {
		MarketImpl m = m();
		if (m != null) {
			boolean ret = m.apply();
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

	
	private boolean setStopLossTakeProfit(int row, double stopLoss, double takeProfit) {
		StockImpl stock = m().c(getStock(row)); if (stock == null) return false;
		if (isGroup())
			return false;
		else {
			stock.setStopLoss(stopLoss);
			stock.setTakeProfit(takeProfit);
			setValueAt(new Pair(stopLoss, takeProfit), row, 7);
			return true;
		}
	}
	
	
	private boolean setUnitBias(int row, double estUnitBias) {
		Stock stock = getStock(row); if (stock == null) return false;
		if (isGroup())
			return false;
		else {
			stock.setUnitBias(estUnitBias);
			setValueAt(new Pair(estUnitBias, estUnitBias), row, 15);
			return true;
		}
	}

	
	protected Vector<Object> toRow(Stock stock) {
		long timeViewInterval = market.getTimeViewInterval();
		Vector<Object> row = Util.newVector(0);
		
		if (stock instanceof StockGroup) { 
			StockGroup group = (StockGroup)stock;
			double margin = group.getMargin(timeViewInterval);
			
			row.add(group);
			row.add(group.isBuy());
			row.add(group.getLeverage() != 0 ? 1.0/group.getLeverage() : 0);
			row.add(group.getVolume(timeViewInterval, false));
			row.add(group.getTakenValue(timeViewInterval));
			row.add(margin);
			row.add(group.getProfit(timeViewInterval));
			row.add(new Percentage(group.getROIByLeverage(timeViewInterval), Util.DECIMAL_PRECISION_SHORT));
			row.add(new Percentage(group.getROI(timeViewInterval), Util.DECIMAL_PRECISION_SHORT));
			row.add(new Percentage(group.calcOscillRatio(timeViewInterval), Util.DECIMAL_PRECISION_SHORT));
			row.add(group.calcOscill(timeViewInterval));
			row.add(group.calcBias(timeViewInterval));
			
			Triple tv = new Triple(Double.NaN, Double.NaN, Double.NaN);
			Estimator estimator = estimators.get(StockProperty.keyOf(group.code(), group.isBuy()));
			if (estimator != null && group.getLeverage() != 0) {
				double volume = estimator.estimateInvestVolume(timeViewInterval);
				double amount = estimator.estimateInvestAmount(timeViewInterval);
				double totalAmount = estimator.getInvestAmount(timeViewInterval);
				tv = new Triple(volume, amount, totalAmount);
			}
			row.add(tv);
			
			double div = group.getDividend(timeViewInterval);
			row.add(new PairValuePercentage(div, margin != 0 ? div/margin : 0));
			row.add(new Time(group.getDividendTimePoint(timeViewInterval)));

			row.add(stock.getCategory());
			row.add(estimator);
		}
		else {
			StockImpl s = market.c(stock);
			if (s == null || !s.isValid(timeViewInterval)) return null;

			List<EstimateStock> estimateStocks = getEstimateStocks(stock.code(), stock.isBuy());
			EstimateStock found = EstimateStock.get(stock.code(), stock.isBuy(), estimateStocks);

			row.add(stock);
			row.add(stock.isBuy());
			row.add(new Time(s.getTakenPrice(timeViewInterval).getTime()));
			row.add(stock.getVolume(timeViewInterval, true));
			row.add(stock.getAverageTakenPrice(timeViewInterval));
			
			Price price = s.getPrice();
			row.add(price.get());
			row.add(new Triple(price.getLow(), price.getHigh(), price.getAlt()));
			row.add(new Pair(stock.getStopLoss(), stock.getTakeProfit()));
			
			row.add(stock.getMargin(timeViewInterval));
			row.add(stock.getProfit(timeViewInterval));
			row.add(stock.isCommitted());
			
			row.add(new Percentage(stock.getROIByLeverage(timeViewInterval), Util.DECIMAL_PRECISION_SHORT));
			row.add(new Percentage(stock.getROI(timeViewInterval), Util.DECIMAL_PRECISION_SHORT));
			row.add(new Percentage(stock.calcOscillRatio(timeViewInterval), Util.DECIMAL_PRECISION_SHORT));
			row.add(stock.getPriceOscill(timeViewInterval));
			row.add(found != null ? new Pair(found.estimatedUnitBias, stock.getUnitBias()) : new Pair(stock.getUnitBias(), stock.getUnitBias()));

			Triple tv = new Triple(Double.NaN, Double.NaN, Double.NaN);
			if (found != null) tv = new Triple(found.estimatedPrice, found.estimatedStopLoss, found.estimatedTakeProfit);
			row.add(tv);
			
			row.add(found != null ? new Pair(found.estimatedPriceMean, price.get()) : new Pair(price.get(), price.get()));
			
			row.add(stock.getCategory());
			row.add(found);
		}

		return row;
	}
	
	
	/**
	 * Getting list of column names.
	 * @return list of column names.
	 */
	protected Vector<String> toColumns() {
		Vector<String> columns = Util.newVector(0);
		
		if (isGroup()) {
			columns.add("Code");
			columns.add("Buy");
			columns.add("Leverage");
			columns.add("Volume");
			columns.add("Taken value");
			columns.add("Margin");
			columns.add("Profit");
			columns.add("Leverage ROI");
			columns.add("ROI");
			columns.add("Oscill. ratio");
			columns.add("Total oscill.");
			columns.add("Total bias");
			columns.add("Rec. volume / amount / total amount");
			columns.add("Dividend");
			columns.add("Dividend date");
			columns.add("Category");
			columns.add("");
		}
		else {
			columns.add("Code");
			columns.add("Buy");
			columns.add("Taken date");
			columns.add("Volume");
			columns.add("Taken price");
			columns.add("Price");
			columns.add("Low/high/alt prices");
			columns.add("Stop loss / take profit");
			columns.add("Margin");
			columns.add("Profit");
			columns.add("Committed");
			columns.add("Leverage ROI");
			columns.add("ROI");
			columns.add("Oscill. ratio");
			columns.add("Oscill.");
			columns.add("Unit bias (est. / setting)");
			columns.add("Est. price / stop loss / take profit");
			columns.add("Est. price mean / price setting");
			columns.add("Category");
			columns.add("");
		}
		
		return columns;
	}


	@Override
	public Class<?> getColumnClass(int columnIndex) {
		
		if (isGroup()) {
			if (columnIndex ==  0 || columnIndex ==  15 || columnIndex ==  getColumnCount() - 1)
				return super.getColumnClass(columnIndex);
			else if (columnIndex == 1)
				return Boolean.class;
			else if (columnIndex == 7 || columnIndex == 8 || columnIndex == 9)
				return Percentage.class;
			else if (columnIndex == 12)
				return Triple.class;
			else if (columnIndex == 14)
				return Time.class;
			else
				return Double.class;
		}
		else {
			if (columnIndex ==  0 || columnIndex ==  18 || columnIndex ==  getColumnCount() - 1)
				return super.getColumnClass(columnIndex);
			else if (columnIndex == 1 || columnIndex == 10)
				return Boolean.class;
			else if (columnIndex ==  2)
				return Time.class;
			else if (columnIndex == 6 || columnIndex == 16)
				return Triple.class;
			else if (columnIndex == 7 || columnIndex == 15 || columnIndex == 17)
				return Pair.class;
			else if (columnIndex == 11 || columnIndex == 12 || columnIndex == 13)
				return Percentage.class;
			else
				return Double.class;
		}
	}


	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}

	
	@Override
	public void tableChanged(TableModelEvent e) { }
	

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


	/**
	 * This class represents the percentage value.
	 * @author Loc Nguyen
	 * @version 1.0
	 *
	 */
	protected static class Percentage implements Serializable, Cloneable, Comparable<Percentage> {

		private static final long serialVersionUID = 1L;
		
		protected double v = 0;
		
		protected int formatDecimal = 0;
		
		public Percentage(double v, int formatDecimal) {
			this.v = v;
			this.formatDecimal = formatDecimal;
		}

		public Percentage(double v) {
			this(v, 0);
		}

		@Override
		public int compareTo(Percentage o) {
			if (this.v < o.v)
				return -1;
			else if (this.v == o.v)
				return 0;
			else
				return 1;
		}

		@Override
		public String toString() {
			if (Double.isNaN(v))
				return "";
			else if (Double.isInfinite(v))
				return "Infinity";
			else if (formatDecimal <= 0)
				return Util.format(v*100) + "%";
			else
				return Util.format(v*100, formatDecimal) + "%";
		}

	}

	
	protected static class Pair implements Serializable, Cloneable, Comparable<Pair> {
		
		private static final long serialVersionUID = 1L;

		protected double v1 = 0;
		
		protected double v2 = 0;
		
		public Pair(double v1, double v2) {
			this.v1 = v1;
			this.v2 = v2;
		}

		@Override
		public String toString() {
			if (Double.isNaN(v1) || Double.isNaN(v1))
				return "";
			else if (Double.isInfinite(v1) || Double.isInfinite(v1))
				return "Infinity";
			else {
				int d = Util.DECIMAL_PRECISION_SHORT;
				return Util.format(v1, d) + " / " + Util.format(v2, d);
			}
		}

		@Override
		public int compareTo(Pair o) {
			if (this.v1 < o.v1)
				return -1;
			else if (this.v1 == o.v1)
				return this.v2 < o.v2 ? -1 : (this.v2 == o.v2 ? 0 : 1);
			else
				return 1;
		}
		
	}
	
	
	protected static class PairPercentage extends Pair {

		private static final long serialVersionUID = 1L;
		
		public PairPercentage(double v1, double v2) {
			super(v1, v2);
		}

		@Override
		public String toString() {
			if (Double.isNaN(v1) || Double.isNaN(v1))
				return "";
			else if (Double.isInfinite(v1) || Double.isInfinite(v1))
				return "Infinity";
			else {
				int d = Util.DECIMAL_PRECISION_SHORT;
				return Util.format(v1*100, d) + "% / " + Util.format(v2*100, d) + "%";
			}
		}

	}
	
	
	@Deprecated
	protected static class PairPercentageValue extends PairPercentage {

		private static final long serialVersionUID = 1L;
		
		public PairPercentageValue(double v1, double v2) {
			super(v1, v2);
		}

		@Override
		public String toString() {
			if (Double.isNaN(v1) || Double.isNaN(v1))
				return "";
			else if (Double.isInfinite(v1) || Double.isInfinite(v1))
				return "Infinity";
			else {
				int d = Util.DECIMAL_PRECISION_SHORT;
				return Util.format(v1*100, d) + "% / " + Util.format(v2, d);
			}
		}

	}

	
	protected static class PairValuePercentage extends PairPercentage {

		private static final long serialVersionUID = 1L;
		
		public PairValuePercentage(double v1, double v2) {
			super(v1, v2);
		}

		@Override
		public String toString() {
			if (Double.isNaN(v1) || Double.isNaN(v1))
				return "";
			else if (Double.isInfinite(v1) || Double.isInfinite(v1))
				return "Infinity";
			else {
				int d = Util.DECIMAL_PRECISION_SHORT;
				return Util.format(v1, d) + " / " + Util.format(v2*100, d) + "%";
			}
		}

	}

	
	protected static class Triple implements Serializable, Cloneable, Comparable<Triple> {
		
		private static final long serialVersionUID = 1L;

		protected double v1 = 0;
		
		protected double v2 = 0;
		
		protected double v3 = 0;

		public Triple(double v1, double v2, double v3) {
			this.v1 = v1;
			this.v2 = v2;
			this.v3 = v3;
		}

		@Override
		public String toString() {
			if (Double.isNaN(v1) || Double.isNaN(v1) || Double.isNaN(v3))
				return "";
			else if (Double.isInfinite(v1) || Double.isInfinite(v1) || Double.isInfinite(v3))
				return "Infinity";
			else {
				int d = Util.DECIMAL_PRECISION_SHORT;
				return Util.format(v1, d) + " / " + Util.format(v2, d) + " / " + Util.format(v3, d) + "";
			}
		}

		@Override
		public int compareTo(Triple o) {
			if (this.v1 < o.v1)
				return -1;
			else if (this.v1 == o.v1) {
				if (this.v2 < o.v2)
					return -1;
				else if (this.v2 == o.v2)
					return this.v3 < o.v3 ? -1 : (this.v3 == o.v3 ? 0 : 1);
				else
					return 1;
			}
			else
				return 1;
		}
		
	}

	
	protected static class Time implements Serializable, Cloneable, Comparable<Time> {
		
		private static final long serialVersionUID = 1L;
		
		protected long time = 0;
		
		public Time(long time) {
			this.time = time;
		}

		@Override
		public int compareTo(Time o) {
			if (this.time < o.time)
				return -1;
			else if (this.time == o.time)
				return 0;
			else
				return 1;
		}

		@Override
		public String toString() {
			return time != 0 ? Util.format(new Date(time)) : "";
		}
		
	}
	
	
}



class MarketPanel extends JPanel implements MarketListener {


	private static final long serialVersionUID = 1L;

	
	protected JButton btnTake;
	
	
	protected JButton btnSummary;
	
	
	protected JButton btnRefresh;

	
	protected JButton btnReestimateLossesProfits;

	
	protected JButton btnReestimateUnitBiases;

	
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

	
	protected JLabel lblSurplus;

	
	protected JLabel lblBias;

	
	protected JLabel lblOscill;

	
	protected JLabel lblEstInvest;

	
	protected MarketTable tblMarket = null;
	
	
	protected File file = null;
	
	
	protected boolean enableContext = false;
	
	
	public MarketPanel(Market market, boolean group, MarketListener superListener) {
		tblMarket = createMarketTable(market, group, this);
		if (superListener != null) tblMarket.getModel2().addMarketListener(superListener);
		
		setLayout(new BorderLayout());
		
		MarketPanel thisPanel = this;

		JPanel header = new JPanel(new BorderLayout());
		add(header, BorderLayout.NORTH);

		JPanel toolbar1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		header.add(toolbar1, BorderLayout.WEST);
		
		lblStartTime = new JLabel();
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
				tblMarket.marketSummary();
			}
		});
		btnSummary.setMnemonic('s');
		toolbar2.add(btnSummary);

		btnRefresh = new JButton("Refresh");
		btnRefresh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tblMarket.update();
			}
		});
		btnRefresh.setMnemonic('r');
		toolbar2.add(btnRefresh);
		
		
		JPanel body = new JPanel(new BorderLayout());
		add(body, BorderLayout.CENTER);
		body.add(new JScrollPane(tblMarket), BorderLayout.CENTER);
		
		JPanel paneMarket = new JPanel(new BorderLayout());
		body.add(paneMarket, BorderLayout.SOUTH);
		
		JPanel paneMarketButtons1 = new JPanel();
		paneMarket.add(paneMarketButtons1, BorderLayout.WEST);
		
		btnReestimateLossesProfits = new JButton("Re. losses / profits");
		btnReestimateLossesProfits.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int answer= JOptionPane.showConfirmDialog(thisPanel, "Be careful to reestimate stop losses and take profits.\nAre you sure to reestimate them?", "Reestimation confirmation", JOptionPane.YES_NO_OPTION);
				if (answer == JOptionPane.YES_OPTION) {
					tblMarket.resetAllStopLossTakeProfits();
				}
			}
		});
		btnReestimateLossesProfits.setMnemonic('o');
		btnReestimateLossesProfits.setVisible(false);
		paneMarketButtons1.add(btnReestimateLossesProfits);

		btnReestimateUnitBiases = new JButton("Re. unit biases");
		btnReestimateUnitBiases.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int answer= JOptionPane.showConfirmDialog(thisPanel, "Be careful to reestimate unit biases.\nAre you sure to reestimate them?", "Reestimation confirmation", JOptionPane.YES_NO_OPTION);
				if (answer == JOptionPane.YES_OPTION) {
					tblMarket.resetAllUnitBiases();
				}
			}
		});
		btnReestimateUnitBiases.setMnemonic('b');
		btnReestimateUnitBiases.setVisible(false);
		paneMarketButtons1.add(btnReestimateUnitBiases);

		btnSortCodes = new JButton("Sort codes");
		btnSortCodes.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tblMarket.m().sortCodes();
				tblMarket.update();
			}
		});
		btnSortCodes.setMnemonic('c');
		btnSortCodes.setVisible(false);
		paneMarketButtons1.add(btnSortCodes);

		JPanel paneMarketButtons2 = new JPanel();
		paneMarket.add(paneMarketButtons2, BorderLayout.EAST);

		chkShowCommit = new JCheckBox();
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
		paneMarketButtons2.add(chkShowCommit);

		
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

		JPanel footerRow2 = new JPanel(new BorderLayout());
		footer.add(footerRow2, BorderLayout.SOUTH);

		JPanel footerRow21 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		footerRow2.add(footerRow21, BorderLayout.NORTH);

		footerRow21.add(lblProfit = new JLabel());
		footerRow21.add(new JLabel(" "));
		footerRow21.add(lblSurplus = new JLabel());
		footerRow21.add(new JLabel(" "));
		footerRow21.add(lblROI = new JLabel());
		footerRow21.add(new JLabel(" "));
		footerRow21.add(lblOscill = new JLabel());
		footerRow21.add(new JLabel(" "));
		footerRow21.add(lblBias = new JLabel());
		
		JPanel footerRow22 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		footerRow2.add(footerRow22, BorderLayout.SOUTH);
		
		footerRow22.add(lblEstInvest = new JLabel());

		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Component parent = thisPanel.getParent();
				if (!(parent instanceof InvestorTabbedPane) && !enableContext) return;
				
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
	
	
	protected void update() {
		Market market = getMarket();
		MarketImpl m = tblMarket.m();
		try {chkShowCommit.setText("Show/hide commit (" + m.countText(chkShowCommit.isSelected()) + ")");} catch (Throwable ex) {}

		int d = Util.DECIMAL_PRECISION_SHORT;
		long timeViewInterval = market.getTimeViewInterval();
		double balance = market.getBalance(timeViewInterval);
		double margin = market.getMargin(timeViewInterval);
		double freeMargin = market.getFreeMargin(timeViewInterval);
		double equity = margin + freeMargin;
		double profit = market.getProfit(timeViewInterval);
		double surplus = balance != 0 ? profit / balance : 0;
		//double roi = market.getROI(timeViewInterval);
		double lRoi = market.getROIByLeverage(timeViewInterval);
		//double oscillRatio = market.getPriceOscillRatio(timeViewInterval);
		double oscill = market.calcOscill(timeViewInterval);
		double bias = market.calcBias(timeViewInterval);
		double dev = market.calcMinMaxDev(timeViewInterval);
		double invest = market.calcInvestAmount(timeViewInterval);
		double investRisky = market.calcInvestAmountRisky(timeViewInterval);
		
		if (m != null) {
			int viewDays = (int) (m.getTimeViewInterval() / (1000*3600*24));
			Date currentDate = new Date();
			int days = (int) ((currentDate.getTime() - m.getTimeStartPoint()) / (1000*3600*24));
			String txtDate = Util.formatSimple(new Date(m.getTimeStartPoint())) + " -- " + Util.formatSimple(currentDate) + " last ";
			if (m.getTimeViewInterval() <= 0)
				txtDate += days + " days";
			else
				txtDate += (viewDays <  days ? viewDays + "/" + days : days) + " days";
			lblStartTime.setText(txtDate);
		}
		
		lblBalance.setText("Balance: " + Util.format(balance, d));
		lblEquity.setText("Equity: " + Util.format(equity, d));
		lblMargin.setText("Margin: " + Util.format(margin, d));
		lblFreeMargin.setText("Free margin: " + Util.format(freeMargin, d));
		lblMarginLevel.setText("Mar. level: " + Util.format((margin != 0 ? equity / margin : 0)*100, d) + "%");
		
		lblProfit.setText("Profit: " + Util.format(profit, d));
		lblSurplus.setText("Sur: " + Util.format(surplus*100, d) + "%");
		lblROI.setText("Lev.ROI: " + Util.format(lRoi*100, d) + "%");
		lblOscill.setText("Oscill: " + Util.format(oscill, d));
		lblBias.setText("Bias: " + Util.format(bias, d) + " / " + Util.format(dev, d));
		lblEstInvest.setText("INVEST: " + Util.format(invest, d) + " / " + Util.format(investRisky, d));
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
		MarketWatchDialog dlgMarket = new MarketWatchDialog(tblMarket.getWatchMarket(), tblMarket.getModel2().isGroup(), StockProperty.RUNTIME_CASCADE ? tblMarket : null, this);
		dlgMarket.setTitle("Watch stocks for market " + tblMarket.getMarket().getName());
		dlgMarket.setVisible(true);
		
		if (dlgMarket.isPressOK())
			tblMarket.apply();
		else {
			int answer= JOptionPane.showConfirmDialog(this, "Would you like to to apply changes?", "Applying confirmation", JOptionPane.YES_NO_OPTION);
			if (answer == JOptionPane.YES_OPTION) tblMarket.apply();
		}
	}
	
	
	private void placeStocks() {
		MarketPlaceDialog2 dlgMarket = new MarketPlaceDialog2(tblMarket.getPlaceMarket(), tblMarket.getModel2().isGroup(), StockProperty.RUNTIME_CASCADE ? tblMarket : null, this);
		dlgMarket.setTitle("Place stocks for market " + tblMarket.getMarket().getName());
		dlgMarket.setVisible(true);
		
		if (dlgMarket.isPressOK())
			tblMarket.apply();
		else {
			int answer= JOptionPane.showConfirmDialog(this, "Would you like to to apply changes?", "Applying confirmation", JOptionPane.YES_NO_OPTION);
			if (answer == JOptionPane.YES_OPTION) tblMarket.apply();
		}
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
	
	
	protected MarketTable createMarketTable(Market market, boolean group, MarketListener listener) {
		MarketPanel thisPanel = this;
		return new MarketTable(market, group, listener) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void deleteWithConfirm() {
				int answer = JOptionPane.showConfirmDialog(thisPanel, "Would you like to move these stocks to trash?\nIf yes, they are moved to trash.\nIf no, they are deleted forever.", "Delete stocks", JOptionPane.YES_NO_OPTION);
				if (answer != JOptionPane.YES_OPTION) {
					super.delete();
					return;
				}
				
				doTasksOnSelected(new Task() {
					@Override
					public boolean doOne(Stock stock) {
						return moveStockToTrash(stock);
					}
				}, true);
			}

			private void watch() {
				doTasksOnSelected(new Task() {
					@Override
					public boolean doOne(Stock stock) {
						return watch0(stock);
					}
				}, true);
			}

			private boolean watch0(Stock stock) {
				MarketImpl m = m(); if (m == null) return false;
				return MarketImpl.watch(stock, m, m.getWatchMarket());
			}
			
			private void place() {
				int answer = JOptionPane.showConfirmDialog(thisPanel, "Be careful to place bought/sold stocks.\nWould you like to place them?", "Placing confirmation", JOptionPane.YES_NO_OPTION);
				if (answer != JOptionPane.YES_OPTION) return;

				doTasksOnSelected(new Task() {
					@Override
					public boolean doOne(Stock stock) {
						return place0(stock);
					}
				}, true);
			}

			private boolean place0(Stock stock) {
				MarketImpl m = m(); if (m == null) return false;
				boolean placed = MarketImpl.place(stock, m, m.getPlaceMarket());
				if (!placed)
					return false;
				else
					return MarketImpl.remove(stock, m);
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
				
				JMenuItem miPlace = new JMenuItem("Place");
				miPlace.addActionListener( 
					new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							place();
						}
					});
				ctxMenu.add(miPlace);
				
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
	
	
	@Override
	public void notify(MarketEvent evt) {
		update();
	}


	protected boolean onOpen() {
		return onOpen(null);
	}
	
	
	protected boolean onOpen(List<String> exclusiveNames) {
		JFileChooser fc = createFileChooser();
        if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return false;
        File file = fc.getSelectedFile();
        if (!file.exists() || file.isDirectory()) {
			JOptionPane.showMessageDialog(this, "Wrong file", "Wrong file", JOptionPane.ERROR_MESSAGE);
			return false;
        }
        
        if (exclusiveNames != null && exclusiveNames.size() > 0) {
        	String marketName = MarketImpl.readMarketName(file);
        	if (marketName != null && exclusiveNames.contains(marketName)) {
    			JOptionPane.showMessageDialog(this, "Duplicated market name", "Duplicated name", JOptionPane.ERROR_MESSAGE);
        		return false;
        	}
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
			Util.trace(e);
        }
        
        return false;
	}
	
	
	protected void onSave() {
		JFileChooser fc = createFileChooser();
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        
        File file = fc.getSelectedFile();
        FileFilter filter = fc.getFileFilter();
        if (filter.getDescription().compareToIgnoreCase(StockProperty.JSI_EXT) == 0) {
        	int index = file.getName().indexOf(".");
        	if (index < 0)
        		file = new File(file.getAbsolutePath().concat("." + StockProperty.JSI_EXT));
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
			Util.trace(e);
        }
        
        return false;
	}
	
	
	protected File getFile() {
		return file;
	}
	
	
	protected void autoBackup() {
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
	
	
	protected MarketPanel paneMarket = null;
	
	
	protected JButton btnOK;
	
	
	protected JButton btnCancel;
	
	
	private boolean isPressOK = false;
	
	
	public MarketDialog(Market market, boolean group, MarketListener superListener, Component parent) {
		super(Util.getDialogForComponent(parent), "Market " + market.getName(), true);
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
			}
		});
		
		addMouseListener(new MouseAdapter() { });
		
		setSize(650, 500);
		setLocationRelativeTo(Util.getDialogForComponent(parent));
		
		setLayout(new BorderLayout());

		JPanel body = new JPanel(new BorderLayout());
		add(body, BorderLayout.CENTER);
		paneMarket = createMarketPanel(market, group, superListener);
		body.add(paneMarket, BorderLayout.CENTER);
		
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
	
	
	protected MarketPanel createMarketPanel(Market market, boolean group, MarketListener superListener) {
		return new MarketPanel(market, group, superListener);
	}
	
	
}



class MarketSummary extends JDialog implements MarketListener {

	
	private static final long serialVersionUID = 1L;

	
	protected Market market = null;
	
	
	protected MarketTable tblMarket = null;
	
	
	protected JCheckBox chkShowCommit = null;
	
	
	protected JButton btnOK = null;
	
	
	protected JButton btnCancel = null;

	
	private boolean isPressOK = false;

	
	public MarketSummary(Market market, MarketListener superListener, Component component) {
		super(Util.getDialogForComponent(component), "Market summary", true);
		this.market = market;
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setSize(600, 400);
		setLocationRelativeTo(Util.getDialogForComponent(component));
		setLayout(new BorderLayout());
		
		JPanel body = new JPanel(new BorderLayout());
		add(body, BorderLayout.CENTER);
		
		tblMarket = createMarketTable(market, superListener);
		body.add(new JScrollPane(tblMarket), BorderLayout.CENTER);
		
		JPanel paneMarket = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		body.add(paneMarket, BorderLayout.SOUTH);
		
		chkShowCommit = new JCheckBox();
		chkShowCommit.setSelected(tblMarket.isShowCommit());
		chkShowCommit.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (tblMarket.isShowCommit() != chkShowCommit.isSelected()) {
					tblMarket.setShowCommit(chkShowCommit.isSelected());
					tblMarket.update();
					
					try {chkShowCommit.setText("Show/hide commit (" + tblMarket.m().countText(chkShowCommit.isSelected()) + ")");} catch (Throwable ex) {}
				}
			}
		});
		paneMarket.add(chkShowCommit);

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
		
		btnCancel = new JButton("Close");
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		footer.add(btnCancel);
		
		update();
	}
	
	
	public MarketTable getMarketTable() {
		return tblMarket;
	}
	
	
	public boolean isPressOK() {
		return isPressOK;
	}
	
	
	protected MarketTable createMarketTable(Market market, MarketListener superListener) {
		MarketTable tblMarket = new MarketTable(market, true, this);
		tblMarket.getModel2().addMarketListener(superListener);
		return tblMarket;
	}
	
	
	protected void update() {
		try {chkShowCommit.setText("Show/hide commit (" + tblMarket.m().countText(chkShowCommit.isSelected()) + ")");} catch (Throwable ex) {}
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
		super(Util.getDialogForComponent(parent), "Add price" + (input != null ? " for " + input.code() : ""), true);
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
		left.add(new JLabel("Low price: "));
		left.add(new JLabel("High price: "));
		left.add(new JLabel("Alt price: "));
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
		right.add(paneAltPrice);
		txtAltPrice = new JFormattedTextField(Util.getNumberFormatter());
		txtAltPrice.setValue(input.getPrice().getAlt());
		paneAltPrice.add(txtAltPrice, BorderLayout.CENTER);
		//
		btnAltPrice = new JButton("Estimate");
		btnAltPrice.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Estimator estimator = getEstimator();
				if (estimator != null)  txtAltPrice.setValue(estimator.estimatePrice(market.getTimeViewInterval()));
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
		
		if (lowPrice == 0 && highPrice == 0) {
			txtLowPrice.setValue(lowPrice = price);
			txtHighPrice.setValue(highPrice = price);
		}
		else if (price < lowPrice || price > highPrice)
			return false;
		
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
		
		double altPrice = txtAltPrice.getValue() instanceof Number ? ((Number)txtAltPrice.getValue()).doubleValue() : 0;
		if (altPrice < price.getLow() || altPrice > price.getHigh()) altPrice = 0;
		price.setAlt(altPrice);
		
		if (!input.setPrice(price)) return;

		m.apply();
		
		output = input;
		
		JOptionPane.showMessageDialog(this, "Add price successfully", "Add price", JOptionPane.INFORMATION_MESSAGE);
		dispose();
	}
	
	
	public Stock getOutput() {
		return output;
	}
	

}



