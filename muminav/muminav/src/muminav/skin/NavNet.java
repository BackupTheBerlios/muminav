package muminav.skin;

import java.awt.Graphics;
import java.awt.Dimension;
import java.util.Hashtable;

/**
 *  Description of the Class
 *
 *@author     Joerg
 *@created    24. September 2002
 */
public class NavNet extends Part {
	/**  Description of the Field */
	private Dimension rasterDimension = null;


	/**
	 *  Description of the Method
	 *
	 *@param  g  Description of the Parameter
	 */
	public void draw(Graphics g) {

	}


	/**
	 *  Gets the rasterDimension attribute of the NavNet object
	 *
	 *@return    The rasterDimension value
	 */
	public Dimension getRasterDimension() {
		return rasterDimension;
	}


	/**
	 *  Description of the Method
	 *
	 *@param  hParams  Description of the Parameter
	 */
	public void init(Hashtable hParams) {

		if (hParams.containsKey("height") && hParams.containsKey("width")) {
			rasterDimension = new Dimension(getIntParam(hParams.get("width"))
					, getIntParam(hParams.get("height")));
		}
	}
}

