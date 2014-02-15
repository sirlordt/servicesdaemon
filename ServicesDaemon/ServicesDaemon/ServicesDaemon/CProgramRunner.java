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
package ServicesDaemon;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.TimeZone;
import java.util.Timer;

import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
/*
 * Jetty 8 Imports
import org.eclipse.jetty.server.NCSARequestLog;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.IPAccessHandler;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.server.ssl.SslSelectChannelConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;
*/

/*
 * Jetty 9.1 Imports*/
import org.eclipse.jetty.server.NCSARequestLog;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.IPAccessHandler;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import AbstractCacheEngine.CMasterCacheEngine;
import AbstractServicesManager.*;
import CommonClasses.CClassPathLoader;
import CommonClasses.CConfigAccessControl;
import CommonClasses.CConfigNetworkInterface;
import CommonClasses.CCustomErrorHandler;
import CommonClasses.CLanguage;
import CommonClasses.CConfigServicesDaemon;
import CommonClasses.ConstantsCommonConfigXMLTags;
import CommonClasses.ConstantsCommonClasses;
import CommonClasses.ConstantsMessagesCodes;
import CommonClasses.InitArgsConstants;
import ExtendedLogger.CExtendedLogger;

public class CProgramRunner implements IMessageObject {

	protected static ArrayList<String> InitArgs = null;
	protected static ArrayList<CAbstractServicesManager> RegisteredServicesManagers = null;

    protected static final String strVersion = "0.0.0.1";
    
    protected CExtendedLogger ServicesDaemonLogger = null;
    protected CLanguage ServicesDaemonLang = null;
    
    protected Server MainServer = null;
    
    protected CConfigServicesDaemon GlobalConfig = null;
    
    protected int intRunCode = 0; 
    
    protected String strRunningPath = ""; 
    
    public void finalize() {
    	
	    endServicesManagersFromMainServer( GlobalConfig, ServicesDaemonLogger, ServicesDaemonLang );
    	
    }
    
    static {
    	
    	RegisteredServicesManagers = new ArrayList<CAbstractServicesManager>();
    	
    }

    public String getRunningPath() {
    	
    	return strRunningPath;
    	
    }
    
    public class ServicesManagerComparator implements Comparator<CAbstractServicesManager> {

		@Override
		public int compare( CAbstractServicesManager ServiceManager1, CAbstractServicesManager ServiceManager2 ) {
			
			if ( ServiceManager1.getInitPriority() < ServiceManager2.getInitPriority() ) {
			
				return -1;
				
			}
			else if ( ServiceManager1.getInitPriority() > ServiceManager2.getInitPriority() ) {
				
				return 1;
				
			}
			
			return 0;
			
		}
		
    }    
    
    protected boolean loadServicesManagersInstances( CConfigServicesDaemon ServicesDaemonConfig, CExtendedLogger ServicesDaemonLogger, CLanguage ServicesDaemonLang ) {
    	
		boolean bResult = false;

		try {
			
			ServiceLoader<CAbstractServicesManager> sl = ServiceLoader.load( CAbstractServicesManager.class );
			sl.reload();

			RegisteredServicesManagers.clear();

			ArrayList<CAbstractServicesManager> AuxRegisteredServicesManagers = new ArrayList<CAbstractServicesManager>();
			
			Iterator<CAbstractServicesManager> it = sl.iterator();

			while ( it.hasNext() ) {

				try {

					CAbstractServicesManager ServicesManagerInstance = it.next();

				    AuxRegisteredServicesManagers.add( ServicesManagerInstance );

				} 
				catch ( Error Err ) {
					
					if ( ServicesDaemonLogger != null )
						ServicesDaemonLogger.logError( "-1020", Err.getMessage(), Err );
					
				}
				catch ( Exception Ex ) {

					if ( ServicesDaemonLogger != null )
						ServicesDaemonLogger.logException( "-1021", Ex.getMessage(), Ex );

				}

			}

			Collections.sort( AuxRegisteredServicesManagers, new ServicesManagerComparator() ); //Sort by init priority CAbstractServicesManager.getInitPriority()
			
			for ( CAbstractServicesManager ServicesManagerInstance: AuxRegisteredServicesManagers ) {

				try {
				
					if ( ServicesManagerInstance.initManager( ServicesDaemonConfig ) == true ) {

						RegisteredServicesManagers.add(ServicesManagerInstance);

					}

				} 
				catch ( Error Err ) {
					
					if ( ServicesDaemonLogger != null )
						ServicesDaemonLogger.logError( "-1022", Err.getMessage(), Err );
					
				}
				catch ( Exception Ex ) {

					if ( ServicesDaemonLogger != null )
						ServicesDaemonLogger.logException( "-1023", Ex.getMessage(), Ex );

				}

			}
			
		    bResult = true;
			
		} 
		catch ( Exception Ex ) {

			if ( ServicesDaemonLogger != null )
				ServicesDaemonLogger.logException( "-1012", Ex.getMessage(), Ex );

		}

		return bResult;
    	
    }
    
    protected boolean addConectorsToMainServer( Server MainServer, CConfigServicesDaemon ServicesDaemonConfig, CExtendedLogger ServicesDaemonLogger, CLanguage ServicesDaemonLang ) {
    	
    	boolean bResult = false;
    	
    	try {
    		
        	for ( int intIndexConnector = 0; intIndexConnector < ServicesDaemonConfig.ConfiguredNetworkInterfaces.size(); intIndexConnector++ ) {
        		
        		CConfigNetworkInterface NetInterface = ServicesDaemonConfig.ConfiguredNetworkInterfaces.get( intIndexConnector );
        		
        		//Jetty 9 Style
        		if ( NetInterface.bUseSSL == true ) {

        			HttpConfiguration HTTPS_config = new HttpConfiguration();
        			HTTPS_config.setSecureScheme( "https" );
        			HTTPS_config.setSecurePort( NetInterface.intPort );
        			HTTPS_config.setOutputBufferSize( 32768 );
        			HTTPS_config.setRequestHeaderSize( ServicesDaemonConfig.intMaxRequestHeaderSize );

        			HTTPS_config.addCustomizer(new SecureRequestCustomizer());

        			SslContextFactory ContextFactory = new SslContextFactory();
        			ContextFactory.setKeyStorePath( ServicesDaemonConfig.strKeyStoreFile );
        			ContextFactory.setKeyStorePassword( ServicesDaemonConfig.strKeyStorePassword );
        			ContextFactory.setKeyManagerPassword( ServicesDaemonConfig.strKeyManagerPassword );

        			ServerConnector SSL_Connector = new ServerConnector( MainServer, new SslConnectionFactory( ContextFactory, "http/1.1" ), new HttpConnectionFactory( HTTPS_config ) );
        			//SslSelectChannelConnector SSL_Connector = new SslSelectChannelConnector();

        			SSL_Connector.setIdleTimeout( ServicesDaemonConfig.intMaxIdleTime );
        			//SSL_Connector.setRequestHeaderSize( ServicesDaemonConfig.intMaxRequestHeaderSize );

        			SSL_Connector.setHost( NetInterface.strIP );
        			SSL_Connector.setPort( NetInterface.intPort );

        			//SslContextFactory ContextFactory = SSL_Connector.getSslContextFactory();

        			//ContextFactory.setKeyStorePath( ServicesDaemonConfig.strKeyStoreFile );
        			//ContextFactory.setKeyStorePassword( ServicesDaemonConfig.strKeyStorePassword );
        			//ContextFactory.setKeyManagerPassword( ServicesDaemonConfig.strKeyManagerPassword );

        			MainServer.addConnector( SSL_Connector );

        			}
        			else {

        			HttpConfiguration HTTP_config = new HttpConfiguration();

        			HTTP_config.setRequestHeaderSize( ServicesDaemonConfig.intMaxRequestHeaderSize );

        			ServerConnector Normal_Connector = new ServerConnector( MainServer, new HttpConnectionFactory( HTTP_config ) );

        			//SelectChannelConnector Normal_Connector = new SelectChannelConnector();

        			Normal_Connector.setIdleTimeout( ServicesDaemonConfig.intMaxIdleTime );
        			//Normal_Connector.setRequestHeaderSize( ServicesDaemonConfig.intMaxRequestHeaderSize );

        			Normal_Connector.setHost( NetInterface.strIP );
        			Normal_Connector.setPort( NetInterface.intPort );

        			MainServer.addConnector( Normal_Connector );

        			}        		
        		
        		/* Jetty 8 Style
        		if ( NetInterface.bUseSSL == true ) {
        			
        	        SslSelectChannelConnector SSL_Connector = new SslSelectChannelConnector();
        	        
        	        SSL_Connector.setMaxIdleTime( ServicesDaemonConfig.intMaxIdleTime );
        	        SSL_Connector.setRequestHeaderSize( ServicesDaemonConfig.intMaxRequestHeaderSize );

        	        SSL_Connector.setHost( NetInterface.strIP );
        	        SSL_Connector.setPort( NetInterface.intPort );
        	        
        	        SslContextFactory ContextFactory = SSL_Connector.getSslContextFactory();
        	        
        	        ContextFactory.setKeyStorePath( ServicesDaemonConfig.strKeyStoreFile );
        	        ContextFactory.setKeyStorePassword( Utilities.UncryptString( ConfigXMLTagsServicesDaemon._Password_Crypted, ConfigXMLTagsServicesDaemon._Password_Crypted_Sep, DefaultConstantsCommonClasses.strDefaultCryptAlgorithm, ServicesDaemonConfig.strKeyStorePassword, ServicesDaemonLogger, ServicesDaemonLang ) );
        	        ContextFactory.setKeyManagerPassword( Utilities.UncryptString( ConfigXMLTagsServicesDaemon._Password_Crypted, ConfigXMLTagsServicesDaemon._Password_Crypted_Sep, DefaultConstantsCommonClasses.strDefaultCryptAlgorithm, ServicesDaemonConfig.strKeyManagerPassword, ServicesDaemonLogger, ServicesDaemonLang ) );
        			
        	        MainServer.addConnector( SSL_Connector );
        	        
        		}
        		else {
        			
        	        SelectChannelConnector Normal_Connector = new SelectChannelConnector();
        	        
        	        Normal_Connector.setMaxIdleTime( ServicesDaemonConfig.intMaxIdleTime );
        	        Normal_Connector.setRequestHeaderSize( ServicesDaemonConfig.intMaxRequestHeaderSize );

        	        Normal_Connector.setHost( NetInterface.strIP );
        	        Normal_Connector.setPort( NetInterface.intPort );
        			
        	        MainServer.addConnector( Normal_Connector );

        		}*/
        		
        	}

        	bResult = true;
        	
    	}
    	catch ( Exception Ex ) {
    		
     	    ServicesDaemonLogger.logException( "-1011", Ex.getMessage(), Ex );
     	   
    	}
    	
    	return bResult;
    	
    }

    protected IPAccessHandler createIPAccessControl( CConfigServicesDaemon ServicesDaemonConfig, CExtendedLogger ServicesDaemonLogger, CLanguage ServicesDaemonLang ) {

		IPAccessHandler IPHandler = null;
    	
    	try {
	        
	        
	        if ( ServicesDaemonConfig.ConfiguredAccessControl.size() > 0 ) {
	        	
	        	IPHandler = new IPAccessHandler();
	        	
	        	for ( int intIndexAccessControl = 0; intIndexAccessControl < ServicesDaemonConfig.ConfiguredAccessControl.size(); intIndexAccessControl++ ) {
	        		
	                CConfigAccessControl AccessControl = ServicesDaemonConfig.ConfiguredAccessControl.get( intIndexAccessControl );
	        		
	        		if ( AccessControl.bAlwaysDeny == true )
	        			IPHandler.addBlack( AccessControl.strFromIP + "|" + AccessControl.strContextPath );
	        		else if ( AccessControl.bAlwaysAllow == true )
	                    IPHandler.addWhite( AccessControl.strFromIP + "|" + AccessControl.strContextPath );
	        	
	        	}
	        	
	        	
	        }

    	}
    	catch ( Exception Ex ) {

    		ServicesDaemonLogger.logException( "-1011", Ex.getMessage(), Ex );
     	   
    	}
    	
    	return IPHandler;
    	
    }

    protected ServletContextHandler addHandlersToMainServer( Server MainServer, IPAccessHandler IPHandler, CExtendedLogger ServicesDaemonLogger, CLanguage ServicesDaemonLang ) {
    	
    	ServletContextHandler ServletContext = null;
    	
    	try {

    		HandlerCollection Handlers = new HandlerCollection();
    		RequestLogHandler LogHandlerRequest = new RequestLogHandler();
			
			if ( IPHandler != null ) {
        		
				ServletContext = new ServletContextHandler( ServletContextHandler.SESSIONS );
				ServletContext.setContextPath( "/" );
				IPHandler.setHandler( ServletContext  );
				Handlers.addHandler( IPHandler );

        	}
        	else {

        		ServletContext = new ServletContextHandler( ServletContextHandler.SESSIONS );
        		ServletContext.setContextPath( "/" );
        		Handlers.addHandler( ServletContext );

        	}
		
			
			LogHandlerRequest.setHandler( ServletContext );
			Handlers.addHandler( LogHandlerRequest );

			CCustomErrorHandler CustomErrorHandler = new CCustomErrorHandler();
			
			CustomErrorHandler.setShowStacks( false );
			
			MainServer.addBean( CustomErrorHandler );
			
			File LogAccessDir = new File( this.strRunningPath + ConstantsCommonClasses._Logs_Access_Dir );
			
        	if ( LogAccessDir.exists() == false ) {
			
        		LogAccessDir.mkdirs();
				
			};
			
			NCSARequestLog RequestLog = new NCSARequestLog( this.strRunningPath + ConstantsCommonClasses._Logs_Access_Dir + ConstantsCommonClasses._Main_File_Access_Log );
			RequestLog.setRetainDays( 90 );
			RequestLog.setAppend( true );
			RequestLog.setExtended( true );
			RequestLog.setLogTimeZone( TimeZone.getDefault().getID() ); //"GMT" );
			LogHandlerRequest.setRequestLog( RequestLog );
    		
			//Handlers.addHandler( LogHandlerRequest );

			MainServer.setHandler( Handlers );
			
    	}
    	catch ( Exception Ex ) {

    		ServicesDaemonLogger.logException( "-1010", Ex.getMessage(), Ex );
     	   
    	}
    	
    	return ServletContext;
    	
    }
   
    protected boolean addServicesManagersToMainServer( Server MainServer, ServletContextHandler ServletContext, CConfigServicesDaemon ServicesDaemonConfig, CExtendedLogger ServicesDaemonLogger, CLanguage ServicesDaemonLang ) {
    
    	boolean bResult = false;
    	
    	try {
    	
			CClassPathLoader ClassPathLoader = new CClassPathLoader();
			
		    ClassPathLoader.loadClassFiles( this.strRunningPath + ConstantsCommonClasses._Managers_Dir, ConstantsCommonClasses._Managers_Ext, 2, ServicesDaemonLogger, ServicesDaemonLang );
			
			loadServicesManagersInstances( ServicesDaemonConfig, ServicesDaemonLogger, ServicesDaemonLang );
				
			if ( RegisteredServicesManagers.size() == 0 ) {  
			 
	    		ServicesDaemonLogger.logWarning( "-1",  ServicesDaemonLang.translate( "Using default services manager for context path [%s]", ConstantsCommonConfigXMLTags._Context_Path_Default ) );        
	
	    		CAbstractServicesManager AbstractServicesManager = new CAbstractServicesManager();
	    		
	    		AbstractServicesManager.initManager( ServicesDaemonConfig );
	    		
	    		ServletContext.addServlet( new ServletHolder( AbstractServicesManager ), AbstractServicesManager.getContextPath() );
			
			}
			else {
				
				boolean bNotRootConext = true;
				
				for ( int intIndexRegisteredServicesManager = 0; intIndexRegisteredServicesManager < RegisteredServicesManagers.size(); intIndexRegisteredServicesManager++ ) {
					
					CAbstractServicesManager RegisteredServicesManager = RegisteredServicesManagers.get( intIndexRegisteredServicesManager );
					
					ServletContext.addServlet( new ServletHolder( RegisteredServicesManager ), RegisteredServicesManager.getContextPath() );
				
					if ( RegisteredServicesManager.getContextPath().equals( ConstantsCommonClasses._Servlet_Context_ALL ) ) {

						bNotRootConext = false;
						
					}
					
				}
				
				if ( bNotRootConext == true ) { //catch any request and show blank page
					
					CBlankServicesManager BlankServicesManager = new CBlankServicesManager();
		    		
		    		BlankServicesManager.initManager( ServicesDaemonConfig );
		    		
		    		ServletContext.addServlet( new ServletHolder( BlankServicesManager ), BlankServicesManager.getContextPath() );
					
				}
				
			}

			bResult = true;
    	
    	}
    	catch ( Exception Ex ) {
     	   
    		ServicesDaemonLogger.logException( "-1010", Ex.getMessage(), Ex );
     	   
    	}

    	return bResult;
    	
    }

    protected boolean endServicesManagersFromMainServer( CConfigServicesDaemon ServicesDaemonConfig, CExtendedLogger ServicesDaemonLogger, CLanguage ServicesDaemonLang ) {
        
    	boolean bResult = false;
    	
    	try {
				
			if ( RegisteredServicesManagers.size() > 0 ) {  
			 
	    		for ( int I = RegisteredServicesManagers.size() - 1; I >= 0 ; I-- ) {
	    			
	    		 	CAbstractServicesManager RegisteredServiceManager = RegisteredServicesManagers.get( I );
	    			
	    			RegisteredServiceManager.endManager( ServicesDaemonConfig );
	    			
	    		}
				
			}

			RegisteredServicesManagers.clear();
			
			bResult = true;
    	
    	}
    	catch ( Exception Ex ) {
     	   
    		ServicesDaemonLogger.logException( "-1010", Ex.getMessage(), Ex );
     	   
    	}

    	return bResult;
    	
    }
    
    protected void createSetupAndRunMainServer( CConfigServicesDaemon ServicesDaemonConfig, CExtendedLogger ServicesDaemonLogger, CLanguage ServicesDaemonLang ) {
    	
		try {    

			if ( ServicesDaemonConfig.bLogMissingTranslations == true ) {
			
				CLanguage.LoggerMissingTranslations = CExtendedLogger.getLogger( ConstantsCommonClasses._Logger_Name_Missing_Translations );

				CLanguage.LoggerMissingTranslations.setupLogger( "", false, this.strRunningPath + ConstantsCommonClasses._Logs_System_Dir, ConstantsCommonClasses._Missing_Translations_File_Log, "*.*", false, "ALL", ServicesDaemonConfig.strLogIP, ServicesDaemonConfig.intLogPort, ServicesDaemonConfig.strHTTPLogURL, ServicesDaemonConfig.strHTTPLogUser, ServicesDaemonConfig.strHTTPLogPassword, ServicesDaemonConfig.strProxyIP, ServicesDaemonConfig.intProxyPort, ServicesDaemonConfig.strProxyUser, ServicesDaemonConfig.strProxyPassword );
			
			}

			//Override the default logger class
			System.setProperty( "org.eclipse.jetty.util.log.class", "CommonClasses.CExtendedJettyLogger" );
			
			ServicesDaemonLogger.logMessage( "1", ServicesDaemonLang.translate( "Creating the main server" ) );        
        	MainServer = new Server();
        	
        	if ( addConectorsToMainServer( MainServer, ServicesDaemonConfig, ServicesDaemonLogger, ServicesDaemonLang ) == true ) {
        	
            	IPAccessHandler IPHandler = createIPAccessControl( ServicesDaemonConfig, ServicesDaemonLogger, ServicesDaemonLang );

            	ServletContextHandler ServletContext = addHandlersToMainServer( MainServer, IPHandler, ServicesDaemonLogger, ServicesDaemonLang );
			    	
			    if ( addServicesManagersToMainServer( MainServer, ServletContext, ServicesDaemonConfig, ServicesDaemonLogger, ServicesDaemonLang ) == true ) {    
				
					Timer AtferRunServerTimer = new Timer();
			
					CAtferRunServerTask AtferRunServerTask = new CAtferRunServerTask();
					//AtferRunServerTask.MainServer = MainServer;
					AtferRunServerTask.MessageToProgramRunner = this;
					AtferRunServerTask.AtferRunServerTimer = AtferRunServerTimer;
					AtferRunServerTask.RegisteredServicesManagers = RegisteredServicesManagers;
					AtferRunServerTask.ServicesDaemonConfig = ServicesDaemonConfig;
					AtferRunServerTask.ServicesDaemonLogger = ServicesDaemonLogger;
					AtferRunServerTask.ServicesDaemonLang = ServicesDaemonLang;
					
					AtferRunServerTimer.scheduleAtFixedRate( AtferRunServerTask, 2000, 30000 );
					
			    	//Hardcoded message
					ServicesDaemonLogger.logMessage( "1",  ServicesDaemonLang.translate( "Daemon starting now" ) );        

					MainServer.setStopAtShutdown( true );
					MainServer.setStopTimeout( 3000 );
					
					MainServer.start();
			        MainServer.join();
			    
					AtferRunServerTimer.cancel();
					AtferRunServerTask.cancel();
					
			    }   

			    endServicesManagersFromMainServer( ServicesDaemonConfig, ServicesDaemonLogger, ServicesDaemonLang );
			    
            }

        }
        catch ( Exception Ex ) {
        	
    		ServicesDaemonLogger.logException( "-1011", Ex.getMessage(), Ex ); 
        	
        }
    	
    } 
    
    /*
    protected void testCache( CMasterCacheEngine MasterCacheEngine ) throws Exception {
	 	
    	MasterCacheEngine.addToCache( "", "test01", 100000, "1", ServicesDaemonLogger, ServicesDaemonLang );

	 	MasterCacheEngine.addToCache( "", "test02", 100000, "1", ServicesDaemonLogger, ServicesDaemonLang );

	 	Thread.sleep( 3000 );
		
	 	MasterCacheEngine.replaceOnCache( "", "test01", 100000, "2", ServicesDaemonLogger, ServicesDaemonLang );

	 	MasterCacheEngine.removeFromCache( "", "test02", ServicesDaemonLogger, ServicesDaemonLang );
	 	
	 	String strValue = (String) MasterCacheEngine.getFromCache( "", "test01", ServicesDaemonLogger, ServicesDaemonLang );
	 	
	 	System.out.println( "tets01 => " + strValue );
	 	
	 	strValue = (String) MasterCacheEngine.getFromCache( "", "test02", ServicesDaemonLogger, ServicesDaemonLang );
	 	
	 	System.out.println( "tets02 => " + strValue );
	 	
	 	Future<Object> F = MasterCacheEngine.asyncGetFromCache( "", "test01", ServicesDaemonLogger, ServicesDaemonLang );
	 	
	 	strValue = (String) F.get();
	 	
	 	System.out.println( "tets01 => " + strValue );
	 	
    }
    */
    
    public int runProgram( String strRunningPath, String[] args ) throws Exception {
    	
		this.strRunningPath = strRunningPath;

		InitArgs = new ArrayList<String>( Arrays.asList( args ) );
		
		ServicesDaemonLogger = CExtendedLogger.getLogger( ConstantsCommonClasses._Logger_Name );
		ServicesDaemonLogger.setupLogger( "", InitArgs.contains( InitArgsConstants._LogToScreen ), this.strRunningPath + ConstantsCommonClasses._Logs_System_Dir, ConstantsCommonClasses._Main_File_Log, ConstantsCommonClasses._Log_Class_Method, ConstantsCommonClasses._Log_Exact_Match, ConstantsCommonClasses._Log_Level, "", -1, "", "", "", "", -1, "", "" );

		CLanguage CommonlLang = CLanguage.getLanguage( ServicesDaemonLogger, this.strRunningPath + ConstantsCommonClasses._Langs_Dir + ConstantsCommonClasses._Common_Lang_File ); 
		
		CLanguage.addLanguageToCommonPhrases( CommonlLang );
		
		ServicesDaemonLang = CLanguage.getLanguage( ServicesDaemonLogger, this.strRunningPath + ConstantsCommonClasses._Langs_Dir + ConstantsCommonClasses._Main_File + "." + ConstantsCommonClasses._Lang_Ext );
		
		ServicesDaemonLogger.logMessage( "1", ServicesDaemonLang.translate( "Running dir: [%s]", this.strRunningPath ) );        
		ServicesDaemonLogger.logMessage( "1", ServicesDaemonLang.translate( "Version: [%s]", strVersion ) );        

		GlobalConfig = CConfigServicesDaemon.getConfigServicesDaemon( this.strRunningPath );

		//Initialize the master cache engine and all cache engines implementations
	 	CMasterCacheEngine MasterCacheEngine = CMasterCacheEngine.getMasterCacheEngine();
	 	
	 	MasterCacheEngine.loadAndRegisterCacheEnginesFromDir( this.strRunningPath + ConstantsCommonClasses._Cache_Engines_Dir, null, ServicesDaemonLogger, ServicesDaemonLang );
		
	 	//this.testCache( MasterCacheEngine );
	 	
		if ( GlobalConfig.loadConfig( this.strRunningPath + ConstantsCommonClasses._Conf_File, ServicesDaemonLogger, ServicesDaemonLang ) == true ) {
        	
		 	GlobalConfig.InitArgs = InitArgs; //Save the init arguments
			
			ServicesDaemonLogger.logMessage( "1", ServicesDaemonLang.translate( "Config Ok" ) );        

    		createSetupAndRunMainServer( GlobalConfig, ServicesDaemonLogger, ServicesDaemonLang );

        }
        else {
        	
			//Hardcoded error
    		ServicesDaemonLogger.logError( "-1000", ServicesDaemonLang.translate( "Failed to load config file" ) );        
        	
        };

		//Hardcoded message
		ServicesDaemonLogger.logMessage( "1", ServicesDaemonLang.translate( "Daemon ending now" ) );        
        
		return intRunCode;
		
	}

	@Override
	public Object sendMessage( String strMessageName, Object MessageData ) {

		if ( strMessageName.equals( ConstantsMessagesCodes._Shutdown ) || strMessageName.equals( ConstantsMessagesCodes._Restart ) ) {
			
			if ( MainServer != null && MainServer.isRunning() ) {
				
				if ( strMessageName.equals( "restart" ) ) {
					
					intRunCode = 1000;
					
				}

				try {
				
					MainServer.stop();
				
				} 
				catch ( Exception Ex ) {
					
		    		ServicesDaemonLogger.logException( "-1020", Ex.getMessage(), Ex );        
					
				} 
				
			}

			return 1000;
			
		}
		else if ( strMessageName.equals( ConstantsMessagesCodes._Server_Running ) ) {
			
			if ( MainServer != null ) {
				
				return true;
				
			}
			else {
				
				return false;
				
			}
			
		}
		else if ( strMessageName.equals( ConstantsMessagesCodes._Running_Path ) ) {
			
			return strRunningPath;
			
		}
		
		return "";
		
	}
    
}
