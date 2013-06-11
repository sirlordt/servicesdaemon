package AbstractService;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import AbstractResponseFormat.CAbstractResponseFormat;
import CommonClasses.CLanguage;
import CommonClasses.CServicesDaemonConfig;
import ExtendedLogger.CExtendedLogger;

public abstract class CAbstractServicePreExecute {

	protected ArrayList<String> strOwnerServiceName = new ArrayList<String>();
	protected String strName = "";
	protected String strVersion = "";
	
	public CAbstractServicePreExecute() {
		
		strOwnerServiceName.add( "*" );
		
	}

	public ArrayList<String> getOwnerServiceName() {
		
		return strOwnerServiceName;
		
	}

	public String getName() {

		return strName;
		
	}

	public String getVersion() {
		
		return strVersion;
		
	}

	public abstract boolean InitializePreExecute( String strOwnerServiceName, CServicesDaemonConfig ServicesDaemonConfig, CExtendedLogger OwnerLogger, CLanguage OwnerLang, CExtendedLogger ServiceLogger, CLanguage ServiceLang );
	public abstract CServicePreExecuteResult PreExecute( int intEntryCode, String strServiceName, HttpServletRequest Request, HttpServletResponse Response, String strSecurityTokenID, HashMap<String,CAbstractService> RegisteredServices, CAbstractResponseFormat ResponseFormat, String strResponseFormatVersion );
	
}
