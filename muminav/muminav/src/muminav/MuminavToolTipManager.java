package muminav;

import muminav.skin.Part;

import java.util.Vector;
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.MouseInputAdapter;

/**
 *  Description of the Class
 *
 *@author     Joerg
 *@created    17. September 2002
 */
class MuminavToolTipManager extends MouseMotionAdapter implements ActionListener {
	/**  Description of the Field */
	protected Timer m_timer;
	/**  Description of the Field */
	public int m_lastX = -1;
	/**  Description of the Field */
	public int m_lastY = -1;
	/**  Description of the Field */
	protected boolean m_moved = false;
	/**  Description of the Field */
	protected int m_counter = 0;
	/**  Description of the Field */
	public boolean m_visible = false;

	private Part tooltipPart;

	/**  Description of the Field */
	public JToolTip m_toolTip = new JToolTip();

	private MuminavPanel panel;


	/**
	 *  Constructor for the MyToolTipManager object
	 *
	 *@param  parent  Description of the Parameter
	 *@param  panel   Description of the Parameter
	 */
	public MuminavToolTipManager(JApplet parent, MuminavPanel panel) {

		//parent.addMouseMotionListener(this);
		panel.add(m_toolTip);
		panel.addMouseMotionListener(this);
		m_toolTip.setTipText(null);
		//panel.add(m_toolTip);
		m_toolTip.setVisible(false);
		m_timer = new Timer(1000, this);
		m_timer.start();

		m_toolTip.setTipText("TEST");
		m_toolTip.setComponent(panel);
		this.panel = panel;
	}


	/**
	 *  Gets the visible attribute of the MyToolTipManager object
	 *
	 *@return    The visible value
	 */
	public boolean isVisible() {
		return m_visible;
	}

	/**
	 *  Sets the visible attribute of the MyToolTipManager object
	 *
	 *@return    The visible value
	 */
	public void setVisible(boolean b) {
		m_visible = b;
	}

	/**
	 *  Gets the tooltipPart attribute of the MyToolTipManager object
	 *
	 *@return    The tooltipPart value
	 */
	public Part getTooltipPart() {
		return tooltipPart;
	}


	/**
	 *  Description of the Method
	 *
	 *@param  e  Description of the Parameter
	 */
	public void mouseMoved(MouseEvent e) {
		m_moved = true;
		m_counter = -1;
		m_lastX = e.getX();
		m_lastY = e.getY();
		if (m_visible || m_toolTip.isVisible()) {
			m_toolTip.setVisible(false);
			m_visible = false;
			m_toolTip.getParent().repaint();
		}
	}


	/**
	 *  Description of the Method
	 *
	 *@param  e  Description of the Parameter
	 */
	public void actionPerformed(ActionEvent e) {

		if (m_moved || m_counter == 0 || m_toolTip.getTipText() == null) {
			if (m_toolTip.isVisible()) {
				m_toolTip.setVisible(false);
				m_visible = false;
			}
			m_moved = false;
			return;
		}

		if (m_counter < 0) {
			m_counter = 4;
			m_toolTip.setTipText("! " + (Math.random() * 255));
//     m_toolTip.setVisible(true);

			tooltipPart = getTooltipObject(m_lastX, m_lastY);
			if (tooltipPart != null) {

			}

			m_visible = true;
			Dimension d = m_toolTip.getPreferredSize();
			m_toolTip.setBounds(m_lastX, m_lastY + 20, d.width, d.height);
			m_toolTip.getParent().repaint();
		}
		m_counter--;
	}


	/**
	 *  Gets the tooltipObject attribute of the MyToolTipManager object
	 *
	 *@param  x  Description of the Parameter
	 *@param  y  Description of the Parameter
	 *@return    The tooltipObject value
	 */
	private Part getTooltipObject(int x, int y) {

		// Events in each child of the root
		for (int i = 0; i < panel.treeRoot.size(); i++) {
			Part p = getTooltipObjectRecursive((Part) panel.treeRoot.elementAt(i), x, y);
			if (p != null) {
				return (p);
			}
		}
		return (null);
	}


	/**
	 *  Gets the tooltipObjectRecursive attribute of the MyToolTipManager object
	 *
	 *@param  t  Description of the Parameter
	 *@param  x  Description of the Parameter
	 *@param  y  Description of the Parameter
	 *@return    The tooltipObjectRecursive value
	 */
	private Part getTooltipObjectRecursive(Part t, int x, int y) {
		// inside Part?
		if (((Part) t).fitToRaster(panel.getSize(), panel.getRasterDimension(), panel.getStartZoom(), panel.getEndZoom()).isInside(new Point(x, y))) {
			return ((Part) t);
		}
		// get Childs
		Vector childs = ((Part) t).getChilds();
		// Events in each subtree
		for (int i = 0; i < childs.size(); i++) {
			Part p = getTooltipObjectRecursive((Part) childs.elementAt(i), x, y);
			if (p != null) {
				return ((Part) p);
			}
		}
                return null;
	}

}

