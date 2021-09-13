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
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

import net.jsi.Market;
import net.jsi.MarketAbstract;
import net.jsi.MarketImpl;
import net.jsi.StockProperty;
import net.jsi.Universe;
import net.jsi.UniverseImpl;
import net.jsi.Util;

public class Investor extends JFrame implements MarketListener {

	
	private static final long serialVersionUID = 1L;
	
	
	protected Universe universe = null;
	
	
	protected JTabbedPane body;
	
	
	protected JLabel lblTotalProfit;

	
	protected JLabel lblTotalROI;

	
	protected JLabel lblTotalBias;

	
	protected File curDir = null;
	
	public Investor(Universe universe) {
		super("JSI - Stock/forex investment manager");
		this.universe = universe;
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				super.windowClosed(e);
				
				MarketPanel[] mps = getMarketPanels();
				for (MarketPanel mp : mps) mp.dispose();
			}
		});
		
		addMouseListener(new MouseAdapter() {
			
		});
		
		setSize(800, 600);
		setLocationRelativeTo(null);
	    setJMenuBar(createMenuBar());
		
		setLayout(new BorderLayout());

		JToolBar toolbar = createToolbar();
		if (toolbar != null) add(toolbar, BorderLayout.NORTH);

		body = new InvestorTabbedPane();
		add(body, BorderLayout.CENTER);
		for (int i = 0; i < universe.size(); i++) {
			Market market = universe.get(i);
			MarketPanel mp = createMarketPanel(market);
			body.add(market.getName(), mp);
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
	}
	
	
	private void update() {
		long timeViewInterval = universe.getTimeViewInterval();
		double profit = universe.getProfit(timeViewInterval);
		double roi = universe.getROI(timeViewInterval);
		double lRoi = universe.getROIByLeverage(timeViewInterval);
		double totalBias = universe.calcTotalBias(timeViewInterval);
		
		lblTotalProfit.setText("PROFIT: " + Util.format(profit));
		lblTotalROI.setText("ROI: " + Util.format(roi*100) + "% / " + Util.format(lRoi*100) + "%");
		lblTotalBias.setText("BIAS: " + Util.format(totalBias));
	}
	
	
	private JMenuBar createMenuBar() {
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

		JMenuItem mniSaveAs = new JMenuItem(
			new AbstractAction("Save as") {
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					onSaveAs();
				}
			});
		mniSaveAs.setMnemonic('v');
		mnFile.add(mniSaveAs);

		mnFile.addSeparator();

		Component thisInvestor = Util.getDialogForComponent(this);
		JMenuItem mniWatchMarket = new JMenuItem(
		new AbstractAction("Watch stocks") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				Market selectedMarket = getSelectedWatchMarket();
				if (selectedMarket == null) return;
				
				MarketPanel selectedMarketPanel = getSelectedMarketPanel();
				MarketTable tblMarket = selectedMarketPanel.getMarketTable();
				MarketWatchDialog dlgMarket = new MarketWatchDialog(selectedMarket, true, StockProperty.RUNTIME_CASCADE ? tblMarket : null, thisInvestor);
				dlgMarket.setTitle("Watch stocks for market " + tblMarket.getMarket().getName());
				dlgMarket.setVisible(true);
				
				if (dlgMarket.isPressOK())
					tblMarket.applyPlace();
				else {
					int answer= JOptionPane.showConfirmDialog(thisInvestor, "Would you like to to apply placing?", "Applying confirmation", JOptionPane.YES_NO_OPTION);
					if (answer == JOptionPane.YES_OPTION) tblMarket.applyPlace();
				}
			}
		});
		mniWatchMarket.setMnemonic('w');
		mniWatchMarket.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_DOWN_MASK));
		mnFile.add(mniWatchMarket);

		JMenuItem mniPlaceMarket = new JMenuItem(
		new AbstractAction("Place stocks") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				Market selectedMarket = getSelectedPlaceMarket();
				if (selectedMarket == null) return;
				
				MarketPanel selectedMarketPanel = getSelectedMarketPanel();
				MarketTable tblMarket = selectedMarketPanel.getMarketTable();
				MarketPlaceDialog dlgMarket = new MarketPlaceDialog(selectedMarket, true, StockProperty.RUNTIME_CASCADE ? tblMarket : null, thisInvestor);
				dlgMarket.setTitle("Place stocks for market " + tblMarket.getMarket().getName());
				dlgMarket.setVisible(true);
				
				if (dlgMarket.isPressOK())
					tblMarket.applyPlace();
				else {
					int answer= JOptionPane.showConfirmDialog(thisInvestor, "Would you like to to apply placing?", "Applaying confirmation", JOptionPane.YES_NO_OPTION);
					if (answer == JOptionPane.YES_OPTION) tblMarket.applyPlace();
				}
			}
		});
		mniPlaceMarket.setMnemonic('p');
		mnFile.add(mniPlaceMarket);

		JMenuItem mniApplyPlaceMarket = new JMenuItem(
		new AbstractAction("Apply placing") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				Market selectedMarket = getSelectedWatchMarket();
				if (selectedMarket != null) {
					MarketPanel selectedMarketPanel = getSelectedMarketPanel();
					MarketTable tblMarket = selectedMarketPanel.getMarketTable();
					tblMarket.applyPlace();
				}
			}
		});
		mniApplyPlaceMarket.setMnemonic('a');
		mniApplyPlaceMarket.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK));
		mnFile.add(mniApplyPlaceMarket);
		
		JMenuItem mniTrashMarket = new JMenuItem(
		new AbstractAction("Trash") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				Market selectedMarket = getSelectedTrashMarket();
				if (selectedMarket == null) return;
				
				MarketPanel selectedMarketPanel = getSelectedMarketPanel();
				MarketTable tblMarket = selectedMarketPanel.getMarketTable();
				MarketTrashDialog dlgMarket = new MarketTrashDialog(selectedMarket, true, StockProperty.RUNTIME_CASCADE ? tblMarket : null, thisInvestor);
				dlgMarket.setTitle("Stocks trash for market " + tblMarket.getMarket().getName());
				dlgMarket.setVisible(true);
				
				if (dlgMarket.isPressOK())
					tblMarket.update();
				else {
					int answer= JOptionPane.showConfirmDialog(thisInvestor, "Would you like to to refresh stocks?", "Refresh confirmation", JOptionPane.YES_NO_OPTION);
					if (answer == JOptionPane.YES_OPTION) tblMarket.update();
				}
			}
		});
		mniTrashMarket.setMnemonic('t');
		mnFile.add(mniTrashMarket);
		
		mnFile.addSeparator();
		
		JMenuItem mniAddMarket = new JMenuItem(
			new AbstractAction("Add market") {
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					addMarketPanel();
				}
			});
		mniAddMarket.setMnemonic('d');
		mnFile.add(mniAddMarket);
		
		JMenuItem mniRenameMarket = new JMenuItem(
			new AbstractAction("Rename market") {
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					renameMarketPanel();
				}
			});
		mniRenameMarket.setMnemonic('n');
		mnFile.add(mniRenameMarket);

		JMenuItem mniRemoveMarket = new JMenuItem(
			new AbstractAction("Remove current market") {
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					removeSelectedMarketPanel();
				}
			});
		mniRemoveMarket.setMnemonic('r');
		mnFile.add(mniRemoveMarket);

		mnFile.addSeparator();

		JMenuItem mniExit = new JMenuItem(
			new AbstractAction("Exit") {
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});
		mniExit.setMnemonic('x');
		mnFile.add(mniExit);

		
		JMenu mnTool = new JMenu("Tool");
		mnTool.setMnemonic('t');
		mnBar.add(mnTool);

		JMenuItem mniPriceList = new JMenuItem(
			new AbstractAction("Price list") {
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					MarketPanel selectedMarketPanel = getSelectedMarketPanel();
					MarketTable tblMarket = selectedMarketPanel.getMarketTable();
					PriceList pl = new PriceList(universe, null, universe.getTimeViewInterval(), true, false, thisInvestor);
					pl.setVisible(true);
					if (pl.isApplied()) tblMarket.applyPlace();
				}
			});
		mniPriceList.setMnemonic('p');
		mnTool.add(mniPriceList);

		JMenuItem mniRecommend = new JMenuItem(
			new AbstractAction("Recommend") {
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					MarketImpl selectedMarket = getSelectedMarket();
					RecDialog rd = new RecDialog(selectedMarket, selectedMarket.getTimeViewInterval(), thisInvestor);
					rd.setVisible(true);
				}
			});
		mniRecommend.setMnemonic('r');
		mniRecommend.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK));
		mnTool.add(mniRecommend);
		
		mnTool.addSeparator();
		
		JMenuItem mniOption = new JMenuItem(
			new AbstractAction("Option") {
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					if (getSelectedMarket() != null) new Option().setVisible(true);
				}
			});
		mniOption.setMnemonic('o');
		mnTool.add(mniOption);

		JMenuItem mniResetAllLossesProfits = new JMenuItem(
			new AbstractAction("Reset all losses / profits") {
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					resetAllStopLossesTakeProfits();
				}
			});
		mniResetAllLossesProfits.setMnemonic('o');
		mnTool.add(mniResetAllLossesProfits);

		JMenuItem mniResetAllUnitBiases = new JMenuItem(
			new AbstractAction("Reset all unit biases") {
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					resetAllUnitBiases();
				}
			});
		mniResetAllUnitBiases.setMnemonic('b');
		mnTool.add(mniResetAllUnitBiases);
		
		JMenuItem mniSortAllCodes = new JMenuItem(
			new AbstractAction("Sort all codes") {
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					sortAllCodes();
				}
			});
		mniSortAllCodes.setMnemonic('s');
		mnTool.add(mniSortAllCodes);
					

		JMenu mnHelp = new JMenu("Help");
		mnHelp.setMnemonic('h');
		mnBar.add(mnHelp);

		JMenuItem mniAbout = new JMenuItem(
			new AbstractAction("About") {
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					JDialog dlgAbout = new JDialog(getInvestor(), "About", true);
					dlgAbout.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
					dlgAbout.setSize(400, 200);
					dlgAbout.setLocationRelativeTo(getInvestor());
					dlgAbout.setLayout(new BorderLayout());
					
					JPanel body = new JPanel(new BorderLayout());
					dlgAbout.add(body, BorderLayout.CENTER);
					
					JTextArea txtContent = new JTextArea();
					txtContent.setLineWrap(true);
					txtContent.setEditable(false);
					txtContent.setText("JSI is the stock/forex investment manager.\nJSI current version is " + StockProperty.VERSION + ".\nCopyright @ by Loc Nguyen - Loc Nguyen's Academic Network");
					dlgAbout.add(new JScrollPane(txtContent), BorderLayout.CENTER);
					
					JPanel footer = new JPanel();
					dlgAbout.add(footer, BorderLayout.SOUTH);
					
					JButton ok = new JButton("OK");
					ok.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							dlgAbout.dispose();
						}
					});
					footer.add(ok);
					
					dlgAbout.setVisible(true);
				}
			});
		mniAbout.setMnemonic('a');
		mnHelp.add(mniAbout);
		
		JMenuItem mniHelpContent = new JMenuItem(
			new AbstractAction("Help content") {
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					JDialog dlgHelpContent = new JDialog(getInvestor(), "Help content", true);
					dlgHelpContent.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
					dlgHelpContent.setSize(400, 300);
					dlgHelpContent.setLocationRelativeTo(getInvestor());
					dlgHelpContent.setLayout(new BorderLayout());
					
					JPanel body = new JPanel(new BorderLayout());
					dlgHelpContent.add(body, BorderLayout.CENTER);
					
					JTextArea txtContent = new JTextArea();
					txtContent.setLineWrap(true);
					txtContent.setEditable(false);
					txtContent.setText("The JSI product is the place to acknowledge individuals and organizations who gave me software libraries used in JSI.\n"
							+ "Some libraries are no longer used in current version of JSI.\n"
							+ "\n"
							+ "Tom Martin and Ky Leggiero developed draggable tabed panel available at https://stackoverflow.com/questions/60269/how-to-implement-draggable-tab-using-java-swing\n");
					dlgHelpContent.add(new JScrollPane(txtContent), BorderLayout.CENTER);
					
					JPanel footer = new JPanel();
					dlgHelpContent.add(footer, BorderLayout.SOUTH);
					
					JButton ok = new JButton("OK");
					ok.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							dlgHelpContent.dispose();
						}
					});
					footer.add(ok);
					
					dlgHelpContent.setVisible(true);
				}
			});
		mniHelpContent.setMnemonic('c');
		mnHelp.add(mniHelpContent);

		return mnBar;
	}
	
	
	private JToolBar createToolbar() {
		//JToolBar toolbar = new JToolBar();
		//return toolbar;
		return null;
	}


	private void onOpen() {
		MarketPanel mp = getSelectedMarketPanel();
		if (mp == null) return;
		
		if (mp.onOpen()) {
			int index = indexOfMarketPanel(mp);
			if (index >= 0) body.setTitleAt(index, mp.getMarket().getName());
		}
	}
	
	
	private void onSaveAs() {
		MarketPanel mp = getSelectedMarketPanel();
		if (mp != null) mp.onSave();
	}
	
	
	private void onSave() {
		MarketPanel mp = getSelectedMarketPanel();
		if (mp == null)
			return;
		else if (mp.getFile() != null) {
			boolean ret = mp.save(mp.getFile());
			if (ret)
				JOptionPane.showMessageDialog(this, "Success to save market \"" + mp.getMarket().getName() + "\"", "Save market", JOptionPane.INFORMATION_MESSAGE);
		}
		else
			mp.onSave();
	}
	
	
	protected void addMarketPanel() {
		MarketPanel[] mps = getMarketPanels();
		String marketName = JOptionPane.showInputDialog(this, "Enter new market name", "Market " + (mps.length + 1));
		if (marketName == null) return;
		
		marketName = marketName.trim();
		if (marketName.isEmpty())
			JOptionPane.showMessageDialog(this, "Empty market name", "Empty market name", JOptionPane.ERROR_MESSAGE);
		else if (getMarketPanel(marketName) != null)
			JOptionPane.showMessageDialog(this, "Duplicated market name", "Duplicated market name", JOptionPane.ERROR_MESSAGE);
		else {
			MarketPanel mp = addMarketPanel(marketName);
			if (mp == null)
				JOptionPane.showMessageDialog(this, "Impossible to add market", "Impossible to add market", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	
	protected void renameMarketPanel() {
		MarketImpl selectedMarket = getSelectedMarket();
		if (selectedMarket == null) return;
		MarketPanel[] mps = getMarketPanels();
		String marketName = JOptionPane.showInputDialog(this, "Enter new market name", "Market " + (mps.length + 1));
		if (marketName == null) return;
		
		marketName = marketName.trim();
		if (marketName.isEmpty())
			JOptionPane.showMessageDialog(this, "Empty market name", "Empty market name", JOptionPane.ERROR_MESSAGE);
		else if (getMarketPanel(marketName) != null)
			JOptionPane.showMessageDialog(this, "Duplicated market name", "Duplicated market name", JOptionPane.ERROR_MESSAGE);
		else {
			selectedMarket.setName(marketName);
			int index = getSelectedMarketPanelIndex();
			body.setTitleAt(index, marketName);
		}
	}

	
	protected MarketPanel addMarketPanel(String marketName) {
		if (marketName == null || marketName.isEmpty()) return null;
		if (getMarketPanel(marketName) != null) return null;
		
		MarketPanel mp = createMarketPanel(marketName, StockProperty.LEVERAGE, StockProperty.UNIT_BIAS);
		if (mp == null) return null;
		body.add(mp.getMarket().getName(), mp);
		
		update();
		
		return mp;
	}
	
	
	private MarketPanel addMarketPanel(File file) {
		if (file == null || !file.exists()) return null;
		String fileName = file.getName();
		if (fileName == null || fileName.isEmpty()) return null;
		
		String marketName = null;
		int index = fileName.lastIndexOf(".");
		if (index < 0)
			marketName = fileName;
		else
			marketName = fileName.substring(0, index);
		if (marketName == null | marketName.isEmpty()) return null;
		
		MarketPanel mp = createMarketPanel(marketName, StockProperty.LEVERAGE, StockProperty.UNIT_BIAS);
		if (mp == null) return null;
		boolean ret = mp.open(file);
		if (!ret) return null;
		
		marketName = mp.getMarket().getName();
		if (getMarketPanel(marketName) != null) return null;
		mp.getMarketTable().getModel2().addMarketListener(this);
		body.add(marketName, mp);
		
		update();
		
		return mp;
	}

		
	private MarketPanel createMarketPanel(String name, double leverage, double unitBias) {
		Market market = universe.newMarket(name, leverage, unitBias);
		if (!universe.add(market))
			return null;
		else
			return createMarketPanel(market);
	}
	
	
	private MarketPanel createMarketPanel(Market market) {
		MarketPanel mp = new MarketPanel(market, true, this) {

			private static final long serialVersionUID = 1L;

			@Override
			protected File getWorkingDirectory() {
				return curDir;
			}
			
		};
		mp.btnReestimateLossesProfits.setVisible(true);
		mp.btnReestimateUnitBiases.setVisible(true);
		
		return mp;
	}
	
	
	private void removeSelectedMarketPanel() {
		MarketPanel[] mps = getMarketPanels();
		if (mps.length == 0) return;
		if (mps.length < 2) {
			JOptionPane.showMessageDialog(this, "Universe has 1 market at least", "Imposible to remove market", JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		MarketPanel mp = getSelectedMarketPanel();
		if (mp == null) return;
		int index = universe.lookup(mp.getMarket().getName());
		if (index < 0) return;
		
		mp.dispose();
		Market removedMarket = universe.remove(index);
		if (removedMarket != null) {
			int idx = getSelectedMarketPanelIndex();
			if (idx >= 0) body.removeTabAt(idx);
			
			update();
		}
	}
	
	
	@Override
	public void notify(MarketEvent evt) {
		update();
	}

	
	private MarketPanel getSelectedMarketPanel() {
		Component comp = body.getSelectedComponent();
		if ((comp != null) && (comp instanceof MarketPanel))
			return (MarketPanel)comp;
		return
			null;
	}
	
	
	private MarketPanel[] getMarketPanels() {
		int n = body.getTabCount();
		
		MarketPanel[] mps = new MarketPanel[n];
		for (int i = 0; i < n; i++) {
			mps[i] = (MarketPanel) body.getComponentAt(i);
		}
		
		return mps;
	}
	
	
	private int getSelectedMarketPanelIndex() {
		return indexOfMarketPanel(getSelectedMarketPanel());
	}
	
	
	private int indexOfMarketPanel(MarketPanel mp) {
		if (mp == null) return -1;
		MarketPanel[] mps = getMarketPanels();
		for (int i = 0; i < mps.length; i++) {
			if (mps[i] == mp) return i;
		}
		return -1;
	}
	
	
	private MarketPanel getMarketPanel(String marketName) {
		MarketPanel[] mps = getMarketPanels();
		for (MarketPanel mp : mps) {
			if (mp.getMarket().getName().equals(marketName)) return mp;
		}
		
		return null;
	}
	
	
	private MarketImpl getSelectedMarket() {
		MarketPanel mp = getSelectedMarketPanel();
		if (mp == null)
			return null;
		else
			return universe.c(mp.getMarket());
	}

	
	private MarketImpl getSelectedWatchMarket() {
		Market selectedMarket = getSelectedMarket();
		if (selectedMarket == null)
			return null;
		else
			return universe.c(selectedMarket).getWatchMarket();
	}
	
	
	private MarketImpl getSelectedPlaceMarket() {
		Market selectedMarket = getSelectedMarket();
		if (selectedMarket == null)
			return null;
		else
			return universe.c(selectedMarket).getPlaceMarket();
	}

	
	private MarketImpl getSelectedTrashMarket() {
		Market selectedMarket = getSelectedMarket();
		if (selectedMarket == null)
			return null;
		else
			return universe.c(selectedMarket).getTrashMarket();
	}

	
	protected MarketImpl[] getMarkets() {
		MarketPanel[] mps = getMarketPanels();
		MarketImpl[] ms = new MarketImpl[mps.length];
		for (int i = 0; i < mps.length; i++) ms[i] = universe.c(mps[i].getMarket());
		
		return ms;
	}
	
	
	private long enterTimeInterval() {
		long timeInterval = 0;
		MarketImpl[] markets = getMarkets();
		for (MarketImpl market : markets) {
			long ti = market.getTimeViewInterval();
			if (ti == 0) {
				timeInterval = 0;
				break;
			}
			else
				timeInterval = Math.max(ti, timeInterval);
		}
		
		long days = 0;
		if (timeInterval != 0) {
			days = (long)(timeInterval * StockProperty.TIME_VIEW_PERIOD_RATIO / (1000*3600*24));
			days = days > 0 ? days : 1;
		}
		
		String daysText = JOptionPane.showInputDialog(this, "Enter valid interval in days", days);
		if (daysText == null) return -1;
		
		days = 0;
		try {
			return Long.parseLong(daysText) * (1000*3600*24);
		}
		catch (Exception e) { }
		
		return -1;
	}
	
	
	private void resetAllStopLossesTakeProfits() {
		int answer= JOptionPane.showConfirmDialog(this, "Be careful to reset all stop losses and take profits.\nAre you sure to reset them?", "Reset confirmation", JOptionPane.YES_NO_OPTION);
		if (answer != JOptionPane.YES_OPTION) return;
		long timeInterval = enterTimeInterval();
		if (timeInterval < 0) {
			JOptionPane.showMessageDialog(this, "Invalid time interval", "Invalid time interval", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		MarketPanel[] mps = getMarketPanels();
		for (MarketPanel mp : mps) {
			MarketImpl market = universe.c(mp.getMarket());
			if (market == null) continue;
			
			market.resetAllStopLossesTakeProfits(timeInterval);
			MarketImpl watchMarket = market.getWatchMarket();
			if (watchMarket != null) watchMarket.resetAllStopLossesTakeProfits(timeInterval);
			MarketImpl placeMarket = market.getPlaceMarket();
			if (placeMarket != null) placeMarket.resetAllStopLossesTakeProfits(timeInterval);
			MarketImpl trashMarket = market.getTrashMarket();
			if (trashMarket != null) trashMarket.resetAllStopLossesTakeProfits(timeInterval);

			mp.getMarketTable().update();
		}
	}
	
	
	private void resetAllUnitBiases() {
		int answer= JOptionPane.showConfirmDialog(this, "Be careful to reset all unit biases.\nAre you sure to reset them?", "Reset confirmation", JOptionPane.YES_NO_OPTION);
		if (answer != JOptionPane.YES_OPTION) return;
		long timeInterval = enterTimeInterval();
		if (timeInterval < 0) {
			JOptionPane.showMessageDialog(this, "Invalid time interval", "Invalid time interval", JOptionPane.ERROR_MESSAGE);
			return;
		}

		MarketPanel[] mps = getMarketPanels();
		for (MarketPanel mp : mps) {
			MarketImpl market = universe.c(mp.getMarket());
			if (market == null) continue;
			
			market.resetAllUnitBiases(timeInterval);
			MarketImpl watchMarket = market.getWatchMarket();
			if (watchMarket != null) watchMarket.resetAllUnitBiases(timeInterval);
			MarketImpl placeMarket = market.getPlaceMarket();
			if (placeMarket != null) placeMarket.resetAllUnitBiases(timeInterval);
			MarketImpl trashMarket = market.getTrashMarket();
			if (trashMarket != null) trashMarket.resetAllUnitBiases(timeInterval);

			mp.getMarketTable().update();
		}
	}
	
	
	private void sortAllCodes() {
		MarketPanel[] mps = getMarketPanels();
		for (MarketPanel mp : mps) {
			MarketImpl market = universe.c(mp.getMarket());
			if (market == null) continue;
			
			market.sortByCode();
			MarketImpl watchMarket = market.getWatchMarket();
			if (watchMarket != null) watchMarket.sortByCode();
			MarketImpl placeMarket = market.getPlaceMarket();
			if (placeMarket != null) placeMarket.sortByCode();
			MarketImpl trashMarket = market.getTrashMarket();
			if (trashMarket != null) trashMarket.sortByCode();

			mp.getMarketTable().update();
		}
	}

	
	private Investor getInvestor() {
		return this;
	}
	
	
	class Option extends JDialog {

		private static final long serialVersionUID = 1L;

		protected JFormattedTextField txtBalanceBase;
		
		protected JFormattedTextField txtBalanceBias;
		
		protected JButton btnBalanceBias;
		
		protected JFormattedTextField txtMarginFee;
		
		protected JButton btnMarginFee;
		
		protected JFormattedTextField txtDayViewInterval;
		
		protected JFormattedTextField txtDayValidInterval;
		
		protected JFormattedTextField txtTimeStartPoint;

		protected JButton btnTimeStartPoint;

		protected JFormattedTextField txtRefLeverage;
		
		protected JFormattedTextField txtRefUnitBias;
		
		protected JTextArea txtDefaultStockCodes;
		
		protected JTextField txtCurDir;
		
		protected JButton btnCurDir;
		
		public Option() {
			super(getInvestor(), "Option", true);
			MarketImpl m = getSelectedMarket();
			Option thisOption = this;
			setTitle("Option for market \"" + m.getName() + "\"");
			
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			setSize(400, 380);
			setLocationRelativeTo(getInvestor());
			setLayout(new BorderLayout());
			
			
			JPanel header = new JPanel(new BorderLayout());
			add(header, BorderLayout.NORTH);
			
			JPanel left = new JPanel(new GridLayout(0, 1));
			header.add(left, BorderLayout.WEST);
			
			left.add(new JLabel("Balance (basic): "));
			left.add(new JLabel("Balance bias: "));
			left.add(new JLabel("Margin fee: "));
			left.add(new JLabel("View interval (days): "));
			left.add(new JLabel("Valid interval (days): "));
			left.add(new JLabel("Start date: "));
			left.add(new JLabel("Referred leverage: "));
			left.add(new JLabel("Referred unit bias: "));
			left.add(new JLabel("Current directory: "));

			JPanel right = new JPanel(new GridLayout(0, 1));
			header.add(right, BorderLayout.CENTER);
			
			JPanel paneBalance = new JPanel(new BorderLayout());
			right.add(paneBalance);
			txtBalanceBase = new JFormattedTextField(Util.getNumberFormatter());
			txtBalanceBase.setValue(m.getBalanceBase());
			paneBalance.add(txtBalanceBase, BorderLayout.CENTER);
			
			JPanel paneBalanceBias = new JPanel(new BorderLayout());
			right.add(paneBalanceBias);
			txtBalanceBias = new JFormattedTextField(Util.getNumberFormatter());
			txtBalanceBias.setValue(m.getBalanceBias());
			paneBalanceBias.add(txtBalanceBias, BorderLayout.CENTER);
			//
			btnBalanceBias = new JButton("Calc");
			btnBalanceBias.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					String txtProvidedBalance = JOptionPane.showInputDialog(thisOption, "Enter provided balance (basic)", getBalanceBase0());
					double providedBalance = 0;
					try {
						providedBalance = Double.parseDouble(txtProvidedBalance);
					}
					catch (Exception ex) {return;}
					txtBalanceBias.setValue(getSelectedMarket().calcBalanceBias(providedBalance, getTimeViewInterval0()));
				}
			});
			paneBalanceBias.add(btnBalanceBias, BorderLayout.EAST);
			
			JPanel paneMarginFee = new JPanel(new BorderLayout());
			right.add(paneMarginFee);
			txtMarginFee = new JFormattedTextField(Util.getNumberFormatter());
			txtMarginFee.setValue(m.getMarginFee());
			paneMarginFee.add(txtMarginFee, BorderLayout.CENTER);
			//
			btnMarginFee = new JButton("Calc");
			btnMarginFee.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					String txtNewMargin = JOptionPane.showInputDialog(thisOption, "Enter new margin", getMargin0());
					double newMargin = 0;
					try {
						newMargin = Double.parseDouble(txtNewMargin);
					}
					catch (Exception ex) {return;}
					txtMarginFee.setValue(getSelectedMarket().calcMarginBias(newMargin, getTimeViewInterval0()));
				}
			});
			paneMarginFee.add(btnMarginFee, BorderLayout.EAST);
			
			JPanel paneDayViewInterval = new JPanel(new BorderLayout());
			right.add(paneDayViewInterval);
			txtDayViewInterval = new JFormattedTextField(Util.getNumberFormatter());
			txtDayViewInterval.setToolTipText("Value 0 specifies viewing al time points");
			txtDayViewInterval.setValue(m.getTimeViewInterval() / (1000*3600*24));
			paneDayViewInterval.add(txtDayViewInterval, BorderLayout.CENTER);
			
			JPanel paneDayValidInterval = new JPanel(new BorderLayout());
			right.add(paneDayValidInterval);
			txtDayValidInterval = new JFormattedTextField(Util.getNumberFormatter());
			txtDayValidInterval.setToolTipText("Value 0 specifies viewing al time points");
			txtDayValidInterval.setValue(m.getTimeValidInterval() / (1000*3600*24));
			paneDayValidInterval.add(txtDayValidInterval, BorderLayout.CENTER);
			
			JPanel paneTimeStartPoint = new JPanel(new BorderLayout());
			right.add(paneTimeStartPoint);
			txtTimeStartPoint = new JFormattedTextField(Util.getDateSimpleFormatter());
			txtTimeStartPoint.setValue(new Date(m.getTimeStartPoint()));
			paneTimeStartPoint.add(txtTimeStartPoint, BorderLayout.CENTER);
			//
			btnTimeStartPoint = new JButton("Now");
			btnTimeStartPoint.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					txtTimeStartPoint.setValue(new Date());
				}
			});
			paneTimeStartPoint.add(btnTimeStartPoint, BorderLayout.EAST);
			
			JPanel paneRefLeverage = new JPanel(new BorderLayout());
			right.add(paneRefLeverage);
			txtRefLeverage = new JFormattedTextField(Util.getNumberFormatter());
			txtRefLeverage.setToolTipText("Value 0 specifies infinity leverage");
			txtRefLeverage.setValue(m.getLeverage() == 0 ? 0 : 1/m.getLeverage());
			paneRefLeverage.add(txtRefLeverage, BorderLayout.CENTER);
			
			JPanel paneRefUnitBias = new JPanel(new BorderLayout());
			right.add(paneRefUnitBias);
			txtRefUnitBias = new JFormattedTextField(Util.getNumberFormatter());
			txtRefUnitBias.setValue(m.getUnitBias());
			paneRefUnitBias.add(txtRefUnitBias, BorderLayout.CENTER);
			
			JPanel paneCurDir = new JPanel(new BorderLayout());
			right.add(paneCurDir);
			txtCurDir = new JTextField();
			File curDir = getInvestor().curDir;
			if (curDir != null && curDir.exists()) {
				txtCurDir.setText(curDir.getAbsolutePath());
			}
			txtCurDir.setEditable(false);
			txtCurDir.setCaretPosition(0);
			paneCurDir.add(txtCurDir, BorderLayout.CENTER);
			//
			btnCurDir = new JButton("Browse");
			btnCurDir.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					String curDirPath = txtCurDir.getText();
					File curDir = new File(curDirPath);
					JFileChooser fc = new JFileChooser(curDir.exists() ? curDir : new File("."));
					
					fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			        if (fc.showOpenDialog(getInvestor()) != JFileChooser.APPROVE_OPTION) return;
			        curDir = fc.getSelectedFile();
			        if (curDir != null && curDir.exists()) txtCurDir.setText(curDir.getAbsolutePath());
				}
			});
			paneCurDir.add(btnCurDir, BorderLayout.EAST);

			
			JPanel body = new JPanel(new BorderLayout());
			add(body, BorderLayout.CENTER);
			JPanel paneDefaultStockCodes = new JPanel(new BorderLayout());
			body.add(paneDefaultStockCodes, BorderLayout.CENTER);
			//
			paneDefaultStockCodes.add(new JLabel("Default stock codes (,): "), BorderLayout.WEST);
			//
			txtDefaultStockCodes = new JTextArea();
			txtDefaultStockCodes.setLineWrap(true);
			txtDefaultStockCodes.setToolTipText("Stock codes are separated by a comma or a new line character");
			txtDefaultStockCodes.setText(MarketAbstract.toCodesText(getSelectedMarket().getDefaultStockCodes()));
			paneDefaultStockCodes.add(new JScrollPane(txtDefaultStockCodes), BorderLayout.CENTER);

			
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
			MarketImpl m = getSelectedMarket(); if (m == null) return;
			double balanceBase = txtBalanceBase.getValue() instanceof Number ? ((Number)txtBalanceBase.getValue()).doubleValue() : 0;
			double balanceBias = txtBalanceBias.getValue() instanceof Number ? ((Number)txtBalanceBias.getValue()).doubleValue() : 0;
			double marginFee = txtMarginFee.getValue() instanceof Number ? ((Number)txtMarginFee.getValue()).doubleValue() : 0;
			long dayViewInterval = txtDayViewInterval.getValue() instanceof Number ? ((Number)txtDayViewInterval.getValue()).longValue() : 0;
			long dayValidInterval = txtDayValidInterval.getValue() instanceof Number ? ((Number)txtDayValidInterval.getValue()).longValue() : 0;
			Date timeStartPoint = txtTimeStartPoint.getValue() instanceof Date ? (Date)txtTimeStartPoint.getValue() : new Date();
			double refLeverage = txtRefLeverage.getValue() instanceof Number ? ((Number)txtRefLeverage.getValue()).doubleValue() : 0;
			double refUnitBias = txtRefUnitBias.getValue() instanceof Number ? ((Number)txtRefUnitBias.getValue()).doubleValue() : 0;
			String codesText = txtDefaultStockCodes.getText();
			File curDir = new File(txtCurDir.getText());
			List<String> codes = codesText != null ? Arrays.asList(codesText.split(Util.DEFAULT_SEP)) : Util.newList(0);
			
			if (dayViewInterval < 0 || dayValidInterval < 0 || (dayViewInterval == 0 && dayValidInterval != 0) || (dayViewInterval > dayValidInterval && dayValidInterval != 0)) {
				JOptionPane.showMessageDialog(this, "Invalidate day interval", "Invalidate day interval", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			m.setBalanceBase(balanceBase);
			m.setBalanceBias(balanceBias);
			m.setMarginFee(marginFee);
			m.setTimeViewInterval(dayViewInterval*1000*3600*24);
			m.setTimeValidInterval(dayValidInterval*1000*3600*24);
			m.setTimeStartPoint(timeStartPoint.getTime());
			m.setLeverage(refLeverage != 0 ? 1/refLeverage : 0);
			m.setUnitBias(refUnitBias);
			if (curDir != null && curDir.exists()) getInvestor().curDir = curDir; 
			
			Universe u = u();
			if (u != null) u.addDefaultStockCodes(codes);
			
			getSelectedMarketPanel().getMarketTable().update();
			//getInvestor().update();
			
			dispose();
		}
		
		private long getTimeViewInterval0() {
			if (txtDayViewInterval != null)
				return txtDayViewInterval.getValue() instanceof Number ? ((Number)txtDayViewInterval.getValue()).longValue() : 0;
			else
				return getSelectedMarket().getTimeViewInterval();
		}
		
		private double getBalanceBase0() {
			if (txtBalanceBase != null)
				return txtBalanceBase.getValue() instanceof Number ? ((Number)txtBalanceBase.getValue()).doubleValue() : 0;
			else
				return getSelectedMarket().getBalanceBase();
		}
		
		private double getMargin0() {
			return getSelectedMarket().getMargin(getTimeViewInterval0());
		}

		
		private Universe u() {
			return getSelectedMarket().getNearestUniverse();
		}
	}

	
	public static void main(String[] args) {
		Investor investor = new Investor(new UniverseImpl());
		File workingJSIDir = new File(StockProperty.WORKING_DIRECTORY);
		if (workingJSIDir.exists() && workingJSIDir.isFile()) {
			investor.addMarketPanel(StockProperty.MARKET_NAME_PREFIX + "1");
			investor.setVisible(true);
			return;
		}
		
		try {
			if (!workingJSIDir.exists()) {
				File parent = workingJSIDir.getParentFile();
				if (parent != null && !parent.exists()) parent.mkdir();
				workingJSIDir.mkdir();
			}
		}
		catch (Exception e) { }
		
		if (!workingJSIDir.exists()) {
			investor.addMarketPanel(StockProperty.MARKET_NAME_PREFIX + "1");
			investor.setVisible(true);
			return;
		}
		
		String[] fileNames = workingJSIDir.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if (name == null || name.isEmpty()) return false;
				int index = name.lastIndexOf(".");
				if (index < 0) return false;
				String ext = name.substring(index + 1);
				return ext != null && !ext.isEmpty() && ext.compareToIgnoreCase(StockProperty.JSI_EXT) == 0;
			}
		});
		
		investor.curDir = workingJSIDir;
		for (String fileName : fileNames) {
			investor.addMarketPanel(new File(workingJSIDir, fileName));
		}
		
		if (investor.getMarketPanels().length == 0) investor.addMarketPanel(StockProperty.MARKET_NAME_PREFIX + "1");
		investor.setVisible(true);
	}
	
	
}



/**
 * This class represents a draggable panel. The class was developed by Tom Martin and Ky Leggiero in 2018.09.13.
 * The source code is available at <a href="https://stackoverflow.com/questions/60269/how-to-implement-draggable-tab-using-java-swing">https://stackoverflow.com/questions/60269/how-to-implement-draggable-tab-using-java-swing</a><br>
 * I thank Tom Martin, Ky Leggiero for their interesting source code.
 * 
 * @author Tom Martin, Ky Leggiero
 * @version 1.0
 *
 */
class DraggableTabbedPane extends JTabbedPane {
	
	
	private static final long serialVersionUID = 1L;


	protected boolean dragging = false;
	
	
	protected Image tabImage = null;
	
	
	protected Point currentMouseLocation = null;
	
	
	protected int draggedTabIndex = 0;

	
	public DraggableTabbedPane() {
		super();
		
		addMouseMotionListener(new MouseMotionAdapter() {
			
			@Override
			public void mouseDragged(MouseEvent e) {
				if(!dragging) {
					int tabNumber = getUI().tabForCoordinate(DraggableTabbedPane.this, e.getX(), e.getY());
					if(tabNumber >= 0) {
						draggedTabIndex = tabNumber;
						Rectangle bounds = getUI().getTabBounds(DraggableTabbedPane.this, tabNumber);

						Image totalImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
						Graphics totalGraphics = totalImage.getGraphics();
						totalGraphics.setClip(bounds);
						setDoubleBuffered(false);
						paintComponent(totalGraphics);

						tabImage = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_INT_ARGB);
						Graphics graphics = tabImage.getGraphics();
						graphics.drawImage(totalImage, 0, 0, bounds.width, bounds.height, bounds.x, bounds.y, bounds.x + bounds.width, bounds.y+bounds.height, DraggableTabbedPane.this);

						dragging = true;
						repaint();
					}
				}
				else {
					currentMouseLocation = e.getPoint();
					repaint();
				}

				super.mouseDragged(e);
			}
		});

		addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				if(dragging) {
					int tabNumber = getUI().tabForCoordinate(DraggableTabbedPane.this, e.getX(), 10);

					if(tabNumber >= 0) {
						Component comp = getComponentAt(draggedTabIndex);
						String title = getTitleAt(draggedTabIndex);
						removeTabAt(draggedTabIndex);
						insertTab(title, null, comp, null, tabNumber);
					}
				}

				dragging = false;
				tabImage = null;
			}
		});
	}


	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		if(dragging && currentMouseLocation != null && tabImage != null) {
			g.drawImage(tabImage, currentMouseLocation.x, currentMouseLocation.y, this);
		}
	}


}



class InvestorTabbedPane extends DraggableTabbedPane {

	private static final long serialVersionUID = 1L;

	
	public InvestorTabbedPane() {

	}

	
}

