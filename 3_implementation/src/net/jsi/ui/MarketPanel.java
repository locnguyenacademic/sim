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
import net.jsi.PriceImpl;
import net.jsi.QueryEstimator;
import net.jsi.Stock;
import net.jsi.StockAbstract;
import net.jsi.StockGroup;
import net.jsi.StockImpl;
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
		double balance = m.getBalance();
		double margin = m.getMargin(timeViewInterval);
		double freeMargin = m.getFreeMargin(timeViewInterval);
		double equity = margin + freeMargin;
		double profit = m.getProfit(timeViewInterval);
		double roi = m.getROIByLeverage(timeViewInterval);
		double invest = m.estimateInvestAmount(timeViewInterval);
		
		lblBalance.setText("Balance: " + Util.format(balance));
		lblEquity.setText("Equity: " + Util.format(equity));
		lblMargin.setText("Margin: " + Util.format(margin));
		lblFreeMargin.setText("Free margin: " + Util.format(freeMargin));
		lblMarginLevel.setText("Margin level: " + Util.format((margin != 0 ? equity / margin : 0)*100) + "%");
		
		lblProfit.setText("Profit: " + Util.format(profit));
		lblROI.setText("ROI: " + Util.format(roi*100) + "%");
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
	
	
	protected JFormattedTextField txtTakenDate;

	
	protected JFormattedTextField txtPrice;
	
	
	protected JFormattedTextField txtLowPrice;
	
	
	protected JFormattedTextField txtHighPrice;
	
	
	protected JFormattedTextField txtLastDate;

	
	protected JCheckBox chkLastDate;
	
	
	protected JPanel paneLastDate;

	
	protected JFormattedTextField txtUnitBias;

	
	protected JCheckBox chkUnitBias;

	
	protected JFormattedTextField txtStopLoss;
	
	
	protected JFormattedTextField txtTakeProfit;

	
	protected JCheckBox chkCommitted;

	
	protected JCheckBox chkSetPrice;

	
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
		setSize(600, 450);
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
		chkLeverage = new JCheckBox();
		chkLeverage.setSelected(false);
		chkLeverage.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				txtLeverage.setEnabled(chkLeverage.isSelected());
			}
		});
		paneLeverage.add(chkLeverage, BorderLayout.EAST);
	
		txtVolume = new JFormattedTextField(new NumberFormatter());
		txtVolume.setValue(update ? input.getVolume(0, false) : 1);
		right.add(txtVolume);

		txtTakenPrice = new JFormattedTextField(new NumberFormatter());
		txtTakenPrice.setValue(update ? input.getAverageTakenPrice(0) : 1);
		txtTakenPrice.setEnabled(false);
		if (update) right.add(txtTakenPrice);

		txtTakenDate = new JFormattedTextField(new SimpleDateFormat(Util.DATE_FORMAT));
		txtTakenDate.setValue(new Date(0));
		txtTakenDate.setEnabled(false);
		if (update) right.add(txtTakenDate);

		txtPrice = new JFormattedTextField(new NumberFormatter());
		txtPrice.setValue(update ? input.getPrice().get() : 1);
		right.add(txtPrice);
		
		txtLowPrice = new JFormattedTextField(new NumberFormatter());
		txtLowPrice.setValue(update ? input.getPrice().getLow() : 1);
		right.add(txtLowPrice);
		
		txtHighPrice = new JFormattedTextField(new NumberFormatter());
		txtHighPrice.setValue(update ? input.getPrice().getHigh() : 1);
		right.add(txtHighPrice);
		
		paneLastDate = new JPanel(new BorderLayout());
		right.add(paneLastDate);
		txtLastDate = new JFormattedTextField(new SimpleDateFormat(Util.DATE_FORMAT));
		txtLastDate.setValue(new Date(0));
		txtLastDate.setEnabled(false);
		paneLastDate.add(txtLastDate, BorderLayout.CENTER);
		chkLastDate = new JCheckBox();
		chkLastDate.setSelected(false);
		chkLastDate.setEnabled(update);
		chkLastDate.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				txtLastDate.setEnabled(chkLastDate.isSelected());
			}
		});
		paneLastDate.add(chkLastDate, BorderLayout.EAST);

		JPanel paneUnitBias = new JPanel(new BorderLayout());
		right.add(paneUnitBias);
		txtUnitBias = new JFormattedTextField(new NumberFormatter());
		txtUnitBias.setValue(0);
		txtUnitBias.setEnabled(false);
		paneUnitBias.add(txtUnitBias, BorderLayout.CENTER);
		chkUnitBias = new JCheckBox();
		chkUnitBias.setSelected(false);
		chkUnitBias.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				txtUnitBias.setEnabled(chkUnitBias.isSelected());
			}
		});
		paneUnitBias.add(chkUnitBias, BorderLayout.EAST);
		
		txtStopLoss = new JFormattedTextField(new NumberFormatter());
		txtStopLoss.setValue(0);
		txtStopLoss.setToolTipText("Value 0 specifies no effect");
		right.add(txtStopLoss);
		
		txtTakeProfit = new JFormattedTextField(new NumberFormatter());
		txtTakeProfit.setToolTipText("Value 0 specifies no effect");
		txtTakeProfit.setValue(0);
		right.add(txtTakeProfit);

		chkCommitted = new JCheckBox();
		chkCommitted.setSelected(update ? input.isCommitted() : false);
		chkCommitted.setEnabled(update);
		right.add(chkCommitted);

		JPanel paneSetPrice = new JPanel(new BorderLayout());
		if (update) right.add(paneSetPrice);
		chkSetPrice = new JCheckBox("Only set price");
		chkSetPrice.setSelected(update ? true : false);
		paneSetPrice.add(chkSetPrice, BorderLayout.EAST);
		
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
	
	
	private void update() {
		Universe u = market.getNearestUniverse();
		MarketImpl m = null;
		if (u != null) m = u.c(market);

		if (update) {
			StockImpl s = m.c(input);
			chkBuy.setEnabled(false);
			chkBuy.setSelected(s.isBuy());
			
			txtLeverage.setValue(input.getLeverage() == 0 ? 0 : 1.0 / input.getLeverage());

			txtTakenDate.setValue(s != null && s.getTakenPrice(0) != null ? s.getTakenPrice(0).getDate() : new Date(0));
			
			txtLastDate.setValue(s != null ? new Date(s.getPriceTimePoint()) : new Date(0));
			
			txtUnitBias.setValue(input.getUnitBias());
			
			if (s != null) {
				txtStopLoss.setValue(s.getStopLoss());
				txtTakeProfit.setValue(s.getTakeProfit());
			}
		}
		else {
			String code = cmbCode.getSelectedItem() != null ? cmbCode.getSelectedItem().toString() : null;
			StockGroup group = code != null && m != null ? m.get(code, input != null ? input.isBuy() : true) : null;
			
			chkBuy.setSelected(group != null ? group.isBuy() : (input != null ? input.isBuy() : true));
			
			double leverage = StockAbstract.LEVERAGE;
			double unitBias = StockAbstract.UNIT_BIAS;
			if (group != null) {
				leverage = group.getLeverage();
				unitBias = group.getUnitBias();
			}
			else if (m != null) {
				leverage = m.getRefLeverage();
				unitBias = StockAbstract.calcMaxUnitBias(StockAbstract.UNIT_BIAS, StockAbstract.LEVERAGE, leverage);
			}
			txtLeverage.setValue(leverage == 0 ? 0 : 1.0 / leverage);
			
			paneLastDate.setVisible(group != null);
			
			txtUnitBias.setValue(unitBias);
		}
		
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
			StockImpl stock = universe.c(input);
			if (stock == null || !stock.checkPriceTimePointPrevious(lastDate.getTime()))
				return false;
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
		
		MarketImpl m = null;
		Universe u = market.getNearestUniverse();
		if (u != null) m = u.c(market);
		if (m == null) return;
		
		String code = cmbCode.getSelectedItem().toString();
		PriceImpl price = new PriceImpl(
				((Number)txtPrice.getValue()).doubleValue(), 
				((Number) txtLowPrice.getValue()).doubleValue(),
				((Number) txtHighPrice.getValue()).doubleValue(),
				System.currentTimeMillis());
		
		double leverage = Double.NaN;
		if (chkLeverage.isSelected()) {
			double v = ((Number)txtLeverage.getValue()).doubleValue();
			leverage = v == 0 ? 0 : 1 / v;
		}

		if (update)
			output = input;
		else {
			output = m.addStock(code, chkBuy.isSelected(), leverage, ((Number)txtVolume.getValue()).doubleValue(), price);
			if (output == null) return;
		}
		
		StockImpl s = m.c(output);
		if (s == null) return;
		StockGroup group = m.get(s.code(), s.isBuy());
		if (group == null) return;
		
		if (update) {
			if (!chkSetPrice.isSelected()) {
				if (chkLastDate.isSelected()) s.setPriceTimePoint(((Date)txtLastDate.getValue()).getTime());
				if (!Double.isNaN(leverage)) group.setLeverage(leverage);
			}
			else {
				Price newPrice = (Price) price.clone();
				if (newPrice == null) return;
				if (chkLastDate.isSelected()) newPrice.setTime(((Date)txtLastDate.getValue()).getTime());
				if (!s.setPrice(newPrice)) return;
			}
		}
		
		if (!chkSetPrice.isSelected()) {
			s.setVolume(((Number)txtVolume.getValue()).doubleValue());
			s.getPrice().set(price.get());
			s.getPrice().setLow(price.getLow());
			s.getPrice().setHigh(price.getHigh());
			s.setStopLoss(((Number)txtStopLoss.getValue()).doubleValue());
			s.setTakeProfit(((Number)txtTakeProfit.getValue()).doubleValue());
			s.setCommitted(chkCommitted.isSelected());
			if (chkUnitBias.isSelected()) group.setUnitBias(((Number)txtUnitBias.getValue()).doubleValue());
		}
	
		JOptionPane.showMessageDialog(this, "Take/Modify successfully", "Take/Modify", JOptionPane.INFORMATION_MESSAGE);
		dispose();
	}
	
	
	public Stock getOutput() {
		return output;
	}


}



class MarketSummary extends JDialog {

	
	private static final long serialVersionUID = 1L;

	
	protected Market market = null;
	
	
	public MarketSummary(Market market, Component component) {
		super(UIUtil.getFrameForComponent(component), "Stock summary", true);
		this.market = market;
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setSize(600, 400);
		setLocationRelativeTo(Util.getFrameForComponent(component));
		setLayout(new BorderLayout());
		
		JPanel body = new JPanel(new BorderLayout());
		add(body, BorderLayout.CENTER);
		
		MarketTable tblGroup = new MarketTable(market, false);
		body.add(new JScrollPane(tblGroup), BorderLayout.CENTER);
		
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

		info.append("\n");
		info.append("Total estimated invest amount: " + Util.format(estimator.getInvestAmount(timeViewInterval)) + "\n");
		info.append("Recommended take amount: " + Util.format(estimator.estimateTakenAmount(timeViewInterval)) + "\n");
		info.append("Recommended take volume: " + Util.format(estimator.estimateTakenVolume(timeViewInterval)) + "\n");
		
		txtInfo.setText(info.toString());
	}
	
	
}


