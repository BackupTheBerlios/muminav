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

	/**
	 *  set true, if this element have to draw at first (for example connectors)
	 */
	protected boolean drawFirst = false;

	/**  text for element */
	protected String text;
	/**  text for element (in zoom mode) */
	protected String textZoom;

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
	 *@param  g       Description of the Parameter
	 *@param  scale   scale value of zoom; 1 means no zoom
	 *@param  center  Description of the Parameter
	 */
	public abstract void drawZoomed(Graphics g, Point center, double scale);


	/**
	 *  Description of the Method
	 *
	 *@param  point    the point to scale
	 *@param  scale    scale value
	 *@param  xOffset  x offset
	 *@param  yOffset  y offset
	 *@return          scaled point
	 */
	public static Point scalePoint(Point point, int scale, int xOffset, int yOffset) {
		return new Point(point.x * scale + xOffset, point.y * scale + yOffset);
	}



	/**
	 *  Description of the Method
	 *
	 *@param  scale      scale value
	 *@param  dimension  dimension to scale
	 *@return            Description of the Return Value
	 */
	public static Dimension scaleDimension(Dimension dimension, int scale) {
		return new Dimension(dimension.width * scale, dimension.height * scale);
	}


	/**
	 *  center point and dimension of each element will be scaled; additional each
	 *  public point and dimension will be scaled; additional each int value its
	 *  name ends with length (case-insensitive) will be scaled; not only in this
	 *  class, in all classes who inherit from this one
	 *
	 *@param  realDim    dimension of the target raster
	 *@param  rasterDim  dimension of the source raster
	 *@return            scaled clone of the object
	 */
	public Part fitToRaster(Dimension realDim, Dimension rasterDim) {
		int scaleValue;
		int xOffset = 0;
		int yOffset = 0;

		// cloned object with scaled points and dimensions
		Part part = null;

		if ((double) realDim.width / (double) realDim.height > (double) rasterDim.width / (double) rasterDim.height) {
			//raster ist smaller than real area(relative)
			scaleValue = realDim.height / rasterDim.height;
			xOffset = (realDim.width - rasterDim.width * scaleValue) / 2;
		}
		else {
			//raster ist widther than real area(relative)
			scaleValue = realDim.width / rasterDim.width;
			yOffset = (realDim.height - rasterDim.height * scaleValue) / 2;
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
					if (fieldName.toLowerCase().endsWith("length")) {
						//System.out.println(fieldType);
						Field field = part.getClass().getField(fieldName);
						field.set(part, new Integer(((Integer) publicFields[i].get(this)).intValue() * scaleValue));
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
				point.y >= center.y - dimension.width / 2 &&
				point.x <= center.x + dimension.width / 2 &&
				point.y <= center.y + dimension.width / 2) {
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
	 *  Tries to convert the object into a String
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
	 *@return        the Color
	 */
	public Color getColorParam(Object param) {
		String[] colStrings = (((String) param).trim()).split(",", 3);

		return new Color(getIntParam(colStrings[0]), getIntParam(colStrings[1]), getIntParam(colStrings[2]));
	}

}

