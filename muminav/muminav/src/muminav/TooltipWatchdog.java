package muminav;

import java.lang.*;

/**
 *  Description of the Class
 *
 *@author     glaessel
 *@created    September 16, 2002
 *@version    $Revision: 1.2 $
 */
public class TooltipWatchdog extends Thread {

    Muminav parent;

    static int posx;
    static int posy;
    static int lastposx;
    static int lastposy;

    static long lastTime;

    boolean stop = false;


    /**
     *  Constructor for the TooltipWatchdog object
     *
     *@param  p  Description of the Parameter
     */
    public TooltipWatchdog( Muminav p ) {
        parent = p;
    }


    /**
     *  Main processing method for the TooltipWatchdog object
     */
    public void run() {
        while ( !this.stop ) {

            System.out.println( "Bark" + posx + " !" + "last " + ( System.currentTimeMillis() - lastTime ) );

            // es wurde schon mehr als 2500ms keine neue MousePos ?bergeben UND
            // bisher kein Tooltip angezeigt
            if ( ( System.currentTimeMillis() - lastTime ) > 2500 && parent.showTooltip == false ) {
                System.out.println( "ToolTip hin" );
                parent.setTooltip( true, posx, posy );
            } else if ( ( System.currentTimeMillis() - lastTime ) <= 2500 && parent.showTooltip == true ) {
                System.out.println( "ToolTip weg" );
                parent.setTooltip( false, posx, posy );
            }

            try {
                sleep( 500 );
            } catch ( Exception ex ) {
                ex.printStackTrace();
            }
        }

    }


    /**
     *  Description of the Method
     *
     *@param  px  Description of the Parameter
     *@param  py  Description of the Parameter
     */
    public static void updatePos( int px, int py ) {
        lastTime = System.currentTimeMillis();
        // System.out.println("pos in updatepos " + px + "," +py);
        posx = px;
        posy = py;
    }


    /**
     *  Description of the Method
     */
    public void hold() {
        this.stop = true;
    }

}

