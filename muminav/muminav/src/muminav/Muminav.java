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
import java.net.MalformedURLException;

/**
 *  <p>
 *
 *  Title: </p> <p>
 *
 *  Description: </p> <p>
 *
 *  Copyright: Copyright (c) 2002</p> <p>
 *
 *  Company: </p>
 *
 *@author     unascribed
 *@created    15. September 2002
 *@version    1.0
 */

public class Muminav extends Applet {

	String strSkin;
	String strSkinType;
	Object mySkin;

	String tooltipText;
	boolean showTooltip;
	boolean drawFirst = true;
	int tooltipX, tooltipY;

	// dimension of the basis raster
	private Dimension rasterDimension;

	Thread tooltipWatchdog;

	private AppletContext appletContext;

	// Vector for the child parts of the root
	private Vector treeRoot = new Vector();

	boolean isStandalone = false;

	//Get a parameter value
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

		//tooltipWatchdog = new TooltipWatchdog(this);
		//tooltipWatchdog.start();
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
							case 1:  // left button -> load content
								handleEvents(new Point(e.getX(), e.getY()));
								break;
							case 3:  // right button -> zoom in
								break;
			}
		}
	}


	/**
	 *  Description of the Class
	 *
	 *@author     Joerg
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
			    TooltipWatchdog.updatePos(e.getX(), e.getY());
			    if (showTooltip == true) {
			    System.out.println("-bewegung");
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
		if (((Part) t).isInside(point)) {
//      repaint();
			showStatus("MyApplet: Loading image file");
			System.out.println("url?");
			if (((Part) t).getUrl() != "") {
				System.out.println("url!");
				try {
					appletContext.showDocument(new URL(getCodeBase() + ((Part) t).getUrl()), "Content");
				}
				catch (Exception ex) {
					ex.printStackTrace();
				}
			}

			return (true);
		}
		// get Childs
		Vector childs = ((Part) t).getChilds();
		// Events in each subtree
		for (int i = 0; i < childs.size(); i++) {
			if (handleEventsForTree((Part) childs.elementAt(i), point)) {
				return (true);
			}
		}
		return (false);
	}

	//Initialize the applet
	/**  Description of the Method */
	public void init() {
		appletContext = getAppletContext();

		try {
			jbInit();
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		// Welches Skin wurde über den Param-Tag übergeben
		//    strSkin = super.getParameter("Skin");
		//    strSkinType = super.getParameter("SkinType");

		// raster dimension uebergeben
		// hier noch hardcoded
		rasterDimension = new Dimension(12, 22);

		// erstelle einen test-Baum
		initTree();
	}


	/**
	 *  Description of the Method
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
		// nur wenn sich der Zustand ändert neu zeichen
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
			t.fitToRaster(this.getSize(), rasterDimension).draw(g);
		}
	}

	//Component initialization
	/**
	 *  Description of the Method
	 *
	 *@exception  Exception  Description of the Exception
	 */
	private void jbInit()
		throws Exception {

	}


	// Get Applet information
	/**
	 *  Gets the appletInfo attribute of the Muminav object
	 *
	 *@return    The appletInfo value
	 */
	public String getAppletInfo() {
		return "Applet Information";
	}
	// Get parameter info

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
		hParams.put("posX", new Integer(5));
		hParams.put("posY", new Integer(7));
		hParams.put("width", new Integer(5));
		hParams.put("height", new Integer(5));
		hParams.put("text", new String("T"));
		hParams.put("textZoom", new String("Theorem"));
		hParams.put("url", new String("contentM1.html"));
		hParams.put("bgColor", new Color(0, 0, 0));
		hParams.put("fontColor", new Color(255, 255, 255));
		hParams.put("borderColor", new Color(255, 0, 0));
		hParams.put("lineRelSize", new Integer(5));
		hParams.put("fontRelSize", new Integer(70));
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
		hParams.put("posX", new Integer(8));
		hParams.put("posY", new Integer(4));
		hParams.put("width", new Integer(3));
		hParams.put("height", new Integer(3));
		hParams.put("text", new String("B"));
		hParams.put("textZoom", new String("Bew."));
		hParams.put("url", new String("contentM1.html"));
		hParams.put("bgColor", new Color(0, 0, 0));
		hParams.put("fontColor", new Color(255, 255, 255));
		hParams.put("borderColor", new Color(255, 0, 0));
		hParams.put("lineRelSize", new Integer(2));
		hParams.put("fontRelSize", new Integer(40));
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
		hParams.put("posX", new Integer(10));
		hParams.put("posY", new Integer(2));
		hParams.put("width", new Integer(3));
		hParams.put("height", new Integer(3));
		hParams.put("text", new String("H"));
		hParams.put("textZoom", new String("Herl."));
		hParams.put("url", new String("contentM1.html"));
		hParams.put("bgColor", new Color(0, 0, 0));
		hParams.put("fontColor", new Color(255, 255, 255));
		hParams.put("borderColor", new Color(255, 0, 0));
		hParams.put("lineRelSize", new Integer(2));
		hParams.put("fontRelSize", new Integer(40));
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
		hParams.put("posX", new Integer(2));
		hParams.put("posY", new Integer(10));
		hParams.put("width", new Integer(3));
		hParams.put("height", new Integer(3));
		hParams.put("text", new String("B"));
		hParams.put("textZoom", new String("Bem."));
		hParams.put("url", new String("contentM1.html"));
		hParams.put("bgColor", new Color(0, 0, 0));
		hParams.put("fontColor", new Color(255, 255, 255));
		hParams.put("borderColor", new Color(255, 0, 0));
		hParams.put("lineRelSize", new Integer(2));
		hParams.put("fontRelSize", new Integer(40));
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
		hParams.put("posX", new Integer(8));
		hParams.put("posY", new Integer(10));
		hParams.put("width", new Integer(3));
		hParams.put("height", new Integer(3));
		hParams.put("text", new String("B"));
		hParams.put("textZoom", new String("Beisp."));
		hParams.put("url", new String("contentM1.html"));
		hParams.put("bgColor", new Color(0, 0, 0));
		hParams.put("fontColor", new Color(255, 255, 255));
		hParams.put("borderColor", new Color(255, 0, 0));
		hParams.put("lineRelSize", new Integer(2));
		hParams.put("fontRelSize", new Integer(40));
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
		hParams.put("startX", new Integer(5));
		hParams.put("startY", new Integer(7));
		hParams.put("endX", new Integer(6));
		hParams.put("endY", new Integer(18));
		hParams.put("lineRelSize", new Integer(3));
		hParams.put("color", new Color(0, 0, 255));
		try {
			p6 = Class.forName("muminav.skin.math.Connector").newInstance();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		((Part) p6).init(hParams);
		treeRoot.add(p6);

		// main element noname
		hParams.put("posX", new Integer(6));
		hParams.put("posY", new Integer(18));
		hParams.put("width", new Integer(10));
		hParams.put("height", new Integer(3));
		hParams.put("text", new String("NN"));
		hParams.put("textZoom", new String("NoName"));
		hParams.put("url", new String("contentM1.html"));
		hParams.put("bgColor", new Color(0, 0, 0));
		hParams.put("fontColor", new Color(255, 255, 255));
		hParams.put("borderColor", new Color(255, 0, 0));
		hParams.put("lineRelSize", new Integer(5));
		hParams.put("fontRelSize", new Integer(70));
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
		    hParams.put("posX", new Integer(100));
		    hParams.put("posY", new Integer(225));
		    hParams.put("size", new Integer(50));
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
		    hParams.put("posX", new Integer(250));
		    hParams.put("posY", new Integer(125));
		    hParams.put("size", new Integer(50));
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
		    hParams.put("posX", new Integer(140));
		    hParams.put("posY", new Integer(110));
		    hParams.put("pos", new Integer(100));
		    hParams.put("h", new Integer(50));
		    hParams.put("size", new Integer(40));
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
		    hParams.put("posX", new Integer(150));
		    hParams.put("posY", new Integer(100));
		    hParams.put("pos", new Integer(100));
		    hParams.put("h", new Integer(50));
		    hParams.put("size", new Integer(40));
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
		    hParams.put("x1", new Integer(100));
		    hParams.put("y1", new Integer(150));
		    hParams.put("x2", new Integer(100));
		    hParams.put("y2", new Integer(200));
		    hParams.put("size", new Integer(3));
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


