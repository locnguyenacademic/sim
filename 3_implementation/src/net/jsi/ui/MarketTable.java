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
import java.util.Date;
import java.util.List;
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
import javax.swing.text.NumberFormatter;

import net.jsi.Estimator;
import net.jsi.Market;
import net.jsi.MarketImpl;
import net.jsi.Price;
import net.jsi.QueryEstimator;
import net.jsi.Stock;
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
		
		if (listener != null) getModel2().addMarketListener(listener);
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
			
			ctxMenu.addSeparator();

			JMenuItem miListPrices = new JMenuItem("List prices");
			miListPrices.addActionListener( 
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						new PriceList(getMarket(), stock, getMarket().getTimeViewInterval(), false, false, tblMarket).setVisible(true);;
					}
				});
			ctxMenu.add(miListPrices);

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
					MarketSummary ms = new MarketSummary(getMarket(), tblMarket, tblMarket);
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

	
	protected boolean open(Reader reader) {
        try {
        	boolean ret = m().open(reader);
        	if (ret) update();
        	
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
	
	
	private MarketImpl m() {
		Universe u = getMarket().getNearestUniverse();
		return u != null ? u.c(getMarket()) : null;
	}


}



class MarketTableModel extends DefaultTableModel implements MarketListener, TableModelListener {

	
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

					String volume = Util.format(estimator.estimateInvestVolume(timeViewInterval));
					String amount = Util.format(estimator.estimateInvestAmount(timeViewInterval));
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
		super(Util.getFrameForComponent(component), "Stock summary", true);
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



class MarketGroupSummary extends JDialog {

	
	private static final long serialVersionUID = 1L;

	
	protected Market market = null;
	
	
	public MarketGroupSummary(Market market, String code, boolean buy, Component component) {
		super(Util.getFrameForComponent(component), "Stock group summary", true);
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

		int index = u.lookup(market.name());
		if (index < 0) return;
		QueryEstimator query = u.query(index);
		Estimator estimator = query.getEstimator(code, buy);
		if (estimator == null) return;
		
		StringBuffer info = new StringBuffer();
		info.append("Code: " + code + "\n");
		info.append("Buy: " + group.isBuy() + "\n");
		info.append("Leverage: " + (group.getLeverage() != 0 ? Util.format(1.0 / group.getLeverage()) : "Infinity") + "\n");
		info.append("Volume: " + Util.format(group.getVolume(timeViewInterval, true)) + "\n");
		info.append("Taken value: " + Util.format(group.getTakenValue(timeViewInterval)) + "\n");
		info.append("Margin: " + Util.format(group.getMargin(timeViewInterval)) + "\n");
		info.append("Profit: " + Util.format(group.getProfit(timeViewInterval)) + "\n");
		info.append("ROI: " + Util.format(group.getROIByLeverage(timeViewInterval)*100) + "%\n");
		
		info.append("\n");
		info.append("Estimated low price: " + Util.format(estimator.estimateLowPrice(timeViewInterval)) + "\n");
		info.append("Estimated high price: " + Util.format(estimator.estimateHighPrice(timeViewInterval)) + "\n");
		info.append("Estimated stop loss: " + Util.format(estimator.estimateStopLoss(timeViewInterval)) + "\n");
		info.append("Estimated take profit: " + Util.format(estimator.estimateTakeProfit(timeViewInterval)) + "\n");
		info.append("Estimated unit bias: " + Util.format(estimator.estimateUnitBias(timeViewInterval)) + "\n");
		info.append("Estimated total bias: " + Util.format(m.calcTotalBias(timeViewInterval)) + "\n");

		info.append("\n");
		info.append("Estimated invest amount: " + Util.format(estimator.getInvestAmount(timeViewInterval)) + "\n");
		info.append("Recommended invest amount: " + Util.format(estimator.estimateInvestAmount(timeViewInterval)) + "\n");
		info.append("Recommended invest volume: " + Util.format(estimator.estimateInvestVolume(timeViewInterval)) + "\n");
		
		txtInfo.setText(info.toString());
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
		txtPrice = new JFormattedTextField(new NumberFormatter());
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
		txtLowPrice = new JFormattedTextField(new NumberFormatter());
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
		txtHighPrice = new JFormattedTextField(new NumberFormatter());
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
		txtLastDate = new JFormattedTextField(new SimpleDateFormat(Util.DATE_FORMAT));
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
//		paneLastDate2.add(btnLastDateList);
		
		
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

		output = input;
		
		JOptionPane.showMessageDialog(this, "Add new / update successfully", "Add new / update", JOptionPane.INFORMATION_MESSAGE);
		dispose();
	}
	
	
	public Stock getOutput() {
		return output;
	}
	

}


/**
 * Text field contains stock property.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
class StockPropertyTextField extends JTextArea {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Internal stock property.
	 */
	protected StockProperty property = null;
	
	
	/**
	 * Default constructor.
	 */
	public StockPropertyTextField() {
		super();
		setEditable(false);
	}
	
	
	/**
	 * Constructor with specified stock property.
	 * @param property specified stock property.
	 */
	public StockPropertyTextField(StockProperty property) {
		this();
		setStockProperty(property);
	}

	
	/**
	 * Getting stock property.
	 * @return internal stock property.
	 */
	public StockProperty getStockProperty() {
		return property;
	}
	
	
	/**
	 * Setting stock property.
	 * @param property specified stock property.
	 */
	public void setStockProperty(StockProperty property) {
		this.property = property;
		if (property == null)
			setText("");
		else
			setText("(stock property)");
	}
	
	
	public void modify() {
		
	}
	
	
}



class PriceListTable extends JTable {

	
	private static final long serialVersionUID = 1L;
	

//	private DateCellRenderer dateCellRenderer = new DateCellRenderer();
//	private DateCellEditor dateCellEditor = new DateCellEditor();

	
	protected ReadOnlyCellRenderer readOnlyCellRenderer = new ReadOnlyCellRenderer();

	
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

//		setDefaultRenderer(Date.class, dateCellRenderer);
//		setDefaultEditor(Date.class, dateCellEditor);

		update();
	}


	public void update() {
		getModel2().update();
		
		if (getColumnModel().getColumnCount() > 0) {
			getColumnModel().getColumn(0).setMaxWidth(0);
			getColumnModel().getColumn(0).setMinWidth(0);
			getColumnModel().getColumn(0).setPreferredWidth(0);
		}
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
	
	
	protected TakenPrice getTakenPrice() {
		return getModel2().getTakenPrice();
	}

	
	public Price getSelectedPrice() {
		int row = getSelectedRow();
		return getModel2().getPriceAt(row);
	}
	
	
	@Override
	public TableCellRenderer getCellRenderer(int row, int column) {
		TableCellRenderer r = getCellRenderer0(row, column);
		if (r != null) return r;
		
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

	
	private TableCellRenderer getCellRenderer0(int row, int column) {
		Object value = getValueAt(row, column);
		if (value == null) return null;
		
		TakenPrice takenPrice = getTakenPrice();
		if (takenPrice != null && takenPrice.getPrice() == getModel2().getPriceAt(row))
			return readOnlyCellRenderer;
		else
			return null;
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

	
	/**
	 * This class represents read-only cell renderer.
	 * @author Loc Nguyen
	 * @version 1.0
	 */
	private class ReadOnlyCellRenderer extends DefaultTableCellRenderer.UIResource {

		/**
		 * Serial version UID for serializable class.
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			comp.setBackground(new Color(200, 200, 200));
			
			return comp;
			
		}
		
	}


}



abstract class PriceListTableModel extends DefaultTableModel {

	
	private static final long serialVersionUID = 1L;
	
	
	protected Stock stock = null;
	
	
	protected long timeInterval = 0;
	
	
	protected boolean modified = false;
	
	
	protected boolean editable = true;
	
	
	public PriceListTableModel(Stock stock, long timeInterval) {
		this.stock = stock;
		this.timeInterval = timeInterval;
	}
	
	
	public void apply() {
		modified = false;
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
	}
	
	
	protected abstract MarketImpl m();
	
	
	protected StockImpl getStock() {
		return m().c(stock);
	}
	
	
	protected Price getPriceAt(int row) {
		return (Price)getValueAt(row, 0);
	}
	
	
	protected TakenPrice getTakenPrice() {
		StockImpl s = getStock();
		if (s == null) return null;
		
		Price takenPrice = s.getTakenPrice(timeInterval);
		if (takenPrice == null)
			return null;
		else
			return takenPrice instanceof TakenPrice ? (TakenPrice)takenPrice : null;
	}
	
	
	@Override
	public void setValueAt(Object aValue, int row, int column) {
		super.setValueAt(aValue, row, column);
		modified = true;
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
	
	
	protected boolean editMode = false;
	
	
	protected boolean selectMode = false;
	
	
	protected Price selectPrice = null;

	
	public PriceList(Market market, Stock stock, long timeInterval, boolean editMode, boolean selectMode, Component parent) {
		super(Util.getFrameForComponent(parent), "Price list", true);
		this.tblPriceList = new PriceListTable(market, stock, timeInterval);
		this.editMode = editMode;
		this.selectMode = selectMode;
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(350, 250);
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
		
		JButton ok = new JButton(editMode ? (selectMode ? "Apply and select" : "Apply") : (selectMode ? "Select" : "OK"));
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
			
		}
		
		if (selectMode) {
			output = tblPriceList.getSelectedPrice();
		}
		
		dispose();
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
