package muminav.skin.math;

import java.awt.*;
import java.util.Hashtable;
import java.awt.FontMetrics;

import muminav.skin.Part;

/**
 *@author     zander
 *@created    15. September 2002
 *@version    $Revision: 1.8 $
 */
public class MainElement extends Part {

	// colors for backgroud,font and border
	private Color bgColor, fontColor, borderColor;
	// size of the font relative to the height(in percent)
	private int fontRelSize;
	// thickness of the line relative to the mean of width and height(in percent)
	private int lineRelSize;


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

		int border = (dimension.width + dimension.height) / 2 * lineRelSize / 100;
		if (border < 1) {
			border = 1;
		}

		//draw border
		g.setColor(borderColor);
		g.fillRect(new Double((double) center.x - (double) dimension.width / 2).intValue()
				, new Double((double) center.y - (double) dimension.height / 2).intValue()
				, dimension.width, dimension.height);

		//draw background
		g.setColor(bgColor);
		g.fillRect(new Double((double) center.x - (double) dimension.width / 2 + border).intValue()
				, new Double((double) center.y - (double) dimension.height / 2 + border).intValue()
				, dimension.width - 2 * border, dimension.height - 2 * border);

		// draw text
		Font font = g.getFont();
		g.setFont(new Font(font.getFamily(), font.getStyle(), dimension.height * fontRelSize / 100));
		FontMetrics fm = g.getFontMetrics();
		g.setColor(fontColor);
		g.drawString(text, center.x - fm.stringWidth(text) / 2, center.y - fm.getHeight() / 2 + fm.getAscent());
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
		fontRelSize = 50;
		lineRelSize = 2;

		if (hParams.containsKey("url")) {
			url = this.getStringParam(hParams.get("url"));
		}
		if (hParams.containsKey("text")) {
                        System.out.println("text initialized");
			text = this.getStringParam(hParams.get("text"));
		}
		if (hParams.containsKey("posX") && hParams.containsKey("posY")) {
			center = new Point(this.getIntParam(hParams.get("posX"))
					, this.getIntParam(hParams.get("posY")));
		}
		if (hParams.containsKey("height") && hParams.containsKey("width")) {
			dimension = new Dimension(this.getIntParam(hParams.get("width"))
					, this.getIntParam(hParams.get("height")));
		}
		if (hParams.containsKey("bgColor")) {
			bgColor = (this.getColorParam(hParams.get("bgColor")));
		}
		if (hParams.containsKey("fontRelSize")) {
			fontRelSize = this.getIntParam(hParams.get("fontRelSize"));
		}
		if (hParams.containsKey("lineRelSize")) {
			lineRelSize = this.getIntParam(hParams.get("lineRelSize"));
		}
		if (hParams.containsKey("fontColor")) {
			fontColor = (this.getColorParam(hParams.get("fontColor")));
		}
		if (hParams.containsKey("borderColor")) {
			borderColor = (this.getColorParam(hParams.get("borderColor")));
		}
		if (hParams.containsKey("tooltipText")) {
                        System.out.println("tooltiptext initialized");
			tooltipText = (this.getStringParam(hParams.get("tooltipText")));
		}
	}

}

