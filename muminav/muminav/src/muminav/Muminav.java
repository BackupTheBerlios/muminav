package muminav;

/**
 * $Id: Muminav.java,v 1.4 2002/09/14 17:08:59 glaessel Exp $
 */

import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.util.Hashtable;
import java.util.Vector;
import java.lang.Class;
import javax.swing.event.MouseInputAdapter;
import javax.swing.*;

import muminav.skin.*;
import muminav.skin.math.MainElement;
import java.awt.event.*;
import java.net.URL;
import java.net.MalformedURLException;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class Muminav extends Applet {

  String strSkin;
  String strSkinType;
  Object mySkin;

  String tooltipText;
  boolean showTooltip;
  int tooltipX,tooltipY;

  Thread tooltipWatchdog;


  private AppletContext appletContext;

  // Vector for the child parts of the root
  private Vector treeRoot = new Vector();

  boolean isStandalone = false;

  //Get a parameter value
  public String getParameter(String key, String def) {
    return isStandalone ? System.getProperty(key, def) :
      (getParameter(key) != null ? getParameter(key) : def);
  }

  //Construct the applet
  public Muminav() {

    Panel marquee = new Panel();


    showTooltip = false;
    MyListener myListener = new MyListener();
    MyMotionListener myMotionListener = new MyMotionListener();

    addMouseListener(myListener);
    addMouseMotionListener(myMotionListener);

    tooltipWatchdog = new TooltipWatchdog(this);
    tooltipWatchdog.start();
  }

  class MyListener extends MouseInputAdapter {
    public void mousePressed(MouseEvent e) {
      handleEvents(e.getX(),e.getY());
    }
  }

  class MyMotionListener extends MouseMotionAdapter {
    public void mouseMoved(MouseEvent e){
      TooltipWatchdog.updatePos(e.getX(),e.getY());
      if(showTooltip == true){
         System.out.println("-bewegung");
         showTooltip = false;
         tooltipText = "";
         repaint();
      }
    }
  }

  /**
   * handle Events
   *
   * @param x
   * @param y
   * @return
   */
  private boolean handleEvents(int x, int y){

    // Events in each child of the root
    for(int i = 0 ; i < treeRoot.size(); i++){
      if (handleEventsForTree((Part)treeRoot.elementAt(i), x, y))
        return(true);
    }

    return(false);
  }

  private boolean handleEventsForTree(Part t, int x, int y){
    // clicked inside Part?
    if(((Part)t).isInside(x, y)){
//      repaint();
      showStatus("MyApplet: Loading image file");
      System.out.println("url?");
      if(((Part)t).getUrl()  != ""){
        System.out.println("url!");
        try {
         appletContext.showDocument(new URL(getCodeBase() + ((Part)t).getUrl()) , "Content");
        } catch (Exception ex) { ex.printStackTrace(); }
      }

      return(true);
    }
    // get Childs
    Vector childs = ((Part)t).getChilds();
    // Events in each subtree
    for(int i = 0; i < childs.size(); i++){
      if (handleEventsForTree((Part)childs.elementAt(i), x, y))
        return(true);
    }
    return(false);
  }

  //Initialize the applet
  public void init() {
    appletContext = getAppletContext();

    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }

    // Welches Skin wurde über den Param-Tag übergeben
    //    strSkin = super.getParameter("Skin");
    //    strSkinType = super.getParameter("SkinType");


    // erstelle einen test-Baum
    initTree();
  }

  public void paint(Graphics g){

    // Draw each child of the root
    for(int i = 0 ; i < treeRoot.size(); i++){
      drawTree(g, (Part)treeRoot.elementAt(i) );
    }
 //   System.out.println("repaint " + tooltipX + "," + tooltipY +" bool " + showTooltip);
    if(showTooltip == true){
      g.setColor(Color.black);
      g.fillRect(tooltipX,tooltipY,60,20);
    }
  }

  public void setTooltip(boolean trigger,int posx,int posy){
    tooltipX = posx;
    tooltipY = posy;
    // nur wenn sich der Zustand ändert neu zeichen
    if(trigger == false) {
      showTooltip = false;
      //System.out.println("showTooltip false");
      repaint();
    } else {
      showTooltip = true;
      //System.out.println("showTooltip true");
      repaint();
    }
  }


  /**
   * recursive Method to traverse tree and draw Parts
   *
   * @param g Graphic-Object
   * @param t Subtree to draw
   */
  private void drawTree(Graphics g, Part t){
    // get Childs
    Vector childs = ((Part)t).getChilds();
    // Draw each subtree
    for(int i = 0; i < childs.size(); i++){
      drawTree(g, (Part)childs.elementAt(i));
    }
    // draw Part
    ((Part)t).draw(g);
  }

  //Component initialization
  private void jbInit() throws Exception {

  }


  // Get Applet information
  public String getAppletInfo() {
    return "Applet Information";
  }
  // Get parameter info
  public String[][] getParameterInfo() {
    return null;
  }


  private void initTree(){
    Hashtable hParams = new Hashtable();
    Object p1=null;
    Object p2=null;
    Object p3=null;
    Object p4=null;
    Object p5=null;
    Object p6=null;

    hParams.put("posX",new Integer(100));   hParams.put("posY",new Integer(125));
    hParams.put("size",new Integer(50));
    hParams.put("text",new String("T"));
    hParams.put("url",new String("contentM1.html"));
    try {
      p1 = Class.forName("muminav.skin.math.MainElement").newInstance();
    } catch(Exception e) {
      e.printStackTrace();
    }
    ((Part) p1).init(hParams);
    // add as child to root
    treeRoot.add(p1);

    hParams.clear();
    hParams.put("posX",new Integer(100));   hParams.put("posY",new Integer(225));
    hParams.put("size",new Integer(50));
    hParams.put("text",new String("D"));
    hParams.put("url",new String("contentM2.html"));
    try {
      p2 = Class.forName("muminav.skin.math.MainElement").newInstance();
    } catch(Exception e) {
      e.printStackTrace();
    }
    ((Part) p2).init(hParams);
    // add as child to p1
    ((Part) p1).addChild((Part) p2);

    hParams.clear();
    hParams.put("posX",new Integer(250));   hParams.put("posY",new Integer(125));
    hParams.put("size",new Integer(50));
    hParams.put("bgColor",Color.yellow);
    hParams.put("fontColor",Color.lightGray);
    hParams.put("borderColor",Color.green);
    hParams.put("text",new String("L"));

   try {
      p3 = Class.forName("muminav.skin.math.MainElement").newInstance();
    } catch(Exception e) {
      e.printStackTrace();
    }
    ((Part) p3).init(hParams);
    // add as child to p1
    ((Part) p2).addChild((Part) p3);

    hParams.clear();
    hParams.put("posX",new Integer(140));  hParams.put("posY",new Integer(110));
    hParams.put("pos",new Integer(100));  hParams.put("h",new Integer(50));
    hParams.put("size",new Integer(40));
    hParams.put("text",new String("S"));
    hParams.put("url",new String("contentS1.html"));
    try {
      p4 = Class.forName("muminav.skin.math.SubElement").newInstance();
    } catch(Exception e) {
      e.printStackTrace();
    }
    ((Part) p4).init(hParams);
    // add as child to p1
    ((Part) p2).addChild((Part) p4);

    hParams.clear();
    hParams.put("posX",new Integer(150));  hParams.put("posY",new Integer(100));
    hParams.put("pos",new Integer(100));  hParams.put("h",new Integer(50));
    hParams.put("size",new Integer(40));
    hParams.put("text",new String("S"));
    try {
      p6 = Class.forName("muminav.skin.math.SubElement").newInstance();
    } catch(Exception e) {
      e.printStackTrace();
    }
    ((Part) p6).init(hParams);
    // add as child to p1
    ((Part) p4).addChild((Part) p6);

    hParams.clear();
    hParams.put("x1",new Integer(100));  hParams.put("y1",new Integer(150));
    hParams.put("x2",new Integer(100));  hParams.put("y2",new Integer(200));
    hParams.put("size",new Integer(3));
    hParams.put("color",Color.red);
    try {
      p5 = Class.forName("muminav.skin.math." + "Connector").newInstance();
    } catch(Exception e) {
      e.printStackTrace();
    }
    ((Part) p5).init(hParams);
    // add as child to p1
    ((Part) p2).addChild((Part) p5);
  }

}

/**
 * $Log: Muminav.java,v $
 * Revision 1.4  2002/09/14 17:08:59  glaessel
 * mit Tooltip-Thread
 *
 * Revision 1.3  2002/09/12 23:54:19  glaessel
 * no message
 *
 * Revision 1.2  2002/09/12 18:44:03  glaessel
 * Part jetzt abstract (Drawable obsolete)
 *
 * Revision 1.1.1.1  2002/09/02 19:50:08  glaessel
 * neu
 *
 * Revision 1.8  2002/07/06 11:08:56  glaessel
 * mit ULR laden
 *
 * Revision 1.7  2002/07/05 23:39:58  glaessel
 * kleine bugfixes
 *
 * Revision 1.6  2002/07/05 23:24:03  glaessel
 * mit Mouse_Event Handling
 *
 * Revision 1.5  2002/07/05 19:27:09  glaessel
 * jetzt mit Parts als Baum
 *
 * Revision 1.4  2002/07/02 10:45:00  glaessel
 * no message
 *
 * Revision 1.3  2002/06/24 21:35:51  glaessel
 * no message
 *
 * Revision 1.2  2002/06/24 21:35:31  glaessel
 * no message
 *
 * Revision 1.1  2002/06/24 21:21:48  glaessel
 * first commitment
 *
 */
