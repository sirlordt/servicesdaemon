package BPServicesManager;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ServiceLoader;

import AbstractResponseFormat.CAbstractResponseFormat;
import AbstractResponseFormat.CCSVResponseFormat;
import AbstractResponseFormat.CJSONResponseFormat;
import AbstractResponseFormat.CJavaXMLWebRowSetResponseFormat;
import AbstractResponseFormat.CXMLDataPacketResponseFormat;
import AbstractService.CAbstractService;
import AbstractServicesManager.CAbstractServicesManager;
import CommonClasses.CClassPathLoader;
import CommonClasses.CLanguage;
import CommonClasses.CServicesDaemonConfig;
import CommonClasses.DefaultConstantsServicesDaemon;
import CommonClasses.InitArgsConstants;
import ExtendedLogger.CExtendedLogger;

public class CBPServicesManager extends CAbstractServicesManager {

	private static final long serialVersionUID = -1535559412203731810L;

    public static final String strVersion = "0.0.0.1";

	public static CBPServicesManagerConfig BPServicesManagerConfig = null;;
    
	public static HashMap<String,CAbstractService> RegisteredBPServices = null;

    protected HashMap<String,String> SecurityTokenIdToDBSecurityTokenId;
    
    static {
    	
    	RegisteredBPServices = new HashMap<String,CAbstractService>();
    	
    }
	
	public CBPServicesManager() {

		super();
		
		SecurityTokenIdToDBSecurityTokenId = new HashMap<String,String>();
		
        this.strContextPath = "/BPServices";
        
	}

    public final static String getJarFolder() {

        String name =  CBPServicesManager.class.getCanonicalName().replace( '.', '/' );

        String s = CBPServicesManager.class.getClass().getResource( "/" + name + ".class" ).toString();

        s = s.replace( '/', File.separatorChar );

        if ( s.indexOf(".jar") >= 0 )
           s = s.substring( 0, s.indexOf(".jar") + 4 );
        else
           s = s.substring( 0, s.indexOf(".class") );

        if ( s.indexOf( "jar:file:\\" )  == 0 ) { //Windows style path SO inside jar file 

        	s = s.substring( 10 );

        }
        else if ( s.indexOf( "file:\\" )  == 0 ) { //Windows style path SO .class file

        	s = s.substring( 6 );

        }
        else { //Unix family ( Linux/BSD/Mac/Solaris ) style path SO

            s = s.substring( s.lastIndexOf(':') + 1 );

        }

        return s.substring( 0, s.lastIndexOf( File.separatorChar ) + 1 );

    }

    public boolean LoadAndRegisterResponsesFormats( CServicesDaemonConfig ServicesDaemonConfig ) {
    	
		boolean bResult = false;

		try {
			
			ServiceLoader<CAbstractResponseFormat> sl = ServiceLoader.load( CAbstractResponseFormat.class );
			sl.reload();

			CAbstractResponseFormat.ClearRegisteredResponseFormat();

			Iterator<CAbstractResponseFormat> it = sl.iterator();

			while ( it.hasNext() ) {

				try {
					
					CAbstractResponseFormat ResponseFormatInstance = it.next();

					if ( ResponseFormatInstance.InitResponseFormat( ServicesDaemonConfig, BPServicesManagerConfig ) == true ) {
					   
						CAbstractResponseFormat.ResigterResponseFormat( ResponseFormatInstance );

						BPServicesManagerConfig.Logger.LogMessage( "1", BPServicesManagerConfig.Lang.Translate( "Registered response format: [%s] min version: [%s] max version: [%s]", ResponseFormatInstance.getName(), ResponseFormatInstance.getMinVersion(), ResponseFormatInstance.getMaxVersion() ) );        
					
					}
					
				} 
				catch ( Exception Ex ) {

					if ( BPServicesManagerConfig.Logger != null )
						BPServicesManagerConfig.Logger.LogException( "-1011", Ex.getMessage(), Ex );

				}

			}

		    //Add to the end of list the built response formats 
			BPServicesManagerConfig.Logger.LogMessage( "1", BPServicesManagerConfig.Lang.Translate( "Adding to end of list the built in responses formats" ) );        

			CXMLDataPacketResponseFormat XMLDataPacketResponseFormat = new CXMLDataPacketResponseFormat();
			
			if ( XMLDataPacketResponseFormat.InitResponseFormat( ServicesDaemonConfig, BPServicesManagerConfig ) == true ) {
				   
				CAbstractResponseFormat.ResigterResponseFormat( XMLDataPacketResponseFormat );
				BPServicesManagerConfig.Logger.LogMessage( "1", BPServicesManagerConfig.Lang.Translate( "Added built in response format [%s] min version: [%s] max version: [%s]", XMLDataPacketResponseFormat.getName(), XMLDataPacketResponseFormat.getMinVersion(), XMLDataPacketResponseFormat.getMaxVersion() ) );        
			
			}

			CJavaXMLWebRowSetResponseFormat JavaXMLWebRowSetResponseFormat = new CJavaXMLWebRowSetResponseFormat();
			
			if ( JavaXMLWebRowSetResponseFormat.InitResponseFormat( ServicesDaemonConfig, BPServicesManagerConfig ) == true ) {
				   
				CAbstractResponseFormat.ResigterResponseFormat( JavaXMLWebRowSetResponseFormat );
				BPServicesManagerConfig.Logger.LogMessage( "1", BPServicesManagerConfig.Lang.Translate( "Added built in response format [%s] min version: [%s] max version: [%s]", JavaXMLWebRowSetResponseFormat.getName(), JavaXMLWebRowSetResponseFormat.getMinVersion(), JavaXMLWebRowSetResponseFormat.getMaxVersion() ) );        
			
			}
			
			CJSONResponseFormat JSONResponseFormat = new CJSONResponseFormat(); //JSON
			
			if ( JSONResponseFormat.InitResponseFormat( ServicesDaemonConfig, BPServicesManagerConfig ) == true ) {
				   
				CAbstractResponseFormat.ResigterResponseFormat( JSONResponseFormat );
				BPServicesManagerConfig.Logger.LogMessage( "1", BPServicesManagerConfig.Lang.Translate( "Added built in response format [%s] min version: [%s] max version: [%s]", JSONResponseFormat.getName(), JSONResponseFormat.getMinVersion(), JSONResponseFormat.getMaxVersion() ) );        
			
			}
			
			CCSVResponseFormat CSVResponseFormat = new CCSVResponseFormat(); //CSV
			
			if ( CSVResponseFormat.InitResponseFormat( ServicesDaemonConfig, BPServicesManagerConfig ) == true ) {
				   
				CAbstractResponseFormat.ResigterResponseFormat( CSVResponseFormat );
				BPServicesManagerConfig.Logger.LogMessage( "1", BPServicesManagerConfig.Lang.Translate( "Added built in response format [%s] min version: [%s] max version: [%s]", CSVResponseFormat.getName(), CSVResponseFormat.getMinVersion(), CSVResponseFormat.getMaxVersion() ) );        
			
			}
			
			CAbstractResponseFormat DefaultResponseFormat = CAbstractResponseFormat.getResponseFomat( BPServicesManagerConfig.strDefaultResponseFormat, BPServicesManagerConfig.strDefaultResponseFormatVersion );

			if ( DefaultResponseFormat == null ) {
				
				if ( CAbstractResponseFormat.getReponseFormatSearchCodeResult() == -2 ) { //Response format name not found 
				
					BPServicesManagerConfig.Logger.LogWarning( "1", BPServicesManagerConfig.Lang.Translate( "The default response format [$s] version [%s] not found", BPServicesManagerConfig.strDefaultResponseFormat, BPServicesManagerConfig.strDefaultResponseFormatVersion ) );
					
					BPServicesManagerConfig.strDefaultResponseFormat = DefaultConstantsBPServicesManager.strDefaultResponseFormat;
					BPServicesManagerConfig.strDefaultResponseFormatVersion = DefaultConstantsBPServicesManager.strDefaultResponseFormatVersion;
				
				}
				else { //Response format name found but the version not match, use the response format with the min version available
					
					BPServicesManagerConfig.Logger.LogWarning( "1", BPServicesManagerConfig.Lang.Translate( "The default response format [$s] found, but the version [%s] not found, using the min version [%s] available", BPServicesManagerConfig.strDefaultResponseFormat, BPServicesManagerConfig.strDefaultResponseFormatVersion, CAbstractResponseFormat.getReponseFormatVersionSearchResult() ) );
					
					BPServicesManagerConfig.strDefaultResponseFormatVersion = CAbstractResponseFormat.getReponseFormatVersionSearchResult();
					
				}
				
			}
			
			BPServicesManagerConfig.Logger.LogMessage( "1", BPServicesManagerConfig.Lang.Translate( "Using default response format: [$s] min version: [%s]", BPServicesManagerConfig.strDefaultResponseFormat, BPServicesManagerConfig.strDefaultResponseFormatVersion ) );

			BPServicesManagerConfig.Logger.LogMessage( "1", BPServicesManagerConfig.Lang.Translate( "Count of responses formats registered: [%s]", Integer.toString( CAbstractResponseFormat.GetCountRegisteredResponsesFormats() ) ) );        

			bResult = CAbstractResponseFormat.GetCountRegisteredResponsesFormats() > 0;
			
		} 
		catch ( Exception Ex ) {

			if ( BPServicesManagerConfig.Logger != null )
				BPServicesManagerConfig.Logger.LogException( "-1012", Ex.getMessage(), Ex );

		}

		return bResult;
    	
    }
    
    public boolean LoadAndRegisterBPServices( CServicesDaemonConfig ServicesDaemonConfig ) {
    	
    	boolean bResult = false;

    	try {

			ServiceLoader<CAbstractService> sl = ServiceLoader.load( CAbstractService.class );
			sl.reload();

			RegisteredBPServices.clear();

			Iterator<CAbstractService> it = sl.iterator();

			while ( it.hasNext() ) {

				try {
					
					CAbstractService ServiceInstance = it.next();

					if ( ServiceInstance.InitializeService( ServicesDaemonConfig, BPServicesManagerConfig ) == true ) {
					   
						RegisteredBPServices.put( ServiceInstance.getServiceName().toLowerCase(), ServiceInstance );

						BPServicesManagerConfig.Logger.LogMessage( "1", BPServicesManagerConfig.Lang.Translate( "Registered service: [%s] description: [%s] version: [%s]", ServiceInstance.getServiceName().toLowerCase(), ServiceInstance.getServiceDescription(), ServiceInstance.getServiceVersion() ) );        
					
					}
					
				} 
				catch ( Exception Ex ) {

					if ( BPServicesManagerConfig.Logger != null )
						BPServicesManagerConfig.Logger.LogException( "-1011", Ex.getMessage(), Ex );

				}

			}
    		
			BPServicesManagerConfig.Logger.LogMessage( "1", BPServicesManagerConfig.Lang.Translate( "Count of services registered: [%s]", Integer.toString( RegisteredBPServices.size() ) ) );        

			bResult = RegisteredBPServices.size() > 0;

    	} 
		catch ( Exception Ex ) {
	
			if ( BPServicesManagerConfig.Logger != null )
				BPServicesManagerConfig.Logger.LogException( "-1010", Ex.getMessage(), Ex );
	
		}
    	
    	return bResult;
    	
    }
    
    public boolean InitManager( CServicesDaemonConfig ServicesDaemonConfig ) {
    	
    	super.InitManager( ServicesDaemonConfig );

    	DefaultConstantsBPServicesManager.strDefaultRunningPath = CBPServicesManager.getJarFolder();
    	
        CExtendedLogger BPServicesManagerLogger = CExtendedLogger.getLogger( DefaultConstantsBPServicesManager.strDefaultLoggerName );
        BPServicesManagerLogger.SetupLogger( ServicesDaemonConfig.InitArgs.contains( InitArgsConstants._LogToScreen ), DefaultConstantsBPServicesManager.strDefaultRunningPath + DefaultConstantsBPServicesManager.strDefaultLogsSystemDir, DefaultConstantsBPServicesManager.strDefaultMainFileLog, ServicesDaemonConfig.strClassNameMethodName, ServicesDaemonConfig.bExactMatch, ServicesDaemonConfig.LoggingLevel.toString() );
		
		CLanguage BPServicesManagerLang = CLanguage.getLanguage( BPServicesManagerLogger, DefaultConstantsBPServicesManager.strDefaultRunningPath + CommonClasses.DefaultConstantsServicesDaemon.strDefaultLangsDir + DefaultConstantsBPServicesManager.strDefaultMainFile + "." + ServicesDaemonConfig.strDefaultLang );

		BPServicesManagerLogger.LogMessage( "1", BPServicesManagerLang.Translate( "Running dir: [%s]", DefaultConstantsBPServicesManager.strDefaultRunningPath ) );        
		BPServicesManagerLogger.LogMessage( "1", BPServicesManagerLang.Translate( "Version: [%s]", strVersion ) );        
    	
    	BPServicesManagerConfig = CBPServicesManagerConfig.getBPServicesManagerConfig();
		
    	boolean bResult = false;
    	
    	if ( BPServicesManagerConfig.LoadConfig( DefaultConstantsBPServicesManager.strDefaultRunningPath + DefaultConstantsBPServicesManager.strDefaultConfFile, BPServicesManagerLang, BPServicesManagerLogger ) == true ) {
    		
    		try {
    		
    			CClassPathLoader ClassPathLoader = new CClassPathLoader( BPServicesManagerLogger, BPServicesManagerLang );

				//Load responses formats class
				ClassPathLoader.LoadClassFiles( BPServicesManagerConfig.strResponsesFormatsDir, DefaultConstantsServicesDaemon.strDefaultLibsExt, 2 );

				if ( this.LoadAndRegisterResponsesFormats( ServicesDaemonConfig ) == true ) {

	    			//Load the business process services class
	    			ClassPathLoader.LoadClassFiles( BPServicesManagerConfig.strBPServicesDir, DefaultConstantsServicesDaemon.strDefaultLibsExt, 2 );

	    			if ( this.LoadAndRegisterBPServices( ServicesDaemonConfig ) == true ) {

						bResult = true;

					}	
					else {

						BPServicesManagerLogger.LogError( "-1002", BPServicesManagerLang.Translate( "No business process services found in path [%s]", BPServicesManagerConfig.strBPServicesDir ) );

					}
					
				}
				else {

					BPServicesManagerLogger.LogError( "-1001", BPServicesManagerLang.Translate( "No responses formats drivers found in path [%s]", BPServicesManagerConfig.strResponsesFormatsDir ) );

				}
				
    		}
    		catch ( Exception Ex ) {
    		
    			BPServicesManagerLogger.LogException( "-1010", Ex.getMessage(), Ex );
    			
    		}
    		
    	}
    	
    	return bResult;
    	
    }
    
}
