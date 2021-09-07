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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import net.jsi.Market;
import net.jsi.MarketImpl;
import net.jsi.Stock;
import net.jsi.StockProperty;
import net.jsi.Universe;
import net.jsi.Util;

public class MarketPanel extends JPanel implements MarketListener {


	private static final long serialVersionUID = 1L;

	
	protected JCheckBox chkShowCommit;
	
	
	protected JLabel lblStartTime;
			
			
	protected JLabel lblBalance;
	
	
	protected JLabel lblEquity;

	
	protected JLabel lblMargin;

	
	protected JLabel lblFreeMargin;

	
	protected JLabel lblMarginLevel;

	
	protected JLabel lblProfit;

	
	protected JLabel lblROI;

	
	protected JLabel lblBias;

	
	protected JLabel lblEstInvest;

	
	protected MarketTable tblMarket = null;
	
	
	protected File file = null;
	
	
	public MarketPanel(Market market) {
		tblMarket = createMarketTable(market);
		tblMarket.getModel2().addMarketListener(this);
		setLayout(new BorderLayout());
		
		MarketPanel thisPanel = this;
		MarketImpl m = tblMarket.m();

		JPanel header = new JPanel(new BorderLayout());
		add(header, BorderLayout.NORTH);

		JPanel toolbar1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		header.add(toolbar1, BorderLayout.WEST);
		
		lblStartTime = new JLabel(Util.formatSimple(new Date(m.getTimeStartPoint())) + " -- " + Util.formatSimple(new Date()));
		toolbar1.add(lblStartTime);

		JPanel toolbar2 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		header.add(toolbar2, BorderLayout.EAST);
		
		JButton take = new JButton("Take new");
		take.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Stock stock = tblMarket.getSelectedStock();
				take(stock, false);
			}
		});
		take.setMnemonic('n');
		toolbar2.add(take);
		
		JButton summary = new JButton("Summary");
		summary.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				MarketSummary ms = new MarketSummary(getMarket(), StockProperty.RUNTIME_CASCADE ? tblMarket : null, thisPanel);
				ms.setVisible(true);
				if (!StockProperty.RUNTIME_CASCADE) tblMarket.update();
			}
		});
		summary.setMnemonic('s');
		toolbar2.add(summary);

		
		JPanel body = new JPanel(new BorderLayout());
		add(body, BorderLayout.CENTER);
		body.add(new JScrollPane(tblMarket), BorderLayout.CENTER);
		
		JPanel paneMarket = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		body.add(paneMarket, BorderLayout.SOUTH);
		
		chkShowCommit = new JCheckBox("Show commit");
		chkShowCommit.setSelected(true);
		chkShowCommit.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (tblMarket.isShowCommit() != chkShowCommit.isSelected()) {
					tblMarket.setShowCommit(chkShowCommit.isSelected());
					tblMarket.update();
				}
				
				if (chkShowCommit.isSelected())
					chkShowCommit.setText("Show commit");
				else
					chkShowCommit.setText("Hide commit");
			}
		});
		paneMarket.add(chkShowCommit);

		
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
		footerRow2.add(lblEstInvest = new JLabel());
		
		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Component parent = thisPanel.getParent();
				if (!(parent instanceof InvestorTabbedPane)) return;
				
				if(SwingUtilities.isRightMouseButton(e) ) {
					JPopupMenu contextMenu = createContextMenu();
					if(contextMenu != null) contextMenu.show((Component)e.getSource(), e.getX(), e.getY());
				}
				else if (e.getClickCount() >= 2) {
					onDoubleClick();
				}
			}
		});
		
		update();
	}
	
	
	protected JPopupMenu createContextMenu() {
		JPopupMenu ctxMenu = new JPopupMenu();
		
		JMenuItem watchStocks = new JMenuItem("Watch stocks");
		watchStocks.addActionListener( 
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					watchStocks();
				}
			});
		ctxMenu.add(watchStocks);

		JMenuItem placeStocks = new JMenuItem("Place stocks");
		placeStocks.addActionListener( 
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					placeStocks();
				}
			});
		ctxMenu.add(placeStocks);

		return ctxMenu;
	}
	
	
	protected void onDoubleClick() {
		watchStocks();
	}
	
	
	private void watchStocks() {
		MarketWatchDialog dlgMarket = new MarketWatchDialog(tblMarket.getWatchMarket(), StockProperty.RUNTIME_CASCADE ? tblMarket : null, this);
		dlgMarket.setTitle("Watch stocks for market " + tblMarket.getMarket().getName());
		dlgMarket.setVisible(true);
		
		tblMarket.applyPlace();
	}
	
	
	private void placeStocks() {
		MarketPlaceDialog dlgMarket = new MarketPlaceDialog(tblMarket.getPlaceMarket(), StockProperty.RUNTIME_CASCADE ? tblMarket : null, this);
		dlgMarket.setTitle("Place stocks for market " + tblMarket.getMarket().getName());
		dlgMarket.setVisible(true);
		
		tblMarket.applyPlace();
	}

	
	protected Market getMarket() {
		return tblMarket.getMarket();
	}
	
	
	protected MarketTable getMarketTable() {
		return tblMarket;
	}
	
	
	protected File getWorkingDirectory() {
		return null;
	}
	
	
	protected MarketTable createMarketTable(Market market) {
		return new MarketTable(market, true, null);
	}
	
	
	protected void take(Stock input, boolean update) {
		if (update && input == null) return;
		StockTaker taker = new StockTaker(getMarket(), input, update, this);
		taker.setVisible(true);
		if (taker.getOutput() != null) tblMarket.update();
	}
	
	
	protected void update() {
		Market market = getMarket();
		MarketImpl m = tblMarket.m();

		long timeViewInterval = market.getTimeViewInterval();
		double balance = market.getBalance(timeViewInterval);
		double margin = market.getMargin(timeViewInterval);
		double freeMargin = market.getFreeMargin(timeViewInterval);
		double equity = margin + freeMargin;
		double profit = market.getProfit(timeViewInterval);
		double roi = market.getROI(timeViewInterval);
		double bias = getMarket().calcTotalBias(timeViewInterval);
		double estInvest = market.calcInvestAmount(timeViewInterval);
		
		if (m != null)
			lblStartTime.setText(Util.formatSimple(new Date(m.getTimeStartPoint())) + " -- " + Util.formatSimple(new Date()));

		lblBalance.setText("Balance: " + Util.format(balance));
		lblEquity.setText("Equity: " + Util.format(equity));
		lblMargin.setText("Margin: " + Util.format(margin));
		lblFreeMargin.setText("Free margin: " + Util.format(freeMargin));
		lblMarginLevel.setText("Margin level: " + Util.format((margin != 0 ? equity / margin : 0)*100) + "%");
		
		lblProfit.setText("Profit: " + Util.format(profit));
		lblROI.setText("ROI: " + Util.format(roi*100) + "%");
		lblBias.setText("Bias: " + Util.format(bias));
		lblEstInvest.setText("Est. invest: " + Util.format(estInvest));
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
		if (save0(file)) {
			this.file = file;
			return true;
		}
		else
			return false;
	}
	
	
	private boolean save0(File file) {
		if (file == null) return false;
        try {
        	FileWriter writer = new FileWriter(file);
        	boolean ret = tblMarket.save(writer);
        	writer.close();

            return ret;
        }
        catch (Exception e) {
        	e.printStackTrace();
        }
        
        return false;
	}
	
	
	protected File getFile() {
		return file;
	}
	
	
	protected void dispose() {
		Universe u = getMarket().getNearestUniverse();
		if (u != null) {
			MarketImpl m = u.c(getMarket());
			if (m != null) m.applyPlace();
		}
		
		String backupExt = "" + Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		if (this.file != null) {
			if (save(this.file)) {
				try {
					File backupFile = new File(file.getAbsolutePath() + "." + backupExt);
					save(backupFile);
				}
				catch (Exception e) {}
				
				return;
			}
		}
		else {
			File workingDir = getWorkingDirectory();
			if (workingDir != null && workingDir.exists() && workingDir.isDirectory()) {
				File file = new File(workingDir, getMarket().getName() + "." + StockProperty.JSI_EXT);
				if (save(file)) {
					try {
						File backupFile = new File(file.getAbsolutePath() + "." + backupExt);
						save(backupFile);
					}
					catch (Throwable e) {}
					
					return;
				}
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



class MarketDialog extends JDialog {

	
	private static final long serialVersionUID = 1L;
	
	
	protected JButton btnOK;
	
	
	protected JButton btnCancel;
	
	
	private boolean isPressOK = false;
	
	
	public MarketDialog(Market market, MarketListener listener, Component parent) {
		super(Util.getDialogForComponent(parent), "Market " + market.getName(), true);
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
			}
		});
		
		addMouseListener(new MouseAdapter() { });
		
		setSize(600, 400);
		setLocationRelativeTo(null);
		//setJMenuBar(createMenuBar());
		
		setLayout(new BorderLayout());

		//JToolBar toolbar = createToolbar();
		//if (toolbar != null) add(toolbar, BorderLayout.NORTH);

		JPanel body = new JPanel(new BorderLayout());
		add(body, BorderLayout.CENTER);
		MarketPanel mp = createMarketPanel(market);
		if (mp.getMarketTable() != null && listener != null && StockProperty.RUNTIME_CASCADE)
			mp.getMarketTable().getModel2().addMarketListener(listener);
		body.add(mp, BorderLayout.CENTER);
		
		JPanel footer = new JPanel();
		add(footer, BorderLayout.SOUTH);
		
		btnOK = new JButton("OK");
		btnOK.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				isPressOK = true;
				dispose();
			}
		});
		footer.add(btnOK);
		
		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		footer.add(btnCancel);
	}
	
	
	public boolean isPressOK() {
		return isPressOK;
	}
	
	
	protected MarketPanel createMarketPanel(Market market) {
		return new MarketPanel(market);
	}
	
	
}





