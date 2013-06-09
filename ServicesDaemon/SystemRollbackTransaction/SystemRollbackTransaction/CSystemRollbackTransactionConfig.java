package SystemRollbackTransaction;

import CommonClasses.CAbstractConfigLoader;
import CommonClasses.CLanguage;
import CommonClasses.CServicesDaemonConfig;
import DBServicesManager.CDBServicesManagerConfig;
import ExtendedLogger.CExtendedLogger;

public class CSystemRollbackTransactionConfig extends CAbstractConfigLoader {

	protected static CSystemRollbackTransactionConfig SystemRollbackTransactionConfig = null;

	static {
		
		SystemRollbackTransactionConfig = new CSystemRollbackTransactionConfig();
		
	} 

	public static CSystemRollbackTransactionConfig getSystemRollbackTransactionConfig( CServicesDaemonConfig ServicesDaemonConfig, CDBServicesManagerConfig DBServicesManagerConfig ) {
		
		SystemRollbackTransactionConfig.DBServicesManagerConfig = DBServicesManagerConfig;

		SystemRollbackTransactionConfig.ServicesDaemonConfig = ServicesDaemonConfig;

		return SystemRollbackTransactionConfig;
		
	}
	
	public CDBServicesManagerConfig DBServicesManagerConfig = null;
	public CServicesDaemonConfig ServicesDaemonConfig = null;
	
	public CExtendedLogger ServiceLogger =  null;
	public CLanguage ServiceLang = null;

	public CSystemRollbackTransactionConfig() {
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
