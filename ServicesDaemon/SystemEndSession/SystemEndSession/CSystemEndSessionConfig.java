package SystemEndSession;

import CommonClasses.CAbstractConfigLoader;
import CommonClasses.CLanguage;
import CommonClasses.CServicesDaemonConfig;
import DBServicesManager.CDBServicesManagerConfig;
import ExtendedLogger.CExtendedLogger;

public class CSystemEndSessionConfig extends CAbstractConfigLoader {

	protected static CSystemEndSessionConfig SystemEndSessionConfig = null;

	static {
		
		SystemEndSessionConfig = new CSystemEndSessionConfig();
		
	} 

	public static CSystemEndSessionConfig getSystemEndSessionConfig( CServicesDaemonConfig ServicesDaemonConfig, CDBServicesManagerConfig DBServicesManagerConfig ) {
		
		SystemEndSessionConfig.DBServicesManagerConfig = DBServicesManagerConfig;

		SystemEndSessionConfig.ServicesDaemonConfig = ServicesDaemonConfig;

		return SystemEndSessionConfig;
		
	}
	
	public CDBServicesManagerConfig DBServicesManagerConfig = null;
	public CServicesDaemonConfig ServicesDaemonConfig = null;
	
	public CExtendedLogger ServiceLogger =  null;
	public CLanguage ServiceLang = null;

	public CSystemEndSessionConfig() {
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
