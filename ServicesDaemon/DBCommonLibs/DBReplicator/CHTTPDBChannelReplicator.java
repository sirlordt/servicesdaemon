package DBReplicator;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
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
import CommonClasses.CLanguage;
import ExtendedLogger.CExtendedLogger;

public class CHTTPDBChannelReplicator implements IDBChannelReplicator {

	CConfigHTTPDBChannelReplicator ConfigHTTPDBChannelReplicator;
	
	protected String strInstanceID;
	
	protected String strReplicatorStorePath;

	protected String strReplicatorStorePathTemp;
	
	protected CloseableHttpClient HTTPClient = null;
	
	protected HttpPost PostData = null;
	
	HttpClientContext Context = null;
	
	CBasicBackendServices BasicBackendServices = null; 

	CExtendedLogger Logger;
	
	CLanguage Lang;
	
	public CHTTPDBChannelReplicator() {
		
	}

	public void finalize() {
		
		BasicBackendServices.deleteTempResponsesFiles( Logger, Lang );
		
	}
	
	@Override
	public String getName() {

		return this.getClass().getName();
		
	}

	@Override
	public String getVersion() {

		return "0.0.0.1";
		
	}

	@Override
	public boolean loadConfig( String strInstanceID, String strReplicatorStorePath, String strRunningPath, String strConfigFile, CExtendedLogger Logger, CLanguage Lang ) {

		boolean bResult = false;
		
		try {
		
			ConfigHTTPDBChannelReplicator = CConfigHTTPDBChannelReplicator.getConfigHTTPDBChannelReplicator( strRunningPath );

			if (  ConfigHTTPDBChannelReplicator.loadConfig( strConfigFile, Logger, Lang ) ) {

				this.strReplicatorStorePath = strReplicatorStorePath;
				
				this.strReplicatorStorePathTemp = strReplicatorStorePath + "Temp" + File.separatorChar;

				new File( this.strReplicatorStorePathTemp ).mkdirs();
				
				BasicBackendServices = new CBasicBackendServices( this.strReplicatorStorePathTemp );
				
				this.strInstanceID = strInstanceID;
				
				RequestConfig clientConfig = RequestConfig.custom().setConnectTimeout( ConfigHTTPDBChannelReplicator.intRequestTimeout ).setConnectionRequestTimeout( ConfigHTTPDBChannelReplicator.intRequestTimeout ).setSocketTimeout( ConfigHTTPDBChannelReplicator.intSocketTimeout ).build();

				this.HTTPClient = HttpClientBuilder.create().setDefaultRequestConfig( clientConfig ).build();

				PostData = new HttpPost( ConfigHTTPDBChannelReplicator.strURL );

				//Set the proxy
				Context = CBackendServicesManager.setConfigProxy( PostData, ConfigHTTPDBChannelReplicator.ConfigProxy, Logger, Lang );

				// add header
				PostData.setHeader( "User-Agent", "HTTPDBReplicatorChannelClient" );

				this.Logger = Logger;
				
				this.Lang = Lang;
				
				bResult = true;

			}

		}	
		catch ( Exception Ex ) {
			
        	bResult = false;
        	
			Logger.logException( "-1020", Ex.getMessage(), Ex );
			
		}
		catch ( Error Err ) {
			
        	bResult = false;
        	
			Logger.logError( "-1021", Err.getMessage(), Err );
			
		}

		return bResult;
		
	}

	@Override
	public boolean sendData( String strTransactionID, String strCommandID, String strCommand, LinkedHashMap<String, String> Params, CExtendedLogger Logger, CLanguage Lang ) {

		boolean bResult = false;
		
		try {
			
        	CloseableHttpResponse Response = null;

    		ArrayList<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
    		urlParameters.add( new BasicNameValuePair( "ServiceName", "System.Replicate.Command" ) );
    		urlParameters.add( new BasicNameValuePair( "InstanceID", strInstanceID ) );
    		urlParameters.add( new BasicNameValuePair( "TransactionID", strTransactionID ) );
    		urlParameters.add( new BasicNameValuePair( "CommandID", strCommandID ) );
    		urlParameters.add( new BasicNameValuePair( "Command", strCommand ) );
    		urlParameters.add( new BasicNameValuePair( "User", ConfigHTTPDBChannelReplicator.strUser ) );
    		urlParameters.add( new BasicNameValuePair( "Password", ConfigHTTPDBChannelReplicator.strPassword ) );
    		urlParameters.add( new BasicNameValuePair( "Database", ConfigHTTPDBChannelReplicator.strDatabase ) );
    		urlParameters.add( new BasicNameValuePair( "ResponseFormat", CBackendServicesManager._ResponseFormat ) );
    		urlParameters.add( new BasicNameValuePair( "ResponseFormatVersion", CBackendServicesManager._ResponseFormatVersion ) );
        	
        	if ( Params.size() > 0 ) {

        		for ( Entry<String,String> Param : Params.entrySet() ) {
        			
            		urlParameters.add( new BasicNameValuePair( Param.getKey(), Param.getValue() ) );
        			
        		}

        	};
			
    		PostData.setEntity( new UrlEncodedFormEntity( urlParameters ) );
			
			if ( Context == null )
				Response = HTTPClient.execute( PostData );
			else
				Response = HTTPClient.execute( PostData, Context );
			
			if ( Response != null && BasicBackendServices.saveResponseToFile( Response.getEntity().getContent(), this.strReplicatorStorePathTemp, "System.Replicate.Command.Response", Logger, Lang ) ) { 

				ArrayList<LinkedHashMap<String,String>> Result = BasicBackendServices.parseResponseMessage( this.strReplicatorStorePathTemp, "System.Replicate.Command.Response", Logger, Lang );
				
				if ( Result != null && Result.size() > 0 ) {
					
					String strCode = Result.get( 0 ).get( "Code" );
					
					if ( strCode != null && strCode.equals( "1000" ) ) {
					
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
