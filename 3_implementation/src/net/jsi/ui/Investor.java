package net.jsi.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.text.NumberFormatter;

import net.jsi.Market;
import net.jsi.MarketImpl;
import net.jsi.StockProperty;
import net.jsi.Universe;
import net.jsi.UniverseImpl;
import net.jsi.Util;

public class Investor extends JFrame implements MarketListener {

	
	private static final long serialVersionUID = 1L;
	
	
	protected Universe universe = null;
	
	
	JTabbedPane body;
	
	
	protected JLabel lblTotalProfit;

	
	protected JLabel lblTotalROI;

	
	protected JLabel lblTotalBias;

	
	public Investor(Universe universe) {
		super("Stock investor");
		this.universe = universe;
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				
				for (int i = 0; i < body.getTabCount(); i++) {
					Component comp = body.getComponentAt(i);
					if ((comp != null) && (comp instanceof MarketPanel))
						((MarketPanel)comp).dispose();
				}
			}
		});
		
		addMouseListener(new MouseAdapter() {
			
		});
		
		setSize(800, 600);
		setLocationRelativeTo(null);
	    setJMenuBar(createMenuBar());
		
		setLayout(new BorderLayout());

		add(createToolbar(), BorderLayout.NORTH);

		body = new JTabbedPane();
		add(body, BorderLayout.CENTER);
		for (int i = 0; i < universe.size(); i++) {
			Market market = universe.get(i);
			MarketPanel mp = new MarketPanel(market);
			mp.getMarketTable().getModel2().addMarketListener(this);
			body.add(market.name(), mp);
		}
		
		JPanel footer = new JPanel(new BorderLayout());
		add(footer, BorderLayout.SOUTH);
		
		JPanel footerRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		footer.add(footerRow, BorderLayout.NORTH);
		
		footerRow.add(lblTotalProfit = new JLabel());
		footerRow.add(new JLabel(" "));
		footerRow.add(lblTotalROI = new JLabel());
		footerRow.add(new JLabel(" "));
		footerRow.add(lblTotalBias = new JLabel());

		update();
		
		setVisible(true);
	}
	
	
	private void update() {
		long timeViewInterval = universe.getTimeViewInterval();
		double profit = universe.getProfit(timeViewInterval);
		double roi = universe.getROIByLeverage(timeViewInterval);
		double totalBias = universe.calcTotalBias(timeViewInterval);
		
		lblTotalProfit.setText("PROFIT: " + Util.format(profit));
		lblTotalROI.setText("ROI: " + Util.format(roi*100) + "%");
		lblTotalBias.setText("BIAS: " + Util.format(totalBias));
	}

	
	protected JMenuBar createMenuBar() {
		JMenuBar mnBar = new JMenuBar();
		
		JMenu mnFile = new JMenu("File");
		mnFile.setMnemonic('f');
		mnBar.add(mnFile);

		JMenuItem mniOpen = new JMenuItem(
			new AbstractAction("Open") {
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					onOpen();
				}
			});
		mniOpen.setMnemonic('o');
		mniOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
		mnFile.add(mniOpen);

		JMenuItem mniSave = new JMenuItem(
			new AbstractAction("Save") {
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					onSave();
				}
			});
		mniSave.setMnemonic('s');
		mniSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
		mnFile.add(mniSave);

		JMenu mnTool = new JMenu("Tool");
		mnFile.setMnemonic('t');
		mnBar.add(mnTool);

		JMenuItem mnOption = new JMenuItem(
			new AbstractAction("Option") {
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					new Option().setVisible(true);
				}
			});
		mnOption.setMnemonic('p');
		mnTool.add(mnOption);

		return mnBar;
	}
	
	
	protected JToolBar createToolbar() {
		JToolBar toolbar = new JToolBar();

		return toolbar;
	}


	private void onOpen() {
		MarketPanel mp = getMarketPanel();
		if (mp != null) mp.onOpen();
	}
	
	
	private void onSave() {
		MarketPanel mp = getMarketPanel();
		if (mp != null) mp.onSave();
	}
	
	
	@Override
	public void notify(MarketEvent evt) {
		update();
	}

	
	protected MarketPanel getMarketPanel() {
		Component comp = body.getSelectedComponent();
		if (comp instanceof MarketPanel)
			return (MarketPanel)comp;
		return
			null;
	}
	
	
	protected MarketImpl getMarket() {
		MarketPanel mp = getMarketPanel();
		if (mp == null) return null;
		
		Universe u = mp.getMarket().getNearestUniverse();
		return u != null ? u.c(mp.getMarket()) : null;
	}

	
	private Investor getInvestor() {
		return this;
	}
	
	
	class Option extends JDialog {

		private static final long serialVersionUID = 1L;

		protected JFormattedTextField txtBalance;
		
		protected JFormattedTextField txtBalanceBias;
		
		protected JFormattedTextField txtMarginFee;
		
		protected JFormattedTextField txtTimeViewInterval;
		
		protected JFormattedTextField txtRefLeverage;
		
		public Option() {
			super(getInvestor(), "Option", true);
			MarketImpl m = getMarket();
			
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			setSize(350, 250);
			setLocationRelativeTo(Util.getFrameForComponent(getInvestor()));
			setLayout(new BorderLayout());
			
			
			JPanel header = new JPanel(new BorderLayout());
			add(header, BorderLayout.NORTH);
			
			JPanel left = new JPanel(new GridLayout(0, 1));
			header.add(left, BorderLayout.WEST);
			
			left.add(new JLabel("Balance: "));
			left.add(new JLabel("Balance bias: "));
			left.add(new JLabel("Margin fee: "));
			left.add(new JLabel("Day interval (days): "));
			left.add(new JLabel("Referred leverage: "));

			JPanel right = new JPanel(new GridLayout(0, 1));
			header.add(right, BorderLayout.CENTER);
			
			JPanel paneBalance = new JPanel(new BorderLayout());
			right.add(paneBalance);
			txtBalance = new JFormattedTextField(new NumberFormatter());
			txtBalance.setValue(m.getBalanceBase());
			paneBalance.add(txtBalance, BorderLayout.CENTER);
			
			JPanel paneBalanceBias = new JPanel(new BorderLayout());
			right.add(paneBalanceBias);
			txtBalanceBias = new JFormattedTextField(new NumberFormatter());
			txtBalanceBias.setValue(m.getBalanceBias());
			paneBalanceBias.add(txtBalanceBias, BorderLayout.CENTER);
			
			JPanel paneMarginFee = new JPanel(new BorderLayout());
			right.add(paneMarginFee);
			txtMarginFee = new JFormattedTextField(new NumberFormatter());
			txtMarginFee.setValue(m.getMarginFee());
			paneMarginFee.add(txtMarginFee, BorderLayout.CENTER);
			
			JPanel paneTimeViewInterval = new JPanel(new BorderLayout());
			right.add(paneTimeViewInterval);
			txtTimeViewInterval = new JFormattedTextField(new NumberFormatter());
			txtTimeViewInterval.setValue(m.getTimeViewInterval() / (1000*3600*24));
			paneTimeViewInterval.add(txtTimeViewInterval, BorderLayout.CENTER);
			
			JPanel paneRefLeverage = new JPanel(new BorderLayout());
			right.add(paneRefLeverage);
			txtRefLeverage = new JFormattedTextField(new NumberFormatter());
			txtRefLeverage.setToolTipText("Value 0 specifies infinity leverage");
			txtRefLeverage.setValue(m.getRefLeverage() == 0 ? 0 : 1/m.getRefLeverage());
			paneRefLeverage.add(txtRefLeverage, BorderLayout.CENTER);

			
			JPanel footer = new JPanel();
			add(footer, BorderLayout.SOUTH);
			
			JButton ok = new JButton("OK");
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
		
		private void ok() {
			double balanceBase = txtBalance.getValue() instanceof Number ? ((Number)txtBalance.getValue()).doubleValue() : 0;
			double balanceBias = txtBalanceBias.getValue() instanceof Number ? ((Number)txtBalanceBias.getValue()).doubleValue() : 0;
			double marginFee = txtMarginFee.getValue() instanceof Number ? ((Number)txtMarginFee.getValue()).doubleValue() : 0;
			long dayViewInterval = txtTimeViewInterval.getValue() instanceof Number ? ((Number)txtTimeViewInterval.getValue()).longValue() : 0;
			double refLeverage = txtRefLeverage.getValue() instanceof Number ? ((Number)txtRefLeverage.getValue()).doubleValue() : 0;
			
			MarketImpl m = getMarket();
			m.setBalanceBase(balanceBase);
			m.setBalanceBias(balanceBias);
			m.setMarginFee(marginFee);
			m.setTimeViewInterval(dayViewInterval*1000*3600*24);
			m.setRefLeverage(refLeverage);
			
			getMarketPanel().getMarketTable().update();
			
			dispose();
		}
		
	}

	
	public static void main(String[] args) {
		Universe universe = new UniverseImpl();
		Market market = universe.newMarket("Market 1", StockProperty.LEVERAGE, StockProperty.UNIT_BIAS);
		universe.add(market);
		
		new Investor(universe);
	}
	
	
}



