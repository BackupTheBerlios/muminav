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

public class Connector extends Part {

    private Point start;
    private Point end;
    private Color color;
    // thickness of the line relative to the mean of width and height(in percent)
    // of the rectangle, where the line is the diagonal
    private int lineRelSize;


    /**
     *  Constructor for the Connector object
     */
    public Connector() {
        super();
        drawFirst = true;
    }


    /**
     *  Description of the Method
     *
     *@param  x0         start x
     *@param  y0         start y
     *@param  x1         end x
     *@param  y1         end y
     *@param  g          graphic context
     *@param  thickness  Description of the Parameter
     */
    public void drawBresenham( Graphics g, int x0, int y0, int x1, int y1, int thickness ) {

        // we work with fillRect instead of fillOval, because java isn't able
        // to draw circles with 2 pixels diameter

        int hilf;
        int negativerAnstieg = 0;
        int groesser45 = 0;

        if ( thickness <= 1 ) {
            // anything we wanna se
            thickness = 1;
        } else {
            // compensation of startpoint for fillOval
            int radius = thickness / 2;
            x0 -= radius;
            y0 -= radius;
            x1 -= radius;
            y1 -= radius;
        }

        if ( x1 < x0 ) {
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
        if ( dy < 0 ) {
            //Anstieg negativ?
            dy = -dy;
            //dy wird geaendert, so dass es positiv ist
            negativerAnstieg = 1;
            //fuer Zeichnen im 5.-8.Oktanten
        }
        if ( dy > dx ) {
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

        g.fillRect( x - thickness / 2, y - thickness / 2, thickness, thickness );
        //Zeichnen des Anfangswertes
        for ( int i = 0; i < dx; i++ ) {
            x++;
            if ( entscheidung > 0 ) {
                y++;
                //naechster Punkt wird bei x+1,y+1 gesetzt
                entscheidung += 2 * ( dy - dx );
            } else {
                entscheidung += 2 * dy;
            }
            //naechster Punkt wird bei x+1,y gesetzt

            if ( ( groesser45 == 0 ) && ( negativerAnstieg == 0 ) ) {
                g.fillRect( x - thickness / 2, y - thickness / 2, thickness, thickness );
            }
            if ( ( groesser45 == 1 ) && ( negativerAnstieg == 0 ) ) {
                g.fillRect( ( y - y0 ) + x0 - thickness / 2, ( x - x0 ) + y0 - thickness / 2, thickness, thickness );
            }
            if ( ( groesser45 == 0 ) && ( negativerAnstieg == 1 ) ) {
                g.fillRect( x - thickness / 2, y0 - ( y - y0 ) - thickness / 2, thickness, thickness );
            }
            if ( ( groesser45 == 1 ) && ( negativerAnstieg == 1 ) ) {
                g.fillRect( ( y - y0 ) + x0 - thickness / 2, y0 - ( x - x0 ) - thickness / 2, thickness, thickness );
            }
        }
    }


    /**
     *  Description of the Method
     *
     *@param  g  Description of the Parameter
     */
    public void draw( Graphics g ) {
        int size = ( java.lang.Math.abs( start.x - end.x ) + java.lang.Math.abs( start.y - end.y ) ) / 2 * lineRelSize / 100;
        g.setColor( color );
        drawBresenham( g, start.x, start.y, end.x, end.y, size );
    }


    /**
     *  Description of the Method
     *
     *@param  g       Description of the Parameter
     *@param  scale   scale value of zoom; 1 means no zoom
     *@param  center  Description of the Parameter
     */
    public void drawZoomed( Graphics g, Point center, double scale ) {
        draw( g );
    }


    /**
     *  Description of the Method
     *
     *@param  realDim    Description of the Parameter
     *@param  rasterDim  Description of the Parameter
     *@return            Description of the Return Value
     */
    public Part fitToRaster( Dimension realDim, Dimension rasterDim ) {
        Connector con = null;
        int scale;
        int xOffset = 0;
        int yOffset = 0;

        try {
            con = (Connector) this.clone();
        } catch ( Exception e ) {
            System.out.println( e );
        }

        if ( (double) realDim.width / (double) realDim.height > (double) rasterDim.width / (double) rasterDim.height ) {
            //roster ist smaller than real area(relative)
            scale = realDim.height / rasterDim.height;
            xOffset = ( realDim.width - rasterDim.width * scale ) / 2;
        } else {
            // roster ist widther than real area(relative)
            scale = realDim.width / rasterDim.width;
            yOffset = ( realDim.height - rasterDim.height * scale ) / 2;
        }

        con.start = new Point( start.x * scale + xOffset, start.y * scale + yOffset );
        con.end = new Point( end.x * scale + xOffset, end.y * scale + yOffset );
        return con;
    }


    /**
     *  Gets the inside attribute of the Connector object
     *
     *@param  point  Description of the Parameter
     *@return        The inside value
     */
    public boolean isInside( Point point ) {
        System.out.println( "check Connector" );

        return ( false );
    }


    /**
     *  Description of the Method
     *
     *@param  hParams  Description of the Parameter
     */
    public void init( Hashtable hParams ) {
        color = Color.black;
        // thickness of line relative to length (in percent)
        lineRelSize = 4;

        if ( hParams.containsKey( "size" ) ) {
            lineRelSize = this.getIntParam( hParams.get( "size" ) );
        }
        if ( hParams.containsKey( "lineRelSize" ) ) {
            lineRelSize = this.getIntParam( hParams.get( "lineRelSize" ) );
        }
        if ( hParams.containsKey( "startX" ) && hParams.containsKey( "startY" ) ) {
            start = new Point( this.getIntParam( hParams.get( "startX" ) )
                    , this.getIntParam( hParams.get( "startY" ) ) );
        }
        if ( hParams.containsKey( "endX" ) && hParams.containsKey( "endY" ) ) {
            end = new Point( this.getIntParam( hParams.get( "endX" ) )
                    , this.getIntParam( hParams.get( "endY" ) ) );
        }
        if ( hParams.containsKey( "color" ) ) {
            color = this.getColorParam( hParams.get( "color" ) );
        }
    }

}

