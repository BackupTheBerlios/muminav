package muminav.skin.math;

import java.awt.*;
import java.util.Hashtable;
import java.awt.FontMetrics;

import muminav.skin.Part;
import muminav.skin.DrawLib;

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

public class Connector extends Part {

	/**  line start point */
	public Point start;
	/**  line end point */
	public Point end;

	private Color color;

	/**  line thickness (in raster points */
	public double lineThickness;


	/**  Constructor for the Connector object */
	public Connector() {
		super();
		drawFirst = true;
	}


	/**
	 *  Description of the Method
	 *
	 *@param  g  Description of the Parameter
	 */
	public void draw(Graphics g) {
		g.setColor(color);
		DrawLib.drawLine(g, start.x, start.y, end.x, end.y, (int) lineThickness);
	}


	/**
	 *  Description of the Method
	 *
	 *@param  g       Description of the Parameter
	 *@param  scale   scale value of zoom; 1 means no zoom
	 *@param  center  Description of the Parameter
	 */
	public void drawZoomed(Graphics g, Point center, double scale) {
		draw(g);
	}



	/**
	 *  Description of the Method
	 *
	 *@param  hParams  Description of the Parameter
	 */
	public void init(Hashtable hParams) {
		color = Color.black;
		lineThickness = 0.02;

		if (hParams.containsKey("lineThickness")) {
			lineThickness = this.getDoubleParam(hParams.get("lineThickness"));
		}
		if (hParams.containsKey("startX") && hParams.containsKey("startY")) {
			start = new Point(this.getIntParam(hParams.get("startX"))
					, this.getIntParam(hParams.get("startY")));
		}
		if (hParams.containsKey("endX") && hParams.containsKey("endY")) {
			end = new Point(this.getIntParam(hParams.get("endX"))
					, this.getIntParam(hParams.get("endY")));
		}
		if (hParams.containsKey("color")) {
			color = this.getColorParam(hParams.get("color"));
		}
	}

}

