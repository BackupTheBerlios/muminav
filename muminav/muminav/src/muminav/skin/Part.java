package muminav.skin;

import java.util.Vector;
import muminav.skin.Drawable;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class Part {

  private Vector childs = new Vector();

  public String url="";

  public String getUrl(){
    return(url);
  }

  public int addChild(Part p){
    childs.add(p);
    return childs.size();
  }

  public Vector getChilds(){
    return childs;
  }

  public int size(){
    return childs.size();
  }

}