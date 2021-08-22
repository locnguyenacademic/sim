package net.jsi.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;
import javax.swing.text.NumberFormatter;

import net.hudup.core.logistic.ui.UIUtil;
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
import net.jsi.Universe;
import net.jsi.Util;

public class MarketPanel extends JPanel implements MarketListener {


	private static final long serialVersionUID = 1L;

	
	protected JLabel lblBalance;
	
	
	protected JLabel lblEquity;

	
	protected JLabel lblMargin;

	
	protected JLabel lblFreeMargin;

	
	protected JLabel lblMarginLevel;

	
	protected JLabel lblProfit;

	
	protected JLabel lblROI;

	
	protected JLabel lblBias;

	
	protected JLabel lblInvest;

	
	protected MarketTable tblMarket = null;
	
	
	public MarketPanel(Market market) {
		tblMarket = new MarketTable(market, true);
		tblMarket.getModel2().addMarketListener(this);
		setLayout(new BorderLayout());
		
		JPanel header = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		add(header, BorderLayout.NORTH);
		
		JButton take = new JButton("Take new");
		take.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Stock stock = tblMarket.getSelectedStock();
				take(stock, false);
			}
		});
		header.add(take);
		
		MarketPanel thisPanel = this;
		JButton summary = new JButton("Summary");
		summary.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new MarketSummary(getMarket(), thisPanel).setVisible(true);
			}
		});
		header.add(summary);

		
		JPanel body = new JPanel(new BorderLayout());
		add(body, BorderLayout.CENTER);
		body.add(new JScrollPane(tblMarket), BorderLayout.CENTER);
		
		
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
		footerRow2.add(lblInvest = new JLabel());
		
		update();
	}
	
	
	public Market getMarket() {
		return tblMarket.getMarket();
	}
	
	
	public MarketTable getMarketTable() {
		return tblMarket;
	}
	
	
	private void take(Stock input, boolean modify) {
		if (modify && input == null) return;
		StockTaker taker = new StockTaker(getMarket(), input, modify, this);
		taker.setVisible(true);
		if (taker.getOutput() != null) tblMarket.update();
	}
	
	
	private void update() {
		Market m = getMarket();

		long timeViewInterval = m.getTimeViewInterval();
		double balance = m.getBalance(timeViewInterval);
		double margin = m.getMargin(timeViewInterval);
		double freeMargin = m.getFreeMargin(timeViewInterval);
		double equity = margin + freeMargin;
		double profit = m.getProfit(timeViewInterval);
		double roi = m.getROIByLeverage(timeViewInterval);
		double bias = getMarket().calcTotalBias(timeViewInterval);
		double invest = m.calcInvestAmount(timeViewInterval);
		
		lblBalance.setText("Balance: " + Util.format(balance));
		lblEquity.setText("Equity: " + Util.format(equity));
		lblMargin.setText("Margin: " + Util.format(margin));
		lblFreeMargin.setText("Free margin: " + Util.format(freeMargin));
		lblMarginLevel.setText("Margin level: " + Util.format((margin != 0 ? equity / margin : 0)*100) + "%");
		
		lblProfit.setText("Profit: " + Util.format(profit));
		lblROI.setText("ROI: " + Util.format(roi*100) + "%");
		lblBias.setText("Bias: " + Util.format(bias));
		lblInvest.setText("Rec invest: " + Util.format(invest));
	}


	@Override
	public void notify(MarketEvent evt) {
		update();
	}


}



class StockTaker extends JDialog {

	
	private static final long serialVersionUID = 1L;
	
	
	protected JComboBox<String> cmbCode;
	
	
	protected JCheckBox chkBuy;
	
	
	protected JFormattedTextField txtLeverage;

	
	protected JCheckBox chkLeverage;

	
	protected JFormattedTextField txtVolume;
	
	
	protected JFormattedTextField txtTakenPrice;
	
	
	protected JButton btnTakenPrice;

	
	protected JFormattedTextField txtTakenDate;
	
	
	protected JButton btnTakenDate;
	
	
	protected JFormattedTextField txtPrice;
	
	
	protected JButton btnPrice;

	
	protected JFormattedTextField txtLowPrice;
	
	
	protected JButton btnLowPrice;
	
	
	protected JFormattedTextField txtHighPrice;
	
	
	protected JButton btnHighPrice;
	
	
	protected JFormattedTextField txtLastDate;
	
	
	protected JCheckBox chkLastDate;
	
	
	protected JButton btnLastDate;
			
	
	protected JPanel paneLastDate;

	
	protected JFormattedTextField txtUnitBias;

	
	protected JCheckBox chkUnitBias;

	
	protected JButton btnUnitBias;

	
	protected JFormattedTextField txtStopLoss;
	
	
	protected JButton btnStopLoss;

	
	protected JFormattedTextField txtTakeProfit;

	
	protected JButton btnTakeProfit;

	
	protected StockPropertyTextField txtProperty;

	
	protected JCheckBox chkProperty;

	
	protected JButton btnProperty;

	
	protected JCheckBox chkCommitted;

	
	protected JCheckBox chkAddPrice;

	
	protected Market market = null;
	
	
	protected Stock output = null;
	
	
	protected Stock input = null;

	
	protected boolean update = false;
	
	
	public StockTaker(Market market, Stock input, boolean update, Component parent) {
		super(Util.getFrameForComponent(parent), "Stock taker: " + (update ? "Update" : "Add new"), true);
		this.market = market;
		this.input = input;
		this.update = update;
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(500, 500);
		setLocationRelativeTo(Util.getFrameForComponent(parent));
		setLayout(new BorderLayout());
		
		JPanel header = new JPanel(new BorderLayout());
		add(header, BorderLayout.NORTH);
		
		JPanel left = new JPanel(new GridLayout(0, 1));
		header.add(left, BorderLayout.WEST);
		
		left.add(new JLabel("Code (*): "));
		left.add(new JLabel("Buy (*): "));
		left.add(new JLabel("Leverage: "));
		left.add(new JLabel("Volume (*): "));
		if (update) left.add(new JLabel("Taken price: "));
		if (update) left.add(new JLabel("Taken date: "));
		left.add(new JLabel("Price (*): "));
		left.add(new JLabel("Low price (*): "));
		left.add(new JLabel("High price (*): "));
		left.add(new JLabel("Last date: "));
		left.add(new JLabel("Unit bias: "));
		left.add(new JLabel("Stop loss: "));
		left.add(new JLabel("Take profit: "));
		left.add(new JLabel("Property: "));
		left.add(new JLabel("Committed: "));
		if (update) left.add(new JLabel(""));
		
		JPanel right = new JPanel(new GridLayout(0, 1));
		header.add(right, BorderLayout.CENTER);
		
		List<String> codes = Util.newList(0);
		Universe universe = market.getNearestUniverse();
		if (universe != null) codes.addAll(universe.getSupportStockCodes());
		cmbCode = new JComboBox<String>(codes.toArray(new String[] {}));
		if (update) {
			cmbCode.setSelectedItem(input.code());
			cmbCode.setEnabled(false);
		}
		else if (input != null) {
			cmbCode.setSelectedItem(input.code());
		}
		cmbCode.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				update();
			}
		});
		right.add(cmbCode);
		
		chkBuy = new JCheckBox();
		chkBuy.setSelected(input != null ? input.isBuy() : true);
		chkBuy.setEnabled(!update);
		right.add(chkBuy);
		
		JPanel paneLeverage = new JPanel(new BorderLayout());
		right.add(paneLeverage);
		txtLeverage = new JFormattedTextField(new NumberFormatter());
		txtLeverage.setValue(0);
		txtLeverage.setEnabled(false);
		txtLeverage.setToolTipText("Value 0 specified infinity leverage");
		paneLeverage.add(txtLeverage, BorderLayout.CENTER);
		//
		chkLeverage = new JCheckBox();
		chkLeverage.setSelected(false);
		chkLeverage.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				txtLeverage.setEnabled(chkLeverage.isSelected());
			}
		});
		paneLeverage.add(chkLeverage, BorderLayout.WEST);
	
		txtVolume = new JFormattedTextField(new NumberFormatter());
		txtVolume.setValue(update ? input.getVolume(0, false) : 1);
		right.add(txtVolume);

		JPanel paneTakenPrice = new JPanel(new BorderLayout());
		if (update) right.add(paneTakenPrice);
		txtTakenPrice = new JFormattedTextField(new NumberFormatter());
		txtTakenPrice.setValue(update ? input.getAverageTakenPrice(0) : 1);
		txtTakenPrice.setEnabled(false);
		paneTakenPrice.add(txtTakenPrice, BorderLayout.CENTER);
		//
		btnTakenPrice = new JButton("Set");
		btnTakenPrice.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setTakenPrice();
			}
		});
		btnTakenPrice.setEnabled(update);
		paneTakenPrice.add(btnTakenPrice, BorderLayout.EAST);
		
		JPanel paneTakenDate = new JPanel(new BorderLayout());
		if (update) right.add(paneTakenDate);
		txtTakenDate = new JFormattedTextField(new SimpleDateFormat(Util.DATE_FORMAT));
		txtTakenDate.setValue(new Date(0));
		txtTakenDate.setEnabled(false);
		paneTakenDate.add(txtTakenDate, BorderLayout.CENTER);
		//
		btnTakenDate = new JButton("Set");
		btnTakenDate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setTakenPrice();
			}
		});
		btnTakenDate.setEnabled(update);
		paneTakenDate.add(btnTakenDate, BorderLayout.EAST);

		JPanel panePrice = new JPanel(new BorderLayout());
		right.add(panePrice);
		txtPrice = new JFormattedTextField(new NumberFormatter());
		txtPrice.setValue(update ? input.getPrice().get() : 1);
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
		txtLowPrice.setValue(update ? input.getPrice().getLow() : 1);
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
		txtHighPrice.setValue(update ? input.getPrice().getHigh() : 1);
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
		
		paneLastDate = new JPanel(new BorderLayout());
		right.add(paneLastDate);
		txtLastDate = new JFormattedTextField(new SimpleDateFormat(Util.DATE_FORMAT));
		txtLastDate.setValue(new Date(0));
		txtLastDate.setEnabled(false);
		paneLastDate.add(txtLastDate, BorderLayout.CENTER);
		//
		chkLastDate = new JCheckBox();
		chkLastDate.setSelected(false);
		chkLastDate.setEnabled(update);
		chkLastDate.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				txtLastDate.setEnabled(chkLastDate.isSelected() && update);
			}
		});
		paneLastDate.add(chkLastDate, BorderLayout.WEST);
		//
		btnLastDate = new JButton("List");
		btnLastDate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listPrices();
			}
		});
		btnLastDate.setEnabled(false);
		paneLastDate.add(btnLastDate, BorderLayout.EAST);

		JPanel paneUnitBias = new JPanel(new BorderLayout());
		right.add(paneUnitBias);
		btnUnitBias = new JButton("Estimate");
		btnUnitBias.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Estimator estimator = getEstimator();
				if (estimator != null)  txtUnitBias.setValue(estimator.estimateUnitBias(market.getTimeViewInterval()));
			}
		});
		btnUnitBias.setEnabled(false);
		paneUnitBias.add(btnUnitBias, BorderLayout.EAST);
		//
		txtUnitBias = new JFormattedTextField(new NumberFormatter());
		txtUnitBias.setValue(0);
		txtUnitBias.setEnabled(false);
		paneUnitBias.add(txtUnitBias, BorderLayout.CENTER);
		//
		chkUnitBias = new JCheckBox();
		chkUnitBias.setSelected(false);
		chkUnitBias.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				txtUnitBias.setEnabled(chkUnitBias.isSelected());
				btnUnitBias.setEnabled(getStockGroup() != null && chkUnitBias.isSelected());
			}
		});
		paneUnitBias.add(chkUnitBias, BorderLayout.WEST);
		
		JPanel paneStopLoss = new JPanel(new BorderLayout());
		right.add(paneStopLoss);
		txtStopLoss = new JFormattedTextField(new NumberFormatter());
		txtStopLoss.setValue(0);
		txtStopLoss.setToolTipText("Value 0 specifies no effect");
		paneStopLoss.add(txtStopLoss, BorderLayout.CENTER);
		//
		btnStopLoss = new JButton("Estimate");
		btnStopLoss.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Estimator estimator = getEstimator();
				if (estimator != null)  txtStopLoss.setValue(estimator.estimateStopLoss(market.getTimeViewInterval()));
			}
		});
		paneStopLoss.add(btnStopLoss, BorderLayout.EAST);
		
		JPanel paneTakeProfit = new JPanel(new BorderLayout());
		right.add(paneTakeProfit);
		txtTakeProfit = new JFormattedTextField(new NumberFormatter());
		txtTakeProfit.setToolTipText("Value 0 specifies no effect");
		txtTakeProfit.setValue(0);
		paneTakeProfit.add(txtTakeProfit, BorderLayout.CENTER);
		//
		btnTakeProfit = new JButton("Estimate");
		btnTakeProfit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Estimator estimator = getEstimator();
				if (estimator != null)  txtTakeProfit.setValue(estimator.estimateTakeProfit(market.getTimeViewInterval()));
			}
		});
		paneTakeProfit.add(btnTakeProfit, BorderLayout.EAST);

		JPanel paneProperty = new JPanel(new BorderLayout());
		right.add(paneProperty);
		//
		btnProperty = new JButton("Modify");
		btnProperty.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				txtProperty.modify();
			}
		});
		btnProperty.setEnabled(false);
		paneProperty.add(btnProperty, BorderLayout.EAST);
		//
		txtProperty = new StockPropertyTextField();
		if (input != null) txtProperty.setStockProperty(input.getProperty());
		txtProperty.setEnabled(false);
		paneProperty.add(txtProperty, BorderLayout.CENTER);
		//
		chkProperty = new JCheckBox();
		chkProperty.setSelected(false);
		chkProperty.setEnabled(input != null);
		chkProperty.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				txtProperty.setEnabled(chkProperty.isSelected() && input != null);
				btnProperty.setEnabled(chkProperty.isSelected() && input != null);
			}
		});
		paneProperty.add(chkProperty, BorderLayout.WEST);
		
		chkCommitted = new JCheckBox();
		chkCommitted.setSelected(update ? input.isCommitted() : false);
		chkCommitted.setEnabled(update);
		right.add(chkCommitted);

		JPanel paneAddPrice = new JPanel(new BorderLayout());
		if (update) right.add(paneAddPrice);
		chkAddPrice = new JCheckBox("Add price only");
		chkAddPrice.setSelected(false);
		paneAddPrice.add(chkAddPrice, BorderLayout.EAST);
		
		
		JPanel footer = new JPanel();
		add(footer, BorderLayout.SOUTH);
		
		JButton ok = new JButton(update ? "Update" : "Add new");
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
		
		update();
	}
	
	
	private MarketImpl m() {
		Universe u = market.getNearestUniverse();
		return u != null ? u.c(market) : null;
	}

	private StockImpl getInputStock() {
		MarketImpl m = m();
		return m != null ? m.c(input) : null;
	}
	
	
	private StockGroup getStockGroup() {
		MarketImpl m = m();
		String code = cmbCode.getSelectedItem() != null ? cmbCode.getSelectedItem().toString() : null;
		return code != null && m != null ? m.get(code, input != null ? input.isBuy() : true) : null;
	}
	
	
	private Estimator getEstimator() {
		StockGroup group = getStockGroup();
		if (group == null)
			return null;
		else {
			MarketImpl m = m();
			return m != null ? m.getEstimator(group.code(), group.isBuy()) : null;
		}
	}
	
	
	private void update() {
		StockImpl s = getInputStock();

		if (update) {
			chkBuy.setEnabled(false);
			chkBuy.setSelected(s.isBuy());
			
			txtLeverage.setValue(input.getLeverage() == 0 ? 0 : 1.0 / input.getLeverage());

			txtTakenDate.setValue(s.getTakenPrice(0) != null ? s.getTakenPrice(0).getDate() : new Date());
			
			txtLastDate.setValue(new Date(s.getPriceTimePoint()));
			btnLastDate.setEnabled(true);
			paneLastDate.setVisible(true);
			
			txtUnitBias.setValue(input.getUnitBias());
			
			txtPrice.setValue(s.getPrice().get());
			txtLowPrice.setValue(s.getPrice().getLow());
			txtHighPrice.setValue(s.getPrice().getHigh());
			
			txtStopLoss.setValue(s.getStopLoss());
			txtTakeProfit.setValue(s.getTakeProfit());
		}
		else {
			StockGroup group = getStockGroup();
			chkBuy.setSelected(group != null ? group.isBuy() : (input != null ? input.isBuy() : true));
			
			double leverage = StockProperty.LEVERAGE;
			double unitBias = StockProperty.UNIT_BIAS;
			if (group != null) {
				leverage = group.getLeverage();
				unitBias = group.getUnitBias();
			}
			else if (m() != null) {
				leverage = m().getRefLeverage();
				unitBias = StockAbstract.calcMaxUnitBias(StockProperty.UNIT_BIAS, StockProperty.LEVERAGE, leverage);
			}
			txtLeverage.setValue(leverage == 0 ? 0 : 1.0 / leverage);
			
			txtLastDate.setValue(group != null ? new Date(group.getPriceTimePoint()) : new Date());
			btnLastDate.setEnabled(group != null);
			paneLastDate.setVisible(group != null);
			
			txtUnitBias.setValue(unitBias);
			
			txtPrice.setValue(group != null ? group.getPrice().get() : 1);
			txtLowPrice.setValue(group != null ? group.getPrice().getLow() : 1);
			txtHighPrice.setValue(group != null ? group.getPrice().getHigh() : 1);
			
			txtStopLoss.setValue(s != null && group != null && s.code().equals(group.code()) ? s.getStopLoss() : 0);
			txtTakeProfit.setValue(s != null && group != null && s.code().equals(group.code()) ? s.getTakeProfit() : 0);
		}
		
	}
	
	
	private void listPrices() {
		
	}
	
	
	private void setTakenPrice() {
		
	}
	
	
	private boolean validateInput() {
		Object code = cmbCode.getSelectedItem();
		if (code == null) return false;
		
		if (chkLeverage.isSelected()) {
			if (!(txtLeverage.getValue() instanceof Number)) return false;
			double leverage = ((Number)txtLeverage.getValue()).doubleValue();
			if (leverage < 0) return false;
		}
		
		double volume = txtVolume.getValue() instanceof Number ? ((Number)txtVolume.getValue()).doubleValue() : 0;
		if (volume <= 0) return false;
		
		double takenPrice = txtTakenPrice.getValue() instanceof Number ? ((Number)txtTakenPrice.getValue()).doubleValue() : 0;
		if (takenPrice <= 0) return false;
		
		double price = txtPrice.getValue() instanceof Number ? ((Number)txtPrice.getValue()).doubleValue() : 0;
		if (price < 0) return false;

		double lowPrice = txtLowPrice.getValue() instanceof Number ? ((Number)txtLowPrice.getValue()).doubleValue() : 0;
		if (lowPrice < 0) return false;

		double highPrice = txtHighPrice.getValue() instanceof Number ? ((Number)txtHighPrice.getValue()).doubleValue() : 0;
		if (highPrice < 0) return false;
		
		if (price < lowPrice || price > highPrice) return false;
		
		if (chkLastDate.isSelected()) {
			Date lastDate = txtLastDate.getValue() instanceof Date ? (Date)txtLastDate.getValue() : null;
			Universe universe = market.getNearestUniverse();
			if (lastDate == null || input == null || universe == null) return false;
			StockImpl s = universe.c(input);
			if (s == null)
				return false;
			else if (chkAddPrice.isSelected())
				return s.checkPriceTimePoint(lastDate.getTime());
			else
				return s.checkPriceTimePointPrevious(lastDate.getTime());
		}
		
		double unitBias = txtUnitBias.getValue() instanceof Number ? ((Number)txtUnitBias.getValue()).doubleValue() : 0;
		if (unitBias < 0) return false;

		if (!(txtStopLoss.getValue() instanceof Number)) return false;
		if (!(txtTakeProfit.getValue() instanceof Number)) return false;

		
		return true;
	}
	
	
	private void ok() {
		if (!validateInput()) {
			JOptionPane.showMessageDialog(this, "Invalid input", "Invalid input", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		MarketImpl m = m();
		if (m == null) return;
		
		String code = cmbCode.getSelectedItem().toString();
		long lastTime = ((Date)txtLastDate.getValue()).getTime();
		Price price = m.newPrice(
				((Number)txtPrice.getValue()).doubleValue(), 
				((Number) txtLowPrice.getValue()).doubleValue(),
				((Number) txtHighPrice.getValue()).doubleValue(),
				lastTime);
		
		double leverage = Double.NaN;
		if (chkLeverage.isSelected()) {
			double v = ((Number)txtLeverage.getValue()).doubleValue();
			leverage = v == 0 ? 0 : 1 / v;
		}

		Stock output = null;
		if (update)
			output = input;
		else {
			output = m.addStock(code, chkBuy.isSelected(), leverage, ((Number)txtVolume.getValue()).doubleValue(), price);
			if (output == null) return;
		}
		
		StockImpl s = m.c(output); if (s == null) return;
		StockGroup group = m.get(s.code(), s.isBuy());
		if (group == null) return;
		
		if (update) {
			if (!chkAddPrice.isSelected()) {
				long takenTime =  ((Date)txtTakenDate.getValue()).getTime();
				Price p = group.getPrice(); if (p == null) return;
				if (p.getTime() == takenTime) {
					Price newPrice = (Price) price.clone(); if (newPrice == null) return;
					if (!s.setPrice(newPrice)) return;
				}
				else if (chkLastDate.isSelected()) {
					s.setPriceTimePoint(lastTime);
				}
				
				if (!Double.isNaN(leverage)) group.setLeverage(leverage);
				s.setCommitted(chkCommitted.isSelected());
			}
			else {
				Price newPrice = (Price) price.clone(); if (newPrice == null) return;
				if (!s.setPrice(newPrice)) return;
			}
		}
		else if (!chkAddPrice.isSelected()) {
			if (group.size() == 1 && txtProperty.getStockProperty() != null) {
				s.getProperty().set(txtProperty.getStockProperty());
			}
		}
		
		
		if (!chkAddPrice.isSelected()) {
			s.setVolume(((Number)txtVolume.getValue()).doubleValue());
			s.getPrice().set(price.get());
			s.getPrice().setLow(price.getLow());
			s.getPrice().setHigh(price.getHigh());
			s.setStopLoss(((Number)txtStopLoss.getValue()).doubleValue());
			s.setTakeProfit(((Number)txtTakeProfit.getValue()).doubleValue());
			if (chkUnitBias.isSelected()) group.setUnitBias(((Number)txtUnitBias.getValue()).doubleValue());
		}
	
		
		this.output = output;
		JOptionPane.showMessageDialog(this, "Add new / update successfully", "Add new / update", JOptionPane.INFORMATION_MESSAGE);
		dispose();
	}
	
	
	public Stock getOutput() {
		return output;
	}


}



class MarketSummary extends JDialog {

	
	private static final long serialVersionUID = 1L;

	
	protected Market market = null;
	
	
	protected MarketTable tblMarket = null;
	
	
	public MarketSummary(Market market, Component component) {
		super(UIUtil.getFrameForComponent(component), "Stock summary", true);
		this.market = market;
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setSize(600, 400);
		setLocationRelativeTo(Util.getFrameForComponent(component));
		setLayout(new BorderLayout());
		
		JPanel body = new JPanel(new BorderLayout());
		add(body, BorderLayout.CENTER);
		
		tblMarket = new MarketTable(market, false);
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
		super(UIUtil.getFrameForComponent(component), "Stock group summary", true);
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
		info.append("Total estimated invest amount: " + Util.format(estimator.getInvestAmount(timeViewInterval)) + "\n");
		info.append("Recommended take amount: " + Util.format(estimator.estimateTakenAmount(timeViewInterval)) + "\n");
		info.append("Recommended take volume: " + Util.format(estimator.estimateTakenVolume(timeViewInterval)) + "\n");
		
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
	
	
	protected JButton btnLastDate;
			
	
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
		txtLastDate.setValue(input.getPrice().getDate());
		paneLastDate.add(txtLastDate, BorderLayout.CENTER);
		//
		btnLastDate = new JButton("List");
		btnLastDate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listPrices();
			}
		});
		paneLastDate.add(btnLastDate, BorderLayout.EAST);
		
		
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

