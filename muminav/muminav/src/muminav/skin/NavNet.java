package muminav.skin;

import java.awt.Graphics;
import java.awt.Dimension;
import java.util.Hashtable;

/**  This class represents the basic attributes for an navigation net */

public class NavNet extends Part {
	/**  raster dimension of the net */
	private Dimension rasterDimension = null;
	// contains the package path to the skin
	private String skin;


	/**
	 *  overwritten nothing doing draw method
	 *
	 *@param  g  graphics to paint
	 */
	public void draw(Graphics g) {

	}


	/**
	 *@return    The rasterDimension value
	 */
	public Dimension getRasterDimension() {
		return rasterDimension;
	}


	/**
	 *  This method initializes the NavNet object with the values contained in the give Hashtable
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

