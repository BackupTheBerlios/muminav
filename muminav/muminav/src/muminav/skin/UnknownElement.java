package muminav.skin;

import java.awt.*;
import java.util.Hashtable;
import java.awt.FontMetrics;

import muminav.skin.Part;
import muminav.skin.DrawLib;

/**
 *  Used if the xml file contains an unknown element and for the root element.
 *
 *@author     ercmat
 *@created    21. September 2002
 *@version    $Revision: 1.1 $
 */
public class UnknownElement extends Part {

    /**
     *  Constructor for the UnknownElement object
     */
    public UnknownElement() {
        super();
    }


    /**
     *  Description of the Method
     *
     *@param  g  Description of the Parameter
     */
    public void draw( Graphics g ) {

    }


    /**
     *  Initialises parameters of this element with values from Hashtable.
     *
     *@param  hParams  Contains the parameter values.
     */
    public void init( Hashtable hParams ) {

    }

}

