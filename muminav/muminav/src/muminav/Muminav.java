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
 *  This is the main class where we insert our MuminavPanel
 *
 *@version    $Revision: 1.19 $
 */

public class Muminav extends JApplet {

	// background color
	private final Color bgColor = Color.white;
	// Vector for the child parts of the root
	private Vector treeRoot = new Vector();

	boolean isStandalone = false;

	/**  the MuminalPanel */
	public MuminavPanel muminavPanel;


	/**
	 *  gets the parameter attribute of the Muminav object
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
		super();
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
		// TODO: implement a better exception handling
		try {
			// load the xml file

			URL url = new URL(getDocumentBase(), super.getParameter("XMLFile"));
			HttpURLConnection con = (HttpURLConnection) url.openConnection();

			//con.setRequestMethod( "POST" );
			//con.setUseCaches( false );
			if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {
				System.out.println("Can't establish connection to xml file.");
				//this.initTree();
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
			System.out.println("Can't establish connection to xml file.");
			e.printStackTrace();
			//this.initTree();
		}

		muminavPanel = new MuminavPanel(treeRoot, this);
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(muminavPanel, BorderLayout.CENTER);

		// if there ar elements on the red path, display the navigation buttons
		if (muminavPanel.getNumberOfRedElements() > 0) {
			// place the buttons under the MuminavPanel
			muminavPanel = new MuminavPanel(treeRoot, this);
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

}
/*
    $Id: Muminav.java,v 1.19 2002/09/30 00:05:10 zander Exp $
  */

