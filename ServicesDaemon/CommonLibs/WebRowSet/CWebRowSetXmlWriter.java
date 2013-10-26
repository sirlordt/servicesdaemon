package WebRowSet;

import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javax.sql.RowSet;
import javax.sql.rowset.WebRowSet;

import com.sun.rowset.internal.WebRowSetXmlWriter;

public class CWebRowSetXmlWriter extends WebRowSetXmlWriter {

	/**
	 * 
	 */
	private static final long serialVersionUID = -943096318023880256L;

	protected Method startHeaderMethod = null;
	protected Method endHeaderMethod = null;
	protected Method writePropertiesMethod = null;
	protected Method writeMetaDataMethod = null;
	//protected Method writeDataMethod = null;
	protected Method beginSectionMethod = null;
	protected Method endSectionMethod = null;
	protected Method endSectionMethodVoid = null;
	protected Method beginTagMethod = null;
	protected Method endTagMethod = null;
	protected Method writeValueMethod = null;
	
	@SuppressWarnings("rawtypes")
	protected java.util.Stack stack;
	
	protected OutputStreamWriter writer; 
	
	public CWebRowSetXmlWriter() {  
		
		super();
		
		try {

			//I hate the private method on Object Oriented Programming
			//This suck because the stupid private members and method
			startHeaderMethod = WebRowSetXmlWriter.class.getDeclaredMethod( "startHeader" );
	    	startHeaderMethod.setAccessible( true );
	    	
			endHeaderMethod = WebRowSetXmlWriter.class.getDeclaredMethod( "endHeader" );
	    	endHeaderMethod.setAccessible( true );

	    	writePropertiesMethod = WebRowSetXmlWriter.class.getDeclaredMethod( "writeProperties", WebRowSet.class );
	    	writePropertiesMethod.setAccessible( true );

	    	writeMetaDataMethod = WebRowSetXmlWriter.class.getDeclaredMethod( "writeMetaData", WebRowSet.class );
	    	writeMetaDataMethod.setAccessible( true );

	    	beginSectionMethod = WebRowSetXmlWriter.class.getDeclaredMethod( "beginSection", String.class );
	    	beginSectionMethod.setAccessible( true );
	    	
	    	endSectionMethod = WebRowSetXmlWriter.class.getDeclaredMethod( "endSection", String.class );
	    	endSectionMethod.setAccessible( true );

	    	endSectionMethodVoid = WebRowSetXmlWriter.class.getDeclaredMethod( "endSection" );
	    	endSectionMethodVoid.setAccessible( true );

	    	beginTagMethod = WebRowSetXmlWriter.class.getDeclaredMethod( "beginTag", String.class );
	    	beginTagMethod.setAccessible( true );
	    	
	    	endTagMethod = WebRowSetXmlWriter.class.getDeclaredMethod( "endTag", String.class );
	    	endTagMethod.setAccessible( true );
	    	
	    	writeValueMethod = WebRowSetXmlWriter.class.getDeclaredMethod( "writeValue", int.class, RowSet.class );
	    	writeValueMethod.setAccessible( true );
	    	
		} 
		catch ( Exception Ex ) {
			
			Ex.printStackTrace();
			
		}
		
	}
	
	@SuppressWarnings("rawtypes")
	public java.util.Stack getInternalStack() {
		
		return stack;
		
	}

	public OutputStreamWriter getInternalWriter() {
		
		return writer;
		
	}
	
    @SuppressWarnings({ "rawtypes" })
	public void initStackAndWriter( java.io.OutputStream oStream ) {
		
        // create a new stack for tag checking.
        stack = new java.util.Stack();
        writer = new OutputStreamWriter( oStream );
        
        try {
        	
        	//I hate the private members in Object Oriented Programming 
    		Field PrivateFieldClass = WebRowSetXmlWriter.class.getDeclaredField( "stack" );
    		PrivateFieldClass.setAccessible( true ); //Abracadabra 
    		PrivateFieldClass.set( this, stack ); //now its ok

    		PrivateFieldClass = WebRowSetXmlWriter.class.getDeclaredField( "writer" );
    		PrivateFieldClass.setAccessible( true ); //Abracadabra 
    		PrivateFieldClass.set( this, writer ); //now its ok
    		
        }
        catch ( Exception Ex ) {
        	
        	Ex.printStackTrace();
        	
        }
		
	}
	
	public void initWriteXML( WebRowSet caller, java.io.OutputStream oStream ) throws SQLException {

    	initStackAndWriter( oStream );
    	
        initWriteRowSet( caller );
    
    }
    
	public void WriteXMLBody( WebRowSet caller, java.io.OutputStream oStream ) throws SQLException {

        WriteRowSetData( caller );
    
    }
    
	public void endWriteXMLData( WebRowSet caller, java.io.OutputStream oStream ) throws SQLException {
        
        endWriteRowSet( caller );
    
    }
    
    protected void initWriteRowSet( WebRowSet caller ) throws SQLException {
    	
        try {

			startHeaderMethod.invoke( this );
			writePropertiesMethod.invoke( this, caller );
            writeMetaDataMethod.invoke( this, caller );
            beginSectionMethod.invoke( this, "data" );

        }
        catch ( Exception Ex ) {
            
        	//throw new SQLException( MessageFormat.format( resBundle.handleGetObject( "wrsxmlwriter.ioex" ).toString(), Ex.getMessage() ) );
        	Ex.printStackTrace();
        	
        }
    	
    }

    protected void WriteRowSetData( WebRowSet caller ) throws SQLException {

        try {

            this.writeData( caller );
        	//writeDataMethod.invoke( this, caller );

        } 
        catch ( Exception Ex ) {

        	//throw new SQLException(MessageFormat.format(resBundle.handleGetObject("wrsxmlwriter.ioex").toString(), ex.getMessage()));
        	Ex.printStackTrace();
        	
        }
        
    }	
    
    protected void endWriteRowSet( WebRowSet caller ) throws SQLException {
    	
        try {

            endSectionMethod.invoke( this, "data" );
            endHeaderMethod.invoke( this );

        } 
        catch ( Exception Ex ) {
            
        	//throw new SQLException(MessageFormat.format(resBundle.handleGetObject("wrsxmlwriter.ioex").toString(), ex.getMessage()));
        	Ex.printStackTrace();
        
        }
    	
    }
	
    private void writeData( WebRowSet caller ) throws java.io.IOException {
        
    	ResultSet rs;

        try {
            
        	ResultSetMetaData rsmd = caller.getMetaData();
            int columnCount = rsmd.getColumnCount();
            int i;

            //if ( bWriteDataSection )
            //   beginSection("data");

            caller.beforeFirst();
            caller.setShowDeleted( true );
            
            while ( caller.next() ) {
                
            	if ( caller.rowDeleted() && caller.rowInserted() ) {
            		
            		beginSectionMethod.invoke( this, "modifyRow" );
            		
                } 
            	else if (caller.rowDeleted()) {
                	
            		beginSectionMethod.invoke( this, "deleteRow" );
            		
                } 
                else if (caller.rowInserted()) {
                	
                	beginSectionMethod.invoke( this, "insertRow" );
                	
                } 
                else {
                	
                	beginSectionMethod.invoke( this, "currentRow" );
                	
                }

                for ( i = 1; i <= columnCount; i++ ) {
                    
                	if ( caller.columnUpdated( i ) ) {
                        
                    	rs = caller.getOriginalRow();
                        rs.next();
                        
                        beginTagMethod.invoke( this, "columnValue" );
                        writeValueMethod.invoke( this, i, (RowSet)rs);
                        endTagMethod.invoke( this, "columnValue" );
                        beginTagMethod.invoke( this, "updateRow" );
                        writeValueMethod.invoke( this, i, caller);
                        endTagMethod.invoke( this, "updateRow" );
                        
                    } 
                    else {
                        
                    	beginTagMethod.invoke( this, "columnValue" );
                        writeValueMethod.invoke( this, i, caller);
                        endTagMethod.invoke( this, "columnValue" );
                        
                    }
                }

                endSectionMethodVoid.invoke( this ); // this is unchecked
            
            }
            
            //if ( bWriteDataSection )
            //   endSection("data");
            
        } 
        catch ( Exception Ex ) {
        
        	//throw new java.io.IOException(MessageFormat.format(resBundle.handleGetObject("wrsxmlwriter.sqlex").toString(), ex.getMessage()));
        	Ex.printStackTrace();
        
        }
        
    }
	
}
