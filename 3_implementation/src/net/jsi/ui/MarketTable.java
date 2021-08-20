package net.jsi.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import net.hudup.core.logistic.ui.UIUtil;
import net.jsi.Market;
import net.jsi.MarketImpl;
import net.jsi.Price;
import net.jsi.Stock;
import net.jsi.StockGroup;
import net.jsi.StockImpl;
import net.jsi.Universe;
import net.jsi.Util;

public class MarketTable extends JTable {

	
	private static final long serialVersionUID = 1L;

	
	public MarketTable(Market market, boolean forStock) {
		super();
		setModel(new MarketTableModel(market, forStock));

		setAutoCreateRowSorter(true);
		setAutoResizeMode(AUTO_RESIZE_OFF);
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(SwingUtilities.isRightMouseButton(e) ) {
					JPopupMenu contextMenu = createContextMenu();
					if(contextMenu != null)
						contextMenu.show((Component)e.getSource(), e.getX(), e.getY());
				}
			}
		});
		
		update();
	}

	
	protected JPopupMenu createContextMenu() {
		if (!getModel2().isForStock()) return null;

		JPopupMenu ctxMenu = new JPopupMenu();
		Stock stock = getSelectedStock();
		MarketTable tblMarket = this;
		
		JMenuItem miTake = new JMenuItem("Take new");
		miTake.addActionListener( 
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					StockTaker taker = new StockTaker(getMarket(), stock, false, tblMarket);
					taker.setVisible(true);
					if (taker.getOutput() != null) update();
				}
			});
		ctxMenu.add(miTake);
		
		if (stock != null) {
			JMenuItem miModify = new JMenuItem("Modify");
			miModify.addActionListener( 
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						StockTaker taker = new StockTaker(getMarket(), stock, true, tblMarket);
						taker.setVisible(true);
						if (taker.getOutput() != null) update();
					}
				});
			ctxMenu.add(miModify);
			
			JMenuItem miDelete = new JMenuItem("Delete");
			miDelete.addActionListener( 
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						Universe universe = getMarket().getNearestUniverse();
						if (universe == null) return;
						MarketImpl mi = ((Universe)universe).c(getMarket());
						
						mi.removeStock(stock.code(), mi.c(stock).getTimePoint());
						update();
					}
				});
			ctxMenu.add(miDelete);
		}

		ctxMenu.addSeparator();
		
		JMenuItem miSummary = new JMenuItem("Summary");
		miSummary.addActionListener( 
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JDialog dlgGroup = new JDialog(UIUtil.getFrameForComponent(tblMarket), "Stock summary", true);
					
					dlgGroup.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
					dlgGroup.setSize(600, 400);
					dlgGroup.setLocationRelativeTo(Util.getFrameForComponent(tblMarket));
					dlgGroup.setLayout(new BorderLayout());
					
					JPanel body = new JPanel(new BorderLayout());
					dlgGroup.add(body, BorderLayout.CENTER);
					
					MarketTable tblGroup = new MarketTable(getMarket(), false);
					body.add(new JScrollPane(tblGroup), BorderLayout.CENTER);
					
					JPanel footer = new JPanel();
					dlgGroup.add(footer, BorderLayout.SOUTH);
					
					JButton ok = new JButton("OK");
					ok.addActionListener(new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent e) {
							dlgGroup.dispose();
						}
					});
					footer.add(ok);
					
					dlgGroup.setVisible(true);
				}
			});
		ctxMenu.add(miSummary);

		return ctxMenu;
	}

	
	public MarketTableModel getModel2() {
		return (MarketTableModel) getModel();
	}
	
	
	public Market getMarket() {
		return getModel2().getMarket();
	}
	
	
	public void update() {
		getModel2().update();
	}
	
	
	public Stock getSelectedStock() {
		int selectedRow = getSelectedRow();
		if (selectedRow < 0) return null;
		return (Stock) getValueAt(selectedRow, 0);
	}
	
	
	@Override
	public TableCellRenderer getCellRenderer(int row, int column) {
		Object value = getValueAt(row, column);
		if (value == null)
			return super.getCellRenderer(row, column);
		else {
			TableCellRenderer renderer = getDefaultRenderer(value.getClass());
			if(renderer == null)
				return super.getCellRenderer(row, column);
			else
				return renderer;
		}
	}

	
	@Override
    public TableCellEditor getCellEditor(int row, int column) {
		Object value = getValueAt(row, column);
		if (value == null)
			return super.getCellEditor(row, column);
		else {
			TableCellEditor editor = getDefaultEditor(value.getClass());
			if(editor == null)
				return super.getCellEditor(row, column);
			else
				return editor;
		}
    }

}



class MarketTableModel extends DefaultTableModel implements TableModelListener {

	
	private static final long serialVersionUID = 1L;

	
	protected Market market = null;
	
	
	protected boolean forStock = true;
	
	
	public MarketTableModel(Market market, boolean forStock) {
		this.market = market;
		this.forStock = forStock;
	}
	
	
	public Market getMarket() {
		return market;
	}
	
	
	public boolean isForStock() {
		return forStock;
	}
	
	
	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}

	
	@Override
	public void tableChanged(TableModelEvent e) {

	}
	

	public void update() {
		setDataVector(new Object[][] {}, new Object[] {});
		
		Vector<Vector<Object>> data = Util.newVector(0);
		
		if (forStock) {
			List<Stock> stocks = market.getStocks(market.getTimeViewInterval());
			for (Stock stock : stocks) {
				Vector<Object> row = toRow(stock);
				if (row != null) data.add(row);
			}
		}
		else {
			Universe universe = market.getNearestUniverse();
			if (universe != null) {
				MarketImpl mi = ((Universe)universe).c(market);
				for (int i = 0; i < mi.size(); i++) {
					StockGroup group = mi.get(i);
					Vector<Object> row = toRow(group);
					if (row != null) data.add(row);
				}
			}

		}
		
		setDataVector(data, toColumns());
	}
	
	
	private Vector<Object> toRow(Stock stock) {
		long timeViewInterval = market.getTimeViewInterval();
		Vector<Object> row = Util.newVector(0);
		
		if (forStock) { 
			StockImpl sp = market.c(stock);
			if (sp == null || !sp.isValid(timeViewInterval)) return null;

			row.add(stock);
			row.add(stock.isBuy());
			row.add(Util.format(sp.getTakenPrice(timeViewInterval).getDate()));
			row.add(sp.getVolume(timeViewInterval, false));
			row.add(sp.getAverageTakenPrice(timeViewInterval));
			
			Price price = sp.getPrice();
			row.add(price.get());
			row.add(price.getLow());
			row.add(price.getHigh());
			
			row.add(sp.isCommitted());
			row.add(sp.getMargin(timeViewInterval));
			row.add(sp.getProfit(timeViewInterval));
		}
		else {
			StockGroup group = (StockGroup)stock;
			
			row.add(group);
			row.add(group.isBuy());
			row.add(group.getLeverage());
			row.add(group.getVolume(timeViewInterval, true));
			row.add(group.getTakenValue(timeViewInterval));
			row.add(group.getMargin(timeViewInterval));
			row.add(group.getProfit(timeViewInterval));
		}

		return row;
	}
	
	
	/**
	 * Getting list of column names.
	 * @return list of column names.
	 */
	private Vector<String> toColumns() {
		Vector<String> columns = Util.newVector(0);
		
		if (forStock) {
			columns.add("Code");
			columns.add("Buy");
			columns.add("Date");
			columns.add("Volume");
			columns.add("Taken price");
			columns.add("Price");
			columns.add("Low price");
			columns.add("High price");
			columns.add("Committed");
			columns.add("Margin");
			columns.add("Profit");
		}
		else {
			columns.add("Code");
			columns.add("Buy");
			columns.add("Leverage");
			columns.add("Volume");
			columns.add("Taken value");
			columns.add("Margin");
			columns.add("Profit");
		}
		
		return columns;
	}


}
