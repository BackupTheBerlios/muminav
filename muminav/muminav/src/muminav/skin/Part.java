package muminav.skin;

import java.awt.*;
import java.util.Vector;
import java.util.Hashtable;
import java.lang.reflect.Field;

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

public abstract class Part implements Cloneable {

	/**  Description of the Field */
	protected String tooltipText = null;
	/**
	 *  set true, if this element have to draw at first (for example connectors)
	 */
	protected boolean drawFirst = false;

	/**  text for element */
	protected String text = "";

	/**  center/root point for element */
	protected Point center = null;

	/**  dimension of the element */
	protected Dimension dimension = null;

	private Vector childs = new Vector();

	/**  Description of the Field */
	public String url = "";


	/**
	 *  Gets the url attribute of the Part object
	 *
	 *@return    The url value
	 */
	public String getUrl() {
		return (url);
	}


	/**
	 *  Description of the Method
	 *
	 *@param  g  Description of the Parameter
	 */
	public abstract void draw(Graphics g);


	/**
	 *  Description of the Method
	 *
	 *@param  point    the point to scale
	 *@param  scale    scale value
	 *@param  xOffset  x offset
	 *@param  yOffset  y offset
	 *@return          scaled point
	 */
	public static Point scalePoint(Point point, double scale, int xOffset, int yOffset) {
		return new Point(new Double((double) point.x * scale).intValue() + xOffset
				, new Double((double) point.y * scale).intValue() + yOffset);
	}



	/**
	 *  Description of the Method
	 *
	 *@param  scale      scale value
	 *@param  dimension  dimension to scale
	 *@return            Description of the Return Value
	 */
	public static Dimension scaleDimension(Dimension dimension, double scale) {
		return new Dimension(new Double((double) dimension.width * scale).intValue()
				, new Double((double) dimension.height * scale).intValue());
	}


	/**
	 *  Description of the Method
	 *
	 *@param  realDim     Description of the Parameter
	 *@param  endPoint    Description of the Parameter
	 *@param  rasterDim   Description of the Parameter
	 *@param  startPoint  Description of the Parameter
	 *@return             Description of the Return Value
	 */
	public Part fitToRaster(Dimension realDim, Dimension rasterDim, Point startPoint, Point endPoint) {
		Dimension zoomDim;
		Point topLeftZoom;
		boolean zoom;
		double scaleValueResize;
		double scaleValue;
		int xOffset = 0;
		int yOffset = 0;

		// if zoom points existing, we switch to zoom mode
		zoom = startPoint != null && endPoint != null;

		// cloned object with scaled points and dimensions
		Part part = null;

		if (zoom) {
			zoomDim = new Dimension(Math.abs(startPoint.x - endPoint.x)
					, Math.abs(startPoint.y - endPoint.y));
			// calculate the top left point from zoom window
			topLeftZoom = new Point();
			if (startPoint.x < endPoint.x) {
				topLeftZoom.x = startPoint.x;
			}
			else {
				topLeftZoom.x = endPoint.x;
			}
			if (startPoint.y < endPoint.y) {
				topLeftZoom.y = startPoint.y;
			}
			else {
				topLeftZoom.y = endPoint.y;
			}

			if ((double) realDim.width / (double) realDim.height > (double) zoomDim.width / (double) zoomDim.height) {
				// zoom window is higher then real window (ratio)
				scaleValueResize = (double) realDim.height / (double) zoomDim.height;
				realDim.width = new Double(realDim.width * scaleValueResize).intValue();
				realDim.height = new Double(realDim.height * scaleValueResize).intValue();

				// center zoom window
				xOffset -= new Double(((double) topLeftZoom.x) * scaleValueResize).intValue();
				xOffset += new Double(((double) realDim.width / scaleValueResize - (double) zoomDim.width * scaleValueResize) / 2).intValue();
				yOffset -= new Double(topLeftZoom.y * scaleValueResize).intValue();
			}
			else {
				// zoom window is widther then real window (ratio)
				scaleValueResize = (double) realDim.width / (double) zoomDim.width;
				realDim.width = new Double(realDim.width * scaleValueResize).intValue();
				realDim.height = new Double(realDim.height * scaleValueResize).intValue();

				// center zoom window
				yOffset -= new Double(((double) topLeftZoom.y) * scaleValueResize).intValue();
				yOffset += new Double(((double) realDim.height / scaleValueResize - (double) zoomDim.height * scaleValueResize) / 2).intValue();
				xOffset -= new Double(topLeftZoom.x * scaleValueResize).intValue();
			}
			if ((double) realDim.width / (double) realDim.height > (double) rasterDim.width / (double) rasterDim.height) {
				//raster ist smaller than real area (ratio)
				scaleValue = (double) realDim.height / (double) rasterDim.height;
				// center raster
				xOffset += new Double(((double) realDim.width - (double) rasterDim.width * scaleValue) / 2).intValue();
			}
			else {
				//raster ist widther than real area (ratio)
				scaleValue = (double) realDim.width / (double) rasterDim.width;
				// center raster
				yOffset += new Double(((double) realDim.height - (double) rasterDim.height * scaleValue) / 2).intValue();
			}
		}
		else {

			if ((double) realDim.width / (double) realDim.height > (double) rasterDim.width / (double) rasterDim.height) {
				//raster ist smaller than real area (ratio)
				scaleValue = (double) realDim.height / (double) rasterDim.height;
				xOffset = new Double(((double) realDim.width - (double) rasterDim.width * scaleValue) / 2).intValue();
			}
			else {
				//raster ist widther than real area (ratio)
				scaleValue = (double) realDim.width / (double) rasterDim.width;
				yOffset = new Double(((double) realDim.height - (double) rasterDim.height * scaleValue) / 2).intValue();
			}
		}
		try {
			// cloning this object to keep the original values
			part = (Part) this.clone();

			// scaling center point and dimension
			if (center != null) {
				part.center = scalePoint(center, scaleValue, xOffset, yOffset);
			}
			if (dimension != null) {
				part.dimension = scaleDimension(dimension, scaleValue);
			}

			// also all public points and dimensions in objects who inherit
			// from this class will be scaled by using reflection
			Class partClass = part.getClass();
			Field[] publicFields = partClass.getFields();

			for (int i = 0; i < publicFields.length; i++) {
				String fieldName = publicFields[i].getName();
				Class typeClass = publicFields[i].getType();
				String fieldType = typeClass.getName();
				Object object = new Object();

				try {
					object = typeClass.newInstance();
				}
				catch (InstantiationException iE) {
					// checking for public int aund double values
					if (fieldName.toLowerCase().endsWith("length") || fieldName.toLowerCase().endsWith("thickness")
							 || fieldName.toLowerCase().endsWith("width") || fieldName.toLowerCase().endsWith("height")) {

						if (fieldType.equals("int")) {
							Field field = part.getClass().getField(fieldName);
							field.set(part, new Integer(((Integer) publicFields[i].get(this)).intValue() * (int) scaleValue));
						}
						if (fieldType.equals("double")) {
							Field field = part.getClass().getField(fieldName);
							field.set(part, new Double(((Double) publicFields[i].get(this)).doubleValue() * scaleValue));
						}
					}
				}

				if (object instanceof Dimension) {
					Field field = part.getClass().getField(fieldName);
					field.set(part, scaleDimension((Dimension) publicFields[i].get(this), scaleValue));
				}
				if (object instanceof Point) {
					Field field = part.getClass().getField(fieldName);
					field.set(part, scalePoint((Point) publicFields[i].get(this), scaleValue, xOffset, yOffset));
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return part;
	}


	/**
	 *  Gets the inside attribute of the Part object
	 *
	 *@param  point  Description of the Parameter
	 *@return        The inside value
	 */
	public boolean isInside(Point point) {
		if (center == null || dimension == null) {
			// no calculation possible
			return false;
		}

		if (point.x >= center.x - dimension.width / 2 &&
				point.y >= center.y - dimension.height / 2 &&
				point.x <= center.x + dimension.width / 2 &&
				point.y <= center.y + dimension.height / 2) {
			return (true);
		}
		return (false);
	}


	/**
	 *  Description of the Method
	 *
	 *@param  v  Description of the Parameter
	 */
	public abstract void init(Hashtable v);


	/**
	 *  Adds a feature to the Child attribute of the Part object
	 *
	 *@param  p  The feature to be added to the Child attribute
	 *@return    Description of the Return Value
	 */
	public int addChild(Part p) {
		childs.add(p);
		return childs.size();
	}


	/**
	 *  Gets the childs attribute of the Part object
	 *
	 *@return    The childs value
	 */
	public Vector getChilds() {
		return childs;
	}


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	public int size() {
		return childs.size();
	}


	/**
	 *  Gets the center attribute of the Part object
	 *
	 *@return    returns the center point
	 */
	public Point getCenter() {
		return center;
	}


	/**
	 *  Description of the Method
	 *
	 *@return    returns the drawFirst value
	 */
	public boolean drawFirst() {
		return drawFirst;
	}


	/**
	 *  Tries to convert the object into a String
	 *
	 *@param  param  parameter to convert
	 *@return        the String
	 */
	public String getStringParam(Object param) {
		return (String) param;
	}


	/**
	 *  Tries to convert the object into a int
	 *
	 *@param  param  parameter to convert
	 *@return        The int value of the parameter
	 */
	public int getIntParam(Object param) {
		return (new Integer(((String) param).trim())).intValue();
	}


	/**
	 *  Tries to convert the object into a String
	 *
	 *@param  param  parameter to convert
	 *@return        The int value of the parameter
	 */
	public double getDoubleParam(Object param) {
		return (new Double(((String) param).trim())).doubleValue();
	}


	/**
	 *  Tries to convert the object into a Color
	 *
	 *@param  param  parameter to convert
	 *@return        the Color
	 */
	public Color getColorParam(Object param) {
		String[] colStrings = (((String) param).trim()).split(",", 3);

		return new Color(getIntParam(colStrings[0]), getIntParam(colStrings[1]), getIntParam(colStrings[2]));
	}


	/**
	 *  Gets the tooltipText attribute of the Part object
	 *
	 *@return    The tooltipText value
	 */
	public String getTooltipText() {
		return tooltipText;
	}


	/**
	 *  Description of the Method
	 *
	 *@param  g       Description of the Parameter
	 *@param  x       Description of the Parameter
	 *@param  y       Description of the Parameter
	 *@param  ttText  Description of the Parameter
	 */
	public void drawTooltip(Graphics g, int x, int y, String ttText) {
		Font font = g.getFont();
		g.setFont(new Font(font.getFamily(), font.getStyle(), 10));
		FontMetrics fm = g.getFontMetrics();
		int slen = fm.stringWidth(ttText);
		int sheight = fm.getHeight();

		g.setColor(new Color(0, 0, 127));
		g.fillRect(x, y + 20, slen + 20, sheight + 2);
		g.setColor(new Color(150, 150, 255));
		g.drawRect(x, y + 20, slen + 20, sheight + 2);
		g.setColor(Color.white);
		g.drawString(ttText, x + 10, y + 20 + 10 + 2);
	}

}

