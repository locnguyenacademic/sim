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
import java.util.List;

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
import javax.swing.KeyStroke;

import net.jsi.Estimator;
import net.jsi.Market;
import net.jsi.MarketImpl;
import net.jsi.Price;
import net.jsi.PricePool;
import net.jsi.Stock;
import net.jsi.StockAbstract;
import net.jsi.StockGroup;
import net.jsi.StockImpl;
import net.jsi.StockInfoStore;
import net.jsi.StockProperty;
import net.jsi.Universe;
import net.jsi.Util;
import net.jsi.PricePool.TakenStockPrice;

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
		super(Util.getDialogForComponent(parent), "Stock taker: " + (update ? "Update" : "Add new"), true);
		this.market = market;
		this.input = input;
		this.update = update;
		this.parent = parent;
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(500, 550);
		setLocationRelativeTo(Util.getDialogForComponent(parent));
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
		left.add(new JLabel("Low price: "));
		left.add(new JLabel("High price: "));
		left.add(new JLabel("Alt price: "));
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
				if (e.getStateChange() == ItemEvent.SELECTED) update();
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
			catch (Throwable e) {
				Util.trace(e);
			}
		}
		txtTakenPrice.setEditable(false);
		paneTakenPrice.add(txtTakenPrice, BorderLayout.CENTER);
		//
		btnTakenPrice = new JButton("Modify");
		btnTakenPrice.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				double takenPrice = txtTakenPrice.getValue() instanceof Number ? ((Number)txtTakenPrice.getValue()).doubleValue() : 1.0;
				String txtPrice = JOptionPane.showInputDialog(btnTakenPrice, "Enter taken price", takenPrice);
				takenPrice = Double.NaN;
				try {
					takenPrice = Double.parseDouble(txtPrice);
				}
				catch (Throwable ex) {}
				if (!Double.isNaN(takenPrice) && takenPrice >= 0) txtTakenPrice.setValue(takenPrice);
			}
		});
		btnTakenPrice.setEnabled(update);
		paneTakenPrice.add(btnTakenPrice, BorderLayout.EAST);
		if (input != null) {
			MarketImpl m = m(); StockImpl s = m != null ? m.c(input) : null;
			btnTakenPrice.setVisible(s != null && !s.isFixedMargin());
		}
		
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
		if (input != null) {
			MarketImpl m = m(); StockImpl s = m != null ? m.c(input) : null;
			btnTakenDate.setVisible(s != null && !s.isFixedMargin());
		}

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
		txtLowPrice.setValue(update ? input.getPrice().getLow() : 0.0);
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
		txtHighPrice.setValue(update ? input.getPrice().getHigh() : 0.0);
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
		txtAltPrice.setValue(update ? input.getPrice().getAlt() : 0.0);
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
		txtUnitBias.setValue(market != null ? market.getUnitBias() : StockProperty.UNIT_BIAS);
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

		JMenuItem mniSwitch = new JMenuItem(
			new AbstractAction("Switch to selector") {
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					switchSelector();
				}
			});
		mniSwitch.setMnemonic('w');
		mniSwitch.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_DOWN_MASK));
		mnTool.add(mniSwitch);
		
		JMenuItem mniToggleFixMargin = new JMenuItem(
			new AbstractAction("Toggle fix margin") {
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					toggleFixMargin();
				}
			});
		mniToggleFixMargin.setMnemonic('f');
		mnTool.add(mniToggleFixMargin);

		return mnBar;
	}

	
	protected void switchSelector() {
		this.dispose();
		StockSelector selector = new StockSelector(market, input, update, parent);
		selector.setVisible(true);
		this.setOutput(selector.getOutput());
	}
	
	
	protected void toggleFixMargin() {
		MarketImpl m = m();
		StockImpl s = m != null ? m.c(input) : null;
		boolean ret = MarketTable.toggleFixMargin(s, this);
		if (ret) {
			btnTakenPrice.setVisible(!s.isFixedMargin());
			btnTakenDate.setVisible(!s.isFixedMargin());
			update();
		}
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

	
	private StockGroup getAltStockGroup() {
		return getAltStockGroup(input != null ? input.isBuy() : true);
	}
	
	
	private StockGroup getAltStockGroup(boolean buy) {
		StockGroup group = getAltStockGroup0(buy);
		if (group != null) return group;
		
		String code = cmbCode.getSelectedItem() != null ? cmbCode.getSelectedItem().toString() : null;
		if (code == null) return null;
		PricePool pricePool = StockInfoStore.getPricePool(code);
		if (pricePool == null || pricePool.size() == 0) return null;
		
		List<TakenStockPrice> takenPrices = PricePool.getLastTakenPrices(code, market.getNearestUniverse(), market.getTimeViewInterval());
		return takenPrices != null && takenPrices.size() > 0 ? takenPrices.get(0).group : null;
	}
	
	
	private StockGroup getAltStockGroup0(boolean buy) {
		StockGroup group = getStockGroup(buy);
		if (group != null) return group;
		
		String code = cmbCode.getSelectedItem() != null ? cmbCode.getSelectedItem().toString() : null;
		if (code == null) return null;
		MarketImpl m = m(); if (m == null) return null;
		if (m.getWatchMarket() != null) return stockGroupOf0(m.getWatchMarket(), code, buy);
		
		Universe u = m.getNearestUniverse();
		MarketImpl dualMarket = u != null ? u.c(m.getDualMarket()) : null;
		if (dualMarket == null || dualMarket.getWatchMarket() == null || dualMarket.getWatchMarket() == m)
			return null;
		
		group = stockGroupOf0(dualMarket, code, buy);
		group = group != null ? group : stockGroupOf0(dualMarket.getWatchMarket(), code, buy);
		return group;
	}
	
	
	private static StockGroup stockGroupOf0(Market market, String code, boolean buy) {
		if (market == null) return null;
		Universe u = market.getNearestUniverse();
		MarketImpl m = u != null ? u.c(market) : null;
		return m != null ? m.get(code, buy) : null;
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
	
	
	protected void update() {
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
			txtAltPrice.setValue(s.getPrice().getAlt());
			
			txtStopLoss.setValue(s.getStopLoss());
			txtTakeProfit.setValue(s.getTakeProfit());
		}
		else {
			StockGroup group = getStockGroup();
			if (group == null) group = getAltStockGroup();
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
			txtLowPrice.setValue(group != null ? group.getPrice().getLow() : 0.0);
			txtHighPrice.setValue(group != null ? group.getPrice().getHigh() : 0.0);
			txtAltPrice.setValue(group != null ? group.getPrice().getAlt() : 0.0);
			
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
		txtAltPrice.setValue(price.getAlt());
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
		
		if (lowPrice == 0 && highPrice == 0) {
			txtLowPrice.setValue(lowPrice = price);
			txtHighPrice.setValue(highPrice = price);
		}
		else if (price < lowPrice || price > highPrice) {
			txtLowPrice.setValue(price);
			txtHighPrice.setValue(price);
			return false;
		}
		
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
		double altPrice = txtAltPrice.getValue() instanceof Number ? ((Number)txtAltPrice.getValue()).doubleValue() : 0;
		if (altPrice < price.getLow() || altPrice > price.getHigh()) altPrice = 0;
		price.setAlt(altPrice);
		
		double leverage = Double.NaN;
		if (chkLeverage.isSelected()) {
			double v = ((Number)txtLeverage.getValue()).doubleValue();
			leverage = v == 0 ? 0 : 1 / v;
		}

		Stock output = null;
		if (update)
			output = input;
		else {
			output = m.addStock(code, chkBuy.isSelected(), leverage, ((Number)txtVolume.getValue()).doubleValue(), price, Double.NaN);
			if (output == null) {
				JOptionPane.showMessageDialog(this, "Unable to add", "Unable to add", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		
		StockImpl s = m.c(output); if (s == null) return;
		StockGroup group = m.get(s.code(), s.isBuy());
		if (group == null) return;
		
		if (update) {
			if (!chkAddPrice.isSelected()) {
				long takenTimePoint = ((Date)txtTakenDate.getValue()).getTime();
				double takenPrice = txtTakenPrice.getValue() instanceof Number ? ((Number)txtTakenPrice.getValue()).doubleValue() : Double.NaN;
				Stock found = group.get(takenTimePoint, takenPrice);
				if (found != null && found != s) return;
				
				s.take(market.getTimeViewInterval(), takenTimePoint, !Double.isNaN(takenPrice) && takenPrice >= 0? takenPrice : Double.NaN);
				
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
			s.getPrice().setAlt(price.getAlt());
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
		super(Util.getDialogForComponent(parent), "Stock selector: " + (update ? "Update" : "Add new"), true);
		this.market = market;
		this.input = input;
		this.update = update;
		this.parent = parent;
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(500, 550);
		setLocationRelativeTo(Util.getDialogForComponent(parent));
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
				if (e.getStateChange() == ItemEvent.SELECTED) update();
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
			catch (Throwable e) {
				Util.trace(e);
			}
		}
		txtTakenPrice.setEditable(false);
		paneTakenPrice.add(txtTakenPrice, BorderLayout.CENTER);
		//
		btnTakenPrice = new JButton("Modify");
		btnTakenPrice.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				double takenPrice = txtTakenPrice.getValue() instanceof Number ? ((Number)txtTakenPrice.getValue()).doubleValue() : 1.0;
				String txtPrice = JOptionPane.showInputDialog(btnTakenPrice, "Enter taken price", takenPrice);
				takenPrice = Double.NaN;
				try {
					takenPrice = Double.parseDouble(txtPrice);
				}
				catch (Throwable ex) {}
				if (!Double.isNaN(takenPrice) && takenPrice >= 0) txtTakenPrice.setValue(takenPrice);
			}
		});
		btnTakenPrice.setEnabled(update);
		paneTakenPrice.add(btnTakenPrice, BorderLayout.EAST);
		if (input != null) {
			MarketImpl m = m(); StockImpl s = m != null ? m.c(input) : null;
			btnTakenPrice.setVisible(s != null && !s.isFixedMargin());
		}
		
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
		if (input != null) {
			MarketImpl m = m(); StockImpl s = m != null ? m.c(input) : null;
			btnTakenDate.setVisible(s != null && !s.isFixedMargin());
		}

		JPanel paneLowPrice = new JPanel(new BorderLayout());
		right.add(paneLowPrice);
		txtLowPrice = new JFormattedTextField(Util.getNumberFormatter());
		txtLowPrice.setValue(update ? input.getPrice().getLow() : 0.0);
		txtLowPrice.setEditable(false);
		paneLowPrice.add(txtLowPrice, BorderLayout.CENTER);
		
		JPanel paneHighPrice = new JPanel(new BorderLayout());
		right.add(paneHighPrice);
		txtHighPrice = new JFormattedTextField(Util.getNumberFormatter());
		txtHighPrice.setValue(update ? input.getPrice().getHigh() : 0.0);
		txtHighPrice.setEditable(false);
		paneHighPrice.add(txtHighPrice, BorderLayout.CENTER);
		
		JPanel paneAltPrice = new JPanel(new BorderLayout());
		right.add(paneAltPrice);
		txtAltPrice = new JFormattedTextField(Util.getNumberFormatter());
		txtAltPrice.setValue(update ? input.getPrice().getAlt() : 0.0);
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
		txtUnitBias.setValue(market != null ? market.getUnitBias() : StockProperty.UNIT_BIAS);
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

		JMenuItem mniSwitch = new JMenuItem(
			new AbstractAction("Switch to taker") {
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					switchTaker();
				}
			});
		mniSwitch.setMnemonic('w');
		mniSwitch.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_DOWN_MASK));
		mnTool.add(mniSwitch);
		
		JMenuItem mniToggleFixMargin = new JMenuItem(
			new AbstractAction("Toggle fix margin") {
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					toggleFixMargin();
				}
			});
		mniToggleFixMargin.setMnemonic('f');
		mnTool.add(mniToggleFixMargin);

		return mnBar;
	}

	
	protected void switchTaker() {
		this.dispose();
		StockTaker taker = new StockTaker(market, input, update, parent);
		taker.setVisible(true);
		this.setOutput(taker.getOutput());
	}
	
	
	protected void toggleFixMargin() {
		MarketImpl m = m();
		StockImpl s = m != null ? m.c(input) : null;
		boolean ret = MarketTable.toggleFixMargin(s, this);
		if (ret) {
			btnTakenPrice.setVisible(!s.isFixedMargin());
			btnTakenDate.setVisible(!s.isFixedMargin());
			update();
		}
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

	
	private StockGroup getAltStockGroup() {
		return getAltStockGroup(input != null ? input.isBuy() : true);
	}
	
	
	private StockGroup getAltStockGroup(boolean buy) {
		StockGroup group = getAltStockGroup0(buy);
		if (group != null) return group;
		
		String code = cmbCode.getSelectedItem() != null ? cmbCode.getSelectedItem().toString() : null;
		if (code == null) return null;
		PricePool pricePool = StockInfoStore.getPricePool(code);
		if (pricePool == null || pricePool.size() == 0) return null;
		
		List<TakenStockPrice> takenPrices = PricePool.getLastTakenPrices(code, market.getNearestUniverse(), market.getTimeViewInterval());
		return takenPrices != null && takenPrices.size() > 0 ? takenPrices.get(0).group : null;
	}
	
	
	private StockGroup getAltStockGroup0(boolean buy) {
		StockGroup group = getStockGroup(buy);
		if (group != null) return group;
		
		String code = cmbCode.getSelectedItem() != null ? cmbCode.getSelectedItem().toString() : null;
		if (code == null) return null;
		MarketImpl m = m(); if (m == null) return null;
		if (m.getWatchMarket() != null) return stockGroupOf0(m.getWatchMarket(), code, buy);
		
		Universe u = m.getNearestUniverse();
		MarketImpl dualMarket = u != null ? u.c(m.getDualMarket()) : null;
		if (dualMarket == null || dualMarket.getWatchMarket() == null || dualMarket.getWatchMarket() == m)
			return null;
		
		group = stockGroupOf0(dualMarket, code, buy);
		group = group != null ? group : stockGroupOf0(dualMarket.getWatchMarket(), code, buy);
		return group;
	}
	
	
	private static StockGroup stockGroupOf0(Market market, String code, boolean buy) {
		if (market == null) return null;
		Universe u = market.getNearestUniverse();
		MarketImpl m = u != null ? u.c(market) : null;
		return m != null ? m.get(code, buy) : null;
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
	
	
	protected void update() {
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
			txtAltPrice.setValue(takenPrice.getAlt());
			
			txtStopLoss.setValue(s.getStopLoss());
			txtTakeProfit.setValue(s.getTakeProfit());
		}
		else {
			StockGroup group = getStockGroup();
			if (group == null) group = getAltStockGroup();
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
			txtTakenDate.setValue(group != null ? new Date(group.getPriceTimePoint()) : new Date());

			txtUnitBias.setValue(unitBias);
			
			txtLowPrice.setValue(group != null ? group.getPrice().getLow() : 0.0);
			txtHighPrice.setValue(group != null ? group.getPrice().getHigh() : 0.0);
			txtAltPrice.setValue(group != null ? group.getPrice().getAlt() : 0.0);
			
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
			PriceList pl = new PriceList(market.getNearestUniverse(), code, market.getTimeViewInterval(), false, true, this);
			pl.setVisible(true);
			output = pl.getOutput();
		}
		
		if (output != null) {
			txtTakenPrice.setValue(output.get());
			txtTakenDate.setValue(new Date(output.getTime()));
			
			txtLowPrice.setValue(output.getLow());
			txtHighPrice.setValue(output.getHigh());
			txtAltPrice.setValue(output.getAlt());
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
			output = m.addStock(code, chkBuy.isSelected(), leverage, ((Number)txtVolume.getValue()).doubleValue(), takenTimePoint, Double.NaN);
			if (output == null) {
				JOptionPane.showMessageDialog(this, "Unable to add", "Unable to add", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		
		StockImpl s = m.c(output); if (s == null) return;
		StockGroup group = m.get(s.code(), s.isBuy());
		if (group == null) return;
		
		if (update) {
			if (!Double.isNaN(leverage)) group.setLeverage(leverage);
			
			double takenPrice = txtTakenPrice.getValue() instanceof Number ? ((Number)txtTakenPrice.getValue()).doubleValue() : Double.NaN;
			Stock found = group.get(takenTimePoint, takenPrice);
			if (found != null && found != s) return;

			s.take(m.getTimeViewInterval(), takenTimePoint, !Double.isNaN(takenPrice) && takenPrice >= 0? takenPrice : Double.NaN);
			
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



