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
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.NumberFormatter;

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
	
	
	protected File file = null;
	
	
	public MarketPanel(Market market) {
		tblMarket = new MarketTable(market, true, null);
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
				new MarketSummary(getMarket(), tblMarket, thisPanel).setVisible(true);
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
	
	
	protected File getWorkingDirectory() {
		return null;
	}
	
	
	private void take(Stock input, boolean modify) {
		if (modify && input == null) return;
		StockTaker taker = new StockTaker(getMarket(), input, modify, this);
		taker.setVisible(true);
		if (taker.getOutput() != null) tblMarket.update();
	}
	
	
	protected void update() {
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


	protected void onOpen() {
		JFileChooser fc = createFileChooser();
        if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
        File file = fc.getSelectedFile();
        boolean ret = open(file);
        
        if (ret)
            JOptionPane.showMessageDialog(this, "Success to open market \"" + getMarket().getName() + "\"", "Open market", JOptionPane.INFORMATION_MESSAGE);
		else
			JOptionPane.showMessageDialog(this, "Fail to open market \"" + getMarket().getName() + "\"", "Save market", JOptionPane.ERROR_MESSAGE);
	}

	
	protected boolean open(File file) {
		if (file == null || !file.exists() || file.isDirectory()) return false;
        try {
        	FileReader reader = new FileReader(file);
        	boolean ret = getMarketTable().open(reader);
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
		if (file == null) return false;
        try {
        	FileWriter writer = new FileWriter(file);
        	boolean ret = tblMarket.save(writer);
        	if (ret) this.file = file;
        	
        	writer.close();
            return ret;
        }
        catch (Exception e) {
        	e.printStackTrace();
        }
        
        return false;
	}
	
	
	
	protected void dispose() {
		if (file != null) {
			save(file);
			return;
		}
		else {
			File workingDir = getWorkingDirectory();
			if (workingDir != null && workingDir.exists() && workingDir.isDirectory()) {
				File file = new File(workingDir, getMarket().getName() + "." + StockProperty.JSI_EXT);
				if (save(file)) return;
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
		txtLeverage = new JFormattedTextField(new NumberFormatter());
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
	
		txtVolume = new JFormattedTextField(new NumberFormatter());
		txtVolume.setValue(update ? input.getVolume(0, false) : 1);
		right.add(txtVolume);

		JPanel paneTakenPrice = new JPanel(new BorderLayout());
		if (update) right.add(paneTakenPrice);
		txtTakenPrice = new JFormattedTextField(new NumberFormatter());
		txtTakenPrice.setValue(update ? input.getAverageTakenPrice(0) : 1);
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
		paneTakenPrice.add(btnTakenPrice, BorderLayout.EAST);
		
		JPanel paneTakenDate = new JPanel(new BorderLayout());
		if (update) right.add(paneTakenDate);
		txtTakenDate = new JFormattedTextField(new SimpleDateFormat(Util.DATE_FORMAT));
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
				modifyPriceList();
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
				if (estimator != null)  txtUnitBias.setValue(estimator.estimateUnitBias(market.getTimeViewInterval()));
			}
		});
		btnUnitBias.setEnabled(false);
		paneUnitBias.add(btnUnitBias, BorderLayout.EAST);
		//
		txtUnitBias = new JFormattedTextField(new NumberFormatter());
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

			Price takenPrice = s.getTakenPrice(market.getTimeViewInterval());
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
				leverage = m().getRefLeverage();
				unitBias = StockAbstract.calcMaxUnitBias(StockProperty.UNIT_BIAS, StockProperty.LEVERAGE, leverage);
			}
			txtLeverage.setValue(leverage == 0 ? 0 : 1.0 / leverage);
			
			txtLastDate.setValue(group != null ? new Date(group.getPriceTimePoint() + StockProperty.TIME_UPDATE_PRICE_INTERVAL) : new Date());
			btnLastDateList.setEnabled(group != null);
			paneLastDate.setVisible(group != null);
			
			txtUnitBias.setValue(unitBias);
			
			txtPrice.setValue(group != null ? group.getPrice().get() : 1);
			txtLowPrice.setValue(group != null ? group.getPrice().getLow() : 1);
			txtHighPrice.setValue(group != null ? group.getPrice().getHigh() : 1);
			
			txtStopLoss.setValue(s != null && group != null && s.code().equals(group.code()) ? s.getStopLoss() : 0);
			txtTakeProfit.setValue(s != null && group != null && s.code().equals(group.code()) ? s.getTakeProfit() : 0);
		}
		
	}
	
	
	private void modifyPriceList() {
		PriceList pl = new PriceList(market, input, market.getTimeViewInterval(), update, false, this);
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
		PriceList pl = new PriceList(market, input, m().getTimeViewInterval(), false, true, this);
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
		
		double takenPrice = txtTakenPrice.getValue() instanceof Number ? ((Number)txtTakenPrice.getValue()).doubleValue() : 0;
		if (takenPrice <= 0) return false;
		
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
			if (s == null)
				return false;
			else if (!s.checkPriceTimePoint(lastDate.getTime()))
				return false;
		}
		else if (update) {
			if (s == null)
				return false;
			else if (!s.checkPriceTimePointPrevious(lastDate.getTime()))
				return false;
		}
		else if (s != null) { //add new
			if (!s.checkPriceTimePoint(lastDate.getTime()))
				return false;
		}
		else {
			StockGroup group = getStockGroup();
			if (group != null && !group.checkPriceTimePoint(lastDate.getTime()))
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
				
//				long takenTime =  ((Date)txtTakenDate.getValue()).getTime();
//				Price p = group.getPrice(); if (p == null) return;
//				if (p.getTime() == takenTime) {
//					if (!s.setPrice(price)) return;
//				}
//				else if (chkLastDate.isSelected()) {
//					s.setPriceTimePoint(lastTime);
//				}
				if (chkLastDate.isSelected()) {
					s.setPriceTimePoint(lastTime);
				}
				
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


	private Component getParent0() {
		return parent;
	}
	
	
}



