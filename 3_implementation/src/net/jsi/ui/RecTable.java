package net.jsi.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import net.jsi.Estimator;
import net.jsi.EstimatorAbstract;
import net.jsi.Market;
import net.jsi.MarketImpl;
import net.jsi.Price;
import net.jsi.PricePool;
import net.jsi.PricePool.TakenStockPrice;
import net.jsi.Stock;
import net.jsi.StockImpl;
import net.jsi.StockInfo;
import net.jsi.StockProperty;
import net.jsi.Universe;
import net.jsi.Util;
import net.jsi.ui.RecTableModel.InvestBy;

public class RecTable extends JTable {

	
	private static final long serialVersionUID = 1L;

	
	public RecTable(Market market, long timeInterval, MarketListener listener) {
		setModel(new RecTableModel(market));
		getModel2().addMarketListener(listener);

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
			}
		});

		update(true, timeInterval);
	}


	private boolean place0(InvestBy invest) {
		MarketImpl m = getModel2().m();
		if (m == null) return false;
		StockInfo info = m.getStore().get(invest.code);
		if (info == null) return false;
		MarketImpl placeMarket = m.getPlaceMarket();
		if (placeMarket == null) return false;
		
		Price lastPrice = placeMarket.getStore().getLastPrice(invest.code);
		
		Price price1 = placeMarket.newPrice(invest.invests[0].price, invest.invests[0].lowPrice, invest.invests[0].highPrice, System.currentTimeMillis());
		if (lastPrice != null)
			price1.setTime(Math.max(price1.getTime(), lastPrice.getTime() + StockProperty.TIME_UPDATE_PRICE_INTERVAL));
		Stock added1 = (StockImpl)placeMarket.addStock(invest.code, invest.buy, info.getLeverage(), invest.invests[0].volume, price1, Double.NaN);
		if (added1 == null || !(added1 instanceof StockImpl)) return false;
		added1.setUnitBias(invest.invests[0].unitBias);
		placeMarket.c(added1).setStopLoss(invest.invests[0].stopLoss);
		placeMarket.c(added1).setTakeProfit(invest.invests[0].takeProfit);
		
		lastPrice = placeMarket.getStore().getLastPrice(invest.code);
		Price price2 = placeMarket.newPrice(invest.invests[1].price, invest.invests[1].lowPrice, invest.invests[1].highPrice, System.currentTimeMillis());
		if (lastPrice != null)
			price2.setTime(Math.max(price2.getTime(), lastPrice.getTime() + StockProperty.TIME_UPDATE_PRICE_INTERVAL));
		Stock added2 = placeMarket.addStock(invest.code, invest.buy, info.getLeverage(), invest.invests[1].volume, price2, Double.NaN);
		if (added2 == null || !(added2 instanceof StockImpl)) return false;
		added2.setUnitBias(invest.invests[1].unitBias);
		placeMarket.c(added2).setStopLoss(invest.invests[1].stopLoss);
		placeMarket.c(added2).setTakeProfit(invest.invests[1].largeTakeProfit);

		return added1 != null && added2 != null;
	}
	
	
	private void place() {
		List<InvestBy> invests= getSelectedInvests();
		boolean ret = false;
		for (InvestBy invest : invests) {
			if (invest == null) continue;
			boolean ret0 = place0(invest);
			ret = ret || ret0;
		}
		
		if (ret) {
			MarketImpl m = getModel2().m();
			if (m == null)
				return;
			else {
				m.apply();
				update();
				JOptionPane.showMessageDialog(Util.getDialogForComponent(this), "Place recommended stocks successfully", "Successful placing", JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}
	
	
	protected JPopupMenu createContextMenu() {
		if (getSelectedRowCount() <= 0) return null;
		
		JPopupMenu ctxMenu = new JPopupMenu();
		
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
	
	
	protected RecTableModel getModel2() {
		return (RecTableModel) getModel();
	}
	
	
	protected boolean isBuy() {
		return getModel2().buy;
	}
	
	
	public void update(boolean buy, long timeInterval) {
		getModel2().update(buy, timeInterval);
		init();
	}

	
	public void update() {
		int selectedRow = getSelectedRow();

		getModel2().update();
		init();
		
		if (selectedRow >= 0 && selectedRow < getRowCount()) {try {setRowSelectionInterval(selectedRow, selectedRow);} catch (Throwable e) {}}
	}

	
	private void init() {
		if (getColumnModel().getColumnCount() > 0) {
			getColumnModel().getColumn(0).setMaxWidth(0);
			getColumnModel().getColumn(0).setMinWidth(0);
			getColumnModel().getColumn(0).setPreferredWidth(0);
		}
	}

	
	protected List<InvestBy> getSelectedInvests() {
		int[] selectedRows = getSelectedRows();
		if (selectedRows == null || selectedRows.length == 0) return Util.newList(0);
		
		List<InvestBy> invests = Util.newList(selectedRows.length);
		for (int selectedRow : selectedRows) {
			InvestBy investBy = getModel2().getInvestAt(selectedRow);
			if (investBy != null) invests.add(investBy);
		}
		
		return invests;
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


	@Override
	public String getToolTipText(MouseEvent event) {
		return super.getToolTipText(event);
	}


}



class RecTableModel extends DefaultTableModel {

	
	private static final long serialVersionUID = 1L;

	
	protected Market market = null;
	
	
	protected boolean buy = true;
	
	
	private long timeInterval = StockProperty.TIME_VIEW_INTERVAL;
	
	
	protected double investAmount = 0;
	
	
	protected double investedAmount = 0;

	
	private Map<String, Double> unitBiases = Util.newMap(0);

	
	private double positiveROISum = Double.NaN;
	

	public RecTableModel(Market market) {
		this.market =market;
	}
	
    
	protected MarketImpl m() {
		if (market == null)
			return null;
		else {
			Universe u = market.getNearestUniverse();
			return u != null ? u.c(market) : null;
		}
	}
	
	
    protected double getInvestAmount() {
    	return investAmount;
    }
    
	
    protected double getInvestedAmount() {
    	return investedAmount;
    }

    
	protected double getInvestedVolume() {
		double volumeSum = 0;
		for (int row = 0; row < getRowCount(); row++) {
			InvestBy invest = getInvestAt(row);
			volumeSum += invest.invests[0].volume + invest.invests[1].volume;
		}
		
		return volumeSum;
	}

	
    protected double getUnitBias(String code, long timeInterval) {
		if (unitBiases.containsKey(code))
			return unitBiases.get(code);
		else {
			StockInfo info = m().getStore().get(code);
			List<Price> prices = info != null ? info.getPrices(timeInterval) : Util.newList(0);
			if (prices.size() == 0) return 0;
			
			double bias = EstimatorAbstract.estimateUnitBiasFromData(prices);
			unitBiases.put(code, bias);
			return bias;
		}
	}
	
	
	protected double getBiasSum() {
		double biasSum = 0;
		for (int row = 0; row < getRowCount(); row++) {
			InvestBy invest = getInvestAt(row);
			if (unitBiases.containsKey(invest.code))
				biasSum += unitBiases.get(invest.code) * (invest.invests[0].volume + invest.invests[1].volume);
		}
		
		return biasSum;
	}
	

	protected double getROI(String code, long timeInterval) {
		StockInfo info = m().getStore().get(code);
		Price price = info != null ? info.getFirstPriceWithin(timeInterval) : null;
		if (price == null)
			return 0;
		else
			return (info.getLastPrice().get() - price.get()) / price.get();
	}

	
	protected double getPositiveROISum(long timeInterval) {
		if (!Double.isNaN(positiveROISum)) return positiveROISum;
		
		Set<String> codes = m().getStore().codes();
		double roiSum = 0;
		for (String code : codes) {
			double roi = getROI(code, timeInterval);
			if (roi > 0) roiSum += roi;
		}
		
		return this.positiveROISum = roiSum;
	}
	
	
	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}

	
	public void update(boolean buy, long timeInterval) {
		this.buy = buy;
		this.timeInterval = timeInterval;
		MarketImpl m = m();
		if (m != null) {
			this.investAmount = m.calcInvestAmount(timeInterval);
			MarketImpl placeMarket = m.getPlaceMarket();
			if (placeMarket != null)
				this.investAmount -= placeMarket.getMargin(timeInterval);
		}
		else
			this.investAmount = 0;
		
		Vector<Vector<Object>> data = Util.newVector(0);
		List<String> codes = Util.newList(0);
		codes.addAll(m().getStore().codes());
		Collections.sort(codes);

		investedAmount = 0;
		for (String code : codes) {
			Vector<Object> row = toRow(code, timeInterval);
			if (row != null) data.add(row);
		}
		
		for (int row = 0; row < data.size(); row++) {
			try {
				MarketTableModel.Percentage percent = (MarketTableModel.Percentage)data.get(row).get(3);
				double margin1 = (Double)data.get(row).get(9);
				double margin2 = (Double)data.get(row).get(14);
				percent.v = (margin1 + margin2) / investAmount;
			}
			catch (Exception e) {
				Util.trace(e);
			}
		}
		
		setDataVector(data, toColumns());
		
		fireMarketEvent(new MarketEvent(this));
	}
	
	
	protected void update() {
		update(buy, timeInterval);
	}
	
	
	protected class InvestBy {
		
		public String code = null;
		
		public boolean buy = true;
		
		public Estimator.Invest[] invests = null;
		
		public InvestBy(String code, boolean buy, Estimator.Invest[] invests) {
			this.code = code;
			this.buy = buy;
			this.invests = invests;
		}
		
	}
	
	
	protected InvestBy getInvestAt(int row) {
		return (InvestBy)getValueAt(row, 0);
	}
	
	
	private String getCategory(String code) {
		StockInfo si = m().getStore().get(code);
		if (si == null)
			return StockProperty.CATEGORY_UNDEFINED;
		else
			return si.getProperty().getCategory();
	}
	
	
	private Vector<Object> toRow(String code, long timeInterval) {
		Vector<Object> row = Util.newVector(0);
		StockInfo info = m().getStore().get(code);
		if (info == null || info.getPriceCount() == 0) return null;
		
		Estimator estimator = createEstimator(code, timeInterval);
		if (estimator == null) return null;
		Estimator.Invest[] invests = estimator.estimateDualInvest(timeInterval);
		if (invests == null || invests.length < 2) return null;
		if (invests[0].volume <= 0.5) return null;

		InvestBy investBy = new InvestBy(code, buy, invests);
		row.add(investBy);
		row.add(code);
		row.add(getCategory(code));
		row.add(new MarketTableModel.Percentage(0, Util.DECIMAL_PRECISION_SHORT));
		double leverage = info.getLeverage() != 0 ? 1.0/info.getLeverage() : 0;
		row.add(leverage);
		row.add(info.getLastPrice().get());
		row.add(estimator.estimateUnitBias(timeInterval));
		
		row.add(invests[0].volume);
		row.add(invests[0].price);
		row.add(invests[0].margin);
		row.add(invests[0].stopLoss);
		row.add(invests[0].takeProfit);
		
		row.add(invests[1].volume);
		row.add(invests[1].price);
		row.add(invests[1].margin);
		row.add(invests[1].stopLoss);
		row.add(invests[1].takeProfit);
		row.add(invests[1].largeTakeProfit);
		
		investedAmount += invests[0].margin + invests[1].margin;
		return row;
	}
	
	
	/**
	 * Getting list of column names.
	 * @return list of column names.
	 */
	private Vector<String> toColumns() {
		Vector<String> columns = Util.newVector(0);
		
		columns.add("");
		columns.add("Code");
		columns.add("Category");
		columns.add("Percent");
		columns.add("Leverage");
		columns.add("Price (current)");
		columns.add("Unit bias (est.)");
		
		columns.add("1.Volume");
		columns.add("1.Taken price");
		columns.add("1.Margin");
		columns.add("1.Stop loss");
		columns.add("1.Take profit");
		
		columns.add("2.Volume");
		columns.add("2.Taken price");
		columns.add("2.Margin");
		columns.add("2.Stop loss");
		columns.add("2.Take profit");
		columns.add("2.Take profit (large)");
		
		return columns;
	}


	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == 0 || columnIndex == 1 || columnIndex == 2)
			return super.getColumnClass(columnIndex);
		else if (columnIndex == 3)
			return MarketTableModel.Percentage.class;
		else
			return Double.class;
	}


	protected Estimator createEstimator(String code, long timeInterval) {
		RecTableModel thisModel = this;
		return new EstimatorAbstract() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isBuy() {
				return buy;
			}
			
			@Override
			public double getUnitBias() {
				return thisModel.getUnitBias(code, timeInterval);
			}
			
			@Override
			public double getROI(long timeInterval) {
				return thisModel.getROI(code, timeInterval);
			}
			
			@Override
			public List<Price> getPrices(long timeInterval) {
				StockInfo info = m().getStore().get(code);
				return info != null ? info.getPrices(timeInterval) : Util.newList(0);
			}
			
			@Override
			public Price getPrice() {
				StockInfo info = m().getStore().get(code);
				return info != null && info.getPriceCount() > 0 ? info.getLastPrice() : null;
			}
			
			@Override
			public double getPositiveROISum(long timeInterval) {
				return thisModel.getPositiveROISum(timeInterval);
			}
			
			@Override
			public double getLeverage() {
				StockInfo info = m().getStore().get(code);
				return info != null ? info.getLeverage() : StockProperty.LEVERAGE;
			}
			
			@Override
			public double getInvestAmount(long timeInterval) {
				return thisModel.getInvestAmount();
			}
			
			@Override
			public double getAverageTakenPrice(long timeInterval) {
				StockInfo info = m().getStore().get(code);
				if (info == null || info.getPricePool() == null) return 0;
				
				PricePool pool = info.getPricePool();
				double takenSum = 0;
				int n = 0;
				for (int i = 0; i < pool.size(); i++) {
					Price price = pool.getByIndex(i);
					List<TakenStockPrice> takenPrices = PricePool.getTakenPrices(pool.code(), price, market.getNearestUniverse(), timeInterval);
					for (TakenStockPrice takenPrice : takenPrices) {
						takenSum += takenPrice.takenPrice.get();
						n++;
					}
				}
				
				return n != 0 ? takenSum/(double)n : 0;
			}
			
		};
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


}



class RecPanel extends JPanel implements MarketListener {


	private static final long serialVersionUID = 1L;

	
	protected JCheckBox chkBuy;

	
	protected JLabel lblBias;

	
	protected JLabel lblEstInvest;

	
	protected JLabel lblInvest;

	
	protected RecTable tblRec;
	
	
	protected long timeInterval = StockProperty.TIME_VIEW_INTERVAL;

	
	public RecPanel(Market market, long timeInterval, MarketListener superListener) {
		this.timeInterval = timeInterval;
		tblRec = createRecTable(market, timeInterval, this);
		if (superListener != null) tblRec.getModel2().addMarketListener(superListener);

		setLayout(new BorderLayout());
		
		JPanel header = new JPanel(new BorderLayout());
		add(header, BorderLayout.NORTH);
		
		chkBuy = new JCheckBox("Buy", tblRec.isBuy());
		header.add(chkBuy);
		chkBuy .addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (tblRec.isBuy() != chkBuy.isSelected()) tblRec.update(chkBuy.isSelected(), timeInterval);
			}
		});
		
		JPanel body = new JPanel(new BorderLayout());
		add(body, BorderLayout.CENTER);
		body.add(new JScrollPane(tblRec), BorderLayout.CENTER);

		JPanel footer = new JPanel(new BorderLayout());
		add(footer, BorderLayout.SOUTH);
		
		JPanel footerRow1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		footer.add(footerRow1, BorderLayout.NORTH);

		footerRow1.add(lblEstInvest = new JLabel());
		footerRow1.add(new JLabel(" "));
		footerRow1.add(lblInvest = new JLabel());
		footerRow1.add(new JLabel(" "));
		footerRow1.add(lblBias = new JLabel());
		
		update();
	}
	
	
	protected void update() {
		int d = Util.DECIMAL_PRECISION_SHORT;
		RecTableModel m = tblRec.getModel2();
		lblEstInvest.setText("INVEST: " + Util.format(m.getInvestAmount(), d));
		lblInvest.setText("Invested: " + Util.format(m.getInvestedAmount(), d) + " (" + Util.format(m.getInvestedVolume(), d) + ") / " + Util.format(m.investedAmount/m.investAmount*100, d) + "%");
		lblBias.setText("Bias: " + Util.format(m.getBiasSum(), d));
	}
	
	
	@Override
	public void notify(MarketEvent evt) {
		update();
	}


	protected RecTable createRecTable(Market market, long timeInterval, MarketListener listener) {
		return new RecTable(market, timeInterval, listener);
	}
	
	
}



class RecDialog extends JDialog {

	
	private static final long serialVersionUID = 1L;
	
	
	protected JButton btnOK;
	
	
	protected JButton btnCancel;
	
	
	protected boolean isPressOK = false;
	
	
	public RecDialog(Market market, long timeInterval, MarketListener superListener, Component parent) {
		super(Util.getDialogForComponent(parent), "Recommended stocks", true);
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		setSize(600, 400);
		setLocationRelativeTo(Util.getDialogForComponent(parent));
		
		setLayout(new BorderLayout());

		JPanel body = new JPanel(new BorderLayout());
		add(body, BorderLayout.CENTER);
		RecPanel mp = createRecPanel(market, timeInterval, superListener);
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

		btnCancel = new JButton("Close");
		btnCancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		footer.add(btnCancel);
		
	}
	
	
	protected RecPanel createRecPanel(Market market, long timeInterval, MarketListener superListener) {
		return new RecPanel(market, timeInterval, superListener);
	}
	
	
	public boolean isPressOK() {
		return isPressOK;
	}
	
	
}




