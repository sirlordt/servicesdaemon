package SystemStartTransaction;

import CommonClasses.CAbstractConfigLoader;
import CommonClasses.CLanguage;
import CommonClasses.CServicesDaemonConfig;
import DBServicesManager.CDBServicesManagerConfig;
import ExtendedLogger.CExtendedLogger;

public class CSystemStartTransactionConfig extends CAbstractConfigLoader {

	protected static CSystemStartTransactionConfig SystemCommitTransactionConfig = null;

	static {
		
		SystemCommitTransactionConfig = new CSystemStartTransactionConfig();
		
	} 

	public static CSystemStartTransactionConfig getSystemStartTransactionConfig( CServicesDaemonConfig ServicesDaemonConfig, CDBServicesManagerConfig DBServicesManagerConfig ) {
		
		SystemCommitTransactionConfig.DBServicesManagerConfig = DBServicesManagerConfig;

		SystemCommitTransactionConfig.ServicesDaemonConfig = ServicesDaemonConfig;

		return SystemCommitTransactionConfig;
		
	}
	
	public CDBServicesManagerConfig DBServicesManagerConfig = null;
	public CServicesDaemonConfig ServicesDaemonConfig = null;
	
	public CExtendedLogger ServiceLogger =  null;
	public CLanguage ServiceLang = null;

	public CSystemStartTransactionConfig() {
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
