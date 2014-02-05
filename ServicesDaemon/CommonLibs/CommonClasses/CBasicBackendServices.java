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
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;

import ExtendedLogger.CExtendedLogger;
import WebRowSet.CWebRowSetImpl;

public class CBasicBackendServices {

	protected String strGUID = "";
	protected String strPathToTempDir = "";
	
	public final static String _ResponseFormat = "JAVA-XML-WEBROWSET";
	public final static String _ResponseFormatVersion = "1.0";
	
	protected ArrayList<String> strTempFilesPath = null;
	
	public CBasicBackendServices( String strPathToTempDir ) {
		
		this.strGUID = UUID.randomUUID().toString();

		this.strPathToTempDir = strPathToTempDir;
		
		strTempFilesPath = new ArrayList<String>();
		
	}
	
	public static HttpClientContext setConfigProxy( HttpPost PostData, CConfigProxy ConfigProxy, CExtendedLogger Logger, CLanguage Lang ) {
		
		HttpClientContext Context = null;
		
		if ( ConfigProxy.strProxyIP.isEmpty() == false && ConfigProxy.intProxyPort > ConstantsCommonClasses._Min_Port_Number && ConfigProxy.intProxyPort < CommonClasses.ConstantsCommonClasses._Max_Port_Number ) {
			
			HttpHost ProxyHost = new HttpHost( ConfigProxy.strProxyIP.trim(), ConfigProxy.intProxyPort );
			
			RequestConfig ProxyConfig = RequestConfig.custom().setProxy( ProxyHost ).build();
			
			PostData.setConfig( ProxyConfig );
			
			if ( ConfigProxy.strProxyUser.trim().isEmpty() == false && ConfigProxy.strProxyPassword.trim().isEmpty() == false ) {
				
				String strProxyPassword = ConfigProxy.strProxyPassword.trim();
				
				strProxyPassword = net.maindataservices.Utilities.uncryptString( ConstantsCommonConfigXMLTags._Password_Crypted, ConstantsCommonConfigXMLTags._Password_Crypted_Sep, ConstantsCommonClasses._Crypt_Algorithm, strProxyPassword, Logger, Lang );
				
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

		if ( Logger != null && Lang != null )
    		Logger.logWarning( "-1", Lang.translate( "Deleting temporal files in path: [%s]", strPathToTempDir ) );

    	try {
    	
    		for ( String strTempFilePath : strTempFilesPath ) {

    			if ( net.maindataservices.Utilities.checkFile( strTempFilePath, Logger, Lang ) ) {

    				File TempFile = new File( strTempFilePath ); 

    				if ( TempFile.delete() == false ) {
    					
    					if ( Logger != null && Lang != null )
    						Logger.logWarning( "-1", Lang.translate( "Cannot delete the file in the path: [%s]", strTempFilePath ) );
    					
    				}

    			}

    		}
    		
    		strTempFilesPath.clear();
    		
    		bResult = true;
    	
    	}
    	catch ( Exception Ex ) {

    		if ( Logger != null )
    			Logger.logException( "-1015", Ex.getMessage(), Ex );
    		
    	}
    	
    	return bResult;
    	
    }
	
}
