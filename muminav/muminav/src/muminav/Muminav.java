package muminav;

import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.util.Hashtable;
import java.util.Vector;
import java.lang.Class;
import javax.swing.event.MouseInputAdapter;
import javax.swing.*;

import muminav.skin.*;
import muminav.skin.math.MainElement;
import java.awt.event.*;
import java.net.URL;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

/**
 *  This is the main class of the applet which controls the other parts.
 *
 *@author     glaessel
 *@created    15. September 2002
 *@version    $Revision: 1.10 $
 */

public class Muminav extends Applet {

	String skin;
	String skinType;
	Object mySkin;

	String tooltipText;
	boolean showTooltip;
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

	int tooltipX, tooltipY;

	// dimension of the basis raster
	private Dimension rasterDimension;

	// thread for the tooltips
	TooltipWatchdog tooltipWatchdog;

	private AppletContext appletContext;

	// Vector for the child parts of the root
	private Vector treeRoot = new Vector();

	boolean isStandalone = false;


	/**
	 *  Gets the parameter attribute of the Muminav object
	 *
	 *@param  key  Description of the Parameter
	 *@param  def  Description of the Parameter
	 *@return      The parameter value
	 */
	public String getParameter(String key, String def) {
		return isStandalone ? System.getProperty(key, def) :
				(getParameter(key) != null ? getParameter(key) : def);
	}


	/**  Constructor for the Muminav object */
	public Muminav() {

		showTooltip = false;

		MyListener myListener = new MyListener();
		MyMotionListener myMotionListener = new MyMotionListener();

		addMouseListener(myListener);
		addMouseMotionListener(myMotionListener);
	}



	/**
	 *  Called if the applet is started or the applet container is reiconized or if
	 *  it gets the focus.
	 */
	public void start() {
		/*
		    tooltipWatchdog = new TooltipWatchdog( this );
		    tooltipWatchdog.start();
		  */
	}


	/**
	 *  Called if the applet container is iconized or if it losts the focus.
	 */
	public void stop() {
		/*
		    tooltipWatchdog.hold();
		    tooltipWatchdog = null;
		  */
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
	 *  Description of the Class
	 *
	 *@author     zander
	 *@created    15. September 2002
	 */
	class MyMotionListener extends MouseMotionAdapter {

		/**
		 *  Description of the Method
		 *
		 *@param  e  Description of the Parameter
		 */
		public void mouseMoved(MouseEvent e) {
			/*
			    TooltipWatchdog.updatePos( e.getX(), e.getY() );
			    if ( showTooltip == true ) {
			    System.out.println( "-bewegung" );
			    showTooltip = false;
			    tooltipText = "";
			    repaint();
			    }
			  */
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
			if (handleEventsForTree((Part) treeRoot.elementAt(i), point)) {
				return (true);
			}
		}
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
			showStatus("MyApplet: Loading image file");
			System.out.println("you hit me!");
			if (t.getUrl() != "") {
				System.out.println("loading " + t.getUrl());
				try {
					appletContext.showDocument(new URL(getCodeBase() + t.getUrl()), "Content");
				}
				catch (Exception ex) {
					ex.printStackTrace();
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


	/**  Standard init method for applets. */
	public void init() {
		appletContext = getAppletContext();

		try {
			jbInit();
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		// which package is the skin package?
		this.skin = super.getParameter("Skin");
		//    skinType = super.getParameter("SkinType");

		// raster dimension uebergeben
		// hier noch hardcoded
		rasterDimension = new Dimension(12, 21);

		// TODO: implement a better exception handling
		try {
			// load the xml file
			URL url = new URL(getDocumentBase(), super.getParameter("XMLFile"));

			HttpURLConnection con = (HttpURLConnection) url.openConnection();

			//con.setRequestMethod( "POST" );
			//con.setUseCaches( false );
			if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {
				this.initTree();
			}
			else {
				//build tree
				TreeBuilder tb = new TreeBuilder();

				tb.init(con.getInputStream(), skin);
				this.treeRoot = tb.getTree();

			}
		}
		catch (Exception e) {
			e.printStackTrace();
			this.initTree();
		}

	}


	/**
	 *  Starts the painting work.
	 *
	 *@param  g  Description of the Parameter
	 */
	public void paint(Graphics g) {
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

		//   System.out.println("repaint " + tooltipX + "," + tooltipY +" bool " + showTooltip);
		if (showTooltip == true) {
			g.setColor(Color.black);
			g.fillRect(tooltipX, tooltipY, 60, 20);
		}
	}


	/**
	 *  Sets the tooltip attribute of the Muminav object
	 *
	 *@param  trigger  The new tooltip value
	 *@param  posx     The new tooltip value
	 *@param  posy     The new tooltip value
	 */
	public void setTooltip(boolean trigger, int posx, int posy) {
		tooltipX = posx;
		tooltipY = posy;
		// nur wenn sich der Zustand ?ndert neu zeichen
		if (trigger == false) {
			showTooltip = false;
			//System.out.println("showTooltip false");
			repaint();
		}
		else {
			showTooltip = true;
			//System.out.println("showTooltip true");
			repaint();
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
		for (int i = 0; i < childs.size(); i++) {
			drawTree(g, (Part) childs.elementAt(i));
		}
		// draw Part
		if (t.drawFirst() == drawFirst) {
			t.fitToRaster(this.getSize(), rasterDimension, startPoint, endPoint).draw(g);
		}
	}


	/**
	 *  Initializes the applet.
	 *
	 *@exception  Exception  Description of the Exception
	 */
	private void jbInit()
		throws Exception {

	}


	/**
	 *  Gets the appletInfo attribute of the Muminav object
	 *
	 *@return    The appletInfo value
	 */
	public String getAppletInfo() {
		return "Applet Information";
	}


	/**
	 *  Gets the parameterInfo attribute of the Muminav object
	 *
	 *@return    The parameterInfo value
	 */
	public String[][] getParameterInfo() {
		return null;
	}


	/**  Description of the Method */
	private void initTree() {
		Hashtable hParams = new Hashtable();
		Object p1 = null;
		Object p2 = null;
		Object p3 = null;
		Object p4 = null;
		Object p5 = null;
		Object p6 = null;
		Object p7 = null;

		// main element theorem
		hParams.put("posX", "5");
		hParams.put("posY", "7");
		hParams.put("width", "5");
		hParams.put("height", "5");
		hParams.put("text", "T");
		hParams.put("textZoom", "Theorem");
		hParams.put("url", "contentM1.html");
		hParams.put("bgColor", " 0, 0, 0 ");
		hParams.put("fontColor", "255, 255, 255 ");
		hParams.put("borderColor", " 255, 0, 0 ");
		hParams.put("lineRelSize", " 5 ");
		hParams.put("fontRelSize", " 70 ");
		try {
			p1 = Class.forName("muminav.skin.math.MainElement").newInstance();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		((Part) p1).init(hParams);
		// add as child to root
		treeRoot.add(p1);

		// subelemnt beweis
		hParams.clear();
		hParams.put("posX", " 8 ");
		hParams.put("posY", " 4 ");
		hParams.put("width", " 3 ");
		hParams.put("height", " 3 ");
		hParams.put("text", new String("B"));
		hParams.put("textZoom", new String("Bew."));
		hParams.put("url", new String("contentM1.html"));
		hParams.put("bgColor", " 0, 0, 0 ");
		hParams.put("fontColor", " 255, 255, 255 ");
		hParams.put("borderColor", " 255, 0, 0 ");
		hParams.put("lineRelSize", " 2 ");
		hParams.put("fontRelSize", " 40 ");
		try {
			p2 = Class.forName("muminav.skin.math.MainElement").newInstance();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		((Part) p2).init(hParams);
		((Part) p1).addChild((Part) p2);

		// subelemnt herleitung
		hParams.clear();
		hParams.put("posX", " 10 ");
		hParams.put("posY", " 2 ");
		hParams.put("width", " 3 ");
		hParams.put("height", " 3 ");
		hParams.put("text", new String("H"));
		hParams.put("textZoom", new String("Herl."));
		hParams.put("url", new String("contentM1.html"));
		hParams.put("bgColor", " 0, 0, 0 ");
		hParams.put("fontColor", " 255, 255, 255 ");
		hParams.put("borderColor", " 255, 0, 0 ");
		hParams.put("lineRelSize", " 2 ");
		hParams.put("fontRelSize", " 40 ");
		try {
			p3 = Class.forName("muminav.skin.math.MainElement").newInstance();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		((Part) p3).init(hParams);
		((Part) p2).addChild((Part) p3);

		// subelemnt bemerkung
		hParams.clear();
		hParams.put("posX", " 2 ");
		hParams.put("posY", " 10 ");
		hParams.put("width", " 3 ");
		hParams.put("height", " 3 ");
		hParams.put("text", new String("B"));
		hParams.put("textZoom", new String("Bem."));
		hParams.put("url", new String("contentM1.html"));
		hParams.put("bgColor", " 0, 0, 0 ");
		hParams.put("fontColor", " 255, 255, 255 ");
		hParams.put("borderColor", " 255, 0, 0 ");
		hParams.put("lineRelSize", " 2 ");
		hParams.put("fontRelSize", " 40 ");
		try {
			p4 = Class.forName("muminav.skin.math.MainElement").newInstance();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		((Part) p4).init(hParams);
		((Part) p1).addChild((Part) p4);

		// subelemnt beispiel
		hParams.clear();
		hParams.put("posX", " 8 ");
		hParams.put("posY", " 10 ");
		hParams.put("width", " 3 ");
		hParams.put("height", " 3 ");
		hParams.put("text", new String("B"));
		hParams.put("textZoom", new String("Beisp."));
		hParams.put("url", new String("contentM1.html"));
		hParams.put("bgColor", " 0, 0, 0 ");
		hParams.put("fontColor", " 255, 255, 255 ");
		hParams.put("borderColor", " 255, 0, 0 ");
		hParams.put("lineRelSize", " 2 ");
		hParams.put("fontRelSize", " 40 ");
		try {
			p5 = Class.forName("muminav.skin.math.MainElement").newInstance();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		((Part) p5).init(hParams);
		((Part) p1).addChild((Part) p5);

		// connector
		hParams.clear();
		hParams.put("startX", " 5 ");
		hParams.put("startY", " 7 ");
		hParams.put("endX", " 6 ");
		hParams.put("endY", " 18 ");
		hParams.put("lineRelSize", " 3 ");
		hParams.put("color", " 0, 0, 255 ");
		try {
			p6 = Class.forName("muminav.skin.math.Connector").newInstance();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		((Part) p6).init(hParams);
		treeRoot.add(p6);

		// main element noname
		hParams.put("posX", " 6 ");
		hParams.put("posY", " 18 ");
		hParams.put("width", " 10 ");
		hParams.put("height", " 3 ");
		hParams.put("text", new String("NN"));
		hParams.put("textZoom", new String("NoName"));
		hParams.put("url", new String("contentM1.html"));
		hParams.put("bgColor", " 0, 0, 0 ");
		hParams.put("fontColor", " 255, 255, 255 ");
		hParams.put("borderColor", " 255, 0, 0 ");
		hParams.put("lineRelSize", " 5 ");
		hParams.put("fontRelSize", " 70 ");
		try {
			p7 = Class.forName("muminav.skin.math.MainElement").newInstance();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		((Part) p7).init(hParams);
		// add as child to root
		treeRoot.add(p7);

		/*
		    hParams.clear();
		    hParams.put("posX", "100));
		    hParams.put("posY", "225));
		    hParams.put("size", "50));
		    hParams.put("text", new String("D"));
		    hParams.put("url", new String("contentM2.html"));
		    try {
		    p2 = Class.forName("muminav.skin.math.MainElement").newInstance();
		    }
		    catch (Exception e) {
		    e.printStackTrace();
		    }
		    ((Part) p2).init(hParams);
		    add as child to p1
		    ((Part) p1).addChild((Part) p2);
		    hParams.clear();
		    hParams.put("posX", "250));
		    hParams.put("posY", "125));
		    hParams.put("size", "50));
		    hParams.put("bgColor", Color.yellow);
		    hParams.put("fontColor", Color.lightGray);
		    hParams.put("borderColor", Color.green);
		    hParams.put("text", new String("L"));
		    try {
		    p3 = Class.forName("muminav.skin.math.MainElement").newInstance();
		    }
		    catch (Exception e) {
		    e.printStackTrace();
		    }
		    ((Part) p3).init(hParams);
		    add as child to p1
		    ((Part) p2).addChild((Part) p3);
		    hParams.clear();
		    hParams.put("posX", "140));
		    hParams.put("posY", "110));
		    hParams.put("pos", "100));
		    hParams.put("h", "50));
		    hParams.put("size", "40));
		    hParams.put("text", new String("S"));
		    hParams.put("url", new String("contentS1.html"));
		    try {
		    p4 = Class.forName("muminav.skin.math.SubElement").newInstance();
		    }
		    catch (Exception e) {
		    e.printStackTrace();
		    }
		    ((Part) p4).init(hParams);
		    add as child to p1
		    ((Part) p2).addChild((Part) p4);
		    hParams.clear();
		    hParams.put("posX", "150));
		    hParams.put("posY", "100));
		    hParams.put("pos", "100));
		    hParams.put("h", "50));
		    hParams.put("size", "40));
		    hParams.put("text", new String("S"));
		    try {
		    p6 = Class.forName("muminav.skin.math.SubElement").newInstance();
		    }
		    catch (Exception e) {
		    e.printStackTrace();
		    }
		    ((Part) p6).init(hParams);
		    add as child to p1
		    ((Part) p4).addChild((Part) p6);
		    hParams.clear();
		    hParams.put("x1", "100));
		    hParams.put("y1", "150));
		    hParams.put("x2", "100));
		    hParams.put("y2", "200));
		    hParams.put("size", "3));
		    hParams.put("color", Color.red);
		    try {
		    p5 = Class.forName("muminav.skin.math." + "Connector").newInstance();
		    }
		    catch (Exception e) {
		    e.printStackTrace();
		    }
		    ((Part) p5).init(hParams);
		    add as child to p1
		    ((Part) p2).addChild((Part) p5);
		  */
	}

}
/*
    $Id: Muminav.java,v 1.10 2002/09/19 03:22:28 zander Exp $
  */

