package muminav;

import muminav.skin.*;

import java.awt.*;
import java.applet.*;
import java.util.Vector;
import java.util.HashMap;
import java.util.Iterator;
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
public class MuminavPanel extends JPanel implements ActionListener {

	private final int CONTROLS_HEIGHT = 30;

	private MuminavToolTipManager manager;

	// if true, only parts with drawFirst = true were painted, otherwise not
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
	// current position on red path
	private int curPosRedPath = -1;
	// hashtable contains all elements on red path
	private HashMap redElements = new HashMap();
	// current activePart
	private Part activePart = null;

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

		// get the raster dimension out of the NavNet element
		Part first = (Part) tr.elementAt(0);
		if (first instanceof NavNet) {
			rasterDimension = ((NavNet) first).getRasterDimension();
			if (rasterDimension == null) {
				System.out.println("\nRaster Dimension not found in NavNet Element (using default [50x50])");
				rasterDimension = new Dimension(50, 50);
			}
		}
		else {
			System.out.println("\nNo NavNet Mainelement found to setup the Net");
		}

		this.setLayout(new BorderLayout());
		this.setBackground(Color.white);
		addMouseListener(new MyListener());

		manager = new MuminavToolTipManager(prnt, this);
		appletContext = ac;
		treeRoot = tr;
		parent = prnt;

		for (int i = 0; i < treeRoot.size(); i++) {
			searchRedElements((Part) treeRoot.elementAt(i));
		}
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
	 *  Description of the Method
	 *
	 *@param  e  Description of the Parameter
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("forward")) {

			if (activePart != null) {
				activePart.setActive(false);
			}
			Part nextElement = getNextRedElement();
			nextElement.setActive(true);
			activePart = nextElement;
			curPosRedPath = nextElement.getPosRedPath();
			repaint();

		}
		if (e.getActionCommand().equals("backward")) {
			if (activePart != null) {
				activePart.setActive(false);
			}
			Part nextElement = getPreviousRedElement();
			nextElement.setActive(true);
			activePart = nextElement;
			curPosRedPath = nextElement.getPosRedPath();
			repaint();
		}
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
			manager.setVisible(false);
			button = e.getButton();
			switch (button) {
							case 1:
								// left button -> load content
								handleEvents(e.getPoint());
								//test();
								break;
							case 3:
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
			manager.setVisible(false);
			if (e.getButton() == 3) {
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


	/**  A unit test for JUnit */
	public void test() {

		JPopupMenu popup = new JPopupMenu("settings");
		JMenuItem item = new JMenuItem("Ausführen");
		popup.add(item);
		item = new JMenuItem("Beenden");
		popup.add(item);
		popup.setBorderPainted(true);
		popup.setPopupSize(80, 40);
		popup.show(parent, 50, 50);

	}


	/**
	 *  handle Events
	 *
	 *@param  point  Description of the Parameter
	 *@return
	 */
	private boolean handleEvents(Point point) {
		if (activePart != null) {
			activePart.setActive(false);
			activePart = null;
		}
		// Events in each child of the root
		for (int i = 0; i < treeRoot.size(); i++) {
			Part part = (Part) treeRoot.elementAt(i);
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
			//		showStatus("MyApplet: Loading image file");

			if (t.getUrl() != "") {
				try {
					appletContext.showDocument(new URL(parent.getCodeBase() + t.getUrl()), "Content");
				}
				catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			// what is with the red path
			if (t.getPosRedPath() >= 0) {
				// an part on red Path was klicked
				if (t.getPosRedPath() != curPosRedPath) {
					curPosRedPath = t.getPosRedPath();
				}
			}
			t.setActive(true);
			activePart = t;
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


	/**
	 *  Description of the Method
	 *
	 *@param  part  Description of the Parameter
	 *@return       Description of the Return Value
	 */
	private int searchRedElements(Part part) {
		Vector childs = part.getChilds();
		if (part.getPosRedPath() >= 0) {
			redElements.put(new Integer(part.getPosRedPath()), part);
		}
		for (int i = childs.size() - 1; i >= 0; i--) {
			searchRedElements((Part) childs.elementAt(i));
		}
		return redElements.size();
	}


	/**
	 *Constructor for the getNextRedElement object
	 *
	 *@return    The nextRedElement value
	 */
	private Part getNextRedElement() {
		int nextPos;
		int pos = Integer.MAX_VALUE;

		Iterator iter = redElements.keySet().iterator();
		while (iter.hasNext()) {
			nextPos = ((Integer) iter.next()).intValue();
			if (nextPos > curPosRedPath && nextPos < pos) {
				pos = nextPos;
			}
		}
		if (pos == Integer.MAX_VALUE) {
			pos = curPosRedPath;
		}
		return (Part) redElements.get(new Integer(pos));
	}


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	public Part getPreviousRedElement() {
		int nextPos;
		int pos = -1;

		if (curPosRedPath == -1) {
			curPosRedPath = Integer.MAX_VALUE;
		}

		Iterator iter = redElements.keySet().iterator();
		while (iter.hasNext()) {
			nextPos = ((Integer) iter.next()).intValue();
			if (nextPos < curPosRedPath && nextPos > pos) {
				pos = nextPos;
			}
		}
		if (pos == -1) {
			pos = curPosRedPath;
		}
		return (Part) redElements.get(new Integer(pos));
	}


	/**
	 *  Gets the numberOfRedElements attribute of the MuminavPanel object
	 *
	 *@return    The numberOfRedElements value
	 */
	public int getNumberOfRedElements() {
		return redElements.size();
	}

}

