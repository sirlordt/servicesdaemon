package WebRowSet;

import java.io.StringReader;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.text.MessageFormat;

import javax.sql.RowSet;
import javax.sql.rowset.WebRowSet;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.sun.rowset.JdbcRowSetResourceBundle;
import com.sun.rowset.internal.WebRowSetXmlReader;
import com.sun.rowset.internal.XmlReaderContentHandler;
import com.sun.rowset.internal.XmlErrorHandler;
import com.sun.rowset.internal.XmlResolver;

public class CWebRowSetXmlReader extends WebRowSetXmlReader {

	/**
	 * 
	 */
	private static final long serialVersionUID = 453457435059858559L;

	JdbcRowSetResourceBundle resBundle = null; //I hate the private class members

	public CWebRowSetXmlReader() { //Must be called after the new operator 

		super();
		
		try {
			
			Field _PrivateClassField = WebRowSetXmlReader.class.getDeclaredField( "resBundle" );
			_PrivateClassField.setAccessible( true );
			resBundle = (JdbcRowSetResourceBundle) _PrivateClassField.get( this );

		} 
		catch ( Exception Ex ) {
		
			Ex.printStackTrace();
	
		}	
		
	}	
	
    public void ReadXML( WebRowSet caller, String strXMLData ) throws SQLException {
    	
    	try {

    		InputSource is = new InputSource( new StringReader( strXMLData ) );
    		DefaultHandler dh = new XmlErrorHandler();

    		XmlReaderContentHandler hndr = new XmlReaderContentHandler( (RowSet) caller );
    		SAXParserFactory factory = SAXParserFactory.newInstance();
    		factory.setNamespaceAware(true);
    		factory.setValidating(true);

    		SAXParser parser = factory.newSAXParser() ;

    		parser.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage",
    				"http://www.w3.org/2001/XMLSchema");

    		XMLReader reader1 = parser.getXMLReader() ;
    		reader1.setEntityResolver( new XmlResolver() );
    		reader1.setContentHandler(hndr);

    		reader1.setErrorHandler(dh);

    		reader1.parse(is);

    	} 
    	catch ( SAXParseException Ex ) {

    		System.out.println( MessageFormat.format( resBundle.handleGetObject("wrsxmlreader.parseerr").toString(), new Object[]{ Ex.getLineNumber(), Ex.getSystemId() } ) );
    		System.out.println( "   " + Ex.getMessage () );
    		Ex.printStackTrace();
    		throw new SQLException( Ex.getMessage() );

    	} 
    	catch ( SAXException Ex ) {

    		if ( Ex.getMessage().equals( "stop_parsing" ) == false ) {

    			Exception   x = Ex;
    			if (Ex.getException () != null)
    				x = Ex.getException();
    			x.printStackTrace ();
    			throw new SQLException(x.getMessage());

    		}

    	}
    	// Will be here if trying to write beyond the RowSet limits
    	catch ( ArrayIndexOutOfBoundsException Ex ) {

    		throw new SQLException( resBundle.handleGetObject( "wrsxmlreader.invalidcp" ).toString() );

    	}
    	catch ( Throwable Ex ) {

    		throw new SQLException( MessageFormat.format( resBundle.handleGetObject( "wrsxmlreader.readxml" ).toString(), Ex.getMessage() ) );

    	}

    }
	
	
}
