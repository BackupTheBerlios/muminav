package muminav.skin.math;

import java.awt.*;
import java.util.Hashtable;
import java.awt.FontMetrics;

import muminav.skin.Part;
import muminav.skin.DrawLib;

/**
 *@version    1.0
 */

public class Connector extends Part {

	/**  line start point */
	public Point start;
	/**  line end point */
	public Point end;

	/**  size of the shadow */
	public double shadowSize;

	private Color color;

	/**  line thickness (in raster points */
	public double lineThickness;


	/**  Constructor for the Connector object */
	public Connector() {
		super();
		drawFirst = true;
	}


	/**
	 *  This method draws a connector.
	 *
	 *@param  g  The graphics to paint.
	 */
	public void draw(Graphics g) {
		g.setColor(color);
		DrawLib.drawLine(g, start.x, start.y, end.x, end.y, (int) lineThickness);

	}


	/**
	 *  Description of the Method
	 *
	 *@param  hParams  Description of the Parameter
	 */
	public void init(Hashtable hParams) {
		color = Color.black;
		lineThickness = 0.3;
		shadowSize = 0.5;

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

