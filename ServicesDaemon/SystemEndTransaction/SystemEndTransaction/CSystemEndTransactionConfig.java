package SystemEndTransaction;

import CommonClasses.CAbstractConfigLoader;
import CommonClasses.CLanguage;
import CommonClasses.CServicesDaemonConfig;
import DBServicesManager.CDBServicesManagerConfig;
import ExtendedLogger.CExtendedLogger;

public class CSystemEndTransactionConfig extends CAbstractConfigLoader {

	protected static CSystemEndTransactionConfig SystemEndTransactionConfig = null;

	static {
		
		SystemEndTransactionConfig = new CSystemEndTransactionConfig();
		
	} 

	public static CSystemEndTransactionConfig getSystemEndTransactionConfig( CServicesDaemonConfig ServicesDaemonConfig, CDBServicesManagerConfig DBServicesManagerConfig ) {
		
		SystemEndTransactionConfig.DBServicesManagerConfig = DBServicesManagerConfig;

		SystemEndTransactionConfig.ServicesDaemonConfig = ServicesDaemonConfig;

		return SystemEndTransactionConfig;
		
	}
	
	public CDBServicesManagerConfig DBServicesManagerConfig = null;
	public CServicesDaemonConfig ServicesDaemonConfig = null;
	
	public CExtendedLogger ServiceLogger =  null;
	public CLanguage ServiceLang = null;

	public CSystemEndTransactionConfig() {
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
