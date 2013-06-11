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

public class CExtendedLogger extends Logger {

	protected int intInternalSequence;
	protected boolean bSetupSet = false;
	protected int intCallStackLevel = 3; //Adjust call stack index for precise info
	
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
	
    public void SetupLogger( boolean bLogToScreen, String strLogPath, String strLogFile, String strLogFilters, boolean bExactMatch, String strLogLevel ) {
    	
		try { 
    	  
			File DirPath = new File( strLogPath );
			
			if ( DirPath.exists() == false ) {
				
				DirPath.mkdirs();
				
			}
			
			FileHandler FileHand = new FileHandler( strLogPath + strLogFile, 2048576, 50, false );
		 
			ConsoleHandler ConsoleHand = null;
			
			CExtendedLogXMLFormatter ExXmlFormatter = new CExtendedLogXMLFormatter();

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
	
	protected void InternalLog( String strLogType, String strCode, String strMessage, Throwable Thrown, Level ... level ) {
		  
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
		
	public void LogEntry( String strLogType, String strCode, String strMessage, Level ... level ) {
		
		InternalLog( strLogType, strCode, strMessage, null, level );		
		
	}

	public void LogEntry( String strLogType, String strCode, String strMessage, Throwable Thrown, Level ... level ) {
		
		InternalLog( strLogType, strCode, strMessage, Thrown, level );		
		
	}
	
	public void LogMessage( String strCode, String strMessage, Level ... level ) {
		
		InternalLog( "Message", strCode, strMessage, null, level );		
		
	}
	
	public void LogMessage( String strCode, String strMessage, Throwable Thrown, Level ... level ) {
		
		InternalLog( "Message", strCode, strMessage, Thrown, level );		
		
	}

	public void LogInfo( String strCode, String strMessage, Level ... level ) {
		
		InternalLog( "Info", strCode, strMessage, null, level );		
		
	}
	
	public void LogInfo( String strCode, String strMessage, Throwable Thrown, Level ... level ) {
		
		InternalLog( "Info", strCode, strMessage, Thrown, level );		
		
	}

	public void LogDebug( String strCode, String strMessage, Level ... level ) {
		
		InternalLog( "Debug", strCode, strMessage, null, level );		
		
	}

	public void LogDebug( String strCode, String strMessage, Throwable Thrown, Level ... level ) {
		
		InternalLog( "Debug", strCode, strMessage, Thrown, level );		
		
	}

	public void LogError( String strCode, String strMessage, Level ... level ) {
		
		InternalLog( "Error", strCode, strMessage, null, level );		
		
	}
	
	public void LogError( String strCode, String strMessage, Throwable Thrown, Level ... level ) {
		
		InternalLog( "Error", strCode, strMessage, Thrown, level );		
		
	}

	public void LogWarning( String strCode, String strMessage, Level ... level ) {
		
		InternalLog( "Warning", strCode, strMessage, null, level );		
		
	}

	public void LogWarning( String strCode, String strMessage, Throwable Thrown, Level ... level ) {
		
		InternalLog( "Warning", strCode, strMessage, Thrown, level );		
		
	}

	public void LogMethodEntry( String strCode, String strMessage, Level ... level ) {
		
		InternalLog( "MethodEntry", strCode, strMessage, null, level );		
		
	}
	
	public void LogMethodEntry( String strCode, String strMessage, Throwable Thrown, Level ... level ) {
		
		InternalLog( "MethodEntry", strCode, strMessage, Thrown, level );		
		
	}

	public void LogMethodLeave( String strCode, String strMessage, Level ... level ) {
		
		InternalLog( "MethodLeave", strCode, strMessage, null, level );		
		
	}
	
	public void LogMethodLeave( String strCode, String strMessage, Throwable Thrown, Level ... level ) {
		
		InternalLog( "MethodLeave", strCode, strMessage, Thrown, level );		
		
	}

	public void LogException( String strCode, String strMessage, Exception ExceptionInfo, Level ... level ) {
	
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

		//InternalLog( "Exception", strCode, strMessage, level );		
		
	}
	
}
