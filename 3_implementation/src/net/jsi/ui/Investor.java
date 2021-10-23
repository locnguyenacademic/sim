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
import java.awt.Frame;
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
import java.io.FileReader;
import java.io.FilenameFilter;
import java.rmi.Naming;
import java.rmi.Remote;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

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
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.NumberFormatter;

import net.jsi.Market;
import net.jsi.MarketAbstract;
import net.jsi.MarketImpl;
import net.jsi.Price;
import net.jsi.StockInfo;
import net.jsi.StockInfoStore;
import net.jsi.StockProperty;
import net.jsi.Universe;
import net.jsi.UniverseImpl;
import net.jsi.UniverseRemote;
import net.jsi.UniverseRemoteGetter;
import net.jsi.Util;

public class Investor extends JFrame implements MarketListener {

	
	private static final long serialVersionUID = 1L;
	
	
	private static Investor investor = null;
	
	
	public final static Investor g() {
		return investor;
	}
	
	
	/**
	 * Server synchronization period in seconds.
	 */
	public static long SYNC_SERVER_PERIOD = 60*10; //10-minute period to connect server.

	
	protected Universe universe = null;
	
	
	protected UniverseRemote remoteUniverse = null;
	
	
	protected boolean inServer = false;
	
	
	protected Timer timer = null;
	
	
	protected JTabbedPane body;
	
	
	protected JLabel lblTotalProfit;

	
	protected JLabel lblTotalROI;

	
	protected JLabel lblTotalSurplus;

	
	protected JLabel lblTotalBias;

	
	protected JLabel lblTotalOscill;

	
	protected JLabel lblTotalHedge;

	
	protected File curDir = null;
	
	
	protected MarketPanel curMarketPanel = null;
	
	
	public Investor(Universe universe, UniverseRemote remoteUniverse, boolean inServer) {
		super("JSI - Stock/forex investment manager");
		this.universe = universe;
		this.remoteUniverse = remoteUniverse;
		this.inServer = inServer;
		if (investor == null) investor = this;
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				super.windowClosed(e);
			}
		});
		
		addMouseListener(new MouseAdapter() {});
		
		setSize(800, 600);
		setLocationRelativeTo(null);
	    setJMenuBar(createMenuBar());
		
		setLayout(new BorderLayout());

		JToolBar toolbar = createToolbar();
		if (toolbar != null) add(toolbar, BorderLayout.NORTH);

		body = new InvestorTabbedPane();
		add(body, BorderLayout.CENTER);
		
		JPanel footer = new JPanel(new BorderLayout());
		add(footer, BorderLayout.SOUTH);
		
		JPanel footerRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		footer.add(footerRow, BorderLayout.NORTH);
		
		lblTotalProfit = new JLabel();
		footerRow.add(lblTotalProfit);
		footerRow.add(new JLabel(" "));
		
		lblTotalSurplus = new JLabel();
		footerRow.add(lblTotalSurplus);
		footerRow.add(new JLabel(" "));

		lblTotalROI = new JLabel();
		footerRow.add(lblTotalROI);
		footerRow.add(new JLabel(" "));
		
		lblTotalBias = new JLabel();
		//footerRow.add(lblTotalBias);
		//footerRow.add(new JLabel(" "));
		
		lblTotalOscill = new JLabel();
		//footerRow.add(lblTotalOscill);
		//footerRow.add(new JLabel(" "));

		lblTotalHedge = new JLabel();
		footerRow.add(lblTotalHedge);

		initialize();
		
		body.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				onTabChanged();
			}
		});
	}
	
	
	@Override
	public void dispose() {
		universe.apply();
		onSync();
		
		if (!inServer) {
			Set<String> remoteMarketNames = Util.newSet(0);
			if (remoteUniverse != null) {
				try {
					remoteMarketNames.addAll(remoteUniverse.getMarketNames());
				} catch (Throwable e) {Util.trace(e);}
			}
			
			MarketPanel[] mps = getMarketPanels();
			for (MarketPanel mp : mps) {
				String marketName = mp.getMarket().getName();
				if (!remoteMarketNames.contains(marketName)) mp.autoBackup();
			}
		}
		
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		
		super.dispose();
	}


	private void initialize() {
		for (int i = 0; i < universe.size(); i++) {
			Market market = universe.get(i);
			MarketPanel mp = createMarketPanel(market);
			body.add(market.getName(), mp);
		}
		if (inServer) return;
		
		File workingDir = new File(StockProperty.WORKING_DIRECTORY);
		if (workingDir.exists() && workingDir.isFile()) {
			if (getMarketPanels().length == 0)  addMarketPanel(StockProperty.MARKET_NAME_PREFIX + "1");
			return;
		}
		
		try {
			if (!workingDir.exists()) {
				File parent = workingDir.getParentFile();
				if (parent != null && !parent.exists()) parent.mkdir();
				workingDir.mkdir();
			}
		}
		catch (Exception e) { }
		
		if (!workingDir.exists()) {
			if (getMarketPanels().length == 0) addMarketPanel(StockProperty.MARKET_NAME_PREFIX + "1");
			return;
		}
		
		String[] fileNames = workingDir.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if (name == null || name.isEmpty()) return false;
				int index = name.lastIndexOf(".");
				if (index < 0) return false;
				String ext = name.substring(index + 1);
				return ext != null && !ext.isEmpty() && ext.compareToIgnoreCase(StockProperty.JSI_EXT) == 0;
			}
		});
		
		this.curDir = workingDir;
		Arrays.sort(fileNames);
		for (String fileName : fileNames) {
			File file = new File(workingDir, fileName);
			if (remoteUniverse == null) {
				addMarketPanel(file);
				continue;
			}
			
			String marketName = null;
			try {
				FileReader reader = new FileReader(file);
				marketName = MarketImpl.readMarketName(reader);
				reader.close();
			}
			catch (Throwable e) {Util.trace(e);}
			if (marketName == null) continue;
			
			if (getMarketPanel(marketName) == null) addMarketPanel(file);
		}
		
		if (getMarketPanels().length == 0) addMarketPanel(StockProperty.MARKET_NAME_PREFIX + "1");
		
		if (remoteUniverse != null) {
			if (timer != null) timer.cancel();
			
			timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					onSync();
				}
			}, SYNC_SERVER_PERIOD*1000, SYNC_SERVER_PERIOD*1000);
		}
	}

	
	private void update() {
		int d = Util.DECIMAL_PRECISION_SHORT;
		long timeViewInterval = universe.getTimeViewInterval();
		double balance = universe.getBalance(timeViewInterval);
		double profit = universe.getProfit(timeViewInterval);
		double surplus = balance != 0 ? profit / balance : 0;
		//double roi = universe.getROI(timeViewInterval);
		double lRoi = universe.getROIByLeverage(timeViewInterval);
		//double totalBias = universe.calcBias(timeViewInterval);
		//double totalOscill = universe.calcOscillAbs(timeViewInterval);
		double totalInvest = universe.calcInvestAmount(timeViewInterval);
		
		profit = profit < 0 ? profit : (totalInvest > profit ? profit : profit - totalInvest);
		
		lblTotalProfit.setText("PROFIT: " + Util.format(profit, d));
		lblTotalSurplus.setText("SUR: " + Util.format(surplus*100, d) + "%");
		lblTotalROI.setText("LEV.ROI: " + Util.format(lRoi*100, d) + "%");
		//lblTotalBias.setText("BIAS: " + Util.format(totalBias, d));
		//lblTotalOscill.setText("OSCILL: " + Util.format(totalOscill, d));
		lblTotalHedge.setText("HEDGE: " + (totalInvest > 0 ? 0 : Util.format(-totalInvest, d)));
		
		curMarketPanel = getSelectedMarketPanel();
		System.out.println("Current market updated: " + curMarketPanel.getMarket().getName());
	}
	
	
	private JMenuBar createMenuBar() {
		JMenuBar mnBar = new JMenuBar();
		Component thisInvestor = Util.getDialogForComponent(this);
		
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

		if (remoteUniverse != null && !inServer) {
			JMenuItem mniUpdateFromServer = new JMenuItem(
				new AbstractAction("Update from server") {
					private static final long serialVersionUID = 1L;

					@Override
					public void actionPerformed(ActionEvent e) {
						updateFromServer();
					}
				});
			mniUpdateFromServer.setMnemonic('u');
			mniUpdateFromServer.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
			mnFile.add(mniUpdateFromServer);
		}

		mnFile.addSeparator();

		JMenuItem mniReport = new JMenuItem(
			new AbstractAction("Report") {
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					report();
				}
			});
		mniReport.setMnemonic('r');
		mnFile.add(mniReport);
		
		mnFile.addSeparator();

		JMenuItem mniWatchMarket = new JMenuItem(
		new AbstractAction("Watch stocks") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				Market selectedWatchMarket = getSelectedWatchMarket();
				if (selectedWatchMarket == null) return;
				
				MarketTable tblMarket = getSelectedMarketTable();
				MarketWatchDialog dlgMarket = new MarketWatchDialog(selectedWatchMarket, false, StockProperty.RUNTIME_CASCADE ? tblMarket : null, thisInvestor);
				dlgMarket.setTitle("Watch stocks for market " + tblMarket.getMarket().getName());
				dlgMarket.setVisible(true);
				
				if (dlgMarket.isPressOK())
					tblMarket.apply();
				else {
					int answer= JOptionPane.showConfirmDialog(thisInvestor, "Would you like to to apply changes?", "Applying confirmation", JOptionPane.YES_NO_OPTION);
					if (answer == JOptionPane.YES_OPTION) tblMarket.apply();
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
				Market selectedPlaceMarket = getSelectedPlaceMarket();
				if (selectedPlaceMarket == null) return;
				
				MarketTable tblMarket = getSelectedMarketTable();
				MarketPlaceDialog dlgMarket = new MarketPlaceDialog(selectedPlaceMarket, false, StockProperty.RUNTIME_CASCADE ? tblMarket : null, thisInvestor);
				dlgMarket.setTitle("Place stocks for market " + tblMarket.getMarket().getName());
				dlgMarket.setVisible(true);
				
				if (dlgMarket.isPressOK())
					tblMarket.apply();
				else {
					int answer= JOptionPane.showConfirmDialog(thisInvestor, "Would you like to to apply changes?", "Applaying confirmation", JOptionPane.YES_NO_OPTION);
					if (answer == JOptionPane.YES_OPTION) tblMarket.apply();
				}
			}
		});
		mniPlaceMarket.setMnemonic('p');
		mnFile.add(mniPlaceMarket);

		JMenuItem mniApplyMarket = new JMenuItem(
		new AbstractAction("Apply (changes, placing)") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				Market selectedWatchMarket = getSelectedWatchMarket();
				if (selectedWatchMarket != null) getSelectedMarketTable().apply();
			}
		});
		mniApplyMarket.setMnemonic('a');
		mniApplyMarket.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK));
		mnFile.add(mniApplyMarket);
		
		JMenuItem mniTrashMarket = new JMenuItem(
		new AbstractAction("Trash") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				Market selectedTrashMarket = getSelectedTrashMarket();
				if (selectedTrashMarket == null) return;
				
				MarketTable tblMarket = getSelectedMarketTable();
				MarketTrashDialog dlgMarket = new MarketTrashDialog(selectedTrashMarket, false, StockProperty.RUNTIME_CASCADE ? tblMarket : null, thisInvestor);
				dlgMarket.setTitle("Stocks trash for market " +selectedTrashMarket.getName());
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
		mniExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.ALT_DOWN_MASK));
		mnFile.add(mniExit);

		
		JMenu mnTool = new JMenu("Tool");
		mnTool.setMnemonic('t');
		mnBar.add(mnTool);

		JMenuItem mniPriceList = new JMenuItem(
			new AbstractAction("Price list") {
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					PriceList pl = new PriceList(universe, null, universe.getTimeViewInterval(), true, false, thisInvestor);
					pl.setVisible(true);
					
					if (pl.isApplied()) getSelectedMarketTable().apply();
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
					MarketTable tblMarket = getSelectedMarketTable();
					RecDialog rd = new RecDialog(selectedMarket, selectedMarket.getTimeViewInterval(), StockProperty.RUNTIME_CASCADE ? tblMarket : null, thisInvestor);
					rd.setVisible(true);
					
					if (rd.isPressOK())
						tblMarket.apply();
					else {
						int answer= JOptionPane.showConfirmDialog(thisInvestor, "Would you like to to apply changes?", "Applaying confirmation", JOptionPane.YES_NO_OPTION);
						if (answer == JOptionPane.YES_OPTION) tblMarket.apply();
					}
				}
			});
		mniRecommend.setMnemonic('r');
		mniRecommend.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK));
		mnTool.add(mniRecommend);
		
		JMenuItem mniViewTimeWindow = new JMenuItem(
			new AbstractAction("View time window") {
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					viewTimeWindow();
				}
			});
		mniViewTimeWindow.setMnemonic('v');
		mnTool.add(mniViewTimeWindow);

		mnTool.addSeparator();
		
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

		mnTool.addSeparator();
		
		JMenuItem mniFixMargins = new JMenuItem(
			new AbstractAction("Fix margins") {
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					fixMargins(true);
				}
			});
		mniFixMargins.setMnemonic('f');
		mnTool.add(mniFixMargins);

		JMenuItem mniUnfixMargins = new JMenuItem(
			new AbstractAction("Unfix margins") {
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					fixMargins(false);
				}
			});
		mniUnfixMargins.setMnemonic('u');
		mnTool.add(mniUnfixMargins);

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
		//JToolBar toolbar = new JToolBar(); return toolbar;
		return null;
	}


	private void onOpen() {
		MarketPanel mp = getSelectedMarketPanel();
		if (mp == null) return;
		
		List<String> exclusiveNames = getMarketNames();
		exclusiveNames.remove(mp.getMarket().getName());
		if (mp.onOpen(exclusiveNames)) {
			int index = indexOfMarketPanel(mp);
			if (index >= 0) body.setTitleAt(index, mp.getMarket().getName());
		}
	}
	
	
	private void onSaveAs() {
		MarketPanel mp = getSelectedMarketPanel();
		if (mp != null) {
			mp.onSave();
			onSync();
		}
	}
	
	
	private void onSave() {
		MarketPanel mp = getSelectedMarketPanel();
		if (mp == null)
			return;
		else if (mp.getFile() != null) {
			boolean ret = mp.save(mp.getFile());
			if (ret) {
				onSync();
				JOptionPane.showMessageDialog(this, "Success to save market \"" + mp.getMarket().getName() + "\"", "Save market", JOptionPane.INFORMATION_MESSAGE);
			}
		}
		else {
			mp.onSave();
			onSync();
		}
	}
	
	
	private void onSync() {
		if (remoteUniverse == null || inServer) return;
		try {
			synchronized (remoteUniverse) {remoteUniverse.sync(universe, 0);}
		} catch (Exception e) {Util.trace(e);}
	}

	
	private void updateFromServer() {
		JOptionPane.showMessageDialog(this, "This function not implemented yet");

		if (remoteUniverse == null || inServer) return;
		try {
			synchronized (remoteUniverse) {
				
			}
		} catch (Exception e) {Util.trace(e);}
	}
	
	
	private void report() {
		JOptionPane.showMessageDialog(this, "This function not implemented yet");
	}
	
	
	private void onTabChanged() {
		MarketPanel selected = getSelectedMarketPanel();
		if (selected == curMarketPanel) return;
		
		curMarketPanel = selected;
		curMarketPanel.getMarketTable().apply();
	}
	
	
	private MarketPanel createMarketPanel(String name) {
		Market market = universe.newMarket(name, universe.getLeverage(), universe.getUnitBias());
		if (!universe.add(market))
			return null;
		else
			return createMarketPanel(market);
	}
	
	
	private MarketPanel createMarketPanel(Market market) {
		MarketPanel mp = new MarketPanel(market, false, this) {

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
	
	
	private void addMarketPanel() {
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
	
	
	private MarketPanel addMarketPanel(String marketName) {
		if (marketName == null || marketName.isEmpty()) return null;
		if (getMarketPanel(marketName) != null) return null;
		
		MarketPanel mp = createMarketPanel(marketName);
		if (mp == null) return null;
		body.add(mp.getMarket().getName(), mp);
		
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
		
		MarketPanel mp = createMarketPanel(marketName);
		if (mp == null) return null;
		boolean ret = mp.open(file);
		if (!ret) return null;
		
		marketName = mp.getMarket().getName();
		if (getMarketPanel(marketName) != null) return null;
		body.add(marketName, mp);
		
		return mp;
	}

		
	private void renameMarketPanel() {
		MarketImpl selectedMarket = getSelectedMarket();
		if (selectedMarket == null) return;
		MarketPanel[] mps = getMarketPanels();
		String newMarketName = JOptionPane.showInputDialog(this, "Enter new market name", "Market " + (mps.length + 1));
		if (newMarketName == null) return;
		
		newMarketName = newMarketName.trim();
		if (newMarketName.isEmpty())
			JOptionPane.showMessageDialog(this, "Empty market name", "Empty market name", JOptionPane.ERROR_MESSAGE);
		else if (getMarketPanel(newMarketName) != null)
			JOptionPane.showMessageDialog(this, "Duplicated market name", "Duplicated market name", JOptionPane.ERROR_MESSAGE);
		else {
			String oldMarketName = selectedMarket.getName();
			boolean ret = universe.rename(oldMarketName, newMarketName);
			if (!ret) {
				JOptionPane.showMessageDialog(this, "Impossible to rename market", "Impossible to rename market", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			if (remoteUniverse != null && !inServer) {
				try {
					remoteUniverse.rename(oldMarketName, newMarketName);
				} catch (Throwable e) {Util.trace(e);}
			}
			int index = getSelectedMarketPanelIndex();
			body.setTitleAt(index, newMarketName);
		}
	}

	
	private void removeSelectedMarketPanel() {
		MarketPanel[] mps = getMarketPanels();
		if (mps.length == 0) return;
		if (mps.length < 2) {
			JOptionPane.showMessageDialog(this, "Universe has 1 market at least", "Impossible to remove market", JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		MarketPanel mp = getSelectedMarketPanel();
		if (mp == null) return;
		int index = universe.lookup(mp.getMarket().getName());
		if (index < 0) return;
		
		if (!inServer && remoteUniverse == null) mp.autoBackup();
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
	
	
	private MarketPanel getSelectedMarketPanel() {
		Component comp = body.getSelectedComponent();
		if ((comp != null) && (comp instanceof MarketPanel))
			return (MarketPanel)comp;
		return
			null;
	}
	
	
	private MarketTable getSelectedMarketTable() {
		MarketPanel mp = getSelectedMarketPanel();
		if (mp == null)
			return null;
		else
			return mp.getMarketTable();
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

	
	private List<MarketImpl> getMarkets() {
		MarketPanel[] mps = getMarketPanels();
		List<MarketImpl> ms = Util.newList(mps.length);
		for (int i = 0; i < mps.length; i++) {
			MarketImpl m = universe.c(mps[i].getMarket());
			if (m != null) ms.add(m);
		}
		
		return ms;
	}
	
	
	private List<String> getMarketNames() {
		List<MarketImpl> ms = getMarkets();
		List<String> names = Util.newList(ms.size());
		for (MarketImpl m : ms) names.add(m.getName());
		
		return names;
	}
	
	
	private long enterTimeInterval() {
		long timeInterval = 0;
		List<MarketImpl> markets = getMarkets();
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
	
	
	private void viewTimeWindow() {
		long timeInterval = enterTimeInterval();
		if (timeInterval < 0) {
			JOptionPane.showMessageDialog(this, "Invalid time interval", "Invalid time interval", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		MarketImpl market = getSelectedMarket();
		long recoveredTimeViewInterval = market.getTimeViewInterval();
		
		market.setTimeViewInterval(timeInterval);
		MarketDialog md = new MarketDialog(market, false, null, this) {

			private static final long serialVersionUID = 1L;

			@Override
			protected MarketPanel createMarketPanel(Market market, boolean group, MarketListener superListener) {
				MarketPanel mp = new MarketPanel(market, group, superListener) {

					private static final long serialVersionUID = 1L;

					@Override
					protected JPopupMenu createContextMenu() {
						JPopupMenu ctxMenu = super.createContextMenu();
						
						JMenuItem trash = new JMenuItem("Trash");
						trash.addActionListener( 
							new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									trash();
								}
							});
						ctxMenu.add(trash);

						return ctxMenu;
					}
					
					private void trash() {
						MarketTrashDialog dlgMarket = new MarketTrashDialog(tblMarket.getTrashMarket(), false, StockProperty.RUNTIME_CASCADE ? tblMarket : null, tblMarket);
						dlgMarket.setTitle("Stocks trash for market " + tblMarket.getMarket().getName());
						dlgMarket.setVisible(true);
						
						if (dlgMarket.isPressOK())
							tblMarket.update();
						else {
							int answer= JOptionPane.showConfirmDialog(tblMarket, "Would you like to to refresh stocks?", "Refresh confirmation", JOptionPane.YES_NO_OPTION);
							if (answer == JOptionPane.YES_OPTION) tblMarket.update();
						}
					}
					
				};
				mp.enableContext = true;
				return mp;
			}
			
		};
		md.setVisible(true);
		
		market.setTimeViewInterval(recoveredTimeViewInterval);
		getSelectedMarketTable().update();
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
		}
		
		getSelectedMarketTable().update();
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
		}
		
		getSelectedMarketTable().update();
	}
	
	
	private void sortAllCodes() {
		int answer= JOptionPane.showConfirmDialog(this, "Are you sure to resort codes?", "Resort confirmation", JOptionPane.YES_NO_OPTION);
		if (answer != JOptionPane.YES_OPTION) return;

		MarketPanel[] mps = getMarketPanels();
		if (mps.length == 0) return;
		for (MarketPanel mp : mps) {
			MarketImpl market = universe.c(mp.getMarket());
			if (market == null) continue;
			
			market.sortCodes();
			MarketImpl watchMarket = market.getWatchMarket();
			if (watchMarket != null) watchMarket.sortCodes();
			MarketImpl placeMarket = market.getPlaceMarket();
			if (placeMarket != null) placeMarket.sortCodes();
			MarketImpl trashMarket = market.getTrashMarket();
			if (trashMarket != null) trashMarket.sortCodes();
		}
		
		getSelectedMarketTable().update();
		
		try {
			if (remoteUniverse != null && !inServer) remoteUniverse.sortCodes();
		} catch (Exception e) {Util.trace(e);}
	}

	
	private void fixMargins(boolean fixed) {
		int answer= JOptionPane.showConfirmDialog(this, "Are you sure to " + (fixed ? "fix" : "unfix") + " margins?", "Fixing margins confirmation", JOptionPane.YES_NO_OPTION);
		if (answer != JOptionPane.YES_OPTION) return;

		MarketImpl m = getSelectedMarket();
		if (m == null) return;
		
		m.fixMargin(fixed);
		getSelectedMarketTable().update();
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
		
		protected JFormattedTextField txtPriceFactor;

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
			setSize(400, 420);
			setLocationRelativeTo(getInvestor());
			setLayout(new BorderLayout());
			
			JPanel header = new JPanel(new BorderLayout());
			add(header, BorderLayout.NORTH);
			
			JPanel left = new JPanel(new GridLayout(0, 1));
			header.add(left, BorderLayout.WEST);
			
			left.add(new JLabel("Balance (basic): "));
			left.add(new JLabel("Balance bias: "));
			left.add(new JLabel("Margin fee: "));
			left.add(new JLabel("Price factor: "));
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
			
			JPanel panePriceFactor = new JPanel(new BorderLayout());
			right.add(panePriceFactor);
			txtPriceFactor = new JFormattedTextField(Util.getNumberFormatter());
			txtPriceFactor.setToolTipText("Be careful to set this option different from 1");
			txtPriceFactor.setValue(StockProperty.PRICE_FACTOR);
			panePriceFactor.add(txtPriceFactor, BorderLayout.CENTER);

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

			double priceFactor = txtPriceFactor.getValue() instanceof Number ? ((Number)txtPriceFactor.getValue()).doubleValue() : 0;
			if (priceFactor > 0 && priceFactor != 1) {
				StockInfoStore store = universe.getCreateStore(m.getName());
				Set<String> allCodes = store.codes();
				for (String code : allCodes) {
					StockInfo info = store.get(code);
					List<Price> prices = info.getPricePool().getInternals();
					for (Price price : prices) price.applyFactor(priceFactor);
				}
			}
			
			getSelectedMarketTable().update();
			
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

	
	public static void start(String[] args) {
		JDialog connector = new JDialog((Frame)null, "Connect to server", true);
		connector.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		connector.setSize(300, 200);
		connector.setLocationRelativeTo(null);
		connector.setLayout(new BorderLayout());
		
		JPanel header = new JPanel(new BorderLayout());
		connector.add(header, BorderLayout.NORTH);
		
		JPanel left = new JPanel(new GridLayout(0, 1));
		header.add(left, BorderLayout.WEST);
		
		left.add(new JLabel("Host:"));
		left.add(new JLabel("Port:"));
		left.add(new JLabel("User name:"));
		left.add(new JLabel("Password:"));

		JPanel right = new JPanel(new GridLayout(0, 1));
		header.add(right, BorderLayout.CENTER);
		
		JTextField txtHost = new JTextField("localhost");
		right.add(txtHost);
		
		JFormattedTextField txtPort = new JFormattedTextField(new NumberFormatter());
		txtPort.setValue(10151);
		right.add(txtPort);
		
		JTextField txtUsername = new JTextField("admin");
		right.add(txtUsername);

		JPasswordField txtPassword = new JPasswordField("admin");
		right.add(txtPassword);

		
		JPanel footer = new JPanel();
		connector.add(footer, BorderLayout.SOUTH);
		
		JButton btnConnect = new JButton("Connect");
		btnConnect.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String host = txtHost.getText().trim();
				int port = txtPort.getValue() instanceof Number ? ( (Number) txtPort.getValue()).intValue() : 0; 
				String username = txtUsername.getText();
				@SuppressWarnings("deprecation")
				String password = txtPassword.getText();

				connector.dispose();

				String uri = "rmi://" + host;
				uri = port < 1 ? uri + "/" + "extragateway" : uri + ":" + port + "/" + "extragateway";

				UniverseRemote remoteUniverse = null;
				try {
					Remote extraGateway = Naming.lookup(uri);
					if (extraGateway != null && extraGateway instanceof UniverseRemoteGetter)
						remoteUniverse = ((UniverseRemoteGetter)extraGateway).getUniverseRemote(username, password);
				}
				catch (Exception ex) {ex.printStackTrace();}
				
				if (remoteUniverse == null) {
					JOptionPane.showMessageDialog(null, "Impossible to connect server.\nTherefore running local investor.", "Local investtor", JOptionPane.WARNING_MESSAGE);
					new Investor(new UniverseImpl(), null, false).setVisible(true);
				}
				else {
					UniverseImpl universe = new UniverseImpl();
					universe.sync(remoteUniverse, 0, false);
					new Investor(universe, remoteUniverse, false).setVisible(true);
				}
				
			}
		});
		footer.add(btnConnect);
		
		JButton btnClose = new JButton("Local");
		btnClose.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				connector.dispose();
				JOptionPane.showMessageDialog(null, "Not to connect server.\nTherefore running local investor.", "Local investtor", JOptionPane.INFORMATION_MESSAGE);
				new Investor(new UniverseImpl(), null, false).setVisible(true);
			}
		});
		footer.add(btnClose);
		
		connector.setVisible(true);
	}
	
	
	public static void main(String[] args) {
		start(args);
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

