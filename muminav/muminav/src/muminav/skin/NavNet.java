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
	// contains the package path to the skin
	private String skin;


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
		if (hParams.containsKey("skin")) {
			skin = this.getStringParam(hParams.get("skin"));
		}

		if (hParams.containsKey("url")) {
			url = getStringParam(hParams.get("url"));
		}
	}
}

