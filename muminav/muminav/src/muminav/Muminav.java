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
 *@version    $Revision: 1.16 $
 */

public class Muminav extends JApplet {

	// background color
	private final Color bgColor = Color.white;

	boolean showTooltip;

	private AppletContext appletContext;

	// Vector for the child parts of the root
	private Vector treeRoot = new Vector();

	boolean isStandalone = false;

	/**  Description of the Field */
	public MuminavPanel muminavPanel;


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

	}



	/**
	 *  Called if the applet is started or the applet container is reiconized or
	 *  if it gets the focus.
	 */
	public void start() {

	}


	/**
	 *  Called if the applet container is iconized or if it losts the focus.
	 */
	public void stop() {

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

		// TODO: implement a better exception handling
		try {
			// load the xml file

			URL url = new URL(getDocumentBase(), super.getParameter("XMLFile"));
			HttpURLConnection con = (HttpURLConnection) url.openConnection();

			//con.setRequestMethod( "POST" );
			//con.setUseCaches( false );
			if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {
				System.out.println("Can't use xml file.");
				this.initTree();
			}
			else {
				System.out.println("Using xml file.");
				//build tree
				TreeBuilder tb = new TreeBuilder();

				tb.init(con.getInputStream());
				this.treeRoot = tb.getTree();

			}
		}
		catch (Exception e) {
			System.out.println("Can't use xml file.");
			e.printStackTrace();
			this.initTree();
		}

		muminavPanel = new MuminavPanel(treeRoot, this, appletContext);
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(muminavPanel, BorderLayout.CENTER);

		// if there ar elements on the red path, display the navigation buttons
		if (muminavPanel.getNumberOfRedElements() > 0) {
			System.out.println("RedPathInterface found");
			// place the buttons under the MuminavPanel
			muminavPanel = new MuminavPanel(treeRoot, this, appletContext);
			this.getContentPane().setLayout(new BorderLayout());
			this.getContentPane().add(muminavPanel, BorderLayout.CENTER);
			// button panel
			JPanel buttonPanel = new JPanel();
			buttonPanel.setPreferredSize(new Dimension(1, 30));
			buttonPanel.setBackground(bgColor);
			// backward button
			JButton backward = new JButton("back");
			backward.setActionCommand("backward");
			backward.addActionListener(muminavPanel);
			//backward.setBackground(Color.lightGray);
			// forward button
			JButton forward = new JButton("next");
			forward.setActionCommand("forward");
			forward.addActionListener(muminavPanel);
			forward.setBackground(Color.lightGray);
			//spacer panel
			JPanel spacerPanel = new JPanel();
			spacerPanel.setPreferredSize(new Dimension(15, 0));  // the space between the buttons
			buttonPanel.add(backward);
			buttonPanel.add(spacerPanel);
			buttonPanel.add(forward);
			// control panel
			JPanel controlPanel = new JPanel();
			controlPanel.setBackground(bgColor);
			controlPanel.setLayout(new BorderLayout());
			controlPanel.setPreferredSize(new Dimension(0, 40));
			controlPanel.add(buttonPanel, BorderLayout.SOUTH);
			this.getContentPane().add(controlPanel, BorderLayout.SOUTH);
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
		hParams.put("tooltipText", "T-Tooltip juchuh");
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
		hParams.put("tooltipText", "B-Tooltip juchuh");
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
		hParams.put("tooltipText", "H-Tooltip auch juchuh");
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
		hParams.put("tooltipText", "B2-Tooltip juchuh");
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
		hParams.put("tooltipText", "B3-Tooltip juchuh");
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
		hParams.put("tooltipText", "NN-Tooltip juchuh");
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
    $Id: Muminav.java,v 1.16 2002/09/26 03:15:52 zander Exp $
  */

