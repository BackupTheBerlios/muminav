package muminav.skin.math;

/**
 * $Id: SubElement.java,v 1.3 2002/09/12 23:54:19 glaessel Exp $
 */

import java.awt.*;
import java.util.Hashtable;
import java.awt.FontMetrics;

import muminav.skin.Part;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

 /**
  *
  *
  *
  */
public class SubElement extends Part{

  // x,y position of part (center position)
  private int posX,posY;
  // height
  private int size;
  // caption
  private String text;
  // colors for backgroud,font and border
  private Color bgColor,fontColor,borderColor;

 // size of the font relative to the height
  final int fontRelSize = 3;
  // thickness of the line relative to the height
  final int lineRelSize = 40;
  // ratio: width/height
  final int aspectRatio = 1;

  public SubElement(){
    super();
  }

  /**
   * check if coordinates are inside shape of Part
   *
   * @param x x mouse position
   * @param y y mouse position
   * @return true if inside else false
   */
  public boolean isInside(int x, int y){
    System.out.println("check Sub " + x + "," + y + "," + (posX -  size/2 * aspectRatio) + "," + (posY - size/2));
    if(x > posX - size/2 * aspectRatio &&
       y > posY - size/2 &&
       x < posX + size/2 * aspectRatio &&
       y < posY + size/2){
        bgColor = new Color((int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*255));
        return(true);
    }
    return(false);
  }

  public void draw(Graphics g){
    g.setColor(borderColor);
    g.fillRect(posX -  size/2 * aspectRatio,
               posY - size/2,
               size * aspectRatio,
               size);
    g.setColor(bgColor);
    g.fillRect(posX - size/2 * aspectRatio + size/lineRelSize,
               posY - size/2 + size/lineRelSize,
               size * aspectRatio - size/lineRelSize*2,
               size - size/lineRelSize*2);

    Font font = g.getFont();
    g.setFont(new Font(font.getFamily(), font.getStyle(),
                          size/fontRelSize));
    FontMetrics fm = g.getFontMetrics();
    int slen = fm.stringWidth(text);

    g.setColor(fontColor);
    g.drawString(text, posX - slen/2   , posY + fm.getAscent()/2);

  }

  public void init(Hashtable hParams){
    bgColor = Color.lightGray;
    fontColor = Color.white;
    borderColor = Color.black;

    if(hParams.containsKey("url"))
      url = (String) hParams.get("url");
    if(hParams.containsKey("text"))
      text = (String) hParams.get("text");
    if(hParams.containsKey("posX"))
      posX = ((Integer) hParams.get("posX")).intValue();
    if(hParams.containsKey("posY"))
      posY = ((Integer) hParams.get("posY")).intValue();
    if(hParams.containsKey("size"))
      size = ((Integer) hParams.get("size")).intValue();
    if(hParams.containsKey("bgColor"))
      bgColor = ((Color)hParams.get("bgColor"));
    if(hParams.containsKey("fontColor"))
      fontColor = ((Color)hParams.get("fontColor"));
    if(hParams.containsKey("borderColor"))
      borderColor = ((Color)hParams.get("borderColor"));
  }

}

/**
 * $Log: SubElement.java,v $
 * Revision 1.3  2002/09/12 23:54:19  glaessel
 * no message
 *
 * Revision 1.2  2002/09/12 18:44:17  glaessel
 * Part jetzt abstract (Drawable obsolete)
 *
 * Revision 1.1.1.1  2002/09/02 19:50:09  glaessel
 * neu
 *
 * Revision 1.3  2002/07/06 11:09:14  glaessel
 * mit ULR laden
 *
 * Revision 1.2  2002/07/05 23:40:21  glaessel
 * kleine bugfixes
 *
 * Revision 1.1  2002/07/05 23:25:14  glaessel
 * no message
 *
 * Revision 1.2  2002/07/05 19:27:09  glaessel
 * jetzt mit Parts als Baum
 *
 * Revision 1.1  2002/07/03 21:36:02  glaessel
 * no message
 *
 * Revision 1.2  2002/06/24 21:41:53  glaessel
 * CVS-Tags eingesetzt
 *
 *
 */