package muminav;

import muminav.skin.*;

import java.awt.*;
import java.applet.*;
import java.util.Hashtable;
import java.util.Vector;
import java.lang.Class;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.MouseInputAdapter;
import java.net.URL;
import java.net.MalformedURLException;

/**
 *  Description of the Class
 *
 *@author     Joerg
 *@created    17. September 2002
 */
public class MuminavPanel extends JPanel {

	private MyToolTipManager manager;

	boolean drawFirst = true;

	// zoom attributes
	// indicates if we are in zoom mode or not
	private boolean enableZoom = false;
	// start point of zoom rectangle
	private Point startPoint = null;
	// end point of zoom rectangle
	private Point endPoint = null;
	// default size for zoom
	private int defaultZoom = 100;

	// dimension of the basis raster
	private Dimension rasterDimension;

	/**  Description of the Field */
	public Vector treeRoot;
	private JApplet parent;
	private AppletContext appletContext;


	/**
	 *  Constructor for the MuminavPanel object
	 *
	 *@param  tr    Description of the Parameter
	 *@param  prnt  Description of the Parameter
	 *@param  ac    Description of the Parameter
	 */
	public MuminavPanel(Vector tr, JApplet prnt, AppletContext ac) {
		super();

		// raster dimension uebergeben
		// hier noch hardcoded
		rasterDimension = new Dimension(35, 78);

//    MyListener myListener = new MyListener();
//    MyMotionListener myMotionListener = new MyMotionListener();

//    addMouseListener(myListener);
//    addMouseMotionListener(myMotionListener);

//    this.setOpaque(true);
		this.setBackground(Color.white);
		MyListener myListener = new MyListener();
		addMouseListener(myListener);
		this.setLayout(null);

		manager = new MyToolTipManager(prnt, this);

		appletContext = ac;
		treeRoot = tr;
		parent = prnt;
	}


	/**
	 *  Starts the painting work.
	 *
	 *@param  g  Description of the Parameter
	 */
	public void paint(Graphics g) {
		super.paint(g);
		// Draw each child of the root
		//     first cycle
		drawFirst = true;
		for (int i = 0; i < treeRoot.size(); i++) {
			drawTree(g, (Part) treeRoot.elementAt(i));
		}
		//     second cycle
		drawFirst = false;
		for (int i = 0; i < treeRoot.size(); i++) {
			drawTree(g, (Part) treeRoot.elementAt(i));
		}

		if (enableZoom) {
			Font font = g.getFont();
			g.setFont(new Font(font.getFamily(), font.getStyle(), 30));
			FontMetrics fm = g.getFontMetrics();
			g.setColor(new Color(100, 100, 100, 60));
			g.drawString("ZOOM", this.getSize().width - fm.stringWidth("ZOOM") - 10
					, this.getSize().height - 10);
		}

		// Tooltip zeichnen
		if (manager.isVisible()) {
			Part ttpart = manager.getTooltipPart();
			if (ttpart != null) {
				String ttText = ttpart.getTooltipText();
				if (ttText != null) {
					manager.getTooltipPart().drawTooltip(g, manager.m_lastX,
							manager.m_lastY, ttText);
				}
			}
		}
	}


	/**
	 *  recursive Method to traverse tree and draw Parts
	 *
	 *@param  g  Graphic-Object
	 *@param  t  Subtree to draw
	 */
	private void drawTree(Graphics g, Part t) {
		// get Childs
		Vector childs = t.getChilds();

		// Draw each subtree
		for (int i = childs.size() - 1; i >= 0; i--) {
			drawTree(g, (Part) childs.elementAt(i));
		}
		// draw Part
		if (t.drawFirst() == drawFirst) {
			t.fitToRaster(this.getSize(), rasterDimension, startPoint, endPoint).draw(g);
		}
	}


	/**
	 *  Gets the toolTipText attribute of the MuminavPanel object
	 *
	 *@param  event  Description of the Parameter
	 *@return        The toolTipText value
	 */
	public String getToolTipText(MouseEvent event) {
		return (":" + Math.random() * 100);
	}


	/**
	 *  Description of the Class
	 *
	 *@author     Joerg
	 *@created    15. September 2002
	 */
	class MyListener extends MouseInputAdapter {
		/**
		 *  Description of the Method
		 *
		 *@param  e  Description of the Parameter
		 */
		public void mousePressed(MouseEvent e) {
			int button;
			button = e.getButton();
			switch (button) {
							case 3:
								// left button -> load content
								handleEvents(e.getPoint());
								break;
							case 1:
								// draggin a rectangle with the right button
								startPoint = e.getPoint();
								break;
			}
		}


		/**
		 *  Description of the Method
		 *
		 *@param  e  Description of the Parameter
		 */
		public void mouseReleased(MouseEvent e) {
			if (e.getButton() == 1) {
				endPoint = e.getPoint();
				if (enableZoom) {
					enableZoom = false;
					startPoint = null;
					endPoint = null;
					repaint();
				}
				else {
					Dimension zoomDim = new Dimension(Math.abs(startPoint.x - endPoint.x)
							, Math.abs(startPoint.y - endPoint.y));
					// at single klick without move a default zoom window is used
					if (zoomDim.width + zoomDim.height < 4) {
						startPoint.x = new Double(startPoint.x - defaultZoom / 2).intValue();
						startPoint.y = new Double(startPoint.y - defaultZoom / 2).intValue();
						endPoint.x = new Double(endPoint.x + defaultZoom / 2).intValue();
						endPoint.y = new Double(endPoint.y + defaultZoom / 2).intValue();
					}
					enableZoom = true;
					repaint();
				}
			}
		}
	}


	/**
	 *  sets the isActive attribute in all parts in the given tree to false
	 *
	 *@param  part  Description of the Parameter
	 */
	private void clearActive(Part part) {
		part.setActive(false);
		for (int i = 0; i < part.size(); i++) {
			clearActive((Part) part.getChilds().elementAt(i));
		}
	}


	/**
	 *  handle Events
	 *
	 *@param  point  Description of the Parameter
	 *@return
	 */
	private boolean handleEvents(Point point) {

		// Events in each child of the root
		for (int i = 0; i < treeRoot.size(); i++) {
			Part part = (Part) treeRoot.elementAt(i);
			clearActive(part);
			if (handleEventsForTree(part, point)) {
				// my be a element draws somthing depending on isActive
				repaint();
				return (true);
			}
		}
		// my be a element draws somthing depending on isActive
		repaint();
		return (false);
	}


	/**
	 *  Description of the Method
	 *
	 *@param  t      Description of the Parameter
	 *@param  point  Description of the Parameter
	 *@return        Description of the Return Value
	 */
	private boolean handleEventsForTree(Part t, Point point) {
		// clicked inside Part?
		if (t.fitToRaster(this.getSize(), rasterDimension, startPoint, endPoint).isInside(point)) {
			//      repaint();
//			showStatus("MyApplet: Loading image file");
			System.out.println("you hit me!");
			if (t.getUrl() != "") {
				System.out.println("loading " + t.getUrl());
				try {
					appletContext.showDocument(new URL(parent.getCodeBase() + t.getUrl()), "Content");
				}
				catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			t.setActive(true);
			return (true);
		}
		// get Childs
		Vector childs = t.getChilds();

		// Events in each subtree
		for (int i = 0; i < childs.size(); i++) {
			if (handleEventsForTree((Part) childs.elementAt(i), point)) {
				return (true);
			}
		}
		return (false);
	}


	/**
	 *  Gets the zoomStart attribute of the MuminavPanel object
	 *
	 *@return    The zoomStart value
	 */
	public Point getStartZoom() {
		return startPoint;
	}


	/**
	 *  Gets the zoomEnd attribute of the MuminavPanel object
	 *
	 *@return    The zoomEnd value
	 */
	public Point getEndZoom() {
		return endPoint;
	}


	/**
	 *  Gets the rasterDimension attribute of the MuminavPanel object
	 *
	 *@return    The rasterDimension value
	 */
	public Dimension getRasterDimension() {
		return rasterDimension;
	}

}

