package CommonClasses;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;

import WebRowSet.CWebRowSetImpl;
import ExtendedLogger.CExtendedLogger;

public class CBackendServicesManager extends CBasicBackendServices {
    
    public CBackendServicesManager( String strPathToTempDir ) {

    	super(strPathToTempDir);
    	
	}

	public ArrayList<LinkedHashMap<String,String>> callServiceSystemRegisterManager( CloseableHttpClient HTTPClient, String strSecurityTokenID, String strURL, CConfigProxy ConfigProxy, String strContext, String strManagerURL, int intWeight, int intLoad, CExtendedLogger Logger, CLanguage Lang ) {

    	ArrayList<LinkedHashMap<String,String>> Result = null;
    	
    	try {
    		
        	CloseableHttpResponse Response = null;
        	
    		HttpPost PostData = new HttpPost( strURL );
    		 
    		//Set the proxy
    		HttpClientContext Context = setConfigProxy( PostData, ConfigProxy, Logger, Lang );
    		
    		// add header
    		PostData.setHeader( "User-Agent", strContext );
    	 
    		ArrayList<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
    		urlParameters.add( new BasicNameValuePair( "ServiceName", "System.Register.Manager" ) );
    		urlParameters.add( new BasicNameValuePair( "SecurityTokenID", net.maindataservices.Utilities.uncryptString( ConstantsCommonConfigXMLTags._Password_Crypted, ConstantsCommonConfigXMLTags._Password_Crypted_Sep, ConstantsCommonClasses._Crypt_Algorithm, strSecurityTokenID, Logger, Lang ) ) );
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
    		HttpClientContext Context = setConfigProxy( PostData, ConfigProxy, Logger, Lang );
    		
    		// add header
    		PostData.setHeader( "User-Agent",  strContext );
    	 
    		ArrayList<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
    		urlParameters.add( new BasicNameValuePair( "ServiceName", "System.Unregister.Manager" ) );
    		urlParameters.add( new BasicNameValuePair( "SecurityTokenID", net.maindataservices.Utilities.uncryptString( ConstantsCommonConfigXMLTags._Password_Crypted, ConstantsCommonConfigXMLTags._Password_Crypted_Sep, ConstantsCommonClasses._Crypt_Algorithm, strSecurityTokenID, Logger, Lang ) ) );
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
    		HttpClientContext Context = setConfigProxy( PostData, ConfigProxy, Logger, Lang );
    		
    		// add header
    		PostData.setHeader( "User-Agent", strContext );
    	 
    		ArrayList<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
    		urlParameters.add( new BasicNameValuePair( "ServiceName", "System.List.Registered.Managers" ) );
    		urlParameters.add( new BasicNameValuePair( "Context", strContext ) );
    		urlParameters.add( new BasicNameValuePair( "SecurityTokenID", net.maindataservices.Utilities.uncryptString( ConstantsCommonConfigXMLTags._Password_Crypted, ConstantsCommonConfigXMLTags._Password_Crypted_Sep, ConstantsCommonClasses._Crypt_Algorithm, strSecurityTokenID, Logger, Lang ) ) );
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
