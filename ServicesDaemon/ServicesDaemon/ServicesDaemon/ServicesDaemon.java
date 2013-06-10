package ServicesDaemon;

import java.io.File;
//import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.TimeZone;

//import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.NCSARequestLog;
import org.eclipse.jetty.server.Server;
//import org.eclipse.jetty.server.handler.ContextHandlerCollection;
//import org.eclipse.jetty.server.handler.DefaultHandler;
//import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
//import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.IPAccessHandler;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.server.ssl.SslSelectChannelConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import AbstractServicesManager.*;
import CommonClasses.CClassPathLoader;
import CommonClasses.CConfigAccessControl;
import CommonClasses.CConfigNetworkInterface;
import CommonClasses.CCustomErrorHandler;
import CommonClasses.CLanguage;
import CommonClasses.CServicesDaemonConfig;
import CommonClasses.ConfigXMLTagsServicesDaemon;
import CommonClasses.DefaultConstantsServicesDaemon;
import CommonClasses.InitArgsConstants;
import ExtendedLogger.CExtendedLogger;

public class ServicesDaemon {

    static ArrayList<String> InitArgs = null;
    static ArrayList<CAbstractServicesManager> RegisteredServicesManagers = null;

    public static final String strVersion = "0.0.0.1";
    
    static {
    	
    	RegisteredServicesManagers = new ArrayList<CAbstractServicesManager>();
    	
    }

    public static boolean LoadServicesManagersInstances( CServicesDaemonConfig ServicesDaemonConfig, CExtendedLogger ServicesDaemonLogger ) {
    	
		boolean bResult = false;

		try {
			
			ServiceLoader<CAbstractServicesManager> sl = ServiceLoader.load( CAbstractServicesManager.class );
			sl.reload();

			RegisteredServicesManagers.clear();

			Iterator<CAbstractServicesManager> it = sl.iterator();

			while ( it.hasNext() ) {

				try {

					CAbstractServicesManager ServicesManagerInstance = it.next();

					if ( ServicesManagerInstance.InitManager( ServicesDaemonConfig ) == true ) {
					
					   RegisteredServicesManagers.add(ServicesManagerInstance);
					
					}

				} 
				catch ( Exception Ex ) {

					if ( ServicesDaemonLogger != null )
						ServicesDaemonLogger.LogException( "-1011", Ex.getMessage(), Ex );

				}

			}

		    bResult = true;
			
		} 
		catch ( Exception Ex ) {

			if ( ServicesDaemonLogger != null )
				ServicesDaemonLogger.LogException( "-1012", Ex.getMessage(), Ex );

		}

		return bResult;
    	
    }
    
    public static boolean AddConectorsToMainServer( Server MainServer, CServicesDaemonConfig ServicesDaemonConfig, CExtendedLogger ServicesDaemonLogger ) {
    	
    	boolean bResult = false;
    	
    	try {
    		
        	for ( int intIndexConnector = 0; intIndexConnector < ServicesDaemonConfig.ConfiguredNetworkInterfaces.size(); intIndexConnector++ ) {
        		
        		CConfigNetworkInterface NetInterface = ServicesDaemonConfig.ConfiguredNetworkInterfaces.get( intIndexConnector );
        		
        		if ( NetInterface.bUseSSL == true ) {
        			
        	        SslSelectChannelConnector SSL_Connector = new SslSelectChannelConnector();
        	        
        	        SSL_Connector.setMaxIdleTime( ServicesDaemonConfig.intMaxIdleTime );
        	        SSL_Connector.setRequestHeaderSize( ServicesDaemonConfig.intMaxRequestHeaderSize );

        	        SSL_Connector.setHost( NetInterface.strIP );
        	        SSL_Connector.setPort( NetInterface.intPort );
        	        
        	        SslContextFactory ContextFactory = SSL_Connector.getSslContextFactory();
        	        
        	        ContextFactory.setKeyStorePath( ServicesDaemonConfig.strKeyStoreFile );
        	        ContextFactory.setKeyStorePassword( ServicesDaemonConfig.strKeyStorePassword );
        	        ContextFactory.setKeyManagerPassword( ServicesDaemonConfig.strKeyManagerPassword );
        			
        	        MainServer.addConnector( SSL_Connector );
        	        
        		}
        		else {
        			
        	        SelectChannelConnector Normal_Connector = new SelectChannelConnector();
        	        
        	        Normal_Connector.setMaxIdleTime( ServicesDaemonConfig.intMaxIdleTime );
        	        Normal_Connector.setRequestHeaderSize( ServicesDaemonConfig.intMaxRequestHeaderSize );

        	        Normal_Connector.setHost( NetInterface.strIP );
        	        Normal_Connector.setPort( NetInterface.intPort );
        			
        	        MainServer.addConnector( Normal_Connector );

        		}
        		
        	}

        	bResult = true;
        	
    	}
    	catch ( Exception Ex ) {
    		
     	    ServicesDaemonLogger.LogException( "-1011", Ex.getMessage(), Ex );
     	   
    	}
    	
    	return bResult;
    	
    }

    public static IPAccessHandler CreateIPAccessControl( CServicesDaemonConfig ServicesDaemonConfig, CExtendedLogger ServicesDaemonLogger ) {

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

    		ServicesDaemonLogger.LogException( "-1011", Ex.getMessage(), Ex );
     	   
    	}
    	
    	return IPHandler;
    	
    }

    public static ServletContextHandler AddHandlersToMainServer( Server MainServer, IPAccessHandler IPHandler, CExtendedLogger ServicesDaemonLogger ) {
    	
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
			
			File LogAccessDir = new File( DefaultConstantsServicesDaemon.strDefaultRunningPath + DefaultConstantsServicesDaemon.strDefaultLogsAccessDir );
			
        	if ( LogAccessDir.exists() == false ) {
			
        		LogAccessDir.mkdirs();
				
			};
			
			NCSARequestLog RequestLog = new NCSARequestLog( DefaultConstantsServicesDaemon.strDefaultRunningPath + DefaultConstantsServicesDaemon.strDefaultLogsAccessDir + DefaultConstantsServicesDaemon.strDefaultMainFileAccessLog );
			RequestLog.setRetainDays( 90 );
			RequestLog.setAppend( true );
			RequestLog.setExtended( true );
			RequestLog.setLogTimeZone( TimeZone.getDefault().getID() ); //"GMT" );
			LogHandlerRequest.setRequestLog( RequestLog );
    		
			//Handlers.addHandler( LogHandlerRequest );

			MainServer.setHandler( Handlers );
			
    	}
    	catch ( Exception Ex ) {

    		ServicesDaemonLogger.LogException( "-1010", Ex.getMessage(), Ex );
     	   
    	}
    	
    	return ServletContext;
    	
    }
   
    public static boolean AddServicesManagersToMainServer( Server MainServer, ServletContextHandler ServletContext, CServicesDaemonConfig ServicesDaemonConfig, CExtendedLogger ServicesDaemonLogger, CLanguage ServicesDaemonLang ) {
    
    	boolean bResult = false;
    	
    	try {
    	
			CClassPathLoader ClassPathLoader = new CClassPathLoader( ServicesDaemonLogger, ServicesDaemonLang );
			
		    ClassPathLoader.LoadClassFiles( DefaultConstantsServicesDaemon.strDefaultRunningPath + DefaultConstantsServicesDaemon.strDefaultManagersDir, DefaultConstantsServicesDaemon.strDefaultManagersExt, 2 );
			
			LoadServicesManagersInstances( ServicesDaemonConfig, ServicesDaemonLogger );
				
			if ( RegisteredServicesManagers.size() == 0 ) {  
			 
	    		ServicesDaemonLogger.LogWarning( "-1",  ServicesDaemonLang.Translate( "Using default services manager for context path [%s]", ConfigXMLTagsServicesDaemon._Context_Path_Default ) );        
	
	    		CAbstractServicesManager AbstractServicesManager = new CAbstractServicesManager();
	    		
	    		AbstractServicesManager.InitManager( ServicesDaemonConfig );
	    		
	    		ServletContext.addServlet( new ServletHolder( AbstractServicesManager ), AbstractServicesManager.getContextPath() );
			
			}
			else {
				
				boolean bNotRootConext = true;
				
				for ( int intIndexRegisteredServicesManager = 0; intIndexRegisteredServicesManager < RegisteredServicesManagers.size(); intIndexRegisteredServicesManager++ ) {
					
					CAbstractServicesManager RegisteredServicesManager = RegisteredServicesManagers.get( intIndexRegisteredServicesManager );
					
					ServletContext.addServlet( new ServletHolder( RegisteredServicesManager ), RegisteredServicesManager.getContextPath() );
				
					if ( RegisteredServicesManager.getContextPath().equals( DefaultConstantsServicesDaemon.strDefaultServletContext ) ) {

						bNotRootConext = false;
						
					}
					
				}
				
				if ( bNotRootConext == true ) { //catch any request and show blank page
					
					CBlankServicesManager BlankServicesManager = new CBlankServicesManager();
		    		
		    		BlankServicesManager.InitManager( ServicesDaemonConfig );
		    		
		    		ServletContext.addServlet( new ServletHolder( BlankServicesManager ), BlankServicesManager.getContextPath() );
					
				}
				
			}

			bResult = true;
    	
    	}
    	catch ( Exception Ex ) {
     	   
    		ServicesDaemonLogger.LogException( "-1010", Ex.getMessage(), Ex );
     	   
    	}

    	return bResult;
    	
    }
    
    public static void CreateSetupAndRunMainServer( CServicesDaemonConfig ServicesDaemonConfig, CExtendedLogger ServicesDaemonLogger, CLanguage ServicesDaemonLang ) {
    	
		try {    

			if ( ServicesDaemonConfig.bLogMissingTranslations == true ) {
			
				CLanguage.LoggerMissingTranslations = CExtendedLogger.getLogger( DefaultConstantsServicesDaemon.strDefaultLoggerNameMissingTranslations );

				CLanguage.LoggerMissingTranslations.SetupLogger( false, DefaultConstantsServicesDaemon.strDefaultRunningPath + DefaultConstantsServicesDaemon.strDefaultLogsSystemDir, DefaultConstantsServicesDaemon.strDefaultMisssingTranslationsFileLog, "*.*", false, "ALL" );
			
			}

			//Override the default logger class
			System.setProperty( "org.eclipse.jetty.util.log.class", "CommonClasses.CExtendedJettyLogger" );
			
			ServicesDaemonLogger.LogMessage( "1", ServicesDaemonLang.Translate( "Creating the main server" ) );        
        	Server MainServer = new Server();
        	
        	if ( AddConectorsToMainServer( MainServer, ServicesDaemonConfig, ServicesDaemonLogger ) == true ) {
        	
            	IPAccessHandler IPHandler = CreateIPAccessControl( ServicesDaemonConfig, ServicesDaemonLogger );

            	ServletContextHandler ServletContext = AddHandlersToMainServer( MainServer, IPHandler, ServicesDaemonLogger );
			    	
			    if ( AddServicesManagersToMainServer( MainServer, ServletContext, ServicesDaemonConfig, ServicesDaemonLogger, ServicesDaemonLang) == true ) {    
				
			    	//Hardcoded message
					ServicesDaemonLogger.LogMessage( "1",  ServicesDaemonLang.Translate( "Daemon starting now" ) );        
			
					MainServer.start();
			        MainServer.join();
			    
			    }    

            }

        }
        catch ( Exception Ex ) {
        	
    		ServicesDaemonLogger.LogException( "-1011", Ex.getMessage(), Ex ); 
        	
        }
    	
    } 
    
    public final static String getJarFolder() {

        String name = ServicesDaemon.class.getCanonicalName().replace( '.', '/' );

        String s = ServicesDaemon.class.getClass().getResource( "/" + name + ".class" ).toString();

        s = s.replace( '/', File.separatorChar );

        if ( s.indexOf(".jar") >= 0 )
           s = s.substring( 0, s.indexOf(".jar") + 4 );
        else
           s = s.substring( 0, s.indexOf(".class") );

        s = s.substring( s.indexOf(':') + 2 );

        return s.substring( 0, s.lastIndexOf( File.separatorChar ) + 1 );

    }

    public static void main(String[] args) throws Exception {
	    	    
		DefaultConstantsServicesDaemon.strDefaultRunningPath = getJarFolder();

		InitArgs = new ArrayList<String>( Arrays.asList( args ) );
		
		CExtendedLogger ServicesDaemonLogger = CExtendedLogger.getLogger( DefaultConstantsServicesDaemon.strDefaultLoggerName );
		ServicesDaemonLogger.SetupLogger( InitArgs.contains( InitArgsConstants._LogToScreen ), DefaultConstantsServicesDaemon.strDefaultRunningPath + DefaultConstantsServicesDaemon.strDefaultLogsSystemDir, DefaultConstantsServicesDaemon.strDefaultMainFileLog, DefaultConstantsServicesDaemon.strDefaultLogClassMethod, DefaultConstantsServicesDaemon.bDefaultLogExactMatch, DefaultConstantsServicesDaemon.strDefaultLogLevel );

		CLanguage CommonlLang = CLanguage.getLanguage( ServicesDaemonLogger, DefaultConstantsServicesDaemon.strDefaultRunningPath + DefaultConstantsServicesDaemon.strDefaultLangsDir + DefaultConstantsServicesDaemon.strDefaultCommonLang ); 
		
		CLanguage.addLanguageToCommonPhrases( CommonlLang );
		
		CLanguage ServicesDaemonLang = CLanguage.getLanguage( ServicesDaemonLogger, DefaultConstantsServicesDaemon.strDefaultRunningPath + DefaultConstantsServicesDaemon.strDefaultLangsDir + DefaultConstantsServicesDaemon.strDefaultMainFile + "." + DefaultConstantsServicesDaemon.strDefaultLang );
		
		ServicesDaemonLogger.LogMessage( "1", ServicesDaemonLang.Translate( "Running dir: [%s]", DefaultConstantsServicesDaemon.strDefaultRunningPath ) );        
		ServicesDaemonLogger.LogMessage( "1", ServicesDaemonLang.Translate( "Version: [%s]", strVersion ) );        

		CServicesDaemonConfig GlobalConfig = CServicesDaemonConfig.getServicesDaemonConfig();

		if ( GlobalConfig.LoadConfig(  DefaultConstantsServicesDaemon.strDefaultRunningPath + DefaultConstantsServicesDaemon.strDefaultConfFile, ServicesDaemonLang, ServicesDaemonLogger ) == true ) {
        	
    		GlobalConfig.InitArgs = InitArgs; //Save the init arguments
			
			ServicesDaemonLogger.LogMessage( "1", ServicesDaemonLang.Translate( "Config Ok" ) );        

    		CreateSetupAndRunMainServer( GlobalConfig, ServicesDaemonLogger, ServicesDaemonLang );

        }
        else {
        	
			//Hardcoded error
    		ServicesDaemonLogger.LogError( "-1000", ServicesDaemonLang.Translate( "Failed to load config file" ) );        
        	
        };

		//Hardcoded message
		ServicesDaemonLogger.LogMessage( "1", ServicesDaemonLang.Translate( "Daemon ending now" ) );        
        
	}
    
}
