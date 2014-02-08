package DBReplicator;

import java.util.LinkedHashMap;

import CommonClasses.CLanguage;
import ExtendedLogger.CExtendedLogger;

public interface IDBChannelReplicator {

	public String getName();
	
	public String getVersion();
	
	public boolean loadConfig( String strInstanceID, String strReplicatorStorePath, String strRunningPath, String strConfigFile, CExtendedLogger Logger, CLanguage Lang );
	
	public boolean sendData( String strStoreID, String strTransactionID, String strCommandID, String strCommand, LinkedHashMap<String,String> Params, CExtendedLogger Logger, CLanguage Lang );
	
}
