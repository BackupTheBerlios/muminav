package muminav.skin;

import java.util.Vector;
import java.awt.*;
import java.util.Hashtable;

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

    /**
     *  text for element
     */
    protected String text;
    /**
     *  text for element (in zoom mode)
     */
    protected String textZoom;

    /**
     *  center/root point for element
     */
    protected Point center;

    /**
     *  dimension of the element; best results if values are odd
     */
    protected Dimension dimension;

    private Vector childs = new Vector();

    /**
     *  Description of the Field
     */
    public String url = "";


    /**
     *  Gets the url attribute of the Part object
     *
     *@return    The url value
     */
    public String getUrl() {
        return ( url );
    }


    /**
     *  Description of the Method
     *
     *@param  g  Description of the Parameter
     */
    public abstract void draw( Graphics g );


    /**
     *  Description of the Method
     *
     *@param  g       Description of the Parameter
     *@param  scale   scale value of zoom; 1 means no zoom
     *@param  center  Description of the Parameter
     */
    public abstract void drawZoomed( Graphics g, Point center, double scale );


    /**
     *  Description of the Method
     *
     *@param  realDim    Description of the Parameter
     *@param  rasterDim  Description of the Parameter
     *@return            Description of the Return Value
     */
    public abstract Part fitToRaster( Dimension realDim, Dimension rasterDim );


    /**
     *  Description of the Method
     *
     *@param  v  Description of the Parameter
     */
    public abstract void init( Hashtable v );


    /**
     *  Gets the inside attribute of the Part object
     *
     *@param  point  Description of the Parameter
     *@return        The inside value
     */
    public abstract boolean isInside( Point point );


    /**
     *  Adds a feature to the Child attribute of the Part object
     *
     *@param  p  The feature to be added to the Child attribute
     *@return    Description of the Return Value
     */
    public int addChild( Part p ) {
        childs.add( p );
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
    public String getStringParam( Object param ) {
        return (String) param;
    }


    /**
     *  Tries to convert the object into a String
     *
     *@param  param  parameter to convert
     *@return        The int value of the parameter
     */
    public int getIntParam( Object param ) {
        return ( new Integer( ( (String) param ).trim() ) ).intValue();
    }


    /**
     *  Tries to convert the object into a String
     *
     *@param  param  parameter to convert
     *@return        the Color
     */
    public Color getColorParam( Object param ) {
        String[] colStrings = ( ( (String) param ).trim() ).split( ",", 3 );

        return new Color( getIntParam( colStrings[0] ), getIntParam( colStrings[1] ), getIntParam( colStrings[2] ) );
    }

}

