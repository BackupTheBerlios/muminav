package muminav.skin;

import java.awt.Font;
import java.awt.Point;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.FontMetrics;

/**
 *  This Class contains a set of static methods which drawing geometric figures
 *
 *@author     Joerg
 *@created    17. September 2002
 */
public class DrawLib {

	/**
	 *  This method draws a filled Rectangle with border and text.
	 *  If you want no border, set 0. For no text, set "".
	 *
	 *@param  center       center of the rectangle
	 *@param  backColor    background color
	 *@param  border       boder thickness
	 *@param  borderColor  border color
	 *@param  textHeight   text height
	 *@param  dimension    Description of the Parameter
	 *@param  g            graphic context
	 *@param  text         Description of the Parameter
	 *@param  fontColor    Description of the Parameter
	 */
	public static void drawRectangle(Graphics g, Point center, Dimension dimension, Color backColor, int border, Color borderColor
			, String text, int textHeight, Color fontColor) {

		if (border > 0) {  // draw rectangle with border
			//draw border
			g.setColor(borderColor);
			g.fillRect(new Double((double) center.x - (double) dimension.width / 2).intValue()
					, new Double((double) center.y - (double) dimension.height / 2).intValue()
					, dimension.width, dimension.height);

			//draw background
			g.setColor(backColor);
			g.fillRect(new Double((double) center.x - (double) dimension.width / 2 + border).intValue()
					, new Double((double) center.y - (double) dimension.height / 2 + border).intValue()
					, dimension.width - 2 * border, dimension.height - 2 * border);
		}
		else {  // draw rectangle without border
			g.setColor(backColor);
			g.fillRect(new Double((double) center.x - (double) dimension.width / 2).intValue()
					, new Double((double) center.y - (double) dimension.height / 2).intValue()
					, dimension.width, dimension.height);
		}

		// drawing text
		if (text != null && !text.equals("")) {
			Font font = g.getFont();
			g.setFont(new Font(font.getFamily(), font.getStyle(), textHeight));
			FontMetrics fm = g.getFontMetrics();
			g.setColor(fontColor);
			g.drawString(text, center.x - fm.stringWidth(text) / 2, center.y - fm.getHeight() / 2 + fm.getAscent());
		}
	}


	/**
	 *  this method draws a line from (xo, y0) to (x1, y1) with a given thickness
	 *  by using the Bresenenham algorithm
	 *  to set the color of the line use Graphics.setColor() before you call the method
	 *  note: use for thickness a public int or double value ending with thickness or length,
	 *  so the value will be scaled automaticly
	 *
	 *@param  x0         start x
	 *@param  y0         start y
	 *@param  x1         end x
	 *@param  y1         end y
	 *@param  g          graphic context
	 *@param  thickness  line thickness
	 */
	public static void drawLine(Graphics g, int x0, int y0, int x1, int y1, int thickness) {
		// note: the main part from this method was token from a friend (he did agree to that)
		// he used german names and german comments

		int hilf;
		int negativerAnstieg = 0;
		int groesser45 = 0;

		if (thickness > 0) {

			if (x1 < x0) {
				//x1 links von x0?
				hilf = x0;
				x0 = x1;
				x1 = hilf;
				//tauschen der Punkte
				hilf = y0;
				y0 = y1;
				y1 = hilf;
			}
			int dx = x1 - x0;
			//bestimmen des Abstandes in x-Richtung
			int dy = y1 - y0;
			//                        in y-Richtung
			if (dy < 0) {
				//Anstieg negativ?
				dy = -dy;
				//dy wird geaendert, so dass es positiv ist
				negativerAnstieg = 1;
				//fuer Zeichnen im 5.-8.Oktanten
			}
			if (dy > dx) {
				//im 2.Oktanten?
				groesser45 = 1;
				hilf = dx;
				dx = dy;
				dy = hilf;
				//durch Vertauschung koennen auch Geraden
			}
			//im 2.Oktanten gezeichnet werden
			int entscheidung = 2 * dy - dx;
			//initialisieren der Entscheidungsgroesse
			int x = x0;
			int y = y0;

			// drawing rectangles, because it's much faster than drawing circles and the result isn't worse
			g.fillRect(x - thickness / 2, y - thickness / 2, thickness, thickness);
			//Zeichnen des Anfangswertes
			for (int i = 0; i < dx; i++) {
				x++;
				if (entscheidung > 0) {
					y++;
					//naechster Punkt wird bei x+1,y+1 gesetzt
					entscheidung += 2 * (dy - dx);
				}
				else {
					entscheidung += 2 * dy;
				}
				//naechster Punkt wird bei x+1,y gesetzt
				if ((groesser45 == 0) && (negativerAnstieg == 0)) {
					g.fillRect(x - thickness / 2, y - thickness / 2, thickness, thickness);
				}
				if ((groesser45 == 1) && (negativerAnstieg == 0)) {
					g.fillRect((y - y0) + x0 - thickness / 2, (x - x0) + y0 - thickness / 2, thickness, thickness);
				}
				if ((groesser45 == 0) && (negativerAnstieg == 1)) {
					g.fillRect(x - thickness / 2, y0 - (y - y0) - thickness / 2, thickness, thickness);
				}
				if ((groesser45 == 1) && (negativerAnstieg == 1)) {
					g.fillRect((y - y0) + x0 - thickness / 2, y0 - (x - x0) - thickness / 2, thickness, thickness);
				}
			}
		}
	}

}

