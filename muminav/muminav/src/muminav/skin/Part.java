package muminav.skin;

import java.awt.*;
import java.util.Vector;
import java.util.Hashtable;
import java.lang.reflect.Field;

/**
 * This class is the basic class for all skin elements/parts. If you want to write your own skin element/part, you
 * only need to inherit from this class. This way you get some basic features and a structure anderstand by the
 * MuminavPanel.
 *
 *@version    1.0
 */

public abstract class Part implements Cloneable {

	/**  text shown in the tooltip */
	protected String tooltipText = null;
	/** set true, if this element have to draw at first (for example connectors) */
	protected boolean drawFirst = false;
	/**  true if this is the last klicked element */
	protected boolean isActive = false;
	/**  text for element */
	protected String text = "";
	/**  center/root point for element */
	protected Point center = null;
	/**  dimension of the element */
	protected Dimension dimension = null;
	/**  position on red path */
	protected int posRedPath = -1;
	// the children
	private Vector childs = new Vector();
	/**  this url will be loaded if the element/part will be klicked */
	public String url = "";


	/**
	 *  Sets the active attribute of the Part object
	 *
	 *@param  isActive  the new active value
	 */
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}


	/**
	 *  Gets the url attribute of the Part object
	 *
	 *@return    The url value
	 */
	public String getUrl() {
		return (url);
	}


	/**
	 *  This method draws the part/element itself.
	 *
	 *@param  g  The Graphics to paint.
	 */
	public abstract void draw(Graphics g);


	/**
	 *  This method scales and diplace a Point by the given values.
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
	 *  This method scales and diplace a Dimension by the given values.
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
	 *  This Method does the main scaling work.
	 *  Points and Dimensions are measured in raster points. Here they get there real pixel position.
	 *  The Method needs the dimension of the applet, the dimension of the raster and the start and end
	 *  point of the zoom window. Based on this values points and dimensions special Points and Dimensions
	 *  will be scaled and displaced. The method automaticly calculates the new values for the center point
	 *  and the dimension of a part. If you use/need other points, dimensions or length or so, you only need
	 *  to declare them public. They will also be scaled by using Reflection. For more details read our manual.
	 *
	 *@param  realDim     The dimension of the panel (in pixels)
	 *@param  rasterDim   The dimension of the raster (in raster points)
	 *@param  startPoint  The start point of the zoom window.
	 *@param  endPoint    The end point of the zoom window.
	 *@return             A scaled and displaced clone of the part in pixel coordinates.
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

			// offsets will be calculated
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
							 || fieldName.toLowerCase().endsWith("width") || fieldName.toLowerCase().endsWith("height")
							 || fieldName.toLowerCase().endsWith("size")) {

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
	 *  This method checks if a given point is inside a given rectangle dicribed by dimension and center.
	 *
	 *@param  point      The point to check.
	 *@param  dimension  The Dimension of the rectangle
	 *@param  center     The center of the rectangle.
	 *@return            Returns true if the point is inside.
	 */
	public boolean isInside(Point point, Point center, Dimension dimension) {
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
	 *  This method assumes the part as rectangle by using startPoint and dimension and checks
	 *  if the given point is inside. If your part inn't a rectangle, overwrite this method.
	 *
	 *@param  point  The point to check.
	 *@return        Returns true if the point is inside.
	 */
	public boolean isInside(Point point) {
		return isInside(point, center, dimension);
	}


	/**
	 *  This method initializes the object with the values from the given Hashtable
	 *
	 *@param  v  The Hashtable which contains the values for the part.
	 */
	public abstract void init(Hashtable v);


	/**
	 *  Adds a feature to the child attribute of the part object
	 *
	 *@param  p  The feature to be added to the Child attribute
	 *@return    number of childs
	 */
	public int addChild(Part p) {
		childs.add(p);
		return childs.size();
	}


	/**
	 *  Gets the childs attribute of the Part object
	 *
	 *@return    the childs vector
	 */
	public Vector getChilds() {
		return childs;
	}


	/**
	 *  This method returns the number of childs
	 *
	 *@return    number of childs
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
	 *  This method returns the drawFirst value
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
	 *  Tries to convert the object into a Double
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
	 *  Gets the posRedPath attribute of the Part object
	 *
	 *@return    The posRedPath value
	 */
	public int getPosRedPath() {
		return posRedPath;
	}


	/**
	 *  This method draws a tooltip an a specified position
	 *
	 *@param  g       The graphics to paint.
	 *@param  x       The x coordinate of the tooltip.
	 *@param  y       The y coordinate of the tooltip.
	 *@param  ttText  The text written into the tooltip.
	 */
	public void drawTooltip(Graphics g, int x, int y, String ttText) {
		final Color backColor = new Color(0, 0, 127);
		final Color textColor = Color.white;
		final Color borderColor = new Color(150, 150, 255);

		final int voffset = 20;

		final int addWidth = 20;

		final int addHeight = 2;

		final int fontSize = 12;

		Font font = g.getFont();
		g.setFont(new Font(font.getFamily(), font.getStyle(), fontSize));
		FontMetrics fm = g.getFontMetrics();
		int slen = fm.stringWidth(ttText);
		int sheight = fm.getHeight();

		// reaches the tooltip over the right border?
		if ((x + slen + addWidth) > g.getClipBounds().getWidth()) {
			x = x - (x + slen + addWidth) + (int) g.getClipBounds().getWidth();
		}

		// reaches the tooltip over the bottom border
		if ((y + voffset + sheight + addHeight) > (int) g.getClipBounds().getHeight()) {
			// y =  ((int) g.getClipBounds().getHeight()) - addHeight - sheight - voffset;
			// voffset = voffset * (-1);
			y = y - 2 * voffset;
		}

		g.setColor(backColor);
		g.fillRect(x, y + voffset, slen + addWidth, sheight + addHeight);
		g.setColor(borderColor);
		g.drawRect(x, y + voffset, slen + addWidth, sheight + addHeight);
		g.setColor(textColor);
		g.drawString(ttText, x + addWidth / 2, y + voffset + fontSize + addHeight);
	}

}

