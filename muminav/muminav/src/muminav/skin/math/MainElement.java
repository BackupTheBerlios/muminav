package muminav.skin.math;

import java.awt.*;
import java.util.Hashtable;
import java.awt.FontMetrics;

import muminav.skin.Part;
import muminav.skin.DrawLib;

/**
 *@author     zander
 *@created    15. September 2002
 *@version    $Revision: 1.10 $
 */
public class MainElement extends Part {

	// colors for backgroud,font and border
	private Color bgColor, fontColor, borderColor;
	// size of the font relative to the height(in percent)
	/**  font height (in raster points) */
	public double fontHeight;
	// thickness of the line relative to the mean of width and height(in percent)
	/**  thickness of the border (in raster points) */
	public double borderThickness;


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

		if (borderThickness < 1) {
			borderThickness = 1;
		}

		DrawLib.drawRectangle(g, center, dimension, bgColor, (int) borderThickness, borderColor, text, (int) fontHeight, fontColor);

		/*
		    draw border
		    g.setColor(borderColor);
		    g.fillRect(new Double((double) center.x - (double) dimension.width / 2).intValue()
		    , new Double((double) center.y - (double) dimension.height / 2).intValue()
		    , dimension.width, dimension.height);
		    draw background
		    g.setColor(bgColor);
		    g.fillRect(new Double((double) center.x - (double) dimension.width / 2 + border).intValue()
		    , new Double((double) center.y - (double) dimension.height / 2 + border).intValue()
		    , dimension.width - 2 * border, dimension.height - 2 * border);
		    draw text
		    Font font = g.getFont();
		    g.setFont(new Font(font.getFamily(), font.getStyle(), dimension.height * fontRelSize / 100));
		    FontMetrics fm = g.getFontMetrics();
		    g.setColor(fontColor);
		    g.drawString(text, center.x - fm.stringWidth(text) / 2, center.y - fm.getHeight() / 2 + fm.getAscent());
		  */
	}


	/**
	 *  Description of the Method
	 *
	 *@param  hParams  Description of the Parameter
	 */
	public void init(Hashtable hParams) {
		bgColor = Color.lightGray;
		fontColor = Color.black;
		borderColor = Color.black;
		fontHeight = 0.5;
		borderThickness = 0.2;

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
	}

}

