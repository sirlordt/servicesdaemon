package DBReplicator;

import java.io.File;
import java.util.LinkedHashMap;

import CommonClasses.CBasicBackendServices;
import CommonClasses.CLanguage;
import ExtendedLogger.CExtendedLogger;

public class CHTTPDBChannelReplicator implements IDBChannelReplicator {

	CConfigHTTPDBChannelReplicator ConfigHTTPDBChannelReplicator;
	
	protected String strInstanceID;
	
	protected String strReplicatorStorePath;

	protected String strReplicatorStorePathTemp;
	
	CBasicBackendServices BasicBackendServices = null; 

	public CHTTPDBChannelReplicator() {
		
	}

	public void finalize() {
		
		BasicBackendServices.deleteTempResponsesFiles( null, null );
		
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
				
				if ( ConfigHTTPDBChannelReplicator.MainConfiguredTarget != null ) {
				
					ConfigHTTPDBChannelReplicator.MainConfiguredTarget.HTTPDBChannelReplicatorTarget = this;
					ConfigHTTPDBChannelReplicator.MainConfiguredTarget.initHTTPChannel( Logger, Lang );
				
					for ( CHTTPDBChannelReplicatorTarget ConfigHTTPDBChannel: ConfigHTTPDBChannelReplicator.BackupConfiguredTargets ) {
						
						ConfigHTTPDBChannel.HTTPDBChannelReplicatorTarget = this;
						ConfigHTTPDBChannel.initHTTPChannel( Logger, Lang );
						
					}
					
				}
				
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
	public boolean sendData( String strDataBlockID, String strTransactionID, String strCommandID, String strCommand, LinkedHashMap<String, String> Params, CExtendedLogger Logger, CLanguage Lang ) {

		boolean bResult = false;
		
		if ( ConfigHTTPDBChannelReplicator.MainConfiguredTarget != null ) {
			
			bResult = ConfigHTTPDBChannelReplicator.MainConfiguredTarget.sendData( strDataBlockID, strTransactionID, strCommandID, strCommand, Params, Logger, Lang );
			
			if ( bResult == false ) {
				
				for ( CHTTPDBChannelReplicatorTarget ConfigHTTPDBChannel: ConfigHTTPDBChannelReplicator.BackupConfiguredTargets ) {
					
					if ( ConfigHTTPDBChannel.sendData( strDataBlockID, strTransactionID, strCommandID, strCommand, Params, Logger, Lang ) ) {
						
						bResult = true;
						break;
						
					}
					
				}
				
			}
			
		}

		return bResult;
		
	}

}
