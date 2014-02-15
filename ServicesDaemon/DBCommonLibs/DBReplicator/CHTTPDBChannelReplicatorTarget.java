package DBReplicator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import CommonClasses.CBackendServicesManager;
import CommonClasses.CBasicBackendServices;
import CommonClasses.CConfigProxy;
import CommonClasses.CLanguage;
import ExtendedLogger.CExtendedLogger;

public class CHTTPDBChannelReplicatorTarget {

	public CHTTPDBChannelReplicator HTTPDBChannelReplicatorTarget;
	
	public int intRequestTimeout;
	public int intSocketTimeout;
	public String strURL;
	public CConfigProxy ConfigProxy;
	public String strDatabase;
	public String strUser;
	public String strPassword;
	
	public CloseableHttpClient HTTPClient;
	public HttpPost PostData;
	public HttpClientContext Context;
	
	public static boolean findURL( LinkedList<CHTTPDBChannelReplicatorTarget> ConfiguredChannels, String strURL ) {
		
		boolean bResult = false;
		
		for ( CHTTPDBChannelReplicatorTarget ConfigHTTPDBChannel: ConfiguredChannels ) {
			
			if ( ConfigHTTPDBChannel.strURL.equalsIgnoreCase( strURL ) ) {
				
				bResult = true;
				break;
				
			}
			
		}
		
		return bResult;
		
	}
	
	public CHTTPDBChannelReplicatorTarget() {
		
		intRequestTimeout = -1;
		intSocketTimeout = -1;
		strURL = "";
		ConfigProxy = null;
		strDatabase = "";
		strUser = "";
		strPassword = "";
		
		HTTPClient = null;
		PostData = null;
		Context =null;
		
	}
	
	public CHTTPDBChannelReplicatorTarget( CHTTPDBChannelReplicatorTarget ConfigHTTPDBChannelClone ) {
		
		intRequestTimeout = ConfigHTTPDBChannelClone.intRequestTimeout;
		intSocketTimeout = ConfigHTTPDBChannelClone.intSocketTimeout;
		strURL = ConfigHTTPDBChannelClone.strURL;
		ConfigProxy = new CConfigProxy( ConfigHTTPDBChannelClone.ConfigProxy );
		strDatabase = ConfigHTTPDBChannelClone.strDatabase;
		strUser = ConfigHTTPDBChannelClone.strUser;
		strPassword = ConfigHTTPDBChannelClone.strPassword;
		
		HTTPClient = null;
		PostData = null;
		Context =null;
		
	}
	
	public void initHTTPChannel( CExtendedLogger Logger, CLanguage Lang ) {
		
		RequestConfig clientConfig = RequestConfig.custom().setConnectTimeout( intRequestTimeout ).setConnectionRequestTimeout( intRequestTimeout ).setSocketTimeout( intSocketTimeout ).build();

		this.HTTPClient = HttpClientBuilder.create().setDefaultRequestConfig( clientConfig ).build();

		PostData = new HttpPost( strURL );

		//Set the proxy
		Context = CBasicBackendServices.setConfigProxy( PostData, ConfigProxy, Logger, Lang );

		// add header
		PostData.setHeader( "User-Agent", "HTTPDBReplicatorChannelClient" );
		
	}
	
	public boolean sendData( String strDataBlockID, String strTransactionID, String strCommandID, String strCommand, LinkedHashMap<String, String> Params, CExtendedLogger Logger, CLanguage Lang ) {

		boolean bResult = false;
		
		try {
			
        	CloseableHttpResponse Response = null;

    		ArrayList<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
    		urlParameters.add( new BasicNameValuePair( "ServiceName", "System.Replicate.Command" ) );
    		urlParameters.add( new BasicNameValuePair( "InstanceID", HTTPDBChannelReplicatorTarget.strInstanceID ) );
    		urlParameters.add( new BasicNameValuePair( "DataBlockID", strDataBlockID ) );
    		urlParameters.add( new BasicNameValuePair( "TransactionID", strTransactionID ) );
    		urlParameters.add( new BasicNameValuePair( "CommandID", strCommandID ) );
    		urlParameters.add( new BasicNameValuePair( "Command", strCommand ) );
    		urlParameters.add( new BasicNameValuePair( "User", strUser ) );
    		urlParameters.add( new BasicNameValuePair( "Password", strPassword ) );
    		urlParameters.add( new BasicNameValuePair( "Database", strDatabase ) );
    		urlParameters.add( new BasicNameValuePair( "ResponseFormat", CBackendServicesManager._ResponseFormat ) );
    		urlParameters.add( new BasicNameValuePair( "ResponseFormatVersion", CBackendServicesManager._ResponseFormatVersion ) );
    		
        	if ( Params.size() > 0 ) {

        		urlParameters.add( new BasicNameValuePair( "ComplexCommand", "1" ) );
        		
        		for ( Entry<String,String> Param : Params.entrySet() ) {
        			
            		urlParameters.add( new BasicNameValuePair( Param.getKey(), Param.getValue() ) );
        			
        		}

        	}
        	else {
        		
        		urlParameters.add( new BasicNameValuePair( "ComplexCommand", "0" ) );
        		
        	}
			
    		PostData.setEntity( new UrlEncodedFormEntity( urlParameters ) );
			
			if ( Context == null )
				Response = HTTPClient.execute( PostData );
			else
				Response = HTTPClient.execute( PostData, Context );
			
			if ( Response != null && HTTPDBChannelReplicatorTarget.BasicBackendServices.saveResponseToFile( Response.getEntity().getContent(), HTTPDBChannelReplicatorTarget.strReplicatorStorePathTemp, "System.Replicate.Command.Response", Logger, Lang ) ) { 

				ArrayList<LinkedHashMap<String,String>> Result = HTTPDBChannelReplicatorTarget.BasicBackendServices.parseResponseMessage( HTTPDBChannelReplicatorTarget.strReplicatorStorePathTemp, "System.Replicate.Command.Response", Logger, Lang );
				
				if ( Result != null && Result.size() > 0 ) {
					
					String strCode = Result.get( 0 ).get( "Code" );
					
					if ( strCode != null && Integer.parseInt( strCode ) >= 1000 ) { //1000 = OK, 1001 = DataBlock ok but CommandID field contains ID repeated
					
						bResult = true;
					
					}
					
				}
				
				Response.close();
				
			}
			
			
		}
		catch ( Exception Ex ) {
			
			Logger.logException( "-1020", Ex.getMessage(), Ex );
			
		}
		catch ( Error Err ) {
			
			Logger.logError( "-1021", Err.getMessage(), Err );
			
		}

		return bResult;
		
	}
	
}
