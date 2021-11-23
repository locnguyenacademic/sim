package net.jsi.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Date;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

import net.jsi.EstimateStock;
import net.jsi.Estimator;
import net.jsi.Market;
import net.jsi.MarketImpl;
import net.jsi.Price;
import net.jsi.QueryEstimator;
import net.jsi.Stock;
import net.jsi.StockGroup;
import net.jsi.StockImpl;
import net.jsi.Universe;
import net.jsi.Util;

public abstract class StockDescription extends JDialog {

	
	private static final long serialVersionUID = 1L;

	
	protected Market market = null;
	
	
	public StockDescription(Market market, String code, boolean buy, Stock stock, Component component) {
		super(Util.getDialogForComponent(component), stock != null ? "Stock description" : "Stock group description", true);
		this.market = market;
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setSize(300, 400);
		setLocationRelativeTo(Util.getDialogForComponent(component));
	    setJMenuBar(createMenuBar());

	    setLayout(new BorderLayout());
		
		JPanel body = new JPanel(new BorderLayout());
		add(body, BorderLayout.CENTER);
		
		long timeViewInterval = market.getTimeViewInterval();
		JTextArea txtInfo = new JTextArea();
		txtInfo.setWrapStyleWord(true);
		txtInfo.setLineWrap(true);
		txtInfo.setEditable(false);
		body.add(new JScrollPane(txtInfo), BorderLayout.CENTER);

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
		
		
		Universe u = market.getNearestUniverse();
		if (u == null) return;
		MarketImpl m = u.c(market);
		if (m == null) return;
		StockGroup group = m.get(code, buy);
		if (group == null) return;

		int index = u.lookup(market.getName());
		if (index < 0) return;
		QueryEstimator query = u.query(market.getName(), market);
		if (query == null) query = m;
		Estimator estimator = query.getEstimator(code, buy);
		if (estimator == null) return;
		
		List<EstimateStock> estimateStocks = getEstimateStocks();
		StringBuffer info = new StringBuffer();
		info.append("Code: " + code + "\n");
		info.append("Buy: " + buy + "\n");
		
		if (stock == null || estimateStocks == null || estimateStocks.size() == 0) {
			info.append("Leverage: " + (group.getLeverage() != 0 ? Util.format(1.0 / group.getLeverage()) : "Infinity") + "\n");
			info.append("Volume: " + Util.format(group.getVolume(timeViewInterval, true)) + "\n");
			info.append("Taken value: " + Util.format(group.getTakenValue(timeViewInterval)) + "\n");
			info.append("Price: " + Util.format(group.getPrice().get()) + "\n");
			info.append("Low price: " + Util.format(group.getPrice().getLow()) + "\n");
			info.append("High price: " + Util.format(group.getPrice().getHigh()) + "\n");
			if (group.getPrice().getAlt() > 0)
				info.append("Alternative price (often open price): " + Util.format(group.getPrice().getAlt()) + "\n");
			info.append("Margin: " + Util.format(group.getMargin(timeViewInterval)) + "\n");
			info.append("Profit: " + Util.format(group.getProfit(timeViewInterval)) + "\n");
			double dividend = group.getDividend(timeViewInterval);
			if (dividend > 0) {
				info.append("Dividend: " + Util.format(dividend) + "\n");
				info.append("Dividend date: " + Util.format(new Date(group.getDividendTimePoint(timeViewInterval))) + "\n");
			}
			info.append("ROI (leverage): " + Util.format(group.getROIByLeverage(timeViewInterval)*100) + "%\n");
			info.append("ROI: " + Util.format(group.getROI(timeViewInterval)*100) + "%\n");
			info.append("Oscillate ratio: " + Util.format(group.calcOscillRatio(timeViewInterval)*100) + "%\n");
			info.append("Oscillate total: " + Util.format(group.calcOscill(timeViewInterval)) + "\n");
			info.append("Unit bias (setting): " + Util.format(group.getUnitBias()) + "\n");
			
			info.append("\n");
			info.append("Estimated unit bias: " + Util.format(estimator.estimateUnitBias(timeViewInterval)) + "\n");
			info.append("Estimated total bias: " + Util.format(m.calcBias(timeViewInterval)) + "\n");
			info.append("Estimated price: " + Util.format(estimator.estimatePrice(timeViewInterval)) + "\n");
			info.append("Estimated low price: " + Util.format(estimator.estimateLowPrice(timeViewInterval)) + "\n");
			info.append("Estimated high price: " + Util.format(estimator.estimateHighPrice(timeViewInterval)) + "\n");
			info.append("Estimated price mean: " + Util.format(estimator.estimatePriceMean(timeViewInterval)) + "\n");
	
			info.append("\n");
			if (group.getLeverage() != 0) {
				double investAmount = estimator.getInvestAmount(timeViewInterval);
				if (investAmount > 0) {
					info.append("Estimated invest amount: " + Util.format(investAmount) + "\n");
					double recInvestAmount = estimator.estimateInvestAmount(timeViewInterval);
					if (recInvestAmount > 0)
						info.append("Recommended invest amount: " + Util.format(recInvestAmount) + "\n");
					double recInvestVolume = estimator.estimateInvestVolume(timeViewInterval);
					if (recInvestVolume > 0)
						info.append("Recommended invest volume: " + Util.format(recInvestVolume) + "\n");
				}
			}
			else {
				info.append("Recommended invest amount: Free due to infinity leverage"+ "\n");
			}
			
			Estimator.Invest[] dualInvest = estimator.estimateDualInvest(timeViewInterval);
			if (dualInvest != null && dualInvest.length >= 2) {
				info.append("\n");
				
				info.append((dualInvest[0].buy ? "Buy" : "Sell") + "1\n");
				info.append("Volume: " + dualInvest[0].volume + "\n");
				info.append("Price: " + dualInvest[0].price + "\n");
				info.append("Margin: " + dualInvest[0].margin + "\n");
				info.append("Stop loss: " + dualInvest[0].stopLoss + "\n");
				info.append("Take profit: " + dualInvest[0].takeProfit + "\n");
				
				info.append((dualInvest[1].buy ? "Buy" : "Sell") + "2\n");
				info.append("Volume: " + dualInvest[1].volume + "\n");
				info.append("Price: " + dualInvest[1].price + "\n");
				info.append("Margin: " + dualInvest[1].margin + "\n");
				info.append("Stop loss: " + dualInvest[1].stopLoss + "\n");
				info.append("Take profit: " + dualInvest[1].takeProfit + "\n");
				info.append("Take profit (large): " + dualInvest[1].largeTakeProfit + "\n");
			}
		}
		else {
			StockImpl s = m.c(stock);
			info.append("Leverage: " + (stock.getLeverage() != 0 ? Util.format(1.0 / stock.getLeverage()) : "Infinity") + "\n");
			info.append("Volume: " + Util.format(stock.getVolume(timeViewInterval, true)) + "\n");
			if (s != null) {
				Price takenPrice = s.getTakenPrice(timeViewInterval);
				info.append("Taken date: " + Util.format(takenPrice.getDate()) + "\n");
				info.append("Taken price: " + Util.format(takenPrice.get()) + "\n");
			}
			info.append("Taken value: " + Util.format(stock.getTakenValue(timeViewInterval)) + "\n");
			info.append("Price: " + Util.format(stock.getPrice().get()) + "\n");
			info.append("Low price: " + Util.format(stock.getPrice().getLow()) + "\n");
			info.append("High price: " + Util.format(stock.getPrice().getHigh()) + "\n");
			if (stock.getPrice().getAlt() > 0)
				info.append("Alternative price (often open price): " + Util.format(stock.getPrice().getAlt()) + "\n");
			info.append("Stop loss: " + Util.format(s.getStopLoss()) + "\n");
			info.append("Take profit: " + Util.format(s.getTakeProfit()) + "\n");
			info.append("Margin (" + (s != null && s.isFixedMargin() ? "fixed" : "not fixed") + "): " + Util.format(stock.getMargin(timeViewInterval)) + "\n");
			info.append("Profit: " + Util.format(stock.getProfit(timeViewInterval)) + "\n");
			double dividend = stock.getDividend(timeViewInterval);
			if (dividend > 0) {
				info.append("Dividend: " + Util.format(dividend) + "\n");
				info.append("Dividend date: " + Util.format(new Date(stock.getDividendTimePoint(timeViewInterval))) + "\n");
			}
			long committedTime = stock.getCommittedTimePoint();
			if (committedTime > 0) {
				info.append("Committed date: " + Util.format(new Date(committedTime)) + "\n");
			}
			info.append("ROI (leverage): " + Util.format(stock.getROIByLeverage(timeViewInterval)*100) + "%\n");
			info.append("ROI: " + Util.format(stock.getROI(timeViewInterval)*100) + "%\n");
			info.append("Oscillate ratio: " + Util.format(s.calcOscillRatio(timeViewInterval)*100) + "%\n");
			info.append("Oscillate: " + Util.format(s.getPriceOscill(timeViewInterval)) + "\n");
			info.append("Unit bias (setting): " + Util.format(s.getUnitBias()) + "\n");
			
			info.append("\n");
			info.append("Estimated unit bias: " + Util.format(estimator.estimateUnitBias(timeViewInterval)) + "\n");
			info.append("Estimated price: " + Util.format(estimator.estimatePrice(timeViewInterval)) + "\n");
			info.append("Estimated low price: " + Util.format(estimator.estimateLowPrice(timeViewInterval)) + "\n");
			info.append("Estimated high price: " + Util.format(estimator.estimateHighPrice(timeViewInterval)) + "\n");
			info.append("Estimated price mean: " + Util.format(estimator.estimatePriceMean(timeViewInterval)) + "\n");
			//
			if (estimateStocks == null || estimateStocks.size() == 0) {
				info.append("Estimated stop loss: " + Util.format(estimator.estimateStopLoss(timeViewInterval)) + "\n");
				info.append("Estimated take profit: " + Util.format(estimator.estimateTakeProfit(timeViewInterval)) + "\n");
			}
			else {
				EstimateStock found = EstimateStock.get(code, buy, estimateStocks);
				if (found == null) {
					info.append("Estimated stop loss: " + Util.format(estimator.estimateStopLoss(timeViewInterval)) + "\n");
					info.append("Estimated take profit: " + Util.format(estimator.estimateTakeProfit(timeViewInterval)) + "\n");
				}
				else {
					info.append("Estimated stop loss: " + Util.format(found.estimatedStopLoss) + "\n");
					info.append("Estimated take profit: " + Util.format(found.estimatedTakeProfit) + "\n");
				}
			}
		}
		txtInfo.setText(info.toString());
	}
	
	
	protected abstract List<EstimateStock> getEstimateStocks();
	
	
	private JMenuBar createMenuBar() {
		JMenuBar mnBar = new JMenuBar();
		
		JMenu mnTool = new JMenu("Tool");
		mnTool.setMnemonic('t');
		mnBar.add(mnTool);

		JMenuItem mniCalc = new JMenuItem(
			new AbstractAction("Calculator") {
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					calc();
				}
			});
		mniCalc.setMnemonic('c');
		mniCalc.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
		mnTool.add(mniCalc);
		
		return mnBar;
	}


	private void calc() {
		JOptionPane.showMessageDialog(this, "This function not implemented yet", "Not implemented yet", JOptionPane.INFORMATION_MESSAGE);
	}
	
	
}



