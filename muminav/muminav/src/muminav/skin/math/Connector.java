package muminav.skin.math;

/**
 * $Id: Connector.java,v 1.1 2002/09/02 19:50:08 glaessel Exp $
 */

import java.awt.*;
import java.util.Hashtable;
import java.awt.FontMetrics;

import muminav.skin.Part;
import muminav.skin.Drawable;

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
public class Connector extends Part implements Drawable{

  int x1,y1,x2,y2;
  Color color;
  int size;

  public Connector(){
    super();
  }

  public void draw(Graphics g){
    g.setColor(color);
    for(int i=0;i<size;i++)
      g.drawLine(x1-size/2+i,y1,x2-size/2+i,y2);
  }

  public boolean isInside(int x, int y){
    System.out.println("check Connector");

    return(false);
  }

  public void init(Hashtable hParams){
    color = Color.black;
    size = 1;

    if(hParams.containsKey("size")) size = ((Integer) hParams.get("size")).intValue();
    if(hParams.containsKey("x1")) x1 = ((Integer) hParams.get("x1")).intValue();
    if(hParams.containsKey("y1")) y1 = ((Integer) hParams.get("y1")).intValue();
    if(hParams.containsKey("x2")) x2 = ((Integer) hParams.get("x2")).intValue();
    if(hParams.containsKey("y2")) y2 = ((Integer) hParams.get("y2")).intValue();
    if(hParams.containsKey("color")) color = ((Color)hParams.get("color"));
  }

}

/**
 * $Log: Connector.java,v $
 * Revision 1.1  2002/09/02 19:50:08  glaessel
 * Initial revision
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