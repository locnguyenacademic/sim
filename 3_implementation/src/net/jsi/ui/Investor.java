package net.jsi.ui;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.WindowAdapter;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;

import net.jsi.Market;
import net.jsi.StockAbstract;
import net.jsi.Universe;
import net.jsi.UniverseImpl;

public class Investor extends JFrame {

	
	private static final long serialVersionUID = 1L;
	
	
	protected Universe universe = null;
	
	
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
			body.add(market.name(), mp);
		}
		
		JPanel footer = new JPanel();
		add(footer, BorderLayout.SOUTH);
		
		setVisible(true);
	}
	
	
	protected JMenuBar createMenuBar() {
		JMenuBar mnBar = new JMenuBar();
		
		JMenu mnFile = new JMenu("Tool");
		mnFile.setMnemonic('t');
		mnBar.add(mnFile);

		return mnBar;
	}
	
	
	protected JToolBar createToolbar() {
		JToolBar toolbar = new JToolBar();

		return toolbar;
	}


	public static void main(String[] args) {
		Universe universe = new UniverseImpl();
		Market market = universe.newMarket("Market 1", StockAbstract.LEVERAGE, StockAbstract.UNIT_BIAS);
		universe.add(market);
		
		new Investor(universe);
	}
	
	
}
