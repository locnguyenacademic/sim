package net.jsi.ui;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;

import javax.swing.JTabbedPane;

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



public class InvestorTabbedPane extends DraggableTabbedPane {

	private static final long serialVersionUID = 1L;

	
	public InvestorTabbedPane() {

	}

	
}

