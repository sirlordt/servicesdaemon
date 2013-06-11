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
package AbstractService;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import AbstractResponseFormat.CAbstractResponseFormat;
import CommonClasses.CLanguage;
import CommonClasses.CServicePostExecuteResult;
import CommonClasses.CServicesDaemonConfig;
import ExtendedLogger.CExtendedLogger;

public abstract class CAbstractServicePostExecute {

	protected ArrayList<String> strOwnerServiceName = new ArrayList<String>();
	protected String strName = "";
	protected String strVersion = "";
	
	public CAbstractServicePostExecute() {
	
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
	public abstract CServicePostExecuteResult PostExecute( int intEntryCode, String strServiceName, HttpServletRequest Request, HttpServletResponse Response, String strSecurityTokenID, HashMap<String,CAbstractService> RegisteredServices, CAbstractResponseFormat ResponseFormat, String strResponseFormatVersion );
	
}
