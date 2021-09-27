package net.jsi.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

import net.jsi.StockProperty;
import net.jsi.Universe;
import net.jsi.UniverseImpl;
import net.jsi.Util;

public class StockPropertySetting extends JDialog {
	
	
	private static final long serialVersionUID = 1L;
	
	
	protected JTextArea txtMoreProperties;
	
	
	protected StockProperty output = null;
	
	
	public StockPropertySetting(StockProperty property, Component comp) {
		this(null, property, comp);
	}
	
	
	public StockPropertySetting(String code, StockProperty property, Component comp) {
		super(Util.getDialogForComponent(comp), "Setting property" + (code != null && !code.isEmpty() ? " of " + code : ""), true);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setSize(350, 250);
		setLocationRelativeTo(Util.getDialogForComponent(this));
		
	    setJMenuBar(createMenuBar());

	    setLayout(new BorderLayout());
		
		
		JPanel header = new JPanel(new BorderLayout());
		add(header, BorderLayout.NORTH);
		
		JPanel left = new JPanel(new GridLayout(0, 1));
		header.add(left, BorderLayout.WEST);
		
		//left.add(new JLabel("Maxium price count: "));
		left.add(new JLabel("Swap: "));
		left.add(new JLabel("Spread: "));
		left.add(new JLabel("Commission: "));
		//left.add(new JLabel("Price ratio: "));
		//left.add(new JLabel("Price update interval (days): "));
		
		JPanel right = new JPanel(new GridLayout(0, 1));
		header.add(right, BorderLayout.CENTER);
		
		JPanel paneMaxPriceCount = new JPanel(new BorderLayout());
		//right.add(paneMaxPriceCount);
		JFormattedTextField txtMaxPriceCount = new JFormattedTextField(Util.getNumberFormatter());
		txtMaxPriceCount.setValue(property.maxPriceCount);
		paneMaxPriceCount.add(txtMaxPriceCount, BorderLayout.CENTER);
		
		JPanel paneSwap = new JPanel(new BorderLayout());
		right.add(paneSwap);
		JFormattedTextField txtSwap = new JFormattedTextField(Util.getNumberFormatter());
		txtSwap.setValue(property.swap);
		paneSwap.add(txtSwap, BorderLayout.CENTER);
		
		JPanel paneSpread = new JPanel(new BorderLayout());
		right.add(paneSpread);
		JFormattedTextField txtSpread = new JFormattedTextField(Util.getNumberFormatter());
		txtSpread.setValue(property.spread);
		paneSpread.add(txtSpread, BorderLayout.CENTER);

		JPanel paneCommission = new JPanel(new BorderLayout());
		right.add(paneCommission);
		JFormattedTextField txtCommission = new JFormattedTextField(Util.getNumberFormatter());
		txtCommission.setValue(property.commission);
		paneCommission.add(txtCommission, BorderLayout.CENTER);
		
		JPanel panePriceRatio = new JPanel(new BorderLayout());
		//right.add(panePriceRatio);
		JFormattedTextField txtPriceRatio = new JFormattedTextField(Util.getNumberFormatter());
		txtPriceRatio.setValue(property.priceRatio);
		panePriceRatio.add(txtPriceRatio, BorderLayout.CENTER);
		
		JPanel panePriceUpdateInterval = new JPanel(new BorderLayout());
		//right.add(panePriceUpdateInterval);
		JFormattedTextField txtTimePriceUpdateInterval = new JFormattedTextField(Util.getNumberFormatter());
		txtTimePriceUpdateInterval.setValue(property.timeUpdatePriceInterval / (1000*3600*24));
		txtTimePriceUpdateInterval.setEditable(false);
		panePriceUpdateInterval.add(txtTimePriceUpdateInterval, BorderLayout.CENTER);
		
		
		JPanel body = new JPanel(new BorderLayout());
		add(body, BorderLayout.CENTER);
		JPanel paneMoreProperties = new JPanel(new BorderLayout());
		body.add(paneMoreProperties, BorderLayout.CENTER);
		//
		paneMoreProperties.add(new JLabel("More (,): "), BorderLayout.WEST);
		//
		txtMoreProperties = new JTextArea();
		txtMoreProperties.setLineWrap(true);
		txtMoreProperties.setToolTipText("Pairs \"key=value\" are separated by a comma or a new line character");
		txtMoreProperties.setText(property.getMorePropertiesText());
		paneMoreProperties.add(new JScrollPane(txtMoreProperties), BorderLayout.CENTER);

		
		JPanel footer = new JPanel();
		add(footer, BorderLayout.SOUTH);
		
		JButton ok = new JButton("OK");
		ok.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int maxPriceCount = txtMaxPriceCount.getValue() instanceof Number ? ((Number)txtMaxPriceCount.getValue()).intValue() : StockProperty.MAX_PRICE_COUNT;
				double swap = txtSwap.getValue() instanceof Number ? ((Number)txtSwap.getValue()).doubleValue() : 0;
				double spread = txtSpread.getValue() instanceof Number ? ((Number)txtSpread.getValue()).doubleValue() : 0;
				double commission = txtCommission.getValue() instanceof Number ? ((Number)txtCommission.getValue()).doubleValue() : 0;
				double priceRatio = txtPriceRatio.getValue() instanceof Number ? ((Number)txtPriceRatio.getValue()).doubleValue() : 0;
				long timePriceUpdateInterval = txtTimePriceUpdateInterval.getValue() instanceof Number ? ((Number)txtTimePriceUpdateInterval.getValue()).longValue() : 0;
				timePriceUpdateInterval = timePriceUpdateInterval*1000*3600*24;
				timePriceUpdateInterval = timePriceUpdateInterval <= 0 ? StockProperty.TIME_UPDATE_PRICE_INTERVAL : timePriceUpdateInterval;
				
				output = new StockProperty();
				output.maxPriceCount = Math.max(maxPriceCount, StockProperty.MAX_PRICE_COUNT);
				output.swap = swap;
				output.spread = spread;
				output.commission = commission;
				output.priceRatio = priceRatio;
				output.timeUpdatePriceInterval = timePriceUpdateInterval;
				output.setMorePropertiesText(txtMoreProperties.getText());
				
				dispose();
			}
		});
		footer.add(ok);
		
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				output = null;
				dispose();
			}
		});
		footer.add(cancel);
		
	}
	
	
	private JMenuBar createMenuBar() {
		JMenuBar mnBar = new JMenuBar();
		
		JMenu mnTool = new JMenu("Set");
		mnTool.setMnemonic('s');
		mnBar.add(mnTool);

		JMenuItem mniDividend = new JMenuItem(
			new AbstractAction("Set/clear dividend") {
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					setDividend();
				}
			});
		mniDividend.setMnemonic('d');
		mniDividend.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK));
		mnTool.add(mniDividend);
		
		JMenuItem mniCategory = new JMenuItem(
			new AbstractAction("Set group") {
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					setCategory();
				}
			});
		mniCategory.setMnemonic('g');
		mniCategory.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_DOWN_MASK));
		mnTool.add(mniCategory);
			
		return mnBar;
	}

	
	private void setDividend() {
		String moreText = txtMoreProperties.getText();
		if (moreText == null) moreText = "";
		StockProperty property = new StockProperty();
		property.parseText(moreText);

		JDialog dlgDividend = new JDialog(Util.getDialogForComponent(this), "Set dividend", true);
		dlgDividend.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dlgDividend.setSize(250, 200);
		dlgDividend.setLocationRelativeTo(Util.getDialogForComponent(this));
		
		dlgDividend.setLayout(new BorderLayout());

		JPanel header = new JPanel(new BorderLayout());
		dlgDividend.add(header, BorderLayout.NORTH);

		JPanel left = new JPanel(new GridLayout(0, 1));
		header.add(left, BorderLayout.WEST);
		
		left.add(new JLabel("Value: "));
		left.add(new JLabel("Date: "));

		JPanel right = new JPanel(new GridLayout(0, 1));
		header.add(right, BorderLayout.CENTER);
		
		JPanel paneValue = new JPanel(new BorderLayout());
		right.add(paneValue);
		JFormattedTextField txtValue = new JFormattedTextField(Util.getNumberFormatter());
		txtValue.setValue(property.getDividend());
		txtValue.setToolTipText("Setting value 0 to clear dividend");
		paneValue.add(txtValue, BorderLayout.CENTER);
		
		JPanel paneDate = new JPanel(new BorderLayout());
		right.add(paneDate);
		JFormattedTextField txtDate = new JFormattedTextField(Util.getDateFormatter());
		long dividendTime = property.getDividendTime();
		txtDate.setValue(dividendTime > 0 ? new Date(dividendTime) : new Date());
		paneDate.add(txtDate, BorderLayout.CENTER);
		//
		JButton btnDate = new JButton("Now");
		btnDate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				txtDate.setValue(new Date());;
			}
		});
		paneDate.add(btnDate, BorderLayout.EAST);

		JPanel footer = new JPanel();
		dlgDividend.add(footer, BorderLayout.SOUTH);
		
		JButton ok = new JButton("OK");
		ok.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				double value = txtValue.getValue() instanceof Number ? ((Number)txtValue.getValue()).doubleValue() : 0;
				Date date = txtDate.getValue() instanceof Date ? (Date)txtDate.getValue() : null;
				
				String moreText = txtMoreProperties.getText();
				if (moreText == null) moreText = "";
				StockProperty property = new StockProperty();
				property.parseText(moreText);
				if (value > 0 && date != null)
					property.setDividend(value, date.getTime());
				else
					property.setDividend(0, 0);
					
				txtMoreProperties.setText(property.getMorePropertiesText());
				
				dlgDividend.dispose();
			}
		});
		footer.add(ok);
		
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dlgDividend.dispose();
			}
		});
		footer.add(cancel);
		
		dlgDividend.setVisible(true);
	}
	
	
	private void setCategory() {
		String moreText = txtMoreProperties.getText();
		if (moreText == null) moreText = "";
		StockProperty property = new StockProperty();
		property.parseText(moreText);

		Universe u = UniverseImpl.g();
		String category = null;
		if (u != null) {
			String[] categories = u.getDefaultCategories().toArray(new String[] {});
			category = (String)JOptionPane.showInputDialog(this, "Select group name", "Setting group", JOptionPane.PLAIN_MESSAGE, null,
					categories, property.getCategory());
		}
		else {
			category = JOptionPane.showInputDialog(this, "Enter group name", property.getCategory());
			category = category != null ? category.trim() : null;
		}
		if (category == null) return;
		
		if (category.isEmpty())
			property.setCategory(null);
		else
			property.setCategory(category);
		
		txtMoreProperties.setText(property.getMorePropertiesText());
	}
	
	
	public StockProperty getOutput() {
		return output;
	}
	
	
}


/**
 * Text field contains stock property.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
class StockPropertyTextField extends JTextField {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Internal stock property.
	 */
	protected StockProperty property = null;
	
	
	/**
	 * Default constructor.
	 */
	public StockPropertyTextField() {
		super();
		setEditable(false);
	}
	
	
	/**
	 * Constructor with specified stock property.
	 * @param property specified stock property.
	 */
	public StockPropertyTextField(StockProperty property) {
		this();
		setStockProperty(property);
	}

	
	/**
	 * Getting stock property.
	 * @return internal stock property.
	 */
	public StockProperty getStockProperty() {
		return property;
	}
	
	
	/**
	 * Setting stock property.
	 * @param property specified stock property.
	 */
	public void setStockProperty(StockProperty property) {
		this.property = property;
		if (property == null)
			setText("");
		else
			setText("(stock property)");
	}
	
	
	public StockProperty modify() {
		if (property == null) return null;
		StockPropertySetting setting = new StockPropertySetting(property, this);
		setting.setVisible(true);
		
		return setting.getOutput();
	}
	
	
}

