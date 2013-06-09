package PreExecuteServiceTest;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import AbstractResponseFormat.CAbstractResponseFormat;
import AbstractService.CAbstractService;
import AbstractService.CAbstractServicePreExecute;
import AbstractService.CServicePreExecuteResult;
import CommonClasses.CLanguage;
import CommonClasses.CServicesDaemonConfig;
import ExtendedLogger.CExtendedLogger;

public class CPreExecuteServiceTest extends CAbstractServicePreExecute {

	CExtendedLogger ServiceLogger = null;
	
	public CPreExecuteServiceTest() {
		
		super();
		
		strOwnerServiceName.clear();
		strOwnerServiceName.add( "System.Ping" ); 
		strName = "Pre-Execute-Test1";
		strVersion = "1.0.0.0";
		
	}

	@Override
	public boolean InitializePreExecute( String strOwnerServiceName, CServicesDaemonConfig ServicesDaemonConfig, CExtendedLogger OwnerLogger, CLanguage OwnerLang, CExtendedLogger ServiceLogger, CLanguage ServiceLang) { 
		
		if ( this.strOwnerServiceName.contains( strOwnerServiceName ) ) {
		
			this.ServiceLogger = ServiceLogger;

			return true;
			
		}

		return false;
		
	}

	@Override
	public CServicePreExecuteResult PreExecute( int intEntryCode, String strServiceName, HttpServletRequest Request, HttpServletResponse Response, String strSecurityTokenID, HashMap<String, CAbstractService> RegisteredServices, CAbstractResponseFormat ResponseFormat, String strResponseFormatVersion) { 

		CServicePreExecuteResult Result = new CServicePreExecuteResult();

		Result.intResultCode = 1;
		
		if ( ServiceLogger != null )
			ServiceLogger.LogMessage( "1", this.strName + " " + this.strVersion );
		
		return Result;
		
	}

}
