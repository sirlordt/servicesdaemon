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
package CommonClasses;

import java.util.logging.Level;

import org.eclipse.jetty.util.log.AbstractLogger;
import org.eclipse.jetty.util.log.Logger;

import ExtendedLogger.CExtendedLogger;

public class CExtendedJettyLogger extends AbstractLogger {

	public static CServicesDaemonConfig GlobalConfig = null;
	
	//protected Level ConfiguredLevel;
    protected CExtendedLogger ExtendedLogger;	
    protected String strLoggerName;
	
	public CExtendedJettyLogger() {

		this( DefaultConstantsServicesDaemon.strDefaultJettyLoggerClassName );
		
	}

	public CExtendedJettyLogger( String strName ) {
		
		ExtendedLogger = CExtendedLogger.getLogger( DefaultConstantsServicesDaemon.strDefaultJettyLoggerName );

		if ( ExtendedLogger.getSetupSet() == false ) {  
		
			if ( GlobalConfig == null )
				GlobalConfig = CServicesDaemonConfig.getServicesDaemonConfig();

			ExtendedLogger.SetupLogger( GlobalConfig.InitArgs.contains( "-LogToScreen" ), DefaultConstantsServicesDaemon.strDefaultRunningPath + DefaultConstantsServicesDaemon.strDefaultLogsSystemDir, DefaultConstantsServicesDaemon.strDefaultMainJettyFileLog, GlobalConfig.strClassNameMethodName, GlobalConfig.bExactMatch, GlobalConfig.LoggingLevel.toString() );
		
			//this class is a wrapper for the real logger, adjust the call stack level for precise logging info
			ExtendedLogger.setCallStackLevel( 4 ); 
			
		}	
        
		this.strLoggerName = strName; 
		
	}

	@Override
	public void debug( Throwable Thrown ) {

		ExtendedLogger.LogDebug( "2", "No message", Thrown );
		
	}

	@Override
	public void debug( String strMessage, Object... args ) {

		ExtendedLogger.LogDebug( "2", Format( strMessage, args ) );
		
	}

	@Override
	public void debug( String strMessage, Throwable Thrown ) {
		
		ExtendedLogger.LogDebug( "2", strMessage, Thrown );
		
	}

	@Override
	public String getName() {

		return this.strLoggerName; //ExtendedLogger.getName();
		
	}

	@Override
	public void ignore( Throwable Thrown ) {

		ExtendedLogger.LogEntry( "Ignore", "0", "No message", Thrown );
		
	}

	@Override
	public void info( Throwable Thrown ) {

		ExtendedLogger.LogInfo( "1", "No message", Thrown );
		
	}

	@Override
	public void info( String strMessage, Object... args ) {
		
		ExtendedLogger.LogInfo( "1", Format( strMessage, args ) );
	}

	@Override
	public void info( String strMessage, Throwable Thrown ) {
		
		ExtendedLogger.LogInfo( "1", strMessage, Thrown );

	}

	@Override
	public boolean isDebugEnabled() {
		
		return ExtendedLogger.isLoggable( Level.FINE );
		
	}

	@Override
	public void setDebugEnabled( boolean bEnabled ) {

        //CExtendedLogger always log all
        
	}

	@Override
	public void warn( Throwable Thrown ) {
		
		ExtendedLogger.LogWarning( "-1", "No message", Thrown );
		
	}

	@Override
	public void warn( String strMessage, Object... args ) {

		ExtendedLogger.LogWarning( "-1", Format( strMessage, args ) );
	
	}

	@Override
	public void warn( String strMessage, Throwable Thrown ) {
		
		ExtendedLogger.LogWarning( "-1", strMessage, Thrown );
		
	}

	@Override
	protected Logger newLogger( String strFullName ) {
        
		return new CExtendedJettyLogger( strFullName );
		
    }

    private String Format( String msg, Object... args ) {
    	
        msg = String.valueOf( msg ); // Avoids NPE
        String braces = "{}";
        StringBuilder builder = new StringBuilder();
        int start = 0;
        
        for ( Object arg : args )  {
            
        	int bracesIndex = msg.indexOf( braces, start );
            
        	if ( bracesIndex < 0 ) {
            
        		builder.append( msg.substring( start ) );
                builder.append( " " );
                builder.append( arg );
                start = msg.length();
            
        	}
            else {
            	
            	builder.append(msg.substring( start, bracesIndex ) );
                builder.append(String.valueOf( arg ) );
                start = bracesIndex + braces.length();
            
            }
        	
        }
     
        builder.append( msg.substring( start ) );
        
        return builder.toString();
        
    }	
    
}
