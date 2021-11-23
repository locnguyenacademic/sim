package net.jsi.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import net.jsi.Util;

public class TempTable extends JTable {

	
	private static final long serialVersionUID = 1L;
	
	
	public TempTable() {
		super();
		setModel(new TempTableModel());
		
		setAutoCreateRowSorter(true);
		getTableHeader().setReorderingAllowed(false);
		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(SwingUtilities.isRightMouseButton(e) ) {
					JPopupMenu contextMenu = createContextMenu();
					if(contextMenu != null) contextMenu.show((Component)e.getSource(), e.getX(), e.getY());
				}
				else if (e.getClickCount() >= 2) {
					
				}
			}
		});
		
		this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {

				}
				else if(e.getKeyCode() == KeyEvent.VK_F5) {
					update();
				}
			}
		});

		update();
	}

	
	protected JPopupMenu createContextMenu() {
		JPopupMenu ctxMenu = new JPopupMenu();
		
		JMenuItem miView = new JMenuItem("View");
		miView.addActionListener( 
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {

				}
			});
		ctxMenu.add(miView);

		return ctxMenu;
	}
	
	
	public void update() {
		int selectedRow = getSelectedRow();

		getModel2().update();
		init();
		
		if (selectedRow >= 0 && selectedRow < getRowCount()) {try {setRowSelectionInterval(selectedRow, selectedRow);} catch (Throwable e) {}}
	}
	
	
	private void init() {
		int lastColumn = getColumnCount() - 1;
		if (lastColumn > 0) {
			getColumnModel().getColumn(lastColumn).setMaxWidth(0);
			getColumnModel().getColumn(lastColumn).setMinWidth(0);
			getColumnModel().getColumn(lastColumn).setPreferredWidth(0);
		}
	}

	
	protected TempTableModel getModel2() {
		return (TempTableModel)getModel();
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
	
	
	@Override
	public String getToolTipText(MouseEvent event) {
		return super.getToolTipText(event);
	}


}



class TempTableModel extends DefaultTableModel {
	
	
	private static final long serialVersionUID = 1L;
	
	
	public TempTableModel() {

	}
	
	
	protected void update() {
		Vector<Vector<Object>> data = Util.newVector(0);
		
		setDataVector(data, toColumns());
	}

	
	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}

	
	protected Vector<Object> toRow() {
		Vector<Object> row = Util.newVector(0);
		
		return row;
	}

	
	/**
	 * Getting list of column names.
	 * @return list of column names.
	 */
	private Vector<String> toColumns() {
		Vector<String> columns = Util.newVector(0);
		
		return columns;
	}

	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return super.getColumnClass(columnIndex);
	}
	
	
}



class TempDialog extends JDialog {

	
	private static final long serialVersionUID = 1L;

	
	protected TempTable tblTemp = null;
	
	
	protected JButton btnOK;
	
	
	protected JButton btnCancel;
	
	
	protected boolean isPressOK = false;
	
	
	public TempDialog(Component component) {
		super(Util.getDialogForComponent(component), "Title", true);
		this.tblTemp = new TempTable();
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setSize(450, 350);
		setLocationRelativeTo(Util.getDialogForComponent(component));
		setLayout(new BorderLayout());
		
		JPanel header = new JPanel();
		add(header, BorderLayout.NORTH);

		JPanel body = new JPanel(new BorderLayout());
		add(body, BorderLayout.CENTER);
		
		body.add(new JScrollPane(this.tblTemp), BorderLayout.CENTER);
		
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
	
	
}



