package muminav;

/*
 *  $Id: TreeBuilder.java,v 1.1 2002/09/16 20:16:05 ercmat Exp $
 */
import muminav.skin.Part;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import org.xml.sax.*;
import java.util.Vector;
import java.util.Stack;
import java.util.Hashtable;
import java.io.*;

/**
 *  Reads the XML input and generates a tree representing the graph.
 *
 *@author     ercmat
 *@created    2. September 2002
 */
public class TreeBuilder extends DefaultHandler {

    // this is the node where to add drawable components
    private static Vector actualNode = null;
    // actual level in the tree
    private int actualLevel = 0;
    // level in the last startElement() call
    private int beforeLevel = 0;
    // this Stack holds all nodes (means Vectors of Parts)
    // the topmost element will be the next actuaNode
    private Stack nodeStack = null;


    /**
     *  Constructor for the TreeBuilder object
     */
    public TreeBuilder() {
        super();
    }


    /**
     *  Does some inits for the xml parser.
     *
     *@param  xml            xml representation of the graph
     *@exception  Exception  Thrown in case of problems while configurating a
     *      new parser
     */
    public void init( InputStream xml )
             throws Exception {

        // using myself as the handler
        DefaultHandler handler = new TreeBuilder();

        // default (non-validating) parser
        SAXParserFactory factory = SAXParserFactory.newInstance();

        // creating new parser
        SAXParser saxParser = factory.newSAXParser();

        saxParser.parse( xml, handler );

    }


    /**
     *  Returns the tree which represents the graph, defined by the xml input.
     *
     *@return    The tree which represents the graph
     */
    public Vector getTree() {
        return this.actualNode;
    }


    //
    // methods for event handling
    //

    /**
     *  Actions for the start of the document.
     *
     *@exception  SAXException
     */
    public void startDocument()
             throws SAXException {
        this.nodeStack = new Stack();
        this.nodeStack.push( new Vector() );
    }


    /**
     *  Actions for the end of the document.
     *
     *@exception  SAXException
     */
    public void endDocument()
             throws SAXException {
        Vector part = null;

        while ( !this.nodeStack.empty() ) {
            part = (Vector) this.nodeStack.pop();
            if ( part.size() > 0 ) {
                this.actualNode = part;
            }
        }
    }


    /**
     *  Actions for the start of an element.
     *
     *@param  namespaceURI      URI of the namespace, if enabled
     *@param  sName             simple name (localName)
     *@param  qName             qualified name
     *@param  attrs             The attributes of this element
     *@exception  SAXException
     */
    public void startElement( String namespaceURI, String sName, String qName, Attributes attrs )
             throws SAXException {
        // the new drawable element
        Part element = null;
        // name of the element
        String eName = sName;
        // parameters for drawing the element
        Hashtable hParams = new Hashtable();

        // element name
        if ( "".equals( eName ) ) {
            eName = qName;
        }

        this.actualLevel++;

        // TODO: get it from the applet
        String pack = "muminav.skin.math.";

        try {
            element = (Part) Class.forName( pack + eName.trim() ).newInstance();
        } catch ( Exception e ) {
            // TODO: decide what to do with the topmost element in the xml file
            element = new muminav.skin.math.DummyElement();
        }
        // create a new level in the tree
        if ( this.beforeLevel < this.actualLevel ) {
            this.actualNode = (Vector) this.nodeStack.peek();
        }

        // change beforeLevel for the next call of startElement
        this.beforeLevel = this.actualLevel;

        // namespaceAware = false
        if ( attrs != null ) {
            for ( int i = 0; i < attrs.getLength(); i++ ) {
                // Attr name
                String aName = attrs.getLocalName( i );

                if ( "".equals( aName ) ) {
                    aName = attrs.getQName( i );
                }
                hParams.put( aName, attrs.getValue( i ) );
            }
            ( (Part) element ).init( hParams );
        }
        this.actualNode.add( element );
        this.nodeStack.push( element.getChilds() );
    }


    /**
     *  Actions for the start of an element.
     *
     *@param  namespaceURI      URI of the namespace, if enabled
     *@param  sName             simple name (localName)
     *@param  qName             qualified name
     *@exception  SAXException
     */
    public void endElement( String namespaceURI, String sName, String qName )
             throws SAXException {
        this.actualLevel--;
        this.nodeStack.pop();
    }

}
/*
 *  $Log: TreeBuilder.java,v $
 *  Revision 1.1  2002/09/16 20:16:05  ercmat
 *  TreeBuilder.java hinzugefuegt aber noch nicht benutzt.
 *
 */

