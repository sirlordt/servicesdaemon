/*******************************************************************************
 * Copyright (c) 2013 SirLordT <sirlordt@gmail.com>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     SirLordT <sirlordt@gmail.com> - initial API and implementation
 ******************************************************************************/
package PreExecuteServiceTest;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import AbstractResponseFormat.CAbstractResponseFormat;
import AbstractService.CAbstractService;
import AbstractService.CAbstractServicePreExecute;
import AbstractService.CServicePreExecuteResult;
import CommonClasses.CLanguage;
import CommonClasses.CConfigServicesDaemon;
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
	public boolean initializePreExecute( String strOwnerServiceName, CConfigServicesDaemon ServicesDaemonConfig, CExtendedLogger OwnerLogger, CLanguage OwnerLang, CExtendedLogger ServiceLogger, CLanguage ServiceLang) { 
		
		if ( this.strOwnerServiceName.contains( strOwnerServiceName ) ) {
		
			this.ServiceLogger = ServiceLogger;

			return true;
			
		}

		return false;
		
	}

	@Override
	public CServicePreExecuteResult preExecute( int intEntryCode, String strServiceName, HttpServletRequest Request, HttpServletResponse Response, String strSecurityTokenID, HashMap<String, CAbstractService> RegisteredServices, CAbstractResponseFormat ResponseFormat, String strResponseFormatVersion) { 

		CServicePreExecuteResult Result = new CServicePreExecuteResult();

		Result.intResultCode = 1;
		
		if ( ServiceLogger != null )
			ServiceLogger.logMessage( "1", this.strName + " " + this.strVersion );
		
		return Result;
		
	}

}
