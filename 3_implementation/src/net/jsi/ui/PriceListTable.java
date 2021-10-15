package net.jsi.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import net.jsi.Estimator;
import net.jsi.Market;
import net.jsi.MarketImpl;
import net.jsi.Price;
import net.jsi.PricePool;
import net.jsi.PricePool.TakenStockPrice;
import net.jsi.Stock;
import net.jsi.StockAbstract;
import net.jsi.StockGroup;
import net.jsi.StockImpl;
import net.jsi.StockProperty;
import net.jsi.TakenPrice;
import net.jsi.Universe;
import net.jsi.Util;

public class PriceListTable extends JTable {

	
	private static final long serialVersionUID = 1L;
	
	
	public final static Color LIGHTGRAY = new Color(200, 200, 200);
	

	protected DateCellRenderer dateCellRenderer = new DateCellRenderer();

	
	public PriceListTable(Universe universe, long timeInterval) {
		this(universe, null, timeInterval);
	}

		
	public PriceListTable(Universe universe, String code, long timeInterval) {
		super();
		setModel(new PriceListTableModel(universe, timeInterval));

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (!isEditable()) return;
				
				if(SwingUtilities.isRightMouseButton(e) ) {
					JPopupMenu contextMenu = createContextMenu();
					if(contextMenu != null)
						contextMenu.show((Component)e.getSource(), e.getX(), e.getY());
				}
				else if (e.getClickCount() >= 2) {
					if (getSelectedColumn() == 1) edit(null);
				}
			}
		});

		
		getTableHeader().setReorderingAllowed(false);
		setDefaultRenderer(Date.class, dateCellRenderer);

		update(code);
	}
	
	
	private JPopupMenu createContextMenu() {
		JPopupMenu ctxMenu = new JPopupMenu();
		Price selectedPrice = getSelectedPrice();
		if (selectedPrice == null) return null;
		
		JMenuItem miEdit = new JMenuItem("Edit");
		miEdit.addActionListener( 
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					edit(selectedPrice);
				}
			});
		ctxMenu.add(miEdit);

		JMenuItem miDelete = new JMenuItem("Delete");
		miDelete.addActionListener( 
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					delete();
				}
			});
		ctxMenu.add(miDelete);
		
		ctxMenu.addSeparator();
		
		JMenuItem miTakenStocks = new JMenuItem("Taken stocks");
		miTakenStocks.addActionListener( 
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					listTakenStocks();
				}
			});
		ctxMenu.add(miTakenStocks);
		
		return ctxMenu;
	}

	
	private void delete() {
		PriceListTableModel m = getModel2();
		List<Price> removedPrices = Util.newList(0);
		int[] selectedRows = getSelectedRows();
		if (selectedRows == null) return;
		for (int selectedRow : selectedRows) {
			if (!m.isSelectAsTakenPrice(selectedRow))
				removedPrices.add(m.getPriceAt(selectedRow));
		}

		for (Price removedPrice : removedPrices) {
			int removedRow = m.rowOf(removedPrice);
			if (removedRow >= 0) m.removeRow(removedRow);
		}
	}
	
	
	private void edit(Price input) {
		input = input != null ? input : getSelectedPrice();
		if (input == null) return;
		JDialog editor = new JDialog(Util.getDialogForComponent(this), "Edit price", true);
		String code = getModel2().pricePool != null ? getModel2().pricePool.code() : null;
		if (code != null) editor.setTitle(editor.getTitle() + " of " + code);
		
		editor.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		editor.setSize(350, 250);
		editor.setLocationRelativeTo(Util.getDialogForComponent(this));
		editor.setLayout(new BorderLayout());
		
		JPanel header = new JPanel(new BorderLayout());
		editor.add(header, BorderLayout.NORTH);
		
		JPanel left = new JPanel(new GridLayout(0, 1));
		header.add(left, BorderLayout.WEST);
		
		left.add(new JLabel("Price (*): "));
		left.add(new JLabel("Low price: "));
		left.add(new JLabel("High price: "));
		left.add(new JLabel("Alt price: "));
		left.add(new JLabel("Last date: "));

		JPanel right = new JPanel(new GridLayout(0, 1));
		header.add(right, BorderLayout.CENTER);
		
		JPanel panePrice = new JPanel(new BorderLayout());
		right.add(panePrice);
		JFormattedTextField txtPrice = new JFormattedTextField(Util.getNumberFormatter());
		txtPrice.setValue(input.get());
		panePrice.add(txtPrice, BorderLayout.CENTER);
		
		JPanel paneLowPrice = new JPanel(new BorderLayout());
		right.add(paneLowPrice);
		JFormattedTextField txtLowPrice = new JFormattedTextField(Util.getNumberFormatter());
		txtLowPrice.setValue(input.getLow());
		paneLowPrice.add(txtLowPrice, BorderLayout.CENTER);
		
		JPanel paneHighPrice = new JPanel(new BorderLayout());
		right.add(paneHighPrice);
		JFormattedTextField txtHighPrice = new JFormattedTextField(Util.getNumberFormatter());
		txtHighPrice.setValue(input.getHigh());
		paneHighPrice.add(txtHighPrice, BorderLayout.CENTER);
		
		JPanel paneAltPrice = new JPanel(new BorderLayout());
		right.add(paneAltPrice);
		JFormattedTextField txtAltPrice = new JFormattedTextField(Util.getNumberFormatter());
		txtAltPrice.setValue(input.getAlt());
		paneAltPrice.add(txtAltPrice, BorderLayout.CENTER);

		JPanel paneLastDate = new JPanel(new BorderLayout());
		right.add(paneLastDate);
		JFormattedTextField txtLastDate = new JFormattedTextField(Util.getDateFormatter());
		txtLastDate.setValue(new Date(input.getTime() + StockProperty.TIME_UPDATE_PRICE_INTERVAL));
		paneLastDate.add(txtLastDate, BorderLayout.CENTER);
		//
		JPanel paneLastDate2 = new JPanel(new GridLayout(1, 0));
		paneLastDate.add(paneLastDate2, BorderLayout.EAST);
		JButton btnLastDateNow = new JButton("Now");
		btnLastDateNow.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				txtLastDate.setValue(new Date());
			}
		});
		btnLastDateNow.setEnabled(true);
		paneLastDate2.add(btnLastDateNow);
		
		
		JPanel footer = new JPanel();
		editor.add(footer, BorderLayout.SOUTH);
		
		JButton ok = new JButton("OK");
		ok.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean check = true;
				double price = txtPrice.getValue() instanceof Number ? ((Number)txtPrice.getValue()).doubleValue() : 0;
				if (price < 0) check = check && false;

				double lowPrice = txtLowPrice.getValue() instanceof Number ? ((Number)txtLowPrice.getValue()).doubleValue() : 0;
				if (lowPrice < 0) check = check && false;

				double highPrice = txtHighPrice.getValue() instanceof Number ? ((Number)txtHighPrice.getValue()).doubleValue() : 0;
				if (highPrice < 0) check = check && false;
				
				if (lowPrice == 0 && highPrice == 0) {
					lowPrice = price;
					highPrice = price;
				}
				else if (price < lowPrice || price > highPrice)
					check = check && false;
				
				double altPrice = txtAltPrice.getValue() instanceof Number ? ((Number)txtAltPrice.getValue()).doubleValue() : 0;
				if (altPrice < lowPrice || altPrice > highPrice) altPrice = 0;

				Date lastDate = txtLastDate.getValue() instanceof Date ? (Date)txtLastDate.getValue() : null;
				if (lastDate == null) check = check && false;
				
				if (!check) {
					JOptionPane.showMessageDialog(editor, "Invalid input", "Invalid input", JOptionPane.ERROR_MESSAGE);
					editor.dispose();
					return;
				}
				
				Price newPrice = u().newPrice(price, lowPrice, highPrice, lastDate.getTime());
				newPrice.setAlt(altPrice);
				int selectedRow = getSelectedRow();
				if (newPrice != null && selectedRow >= 0) {
					getModel2().setValueAt(newPrice, selectedRow, true);
				}

				editor.dispose();
			}
		});
		footer.add(ok);
		
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				editor.dispose();
			}
		});
		footer.add(cancel);
		
		editor.setVisible(true);
	}
	
	
	private void listTakenStocks() {
		Price selectedPrice = getSelectedPrice();
		if (selectedPrice == null) return;
		
		Universe u = u();
		TakenStocksOfPrice ts = new TakenStocksOfPrice(u, selectedPrice, getModel2().getTimeInterval(), this);
		ts.setVisible(true);
	}
	
	
	public boolean update(String code) {
		int selectedRow = getSelectedRow();

		PricePool pricePool = code != null ? u().getPricePool(code) : null;
		boolean ret = getModel2().update(pricePool);
		
		if (getColumnModel().getColumnCount() > 0) {
			getColumnModel().getColumn(0).setMaxWidth(0);
			getColumnModel().getColumn(0).setMinWidth(0);
			getColumnModel().getColumn(0).setPreferredWidth(0);
		}
		
		if (selectedRow >= 0 && selectedRow < getRowCount()) {try {setRowSelectionInterval(selectedRow, selectedRow);} catch (Throwable e) {}}
		return ret;
	}
	
	
	public boolean update() {
		return update((String)null);
	}
	
	
	public boolean apply() {
		return getModel2().apply();
	}

	
	public boolean isModified() {
		return getModel2().isModified();
	}
	
	
	public PriceListTableModel getModel2() {
		return (PriceListTableModel)getModel();
	}

	
	public Price getSelectedPrice() {
		int row = getSelectedRow();
		return row >= 0 ? getModel2().getPriceAt(row) : null;
	}

	
	protected Price getLastRowPrice() {
		int lastRow = getRowCount() - 1;
		if (lastRow < 0) return null;
		return getModel2().getRowPriceAt(lastRow);
	}
	
	
	protected boolean addPrice(Price price) {
		Vector<Object> rowData = getModel2().toRow(price);
		getModel2().addRow(rowData);
		return true;
	}
	
	
	protected Universe u() {
		return getModel2().universe;
	}
	
	
	public void setEditable(boolean editable) {
		getModel2().editable = editable;
	}
	
	
	public boolean isEditable() {
		return getModel2().editable;
	}
	
	
	@Override
	public TableCellRenderer getCellRenderer(int row, int column) {
		TableCellRenderer renderer = null;
		Object value = getValueAt(row, column);
		if (value == null)
			renderer = super.getCellRenderer(row, column);
		else {
			renderer = getDefaultRenderer(value.getClass());
			if(renderer == null) renderer = super.getCellRenderer(row, column);
		}
		
		return renderer;
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


	protected static class DateCellRenderer extends DefaultTableCellRenderer.UIResource {

		private static final long serialVersionUID = 1L;

		public void setValue(Object value) {
        	if ((value == null) || !(value instanceof Date))
        		setText("");
        	else
        		setText(Util.format((Date)value));
        }
    }


}



class PriceListTableModel extends DefaultTableModel implements TableModelListener {
	
	
	private static final long serialVersionUID = 1L;
	
	
	protected Universe universe = null;
	
	
	protected PricePool pricePool = null;
	
	
	protected long timeInterval = 0;
	
	
	protected boolean modified = false;
	
	
	protected boolean editable = true;
	
	
	public PriceListTableModel(Universe universe, long timeInterval) {
		this.universe = universe;
		this.timeInterval = timeInterval;
		addTableModelListener(this);
	}
	
	
	protected boolean apply() {
		if (pricePool == null || !modified) return false;
		modified = false;
		
		List<Price> stockPrices = pricePool.gets(timeInterval);
		List<Price> modifiedStockPrices = Util.newList(0);
		List<Price> removedStockPrices = Util.newList(0);
		List<Price> tablePrices = getTablePrices();
		for (Price stockPrice : stockPrices) {
			if (!tablePrices.contains(stockPrice)) {
				boolean selectedAsTaken = isSelectAsTakenPrice(stockPrice); 
				if (!selectedAsTaken) removedStockPrices.add(stockPrice);
			}
			else {
				modifiedStockPrices.add(stockPrice);
				Price rowPrice = rowPriceOf(stockPrice);
				if (rowPrice != null && rowPrice.isValid()) stockPrice.copy(rowPrice);
				
				tablePrices.remove(stockPrice);
			}
		}
		modifiedStockPrices.addAll(tablePrices);
		
		
		List<Price> internalPrices = pricePool.getInternals();
		if (removedStockPrices.size() > 0) {
			for (Price removedStockPrice : removedStockPrices) internalPrices.remove(removedStockPrice);
		}
		
		if (modifiedStockPrices.size() > 0) {
			Collections.sort(modifiedStockPrices, new Comparator<Price>() {
				@Override
				public int compare(Price o1, Price o2) {
					if (o1.getTime() < o2.getTime())
						return -1;
					else if (o1.getTime() == o2.getTime())
						return 0;
					else
						return 1;
				}
			});
			
			PricePool.addSortedPrices(internalPrices, modifiedStockPrices);
		}
		
		
		List<String> marketNames = universe.names();
		for (String marketName : marketNames) {
			Market market = universe.get(marketName);
			MarketImpl m = universe.c(market);
			if (m != null) m.apply();
		}
		
		return true;
	}

	
	protected boolean update(PricePool pricePool) {
		this.pricePool = pricePool;
		Vector<Vector<Object>> data = Util.newVector(0);
		setDataVector(data, toColumns());
		if (pricePool == null) {
			modified = false;
			return true;
		}
		
		for (int i = 0; i < pricePool.size(); i++) {
			Price price = pricePool.getByIndex(i);
			Vector<Object> row = toRow(price);
			data.add(row);
		}
		
		setDataVector(data, toColumns());

		modified = false;
		return true;
	}

	
	protected boolean update() {
		return update((PricePool)null);
	}
	
	
	protected long getTimeInterval() {
		return timeInterval;
	}
	
	
	protected List<TakenStockPrice> getTakenPrices(Price price) {
		if (price == null)
			return Util.newList(0);
		else
			return PricePool.getTakenPrices(pricePool.code(), price, universe, timeInterval);
	}

	
	private boolean isSelectAsTakenPrice(Price price) {
		return getTakenPrices(price).size() > 0;
	}

	
	protected boolean isSelectAsTakenPrice(int row) {
		return (boolean)getValueAt(row, 6);
	}
	
	
	private List<Price> getTablePrices() {
		List<Price> prices = Util.newList(0);
		int n = getRowCount();
		for (int i = 0; i < n; i++) {
			Price price = getPriceAt(i);
			if (price != null) prices.add(price);
		}
		
		return prices;
	}
	
	
	protected Price getPriceAt(int row) {
		return (Price)getValueAt(row, 0);
	}
	
	
	protected void setValueAt(Price price, int row, boolean alreadyChecked) {
		if (price == null || row < 0 | row > getRowCount()) return;
		if (alreadyChecked) {
			super.setValueAt(price.getDate(), row, 1);
			super.setValueAt(price.get(), row, 2);
			super.setValueAt(price.getLow(), row, 3);
			super.setValueAt(price.getHigh(), row, 4);
			super.setValueAt(price.getAlt(), row, 5);
			modified = true;
		}
		else {
			setValueAt(price.getDate(), row, 1);
			setValueAt(price.get(), row, 2);
			setValueAt(price.getLow(), row, 3);
			setValueAt(price.getHigh(), row, 4);
			setValueAt(price.getAlt(), row, 5);
		}
	}
	
	
	@Override
	public void setValueAt(Object aValue, int row, int column) {
		if (column == 0) {
			
		}
		else if (column == 1) {
			if (aValue == null)
				aValue = new Date();
			else if (!(aValue instanceof Date)) {
				try {
					SimpleDateFormat df = new SimpleDateFormat(Util.DATE_FORMAT);
					aValue = df.parse(aValue.toString());
				}
				catch (Exception e) {
					aValue = new Date();
				}
			}
		}
		else if (column == 6) {
			
		}
		else if (aValue == null)
			aValue = Double.valueOf(0);
		else if (!(aValue instanceof Number)) {
			double value = 0;
			if (aValue instanceof Number)
				value = ((Number)aValue).doubleValue();
			else {
				try {
					value = Double.parseDouble(aValue.toString());
				}
				catch (Exception e) {value = 0;}
			}
			
			double[] quad = getRowValuesAt(row);
			switch (column) {
			case 2:
				quad[0] = value;
				break;
			case 3:
				quad[1] = value;
				break;
			case 4:
				quad[2] = value;
				break;
			case 5:
				quad[3] = value;
				break;
			default:
				break;
			}
			
			double price = quad[0];
			double low = quad[1];
			double high = quad[2];
			double alt = quad[3];
			if (column == 5) {
				if ((value != 0) && (alt < low || alt > high)) {
					fireTableCellUpdated(row, column);
					return;
				}
			}
			else {
				if (price < low || price > high) {
					fireTableCellUpdated(row, column);
					return;
				}
			}
			
			aValue = value;
		}
			
		super.setValueAt(aValue, row, column);
		modified = true;
	}

	
	@Override
	public boolean isCellEditable(int row, int column) {
		if (column != 0 && column != 1 && column != 6 && editable)
			return super.isCellEditable(row, column);
		else
			return false;
	}


	protected boolean isModified() {
		return modified;
	}

	
	protected int rowOf(Price price) {
		for (int i = 0; i < getRowCount(); i++) {
			Price p = getPriceAt(i);
			if (p == price) return i;
		}
		
		return -1;
	}
	
	
	private Price rowPriceOf(Price stockPrice) {
		if (stockPrice == null) return null;
		int row = rowOf(stockPrice);
		return row >= 0 ? getRowPriceAt(row) : null;
	}
	
	
	protected Price getRowPriceAt(int row) {
		Object date = getValueAt(row, 1);
		Object price = getValueAt(row, 2);
		Object lowPrice = getValueAt(row, 3);
		Object highPrice = getValueAt(row, 4);
		Object altPrice = getValueAt(row, 5);
		if (date != null && date instanceof Date &&
			price != null && price instanceof Number &&
			lowPrice != null && lowPrice instanceof Number &&
			highPrice != null && highPrice instanceof Number) {
			
			Price p = universe.newPrice(((Number)price).doubleValue(), 
					((Number)lowPrice).doubleValue(), 
					((Number)highPrice).doubleValue(),
					((Date)date).getTime());
			if (altPrice != null && altPrice instanceof Number) p.setAlt(((Number)altPrice).doubleValue());
			return p;
		}
		else
			return null;
	}
	
	
	private double[] getRowValuesAt(int row) {
		double[] quad = new double[] {0, 0, 0, 0};
		Object price = getValueAt(row, 2);
		if (price != null && price instanceof Number) quad[0] = ((Number)price).doubleValue();
		
		Object lowPrice = getValueAt(row, 3);
		if (lowPrice != null && lowPrice instanceof Number) quad[1] = ((Number)lowPrice).doubleValue();
		
		Object highPrice = getValueAt(row, 4);
		if (highPrice != null && highPrice instanceof Number) quad[2] = ((Number)highPrice).doubleValue();

		Object altPrice = getValueAt(row, 5);
		if (altPrice != null && altPrice instanceof Number) quad[3] = ((Number)altPrice).doubleValue();
		
		return quad;
	}
	
	
	@Override
	public void tableChanged(TableModelEvent e) {
		modified = true;
	}


	protected Vector<Object> toRow(Price price) {
		Vector<Object> row = Util.newVector(0);
		
		row.add(price);
		row.add(price.getDate());
		row.add(price.get());
		row.add(price.getLow());
		row.add(price.getHigh());
		row.add(price.getAlt());
		row.add(isSelectAsTakenPrice(price));
		
		return row;
	}

	
	/**
	 * Getting list of column names.
	 * @return list of column names.
	 */
	private Vector<String> toColumns() {
		Vector<String> columns = Util.newVector(0);
		columns.add("");
		columns.add("Date");
		columns.add("Price");
		columns.add("Low price");
		columns.add("High price");
		columns.add("Alt price");
		columns.add("Taken");
		
		return columns;
	}


}



class PriceList extends JDialog {


	private static final long serialVersionUID = 1L;


	protected JComboBox<String> cmbCode;

	
	protected JButton btnNewCode;
			
			
	protected JButton btnRemoveCode;

	
	protected JButton btnNewPrice; 

	
	protected PriceListTable tblPriceList = null;
	
	
	protected JButton btnOK;
	
	
	protected JButton btnApply;
	
	
	protected JButton btnRefresh;

	
	protected JButton btnCancel;

	
	protected Price output = null;
	
	
	protected boolean applied = false;
	
	
	protected boolean editMode = false;

	
	protected boolean selectMode = false;
	
	
	protected Universe universe = null;
	
	
	boolean pressOK = false;

	
	public PriceList(Universe universe, String code, long timeInterval, boolean editMode, boolean selectMode, Component parent) {
		super(Util.getDialogForComponent(parent), "Price list", true);
		this.universe = universe;
		this.editMode = editMode;
		this.selectMode = selectMode;
		this.tblPriceList = new PriceListTable(universe, code, timeInterval);
		this.tblPriceList.setEditable(!selectMode);
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(600, 400);
		setLocationRelativeTo(Util.getDialogForComponent(parent));
		
	    setJMenuBar(createMenuBar());

		setLayout(new BorderLayout());
		
		JPanel header = new JPanel(new BorderLayout());
		add(header, BorderLayout.NORTH);
		
		header.add(new JLabel("Code: "), BorderLayout.WEST);
		cmbCode = new JComboBox<String>(universe.getSupportStockCodes().toArray(new String[] {}));
		if (code != null) cmbCode.setSelectedItem(code);
		cmbCode.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				String selectedItem = e.getItem() != null ? e.getItem().toString() : null; 
				if (e.getStateChange() == ItemEvent.DESELECTED)
					update(selectedItem, false);
				else
					update(selectedItem, true);
			}
		});
		cmbCode.setEnabled(!selectMode);
		cmbCode.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(SwingUtilities.isRightMouseButton(e) ) {
					JPopupMenu contextMenu = createCodeContextMenu();
					if(contextMenu != null) contextMenu.show((Component)e.getSource(), e.getX(), e.getY());
				}
			}
		});
		header.add(cmbCode, BorderLayout.CENTER);
		
		JPanel body = new JPanel(new BorderLayout());
		add(body, BorderLayout.CENTER);
		
		JPanel toolbar = new JPanel(new BorderLayout());
		body.add(toolbar, BorderLayout.NORTH);
		
		JPanel paneCode = new JPanel();
		if (!selectMode) toolbar.add(paneCode, BorderLayout.WEST);
		
		btnNewCode = new JButton("New code");
		btnNewCode.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addNewCode();
			}
		});
		paneCode.add(btnNewCode);

		btnRemoveCode = new JButton("Remove code");
		btnRemoveCode.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				removeCode();
			}
		});
		paneCode.add(btnRemoveCode);

		JPanel panePrice = new JPanel();
		if (!selectMode) toolbar.add(panePrice, BorderLayout.EAST);

		btnNewPrice = new JButton("New price");
		btnNewPrice.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addNewPrice(false);
			}
		});
		panePrice.add(btnNewPrice);

		body.add(new JScrollPane(tblPriceList), BorderLayout.CENTER);
		
		JPanel footer = new JPanel();
		add(footer, BorderLayout.SOUTH);
		
		btnOK = new JButton(selectMode ? "Select" : "OK");
		btnOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ok();
			}
		});
		footer.add(btnOK);
		
		PriceList thisPriceList = this;
		btnApply = new JButton("Apply");
		btnApply.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean applied = apply();
				if (applied) JOptionPane.showMessageDialog(thisPriceList, "Successful applying", "Successful applying", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		if (editMode) footer.add(btnApply);

		btnRefresh = new JButton("Refresh");
		btnRefresh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				update((String)null, true);
			}
		});
		if (selectMode) footer.add(btnRefresh);

		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		footer.add(btnCancel);
		
		
		update((String)null, true);
	}
	
	
	private JMenuBar createMenuBar() {
		JMenuBar mnBar = new JMenuBar();
		
		JMenu mnTool = new JMenu("Tool");
		mnTool.setMnemonic('t');
		mnBar.add(mnTool);

		PriceList thisPriceList = this;
		JMenuItem mniFactor = new JMenuItem(
			new AbstractAction("Set price factor") {
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					int answer= JOptionPane.showConfirmDialog(thisPriceList, "Are you sure to set price factor?", "Factor confirmation", JOptionPane.YES_NO_OPTION);
					if (answer == JOptionPane.YES_OPTION) setFactor();
				}
			});
		mniFactor.setMnemonic('f');
		mniFactor.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK));
		mnTool.add(mniFactor);
		
		mnTool.addSeparator();
		
		JMenuItem mniRenameCode = new JMenuItem(
			new AbstractAction("Rename code") {
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					renameCode();
				}
			});
		mniRenameCode.setMnemonic('n');
		mnTool.add(mniRenameCode);

		return mnBar;
	}
	
	
	private JPopupMenu createCodeContextMenu() {
		JPopupMenu ctxMenu = new JPopupMenu();
		String code = cmbCode.getSelectedItem() != null ? cmbCode.getSelectedItem().toString() : null;
		if (code == null) return null;
		
		JMenuItem miRenameCode = new JMenuItem("Rename code");
		miRenameCode.addActionListener( 
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					renameCode();
				}
			});
		ctxMenu.add(miRenameCode);

		return ctxMenu;
	}
	
	
	private void setFactor() {
		String code = cmbCode.getSelectedItem() != null ? cmbCode.getSelectedItem().toString() : null;
		if (code == null) return;
		
		String factorText = JOptionPane.showInputDialog(this, "Enter price factor", "" + StockProperty.PRICE_FACTOR);
		if (factorText == null) return;

		try {
			double factor = Double.parseDouble(factorText);
			if (factor > 0 && factor != 1) {
				List<Price> prices = universe.getPricePool(code).getInternals();
				for (Price price : prices) price.applyFactor(factor);
				
				update(null, true);
				applied = true;
			}
		}
		catch (Exception e) {}
	}

	
	private void renameCode() {
		JOptionPane.showMessageDialog(this, "This function not implemented yet", "Not implemented yet", JOptionPane.INFORMATION_MESSAGE);
	}
	
	
	private void ok() {
		apply();
		
		if (selectMode) this.output = tblPriceList.getSelectedPrice();
		
		pressOK = true;
		
		dispose();
	}
	
	
	private boolean apply() {
		if (!editMode && !selectMode) return false;
		
		boolean applied = tblPriceList.apply();
		if (applied) {
			String code = cmbCode.getSelectedItem() != null ? cmbCode.getSelectedItem().toString() : null;
			if (code != null) {
				PricePool pricePool  = universe.getPricePool(code);
				btnRemoveCode.setVisible(pricePool == null || pricePool.size() == 0);
			}
		}
		this.applied = this.applied || applied;
		
		return applied;
	}
	
	
	public boolean isApplied() {
		return this.applied;
	}
	
	
	private boolean addNewCode() {
		String newCode = JOptionPane.showInputDialog(this, "Enter new code", "New code");
		if (newCode == null) return false;
		newCode = newCode.trim();
		if (newCode.isEmpty()) return false;

		PricePool pricePool = universe.getPricePool(newCode);
		if (pricePool != null) return false;
		pricePool = universe.getCreatePricePool(newCode);
		if (pricePool != null) {
			DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(universe.getSupportStockCodes().toArray(new String[] {}));
			cmbCode.setModel(model);
			
			String selectedCode = cmbCode.getSelectedItem() != null ? cmbCode.getSelectedItem().toString() : null;
			if (selectedCode == null || selectedCode.equals(newCode))
				update(newCode, true);
			else
				cmbCode.setSelectedItem(newCode);
			
			return true;
		}
		else
			return false;
	}
	
	
	private boolean removeCode() {
		if (tblPriceList.getRowCount() > 0) return false;
		String removedCode = cmbCode.getSelectedItem() != null ? cmbCode.getSelectedItem().toString() : null;
		if (removedCode == null) return false;
		
		PricePool pricelPool = universe.getPricePool(removedCode);
		if (pricelPool == null)
			return false;
		else if (pricelPool.size() > 0)
			return false;
		else {
			universe.removePricePool(removedCode);
			DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(universe.getSupportStockCodes().toArray(new String[] {}));
			cmbCode.setModel(model);
			update(null, true);

			return true;
		}
	}
	
	
	private boolean addNewPrice(boolean bNewCode) {
		String code = cmbCode.getSelectedItem() != null ? cmbCode.getSelectedItem().toString() : null;
		btnRemoveCode.setVisible(false);
		if (code != null) {
			PricePool pricePool = universe.getPricePool(code);
			if (pricePool == null || pricePool.size() == 0) btnRemoveCode.setVisible(true);
		}

		Price input = tblPriceList.getLastRowPrice();
		if (input == null) input = universe.newPrice(1, 0, 0, System.currentTimeMillis());
		NewPrice newPrice = new NewPrice(input, bNewCode, this);
		newPrice.setVisible(true);
		
		Price output = newPrice.getOutput();
		if (output == null) return false;
	
		if (bNewCode) {
			String newCode = newPrice.getNewCode();
			if (newCode == null) return false;
			
			PricePool pricePool = universe.getPricePool(newCode);
			if (pricePool != null) return false;
			pricePool = universe.getCreatePricePool(newCode);
			if (!pricePool.add(output))
				return false;
			else {
				DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(universe.getSupportStockCodes().toArray(new String[] {}));
				cmbCode.setModel(model);
				
				String selectedCode = cmbCode.getSelectedItem() != null ? cmbCode.getSelectedItem().toString() : null;
				if (selectedCode == null || selectedCode.equals(newCode))
					update(newCode, true);
				else
					cmbCode.setSelectedItem(newCode);

				try {int n = tblPriceList.getRowCount(); if (n > 0) tblPriceList.setRowSelectionInterval(n - 1, n - 1);} catch (Throwable e) {}
				return true;
			}
		}
		else {
			boolean ret = tblPriceList.addPrice(output);
			try {int n = tblPriceList.getRowCount(); if (n > 0) tblPriceList.setRowSelectionInterval(n - 1, n - 1);} catch (Throwable e) {}
			return ret;
		}
	}
	
	
	private void update(String code, boolean reload) {
		if (tblPriceList.isModified() && editMode) {
			int ret = JOptionPane.showConfirmDialog(this, "Would you like to apply some changes into price list", "Apply request", JOptionPane.YES_NO_OPTION);
			if (ret == JOptionPane.YES_OPTION) apply();
		}
		
		if (!reload) return;
		
		if (code != null)
			tblPriceList.update(code);
		else if (cmbCode.getSelectedItem() != null) {
			code = cmbCode.getSelectedItem().toString();
			tblPriceList.update(code);
		}
		else
			tblPriceList.update();
		
		btnRemoveCode.setVisible(false);
		if (code != null) {
			PricePool pricePool = universe.getPricePool(code);
			if (pricePool == null || pricePool.size() == 0) btnRemoveCode.setVisible(true);
		}
	}
	
	
	@Override
	public void dispose() {
		if (tblPriceList.isModified() && !selectMode) {
			int ret = JOptionPane.showConfirmDialog(this, "Would you like to apply some changes into price list", "Apply request", JOptionPane.YES_NO_OPTION);
			if (ret == JOptionPane.YES_OPTION) apply();
		}
		
		super.dispose();
	}
	
	
	public Price getOutput() {
		return output;
	}
	
	
}



class TakenStocksOfPrice extends JDialog {

	
	private static final long serialVersionUID = 1L;

	
	protected TakenStocksTable tblTakenStocks = null;
	
	
	public TakenStocksOfPrice(Universe universe, Price price, long timeInterval, Component component) {
		super(Util.getDialogForComponent(component), "Stocks taken with give price", true);
		this.tblTakenStocks = new TakenStocksTable(universe, price, timeInterval);
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setSize(450, 350);
		setLocationRelativeTo(Util.getDialogForComponent(component));
		setLayout(new BorderLayout());
		
		JPanel body = new JPanel(new BorderLayout());
		add(body, BorderLayout.CENTER);
		
		body.add(new JScrollPane(this.tblTakenStocks), BorderLayout.CENTER);
		
		JPanel footer = new JPanel();
		add(footer, BorderLayout.SOUTH);
		
		JButton ok = new JButton("OK");
		ok.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		footer.add(ok);
	}
	
	
	public class TakenStocksTable extends JTable {

		private static final long serialVersionUID = 1L;
		
		public TakenStocksTable(Universe universe, Price price, long timeInterval) {
			super();
			setModel(new TakenStocksTableModel(universe, price, timeInterval));
			
			setAutoCreateRowSorter(true);
			setAutoResizeMode(AUTO_RESIZE_OFF);
			getTableHeader().setReorderingAllowed(false);
			
			update();
		}

		protected TakenStocksTableModel getModel2() {
			return (TakenStocksTableModel)getModel();
		}
		
		public void update() {
			getModel2().update();
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

	}

		
	class TakenStocksTableModel extends DefaultTableModel {
		
		private static final long serialVersionUID = 1L;
		
		protected Universe universe = null;
		
		protected Price price = null;
		
		protected long timeInterval = 0;
		
		public TakenStocksTableModel(Universe universe, Price price, long timeInterval) {
			this.universe = universe;
			this.price = price;
			this.timeInterval = timeInterval;
		}
		
		protected void update() {
			Vector<Vector<Object>> data = Util.newVector(0);
			List<String> marketNames = universe.names();
			Collections.sort(marketNames);
			for (String marketName : marketNames) {
				MarketImpl m = universe.c(universe.get(marketName));
				if (m == null) continue;
				addRows(data, m, 0);
				
				MarketImpl watchMarket = m.getWatchMarket();
				if (watchMarket != null) addRows(data, watchMarket, 1);
				
				MarketImpl trashMarket = m.getTrashMarket();
				if (trashMarket != null) addRows(data, trashMarket, 2);
			}
			
			setDataVector(data, toColumns());
		}

		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}

		private void addRows(Vector<Vector<Object>> data, MarketImpl market, int index) {
			List<Stock> stocks = market.getStocks(timeInterval);
			for (Stock stock : stocks) {
				StockImpl s = market.c(stock);
				if (s == null) continue;
				
				Price p = s.getTakenPrice(timeInterval);
				if (p == null || !(p instanceof TakenPrice)) continue;
				
				if (((TakenPrice)p).checkRefEquals(this.price)) {
					Vector<Object> row = toRow(s, market.getName(), index);
					if (row != null) data.add(row);
				}
			}
		}
		
		private Vector<Object> toRow(StockImpl stock, String marketName, int index) {
			Vector<Object> row = Util.newVector(0);
			
			row.add(stock);
			row.add(marketName);
			row.add(index == 0 ? "main" : (index == 1 ? "watch" : "trash"));
			row.add(stock.isBuy());
			row.add(new MarketTableModel.Time(stock.getTakenTimePoint(timeInterval)));
			row.add(stock.getVolume(timeInterval, true));
			row.add(stock.getAverageTakenPrice(timeInterval));
			row.add(new MarketTableModel.Pair(stock.getStopLoss(), stock.getTakeProfit()));
			row.add(stock.getMargin(timeInterval));
			row.add(stock.isCommitted());
			
			return row;
		}

		/**
		 * Getting list of column names.
		 * @return list of column names.
		 */
		private Vector<String> toColumns() {
			Vector<String> columns = Util.newVector(0);
			columns.add("Code");
			columns.add("Market");
			columns.add("Class");
			columns.add("Buy");
			columns.add("Taken date");
			columns.add("Volume");
			columns.add("Taken price");
			columns.add("Stop loss / take profit");
			columns.add("Margin");
			columns.add("Committed");
			
			return columns;
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			if (columnIndex == 0 || columnIndex == 1 || columnIndex == 2)
				return super.getColumnClass(columnIndex);
			else if (columnIndex == 3 || columnIndex == 9)
				return Boolean.class;
			else if (columnIndex == 4)
				return MarketTableModel.Time.class;
			else if (columnIndex == 7)
				return MarketTableModel.Pair.class;
			else
				return Double.class;
		}
		
	}
	
	
}



class PriceListPartialTable extends JTable {

	
	private static final long serialVersionUID = 1L;
	
	
	public final static Color GRAY = new Color(128, 128, 128);
	
	
	public final static Color LIGHTGRAY = new Color(200, 200, 200);
	

//	private DateCellRenderer dateCellRenderer = new DateCellRenderer();
//	private DateCellEditor dateCellEditor = new DateCellEditor();
//	protected ReadOnlyCellRenderer readOnlyCellRenderer = new ReadOnlyCellRenderer();

	
	protected DateCellRenderer dateCellRenderer = new DateCellRenderer();
	
	
	public PriceListPartialTable(Market market, Stock stock, long timeInterval) {
		super();
		setModel(new PriceListPartialTableModel(stock, timeInterval) {

			private static final long serialVersionUID = 1L;

			@Override
			protected MarketImpl m() {
				Universe u = market.getNearestUniverse();
				return u != null ? u.c(market) : null;
			}
			
		});

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(SwingUtilities.isRightMouseButton(e) ) {
					JPopupMenu contextMenu = createContextMenu();
					if(contextMenu != null)
						contextMenu.show((Component)e.getSource(), e.getX(), e.getY());
				}
				else if (e.getClickCount() >= 2) {
					if (getSelectedColumn() == 1) edit(null);
				}
			}
		});

		
		getTableHeader().setReorderingAllowed(false);
		setDefaultRenderer(Date.class, dateCellRenderer);
//		setDefaultEditor(Date.class, dateCellEditor);

		update();
	}


	private JPopupMenu createContextMenu() {
		JPopupMenu ctxMenu = new JPopupMenu();
		Price selectedPrice = getSelectedPrice();
		if (selectedPrice == null) return null;
		
		JMenuItem miEdit = new JMenuItem("Edit");
		miEdit.addActionListener( 
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					edit(selectedPrice);
				}
			});
		ctxMenu.add(miEdit);

		JMenuItem miDelete = new JMenuItem("Delete");
		miDelete.addActionListener( 
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					delete();
				}
			});
		ctxMenu.add(miDelete);

		return ctxMenu;
	}

	
	private void delete() {
		PriceListPartialTableModel m = getModel2();
		List<Price> removedPrices = Util.newList(0);
		int[] selectedRows = getSelectedRows();
		if (selectedRows == null) return;
		for (int selectedRow : selectedRows) {
			if (!m.isSelectAsTakenPrice(selectedRow))
				removedPrices.add(m.getPriceAt(selectedRow));
		}

		for (Price removedPrice : removedPrices) {
			int removedRow = m.rowOf(removedPrice);
			if (removedRow >= 0) m.removeRow(removedRow);
		}
	}
	
	
	private void edit(Price input) {
		input = input != null ? input : getSelectedPrice();
		if (input == null) return;
		JDialog editor = new JDialog(Util.getDialogForComponent(this), "Edit price", true);
		
		editor.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		editor.setSize(350, 250);
		editor.setLocationRelativeTo(Util.getDialogForComponent(this));
		editor.setLayout(new BorderLayout());
		
		JPanel header = new JPanel(new BorderLayout());
		editor.add(header, BorderLayout.NORTH);
		
		JPanel left = new JPanel(new GridLayout(0, 1));
		header.add(left, BorderLayout.WEST);
		
		left.add(new JLabel("Price (*): "));
		left.add(new JLabel("Low price: "));
		left.add(new JLabel("High price: "));
		left.add(new JLabel("Alt price : "));
		left.add(new JLabel("Last date: "));

		JPanel right = new JPanel(new GridLayout(0, 1));
		header.add(right, BorderLayout.CENTER);
		
		JPanel panePrice = new JPanel(new BorderLayout());
		right.add(panePrice);
		JFormattedTextField txtPrice = new JFormattedTextField(Util.getNumberFormatter());
		txtPrice.setValue(input.get());
		panePrice.add(txtPrice, BorderLayout.CENTER);
		//
		JButton btnPrice = new JButton("Estimate");
		btnPrice.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Estimator estimator = getEstimator();
				if (estimator != null)  txtPrice.setValue(estimator.estimatePrice(getModel2().m().getTimeViewInterval()));
			}
		});
		panePrice.add(btnPrice, BorderLayout.EAST);
		
		JPanel paneLowPrice = new JPanel(new BorderLayout());
		right.add(paneLowPrice);
		JFormattedTextField txtLowPrice = new JFormattedTextField(Util.getNumberFormatter());
		txtLowPrice.setValue(input.getLow());
		paneLowPrice.add(txtLowPrice, BorderLayout.CENTER);
		//
		JButton btnLowPrice = new JButton("Estimate");
		btnLowPrice.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Estimator estimator = getEstimator();
				if (estimator != null)  txtLowPrice.setValue(estimator.estimateLowPrice(getModel2().m().getTimeViewInterval()));
			}
		});
		paneLowPrice.add(btnLowPrice, BorderLayout.EAST);
		
		JPanel paneHighPrice = new JPanel(new BorderLayout());
		right.add(paneHighPrice);
		JFormattedTextField txtHighPrice = new JFormattedTextField(Util.getNumberFormatter());
		txtHighPrice.setValue(input.getHigh());
		paneHighPrice.add(txtHighPrice, BorderLayout.CENTER);
		//
		JButton btnHighPrice = new JButton("Estimate");
		btnHighPrice.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Estimator estimator = getEstimator();
				if (estimator != null)  txtHighPrice.setValue(estimator.estimateHighPrice(getModel2().m().getTimeViewInterval()));
			}
		});
		paneHighPrice.add(btnHighPrice, BorderLayout.EAST);
		
		JPanel paneAltPrice = new JPanel(new BorderLayout());
		right.add(paneAltPrice);
		JFormattedTextField txtAltPrice = new JFormattedTextField(Util.getNumberFormatter());
		txtAltPrice.setValue(input.getAlt());
		paneAltPrice.add(txtAltPrice, BorderLayout.CENTER);
		//
		JButton btnAltPrice = new JButton("Estimate");
		btnAltPrice.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Estimator estimator = getEstimator();
				if (estimator != null)  txtAltPrice.setValue(estimator.estimatePrice(getModel2().m().getTimeViewInterval()));
			}
		});
		paneAltPrice.add(btnAltPrice, BorderLayout.EAST);

		JPanel paneLastDate = new JPanel(new BorderLayout());
		right.add(paneLastDate);
		JFormattedTextField txtLastDate = new JFormattedTextField(Util.getDateFormatter());
		txtLastDate.setValue(new Date(input.getTime() + StockProperty.TIME_UPDATE_PRICE_INTERVAL));
		paneLastDate.add(txtLastDate, BorderLayout.CENTER);
		//
		JPanel paneLastDate2 = new JPanel(new GridLayout(1, 0));
		paneLastDate.add(paneLastDate2, BorderLayout.EAST);
		JButton btnLastDateNow = new JButton("Now");
		btnLastDateNow.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				txtLastDate.setValue(new Date());
			}
		});
		btnLastDateNow.setEnabled(true);
		paneLastDate2.add(btnLastDateNow);
		
		
		JPanel footer = new JPanel();
		editor.add(footer, BorderLayout.SOUTH);
		
		JButton ok = new JButton("OK");
		ok.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean check = true;
				double price = txtPrice.getValue() instanceof Number ? ((Number)txtPrice.getValue()).doubleValue() : 0;
				if (price < 0) check = check && false;

				double lowPrice = txtLowPrice.getValue() instanceof Number ? ((Number)txtLowPrice.getValue()).doubleValue() : 0;
				if (lowPrice < 0) check = check && false;

				double highPrice = txtHighPrice.getValue() instanceof Number ? ((Number)txtHighPrice.getValue()).doubleValue() : 0;
				if (highPrice < 0) check = check && false;
				
				if (lowPrice == 0 && highPrice == 0) {
					lowPrice = price;
					highPrice = price;
				}
				else if (price < lowPrice || price > highPrice)
					check = check && false;
				
				double altPrice = txtAltPrice.getValue() instanceof Number ? ((Number)txtAltPrice.getValue()).doubleValue() : 0;
				if (altPrice < lowPrice || altPrice > highPrice) altPrice = 0;

				Date lastDate = txtLastDate.getValue() instanceof Date ? (Date)txtLastDate.getValue() : null;
				StockAbstract s = getModel2().getStock();
				if (lastDate == null || s == null)
					check = check && false;
				
				if (!check) {
					JOptionPane.showMessageDialog(editor, "Invalid input", "Invalid input", JOptionPane.ERROR_MESSAGE);
					editor.dispose();
					return;
				}
				
				Price newPrice = getModel2().m().newPrice(price, lowPrice, highPrice, lastDate.getTime());
				newPrice.setAlt(altPrice);
				int selectedRow = getSelectedRow();
				if (newPrice != null && selectedRow >= 0) {
					getModel2().setValueAt(newPrice, selectedRow, true);
				}

				editor.dispose();
			}
		});
		footer.add(ok);
		
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				editor.dispose();
			}
		});
		footer.add(cancel);
		
		editor.setVisible(true);
	}
	
	
	private Estimator getEstimator() {
		MarketImpl m = getModel2().m();
		if (m == null) return null;
		StockAbstract s = getModel2().getStock();
		return s!= null ? m.getEstimator(s.code(), s.isBuy()) : null;
	}

	
	public void update() {
		int selectedRow = getSelectedRow();

		getModel2().update();
		
		if (getColumnModel().getColumnCount() > 0) {
			getColumnModel().getColumn(0).setMaxWidth(0);
			getColumnModel().getColumn(0).setMinWidth(0);
			getColumnModel().getColumn(0).setPreferredWidth(0);
		}
		
		if (selectedRow >= 0 && selectedRow < getRowCount()) {try {setRowSelectionInterval(selectedRow, selectedRow);} catch (Throwable e) {}}
	}
	
	
	public boolean apply() {
		return getModel2().apply();
	}
	
	
	public PriceListPartialTableModel getModel2() {
		return (PriceListPartialTableModel)getModel();
	}
	
	
	public void setEditable(boolean editable) {
		getModel2().editable = editable;
	}
	
	
	public boolean isEditable() {
		return getModel2().editable;
	}
	
	
	protected long getTimeInterval() {
		return getModel2().timeInterval;
	}
	
	
	protected List<TakenPrice> getTakenPrices() {
		return getModel2().getTakenPrices();
	}

	
	public Price getSelectedPrice() {
		int row = getSelectedRow();
		return row >= 0 ? getModel2().getPriceAt(row) : null;
	}
	
	
	public boolean isModified() {
		return getModel2().isModified();
	}
	
	
	protected Price getLastRowPrice() {
		int lastRow = getRowCount() - 1;
		if (lastRow < 0) return null;
		return getModel2().getRowPriceAt(lastRow);
	}
	
	
	protected boolean addPrice(Price price) {
		Vector<Object> rowData = getModel2().toRow(price);
		getModel2().addRow(rowData);
		return true;
	}

	
	@Override
	public TableCellRenderer getCellRenderer(int row, int column) {
		TableCellRenderer renderer = null;
		Object value = getValueAt(row, column);
		if (value == null)
			renderer = super.getCellRenderer(row, column);
		else {
			renderer = getDefaultRenderer(value.getClass());
			if(renderer == null) renderer = super.getCellRenderer(row, column);
		}
		
		try {
			if (value != null && renderer instanceof DefaultTableCellRenderer) {
				Price price = getModel2().getPriceAt(row);
				if (price != null && getModel2().isSelectAsTakenPrice(row)) {
					StockImpl stock = getModel2().c(getModel2().stock);
					Price takenPrice = stock != null ? stock.getTakenPrice(getTimeInterval()) : null;
					boolean as = takenPrice != null && takenPrice instanceof TakenPrice ? (((TakenPrice)takenPrice).getPrice() == price) : false;
					if (as)
						((DefaultTableCellRenderer)renderer).setBackground(LIGHTGRAY);
					else
						((DefaultTableCellRenderer)renderer).setBackground(null);
				}
				else
					((DefaultTableCellRenderer)renderer).setBackground(null);
			}
			else if (renderer instanceof DefaultTableCellRenderer)
				((DefaultTableCellRenderer)renderer).setBackground(null);
		}
		catch (Exception e) {}
		
		return renderer;
	}

	
//	@Override
//	public TableCellRenderer getCellRenderer(int row, int column) {
//		TableCellRenderer renderer = getDefaultRenderer(getModel2().getColumnClass(row, column));
//		if(renderer == null) renderer = super.getCellRenderer(row, column);
//		return renderer;
//	}

	
//	private TableCellRenderer getCellRenderer0(int row, int column) {
//		Object value = getValueAt(row, column);
//		if (value == null) return null;
//		
//		Price price = getModel2().getPriceAt(row);
//		return price != null && getModel2().isSelectAsTakenPrice(price) ? readOnlyCellRenderer : null;
//	}

	
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

	
//	@Override
//    public TableCellEditor getCellEditor(int row, int column) {
//    	TableCellEditor editor = getDefaultEditor(getModel2().getColumnClass(row, column));
//    	if(editor == null) editor = super.getCellEditor(row, column);
//    	return editor;
//    }

	
//	/**
//	 * This class represents read-only cell renderer.
//	 * @author Loc Nguyen
//	 * @version 1.0
//	 */
//	protected class ReadOnlyCellRenderer extends DefaultTableCellRenderer.UIResource {
//
//		/**
//		 * Serial version UID for serializable class.
//		 */
//		private static final long serialVersionUID = 1L;
//
//		@Override
//		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
//				int row, int column) {
//			Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
//			comp.setBackground(new Color(200, 200, 200));
//			
//			return comp;
//			
//		}
//		
//	}

	
	protected static class DateCellRenderer extends DefaultTableCellRenderer.UIResource {

		private static final long serialVersionUID = 1L;

		public void setValue(Object value) {
        	if ((value == null) || !(value instanceof Date))
        		setText("");
        	else
        		setText(Util.format((Date)value));
        }
    }

	
}



abstract class PriceListPartialTableModel extends DefaultTableModel implements TableModelListener {

	
	private static final long serialVersionUID = 1L;
	
	
	protected Stock stock = null;
	
	
	protected StockGroup group = null;
	
	
	protected long timeInterval = 0;
	
	
	protected boolean modified = false;
	
	
	protected boolean editable = true;
	
	
	public PriceListPartialTableModel(Stock stock, long timeInterval) {
		this.stock = stock;
		this.timeInterval = timeInterval;
		
		if (stock instanceof StockGroup)
			group = (StockGroup)stock;
		else {
			MarketImpl m = m();
			if (m != null)  group = m.get(stock.code(), stock.isBuy());
		}
		
		addTableModelListener(this);
	}
	
	
	public boolean apply() {
		if (!modified) return false;
		modified = false;
		
		List<Price> stockPrices = getStockPrices();
		List<Price> modifiedStockPrices = Util.newList(0);
		List<Price> removedStockPrices = Util.newList(0);
		List<Price> tablePrices = getTablePrices();
		for (Price stockPrice : stockPrices) {
			if (!tablePrices.contains(stockPrice)) {
				boolean selectedAsTaken = isSelectAsTakenPrice(stockPrice); 
				if (!selectedAsTaken) removedStockPrices.add(stockPrice);
			}
			else {
				modifiedStockPrices.add(stockPrice);
				Price rowPrice = rowPriceOf(stockPrice);
				if (rowPrice != null && rowPrice.isValid()) stockPrice.copy(rowPrice);
				
				tablePrices.remove(stockPrice);
			}
		}
		modifiedStockPrices.addAll(tablePrices);
		
		
		List<Price> internalPrices = getInternalPrices();
		if (removedStockPrices.size() > 0) {
			for (Price removedStockPrice : removedStockPrices) internalPrices.remove(removedStockPrice);
		}

		if (modifiedStockPrices.size() > 0) {
			Collections.sort(modifiedStockPrices, new Comparator<Price>() {
				@Override
				public int compare(Price o1, Price o2) {
					if (o1.getTime() < o2.getTime())
						return -1;
					else if (o1.getTime() == o2.getTime())
						return 0;
					else
						return 1;
				}
			});
			
			PricePool.addSortedPrices(internalPrices, modifiedStockPrices);
		}
		
		
		MarketImpl m = m();
		if (m != null) m.apply();
		
		return true;
	}
	
	
	private List<Price> getInternalPrices() {
		Stock s = group != null ? group : stock;
		return s != null ? s.getInternalPrices() : null;
	}
	
	
	public void update() {
		Vector<Vector<Object>> data = Util.newVector(0);
		List<Price> prices = stock.getPrices(timeInterval);
		for (Price price : prices) {
			Vector<Object> row = toRow(price);
			data.add(row);
		}
		
		setDataVector(data, toColumns());
		modified = false;
	}
	
	
	protected void clearTable() {
		setDataVector(Util.newVector(0), toColumns());
		modified = false;
	}
	
	
	protected abstract MarketImpl m();
	
	
	protected StockImpl c(Stock stock) {
		MarketImpl m = m();
		return m != null ? m.c(stock) : null;
	}
	
	
	protected StockAbstract getStock() {
		if (group != null)
			return group;
		else
			return stock != null ? c(stock) : null;
	}
	
	
	protected StockGroup getGroup() {
		return group;
	}
	
	
	protected List<TakenPrice> getTakenPrices() {
		List<TakenPrice> takenPrices = Util.newList(0);
		if (group == null) {
			StockImpl s = c(stock);
			if (s == null) return takenPrices;
			Price price = s.getTakenPrice(timeInterval);
			if (price != null && price instanceof TakenPrice) takenPrices.add((TakenPrice)price);
		}
		else {
			List<Stock> stocks = group.getStocks(timeInterval);
			for (Stock stock : stocks) {
				StockImpl s = c(stock);
				if (s == null) continue;
				Price price = s.getTakenPrice(timeInterval);
				if (price != null && price instanceof TakenPrice)
					takenPrices.add((TakenPrice)price);
			}
		}
		
		return takenPrices;
	}
	
	
	private boolean isSelectAsTakenPrice(Price price) {
		if (price == null) return false;
		List<TakenPrice> takenPrices = getTakenPrices();
		if (takenPrices.size() == 0) return false;
		
		for (TakenPrice takenPrice : takenPrices) {
			if (takenPrice.checkRefEquals(price)) return true;
		}
		
		return false;
	}
	
	
	protected boolean isSelectAsTakenPrice(int row) {
		return (boolean)getValueAt(row, 6);
	}
	
	
	protected List<Price> getStockPrices() {
		if (group != null)
			return group.getPrices(timeInterval);
		else
			return stock.getPrices(timeInterval);
	}
	
	
	private List<Price> getTablePrices() {
		List<Price> prices = Util.newList(0);
		int n = getRowCount();
		for (int i = 0; i < n; i++) {
			Price price = getPriceAt(i);
			if (price != null) prices.add(price);
		}
		
		return prices;
	}
	
	
	protected Price getPriceAt(int row) {
		return (Price)getValueAt(row, 0);
	}
	
	
	protected void setValueAt(Price price, int row, boolean alreadyChecked) {
		if (price == null || row < 0 | row > getRowCount()) return;
		if (alreadyChecked) {
			super.setValueAt(price.getDate(), row, 1);
			super.setValueAt(price.get(), row, 2);
			super.setValueAt(price.getLow(), row, 3);
			super.setValueAt(price.getHigh(), row, 4);
			super.setValueAt(price.getAlt(), row, 5);
			modified = true;
		}
		else {
			setValueAt(price.getDate(), row, 1);
			setValueAt(price.get(), row, 2);
			setValueAt(price.getLow(), row, 3);
			setValueAt(price.getHigh(), row, 4);
			setValueAt(price.getAlt(), row, 5);
		}
	}
	
	
	@Override
	public void setValueAt(Object aValue, int row, int column) {
		if (column == 0) {
			
		}
		else if (column == 1) {
			if (aValue == null)
				aValue = new Date();
			else if (!(aValue instanceof Date)) {
				try {
					SimpleDateFormat df = new SimpleDateFormat(Util.DATE_FORMAT);
					aValue = df.parse(aValue.toString());
				}
				catch (Exception e) {
					aValue = new Date();
				}
			}
		}
		else if (column == 6) {
			
		}
		else if (aValue == null)
			aValue = Double.valueOf(0);
		else {
			double value = 0;
			if (aValue instanceof Number)
				value = ((Number)aValue).doubleValue();
			else {
				try {
					value = Double.parseDouble(aValue.toString());
				}
				catch (Exception e) {value = 0;}
			}
			
			double[] quad = getRowValuesAt(row);
			switch (column) {
			case 2:
				quad[0] = value;
				break;
			case 3:
				quad[1] = value;
				break;
			case 4:
				quad[2] = value;
				break;
			case 5:
				quad[3] = value;
				break;
			default:
				break;
			}
			
			double price = quad[0];
			double low = quad[1];
			double high = quad[2];
			double alt = quad[3];
			if (column == 5) {
				if ((value != 0) && (alt < low || alt > high)) {
					fireTableCellUpdated(row, column);
					return;
				}
			}
			else {
				if (price < low || price > high) {
					fireTableCellUpdated(row, column);
					return;
				}
			}
			
			aValue = value;
		}
			
		super.setValueAt(aValue, row, column);
		modified = true;
	}

	
	@Override
	public boolean isCellEditable(int row, int column) {
		if (editable && column != 0 && column != 1 && column != 6)
			return super.isCellEditable(row, column);
		else
			return false;
	}


	protected boolean isModified() {
		return modified;
	}

	
	protected int rowOf(Price price) {
		for (int i = 0; i < getRowCount(); i++) {
			Price p = getPriceAt(i);
			if (p == price) return i;
		}
		
		return -1;
	}
	
	
	private Price rowPriceOf(Price stockPrice) {
		if (stockPrice == null) return null;
		int row = rowOf(stockPrice);
		return row >= 0 ? getRowPriceAt(row) : null;
	}
	
	
	protected Price getRowPriceAt(int row) {
		Object date = getValueAt(row, 1);
		Object price = getValueAt(row, 2);
		Object lowPrice = getValueAt(row, 3);
		Object highPrice = getValueAt(row, 4);
		Object altPrice = getValueAt(row, 5);
		if (date != null && date instanceof Date &&
			price != null && price instanceof Number &&
			lowPrice != null && lowPrice instanceof Number &&
			highPrice != null && highPrice instanceof Number) {
			Price p = m().newPrice(((Number)price).doubleValue(), 
					((Number)lowPrice).doubleValue(), 
					((Number)highPrice).doubleValue(),
					((Date)date).getTime());
			if (altPrice != null && altPrice instanceof Number) p.setAlt(((Number)altPrice).doubleValue());
			return p;
		}
		else
			return null;
	}
	
	
	private double[] getRowValuesAt(int row) {
		double[] quad = new double[] {0, 0, 0, 0};
		Object price = getValueAt(row, 2);
		if (price != null && price instanceof Number) quad[0] = ((Number)price).doubleValue();
		
		Object lowPrice = getValueAt(row, 3);
		if (lowPrice != null && lowPrice instanceof Number) quad[1] = ((Number)lowPrice).doubleValue();
		
		Object highPrice = getValueAt(row, 4);
		if (highPrice != null && highPrice instanceof Number) quad[2] = ((Number)highPrice).doubleValue();

		Object altPrice = getValueAt(row, 5);
		if (altPrice != null && altPrice instanceof Number) quad[3] = ((Number)altPrice).doubleValue();
		
		return quad;
	}
	
	
//	/**
//	 * Getting the class of value at specified row and specified column.
//	 * @param row specified row.
//	 * @param column specified column.
//	 * @return class of value at specified row and specified column.
//	 */
//	public Class<?> getColumnClass(int row, int column) {
//		Object value = getValueAt(row, column);
//		if (value == null)
//			return getColumnClass(column);
//		else
//			return value.getClass();
//	}

	
	@Override
	public void tableChanged(TableModelEvent e) {
		modified = true;
//		modifiedDate = true;
	}


	protected Vector<Object> toRow(Price price) {
		Vector<Object> row = Util.newVector(0);
		
		row.add(price);
		row.add(price.getDate());
		row.add(price.get());
		row.add(price.getLow());
		row.add(price.getHigh());
		row.add(price.getAlt());
		row.add(isSelectAsTakenPrice(price));
		
		return row;
	}
	
	
	/**
	 * Getting list of column names.
	 * @return list of column names.
	 */
	private Vector<String> toColumns() {
		Vector<String> columns = Util.newVector(0);
		columns.add("");
		columns.add("Date");
		columns.add("Price");
		columns.add("Low price");
		columns.add("High price");
		columns.add("Alt price");
		columns.add("Taken");
		
		return columns;
	}
	

}



class PriceListPartial extends JDialog {


	private static final long serialVersionUID = 1L;


	protected PriceListPartialTable tblPriceList = null;
	
	
	protected Price output = null;
	
	
	protected boolean applied = false;
	
	
	protected boolean editMode = false;
	
	
	protected boolean selectMode = false;
	
	
	protected Price selectPrice = null;
	
	
	protected boolean pressOK = false;
	
	
	public PriceListPartial(Market market, Stock stock, long timeInterval, boolean editMode, boolean selectMode, Component parent) {
		super(Util.getDialogForComponent(parent), "Price list", true);
		this.tblPriceList = new PriceListPartialTable(market, stock, timeInterval);
		this.editMode = editMode;
		this.selectMode = selectMode;
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(400, 300);
		setLocationRelativeTo(Util.getDialogForComponent(parent));
		setLayout(new BorderLayout());
		
		
		JPanel header = new JPanel(new BorderLayout());
		add(header, BorderLayout.NORTH);
		
		JLabel info = new JLabel(stock.code() + " in " + (stock.isBuy() ? "Buy" : "Sell"));
		header.add(info, BorderLayout.NORTH);

		JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		header.add(toolbar, BorderLayout.SOUTH);
		
		JButton newPrice = new JButton("New price");
		newPrice .addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addNewPrice();
			}
		});
		if(editMode) toolbar.add(newPrice);
		
		
		JPanel body = new JPanel(new BorderLayout());
		add(body, BorderLayout.CENTER);
		
		tblPriceList.setEditable(editMode);
		body.add(new JScrollPane(tblPriceList), BorderLayout.CENTER);
		
		
		JPanel footer = new JPanel();
		add(footer, BorderLayout.SOUTH);
		
		JButton ok = new JButton(selectMode ? "Select" : "OK");
		ok.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ok();
			}
		});
		footer.add(ok);
		
		PriceListPartial thisPriceList = this;
		JButton apply = new JButton("Apply");
		apply.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean applied = apply();
				if (applied) JOptionPane.showMessageDialog(thisPriceList, "Successful applying", "Successful applying", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		if (editMode) footer.add(apply);
		
		JButton refresh = new JButton("Refresh");
		refresh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tblPriceList.update();
			}
		});
		if(selectMode) footer.add(refresh);

		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		footer.add(cancel);
	}
	
	
	private void addNewPrice() {
		Price input = tblPriceList.getLastRowPrice();
		if (input == null) return;
		NewPrice newPrice = new NewPrice(input, false, this);
		newPrice.setVisible(true);
		
		Price output = newPrice.getOutput();
		if (output == null) return;
		
		tblPriceList.addPrice(output);
		try {int n = tblPriceList.getRowCount(); if (n > 0) tblPriceList.setRowSelectionInterval(n - 1, n - 1);} catch (Throwable e) {}
	}

	
	private void ok() {
		apply();
		
		if (selectMode) output = tblPriceList.getSelectedPrice();
		
		pressOK = true;
		
		dispose();
	}
	
	
	private boolean apply() {
		if (!editMode && !selectMode) return false;
		
		boolean applied = tblPriceList.apply();
		this.applied = this.applied || applied;
		
		return applied;
	}
	
	
	public boolean isApplied() {
		return this.applied;
	}

	
	public boolean isPressOK() {
		return pressOK;
	}
	
	
	@Override
	public void dispose() {
		if (editMode) {
			if (tblPriceList.isModified() && editMode) {
				int ret = JOptionPane.showConfirmDialog(this, "Would you like to apply some changes into price list", "Apply request", JOptionPane.YES_NO_OPTION);
				if (ret == JOptionPane.YES_OPTION) apply();
			}
		}
		
		super.dispose();
	}
	
	
	public Price getOutput() {
		return output;
	}
	
	
}



class NewPrice extends JDialog {


	private static final long serialVersionUID = 1L;


	protected JTextField txtCode;

	
	protected JFormattedTextField txtPrice;
	
	
	protected JFormattedTextField txtLowPrice;
	
	
	protected JFormattedTextField txtHighPrice;
	
	
	protected JFormattedTextField txtAltPrice;
	
	
	protected JFormattedTextField txtLastDate;
	
	
	protected JButton btnLastDateNow;
	
	
	protected Price input = null;

	
	protected Price output = null;
	
	
	protected String newCode = null;

	
	public NewPrice(Price input, boolean newCode, Component parent) {
		super(Util.getDialogForComponent(parent), "New price", true);
		this.input = input;
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(300, 250);
		setLocationRelativeTo(Util.getDialogForComponent(parent));
		setLayout(new BorderLayout());
		
		JPanel header = new JPanel(new BorderLayout());
		add(header, BorderLayout.NORTH);
		
		JPanel left = new JPanel(new GridLayout(0, 1));
		header.add(left, BorderLayout.WEST);
		
		if(newCode) left.add(new JLabel("Code (*): "));
		left.add(new JLabel("Price (*): "));
		left.add(new JLabel("Low price: "));
		left.add(new JLabel("High price: "));
		left.add(new JLabel("Alt price: "));
		left.add(new JLabel("Date: "));

		JPanel right = new JPanel(new GridLayout(0, 1));
		header.add(right, BorderLayout.CENTER);
		
		JPanel paneCode = new JPanel(new BorderLayout());
		if(newCode) right.add(paneCode);
		txtCode = new JTextField(StockProperty.NONAME);
		paneCode.add(txtCode, BorderLayout.CENTER);

		JPanel panePrice = new JPanel(new BorderLayout());
		right.add(panePrice);
		txtPrice = new JFormattedTextField(Util.getNumberFormatter());
		txtPrice.setValue(input.get());
		panePrice.add(txtPrice, BorderLayout.CENTER);
		
		JPanel paneLowPrice = new JPanel(new BorderLayout());
		right.add(paneLowPrice);
		txtLowPrice = new JFormattedTextField(Util.getNumberFormatter());
		txtLowPrice.setValue(input.getLow());
		paneLowPrice.add(txtLowPrice, BorderLayout.CENTER);
		
		JPanel paneHighPrice = new JPanel(new BorderLayout());
		right.add(paneHighPrice);
		txtHighPrice = new JFormattedTextField(Util.getNumberFormatter());
		txtHighPrice.setValue(input.getHigh());
		paneHighPrice.add(txtHighPrice, BorderLayout.CENTER);
		
		JPanel paneAltPrice = new JPanel(new BorderLayout());
		right.add(paneAltPrice);
		txtAltPrice = new JFormattedTextField(Util.getNumberFormatter());
		txtAltPrice.setValue(input.getAlt());
		paneAltPrice.add(txtAltPrice, BorderLayout.CENTER);

		JPanel paneLastDate = new JPanel(new BorderLayout());
		right.add(paneLastDate);
		txtLastDate = new JFormattedTextField(Util.getDateFormatter());
		txtLastDate.setValue(new Date(input.getDate().getTime() + StockProperty.TIME_UPDATE_PRICE_INTERVAL));
		paneLastDate.add(txtLastDate, BorderLayout.CENTER);
		//
		btnLastDateNow = new JButton("Now");
		btnLastDateNow.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				txtLastDate.setValue(new Date());
			}
		});
		btnLastDateNow.setEnabled(true);
		paneLastDate.add(btnLastDateNow, BorderLayout.EAST);
		
		
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
	
	
	private boolean validateInput() {
		double price = txtPrice.getValue() instanceof Number ? ((Number)txtPrice.getValue()).doubleValue() : 0;
		double lowPrice = txtLowPrice.getValue() instanceof Number ? ((Number)txtLowPrice.getValue()).doubleValue() : 0;
		double highPrice = txtHighPrice.getValue() instanceof Number ? ((Number)txtHighPrice.getValue()).doubleValue() : 0;
		
		if (lowPrice == 0 && highPrice == 0) {
			txtLowPrice.setValue(lowPrice = price);
			txtHighPrice.setValue(highPrice = price);
		}
		else if (price < lowPrice || price > highPrice)
			return false;
		
		Date lastDate = txtLastDate.getValue() instanceof Date ? (Date)txtLastDate.getValue() : null;
		if (lastDate == null) return false;
		
		return true;
	}
	
	
	private void ok() {
		output = null;
		try {output = (Price)input.clone();} catch (Throwable e) {Util.trace(e);}
		if (output == null) return;
		
		if (!validateInput() || output == null) {
			output = null;
			JOptionPane.showMessageDialog(this, "Invalid input", "Invalid input", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		output.set(((Number)txtPrice.getValue()).doubleValue());
		output.setLow(((Number) txtLowPrice.getValue()).doubleValue());
		output.setHigh(((Number) txtHighPrice.getValue()).doubleValue());
		
		double altPrice = txtAltPrice.getValue() instanceof Number ? ((Number)txtAltPrice.getValue()).doubleValue() : 0;
		if (altPrice < output.getLow() || altPrice > output.getHigh()) altPrice = 0;
		output.setAlt(altPrice);

		output.setTime(((Date)txtLastDate.getValue()).getTime());

		newCode = txtCode.getText();
		newCode = newCode != null ? newCode.trim() : "";
		newCode = newCode.isEmpty() ? null : newCode;
		
		dispose();
	}
	
	
	public Price getOutput() {
		return output;
	}
	

	public String getNewCode() {
		return newCode;
	}
	
	
}



