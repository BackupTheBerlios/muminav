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
 * This class provides a JPanel which is able to display a navigation net. It can easily be created and
 * added as component to the context of an applet
 */
public class MuminavPanel extends JPanel implements ActionListener {

	private MuminavToolTipManager manager;

	// set true, if this element have to draw at first (e.g.: connectors)
	boolean drawFirst = true;

	// indicates if we are in zoom mode or not
	private boolean enableZoom = false;
	// start point of zoom rectangle
	private Point startPoint = null;
	// end point of zoom rectangle
	private Point endPoint = null;
	// default size for zoom
	private int defaultZoom = 100;
	// dimension of the net raster
	private Dimension rasterDimension;
	// current position on red path
	private int curPosRedPath = -1;
	// hashtable contains all elements on red path
	private HashMap redElements = new HashMap();
	// current active part
	private Part activePart = null;
	/**  root of the tree */
	public Vector treeRoot;
	// parent applet
	private JApplet parent;
	// applett context from parent applet
	private AppletContext appletContext;


	/**
	 *  Creates a MuminavPanel from a Vector discribing a navigation net
	 *
	 *@param  tr    the tree that dicribes the net
	 *@param  prnt  the parent applet
	 */
	public MuminavPanel(Vector tr, JApplet prnt) {
		super();

		treeRoot = tr;
		this.setLayout(new BorderLayout());
		this.setBackground(Color.white);

		if (treeRoot.size() != 0) {
			manager = new MuminavToolTipManager(prnt, this);
			appletContext = prnt.getAppletContext();
			parent = prnt;

			Part first = (Part) tr.elementAt(0);
			if (first instanceof NavNet) {
				// get the raster dimension out of the NavNet element
				rasterDimension = ((NavNet) first).getRasterDimension();
				if (rasterDimension == null) {
					System.out.println("\nRaster Dimension not found in NavNet Element (using default [50x50])");
					rasterDimension = new Dimension(50, 50);
				}
				// try to load the introducing html file
				String url = ((NavNet) first).getUrl();
				if (!url.equals("")) {
					try {
						appletContext.showDocument(new URL(parent.getCodeBase() + url), "Content");
					}
					catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
			else {
				System.out.println("\nNo NavNet Mainelement found to setup the Net");
			}

			addMouseListener(new MyListener());
			// the redElements hashtable will be filled
			searchRedElements((Part) treeRoot.elementAt(0));
		}
	}


	/**
	 *  Starts the painting work.
	 *
	 *@param  g  Description of the Parameter
	 */
	public void paint(Graphics g) {
		super.paint(g);
		// we have a net to draw, go to work
		if (treeRoot.size() != 0) {
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

			// a small zoom lable will be displayed in the lower right edge in zoom mode
			if (enableZoom) {
				Font font = g.getFont();
				g.setFont(new Font(font.getFamily(), font.getStyle(), 30));
				FontMetrics fm = g.getFontMetrics();
				g.setColor(new Color(100, 100, 100, 60));
				g.drawString("ZOOM", this.getSize().width - fm.stringWidth("ZOOM") - 10
						, this.getSize().height - 10);
			}

			// draw tooltip
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
		else {
			// we got an empty net, so we have nothing to draw
			Font font = g.getFont();
			g.setFont(new Font(font.getFamily(), font.getStyle(), 20));
			FontMetrics fm = g.getFontMetrics();
			g.setColor(new Color(120, 120, 120));
			Rectangle rect = g.getClipBounds();
			String text = new String("nothing to draw");
			g.drawString(text, rect.width / 2 + rect.x - fm.stringWidth(text) / 2
					, rect.height / 2 + rect.y - fm.getHeight() / 2 + fm.getAscent());
		}
	}


	/**
	 *  recursive Method to traverse tree and draw Parts
	 *
	 *@param  g  Graphic-Object
	 *@param  t  Subtree to draw
	 */
	private void drawTree(Graphics g, Part t) {
		// get childs
		Vector childs = t.getChilds();

		// draw each subtree
		for (int i = childs.size() - 1; i >= 0; i--) {
			drawTree(g, (Part) childs.elementAt(i));
		}
		// draw Part
		if (t.drawFirst() == drawFirst) {
			t.fitToRaster(this.getSize(), rasterDimension, startPoint, endPoint).draw(g);
		}
	}


	/**
	 *  needed for ActionListener interface
	 *  processes the occuring ActionEvents
	 *
	 *@param  e  Description of the Parameter
	 */
	public void actionPerformed(ActionEvent e) {
		// the next button was pressed
		if (e.getActionCommand().equals("forward")) {
			if (activePart != null) {
				activePart.setActive(false);
			}
			activePart = getNextRedElement();
			activePart.setActive(true);
			curPosRedPath = activePart.getPosRedPath();
			// load the content page
			if (!activePart.getUrl().equals("")) {
				try {
					appletContext.showDocument(new URL(parent.getCodeBase() + activePart.getUrl()), "Content");
				}
				catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			repaint();

		}
		// the back button was pressed
		if (e.getActionCommand().equals("backward")) {
			if (activePart != null) {
				activePart.setActive(false);
			}
			activePart = getPreviousRedElement();
			activePart.setActive(true);
			curPosRedPath = activePart.getPosRedPath();
			// load the content page
			if (!activePart.getUrl().equals("")) {
				try {
					appletContext.showDocument(new URL(parent.getCodeBase() + activePart.getUrl()), "Content");
				}
				catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			repaint();
		}
	}


	/**  MouseInputAdapter to respond on mouse klicks */
	class MyListener extends MouseInputAdapter {
		/**
		 *  respond to mouse button pressed
		 *
		 *@param  e  the mouse event
		 */
		public void mousePressed(MouseEvent e) {
			int button;
			manager.setVisible(false);
			button = e.getButton();
			switch (button) {
							case 1:
								// left button, load content
								handleEvents(e.getPoint());
								break;
							case 3:
								// start draggin a rectangle with the right button or single click
								startPoint = e.getPoint();
								break;
			}
		}


		/**
		 *  respond to mouse button released
		 *
		 *@param  e  the mouse event
		 */
		public void mouseReleased(MouseEvent e) {
			manager.setVisible(false);
			// right button released
			if (e.getButton() == 3) {
				endPoint = e.getPoint();
				// zoom out if just zoomed in
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
	 *  handles mouse clicks on elements/parts
	 *
	 *@param  point  point where the click occured
	 *@return        true if a element was hit
	 */
	private boolean handleEvents(Point point) {
		if (activePart != null) {
			activePart.setActive(false);
			activePart = null;
		}
		// events in each child of the root
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
	 *  recursive method to traverse the tree an check if the element was klicked
	 *
	 *@param  t      processed subtree
	 *@param  point  point where the click occured
	 *@return        true if a element was hit
	 */
	private boolean handleEventsForTree(Part t, Point point) {
		// checks if the click was inside the part by using the isInside method
		if (t.fitToRaster(this.getSize(), rasterDimension, startPoint, endPoint).isInside(point)) {
			t.setActive(true);
			activePart = t;
			// loading the content page
			if (!t.getUrl().equals("")) {
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
	 *  gets the zoomStart attribute of the MuminavPanel object
	 *
	 *@return    The zoomStart value
	 */
	public Point getStartZoom() {
		return startPoint;
	}


	/**
	 *  gets the zoomEnd attribute of the MuminavPanel object
	 *
	 *@return    The zoomEnd value
	 */
	public Point getEndZoom() {
		return endPoint;
	}


	/**
	 *  gets the rasterDimension attribute of the MuminavPanel object
	 *
	 *@return    The rasterDimension value
	 */
	public Dimension getRasterDimension() {
		return rasterDimension;
	}


	/**
	 *  parts on RedPath (posRedPath >= 0) will be stored in a hashtable(redElements)
	 *
	 *@param  part  in this tree will be searched
	 *@return       number of found red path elements
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
	 *  This method returns the next element considering the right order
	 *
	 *@return    the next element value
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
	 *  This method returns the previous element considering the right order
	 *
	 *@return    the previous element
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
	 *  returns the number of elements located on the red path
	 *
	 *@return    number of elements
	 */
	public int getNumberOfRedElements() {
		return redElements.size();
	}

}

