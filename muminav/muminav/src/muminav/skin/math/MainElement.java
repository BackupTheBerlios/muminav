package muminav.skin.math;

import java.awt.*;
import java.util.Hashtable;
import java.awt.FontMetrics;

import muminav.skin.Part;

/**
 *@author     zander
 *@created    15. September 2002
 *@version    $Revision: 1.5 $
 */
public class MainElement extends Part {

    // colors for backgroud,font and border
    private Color bgColor, fontColor, borderColor;
    // size of the font relative to the height(in percent)
    int fontRelSize;
    // thickness of the line relative to the mean of width and height(in percent)
    int lineRelSize;


    /**
     *  Constructor for the MainElement object
     */
    public MainElement() {
        super();
    }


    /**
     *  Description of the Method
     *
     *@param  g  Description of the Parameter
     */
    public void draw( Graphics g ) {
        int border = ( dimension.width + dimension.height ) / 2 * lineRelSize / 100;
        if ( border < 1 ) {
            border = 1;
        }
        //draw border
        g.setColor( borderColor );
        g.fillRect( center.x - dimension.width / 2, center.y - dimension.height / 2,
                dimension.width, dimension.height );
        //draw background
        g.setColor( bgColor );
        g.fillRect( center.x - dimension.width / 2 + border, center.y - dimension.height / 2 + border
                , dimension.width - 2 * border, dimension.height - 2 * border );
        /*
         *  old routine
         *  g.fillRect(center.x - size / 2 * aspectRatio + size / lineRelSize,
         *  center.y - size / 2 + size / lineRelSize,
         *  size * aspectRatio - size / lineRelSize * 2,
         *  size - size / lineRelSize * 2);
         */
        Font font = g.getFont();
        g.setFont( new Font( font.getFamily(), font.getStyle(), dimension.height * fontRelSize / 100 ) );
        FontMetrics fm = g.getFontMetrics();
        g.setColor( fontColor );
        g.drawString( text, center.x - fm.stringWidth( text ) / 2, center.y + fm.getHeight() / 4 );
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
        MainElement mE = null;
        int scale;
        int xOffset = 0;
        int yOffset = 0;

        try {
            mE = (MainElement) this.clone();
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

        mE.dimension = new Dimension( dimension.width * scale, dimension.height * scale );
        mE.center = new Point( center.x * scale + xOffset, center.y * scale + yOffset );
        return mE;
    }


    /**
     *  check if coordinates are inside shape of Part
     *
     *@param  point  Description of the Parameter
     *@return        true if inside else false
     */
    public boolean isInside( Point point ) {
        if ( point.x > center.x - dimension.width / 2 &&
                point.y > center.y - dimension.width / 2 &&
                point.x < center.x + dimension.width / 2 &&
                point.y < center.y + dimension.width / 2 ) {
            bgColor = new Color( (int) ( Math.random() * 255 ), (int) ( Math.random() * 255 ), (int) ( Math.random() * 255 ) );
            return ( true );
        }
        return ( false );
    }


    /**
     *  Description of the Method
     *
     *@param  hParams  Description of the Parameter
     */
    public void init( Hashtable hParams ) {
        bgColor = Color.lightGray;
        fontColor = Color.black;
        borderColor = Color.black;
        fontRelSize = 50;
        lineRelSize = 2;

        if ( hParams.containsKey( "url" ) ) {
            url = this.getStringParam( hParams.get( "url" ) );
        }
        if ( hParams.containsKey( "text" ) ) {
            text = this.getStringParam( hParams.get( "text" ) );
        }
        if ( hParams.containsKey( "posX" ) && hParams.containsKey( "posY" ) ) {
            center = new Point( this.getIntParam( hParams.get( "posX" ) )
                    , this.getIntParam( hParams.get( "posY" ) ) );
        }
        if ( hParams.containsKey( "height" ) && hParams.containsKey( "width" ) ) {
            dimension = new Dimension( this.getIntParam( hParams.get( "width" ) )
                    , this.getIntParam( hParams.get( "height" ) ) );
        }
        if ( hParams.containsKey( "textZoom" ) ) {
            textZoom = this.getStringParam( hParams.get( "textZoom" ) );
        }
        if ( hParams.containsKey( "bgColor" ) ) {
            bgColor = ( this.getColorParam( hParams.get( "bgColor" ) ) );
        }
        if ( hParams.containsKey( "fontRelSize" ) ) {
            fontRelSize = this.getIntParam( hParams.get( "fontRelSize" ) );
        }
        if ( hParams.containsKey( "lineRelSize" ) ) {
            lineRelSize = this.getIntParam( hParams.get( "lineRelSize" ) );
        }
        if ( hParams.containsKey( "fontColor" ) ) {
            fontColor = ( this.getColorParam( hParams.get( "fontColor" ) ) );
        }
        if ( hParams.containsKey( "borderColor" ) ) {
            borderColor = ( this.getColorParam( hParams.get( "borderColor" ) ) );
        }
    }

}

