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
package DBServicesManager;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ServiceLoader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import AbstractDBEngine.CAbstractDBEngine;
import AbstractResponseFormat.CAbstractResponseFormat;
import AbstractResponseFormat.CCSVResponseFormat;
import AbstractResponseFormat.CJSONResponseFormat;
import AbstractResponseFormat.CJavaXMLWebRowSetResponseFormat;
import AbstractResponseFormat.CXMLDataPacketResponseFormat;
import AbstractService.CAbstractService;
import AbstractService.ConstantsServicesTags;
import AbstractServicesManager.CAbstractServicesManager;
import CommonClasses.CClassPathLoader;
import CommonClasses.CDBConnectionsManager;
import CommonClasses.CLanguage;
import CommonClasses.CSecurityTokensManager;
import CommonClasses.CServicesDaemonConfig;
import CommonClasses.ConfigXMLTagsServicesDaemon;
import CommonClasses.DefaultConstantsServicesDaemon;
import CommonClasses.InitArgsConstants;
import ExtendedLogger.CExtendedLogger;

public class CDBServicesManager extends CAbstractServicesManager {
	
	private static final long serialVersionUID = 5474999115273351038L;

    public static final String strVersion = "0.0.0.1";
	
	public static CDBServicesManagerConfig DBServicesManagerConfig = null;;
	
	public static HashMap<String,CAbstractService> RegisteredDBServices = null;
	
    protected CDBConnectionsManager DBConnectionsManager;
	
    protected CSecurityTokensManager SecurityTokensManager;
    
    static {
    	
    	RegisteredDBServices = new HashMap<String,CAbstractService>();
    	
    }
	
	public CDBServicesManager() {
		
        super();		

        DBConnectionsManager = CDBConnectionsManager.getDBConnectionManager();
        
        SecurityTokensManager = CSecurityTokensManager.getSecurityTokensManager();
        
        this.strContextPath = "/DBServices";
        
	}
	
    public final static String getJarFolder() {

        String name =  CDBServicesManager.class.getCanonicalName().replace( '.', '/' );

        String s = CDBServicesManager.class.getClass().getResource( "/" + name + ".class" ).toString();

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
    
    public boolean LoadAndRegisterDBEngines( CServicesDaemonConfig ServicesDaemonConfig ) {
    	
    	boolean bResult = false;

    	try {

			ServiceLoader<CAbstractDBEngine> sl = ServiceLoader.load( CAbstractDBEngine.class );
			sl.reload();

			CAbstractDBEngine.ClearRegisteredDBEngines();

			Iterator<CAbstractDBEngine> it = sl.iterator();

			while ( it.hasNext() ) {

				try {
					
					CAbstractDBEngine DBEngineInstance = it.next();

					if ( DBEngineInstance.InitializeDBEngine( ServicesDaemonConfig ) == true ) {
					   
						CAbstractDBEngine.ResigterDBEngine( DBEngineInstance );

						DBServicesManagerConfig.Logger.LogMessage( "1", DBServicesManagerConfig.Lang.Translate( "Registered database engine: [%s] version: [%s]", DBEngineInstance.getName().toLowerCase(), DBEngineInstance.getVersion() ) );        
					
					}
					
				} 
				catch ( Exception Ex ) {

					if ( DBServicesManagerConfig.Logger != null )
						DBServicesManagerConfig.Logger.LogException( "-1011", Ex.getMessage(), Ex );

				}

			}
    		
			int intCountDBEngines = CAbstractDBEngine.GetCountRegisteredDBEngines();
			
			DBServicesManagerConfig.Logger.LogMessage( "1", DBServicesManagerConfig.Lang.Translate( "Count of databases engines registered: [%s]", Integer.toString( intCountDBEngines ) ) );        

			bResult = intCountDBEngines > 0;

    	} 
		catch ( Exception Ex ) {
	
			if ( DBServicesManagerConfig.Logger != null )
				DBServicesManagerConfig.Logger.LogException( "-1010", Ex.getMessage(), Ex );
	
		}
    	
    	return bResult;
    	
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

					if ( ResponseFormatInstance.InitResponseFormat( ServicesDaemonConfig, DBServicesManagerConfig ) == true ) {
					   
						CAbstractResponseFormat.ResigterResponseFormat( ResponseFormatInstance );

						DBServicesManagerConfig.Logger.LogMessage( "1", DBServicesManagerConfig.Lang.Translate( "Registered response format: [%s] min version: [%s] max version: [%s]", ResponseFormatInstance.getName(), ResponseFormatInstance.getMinVersion(), ResponseFormatInstance.getMaxVersion() ) );        
					
					}
					
				} 
				catch ( Exception Ex ) {

					if ( DBServicesManagerConfig.Logger != null )
						DBServicesManagerConfig.Logger.LogException( "-1011", Ex.getMessage(), Ex );

				}

			}

		    //Add to the end of list the built response formats 
			DBServicesManagerConfig.Logger.LogMessage( "1", DBServicesManagerConfig.Lang.Translate( "Adding to end of list the built in responses formats" ) );        

			CXMLDataPacketResponseFormat XMLDataPacketResponseFormat = new CXMLDataPacketResponseFormat();
			
			if ( XMLDataPacketResponseFormat.InitResponseFormat( ServicesDaemonConfig, DBServicesManagerConfig ) == true ) {
				   
				CAbstractResponseFormat.ResigterResponseFormat( XMLDataPacketResponseFormat );
				DBServicesManagerConfig.Logger.LogMessage( "1", DBServicesManagerConfig.Lang.Translate( "Added built in response format [%s] min version: [%s] max version: [%s]", XMLDataPacketResponseFormat.getName(), XMLDataPacketResponseFormat.getMinVersion(), XMLDataPacketResponseFormat.getMaxVersion() ) );        
			
			}

			CJavaXMLWebRowSetResponseFormat JavaXMLWebRowSetResponseFormat = new CJavaXMLWebRowSetResponseFormat();
			
			if ( JavaXMLWebRowSetResponseFormat.InitResponseFormat( ServicesDaemonConfig, DBServicesManagerConfig ) == true ) {
				   
				CAbstractResponseFormat.ResigterResponseFormat( JavaXMLWebRowSetResponseFormat );
				DBServicesManagerConfig.Logger.LogMessage( "1", DBServicesManagerConfig.Lang.Translate( "Added built in response format [%s] min version: [%s] max version: [%s]", JavaXMLWebRowSetResponseFormat.getName(), JavaXMLWebRowSetResponseFormat.getMinVersion(), JavaXMLWebRowSetResponseFormat.getMaxVersion() ) );        
			
			}
			
			CJSONResponseFormat JSONResponseFormat = new CJSONResponseFormat(); //JSON
			
			if ( JSONResponseFormat.InitResponseFormat( ServicesDaemonConfig, DBServicesManagerConfig ) == true ) {
				   
				CAbstractResponseFormat.ResigterResponseFormat( JSONResponseFormat );
				DBServicesManagerConfig.Logger.LogMessage( "1", DBServicesManagerConfig.Lang.Translate( "Added built in response format [%s] min version: [%s] max version: [%s]", JSONResponseFormat.getName(), JSONResponseFormat.getMinVersion(), JSONResponseFormat.getMaxVersion() ) );        
			
			}
			
			CCSVResponseFormat CSVResponseFormat = new CCSVResponseFormat(); //CSV
			
			if ( CSVResponseFormat.InitResponseFormat( ServicesDaemonConfig, DBServicesManagerConfig ) == true ) {
				   
				CAbstractResponseFormat.ResigterResponseFormat( CSVResponseFormat );
				DBServicesManagerConfig.Logger.LogMessage( "1", DBServicesManagerConfig.Lang.Translate( "Added built in response format [%s] min version: [%s] max version: [%s]", CSVResponseFormat.getName(), CSVResponseFormat.getMinVersion(), CSVResponseFormat.getMaxVersion() ) );        
			
			}
			
			CAbstractResponseFormat DefaultResponseFormat = CAbstractResponseFormat.getResponseFomat( DBServicesManagerConfig.strDefaultResponseFormat, DBServicesManagerConfig.strDefaultResponseFormatVersion );

			if ( DefaultResponseFormat == null ) {
				
				if ( CAbstractResponseFormat.getReponseFormatSearchCodeResult() == -2 ) { //Response format name not found 
				
					DBServicesManagerConfig.Logger.LogWarning( "1", DBServicesManagerConfig.Lang.Translate( "The default response format [$s] version [%s] not found", DBServicesManagerConfig.strDefaultResponseFormat, DBServicesManagerConfig.strDefaultResponseFormatVersion ) );
					
					DBServicesManagerConfig.strDefaultResponseFormat = DefaultConstantsDBServicesManager.strDefaultResponseFormat;
					DBServicesManagerConfig.strDefaultResponseFormatVersion = DefaultConstantsDBServicesManager.strDefaultResponseFormatVersion;
				
				}
				else { //Response format name found but the version not match, use the response format with the min version available
					
					DBServicesManagerConfig.Logger.LogWarning( "1", DBServicesManagerConfig.Lang.Translate( "The default response format [$s] found, but the version [%s] not found, using the min version [%s] available", DBServicesManagerConfig.strDefaultResponseFormat, DBServicesManagerConfig.strDefaultResponseFormatVersion, CAbstractResponseFormat.getReponseFormatVersionSearchResult() ) );
					
					DBServicesManagerConfig.strDefaultResponseFormatVersion = CAbstractResponseFormat.getReponseFormatVersionSearchResult();
					
				}
				
			}
			
			DBServicesManagerConfig.Logger.LogMessage( "1", DBServicesManagerConfig.Lang.Translate( "Using default response format: [$s] min version: [%s]", DBServicesManagerConfig.strDefaultResponseFormat, DBServicesManagerConfig.strDefaultResponseFormatVersion ) );

			DBServicesManagerConfig.Logger.LogMessage( "1", DBServicesManagerConfig.Lang.Translate( "Count of responses formats registered: [%s]", Integer.toString( CAbstractResponseFormat.GetCountRegisteredResponsesFormats() ) ) );        

			bResult = CAbstractResponseFormat.GetCountRegisteredResponsesFormats() > 0;
			
		} 
		catch ( Exception Ex ) {

			if ( DBServicesManagerConfig.Logger != null )
				DBServicesManagerConfig.Logger.LogException( "-1012", Ex.getMessage(), Ex );

		}

		return bResult;
    	
    }
	
    public boolean LoadAndRegisterDBServices( CServicesDaemonConfig ServicesDaemonConfig ) {
    	
    	boolean bResult = false;

    	try {

			ServiceLoader<CAbstractService> sl = ServiceLoader.load( CAbstractService.class );
			sl.reload();

			RegisteredDBServices.clear();

			Iterator<CAbstractService> it = sl.iterator();

			while ( it.hasNext() ) {

				try {
					
					CAbstractService ServiceInstance = it.next();

					if ( ServiceInstance.InitializeService( ServicesDaemonConfig, DBServicesManagerConfig ) == true ) {
					   
						RegisteredDBServices.put( ServiceInstance.getServiceName().toLowerCase(), ServiceInstance );

						DBServicesManagerConfig.Logger.LogMessage( "1", DBServicesManagerConfig.Lang.Translate( "Registered service: [%s] description: [%s] version: [%s]", ServiceInstance.getServiceName().toLowerCase(), ServiceInstance.getServiceDescription(), ServiceInstance.getServiceVersion() ) );        
					
					}
					
				} 
				catch ( Exception Ex ) {

					if ( DBServicesManagerConfig.Logger != null )
						DBServicesManagerConfig.Logger.LogException( "-1011", Ex.getMessage(), Ex );

				}

			}
    		
			DBServicesManagerConfig.Logger.LogMessage( "1", DBServicesManagerConfig.Lang.Translate( "Count of services registered: [%s]", Integer.toString( RegisteredDBServices.size() ) ) );        

			bResult = RegisteredDBServices.size() > 0;

    	} 
		catch ( Exception Ex ) {
	
			if ( DBServicesManagerConfig.Logger != null )
				DBServicesManagerConfig.Logger.LogException( "-1010", Ex.getMessage(), Ex );
	
		}
    	
    	return bResult;
    	
    }

    public boolean InitManager( CServicesDaemonConfig ServicesDaemonConfig ) {
    	
    	super.InitManager( ServicesDaemonConfig );
       
    	DefaultConstantsDBServicesManager.strDefaultRunningPath = CDBServicesManager.getJarFolder();
    	
        CExtendedLogger DBServicesManagerLogger = CExtendedLogger.getLogger( DefaultConstantsDBServicesManager.strDefaultLoggerName );
        DBServicesManagerLogger.SetupLogger( ServicesDaemonConfig.InitArgs.contains( InitArgsConstants._LogToScreen ), DefaultConstantsDBServicesManager.strDefaultRunningPath + DefaultConstantsDBServicesManager.strDefaultLogsSystemDir, DefaultConstantsDBServicesManager.strDefaultMainFileLog, ServicesDaemonConfig.strClassNameMethodName, ServicesDaemonConfig.bExactMatch, ServicesDaemonConfig.LoggingLevel.toString() );
		
		CLanguage DBServicesManagerLang = CLanguage.getLanguage( DBServicesManagerLogger, DefaultConstantsDBServicesManager.strDefaultRunningPath + CommonClasses.DefaultConstantsServicesDaemon.strDefaultLangsDir + DefaultConstantsDBServicesManager.strDefaultMainFile + "." + ServicesDaemonConfig.strDefaultLang );

		DBServicesManagerLogger.LogMessage( "1", DBServicesManagerLang.Translate( "Running dir: [%s]", DefaultConstantsDBServicesManager.strDefaultRunningPath ) );        
		DBServicesManagerLogger.LogMessage( "1", DBServicesManagerLang.Translate( "Version: [%s]", strVersion ) );        

    	DBServicesManagerConfig = CDBServicesManagerConfig.getDBServicesManagerConfig();
		
		boolean bResult = false;
    	
    	if ( DBServicesManagerConfig.LoadConfig( DefaultConstantsDBServicesManager.strDefaultRunningPath + DefaultConstantsDBServicesManager.strDefaultConfFile, DBServicesManagerLang, DBServicesManagerLogger ) == true ) {
    		
    		try {

    			//DBConnectionsManager.Initialize( DBServicesManagerLogger, DBServicesManagerLang );
    			
    			CClassPathLoader ClassPathLoader = new CClassPathLoader( DBServicesManagerLogger, DBServicesManagerLang );

    			//Load the databases drivers
    			ClassPathLoader.LoadClassFiles( DBServicesManagerConfig.strDBDriversDir, DefaultConstantsServicesDaemon.strDefaultLibsExt, 2 );
    		
               if ( ClassPathLoader.getCountClassLoaded() > 0 ) {
            	   
           		   //Load database engines class
       			   ClassPathLoader.LoadClassFiles( DBServicesManagerConfig.strDBEnginesDir, DefaultConstantsServicesDaemon.strDefaultLibsExt, 2 );

       			   if ( this.LoadAndRegisterDBEngines( ServicesDaemonConfig ) ) {
            	   
	           		   //Load responses formats class
	       			   ClassPathLoader.LoadClassFiles( DBServicesManagerConfig.strResponsesFormatsDir, DefaultConstantsServicesDaemon.strDefaultLibsExt, 2 );
	
	       			   if ( this.LoadAndRegisterResponsesFormats( ServicesDaemonConfig ) == true ) {
	            	      
	       				   //Load DB services class
	           			   ClassPathLoader.LoadClassFiles( DBServicesManagerConfig.strDBServicesDir, DefaultConstantsServicesDaemon.strDefaultLibsExt, 2 ); //Permit owner dir
	
	           			   if ( this.LoadAndRegisterDBServices( ServicesDaemonConfig ) == true ) {
	            		   
	                		   bResult = true;
	                	   
	                	   }
	                	   else {
	                		   
	               			   DBServicesManagerLogger.LogError( "-1004", DBServicesManagerLang.Translate( "No databases services found in path [%s]", DBServicesManagerConfig.strDBServicesDir ) );
	                		   
	                	   }
	                	   
	            	   }
	            	   else {
	            		   
	           			  DBServicesManagerLogger.LogError( "-1003", DBServicesManagerLang.Translate( "No responses formats drivers found in path [%s]", DBServicesManagerConfig.strResponsesFormatsDir ) );
	            		   
	            	   }
       			   
       			   }
       			   else {
       				   
           			  DBServicesManagerLogger.LogError( "-1002", DBServicesManagerLang.Translate( "No databases engines found in path [%s]", DBServicesManagerConfig.strDBEnginesDir ) );
       				   
       			   }
       			   
               }
               else {
            	   
       			  DBServicesManagerLogger.LogError( "-1001", DBServicesManagerLang.Translate( "No datatabase drivers found in path [%s]", DBServicesManagerConfig.strDBDriversDir ) );
            	   
               }
    			
    		}
    		catch ( Exception Ex ) {
    		
    			DBServicesManagerLogger.LogException( "-1010", Ex.getMessage(), Ex );
    			
    		}
    		
    	}

    	return bResult;
    	
    }

    protected CAbstractResponseFormat getDefaultResponseFormat() {
    	
    	return CAbstractResponseFormat.getResponseFomat( DBServicesManagerConfig.strDefaultResponseFormat, DBServicesManagerConfig.strDefaultResponseFormatVersion );
    	
    }
    
    protected CAbstractResponseFormat getResponseFormat( String strResponseFormat, String strResponseFormatVersion ) {
    	
    	CAbstractResponseFormat ResponseFormat = null;
    	
        try {
    	
        	if (  strResponseFormat == null || strResponseFormat.isEmpty() ) {

        		ResponseFormat = this.getDefaultResponseFormat();    	

        	}   
        	else {

        		if ( strResponseFormatVersion == null || strResponseFormatVersion.isEmpty() )
        			strResponseFormatVersion = DefaultConstantsDBServicesManager.strDefaultVersionAny;

        		ResponseFormat = CAbstractResponseFormat.getResponseFomat( strResponseFormat, strResponseFormatVersion );    	

        		if ( ResponseFormat == null ) {

        			ResponseFormat = this.getDefaultResponseFormat();    	

        		}

        	}
    	
        }
    	catch ( Exception Ex ) {
    		
    		DBServicesManagerConfig.Logger.LogException( "-1010", Ex.getMessage(), Ex );

    	}
    	
    	return ResponseFormat;
    	
    }
    
    @Override
    protected void ProcessRequest( HttpServletRequest Request, HttpServletResponse Response ) {

 	   try {
 	    	
		   /*response.setContentType("text/html");
		   response.setStatus(HttpServletResponse.SC_OK);
		   response.getWriter().println("<h1>" + DBServicesManagerConfig.DBServicesManagerLang.Translate( "DB Services Manager" ) + "</h1>" );
	       response.getWriter().println("<h2>" + DBServicesManagerConfig.DBServicesManagerLang.Translate( "Session" ) + "=" + request.getSession(true).getId() + "<h2>");
	       response.getWriter().println("<h3>IP=" + request.getRemoteAddr() + "<h3>" );*/
	   
 	       Response.setStatus( HttpServletResponse.SC_OK );

 	       String strServiceName = ( String ) Request.getParameter( ConstantsServicesTags._RequestServiceName );

 	       //HttpSession ServiceSession = Request.getSession( true );

		   String strRequestSecurityTokenID  = ( ( String ) Request.getParameter( ConstantsServicesTags._RequestSecurityTokenID ) );

 	       //String strRequestTransactionID = ( String ) Request.getParameter( ConstantsServicesTags._RequestTransactionID );

 	       String strRequestResponseFormat = ( String ) Request.getParameter( ConstantsServicesTags._RequestResponseFormat );
 	       
 	       String strRequestResponseFormatVersion = ( String ) Request.getParameter( ConstantsServicesTags._RequestResponseFormatVersion );

           CAbstractResponseFormat ResponseFormat = this.getResponseFormat( strRequestResponseFormat, strRequestResponseFormatVersion );
 	       
           if ( ResponseFormat != null ) {

        	   if ( strRequestResponseFormatVersion == null || strRequestResponseFormatVersion.isEmpty() ) {

        		   strRequestResponseFormatVersion = DBServicesManagerConfig.strDefaultResponseFormatVersion; //ResponseFormat.getMinVersion();

        	   }

        	   if ( strServiceName != null && strServiceName.isEmpty() == false ) {
        	   
        		   CAbstractService Service = RegisteredDBServices.get( strServiceName.toLowerCase() );

        		   if ( Service != null ) {

        			   if ( Service.getAuthRequired() == false ) { //Auth not required

        				   if ( strRequestSecurityTokenID == null )
        					   strRequestSecurityTokenID = "";
        				   
        				   Service.ExecuteService( 1, Request, Response,  strRequestSecurityTokenID, RegisteredDBServices, ResponseFormat, strRequestResponseFormatVersion );

        			   }
        			  else if ( strRequestSecurityTokenID != null && strRequestSecurityTokenID.equals( "" ) == false ) {

						   //@SuppressWarnings("unchecked")
						   //ArrayList<String> strSessionSecurityTokens = ( ArrayList<String> ) ServiceSession.getAttribute( ConstantsServicesTags._SessionSecurityTokens );

        				   if ( SecurityTokensManager.checkSecurityTokenID( strRequestSecurityTokenID, DBServicesManagerConfig.Logger, DBServicesManagerConfig.Lang ) == true ) {

        					   int intResultCode = Service.ExecuteService( 1, Request, Response,  strRequestSecurityTokenID, RegisteredDBServices, ResponseFormat, strRequestResponseFormatVersion );
        					   
        					   if ( intResultCode < 0 ) {
        						   
        		        		   DBServicesManagerConfig.Logger.LogWarning( "-1", DBServicesManagerConfig.Lang.Translate( "The service name [%s] return negative value [%s]", strServiceName, Integer.toString( intResultCode ) ) );
        						   
        					   }

        				   }
        				   else {

        					   try {

        						   Response.setContentType( ResponseFormat.getContentType() );
        						   Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

        						   String strResponseBuffer = ResponseFormat.FormatSimpleMessage( "", "", -3, DBServicesManagerConfig.Lang.Translate( "The security token [%s] is incorrect. Possible attempted robbery in progress session", strRequestSecurityTokenID ), true, strRequestResponseFormatVersion, DBServicesManagerConfig.strGlobalDateTimeFormat, DBServicesManagerConfig.strGlobalDateFormat, DBServicesManagerConfig.strGlobalTimeFormat, DBServicesManagerConfig.Logger, DBServicesManagerConfig.Lang );
        						   Response.getWriter().print( strResponseBuffer );

        					   }
        					   catch ( Exception Ex ) {

        						   DBServicesManagerConfig.Logger.LogException( "-1014", Ex.getMessage(), Ex );

        					   }

        				   }

        			   }
        			   else {

        				   try {

        					   Response.setContentType( ResponseFormat.getContentType() );
        					   Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

        					   String strResponseBuffer = ResponseFormat.FormatSimpleMessage( "", "", -2, DBServicesManagerConfig.Lang.Translate( "You must log in before using the service [%s]", strServiceName ), true, strRequestResponseFormatVersion, DBServicesManagerConfig.strGlobalDateTimeFormat, DBServicesManagerConfig.strGlobalDateFormat, DBServicesManagerConfig.strGlobalTimeFormat, DBServicesManagerConfig.Logger, DBServicesManagerConfig.Lang );
        					   Response.getWriter().print( strResponseBuffer );

        				   }
        				   catch ( Exception Ex ) {

        					   DBServicesManagerConfig.Logger.LogException( "-1013", Ex.getMessage(), Ex );

        				   }

        			   }

        		   }
        		   else {

        			   try {

        				   Response.setContentType( ResponseFormat.getContentType() );
        				   Response.setCharacterEncoding( ResponseFormat.getCharacterEncoding() );

        				   String strResponseBuffer = ResponseFormat.FormatSimpleMessage( "", "", -1, DBServicesManagerConfig.Lang.Translate( "The service name [%s] not found", strServiceName ), true, strRequestResponseFormatVersion, DBServicesManagerConfig.strGlobalDateTimeFormat, DBServicesManagerConfig.strGlobalDateFormat, DBServicesManagerConfig.strGlobalTimeFormat, DBServicesManagerConfig.Logger, DBServicesManagerConfig.Lang );
        				   Response.getWriter().print( strResponseBuffer );

        			   }
        			   catch ( Exception Ex ) {

        				   DBServicesManagerConfig.Logger.LogException( "-1012", Ex.getMessage(), Ex );

        			   }

        		   }

        	   }
        	   else {

        		   String strRequestIP = Request.getLocalAddr();
        		   String strForwardedIP = Request.getHeader( "X-Forwarded-For" );
        		   
        		   if ( strForwardedIP == null ) {
        			   
        			   strForwardedIP = "";
        			   
        		   }
        		   
        		   DBServicesManagerConfig.Logger.LogWarning( "-1", DBServicesManagerConfig.Lang.Translate( "The service name is empty, request from ip address [%s], forwarded from [%s]", strRequestIP, strForwardedIP ) );
        		   
        	   }
           
           }
           else  {

        	   String strMessage = DBServicesManagerConfig.Lang.Translate( "Fatal error, no response format found" );
        	   
        	   Response.getWriter().print( strMessage );
        	   DBServicesManagerConfig.Logger.LogError( "-1011", strMessage );
        	   
           }
           
	   }
	   catch ( Exception Ex ) {
  
		   DBServicesManagerConfig.Logger.LogException( "-1010", Ex.getMessage(), Ex );
		   
	   }
 	   
    }
    
    @Override
    protected void doGet( HttpServletRequest request, HttpServletResponse response ) {
    	
    	if ( DBServicesManagerConfig.strResponseRequestMethod.equals( ConfigXMLTagsServicesDaemon._Request_Method_ANY ) || DBServicesManagerConfig.strResponseRequestMethod.equals( ConfigXMLTagsServicesDaemon._Request_Method_OnlyGET ) )
    		this.ProcessRequest( request, response );
    	else
   	        response.setStatus( HttpServletResponse.SC_OK );

    }
    
    @Override
    protected void doPost( HttpServletRequest request, HttpServletResponse response ) {
    	
    	if ( DBServicesManagerConfig.strResponseRequestMethod.equals( ConfigXMLTagsServicesDaemon._Request_Method_ANY ) || DBServicesManagerConfig.strResponseRequestMethod.equals( ConfigXMLTagsServicesDaemon._Request_Method_OnlyPOST ) )
    		this.ProcessRequest( request, response );
    	else
   	        response.setStatus( HttpServletResponse.SC_OK );
        
    }
    
}
