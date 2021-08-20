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
import javax.swing.text.NumberFormatter;

import net.jsi.Market;
import net.jsi.MarketImpl;
import net.jsi.Price;
import net.jsi.Stock;
import net.jsi.StockAbstract;
import net.jsi.StockGroup;
import net.jsi.StockImpl;
import net.jsi.Universe;
import net.jsi.Util;

public class MarketPanel extends JPanel {


	private static final long serialVersionUID = 1L;

	
	protected MarketTable tblMarket = null;
	
	
	public MarketPanel(Market market) {
		tblMarket = new MarketTable(market, true);
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
		
		JButton modify = new JButton("Modify");
		modify.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Stock stock = tblMarket.getSelectedStock();
				take(stock, true);
			}
		});
		header.add(modify);

		
		JPanel body = new JPanel(new BorderLayout());
		add(body, BorderLayout.CENTER);
		body.add(new JScrollPane(tblMarket), BorderLayout.CENTER);
		
		
		JPanel footer = new JPanel();
		add(footer, BorderLayout.SOUTH);
	}
	
	
	public Market getMarket() {
		return tblMarket.getMarket();
	}
	
	
	private void take(Stock input, boolean modify) {
		if (modify && input == null) return;
		
		StockTaker taker = new StockTaker(getMarket(), input, modify, this);
		taker.setVisible(true);
		if (taker.getOutput() != null) tblMarket.update();
	}
	
	
}



class StockTaker extends JDialog {

	
	private static final long serialVersionUID = 1L;
	
	
	protected JComboBox<String> cmbCode;
	
	
	protected JCheckBox chkBuy;
	
	
	protected JFormattedTextField txtDate;

	
	protected JCheckBox chkDate;

	
	protected JFormattedTextField txtLeverage;

	
	protected JCheckBox chkLeverage;

	
	protected JFormattedTextField txtVolume;
	
	
	protected JFormattedTextField txtTakenPrice;
	
	
	protected JFormattedTextField txtPrice;
	
	
	protected JFormattedTextField txtLowPrice;
	
	
	protected JFormattedTextField txtHighPrice;
	
	
	protected JCheckBox chkCommitted;

	
	protected Market market = null;
	
	
	protected Stock output = null;
	
	
	protected Stock input = null;

	
	protected boolean modify = false;
	
	
	public StockTaker(Market market, Stock input, boolean modify, Component parent) {
		super(Util.getFrameForComponent(parent), "Stock taker", true);
		this.market = market;
		this.input = input;
		this.modify = modify;
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(600, 400);
		setLocationRelativeTo(Util.getFrameForComponent(parent));
		setLayout(new BorderLayout());
		
		JPanel header = new JPanel(new BorderLayout());
		add(header, BorderLayout.NORTH);
		
		JPanel left = new JPanel(new GridLayout(0, 1));
		header.add(left, BorderLayout.WEST);
		
		left.add(new JLabel("Code:"));
		left.add(new JLabel("Buy:"));
		if (modify) left.add(new JLabel("Date:"));
		left.add(new JLabel("Leverage:"));
		left.add(new JLabel("Volume:"));
		if (modify) left.add(new JLabel("Taken price:"));
		left.add(new JLabel("Price:"));
		left.add(new JLabel("Low price:"));
		left.add(new JLabel("High price:"));
		left.add(new JLabel("Committed:"));
		
		JPanel right = new JPanel(new GridLayout(0, 1));
		header.add(right, BorderLayout.CENTER);
		
		List<String> codes = Util.newList(0);
		Universe universe = market.getNearestUniverse();
		MarketImpl mi = null;
		if (universe != null) {
			codes.addAll(universe.getSupportStockCodes());
			mi = universe.c(market);
		}
		
		cmbCode = new JComboBox<String>(codes.toArray(new String[] {}));
		if (modify) {
			cmbCode.setSelectedItem(input.code());
			cmbCode.setEnabled(false);
		}
		else if (input != null) {
			cmbCode.setSelectedItem(input.code());
		}
		final MarketImpl fmi = mi;
		cmbCode.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (modify || txtLeverage == null) return;
				
				String code = cmbCode.getSelectedItem() != null ? cmbCode.getSelectedItem().toString() : null;
				if (code == null)
					txtLeverage.setValue(fmi != null ? fmi.getRefLeverage() : StockAbstract.LEVERAGE);
				else if (fmi != null) {
					StockGroup group = fmi.get(code);
					if (group != null)
						txtLeverage.setValue(group.getLeverage());
					else
						txtLeverage.setValue(StockAbstract.LEVERAGE);
				}
				else
					txtLeverage.setValue(StockAbstract.LEVERAGE);
			}
		});
		right.add(cmbCode);
		
		chkBuy = new JCheckBox();
		chkBuy.setSelected(modify ? input.isBuy() : true);
		chkBuy.setEnabled(!modify);
		right.add(chkBuy);
		
		JPanel paneDate = new JPanel(new BorderLayout());
		if (modify) right.add(paneDate);
		txtDate = new JFormattedTextField(new SimpleDateFormat(Util.DATE_FORMAT));
		txtDate.setValue(new Date());
		txtDate.setEnabled(false);
		paneDate.add(txtDate, BorderLayout.CENTER);
		chkDate = new JCheckBox();
		chkDate.setSelected(false);
		chkDate.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				txtDate.setEnabled(chkDate.isSelected());
			}
		});
		paneDate.add(chkDate, BorderLayout.EAST);

		JPanel paneLeverage = new JPanel(new BorderLayout());
		right.add(paneLeverage);
		txtLeverage = new JFormattedTextField(new NumberFormatter());
		txtLeverage.setValue(StockAbstract.LEVERAGE);
		txtLeverage.setEnabled(false);
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
		txtVolume.setValue(modify ? input.getVolume(0, false) : 1);
		right.add(txtVolume);

		txtTakenPrice = new JFormattedTextField(new NumberFormatter());
		txtTakenPrice.setValue(modify ? input.getAverageTakenPrice(0) : 1);
		if (modify) right.add(txtTakenPrice);

		txtPrice = new JFormattedTextField(new NumberFormatter());
		txtPrice.setValue(modify ? input.getPrice().get() : 1);
		right.add(txtPrice);
		
		txtLowPrice = new JFormattedTextField(new NumberFormatter());
		txtLowPrice.setValue(modify ? input.getPrice().getLow() : 1);
		right.add(txtLowPrice);
		
		txtHighPrice = new JFormattedTextField(new NumberFormatter());
		txtHighPrice.setValue(modify ? input.getPrice().getHigh() : 1);
		right.add(txtHighPrice);
		
		chkCommitted = new JCheckBox();
		chkCommitted.setSelected(modify ? input.isCommitted() : false);
		right.add(chkCommitted);

		
		JPanel footer = new JPanel();
		add(footer, BorderLayout.SOUTH);
		
		JButton ok = new JButton(modify ? "Modify" : "Take");
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
		
		StockImpl si = mi.c(input);
		if (modify) {
			if (si != null) txtDate.setValue(new Date(si.getTimePoint()));
			txtLeverage.setValue(input.getLeverage());
		}
		else {
			double leverage = StockAbstract.LEVERAGE;
			String code = cmbCode.getSelectedItem() != null ? cmbCode.getSelectedItem().toString() : null;
			if (code != null && mi != null) {
				StockGroup group = mi.get(code);
				if (group != null) leverage = group.getLeverage();
			}
			else if (mi != null)
				leverage = mi.getRefLeverage();
			txtLeverage.setValue(leverage);
		}

	}
	
	
	private boolean validateInput() {
		Object code = cmbCode.getSelectedItem();
		if (code == null) return false;
		
		if (chkLeverage.isSelected()) {
			double leverage = txtLeverage.getValue() instanceof Number ? ((Number)txtLeverage.getValue()).doubleValue() : 0;
			if (leverage <= 0) return false;
		}
		
		double volume = txtVolume.getValue() instanceof Number ? ((Number)txtVolume.getValue()).doubleValue() : 0;
		if (volume <= 0) return false;
		
		double takenPrice = txtTakenPrice.getValue() instanceof Number ? ((Number)txtTakenPrice.getValue()).doubleValue() : 0;
		if (takenPrice <= 0) return false;
		
		double price = txtPrice.getValue() instanceof Number ? ((Number)txtPrice.getValue()).doubleValue() : 0;
		if (price <= 0) return false;

		double lowPrice = txtLowPrice.getValue() instanceof Number ? ((Number)txtLowPrice.getValue()).doubleValue() : 0;
		if (lowPrice <= 0) return false;

		double highPrice = txtHighPrice.getValue() instanceof Number ? ((Number)txtHighPrice.getValue()).doubleValue() : 0;
		if (highPrice <= 0) return false;
		
		if (price < lowPrice || price > highPrice) return false;
		
		return true;
	}
	
	
	private void ok() {
		if (!validateInput()) {
			JOptionPane.showMessageDialog(this, "Invalid input", "Invalid input", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		MarketImpl mi = null;
		Universe universe = market.getNearestUniverse();
		if (universe != null)
			mi = universe.c(market);
		else {
			try {
				mi = (MarketImpl)market;
			}
			catch (Exception e) {}
		}
		if (mi == null) return;
		
		String code = cmbCode.getSelectedItem().toString();
		Price price = new Price(
				((Number)txtPrice.getValue()).doubleValue(), 
				((Number) txtLowPrice.getValue()).doubleValue(),
				((Number) txtHighPrice.getValue()).doubleValue(),
				System.currentTimeMillis());
		
		if (modify)
			output = input;
		else {
			double leverage = chkLeverage.isSelected() ? ((Number)txtLeverage.getValue()).doubleValue() : 0;
			output = mi.addStock(code, chkBuy.isSelected(), leverage, ((Number)txtVolume.getValue()).doubleValue(), price);
			if (output == null) return;
		}
		
		StockImpl si = mi.c(output);
		if (si == null) return;
		
		if (modify) {
			if (chkDate.isSelected()) si.setTimePoint(((Date)txtDate.getValue()).getTime());
			
			if (chkLeverage.isSelected()) {
				StockGroup group = mi.get(si.code());
				if (group != null)
					group.setLeverage(((Number)txtLeverage.getValue()).doubleValue());
			}
			
			si.getTakenPrice(0).set(((Number)txtTakenPrice.getValue()).doubleValue());
		}
		
		si.setVolume(((Number)txtVolume.getValue()).doubleValue());
		si.getPrice().set(price.get());
		si.getPrice().setLow(price.getLow());
		si.getPrice().setHigh(price.getHigh());
		si.setCommitted(chkCommitted.isSelected());

		JOptionPane.showMessageDialog(this, "Take/Modify successfully", "Take/Modify", JOptionPane.INFORMATION_MESSAGE);
		dispose();
	}
	
	
	public Stock getOutput() {
		return output;
	}


}



