package CommonClasses;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.UUID;

import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;

import WebRowSet.CWebRowSetImpl;

import ExtendedLogger.CExtendedLogger;

public class CBackendServicesManager {

	protected String strGUID = "";
	protected String strPathToTempDir = "";
	
	protected final static String _ResponseFormat = "JAVA-XML-WEBROWSET";
	protected final static String _ResponseFormatVersion = "1.0";
	
	protected ArrayList<String> strTempFilesPath = null;
	
	public CBackendServicesManager( String strPathToTempDir ) {
		
		this.strGUID = UUID.randomUUID().toString();

		this.strPathToTempDir = strPathToTempDir;
		
		strTempFilesPath = new ArrayList<String>();
		
	}
	
	public HttpClientContext setConfigProxy( HttpPost PostData, CConfigProxy ConfigProxy, CExtendedLogger Logger, CLanguage Lang ) {
		
		HttpClientContext Context = null;
		
		if ( ConfigProxy.strProxyIP.isEmpty() == false && ConfigProxy.intProxyPort > ConstantsCommonClasses._Min_Port_Number && ConfigProxy.intProxyPort < CommonClasses.ConstantsCommonClasses._Max_Port_Number ) {
			
			HttpHost ProxyHost = new HttpHost( ConfigProxy.strProxyIP.trim(), ConfigProxy.intProxyPort );
			
			RequestConfig ProxyConfig = RequestConfig.custom().setProxy( ProxyHost ).build();
			
			PostData.setConfig( ProxyConfig );
			
			if ( ConfigProxy.strProxyUser.trim().isEmpty() == false && ConfigProxy.strProxyPassword.trim().isEmpty() == false ) {
				
				String strProxyPassword = ConfigProxy.strProxyPassword.trim();
				
				strProxyPassword = net.maindataservices.Utilities.uncryptString( ConfigXMLTagsServicesDaemon._Password_Crypted, ConfigXMLTagsServicesDaemon._Password_Crypted_Sep, ConstantsCommonClasses._Crypt_Algorithm, strProxyPassword, Logger, Lang );
				
				CredentialsProvider credsProvider = new BasicCredentialsProvider();
				credsProvider.setCredentials( new AuthScope( ProxyHost.getHostName(), ProxyHost.getPort() ), new UsernamePasswordCredentials( ConfigProxy.strProxyUser.trim(), strProxyPassword ));    				
			
				// Create AuthCache instance
				AuthCache authCache = new BasicAuthCache();
				// Generate BASIC scheme object and add it to the local auth cache
				BasicScheme basicAuth = new BasicScheme();
				authCache.put( ProxyHost, basicAuth );
				
    			Context = HttpClientContext.create();
    			Context.setCredentialsProvider( credsProvider );
    			Context.setAuthCache( authCache );
    			
			}
			
		}

		return Context;
		
	}
	
    public boolean saveResponseToFile( InputStream IStream, String strFilePath, String strFileName, CExtendedLogger Logger, CLanguage Lang ) {
    	
    	boolean bResult = false;
    	
    	Logger.logInfo( "2", Lang.translate( "Saving response file on: [%s]",  strFilePath + strFileName ) );

    	try {
    	
    		String strResponsePath = strFilePath + strFileName; 

    		FileOutputStream OutputStream = new FileOutputStream( new File( strResponsePath ) );

    		int intChunkSize = 10240; //10kb

    		byte[] bytBuffer = new byte[ intChunkSize ];

    		int n = 0;
    		
    		while ( -1 != ( n = IStream.read( bytBuffer ) ) ) {

    			OutputStream.write( bytBuffer, 0, n );

    		}			

    		OutputStream.close();
    	
    		IStream.close();
    		
    		if ( strTempFilesPath.contains( strResponsePath ) == false )
    		   strTempFilesPath.add( strResponsePath );
    		
    		bResult = true;
    		
    	}
    	catch ( Exception Ex ) {
    		
			Logger.logException( "-1015", Ex.getMessage(), Ex );
    		
    	}
		
    	return bResult;
    	
    }
    
    public ArrayList<LinkedHashMap<String,String>> parseResponseMessage( String strFilePath, String strFileName, CExtendedLogger Logger, CLanguage Lang ) {
    	
    	Logger.logInfo( "2", Lang.translate( "Parsing response file on: [%s]",  strFilePath + strFileName ) );
    	
    	ArrayList<LinkedHashMap<String,String>> Result = new ArrayList<LinkedHashMap<String,String>>();
    	
    	try {
		
			String strResponsePath = strFilePath + strFileName; 

    		if ( new File( strResponsePath ).length() > 0 ) {

    			CWebRowSetImpl WebRSRead = new CWebRowSetImpl();
    		
    			java.io.FileInputStream iStream = new java.io.FileInputStream( strResponsePath );
    			BufferedReader br = new BufferedReader( new InputStreamReader( iStream ) );

    			WebRSRead.setPageSize( 100 );  //Set the number of rows to be loads into memory

    			WebRSRead.initReadXML( br ); //Create the fields/columns metada only, no rows loaded!!! 

    			ResultSetMetaData RSMD = WebRSRead.getMetaData();

    			boolean bOnLastPage;

    			do {

    				bOnLastPage = WebRSRead.readXMLBody( br ); //Read the first page

    				while ( WebRSRead.next() ) {

    					LinkedHashMap<String,String> Record = new LinkedHashMap<String,String>();

    					for ( int I = 1; I <= RSMD.getColumnCount(); I ++ ) {

    						String strColumnName = RSMD.getColumnName( I );

    						if ( strColumnName == null || strColumnName.isEmpty() == true )
    							strColumnName = RSMD.getColumnLabel( I );

    						String strColumnValue = WebRSRead.getString( strColumnName );

    						Record.put( strColumnName, strColumnValue );

    					}

    					Result.add( Record );

    				}

    			} while ( bOnLastPage == false );

    			WebRSRead.close();

    			iStream.close();
    		
    		}
    		else {
    			
    			Logger.logWarning( "-1" , Lang.translate( "The file in the path [%s] is empty" , strResponsePath ) );
    			
    		}
			
		} 
    	catch ( Exception Ex ) {

			Logger.logException( "-1015", Ex.getMessage(), Ex );
    		
		} 
    	
    	return Result;
    	
    }
    
    public boolean isResponseWithRows( String strFilePathName, CExtendedLogger Logger, CLanguage Lang ) {
    
    	boolean bResult = false;
    	
    	try {
    		
    		CWebRowSetImpl WebRSRead = new CWebRowSetImpl();
    		
			java.io.FileInputStream iStream = new java.io.FileInputStream( strFilePathName );
			BufferedReader br = new BufferedReader( new InputStreamReader( iStream ) );

			WebRSRead.setPageSize( 1 );  //Set the number of rows to be loads into memory
			
			WebRSRead.initReadXML( br ); //Create the fields/columns metada only, no rows loaded!!! 
			
			WebRSRead.readXMLBody( br ); //Read the first page

			if ( WebRSRead.next() ) {

				bResult = true;

			}
			
			WebRSRead.close();
			
			iStream.close();
    		
		} 
    	catch ( Exception Ex ) {

			Logger.logException( "-1015", Ex.getMessage(), Ex );
    		
		} 
    	
    	return bResult;
    	
    }
	
    public boolean deleteTempResponsesFiles( CExtendedLogger Logger, CLanguage Lang ) {
    	
    	boolean bResult = false;

    	Logger.logWarning( "-1", Lang.translate( "Deleting temporal files in path: [%s]", strPathToTempDir ) );

    	try {
    	
    		for ( String strTempFilePath : strTempFilesPath ) {

    			if ( net.maindataservices.Utilities.checkFile( strTempFilePath, Logger, Lang ) ) {

    				File TempFile = new File( strTempFilePath ); 

    				if ( TempFile.delete() == false ) {
    					
    	    			Logger.logWarning( "-1", Lang.translate( "Cannot delete the file in the path: [%s]", strTempFilePath ) );
    					
    				}

    			}

    		}
    		
    		strTempFilesPath.clear();
    		
    		bResult = true;
    	
    	}
    	catch ( Exception Ex ) {
    		
			Logger.logException( "-1015", Ex.getMessage(), Ex );
    		
    	}
    	
    	return bResult;
    	
    }
    
    public ArrayList<LinkedHashMap<String,String>> callServiceSystemRegisterManager( CloseableHttpClient HTTPClient, String strSecurityTokenID, String strURL, CConfigProxy ConfigProxy, String strContext, String strManagerURL, int intWeight, int intLoad, CExtendedLogger Logger, CLanguage Lang ) {

    	ArrayList<LinkedHashMap<String,String>> Result = null;
    	
    	try {
    		
        	CloseableHttpResponse Response = null;
        	
    		HttpPost PostData = new HttpPost( strURL );
    		 
    		//Set the proxy
    		HttpClientContext Context = this.setConfigProxy( PostData, ConfigProxy, Logger, Lang );
    		
    		// add header
    		PostData.setHeader( "User-Agent", strContext );
    	 
    		ArrayList<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
    		urlParameters.add( new BasicNameValuePair( "ServiceName", "System.Register.Manager" ) );
    		urlParameters.add( new BasicNameValuePair( "SecurityTokenID", net.maindataservices.Utilities.uncryptString( ConfigXMLTagsServicesDaemon._Password_Crypted, ConfigXMLTagsServicesDaemon._Password_Crypted_Sep, ConstantsCommonClasses._Crypt_Algorithm, strSecurityTokenID, Logger, Lang ) ) );
    		urlParameters.add( new BasicNameValuePair( "Context", strContext ) );
    		urlParameters.add( new BasicNameValuePair( "ManagerURL", strManagerURL ) );
    		urlParameters.add( new BasicNameValuePair( "Weight", Integer.toString( intWeight ) ) );
    		urlParameters.add( new BasicNameValuePair( "Load", Integer.toString( intLoad ) ) );
    		urlParameters.add( new BasicNameValuePair( "ResponseFormat", _ResponseFormat ) );
    		urlParameters.add( new BasicNameValuePair( "ResponseFormatVersion", _ResponseFormatVersion ) );
    		
			PostData.setEntity( new UrlEncodedFormEntity( urlParameters ) );
			
			if ( Context == null )
				Response = HTTPClient.execute( PostData );
			else
				Response = HTTPClient.execute( PostData, Context );
			
			if ( Response != null && this.saveResponseToFile( Response.getEntity().getContent(), strPathToTempDir, "System.Register.Manager.Response." + this.strGUID, Logger, Lang ) ) { 

				Response.close();
				
				Result = this.parseResponseMessage( this.strPathToTempDir, "System.Register.Manager.Response." + this.strGUID, Logger, Lang );
				
			}
    		
    	}
    	catch ( Error Err ) {
    		
			Logger.logError( "-1015", Err.getMessage(), Err );
    		
    	}
    	catch ( Exception Ex ) {
    		
			Logger.logException( "-1016", Ex.getMessage(), Ex );
    		
    	}
    	
    	return Result;
    	
    }

    public ArrayList<LinkedHashMap<String,String>> callServiceSystemUnregisterManager( CloseableHttpClient HTTPClient, String strSecurityTokenID, String strURL, CConfigProxy ConfigProxy, String strContext, String strManagerURL, CExtendedLogger Logger, CLanguage Lang ) {

    	ArrayList<LinkedHashMap<String,String>> Result = null;
    	
    	try {
    		
        	CloseableHttpResponse Response = null;
        	
    		HttpPost PostData = new HttpPost( strURL );
    		 
    		//Set the proxy
    		HttpClientContext Context = this.setConfigProxy( PostData, ConfigProxy, Logger, Lang );
    		
    		// add header
    		PostData.setHeader( "User-Agent",  strContext );
    	 
    		ArrayList<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
    		urlParameters.add( new BasicNameValuePair( "ServiceName", "System.Unregister.Manager" ) );
    		urlParameters.add( new BasicNameValuePair( "SecurityTokenID", net.maindataservices.Utilities.uncryptString( ConfigXMLTagsServicesDaemon._Password_Crypted, ConfigXMLTagsServicesDaemon._Password_Crypted_Sep, ConstantsCommonClasses._Crypt_Algorithm, strSecurityTokenID, Logger, Lang ) ) );
    		urlParameters.add( new BasicNameValuePair( "ManagerURL", strManagerURL ) );
    		urlParameters.add( new BasicNameValuePair( "ResponseFormat", _ResponseFormat ) );
    		urlParameters.add( new BasicNameValuePair( "ResponseFormatVersion", _ResponseFormatVersion ) );
    		
			PostData.setEntity( new UrlEncodedFormEntity( urlParameters ) );
			
			if ( Context == null )
				Response = HTTPClient.execute( PostData );
			else
				Response = HTTPClient.execute( PostData, Context );
			
			if ( Response != null && this.saveResponseToFile( Response.getEntity().getContent(), strPathToTempDir, "System.Unregister.Manager.Response." + this.strGUID, Logger, Lang ) ) { 

				Response.close();
				
				Result = this.parseResponseMessage( this.strPathToTempDir, "System.Unregister.Manager.Response." + this.strGUID, Logger, Lang );
				
			}
    		
    	}
    	catch ( Exception Ex ) {
    		
			Logger.logException( "-1015", Ex.getMessage(), Ex );
    		
    	}
    	
    	return Result;
    	
    }
    
    public ArrayList<CRegisteredManagerInfo> parseResponseMessageRegisteredManagers( String strFilePath, String strFileName, CExtendedLogger Logger, CLanguage Lang ) {
    	
    	ArrayList<CRegisteredManagerInfo> Result = new ArrayList<CRegisteredManagerInfo>();
    	
    	try {
		
    		String strResponsePath = strFilePath + strFileName; 
    		
    		if ( new File( strResponsePath ).length() > 0 ) {
    		
    			CWebRowSetImpl WebRSRead = new CWebRowSetImpl();

    			java.io.FileInputStream iStream = new java.io.FileInputStream( strResponsePath );
    			BufferedReader br = new BufferedReader( new InputStreamReader( iStream ) );

    			WebRSRead.setPageSize( 1000 );  //Set the number of rows to be loads into memory

    			WebRSRead.initReadXML( br ); //Create the fields/columns metada only, no rows loaded!!! 

    			boolean bOnLastPage;

    			do {

    				bOnLastPage = WebRSRead.readXMLBody( br ); //Read the first page

    				while ( WebRSRead.next() ) {

    					CRegisteredManagerInfo RegisterManager = new CRegisteredManagerInfo();

    					try {

    						RegisterManager.strContext = WebRSRead.getString( "Context" );
    						RegisterManager.strManagerURL = WebRSRead.getString( "ManagerURL" );
    						RegisterManager.intLoad = WebRSRead.getInt( "Load" );
    						RegisterManager.intWeight = WebRSRead.getInt( "Weight" );
    						RegisterManager.intStandardizedWeight = WebRSRead.getInt( "StandardizedWeight" );
    						RegisterManager.lngLastUpdate = System.currentTimeMillis();

    						Result.add( RegisterManager );

    					}
    					catch ( Exception Ex ) {

    						Logger.logException( "-1015", Ex.getMessage(), Ex );

    					}

    				}

    			} while ( bOnLastPage == false );

    			WebRSRead.close();

    			iStream.close();

    		}
    		else {
    			
    			Logger.logWarning( "-1" , Lang.translate( "The file in the path [%s] is empty" , strResponsePath ) );
    			
    		}
    		
		} 
    	catch ( Exception Ex ) {

			Logger.logException( "-1015", Ex.getMessage(), Ex );
    		
		} 
    	
    	return Result;
    	
    }
    
    public ArrayList<CRegisteredManagerInfo> callServiceSystemListRegisteredManagers( CloseableHttpClient HTTPClient, String strSecurityTokenID, String strURL, CConfigProxy ConfigProxy, String strContext, CExtendedLogger Logger, CLanguage Lang ) {

    	ArrayList<CRegisteredManagerInfo> Result = null;
    	
    	try {
    		
        	CloseableHttpResponse Response = null;
        	
    		HttpPost PostData = new HttpPost( strURL );
    		 
    		//Set the proxy
    		HttpClientContext Context = this.setConfigProxy( PostData, ConfigProxy, Logger, Lang );
    		
    		// add header
    		PostData.setHeader( "User-Agent", strContext );
    	 
    		ArrayList<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
    		urlParameters.add( new BasicNameValuePair( "ServiceName", "System.List.Registered.Managers" ) );
    		urlParameters.add( new BasicNameValuePair( "Context", strContext ) );
    		urlParameters.add( new BasicNameValuePair( "SecurityTokenID", net.maindataservices.Utilities.uncryptString( ConfigXMLTagsServicesDaemon._Password_Crypted, ConfigXMLTagsServicesDaemon._Password_Crypted_Sep, ConstantsCommonClasses._Crypt_Algorithm, strSecurityTokenID, Logger, Lang ) ) );
    		urlParameters.add( new BasicNameValuePair( "ResponseFormat", _ResponseFormat ) );
    		urlParameters.add( new BasicNameValuePair( "ResponseFormatVersion", _ResponseFormatVersion ) );
    		
			PostData.setEntity( new UrlEncodedFormEntity( urlParameters ) );
			
			if ( Context == null )
				Response = HTTPClient.execute( PostData );
			else
				Response = HTTPClient.execute( PostData, Context );
			
			if ( Response != null && this.saveResponseToFile( Response.getEntity().getContent(), strPathToTempDir, "System.List.Registered.Managers.Response." + this.strGUID, Logger, Lang ) ) { 

				Response.close();
				
				Result = this.parseResponseMessageRegisteredManagers( this.strPathToTempDir, "System.List.Registered.Managers.Response." + this.strGUID, Logger, Lang );
				
			}
    		
    	}
    	catch ( Exception Ex ) {
    		
			Logger.logException( "-1015", Ex.getMessage(), Ex );
    		
    	}
    	
    	return Result;
    	
    }
    
}
