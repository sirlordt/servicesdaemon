package WebRowSet;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.SQLException;

import rowset.CachedRowSetImpl;
import rowset.WebRowSetImpl;

public class CWebRowSetImpl extends WebRowSetImpl {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5808774654566739597L;

    protected CWebRowSetXmlReader xmlReader;

    protected CWebRowSetXmlWriter xmlWriter;
	
	public CWebRowSetImpl() throws SQLException {
		super();

        xmlReader = new CWebRowSetXmlReader();
        xmlWriter = new CWebRowSetXmlWriter();
	}

	@SuppressWarnings("unchecked")
	public void initMergedWriteXML( java.io.OutputStream oStream ) {
		
		//xmlWriter.initStackAndWriter( oStream );

		if ( xmlWriter.getInternalStack() != null ) { //keep the correct indent in the xml file
		
			xmlWriter.getInternalStack().push( "webRowSet" );
			xmlWriter.getInternalStack().push( "data" );
			
		}
		
	}
	
    //Don't standard method for handle write very big XML-WebRowSet file
    public void initWriteXML( java.io.OutputStream oStream )  throws SQLException, IOException {
    	
    	xmlWriter.initWriteXML( this, oStream );
    	
    }
    
    //Don't standard method for handle write very big XML-WebRowSet file
    public void WriteXMLPage( java.io.OutputStream oStream ) throws SQLException, IOException {

    	xmlWriter.WriteXMLBody( this, oStream );
    	
    }	
    
    //Don't standard method for handle write very big XML-WebRowSet file
    public void endWriteXML( java.io.OutputStream oStream ) throws SQLException, IOException {
    	
    	xmlWriter.endWriteXMLData( this, oStream );
    	
    }

    public void initReadXML( BufferedReader BufReader ) throws SQLException, IOException {
    	
        if (BufReader != null) {
        	
			//BufferedReader br = new BufferedReader( new InputStreamReader( iStream ) );
			
			String strLine;

			StringBuilder strXMLHeader = new StringBuilder();
			
			//Read File Line By Line
			while ( (strLine = BufReader.readLine()) != null )   {
			  
				// Print the content on the console
				//System.out.println (strLine);
				
				strXMLHeader.append( strLine + "\r\n" );
				
				if ( strLine.contains( "<data>" ) ) {
					
					break;
					
				}
				
			};
			
			

			strXMLHeader.append( "  </data>\r\n" );
			strXMLHeader.append( "</webRowSet>" );

			//br.close();
			xmlReader.ReadXML( this, strXMLHeader.toString() );
			
			
        } else {
            throw new SQLException(resBundle.handleGetObject("webrowsetimpl.invalidrd").toString());
        }
    	
    }

    public boolean readXMLBody( BufferedReader BufReader ) throws SQLException, IOException {
    	
    	boolean bEof = false;
    	
        if ( BufReader != null ) {
        	
        	this.release(); //Reset the rows to zero
        	
			String strLine;

			StringBuilder strXMLData = new StringBuilder();
			
			int intRowCount = 0;
			
			strXMLData.append( "<data>\r\n" );
			
			//Read File Line By Line
			while ( (strLine = BufReader.readLine()) != null )   {
			  
				if ( strLine.contains( "</currentRow>" ) ) {
					
					intRowCount += 1;
					
				}
				
				if ( this.getPageSize() > 0 && intRowCount >= this.getPageSize() ) {
					
					strXMLData.append( strLine + "\r\n" );
					break;
					
				}
				else if ( strLine.contains( "</data>" ) ) {
					
					bEof = true;
					break;
					
				}
				else {
					
					strXMLData.append( strLine + "\r\n" );
					
				}
				
			};

			strXMLData.append( "</data>\r\n" );

			xmlReader.ReadXML( this, strXMLData.toString() );
			
        	//I Hate the private elements in Object Orient Programming
			try {
    		
        		//Hack the inherited CachedRowSetImpl class
        		Field numRows = CachedRowSetImpl.class.getDeclaredField( "numRows" );
        		numRows.setAccessible( true ); //Abracadabra 
        		numRows.setInt( this, intRowCount );//now its ok
                
        		Field cursorPos = CachedRowSetImpl.class.getDeclaredField( "cursorPos" );
        		cursorPos.setAccessible( true ); //Abracadabra
        		cursorPos.setInt( this, 0 );  //now its ok
        		
			} 
        	catch ( Exception e ) {

				e.printStackTrace();
				
			}
			
			
        } else {
            throw new SQLException(resBundle.handleGetObject("webrowsetimpl.invalidrd").toString());
        }
    	
        return bEof;
        
    }
    
}
