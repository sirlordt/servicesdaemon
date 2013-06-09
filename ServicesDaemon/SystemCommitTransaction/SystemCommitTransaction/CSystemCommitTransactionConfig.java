package SystemCommitTransaction;

import CommonClasses.CAbstractConfigLoader;
import CommonClasses.CLanguage;
import CommonClasses.CServicesDaemonConfig;
import DBServicesManager.CDBServicesManagerConfig;
import ExtendedLogger.CExtendedLogger;

public class CSystemCommitTransactionConfig extends CAbstractConfigLoader {

	protected static CSystemCommitTransactionConfig SystemCommitTransactionConfig = null;

	static {
		
		SystemCommitTransactionConfig = new CSystemCommitTransactionConfig();
		
	} 

	public static CSystemCommitTransactionConfig getSystemCommitTransactionConfig( CServicesDaemonConfig ServicesDaemonConfig, CDBServicesManagerConfig DBServicesManagerConfig ) {
		
		SystemCommitTransactionConfig.DBServicesManagerConfig = DBServicesManagerConfig;

		SystemCommitTransactionConfig.ServicesDaemonConfig = ServicesDaemonConfig;

		return SystemCommitTransactionConfig;
		
	}
	
	public CDBServicesManagerConfig DBServicesManagerConfig = null;
	public CServicesDaemonConfig ServicesDaemonConfig = null;
	
	public CExtendedLogger ServiceLogger =  null;
	public CLanguage ServiceLang = null;

	public CSystemCommitTransactionConfig() {
	}

	public boolean LoadConfig( String strConfigFilePath, CLanguage Lang, CExtendedLogger Logger ) {
		
        if ( ServiceLogger == null )
        	ServiceLogger = Logger;

        if ( ServiceLang == null )
        	ServiceLang = Lang;

        this.strConfigFilePath = strConfigFilePath;
        
	    return true; //do nothing always true
	
	}
	
}
