package muminav.skin.math;

import java.awt.*;
import java.util.Hashtable;
import java.awt.FontMetrics;

import muminav.skin.Part;

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

/**
 *@author     Joerg
 *@created    15. September 2002
 */
public class SubElement extends Part {

	// height
	private int size;
	// colors for backgroud,font and border
	private Color bgColor, fontColor, borderColor;

	// size of the font relative to the height
	final int fontRelSize = 3;
	// thickness of the line relative to the height
	final int lineRelSize = 40;
	// ratio: width/height
	final int aspectRatio = 1;


	/**  Constructor for the SubElement object */
	public SubElement() {
		super();
	}


	/**
	 *  Description of the Method
	 *
	 *@param  g  Description of the Parameter
	 */
	public void draw(Graphics g) {
		g.setColor(borderColor);
		g.fillRect(center.x - size / 2 * aspectRatio,
				center.y - size / 2,
				size * aspectRatio,
				size);
		g.setColor(bgColor);
		g.fillRect(center.x - size / 2 * aspectRatio + size / lineRelSize,
				center.y - size / 2 + size / lineRelSize,
				size * aspectRatio - size / lineRelSize * 2,
				size - size / lineRelSize * 2);

		Font font = g.getFont();
		g.setFont(new Font(font.getFamily(), font.getStyle(),
				size / fontRelSize));
		FontMetrics fm = g.getFontMetrics();
		int slen = fm.stringWidth(text);

		g.setColor(fontColor);
		g.drawString(text, center.x - slen / 2, center.y + fm.getAscent() / 2);

	}



	/**
	 *  Description of the Method
	 *
	 *@param  hParams  Description of the Parameter
	 */
	public void init(Hashtable hParams) {
		bgColor = Color.lightGray;
		fontColor = Color.white;
		borderColor = Color.black;

		if (hParams.containsKey("url")) {
			url = (String) hParams.get("url");
		}
		if (hParams.containsKey("text")) {
			text = (String) hParams.get("text");
		}
		if (hParams.containsKey("posX") && hParams.containsKey("posY")) {
			center = new Point(this.getIntParam(hParams.get("posX"))
					, this.getIntParam(hParams.get("posY")));
		}
		/*
		    if (hParams.containsKey("posX")) {
		    posX = this.getIntParam(hParams.get("posX"));
		    }
		    if (hParams.containsKey("posY")) {
		    posY = this.getIntParam(hParams.get("posY"));
		    }
		  */
		if (hParams.containsKey("size")) {
			size = this.getIntParam(hParams.get("size"));
		}
		if (hParams.containsKey("bgColor")) {
			bgColor = (this.getColorParam(hParams.get("bgColor")));
		}
		if (hParams.containsKey("fontColor")) {
			fontColor = (this.getColorParam(hParams.get("fontColor")));
		}
		if (hParams.containsKey("borderColor")) {
			borderColor = (this.getColorParam(hParams.get("borderColor")));
		}
	}

}

