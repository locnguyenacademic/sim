package net.jsi.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.WindowAdapter;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

import net.jsi.Market;
import net.jsi.StockProperty;
import net.jsi.Universe;
import net.jsi.UniverseImpl;
import net.jsi.Util;

public class Investor extends JFrame implements MarketListener {

	
	private static final long serialVersionUID = 1L;
	
	
	protected Universe universe = null;
	
	
	protected JLabel lblTotalProfit;

	
	protected JLabel lblTotalROI;

	
	protected JLabel lblTotalBias;

	
	public Investor(Universe universe) {
		super("Stock investor");
		this.universe = universe;
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			
		});
		
		addMouseListener(new MouseAdapter() {
			
		});
		
		setSize(800, 600);
		setLocationRelativeTo(null);
	    setJMenuBar(createMenuBar());
		
		setLayout(new BorderLayout());

		add(createToolbar(), BorderLayout.NORTH);

		JTabbedPane body = new JTabbedPane();
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

		return mnBar;
	}
	
	
	protected JToolBar createToolbar() {
		JToolBar toolbar = new JToolBar();

		return toolbar;
	}


	private void onOpen() {
		
	}
	
	
	private void onSave() {
		
	}
	
	
	@Override
	public void notify(MarketEvent evt) {
		update();
	}

	
	public static void main(String[] args) {
		Universe universe = new UniverseImpl();
		Market market = universe.newMarket("Market 1", StockProperty.LEVERAGE, StockProperty.UNIT_BIAS);
		universe.add(market);
		
		new Investor(universe);
	}
	
	
}
