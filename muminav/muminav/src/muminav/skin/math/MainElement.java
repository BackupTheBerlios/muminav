package muminav.skin.math;

import java.awt.*;
import java.util.Hashtable;
import java.awt.FontMetrics;

import muminav.skin.Part;
import muminav.skin.DrawLib;

/**
 *@author     zander
 *@created    15. September 2002
 *@version    $Revision: 1.15 $
 */
public class MainElement extends Part {

	// colors for backgroud,font and border
	private Color bgColor, fontColor, borderColor;
	/**  font height (in raster points) */
	public double fontHeight;
	/**  thickness of the border (in raster points) */
	public double borderThickness;
	/**  Description of the Field */
	public double shadowSize;


	/**  Constructor for the MainElement object */
	public MainElement() {
		super();
	}


	/**
	 *  Description of the Method
	 *
	 *@param  g  Description of the Parameter
	 */
	public void draw(Graphics g) {
		// shadow
		DrawLib.drawRectangle(g, new Point(center.x - (int) shadowSize, center.y + (int) shadowSize)
				, dimension, new Color(110, 110, 110, 128), 0, null, "", 0, null);

		if (isActive) {
			// front
			if (fontHeight > 10) {
				DrawLib.drawRectangle(g, center, dimension, new Color(255, 255, 150), (int) borderThickness, borderColor
						, text, (int) fontHeight, fontColor);
			}
			else {
				DrawLib.drawRectangle(g, center, dimension, new Color(255, 255, 150), (int) borderThickness, borderColor
						, null, (int) fontHeight, fontColor);
			}
		}
		else {
			// front
			if (fontHeight > 10) {
				// only if the text is high enough, it will be displayed
				DrawLib.drawRectangle(g, center, dimension, bgColor, (int) borderThickness, borderColor, text, (int) fontHeight, fontColor);
			}
			else {
				DrawLib.drawRectangle(g, center, dimension, bgColor, (int) borderThickness, borderColor, null, (int) fontHeight, fontColor);
			}
		}

	}


	/**
	 *  Initialises parameters of this element with values from Hashtable.
	 *
	 *@param  hParams  Contains the parameter values.
	 */
	public void init(Hashtable hParams) {
		bgColor = Color.lightGray;
		fontColor = Color.black;
		borderColor = Color.black;
		fontHeight = 0.5;
		borderThickness = 0.2;
		shadowSize = 0.5;

		if (hParams.containsKey("url")) {
			url = getStringParam(hParams.get("url"));
		}
		if (hParams.containsKey("text")) {
			text = getStringParam(hParams.get("text"));
		}
		if (hParams.containsKey("posX") && hParams.containsKey("posY")) {
			center = new Point(getIntParam(hParams.get("posX"))
					, getIntParam(hParams.get("posY")));
		}
		if (hParams.containsKey("height") && hParams.containsKey("width")) {
			dimension = new Dimension(getIntParam(hParams.get("width"))
					, getIntParam(hParams.get("height")));
		}
		if (hParams.containsKey("bgColor")) {
			bgColor = getColorParam(hParams.get("bgColor"));
		}
		if (hParams.containsKey("fontHeight")) {
			fontHeight = getDoubleParam(hParams.get("fontHeight"));
		}
		if (hParams.containsKey("borderThickness")) {
			borderThickness = getDoubleParam(hParams.get("borderThickness"));
		}
		if (hParams.containsKey("fontColor")) {
			fontColor = getColorParam(hParams.get("fontColor"));
		}
		if (hParams.containsKey("borderColor")) {
			borderColor = this.getColorParam(hParams.get("borderColor"));
		}
		if (hParams.containsKey("tooltipText")) {
			tooltipText = this.getStringParam(hParams.get("tooltipText"));
		}
		if (hParams.containsKey("posRedPath")) {
			posRedPath = this.getIntParam(hParams.get("posRedPath"));
		}
	}

}

