package muminav;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

import java.lang.*;

public class TooltipWatchdog extends Thread{

  Muminav parent;

  static int posx;
  static int posy;
  static int lastposx;
  static int lastposy;

  static long lastTime;

  public TooltipWatchdog(Muminav p) {
    parent = p;
  }

  public void run(){
    while(true){

      System.out.println("Bark" +  posx + " !" + "last " + (System.currentTimeMillis() - lastTime));

      // es wurde schon mehr als 2500ms keine neue MousePos übergeben UND
      // bisher kein Tooltip angezeigt
      if( (System.currentTimeMillis() - lastTime) > 2500 && parent.showTooltip == false){
        System.out.println("ToolTip hin");
        parent.setTooltip(true,posx,posy);
      }
      else if( (System.currentTimeMillis() - lastTime) <= 2500 && parent.showTooltip == true){
        System.out.println("ToolTip weg");
        parent.setTooltip(false,posx,posy);
      }

      try {
        sleep(500);
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }
    }

  }

  public static void updatePos(int px, int py){
    lastTime = System.currentTimeMillis();
    // System.out.println("pos in updatepos " + px + "," +py);
    posx = px;
    posy = py;
  }

}

