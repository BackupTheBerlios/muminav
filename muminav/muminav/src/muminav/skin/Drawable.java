package muminav.skin;

/**
 * $Id: Drawable.java,v 1.2 2002/09/12 18:43:48 glaessel Exp $
 */

import java.awt.*;
import java.util.Hashtable;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public interface Drawable {

  public String getUrl();

  public void draw(Graphics g);

  public void init(Hashtable v);

  public int addChild(Part p);

  public boolean isInside(int x, int y);
}

/**
 * $Log: Drawable.java,v $
 * Revision 1.2  2002/09/12 18:43:48  glaessel
 * Part jetzt abstract (Drawable obsolete)
 *
 * Revision 1.1.1.1  2002/09/02 19:50:08  glaessel
 * neu
 *
 * Revision 1.2  2002/07/06 11:09:45  glaessel
 * mit ULR laden
 *
 * Revision 1.1  2002/07/05 23:28:42  glaessel
 * neues Interface (neuer Name)
 *
 * Revision 1.3  2002/06/24 21:46:01  glaessel
 * CVS-Tags eingesetzt
 *
 *
 *
 */