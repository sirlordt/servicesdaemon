/*******************************************************************************
 * Copyright (c) 2013 SirLordT <sirlordt@gmail.com>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     SirLordT <sirlordt@gmail.com> - initial API and implementation
 ******************************************************************************/
package ExtendedLogger;

import java.io.File;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SocketHandler;

public class CExtendedLogger extends Logger {

	protected int intInternalSequence;
	protected boolean bSetupSet = false;
	protected int intCallStackLevel = 3; //Adjust call stack index for precise info
	
	protected String strLogIP = "";
	protected int intLogPort = -1;
	protected boolean bSocketHandActive = false;
	
	protected SocketHandler SocketHand = null;
	protected FileHandler FileHand = null;
	protected ConsoleHandler ConsoleHand = null;
	
	protected CExtendedLogXMLFormatter ExXmlFormatter = null;
	
	public static int intMinPort = 1; 
	public static int intMaxPort = 65535; 
	
	public CExtendedLogger( String strName ) {

	   super( strName, null ); 

	   intInternalSequence = 0;
	   
	   this.setUseParentHandlers( false );
	   
	   this.setLevel( Level.ALL );
	   
	}
	
	public static CExtendedLogger getLogger( String strName ){
	
		LogManager LoggerManager = LogManager.getLogManager();
		
		Object tmpObject = LoggerManager.getLogger( strName );
		
		if ( tmpObject == null ) LoggerManager.addLogger( new CExtendedLogger( strName ) );
		
		tmpObject = LoggerManager.getLogger( strName );
		
		return ( CExtendedLogger ) tmpObject;
		
	}	
	
    public void setupLogger( boolean bLogToScreen, String strLogPath, String strLogFile, String strLogFilters, boolean bExactMatch, String strLogLevel, String strLogIP, int intLogPort ) {
    	
    	try {

    		if ( strLogIP.isEmpty() == false && intLogPort >= intMinPort && intLogPort <= intMaxPort ) {
    		 
    			SocketHand = new SocketHandler( strLogIP, intLogPort );
    			
    			this.strLogIP = strLogIP;
    			this.intLogPort = intLogPort;
    			this.bSocketHandActive = true;
    			
    		}     
    		
    	}
    	catch ( Exception Ex ) {
    		
    		SocketHand = null;
			Ex.printStackTrace();
    		
    	}
    	
		try { 
    	  
			File DirPath = new File( strLogPath );
			
			if ( DirPath.exists() == false ) {
				
				DirPath.mkdirs();
				
			}
			
			FileHand = new FileHandler( strLogPath + strLogFile, 2048576, 50, false );
		 
			ExXmlFormatter = new CExtendedLogXMLFormatter();

			ExXmlFormatter.strLogFilePath = strLogPath;
			ExXmlFormatter.strLogFileName = strLogFile;
			
			FileHand.setFormatter( ExXmlFormatter );

			if ( bLogToScreen == true ) {   
		     
				ConsoleHand = new ConsoleHandler();
			
				ConsoleHand.setFormatter( ExXmlFormatter );

			}   
			
			CExtendedLogFilter LogFilter = new CExtendedLogFilter( strLogFilters, bExactMatch );

			this.setFilter( LogFilter );
			
			if ( bLogToScreen == true )    
			   this.addHandler( ConsoleHand ); 
		
			this.addHandler( FileHand );
			
			if ( SocketHand != null ) {
			
				SocketHand.setFormatter( ExXmlFormatter );
				
				this.addHandler( SocketHand );
				
			}	
			
			bSetupSet = true;
			
		}
		catch ( Exception Ex ) {
			
			Ex.printStackTrace();
			
		};
		
    }
	
	public void setInternalSequence( int intInternalSequence ) {
		
		this.intInternalSequence = intInternalSequence;
		
	}
    
	public int getInternalSequence() {
		
	   return intInternalSequence;	
		
	}
	
	public boolean getSetupSet() {
		
		return bSetupSet;
		
	}
	
	public void setCallStackLevel( int intCallStackLevel ) {

		this.intCallStackLevel = intCallStackLevel;
		
	}
	
	public int getCallStackLevel() {
		
		return intCallStackLevel;
		
	}
	
	protected void internalLog( String strLogType, String strCode, String strMessage, Throwable Thrown, Level ... level ) {
		  
		CExtendedLogRecord LogRecord = null;
		   
		if ( level.length == 0 )    
		   LogRecord = new CExtendedLogRecord( Level.INFO, strMessage ); 
		else
		   LogRecord = new CExtendedLogRecord( level[ 0 ], strMessage ); 

		if ( this.isLoggable( LogRecord.getLevel() ) ) {
			
		    intInternalSequence++;
	      
	        LogRecord.setLogType( strLogType );
	        
		    LogRecord.setThrown( Thrown );

		    LogRecord.setThreadID( (int) Thread.currentThread().getId() );
	      
	        LogRecord.setThreadName( Thread.currentThread().getName() );
	      
	        String strFullSourceClassName = Thread.currentThread().getStackTrace()[ intCallStackLevel ].getClassName();            

	        LogRecord.setSourcePackageName( strFullSourceClassName.substring( 0, strFullSourceClassName.lastIndexOf(".") ) );

	        LogRecord.setSourceClassName( strFullSourceClassName.substring( strFullSourceClassName.lastIndexOf(".") + 1 ) );
	      
	        LogRecord.setSourceMethodName( Thread.currentThread().getStackTrace()[ intCallStackLevel ].getMethodName() );
	      
	        LogRecord.setLineNumber( Thread.currentThread().getStackTrace()[ intCallStackLevel ].getLineNumber() );
	      
	        LogRecord.setCode( strCode );
	      
	        LogRecord.setSequenceNumber( intInternalSequence );
	      
	        LogRecord.setLoggerName( this.getName() );
	        
	        this.log( LogRecord );
		
		}
	
	}	
		
	public void logEntry( String strLogType, String strCode, String strMessage, Level ... level ) {
		
		internalLog( strLogType, strCode, strMessage, null, level );		
		
	}

	public void logEntry( String strLogType, String strCode, String strMessage, Throwable Thrown, Level ... level ) {
		
		internalLog( strLogType, strCode, strMessage, Thrown, level );		
		
	}
	
	public void logMessage( String strCode, String strMessage, Level ... level ) {
		
		internalLog( "Message", strCode, strMessage, null, level );		
		
	}
	
	public void logMessage( String strCode, String strMessage, Throwable Thrown, Level ... level ) {
		
		internalLog( "Message", strCode, strMessage, Thrown, level );		
		
	}

	public void logInfo( String strCode, String strMessage, Level ... level ) {
		
		internalLog( "Info", strCode, strMessage, null, level );		
		
	}
	
	public void logInfo( String strCode, String strMessage, Throwable Thrown, Level ... level ) {
		
		internalLog( "Info", strCode, strMessage, Thrown, level );		
		
	}

	public void logDebug( String strCode, String strMessage, Level ... level ) {
		
		internalLog( "Debug", strCode, strMessage, null, level );		
		
	}

	public void logDebug( String strCode, String strMessage, Throwable Thrown, Level ... level ) {
		
		internalLog( "Debug", strCode, strMessage, Thrown, level );		
		
	}

	public void logError( String strCode, String strMessage, Level ... level ) {
		
		internalLog( "Error", strCode, strMessage, null, level );		
		
	}
	
	public void logError( String strCode, String strMessage, Throwable Thrown, Level ... level ) {
		
		internalLog( "Error", strCode, strMessage, Thrown, level );		
		
	}

	public void logWarning( String strCode, String strMessage, Level ... level ) {
		
		internalLog( "Warning", strCode, strMessage, null, level );		
		
	}

	public void logWarning( String strCode, String strMessage, Throwable Thrown, Level ... level ) {
		
		internalLog( "Warning", strCode, strMessage, Thrown, level );		
		
	}

	public void logMethodEntry( String strCode, String strMessage, Level ... level ) {
		
		internalLog( "MethodEntry", strCode, strMessage, null, level );		
		
	}
	
	public void logMethodEntry( String strCode, String strMessage, Throwable Thrown, Level ... level ) {
		
		internalLog( "MethodEntry", strCode, strMessage, Thrown, level );		
		
	}

	public void logMethodLeave( String strCode, String strMessage, Level ... level ) {
		
		internalLog( "MethodLeave", strCode, strMessage, null, level );		
		
	}
	
	public void logMethodLeave( String strCode, String strMessage, Throwable Thrown, Level ... level ) {
		
		internalLog( "MethodLeave", strCode, strMessage, Thrown, level );		
		
	}

	public void logException( String strCode, String strMessage, Exception ExceptionInfo, Level ... level ) {
	
		CExtendedLogRecord LogRecord = null;
		   
		if ( level.length == 0 )    
		   LogRecord = new CExtendedLogRecord( Level.SEVERE, strMessage ); 
		else
		   LogRecord = new CExtendedLogRecord( level[ 0 ], strMessage ); 

		if ( this.isLoggable( LogRecord.getLevel() ) ) {
			
		    intInternalSequence++;
	      
		    LogRecord.setThrown( ExceptionInfo );
		    
	        LogRecord.setLogType( "Exception" );
	        
	        LogRecord.setThreadID( (int) Thread.currentThread().getId() );
	      
	        LogRecord.setThreadName( Thread.currentThread().getName() );
	      
	        String strFullSourceClassName = Thread.currentThread().getStackTrace()[ intCallStackLevel - 1 ].getClassName(); 

	        LogRecord.setSourcePackageName( strFullSourceClassName.substring( 0, strFullSourceClassName.lastIndexOf(".") ) );

	        LogRecord.setSourceClassName( strFullSourceClassName.substring( strFullSourceClassName.lastIndexOf(".") + 1 ) );
	        
        	LogRecord.setSourceMethodName( Thread.currentThread().getStackTrace()[ intCallStackLevel - 1 ].getMethodName() );
	        
        	LogRecord.setLineNumber( Thread.currentThread().getStackTrace()[ intCallStackLevel - 1 ].getLineNumber() );

        	/*if ( ExceptionInfo != null ) {
	        
	        	strFullSourceClassName = ExceptionInfo.getStackTrace()[ 0 ].getClassName();
	        
	        	LogRecord.setSourceMethodName( ExceptionInfo.getStackTrace()[ 0 ].getMethodName() );
		        
	        	LogRecord.setLineNumber( ExceptionInfo.getStackTrace()[ 0 ].getLineNumber() );
	        	
	        }	
	        else {*/
	        
	      
	        //};
	        
	        LogRecord.setCode( strCode );
	      
	        LogRecord.setSequenceNumber( intInternalSequence );
	      
	        LogRecord.setLoggerName( this.getName() );
	        
	        this.log( LogRecord );
		
		}
		
	}
	
	public void logError( String strCode, String strMessage, Error ErrorInfo, Level ... level ) {
		
		CExtendedLogRecord LogRecord = null;
		   
		if ( level.length == 0 )    
		   LogRecord = new CExtendedLogRecord( Level.SEVERE, strMessage ); 
		else
		   LogRecord = new CExtendedLogRecord( level[ 0 ], strMessage ); 

		if ( this.isLoggable( LogRecord.getLevel() ) ) {
			
		    intInternalSequence++;
	      
		    LogRecord.setThrown( ErrorInfo );
		    
	        LogRecord.setLogType( "Hard-Error" );
	        
	        LogRecord.setThreadID( (int) Thread.currentThread().getId() );
	      
	        LogRecord.setThreadName( Thread.currentThread().getName() );
	      
	        String strFullSourceClassName = Thread.currentThread().getStackTrace()[ intCallStackLevel - 1 ].getClassName(); 

	        LogRecord.setSourcePackageName( strFullSourceClassName.substring( 0, strFullSourceClassName.lastIndexOf(".") ) );

	        LogRecord.setSourceClassName( strFullSourceClassName.substring( strFullSourceClassName.lastIndexOf(".") + 1 ) );
	        
        	LogRecord.setSourceMethodName( Thread.currentThread().getStackTrace()[ intCallStackLevel - 1 ].getMethodName() );
	        
        	LogRecord.setLineNumber( Thread.currentThread().getStackTrace()[ intCallStackLevel - 1 ].getLineNumber() );

        	/*if ( ExceptionInfo != null ) {
	        
	        	strFullSourceClassName = ExceptionInfo.getStackTrace()[ 0 ].getClassName();
	        
	        	LogRecord.setSourceMethodName( ExceptionInfo.getStackTrace()[ 0 ].getMethodName() );
		        
	        	LogRecord.setLineNumber( ExceptionInfo.getStackTrace()[ 0 ].getLineNumber() );
	        	
	        }	
	        else {*/
	        
	      
	        //};
	        
	        LogRecord.setCode( strCode );
	      
	        LogRecord.setSequenceNumber( intInternalSequence );
	      
	        LogRecord.setLoggerName( this.getName() );
	        
	        this.log( LogRecord );
		
		}
		
	}
	
	public void setLogIP( String strLogIP ) {
		
		this.strLogIP = strLogIP;
		
		this.bSocketHandActive = false;
		
	}
	
	public String getLogIP() {
		
		return strLogIP;
		
	}
	
	public void setLogPort( int intLogPort ) {
		
		this.intLogPort = intLogPort;
		
		this.bSocketHandActive = false;
		
	}
	
	public int getLogPort() {
		
		return intLogPort;
		
	}
	
	public boolean activateSocketHandler( boolean bAddSocketHandToList ) {
		
		boolean bResult = false;
		
		if ( this.bSocketHandActive == false ) {
			
			if ( SocketHand != null && bAddSocketHandToList == false )
				this.removeHandler( SocketHand );
			
			try {
				
    			SocketHand = new SocketHandler( strLogIP, intLogPort );

				SocketHand.setFormatter( ExXmlFormatter );
    			
    			this.addHandler( SocketHand );
				
    			bResult = true;
    			
			}
			catch ( Exception Ex ) {
				
				if ( bSetupSet == false )
					Ex.printStackTrace();
				else
					this.logException( "-1020", Ex.getMessage(), Ex );
				
			}
			
			this.bSocketHandActive = true;
			
		}
		
		return bResult;
		
	}
	
}
