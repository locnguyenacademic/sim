package net.jsi.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

import net.jsi.Estimator;
import net.jsi.Market;
import net.jsi.MarketImpl;
import net.jsi.Price;
import net.jsi.Stock;
import net.jsi.StockAbstract;
import net.jsi.StockGroup;
import net.jsi.StockImpl;
import net.jsi.StockProperty;
import net.jsi.Universe;
import net.jsi.Util;

public class StockTaker extends JDialog {

	
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
	
	
	protected JFormattedTextField txtAltPrice;
	
	
	protected JButton btnAltPrice;
	
	
	protected JFormattedTextField txtLastDate;
	
	
	protected JCheckBox chkLastDate;
	
	
	protected JButton btnLastDateNow;
	
	
	protected JButton btnLastDateList;
	
	
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
	
	
	Component parent = null;

	
	protected Market market = null;
	
	
	protected Stock output = null;
	
	
	protected Stock input = null;

	
	protected boolean update = false;
	
	
	public StockTaker(Market market, Stock input, boolean update, Component parent) {
		super(Util.getFrameForComponent(parent), "Stock taker: " + (update ? "Update" : "Add new"), true);
		this.market = market;
		this.input = input;
		this.update = update;
		this.parent = parent;
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(500, 550);
		setLocationRelativeTo(Util.getFrameForComponent(parent));
	    setJMenuBar(createMenuBar());

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
		//left.add(new JLabel("Alt price: "));
		left.add(new JLabel("Last date: "));
		left.add(new JLabel("Unit bias: "));
		left.add(new JLabel("Stop loss: "));
		left.add(new JLabel("Take profit: "));
		left.add(new JLabel("Property: "));
		left.add(new JLabel("Committed: "));
		if (update) left.add(new JLabel(""));
		
		JPanel right = new JPanel(new GridLayout(0, 1));
		header.add(right, BorderLayout.CENTER);
		
		cmbCode = new JComboBox<String>(market.getSupportStockCodes().toArray(new String[] {}));
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
		txtLeverage = new JFormattedTextField(Util.getNumberFormatter());
		txtLeverage.setValue(0);
		txtLeverage.setEditable(false);
		txtLeverage.setToolTipText("Value 0 specified infinity leverage");
		paneLeverage.add(txtLeverage, BorderLayout.CENTER);
		//
		chkLeverage = new JCheckBox();
		chkLeverage.setSelected(false);
		chkLeverage.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				txtLeverage.setEditable(chkLeverage.isSelected());
			}
		});
		paneLeverage.add(chkLeverage, BorderLayout.WEST);
	
		txtVolume = new JFormattedTextField(Util.getNumberFormatter());
		txtVolume.setValue(update ? input.getVolume(0, false) : 1.0);
		right.add(txtVolume);

		JPanel paneTakenPrice = new JPanel(new BorderLayout());
		if (update) right.add(paneTakenPrice);
		txtTakenPrice = new JFormattedTextField(Util.getNumberFormatter());
		txtTakenPrice.setValue(1.0);
		if (update && m() != null) {
			try {
				StockImpl s = m().c(input);
				if (s != null) txtTakenPrice.setValue(s.getAverageTakenPrice(0));
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		txtTakenPrice.setEditable(false);
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
//		paneTakenPrice.add(btnTakenPrice, BorderLayout.EAST);
		
		JPanel paneTakenDate = new JPanel(new BorderLayout());
		if (update) right.add(paneTakenDate);
		txtTakenDate = new JFormattedTextField(Util.getDateFormatter());
		txtTakenDate.setValue(new Date(0));
		txtTakenDate.setEditable(false);
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
		txtPrice = new JFormattedTextField(Util.getNumberFormatter());
		txtPrice.setValue(update ? input.getPrice().get() : 1.0);
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
		txtLowPrice.setValue(update ? input.getPrice().getLow() : 1.0);
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
		txtHighPrice.setValue(update ? input.getPrice().getHigh() : 1.0);
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
		txtAltPrice.setValue(update ? input.getPrice().getAlt() : 1.0);
		paneAltPrice.add(txtAltPrice, BorderLayout.CENTER);
		//
		btnAltPrice = new JButton("Estimate");
		btnAltPrice.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

			}
		});
		paneAltPrice.add(btnAltPrice, BorderLayout.EAST);

		paneLastDate = new JPanel(new BorderLayout());
		right.add(paneLastDate);
		txtLastDate = new JFormattedTextField(Util.getDateFormatter());
		txtLastDate.setValue(new Date());
		txtLastDate.setEditable(false);
		paneLastDate.add(txtLastDate, BorderLayout.CENTER);
		//
		chkLastDate = new JCheckBox();
		chkLastDate.setSelected(false);
		chkLastDate.setEnabled(true);
		chkLastDate.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				txtLastDate.setEditable(chkLastDate.isSelected());
				btnLastDateNow.setEnabled(chkLastDate.isSelected());
			}
		});
		paneLastDate.add(chkLastDate, BorderLayout.WEST);
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
		btnLastDateNow.setEnabled(false);
		paneLastDate2.add(btnLastDateNow);
		//
		btnLastDateList = new JButton("Price list");
		btnLastDateList.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				priceList();
			}
		});
		btnLastDateList.setEnabled(true);
		paneLastDate2.add(btnLastDateList);

		JPanel paneUnitBias = new JPanel(new BorderLayout());
		right.add(paneUnitBias);
		btnUnitBias = new JButton("Estimate");
		btnUnitBias.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Estimator estimator = getEstimator();
				if (estimator != null)  txtUnitBias.setValue(estimator.estimateUnitBiasFromData(market.getTimeViewInterval()));
			}
		});
		btnUnitBias.setEnabled(false);
		paneUnitBias.add(btnUnitBias, BorderLayout.EAST);
		//
		txtUnitBias = new JFormattedTextField(Util.getNumberFormatter());
		txtUnitBias.setValue(0);
		txtUnitBias.setEditable(false);
		paneUnitBias.add(txtUnitBias, BorderLayout.CENTER);
		//
		chkUnitBias = new JCheckBox();
		chkUnitBias.setSelected(false);
		chkUnitBias.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				txtUnitBias.setEditable(chkUnitBias.isSelected());
				btnUnitBias.setEnabled(getStockGroup() != null && chkUnitBias.isSelected());
			}
		});
		paneUnitBias.add(chkUnitBias, BorderLayout.WEST);
		
		JPanel paneStopLoss = new JPanel(new BorderLayout());
		right.add(paneStopLoss);
		txtStopLoss = new JFormattedTextField(Util.getNumberFormatter());
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
		txtTakeProfit = new JFormattedTextField(Util.getNumberFormatter());
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
				if (input == null) return;
				StockProperty output = txtProperty.modify();
				if (output != null) txtProperty.setStockProperty(output);
			}
		});
		btnProperty.setEnabled(false);
		paneProperty.add(btnProperty, BorderLayout.EAST);
		//
		txtProperty = new StockPropertyTextField();
		if (input != null) txtProperty.setStockProperty(input.getProperty());
		txtProperty.setEditable(false);
		paneProperty.add(txtProperty, BorderLayout.CENTER);
		//
		chkProperty = new JCheckBox();
		chkProperty.setSelected(false);
		chkProperty.setEnabled(input != null);
		chkProperty.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				txtProperty.setEditable(chkProperty.isSelected() && input != null);
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
		chkAddPrice.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				StockImpl s = getInputStock();
				if (s == null) return;
				long timePoint = s.getPriceTimePoint();
				if (chkAddPrice.isSelected())
					txtLastDate.setValue(new Date(timePoint + StockProperty.TIME_UPDATE_PRICE_INTERVAL));
				else
					txtLastDate.setValue(new Date(timePoint));
			}
		});
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
	
	
	private JMenuBar createMenuBar() {
		JMenuBar mnBar = new JMenuBar();
		
		JMenu mnTool = new JMenu("Tool");
		mnTool.setMnemonic('t');
		mnBar.add(mnTool);

		StockTaker taker = this;
		JMenuItem mniSwitch = new JMenuItem(
			new AbstractAction("Switch to selector") {
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					taker.dispose();
					StockSelector selector = new StockSelector(market, input, update, parent);
					selector.setVisible(true);
					taker.setOutput(selector.getOutput());
				}
			});
		mniSwitch.setMnemonic('w');
		mniSwitch.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_DOWN_MASK));
		mnTool.add(mniSwitch);
		
		return mnBar;
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
		return getStockGroup(input != null ? input.isBuy() : true);
	}
	
	
	private StockGroup getStockGroup(boolean buy) {
		MarketImpl m = m();
		String code = cmbCode.getSelectedItem() != null ? cmbCode.getSelectedItem().toString() : null;
		return code != null && m != null ? m.get(code, buy) : null;
	}

	
	@SuppressWarnings("unused")
	private StockGroup getDualStockGroup() {
		return getDualStockGroup(input != null ? input.isBuy() : true);
	}
	
	
	private StockGroup getDualStockGroup(boolean buy) {
		StockGroup group = getStockGroup(buy);
		if (group != null) return group.getDualGroup();
		
		String code = cmbCode.getSelectedItem() != null ? cmbCode.getSelectedItem().toString() : null;
		if (code == null) return null;
		
		MarketImpl m = m();
		Market dualMarket = m != null ? m.getDualMarket() : null;
		if (dualMarket == null) return null;
		
		Universe u = dualMarket.getNearestUniverse();
		MarketImpl dm = u != null ? u.c(dualMarket) : null;
		return dm != null ? dm.get(code, buy) : null;
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

			Price takenPrice = s.getTakenPrice(market.getTimeViewInterval());
			txtTakenPrice.setValue(takenPrice != null ? takenPrice.get() : 1.0);
			txtTakenDate.setValue(takenPrice != null ? takenPrice.getDate() : new Date());
			
			txtLastDate.setValue(new Date(s.getPriceTimePoint()));
			btnLastDateList.setEnabled(true);
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
				leverage = m().getLeverage();
				unitBias = StockAbstract.calcMaxUnitBias(StockProperty.UNIT_BIAS, StockProperty.LEVERAGE, leverage);
			}
			txtLeverage.setValue(leverage == 0 ? 0 : 1.0 / leverage);
			
			txtLastDate.setValue(group != null ? new Date(group.getPriceTimePoint() + StockProperty.TIME_UPDATE_PRICE_INTERVAL) : new Date());
			btnLastDateList.setEnabled(group != null);
			paneLastDate.setVisible(group != null);
			
			txtTakenPrice.setValue(group != null ? group.getPrice().get() : 1.0);

			txtUnitBias.setValue(unitBias);
			
			txtPrice.setValue(group != null ? group.getPrice().get() : 1.0);
			txtLowPrice.setValue(group != null ? group.getPrice().getLow() : 1.0);
			txtHighPrice.setValue(group != null ? group.getPrice().getHigh() : 1.0);
			
			txtStopLoss.setValue(s != null && group != null && s.code().equals(group.code()) ? s.getStopLoss() : 0);
			txtTakeProfit.setValue(s != null && group != null && s.code().equals(group.code()) ? s.getTakeProfit() : 0);
		}
		
	}
	
	
	private void priceList() {
		String code = cmbCode.getSelectedItem() != null ? cmbCode.getSelectedItem().toString() : null;
		if (code == null) return;
		Stock input = this.input;
		if (this.input == null || !this.input.code().equals(code))
			input = getStockGroup(chkBuy.isSelected());
		if (input == null) return;
		
		PriceListPartial pl = new PriceListPartial(market, input, market.getTimeViewInterval(), update, false, this);
		pl.setVisible(true);
		if (!update || !pl.isPressOK()) return;
		
		Price price = input.getPrice();
		if (price == null) return;

		txtPrice.setValue(price.get());
		txtLowPrice.setValue(price.getLow());
		txtHighPrice.setValue(price.getHigh());
		txtLastDate.setValue(new Date(price.getTime()));

		StockImpl s = m().c(input);
		if (s == null) return;
		Price takenPrice = s.getTakenPrice(market.getTimeViewInterval());
		if (takenPrice != null) {
			txtTakenPrice.setValue(takenPrice.get());
			txtTakenDate.setValue(new Date(takenPrice.getTime()));
		}
		
		Component parent = getParent0();
		if (parent == null)
			return;
		else if (parent instanceof MarketPanel)
			((MarketPanel)parent).getMarketTable().update();
		else if (parent instanceof MarketTable)
			((MarketTable)parent).update();
	}
	
	
	private void setTakenPrice() {
		String code = cmbCode.getSelectedItem() != null ? cmbCode.getSelectedItem().toString() : null;
		if (code == null) return;
		Stock input = this.input;
		if (this.input == null || !this.input.code().equals(code))
			input = getStockGroup(chkBuy.isSelected());
		if (input == null) return;

		PriceListPartial pl = new PriceListPartial(market, input, m().getTimeViewInterval(), false, true, this);
		pl.setVisible(true);
		if (pl.getOutput() != null) {
			txtTakenPrice.setValue(pl.getOutput().get());
			txtTakenDate.setValue(new Date(pl.getOutput().getTime()));
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
		
		double price = txtPrice.getValue() instanceof Number ? ((Number)txtPrice.getValue()).doubleValue() : 0;
		if (price < 0) return false;

		double lowPrice = txtLowPrice.getValue() instanceof Number ? ((Number)txtLowPrice.getValue()).doubleValue() : 0;
		if (lowPrice < 0) return false;

		double highPrice = txtHighPrice.getValue() instanceof Number ? ((Number)txtHighPrice.getValue()).doubleValue() : 0;
		if (highPrice < 0) return false;
		
		if (price < lowPrice || price > highPrice) return false;
		
		Date lastDate = txtLastDate.getValue() instanceof Date ? (Date)txtLastDate.getValue() : null;
		StockImpl s = getInputStock();
		if (lastDate == null)
			return false;
		else if (chkAddPrice.isSelected()) {
			if (s == null) return false;
		}
		else if (update) {
			if (s == null) return false;
		}
		else if (s != null) { //add new

		}
		else {

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
				long takenTimePoint = ((Date)txtTakenDate.getValue()).getTime();
				s.take(market.getTimeViewInterval(), takenTimePoint);
				
				if (chkLastDate.isSelected()) s.setPriceTimePoint(lastTime);
				
				if (!Double.isNaN(leverage)) group.setLeverage(leverage);
				s.setCommitted(chkCommitted.isSelected());
				
			}
			else {
				if (!s.setPrice(price)) return;
			}
		}
		else if (!chkAddPrice.isSelected()) {

		}
		
		
		if (!chkAddPrice.isSelected()) {
			s.setVolume(((Number)txtVolume.getValue()).doubleValue());
			s.getPrice().set(price.get());
			s.getPrice().setLow(price.getLow());
			s.getPrice().setHigh(price.getHigh());
			s.setStopLoss(((Number)txtStopLoss.getValue()).doubleValue());
			s.setTakeProfit(((Number)txtTakeProfit.getValue()).doubleValue());
			if (chkUnitBias.isSelected()) group.setUnitBias(((Number)txtUnitBias.getValue()).doubleValue());
			
			if (chkProperty.isSelected() && txtProperty.getStockProperty() != null) {
				s.getProperty().set(txtProperty.getStockProperty());
			}
		}
	
		
		this.output = output;
		JOptionPane.showMessageDialog(this, "Add new / update successfully", "Add new / update", JOptionPane.INFORMATION_MESSAGE);
		dispose();
	}
	
	
	public Stock getOutput() {
		return output;
	}


	protected void setOutput(Stock output) {
		this.output = output;
	}

	
	private Component getParent0() {
		return parent;
	}
	
	
}



class StockPropertySetting extends JDialog {
	
	
	private static final long serialVersionUID = 1L;
	
	
	protected StockProperty output = null;
	
	
	public StockPropertySetting(StockProperty property, Component comp) {
		super(Util.getFrameForComponent(comp), "Settings stock property (this function not completed for permanant storing yet)", true);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setSize(300, 250);
		setLocationRelativeTo(Util.getFrameForComponent(this));
		setLayout(new BorderLayout());
		
		
		JPanel header = new JPanel(new BorderLayout());
		add(header, BorderLayout.NORTH);
		
		JPanel left = new JPanel(new GridLayout(0, 1));
		header.add(left, BorderLayout.WEST);
		
		left.add(new JLabel("Maxium price count: "));
		left.add(new JLabel("Swap: "));
		left.add(new JLabel("Spread: "));
		left.add(new JLabel("Commission: "));
		left.add(new JLabel("Price ratio: "));
		left.add(new JLabel("Price update interval (days): "));
		
		JPanel right = new JPanel(new GridLayout(0, 1));
		header.add(right, BorderLayout.CENTER);
		
		JPanel paneMaxPriceCount = new JPanel(new BorderLayout());
		right.add(paneMaxPriceCount);
		JFormattedTextField txtMaxPriceCount = new JFormattedTextField(Util.getNumberFormatter());
		txtMaxPriceCount.setValue(property.maxPriceCount);
		paneMaxPriceCount.add(txtMaxPriceCount, BorderLayout.CENTER);
		
		JPanel paneSwap = new JPanel(new BorderLayout());
		right.add(paneSwap);
		JFormattedTextField txtSwap = new JFormattedTextField(Util.getNumberFormatter());
		txtSwap.setValue(property.swap);
		paneSwap.add(txtSwap, BorderLayout.CENTER);
		
		JPanel paneSpread = new JPanel(new BorderLayout());
		right.add(paneSpread);
		JFormattedTextField txtSpread = new JFormattedTextField(Util.getNumberFormatter());
		txtSpread.setValue(property.spread);
		paneSpread.add(txtSpread, BorderLayout.CENTER);

		JPanel paneCommission = new JPanel(new BorderLayout());
		right.add(paneCommission);
		JFormattedTextField txtCommission = new JFormattedTextField(Util.getNumberFormatter());
		txtCommission.setValue(property.commission);
		paneCommission.add(txtCommission, BorderLayout.CENTER);
		
		JPanel panePriceRatio = new JPanel(new BorderLayout());
		right.add(panePriceRatio);
		JFormattedTextField txtPriceRatio = new JFormattedTextField(Util.getNumberFormatter());
		txtPriceRatio.setValue(property.priceRatio);
		panePriceRatio.add(txtPriceRatio, BorderLayout.CENTER);
		
		JPanel panePriceUpdateInterval = new JPanel(new BorderLayout());
		right.add(panePriceUpdateInterval);
		JFormattedTextField txtTimePriceUpdateInterval = new JFormattedTextField(Util.getNumberFormatter());
		txtTimePriceUpdateInterval.setValue(property.timeUpdatePriceInterval / (1000*3600*24));
		panePriceUpdateInterval.add(txtTimePriceUpdateInterval, BorderLayout.CENTER);
		
		
		JPanel body = new JPanel(new BorderLayout());
		add(body, BorderLayout.CENTER);
		JPanel paneMoreProperties = new JPanel(new BorderLayout());
		body.add(paneMoreProperties, BorderLayout.CENTER);
		//
		paneMoreProperties.add(new JLabel("More properties (,): "), BorderLayout.WEST);
		//
		JTextArea txtMoreProperties = new JTextArea();
		txtMoreProperties.setLineWrap(true);
		txtMoreProperties.setToolTipText("Pairs \"key=value\" are separated by a comma or a new line character");
		txtMoreProperties.setText(property.getMorePropertiesText());
		paneMoreProperties.add(new JScrollPane(txtMoreProperties), BorderLayout.CENTER);

		
		JPanel footer = new JPanel();
		add(footer, BorderLayout.SOUTH);
		
		JButton ok = new JButton("OK");
		ok.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int maxPriceCount = txtMaxPriceCount.getValue() instanceof Number ? ((Number)txtMaxPriceCount.getValue()).intValue() : StockProperty.MAX_PRICE_COUNT;
				double swap = txtSwap.getValue() instanceof Number ? ((Number)txtSwap.getValue()).doubleValue() : 0;
				double spread = txtSpread.getValue() instanceof Number ? ((Number)txtSpread.getValue()).doubleValue() : 0;
				double commission = txtCommission.getValue() instanceof Number ? ((Number)txtCommission.getValue()).doubleValue() : 0;
				double priceRatio = txtPriceRatio.getValue() instanceof Number ? ((Number)txtPriceRatio.getValue()).doubleValue() : 0;
				long timePriceUpdateInterval = txtTimePriceUpdateInterval.getValue() instanceof Number ? ((Number)txtTimePriceUpdateInterval.getValue()).longValue() : 0;
				timePriceUpdateInterval = timePriceUpdateInterval*1000*3600*24;
				timePriceUpdateInterval = timePriceUpdateInterval <= 0 ? StockProperty.TIME_UPDATE_PRICE_INTERVAL : timePriceUpdateInterval;
				
				output = new StockProperty();
				output.maxPriceCount = Math.max(maxPriceCount, StockProperty.MAX_PRICE_COUNT);
				output.swap = swap;
				output.spread = spread;
				output.commission = commission;
				output.priceRatio = priceRatio;
				output.timeUpdatePriceInterval = timePriceUpdateInterval;
				output.setMorePropertiesText(txtMoreProperties.getText());
				
				dispose();
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
	
	
	public StockProperty getOutput() {
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
class StockPropertyTextField extends JTextField {

	
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
	
	
	public StockProperty modify() {
		if (property == null) return null;
		StockPropertySetting setting = new StockPropertySetting(property, this);
		setting.setVisible(true);
		
		return setting.getOutput();
	}
	
	
}



class StockSelector extends JDialog {

	
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
	
	
	protected JFormattedTextField txtLowPrice;
	
	
	protected JFormattedTextField txtHighPrice;
	
	
	protected JFormattedTextField txtAltPrice;

	
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

	
	Component parent = null;

	
	protected Market market = null;
	
	
	protected Stock output = null;
	
	
	protected Stock input = null;

	
	protected boolean update = false;
	
	
	public StockSelector(Market market, Stock input, boolean update, Component parent) {
		super(Util.getFrameForComponent(parent), "Stock selector: " + (update ? "Update" : "Add new"), true);
		this.market = market;
		this.input = input;
		this.update = update;
		this.parent = parent;
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(500, 550);
		setLocationRelativeTo(Util.getFrameForComponent(parent));
	    setJMenuBar(createMenuBar());
		
		setLayout(new BorderLayout());
		
		JPanel header = new JPanel(new BorderLayout());
		add(header, BorderLayout.NORTH);
		
		JPanel left = new JPanel(new GridLayout(0, 1));
		header.add(left, BorderLayout.WEST);
		
		left.add(new JLabel("Code (*): "));
		left.add(new JLabel("Buy (*): "));
		left.add(new JLabel("Leverage: "));
		left.add(new JLabel("Volume (*): "));
		left.add(new JLabel("Taken price: "));
		left.add(new JLabel("Taken date (*): "));
		left.add(new JLabel("Low price: "));
		left.add(new JLabel("High price: "));
		left.add(new JLabel("Alt price: "));
		left.add(new JLabel("Unit bias: "));
		left.add(new JLabel("Stop loss: "));
		left.add(new JLabel("Take profit: "));
		left.add(new JLabel("Property: "));
		left.add(new JLabel("Committed: "));
		
		JPanel right = new JPanel(new GridLayout(0, 1));
		header.add(right, BorderLayout.CENTER);
		
		cmbCode = new JComboBox<String>(market.getSupportStockCodes().toArray(new String[] {}));
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
		txtLeverage = new JFormattedTextField(Util.getNumberFormatter());
		txtLeverage.setValue(0);
		txtLeverage.setEditable(false);
		txtLeverage.setToolTipText("Value 0 specified infinity leverage");
		paneLeverage.add(txtLeverage, BorderLayout.CENTER);
		//
		chkLeverage = new JCheckBox();
		chkLeverage.setSelected(false);
		chkLeverage.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				txtLeverage.setEditable(chkLeverage.isSelected());
			}
		});
		paneLeverage.add(chkLeverage, BorderLayout.WEST);
	
		txtVolume = new JFormattedTextField(Util.getNumberFormatter());
		txtVolume.setValue(update ? input.getVolume(0, false) : 1.0);
		right.add(txtVolume);

		JPanel paneTakenPrice = new JPanel(new BorderLayout());
		right.add(paneTakenPrice);
		txtTakenPrice = new JFormattedTextField(Util.getNumberFormatter());
		txtTakenPrice.setValue(1.0);
		if (update && m() != null) {
			try {
				StockImpl s = m().c(input);
				if (s != null) txtTakenPrice.setValue(s.getAverageTakenPrice(0));
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		txtTakenPrice.setEditable(false);
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
//		paneTakenPrice.add(btnTakenPrice, BorderLayout.EAST);
		
		JPanel paneTakenDate = new JPanel(new BorderLayout());
		right.add(paneTakenDate);
		txtTakenDate = new JFormattedTextField(Util.getDateFormatter());
		txtTakenDate.setValue(new Date(0));
		txtTakenDate.setEditable(false);
		paneTakenDate.add(txtTakenDate, BorderLayout.CENTER);
		//
		btnTakenDate = new JButton("Set");
		btnTakenDate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setTakenPrice();
			}
		});
		paneTakenDate.add(btnTakenDate, BorderLayout.EAST);

		JPanel paneLowPrice = new JPanel(new BorderLayout());
		right.add(paneLowPrice);
		txtLowPrice = new JFormattedTextField(Util.getNumberFormatter());
		txtLowPrice.setValue(update ? input.getPrice().getLow() : 1.0);
		txtLowPrice.setEditable(false);
		paneLowPrice.add(txtLowPrice, BorderLayout.CENTER);
		
		JPanel paneHighPrice = new JPanel(new BorderLayout());
		right.add(paneHighPrice);
		txtHighPrice = new JFormattedTextField(Util.getNumberFormatter());
		txtHighPrice.setValue(update ? input.getPrice().getHigh() : 1.0);
		txtHighPrice.setEditable(false);
		paneHighPrice.add(txtHighPrice, BorderLayout.CENTER);
		
		JPanel paneAltPrice = new JPanel(new BorderLayout());
		right.add(paneAltPrice);
		txtAltPrice = new JFormattedTextField(Util.getNumberFormatter());
		txtAltPrice.setValue(update ? input.getPrice().getAlt() : 1.0);
		txtAltPrice.setEditable(false);
		paneAltPrice.add(txtAltPrice, BorderLayout.CENTER);

		JPanel paneUnitBias = new JPanel(new BorderLayout());
		right.add(paneUnitBias);
		btnUnitBias = new JButton("Estimate");
		btnUnitBias.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Estimator estimator = getEstimator();
				if (estimator != null)  txtUnitBias.setValue(estimator.estimateUnitBiasFromData(market.getTimeViewInterval()));
			}
		});
		btnUnitBias.setEnabled(false);
		paneUnitBias.add(btnUnitBias, BorderLayout.EAST);
		//
		txtUnitBias = new JFormattedTextField(Util.getNumberFormatter());
		txtUnitBias.setValue(0);
		txtUnitBias.setEditable(false);
		paneUnitBias.add(txtUnitBias, BorderLayout.CENTER);
		//
		chkUnitBias = new JCheckBox();
		chkUnitBias.setSelected(false);
		chkUnitBias.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				txtUnitBias.setEditable(chkUnitBias.isSelected());
				btnUnitBias.setEnabled(getStockGroup() != null && chkUnitBias.isSelected());
			}
		});
		paneUnitBias.add(chkUnitBias, BorderLayout.WEST);
		
		JPanel paneStopLoss = new JPanel(new BorderLayout());
		right.add(paneStopLoss);
		txtStopLoss = new JFormattedTextField(Util.getNumberFormatter());
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
		txtTakeProfit = new JFormattedTextField(Util.getNumberFormatter());
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
				if (input == null) return;
				StockProperty output = txtProperty.modify();
				if (output != null) txtProperty.setStockProperty(output);
			}
		});
		btnProperty.setEnabled(false);
		paneProperty.add(btnProperty, BorderLayout.EAST);
		//
		txtProperty = new StockPropertyTextField();
		if (input != null) txtProperty.setStockProperty(input.getProperty());
		txtProperty.setEditable(false);
		paneProperty.add(txtProperty, BorderLayout.CENTER);
		//
		chkProperty = new JCheckBox();
		chkProperty.setSelected(false);
		chkProperty.setEnabled(input != null);
		chkProperty.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				txtProperty.setEditable(chkProperty.isSelected() && input != null);
				btnProperty.setEnabled(chkProperty.isSelected() && input != null);
			}
		});
		paneProperty.add(chkProperty, BorderLayout.WEST);
		
		chkCommitted = new JCheckBox();
		chkCommitted.setSelected(update ? input.isCommitted() : false);
		chkCommitted.setEnabled(update);
		right.add(chkCommitted);

		
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
	
	
	private JMenuBar createMenuBar() {
		JMenuBar mnBar = new JMenuBar();
		
		JMenu mnTool = new JMenu("Tool");
		mnTool.setMnemonic('t');
		mnBar.add(mnTool);

		StockSelector selector = this;
		JMenuItem mniSwitch = new JMenuItem(
			new AbstractAction("Switch to taker") {
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					selector.dispose();
					StockTaker taker = new StockTaker(market, input, update, parent);
					taker.setVisible(true);
					selector.setOutput(taker.getOutput());
				}
			});
		mniSwitch.setMnemonic('w');
		mniSwitch.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_DOWN_MASK));
		mnTool.add(mniSwitch);
		
		return mnBar;
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
		return getStockGroup(input != null ? input.isBuy() : true);
	}
	
	
	private StockGroup getStockGroup(boolean buy) {
		MarketImpl m = m();
		String code = cmbCode.getSelectedItem() != null ? cmbCode.getSelectedItem().toString() : null;
		return code != null && m != null ? m.get(code, buy) : null;
	}

	
	@SuppressWarnings("unused")
	private StockGroup getDualStockGroup() {
		return getDualStockGroup(input != null ? input.isBuy() : true);
	}
	
	
	private StockGroup getDualStockGroup(boolean buy) {
		StockGroup group = getStockGroup(buy);
		if (group != null) return group.getDualGroup();
		
		String code = cmbCode.getSelectedItem() != null ? cmbCode.getSelectedItem().toString() : null;
		if (code == null) return null;
		
		MarketImpl m = m();
		Market dualMarket = m != null ? m.getDualMarket() : null;
		if (dualMarket == null) return null;
		
		Universe u = dualMarket.getNearestUniverse();
		MarketImpl dm = u != null ? u.c(dualMarket) : null;
		return dm != null ? dm.get(code, buy) : null;
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

			Price takenPrice = s.getTakenPrice(market.getTimeViewInterval());
			txtTakenPrice.setValue(takenPrice != null ? takenPrice.get() : 1.0);
			txtTakenDate.setValue(takenPrice != null ? takenPrice.getDate() : new Date());
			
			txtUnitBias.setValue(input.getUnitBias());
			
			txtLowPrice.setValue(takenPrice.getLow());
			txtHighPrice.setValue(takenPrice.getHigh());
			
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
				leverage = m().getLeverage();
				unitBias = StockAbstract.calcMaxUnitBias(StockProperty.UNIT_BIAS, StockProperty.LEVERAGE, leverage);
			}
			txtLeverage.setValue(leverage == 0 ? 0 : 1.0 / leverage);
			
			txtTakenPrice.setValue(group != null ? group.getPrice().get() : 1.0);

			txtUnitBias.setValue(unitBias);
			
			txtLowPrice.setValue(group != null ? group.getPrice().getLow() : 1.0);
			txtHighPrice.setValue(group != null ? group.getPrice().getHigh() : 1.0);
			
			txtStopLoss.setValue(s != null && group != null && s.code().equals(group.code()) ? s.getStopLoss() : 0);
			txtTakeProfit.setValue(s != null && group != null && s.code().equals(group.code()) ? s.getTakeProfit() : 0);
		}
		
	}
	
	
	private void setTakenPrice() {
		String code = cmbCode.getSelectedItem() != null ? cmbCode.getSelectedItem().toString() : null;
		if (code == null) return;
		Stock input = this.input;
		if (this.input == null || !this.input.code().equals(code))
			input = getStockGroup(chkBuy.isSelected());
		
		Price output = null;
		if (input != null) {
			PriceListPartial pl = new PriceListPartial(market, input, market.getTimeViewInterval(), false, true, this);
			pl.setVisible(true);
			output = pl.getOutput();
		}
		else {
			PriceList pl = new PriceList(market.getNearestUniverse(), code, market.getTimeViewInterval(), true, this);
			pl.setVisible(true);
			output = pl.getOutput();
		}
		
		if (output != null) {
			txtTakenPrice.setValue(output.get());
			txtTakenDate.setValue(new Date(output.getTime()));
			
			txtLowPrice.setValue(output.getLow());
			txtHighPrice.setValue(output.getHigh());
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
		long takenTimePoint = ((Date)txtTakenDate.getValue()).getTime();
		
		double leverage = Double.NaN;
		if (chkLeverage.isSelected()) {
			double v = ((Number)txtLeverage.getValue()).doubleValue();
			leverage = v == 0 ? 0 : 1 / v;
		}

		Stock output = null;
		if (update) {
			output = input;
		}
		else {
			output = m.addStock(code, chkBuy.isSelected(), ((Number)txtVolume.getValue()).doubleValue(), takenTimePoint);
			if (!Double.isNaN(leverage)) output.setLeverage(leverage);
			if (output == null) return;
		}
		
		StockImpl s = m.c(output); if (s == null) return;
		StockGroup group = m.get(s.code(), s.isBuy());
		if (group == null) return;
		
		if (update) {
			if (!Double.isNaN(leverage)) group.setLeverage(leverage);
			s.take(m.getTimeViewInterval(), takenTimePoint);
			s.setCommitted(chkCommitted.isSelected());
		}
		
		
		s.setVolume(((Number)txtVolume.getValue()).doubleValue());
		s.setStopLoss(((Number)txtStopLoss.getValue()).doubleValue());
		s.setTakeProfit(((Number)txtTakeProfit.getValue()).doubleValue());
		if (chkUnitBias.isSelected()) group.setUnitBias(((Number)txtUnitBias.getValue()).doubleValue());
			
		if (chkProperty.isSelected() && txtProperty.getStockProperty() != null) {
			s.getProperty().set(txtProperty.getStockProperty());
		}
	
		
		this.output = output;
		JOptionPane.showMessageDialog(this, "Add new / update successfully", "Add new / update", JOptionPane.INFORMATION_MESSAGE);
		dispose();
	}
	
	
	public Stock getOutput() {
		return output;
	}

	
	protected void setOutput(Stock output) {
		this.output = output;
	}
	

}
